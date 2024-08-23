/*
 * Copyright © 2024 Baird Creek Software LLC
 *
 * Licensed under the PolyForm Noncommercial License, version 1.0.0;
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *     https://polyformproject.org/licenses/noncommercial/1.0.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package is.galia.plugin.openjpeg;

import is.galia.Application;
import is.galia.codec.AbstractDecoder;
import is.galia.codec.Decoder;
import is.galia.codec.DecoderHint;
import is.galia.codec.SourceFormatException;
import is.galia.codec.iptc.DataSet;
import is.galia.codec.iptc.IIMReader;
import is.galia.codec.tiff.Directory;
import is.galia.config.Configuration;
import is.galia.image.Format;
import is.galia.image.Metadata;
import is.galia.image.MutableMetadata;
import is.galia.image.Orientation;
import is.galia.image.Size;
import is.galia.image.ReductionFactor;
import is.galia.image.Region;
import is.galia.plugin.Plugin;
import is.galia.processor.Java2DUtils;
import is.galia.stream.PathImageInputStream;
import is.galia.util.ArrayUtils;
import is.galia.util.FileUtils;
import is.galia.util.IOUtils;
import is.galia.util.Stopwatch;
import org.openjpeg.opj_codestream_info_v2;
import org.openjpeg.opj_dparameters;
import org.openjpeg.opj_image;
import org.openjpeg.opj_image_comp_t;
import org.openjpeg.opj_tccp_info_t;
import org.openjpeg.opj_tile_info_v2_t;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.stream.ImageInputStream;
import java.awt.color.ICC_Profile;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.WritableRaster;
import java.io.EOFException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.foreign.FunctionDescriptor;
import java.lang.foreign.Linker;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.StructLayout;
import java.lang.foreign.ValueLayout;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.nio.charset.StandardCharsets;
import java.nio.file.NoSuchFileException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.WeakHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

import static is.galia.plugin.openjpeg.Formats.JP2_BRAND_SIGNATURE;
import static is.galia.plugin.openjpeg.Formats.JP2_CODESTREAM_SIGNATURE;
import static is.galia.plugin.openjpeg.Formats.JP2_FAMILY_SIGNATURE;
import static is.galia.plugin.openjpeg.Formats.JPH_BRAND_SIGNATURE;
import static is.galia.plugin.openjpeg.Formats.JPXB_BRAND_SIGNATURE;
import static is.galia.plugin.openjpeg.Formats.JPX_BRAND_SIGNATURE;
import static org.openjpeg.openjpeg_h.C_POINTER;
import static org.openjpeg.openjpeg_h.OPJ_CLRSPC_CMYK;
import static org.openjpeg.openjpeg_h.OPJ_CLRSPC_EYCC;
import static org.openjpeg.openjpeg_h.OPJ_CLRSPC_SYCC;
import static org.openjpeg.openjpeg_h.OPJ_CODEC_J2K;
import static org.openjpeg.openjpeg_h.OPJ_CODEC_JP2;
import static org.openjpeg.openjpeg_h.OPJ_DPARAMETERS_DUMP_FLAG;
import static org.openjpeg.openjpeg_h.OPJ_J2K_STREAM_CHUNK_SIZE;
import static org.openjpeg.openjpeg_h.OPJ_VERSION_BUILD;
import static org.openjpeg.openjpeg_h.OPJ_VERSION_MAJOR;
import static org.openjpeg.openjpeg_h.OPJ_VERSION_MINOR;
import static org.openjpeg.openjpeg_h.opj_codec_set_threads;
import static org.openjpeg.openjpeg_h.opj_create_decompress;
import static org.openjpeg.openjpeg_h.opj_decode;
import static org.openjpeg.openjpeg_h.opj_decoder_set_strict_mode;
import static org.openjpeg.openjpeg_h.opj_destroy_codec;
import static org.openjpeg.openjpeg_h.opj_end_decompress;
import static org.openjpeg.openjpeg_h.opj_get_cstr_info;
import static org.openjpeg.openjpeg_h.opj_get_num_cpus;
import static org.openjpeg.openjpeg_h.opj_image_destroy;
import static org.openjpeg.openjpeg_h.opj_read_header;
import static org.openjpeg.openjpeg_h.opj_set_decode_area;
import static org.openjpeg.openjpeg_h.opj_set_decoded_resolution_factor;
import static org.openjpeg.openjpeg_h.opj_set_default_decoder_parameters;
import static org.openjpeg.openjpeg_h.opj_set_error_handler;
import static org.openjpeg.openjpeg_h.opj_set_info_handler;
import static org.openjpeg.openjpeg_h.opj_set_warning_handler;
import static org.openjpeg.openjpeg_h.opj_setup_decoder;
import static org.openjpeg.openjpeg_h.opj_stream_create;
import static org.openjpeg.openjpeg_h.opj_stream_create_default_file_stream;
import static org.openjpeg.openjpeg_h.opj_stream_destroy;
import static org.openjpeg.openjpeg_h.opj_stream_set_read_function;
import static org.openjpeg.openjpeg_h.opj_stream_set_seek_function;
import static org.openjpeg.openjpeg_h.opj_stream_set_skip_function;
import static org.openjpeg.openjpeg_h.opj_stream_set_user_data;
import static org.openjpeg.openjpeg_h.opj_stream_set_user_data_length;

/**
 * <p>Implementation using the Java Foreign Function & Memory API to call into
 * the OpenJPEG library.</p>
 *
 * @see <a href="https://www.openjpeg.org/doxygen/">OpenJPEG API
 * Documentation</a>
 */
public final class OpenJPEGDecoder extends AbstractDecoder
        implements Decoder, Plugin {

    private static final Logger LOGGER =
            LoggerFactory.getLogger(OpenJPEGDecoder.class);

    private static final double DELTA = 0.00000001;

    private static final AtomicBoolean IS_CLASS_INITIALIZED =
            new AtomicBoolean();
    private static final int SUPPORTED_MAJOR_VERSION = 2;

    private static final FunctionDescriptor ERROR_HANDLER_FUNCTION_DESCRIPTOR =
            FunctionDescriptor.ofVoid(C_POINTER, C_POINTER);
    private static final FunctionDescriptor INFO_HANDLER_FUNCTION_DESCRIPTOR =
            FunctionDescriptor.ofVoid(C_POINTER, C_POINTER);
    private static final FunctionDescriptor WARNING_HANDLER_FUNCTION_DESCRIPTOR =
            FunctionDescriptor.ofVoid(C_POINTER, C_POINTER);
    private static MethodHandle ERROR_HANDLER_FUNCTION, INFO_HANDLER_FUNCTION,
            WARNING_HANDLER_FUNCTION;

    private static final String NUM_THREADS_CONFIG_KEY = "decoder." +
            OpenJPEGDecoder.class.getSimpleName() + ".num_threads";
    static final Map<String,OpenJPEGDecoder> LIVE_INSTANCES =
            Collections.synchronizedMap(new WeakHashMap<>());

    private final String instanceID = UUID.randomUUID().toString();
    private final MetadataReader metadataReader = new MetadataReader();
    private MemorySegment decoder, stream, image;
    private boolean ownsInputStream;
    private transient int width, height, tileWidth, tileHeight, numResolutions;
    private transient Format format;
    private transient MutableMetadata metadata;

    private static void handleInfo(MemorySegment msg,
                                   MemorySegment userData) {
        String string = msg.getString(0).trim();
        LOGGER.debug(string);
    }

    private static void handleWarning(MemorySegment msg,
                                      MemorySegment userData) {
        String string = msg.getString(0).trim();
        LOGGER.info(string);
        if (Application.isDeveloping() || Application.isTesting()) {
            System.err.println(string);
        }
    }

    private static void handleError(MemorySegment msg,
                                    MemorySegment userData) {
        String string = msg.getString(0).trim();
        LOGGER.warn(string);
        if (Application.isDeveloping() || Application.isTesting()) {
            System.err.println(string);
        }
    }

    private static void checkLibraryVersion() {
        int major = OPJ_VERSION_MAJOR();
        int minor = OPJ_VERSION_MINOR();
        int build = OPJ_VERSION_BUILD();
        LOGGER.debug("Detected libopenjp2 version {}.{}.{}",
                major, minor, build);
        if (major != 2) {
            LOGGER.error("Incompatible libopenjp2 version. The required " +
                    "major version is " + SUPPORTED_MAJOR_VERSION + ".");
        }
    }

    //endregion
    //region Plugin methods

    @Override
    public Set<String> getPluginConfigKeys() {
        return Set.of(NUM_THREADS_CONFIG_KEY);
    }

    @Override
    public String getPluginName() {
        return OpenJPEGDecoder.class.getSimpleName();
    }

    @Override
    public void onApplicationStart() {
        if (!IS_CLASS_INITIALIZED.getAndSet(true)) {
            System.loadLibrary("openjp2");
            checkLibraryVersion();
            StreamFunctions.initializeClass();
            try {
                ERROR_HANDLER_FUNCTION = MethodHandles.lookup().findStatic(
                        OpenJPEGDecoder.class, "handleError",
                        ERROR_HANDLER_FUNCTION_DESCRIPTOR.toMethodType());
                INFO_HANDLER_FUNCTION = MethodHandles.lookup().findStatic(
                        OpenJPEGDecoder.class, "handleInfo",
                        INFO_HANDLER_FUNCTION_DESCRIPTOR.toMethodType());
                WARNING_HANDLER_FUNCTION = MethodHandles.lookup().findStatic(
                        OpenJPEGDecoder.class, "handleWarning",
                        WARNING_HANDLER_FUNCTION_DESCRIPTOR.toMethodType());
            } catch (NoSuchMethodException | IllegalAccessException e) {
                LOGGER.error(e.getMessage());
            }
        }
    }

    @Override
    public void onApplicationStop() {
    }

    @Override
    public void initializePlugin() {
        LIVE_INSTANCES.put(instanceID, this);
    }

    //endregion
    //region Decoder methods

    @Override
    public void close() {
        super.close();
        if (decoder != null && decoder.address() != 0) {
            opj_destroy_codec(decoder);
        }
        if (stream != null && stream.address() != 0) {
            opj_stream_destroy(stream);
        }
        if (image != null && image.address() != 0) {
            opj_image_destroy(image);
        }
        if (ownsInputStream) {
            IOUtils.closeQuietly(inputStream);
        }
        LIVE_INSTANCES.remove(instanceID);
    }

    @Override
    public Format detectFormat() throws IOException {
        if (format == null) {
            initSource();
            byte[] magicBytes = new byte[24];
            inputStream.seek(0);
            inputStream.readFully(magicBytes);
            inputStream.seek(0);
            if (ArrayUtils.startsWith(magicBytes, JP2_CODESTREAM_SIGNATURE)) {
                format = Formats.J2C;
            } else if (ArrayUtils.startsWith(magicBytes, JP2_FAMILY_SIGNATURE)) {
                if (ArrayUtils.startsWith(magicBytes, JP2_BRAND_SIGNATURE, 20)) {
                    format = Formats.JP2;
                } else if (ArrayUtils.startsWith(magicBytes, JPH_BRAND_SIGNATURE, 20)) {
                    format = Formats.JPH;
                } else if (ArrayUtils.startsWith(magicBytes, JPX_BRAND_SIGNATURE, 20) ||
                        ArrayUtils.startsWith(magicBytes, JPXB_BRAND_SIGNATURE, 20)) {
                    format = Formats.JPX;
                }
            }
            if (format == null) {
                format = Format.UNKNOWN;
            }
        }
        return format;
    }

    @Override
    public int getNumImages() throws IOException {
        initDecoder();
        return 1;
    }

    @Override
    public int getNumResolutions() throws IOException {
        initDecoder();
        if (numResolutions < 1) {
            MemorySegment csInfo   = opj_get_cstr_info(decoder);
            MemorySegment tileInfo = opj_codestream_info_v2.m_default_tile_info(csInfo);
            MemorySegment tccpInfo = opj_tile_info_v2_t.tccp_info(tileInfo);
            numResolutions         = opj_tccp_info_t.numresolutions(tccpInfo);
        }
        return numResolutions;
    }

    /**
     * @return Full source image dimensions.
     */
    @Override
    public Size getSize(int imageIndex) throws IOException {
        validateImageIndex(imageIndex);
        if (width == 0) {
            width  = opj_image.x1(image);
            height = opj_image.y1(image);
        }
        return new Size(width, height);
    }

    @Override
    public Set<Format> getSupportedFormats() {
        return Set.of(Formats.JP2, Formats.J2C, Formats.JPH, Formats.JPX);
    }

    @Override
    public Size getTileSize(int imageIndex) throws IOException {
        validateImageIndex(imageIndex);
        if (tileWidth == 0) {
            MemorySegment csInfo = opj_get_cstr_info(decoder);
            tileWidth  = opj_codestream_info_v2.tdy(csInfo);
            tileHeight = opj_codestream_info_v2.tdx(csInfo);
            if (opj_image.x1(image) == tileHeight && opj_image.y1(image) == tileWidth) {
                int tmp    = tileWidth;
                tileWidth  = tileHeight;
                tileHeight = tmp;
            }
        }
        return new Size(tileWidth, tileHeight);
    }

    @Override
    public BufferedImage decode(int imageIndex,
                                Region orientedRegion,
                                double[] scales,
                                ReductionFactor reductionFactor,
                                double[] diffScales,
                                Set<DecoderHint> decoderHints) throws IOException {
        validateImageIndex(imageIndex);
        Stopwatch watch = new Stopwatch();

        // Find the best resolution level to read.
        final int resLevel = Math.max(
                ReductionFactor.forScale(scales[0]).factor,
                ReductionFactor.forScale(scales[1]).factor);
        reductionFactor.factor = resLevel;
        opj_set_decoded_resolution_factor(decoder, resLevel);
        if (Math.abs(reductionFactor.findDifferentialScale(scales[0]) - 1) > DELTA ||
                Math.abs(reductionFactor.findDifferentialScale(scales[1]) - 1) > DELTA) {
            decoderHints.add(DecoderHint.NEEDS_DIFFERENTIAL_SCALE);
        }
        diffScales[0] = reductionFactor.findDifferentialScale(scales[0]);
        diffScales[1] = reductionFactor.findDifferentialScale(scales[1]);

        // Compute a non-oriented region to read.
        Metadata metadata = readMetadata(imageIndex);
        Orientation orientation = metadata.getOrientation();
        Size size = getSize(imageIndex);
        if (orientedRegion.isFull() || orientedRegion.isEmpty()) {
            orientedRegion = orientedRegion.resized(size.width(), size.height());
            orientedRegion = orientedRegion.oriented(size, orientation);
        }
        Region region = orientedRegion.oriented(size, orientation);
        decoderHints.add(DecoderHint.ALREADY_ORIENTED);

        int result = opj_set_decode_area(decoder, image,
                region.intX(), region.intY(),
                region.intX() + region.intWidth(),
                region.intY() + region.intHeight());
        if (result == 0) {
            throw new IOException("Failed to set the decoded area to " + region);
        }

        result = opj_decode(decoder, stream, image);
        if (result == 0) {
            throw new IOException("opj_decode() returned " + result);
        }
        result = opj_end_decompress(decoder, stream);
        if (result == 0) {
            throw new IOException("opj_end_decompress() returned " + result);
        }

        BufferedImage bufferedImage = newBufferedImage(
                region.scaled(reductionFactor.getScale()),
                orientedRegion.scaled(reductionFactor.getScale()),
                orientation);

        LOGGER.trace("read(int, ...): read {}x{} image in {}",
                bufferedImage.getWidth(), bufferedImage.getHeight(), watch);
        return bufferedImage;
    }

    @Override
    public Metadata readMetadata(int imageIndex) throws IOException {
        validateImageIndex(imageIndex);
        if (metadata == null) {
            final long initialPos = inputStream.getStreamPosition();
            try {
                inputStream.seek(0);
                metadataReader.setSource(inputStream);
                metadata = new MutableMetadata();
                // IPTC
                byte[] iptcBytes = metadataReader.getIPTC();
                if (iptcBytes != null) {
                    IIMReader reader = new IIMReader();
                    reader.setSource(iptcBytes);
                    List<DataSet> dataSets = reader.read();
                    metadata.setIPTC(dataSets);
                }
                // EXIF
                Directory exif = metadataReader.getEXIF();
                metadata.setEXIF(exif);
                // XMP
                metadata.setXMP(metadataReader.getXMP());
            } finally {
                inputStream.seek(initialPos);
            }
        }
        return metadata;
    }

    //endregion
    //region Private methods

    ImageInputStream getInputStream() {
        return inputStream;
    }

    private void validateImageIndex(int index) throws IOException {
        if (index < 0 || index >= getNumImages()) {
            throw new IndexOutOfBoundsException();
        }
    }

    private void initSource() throws IOException {
        if (imageFile == null && inputStream == null) {
            throw new IOException("Source not set");
        } else if (inputStream != null) {
            return;
        }
        try {
            FileUtils.checkReadableFile(imageFile);
            inputStream = new PathImageInputStream(imageFile);
            ownsInputStream = true;
        } catch (FileNotFoundException e) {
            throw new NoSuchFileException(e.getMessage());
        }
    }

    private void initDecoder() throws IOException {
        if (decoder != null) {
            return;
        }
        initSource();
        StructLayout parametersStruct = (StructLayout) opj_dparameters.layout();
        MemorySegment parameters      = arena.allocate(parametersStruct);
        opj_set_default_decoder_parameters(parameters);

        String formatKey;
        try {
            formatKey = detectFormat().key();
        } catch (EOFException e) {
            throw new SourceFormatException(
                    "Failed to read header (is this a valid JP2?)");
        }

        int codec = switch (formatKey) {
            case "j2c"               -> OPJ_CODEC_J2K(); // J2K_CFMT
            case "jp2", "jph", "jpx" -> OPJ_CODEC_JP2(); // JP2_CFMT
            default                  -> throw new SourceFormatException();
        };
        opj_dparameters.decod_format(parameters, codec);
        decoder = opj_create_decompress(codec);

        MemorySegment errorCallback   = Linker.nativeLinker().upcallStub(
                ERROR_HANDLER_FUNCTION,
                ERROR_HANDLER_FUNCTION_DESCRIPTOR,
                arena);
        MemorySegment warningCallback = Linker.nativeLinker().upcallStub(
                WARNING_HANDLER_FUNCTION,
                WARNING_HANDLER_FUNCTION_DESCRIPTOR,
                arena);
        MemorySegment infoCallback    = Linker.nativeLinker().upcallStub(
                INFO_HANDLER_FUNCTION,
                INFO_HANDLER_FUNCTION_DESCRIPTOR,
                arena);
        opj_set_warning_handler(decoder, warningCallback, MemorySegment.NULL);
        opj_set_error_handler(decoder, errorCallback, MemorySegment.NULL);
        opj_set_info_handler(decoder, infoCallback, MemorySegment.NULL);

        opj_dparameters.flags(parameters,
                opj_dparameters.flags(parameters) | OPJ_DPARAMETERS_DUMP_FLAG());

        int result = opj_setup_decoder(decoder, parameters);
        if (result == 0) {
            throw new IOException("opj_setup_decoder() returned " + result);
        }

        int numThreads = Configuration.forApplication().getInt(
                NUM_THREADS_CONFIG_KEY, opj_get_num_cpus());
        opj_codec_set_threads(decoder, numThreads);
        opj_decoder_set_strict_mode(decoder, 0);

        if (imageFile != null) {
            MemorySegment pathname = arena.allocate(4096);
            pathname.asByteBuffer().put(imageFile.toString().getBytes(StandardCharsets.UTF_8));
            opj_dparameters.infile(parameters, pathname);
            stream = opj_stream_create_default_file_stream(opj_dparameters.infile(parameters), 1);
        } else if (inputStream != null) {
            MemorySegment readFunction = Linker.nativeLinker().upcallStub(
                    StreamFunctions.READ_FUNCTION,
                    StreamFunctions.READ_FUNCTION_DESCRIPTOR,
                    arena);
            MemorySegment seekFunction = Linker.nativeLinker().upcallStub(
                    StreamFunctions.SEEK_FUNCTION,
                    StreamFunctions.SEEK_FUNCTION_DESCRIPTOR,
                    arena);
            MemorySegment skipFunction = Linker.nativeLinker().upcallStub(
                    StreamFunctions.SKIP_FUNCTION,
                    StreamFunctions.SKIP_FUNCTION_DESCRIPTOR,
                    arena);
            MemorySegment freeUserDataFunction = Linker.nativeLinker().upcallStub(
                    StreamFunctions.FREE_USER_DATA_FUNCTION,
                    StreamFunctions.FREE_USER_DATA_FUNCTION_DESCRIPTOR,
                    arena);

            long chunkSize = Math.min(OPJ_J2K_STREAM_CHUNK_SIZE(), inputStream.length());
            stream = opj_stream_create(chunkSize, 1);
            opj_stream_set_read_function(stream, readFunction);
            opj_stream_set_seek_function(stream, seekFunction);
            opj_stream_set_skip_function(stream, skipFunction);
            // N.B.: OpenJPEG assumes that "user data" means "the image data"
            // and that our stream functions will operate on this data.
            // Accordingly, the stream won't work if the user data length !=
            // the image data length. So we will pass in the image length as
            // user data, but use our own user data, which is the instance ID.
            MemorySegment userData = arena.allocateFrom(instanceID);
            opj_stream_set_user_data(stream, userData, freeUserDataFunction);
            opj_stream_set_user_data_length(stream, inputStream.length());
            inputStream.seek(0);
        } else {
            throw new IOException("Source not set");
        }

        StructLayout imageStruct = (StructLayout) opj_image.layout();
        MemorySegment imagePtr = arena.allocate(imageStruct);
        // opj_read_header() returns 0 for both missing and invalid files.
        result = opj_read_header(stream, decoder, imagePtr);
        if (result == 0) {
            throw new SourceFormatException("opj_read_header() returned " +
                    result + " (is this a JP2?)");
        }
        image = imagePtr.get(C_POINTER, 0);

        int colorSpace = opj_image.color_space(image);
        if (colorSpace == OPJ_CLRSPC_EYCC() || colorSpace == OPJ_CLRSPC_SYCC()) {
            throw new SourceFormatException("YCC color is not supported.");
        } else if (colorSpace == OPJ_CLRSPC_CMYK()) {
            throw new SourceFormatException("CMYK color is not supported.");
        }

        int numBands = opj_image.numcomps(image);
        if (numBands > 4) {
            throw new SourceFormatException(
                    numBands + "-band images are not supported.");
        }
    }

    private BufferedImage newBufferedImage(final Region unorientedScaledRegion,
                                           final Region orientedScaledRegion,
                                           final Orientation orientation) {
        final Stopwatch watch = new Stopwatch();
        int numBands          = opj_image.numcomps(image);

        BufferedImage bufferedImage = switch (numBands) {
            case 1  -> newGrayBufferedImage(
                    orientedScaledRegion.intWidth(),
                    orientedScaledRegion.intHeight(),
                    unorientedScaledRegion.intWidth(),
                    unorientedScaledRegion.intHeight(),
                    orientation);
            case 2  -> newGrayAlphaBufferedImage(
                    orientedScaledRegion.intWidth(),
                    orientedScaledRegion.intHeight(),
                    unorientedScaledRegion.intWidth(),
                    unorientedScaledRegion.intHeight(),
                    orientation);
            case 3  -> newBGRBufferedImage(
                    orientedScaledRegion.intWidth(),
                    orientedScaledRegion.intHeight(),
                    unorientedScaledRegion.intWidth(),
                    unorientedScaledRegion.intHeight(),
                    orientation);
            default -> newABGRBufferedImage(
                    orientedScaledRegion.intWidth(),
                    orientedScaledRegion.intHeight(),
                    unorientedScaledRegion.intWidth(),
                    unorientedScaledRegion.intHeight(),
                    orientation);
        };

        bufferedImage = applyICCProfile(bufferedImage);

        LOGGER.trace("newBufferedImage() completed in {}", watch);
        return bufferedImage;
    }

    private BufferedImage applyICCProfile(BufferedImage bufferedImage) {
        int iccLength = opj_image.icc_profile_len(image);
        if (iccLength > 0) {
            MemorySegment iccProfileSegment = opj_image.icc_profile_buf(image);
            byte[] iccBytes = iccProfileSegment
                    .reinterpret(iccLength)
                    .toArray(ValueLayout.JAVA_BYTE);
            ICC_Profile sourceProfile = ICC_Profile.getInstance(iccBytes);
            try {
                bufferedImage = Java2DUtils.convertToSRGB(
                        bufferedImage, sourceProfile);
            } catch (IllegalArgumentException e) {
                if (("Numbers of source Raster bands and source color space " +
                        "components do not match").equals(e.getMessage())) {
                    LOGGER.debug("Failed to apply ICC profile: {}", e.getMessage());
                } else {
                    throw e;
                }
            }
        }
        return bufferedImage;
    }

    private BufferedImage newGrayBufferedImage(final int orientedScaledWidth,
                                               final int orientedScaledHeight,
                                               final int unorientedScaledRegionWidth,
                                               final int unorientedScaledRegionHeight,
                                               final Orientation orientation) {
        BufferedImage bufImage = new BufferedImage(
                orientedScaledWidth, orientedScaledHeight,
                BufferedImage.TYPE_BYTE_GRAY);
        byte[] pixels          = ((DataBufferByte) bufImage.getRaster().getDataBuffer()).getData();
        MemorySegment comps    = opj_image.comps(image);
        MemorySegment comp0    = comps.reinterpret(opj_image_comp_t.layout().byteSize());
        byte[] band            = opj_image_comp_t.data(comp0)
                .asSlice(0, (long) unorientedScaledRegionWidth * unorientedScaledRegionHeight * 4)
                .toArray(ValueLayout.JAVA_BYTE);

        // If the orientation is 0, we can write the samples directly into
        // the Raster's underlying DataBuffer, which is much faster than
        // going through the Raster.
        switch (orientation) {
            case ROTATE_0 -> {
                for (int i = 0; i < unorientedScaledRegionWidth * unorientedScaledRegionHeight; i++) {
                    pixels[i] = band[i * 4];
                }
            }
            default -> {
                WritableRaster raster = bufImage.getRaster();
                int i = 0;
                for (int y = 0; y < unorientedScaledRegionHeight; y++) {
                    for (int x = 0; x < unorientedScaledRegionWidth; x++) {
                        int rasterX = adjustX(x, y,
                                unorientedScaledRegionWidth,
                                unorientedScaledRegionHeight,
                                orientation);
                        int rasterY = adjustY(x, y,
                                unorientedScaledRegionWidth,
                                unorientedScaledRegionHeight,
                                orientation);
                        raster.setSample(rasterX, rasterY, 0, band[i * 4]);
                        i++;
                    }
                }
            }
        }
        return bufImage;
    }

    private BufferedImage newGrayAlphaBufferedImage(final int orientedScaledWidth,
                                                    final int orientedScaledHeight,
                                                    final int unorientedScaledRegionWidth,
                                                    final int unorientedScaledRegionHeight,
                                                    final Orientation orientation) {
        BufferedImage bufImage = new BufferedImage(
                orientedScaledWidth, orientedScaledHeight,
                BufferedImage.TYPE_4BYTE_ABGR);
        MemorySegment comps    = opj_image.comps(image);
        MemorySegment gray     = comps.asSlice(0, opj_image_comp_t.layout().byteSize());
        MemorySegment alpha    = comps.asSlice(
                opj_image_comp_t.layout().byteSize(),
                opj_image_comp_t.layout().byteSize());

        byte[] grayBand  = opj_image_comp_t.data(gray)
                .asSlice(0, (long) unorientedScaledRegionWidth * unorientedScaledRegionHeight * 4)
                .toArray(ValueLayout.JAVA_BYTE);
        byte[] alphaBand = opj_image_comp_t.data(alpha)
                .asSlice(0, (long) unorientedScaledRegionWidth * unorientedScaledRegionHeight * 4)
                .toArray(ValueLayout.JAVA_BYTE);

        // If the orientation is 0, we can write the samples directly into
        // the Raster's underlying DataBuffer, which is much faster than
        // going through the Raster.
        switch (orientation) {
            case ROTATE_0 -> {
                byte[] pixels = ((DataBufferByte) bufImage.getRaster().getDataBuffer()).getData();
                for (int i = 0, j = 0; i < unorientedScaledRegionWidth * unorientedScaledRegionHeight; i++) {
                    int bandIndex = i * 4;
                    pixels[j++] = alphaBand[bandIndex];
                    pixels[j++] = grayBand[bandIndex];
                    pixels[j++] = grayBand[bandIndex];
                    pixels[j++] = grayBand[bandIndex];
                }
            }
            default -> {
                WritableRaster raster = bufImage.getRaster();
                int i = 0;
                for (int y = 0; y < unorientedScaledRegionHeight; y++) {
                    for (int x = 0; x < unorientedScaledRegionWidth; x++) {
                        int rasterX = adjustX(x, y,
                                unorientedScaledRegionWidth,
                                unorientedScaledRegionHeight,
                                orientation);
                        int rasterY = adjustY(x, y,
                                unorientedScaledRegionWidth,
                                unorientedScaledRegionHeight,
                                orientation);
                        raster.setSample(rasterX, rasterY, 0, grayBand[i * 4]);
                        raster.setSample(rasterX, rasterY, 1, grayBand[i * 4]);
                        raster.setSample(rasterX, rasterY, 2, grayBand[i * 4]);
                        raster.setSample(rasterX, rasterY, 3, alphaBand[i * 4]);
                        i++;
                    }
                }
            }
        }
        return bufImage;
    }

    private BufferedImage newBGRBufferedImage(final int orientedScaledWidth,
                                              final int orientedScaledHeight,
                                              final int unorientedScaledRegionWidth,
                                              final int unorientedScaledRegionHeight,
                                              final Orientation orientation) {
        BufferedImage bufImage = new BufferedImage(
                orientedScaledWidth, orientedScaledHeight,
                BufferedImage.TYPE_3BYTE_BGR);
        MemorySegment comps    = opj_image.comps(image);
        MemorySegment comp0    = comps.reinterpret(opj_image_comp_t.layout().byteSize());
        int sampleSize         = opj_image_comp_t.prec(comp0);
        int numSampleBytes     = Math.max(sampleSize / 8, 1);

        for (int b = 0; b < 3; b++) { // for each band/component
            MemorySegment comp = comps.asSlice(
                    b * opj_image_comp_t.layout().byteSize(),
                    opj_image_comp_t.layout().byteSize());
            byte[] band = opj_image_comp_t.data(comp)
                    .asSlice(0, (long) unorientedScaledRegionWidth * unorientedScaledRegionHeight * 4)
                    .toArray(ValueLayout.JAVA_BYTE);
            // If the orientation is 0, we can write the samples directly into
            // the Raster's underlying DataBuffer, which is much faster than
            // going through the Raster.
            switch (orientation) {
                case ROTATE_0 -> {
                    byte[] bgrData = ((DataBufferByte) bufImage.getRaster().getDataBuffer()).getData();
                    for (int i = 0; i < unorientedScaledRegionWidth * unorientedScaledRegionHeight; i++) {
                        // (2 - b): swap RGB to BGR
                        bgrData[(i * 3) + (2 - b)] = (numSampleBytes == 1) ?
                                band[i * 4] :
                                (byte) ((band[i * 4] << 8) | band[i * 4 + 1]);
                    }
                }
                default -> {
                    WritableRaster raster = bufImage.getRaster();
                    int i = 0;
                    for (int y = 0; y < unorientedScaledRegionHeight; y++) {
                        for (int x = 0; x < unorientedScaledRegionWidth; x++) {
                            int rasterX = adjustX(x, y,
                                    unorientedScaledRegionWidth,
                                    unorientedScaledRegionHeight,
                                    orientation);
                            int rasterY = adjustY(x, y,
                                    unorientedScaledRegionWidth,
                                    unorientedScaledRegionHeight,
                                    orientation);
                            int sample = (numSampleBytes == 1) ?
                                    band[i * 4] :
                                    ((band[i * 4] << 8) | band[i * 4 + 1]);
                            raster.setSample(rasterX, rasterY, b, sample);
                            i++;
                        }
                    }
                }
            }
        }
        return bufImage;
    }

    private BufferedImage newABGRBufferedImage(final int orientedScaledWidth,
                                               final int orientedScaledHeight,
                                               final int unorientedScaledRegionWidth,
                                               final int unorientedScaledRegionHeight,
                                               final Orientation orientation) {
        BufferedImage bufImage = new BufferedImage(
                orientedScaledWidth, orientedScaledHeight,
                BufferedImage.TYPE_4BYTE_ABGR);
        MemorySegment comps    = opj_image.comps(image);
        MemorySegment comp0    = comps.reinterpret(opj_image_comp_t.layout().byteSize());
        int sampleSize         = opj_image_comp_t.prec(comp0);
        int numSampleBytes     = Math.max(sampleSize / 8, 1);

        for (int b = 0; b < 4; b++) { // for each band/component
            MemorySegment comp = comps.asSlice(
                    b * opj_image_comp_t.layout().byteSize(),
                    opj_image_comp_t.layout().byteSize());
            byte[] band = opj_image_comp_t.data(comp)
                    .asSlice(0, (long) unorientedScaledRegionWidth * unorientedScaledRegionHeight * 4)
                    .toArray(ValueLayout.JAVA_BYTE);
            // If the orientation is 0, we can write the samples directly into
            // the Raster's underlying DataBuffer, which is much faster than
            // going through the Raster.
            switch (orientation) {
                case ROTATE_0 -> {
                    byte[] abgrData = ((DataBufferByte) bufImage.getRaster().getDataBuffer()).getData();
                    for (int i = 0; i < unorientedScaledRegionWidth * unorientedScaledRegionHeight; i++) {
                        // (3 - b): swap RGBA to ABGR
                        abgrData[(i * 4) + (3 - b)] = (numSampleBytes == 1) ?
                                band[i * 4] :
                                (byte) ((band[i * 4] << 8) | band[i * 4 + 1]);
                    }
                }
                default -> {
                    WritableRaster raster = bufImage.getRaster();
                    int i = 0;
                    for (int y = 0; y < unorientedScaledRegionHeight; y++) {
                        for (int x = 0; x < unorientedScaledRegionWidth; x++) {
                            int rasterX = adjustX(x, y,
                                    unorientedScaledRegionWidth,
                                    unorientedScaledRegionHeight,
                                    orientation);
                            int rasterY = adjustY(x, y,
                                    unorientedScaledRegionWidth,
                                    unorientedScaledRegionHeight,
                                    orientation);
                            int sample = (numSampleBytes == 1) ?
                                    band[i * 4] :
                                    ((band[i * 4] << 8) | band[i * 4 + 1]);
                            raster.setSample(rasterX, rasterY, b, sample);
                            i++;
                        }
                    }
                }
            }
        }
        return bufImage;
    }

    private static int adjustX(final int x,
                               final int y,
                               final int unorientedScaledRegionWidth,
                               final int unorientedScaledRegionHeight,
                               final Orientation orientation) {
        return switch (orientation) {
            case ROTATE_90  -> unorientedScaledRegionHeight - 1 - y;
            case ROTATE_180 -> unorientedScaledRegionWidth - 1 - x;
            case ROTATE_270 -> y;
            default         -> x;
        };
    }

    private static int adjustY(final int x,
                               final int y,
                               final int unorientedScaledRegionWidth,
                               final int unorientedScaledRegionHeight,
                               final Orientation orientation) {
        return switch (orientation) {
            case ROTATE_90  -> x;
            case ROTATE_180 -> unorientedScaledRegionHeight - 1 - y;
            case ROTATE_270 -> unorientedScaledRegionWidth - 1 - x;
            default         -> y;
        };
    }

}