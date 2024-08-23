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

import is.galia.codec.DecoderHint;
import is.galia.codec.SourceFormatException;
import is.galia.codec.tiff.Directory;
import is.galia.codec.iptc.DataSet;
import is.galia.image.Region;
import is.galia.image.Size;
import is.galia.image.Format;
import is.galia.image.MediaType;
import is.galia.image.Metadata;
import is.galia.image.ReductionFactor;
import is.galia.plugin.openjpeg.test.TestUtils;
import is.galia.stream.PathImageInputStream;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.imageio.stream.ImageInputStream;
import java.awt.image.BufferedImage;
import java.lang.foreign.Arena;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class OpenJPEGDecoderTest {

    private static final double DELTA         = 0.0000001;
    private static final Path DEFAULT_FIXTURE =
            TestUtils.getFixture("rgb-8bit-orientation-0.jp2");
    private static final boolean SAVE_IMAGES  = true;

    private final Arena arena = Arena.ofConfined();
    private OpenJPEGDecoder instance;

    @BeforeAll
    public static void beforeClass() {
        try (OpenJPEGDecoder decoder = new OpenJPEGDecoder()) {
            decoder.onApplicationStart();
        }
    }

    @BeforeEach
    public void setUp() {
        instance = new OpenJPEGDecoder();
        instance.setArena(arena);
        instance.initializePlugin();
        instance.setSource(DEFAULT_FIXTURE);
    }

    @AfterEach
    public void tearDown() {
        instance.close();
        arena.close();
    }

    //region Plugin methods

    @Test
    void getPluginConfigKeys() {
        Set<String> keys = instance.getPluginConfigKeys();
        assertEquals(1, keys.size());
    }

    @Test
    void getPluginName() {
        assertEquals(OpenJPEGDecoder.class.getSimpleName(),
                instance.getPluginName());
    }

    //endregion
    //region Decoder methods

    /* detectFormat() */

    @Test
    void detectFormatWithJP2Bytes() throws Exception {
        assertEquals(Formats.JP2, instance.detectFormat());
    }

    @Test
    void detectFormatWithJPHBytes() throws Exception {
        instance.setSource(TestUtils.getFixture("jph.jph"));
        assertEquals(Formats.JPH, instance.detectFormat());
    }

    @Test
    void detectFormatWithJPMBytes() throws Exception {
        // TODO: write this
    }

    @Test
    void detectFormatWithJPXBytes() throws Exception {
        instance.setSource(TestUtils.getFixture("jpx.jpf"));
        assertEquals(Formats.JPX, instance.detectFormat());
    }

    @Test
    void detectFormatWithUnsupportedBytes() throws Exception {
        instance.setSource(TestUtils.getFixture("unknown"));
        assertEquals(Format.UNKNOWN, instance.detectFormat());
    }

    /* getNumImages() */

    @Test
    void getNumImages() throws Exception {
        assertEquals(1, instance.getNumImages());
    }

    /* getNumResolutions() */

    @Test
    void getNumResolutionsWithNonexistentImage() {
        instance.setSource(TestUtils.getFixture("bogus"));
        assertThrows(NoSuchFileException.class,
                () -> instance.getNumResolutions());
    }

    @Test
    void getNumResolutionsWithEmptyImage() {
        instance.setSource(TestUtils.getFixture("empty"));
        assertThrows(SourceFormatException.class, () ->
                instance.getNumResolutions());
    }

    @Test
    void getNumResolutionsWithInvalidImage() {
        instance.setSource(TestUtils.getFixture("unknown"));
        assertThrows(SourceFormatException.class,
                () -> instance.getNumResolutions());
    }

    @Test
    void getNumResolutions() throws Exception {
        assertEquals(6, instance.getNumResolutions());
    }

    /* getSize() */

    @Test
    void getSizeWithNonexistentImage() {
        instance.setSource(TestUtils.getFixture("bogus"));
        assertThrows(NoSuchFileException.class, () -> instance.getSize(0));
    }

    @Test
    void getSizeWithEmptyImage() {
        instance.setSource(TestUtils.getFixture("empty"));
        assertThrows(SourceFormatException.class, () ->
                instance.getSize(0));
    }

    @Test
    void getSizeWithInvalidImage() {
        instance.setSource(TestUtils.getFixture("unknown"));
        assertThrows(SourceFormatException.class, () -> instance.getSize(0));
    }

    @Test
    void getSizeWithIllegalImageIndex() {
        assertThrows(IndexOutOfBoundsException.class,
                () -> instance.getSize(1));
    }

    @Test
    void getSize() throws Exception {
        Size size = instance.getSize(0);
        assertEquals(150, size.intWidth());
        assertEquals(200, size.intHeight());
    }

    /* getSupportedFormats() */

    @Test
    void getSupportedFormats() {
        Set<Format> formats = instance.getSupportedFormats();
        assertEquals(4, formats.size());
        { // J2C
            Format jp2 = formats.stream()
                    .filter(f -> f.key().equals("j2c")).findAny().orElseThrow();
            assertEquals("j2c", jp2.key());
            assertEquals("JPEG2000 Codestream", jp2.name());
            assertEquals(List.of(new MediaType("image", "jp2"),
                            new MediaType("image", "jpeg2000")),
                    jp2.mediaTypes());
            assertEquals(List.of("j2c", "jpt", "jpc"),
                    jp2.extensions());
            assertTrue(jp2.isRaster());
            assertFalse(jp2.isVideo());
            assertTrue(jp2.supportsTransparency());
        }
        { // JP2
            Format jp2 = formats.stream()
                    .filter(f -> f.key().equals("jp2")).findAny().orElseThrow();
            assertEquals("jp2", jp2.key());
            assertEquals("JPEG2000", jp2.name());
            assertEquals(List.of(new MediaType("image", "jp2"),
                            new MediaType("image", "jpeg2000")),
                    jp2.mediaTypes());
            assertEquals(List.of("jp2", "j2k", "jpg2"),
                    jp2.extensions());
            assertTrue(jp2.isRaster());
            assertFalse(jp2.isVideo());
            assertTrue(jp2.supportsTransparency());
        }
        { // JPH
            Format jph = formats.stream()
                    .filter(f -> f.key().equals("jph")).findAny().orElseThrow();
            assertEquals("jph", jph.key());
            assertEquals("HTJ2K", jph.name());
            assertEquals(List.of(new MediaType("image", "jph")),
                    jph.mediaTypes());
            assertEquals(List.of("jph"), jph.extensions());
            assertTrue(jph.isRaster());
            assertFalse(jph.isVideo());
            assertTrue(jph.supportsTransparency());
        }
        { // JPX
            Format jpx = formats.stream()
                    .filter(f -> f.key().equals("jpx")).findAny().orElseThrow();
            assertEquals("jpx", jpx.key());
            assertEquals("JPX", jpx.name());
            assertEquals(List.of(new MediaType("image", "jpx")),
                    jpx.mediaTypes());
            assertEquals(List.of("jpf", "jpx"), jpx.extensions());
            assertTrue(jpx.isRaster());
            assertFalse(jpx.isVideo());
            assertTrue(jpx.supportsTransparency());
        }
    }

    /* getTileSize() */

    @Test
    void getTileSizeWithNonexistentImage() {
        instance.setSource(TestUtils.getFixture("bogus"));
        assertThrows(NoSuchFileException.class,
                () -> instance.getTileSize(0));
    }

    @Test
    void getTileSizeWithEmptyImage() {
        instance.setSource(TestUtils.getFixture("empty"));
        assertThrows(SourceFormatException.class, () ->
                instance.getTileSize(0));
    }

    @Test
    void getTileSizeWithInvalidImage() {
        instance.setSource(TestUtils.getFixture("unknown"));
        assertThrows(SourceFormatException.class,
                () -> instance.getTileSize(0));
    }

    @Test
    void getTileSizeWithIllegalImageIndex() {
        assertThrows(IndexOutOfBoundsException.class,
                () -> instance.getTileSize(1));
    }

    @Test
    void getTileSizeWithNonTiledImage() throws Exception {
        Size tileSize = instance.getTileSize(0);
        assertEquals(150, tileSize.intWidth());
        assertEquals(200, tileSize.intHeight());
    }

    @Test
    void getTileSizeWithTiledImage() throws Exception {
        instance.setSource(TestUtils.getFixture("tiled.jp2"));
        Size tileSize = instance.getTileSize(0);
        assertEquals(32, tileSize.intWidth());
        assertEquals(28, tileSize.intHeight());
    }

    /* read(int) */

    @Test
    void decode1WithNonexistentImage() {
        instance.setSource(TestUtils.getFixture("bogus"));
        assertThrows(NoSuchFileException.class, () -> instance.decode(0));
    }

    @Test
    void decode1WithEmptyImage() {
        instance.setSource(TestUtils.getFixture("empty"));
        assertThrows(SourceFormatException.class, () ->
                instance.decode(0));
    }

    @Test
    void decode1WithInvalidImage() {
        instance.setSource(TestUtils.getFixture("unknown"));
        assertThrows(SourceFormatException.class, () -> instance.decode(0));
    }

    @Test
    void decode1WithIllegalImageIndex() {
        assertThrows(IndexOutOfBoundsException.class,
                () -> instance.decode(1));
    }

    @Test
    void decode1FromFile() throws Exception {
        BufferedImage image = instance.decode(0);
        assertEquals(150, image.getWidth());
        assertEquals(200, image.getHeight());
        assertEquals(3, image.getSampleModel().getNumBands());
        if (SAVE_IMAGES) TestUtils.save(image);
    }

    @Test
    void decode1FromStream() throws Exception {
        try (ImageInputStream is = new PathImageInputStream(DEFAULT_FIXTURE)) {
            instance.setSource(is);
            BufferedImage image = instance.decode(0);
            assertEquals(150, image.getWidth());
            assertEquals(200, image.getHeight());
            if (SAVE_IMAGES) TestUtils.save(image);
        }
    }

    @Test
    void decode1WithJP2Image() throws Exception {
        BufferedImage image = instance.decode(0);
        assertEquals(150, image.getWidth());
        assertEquals(200, image.getHeight());
        assertEquals(3, image.getSampleModel().getNumBands());
        if (SAVE_IMAGES) TestUtils.save(image);
    }

    @Test
    void decode1WithJPHImage() throws Exception {
        instance.setSource(TestUtils.getFixture("jph.jph"));
        BufferedImage image = instance.decode(0);
        assertEquals(100, image.getWidth());
        assertEquals(88, image.getHeight());
        assertEquals(3, image.getSampleModel().getNumBands());
        if (SAVE_IMAGES) TestUtils.save(image);
    }

    @Test
    void decode1WithJPXImage() throws Exception {
        instance.setSource(TestUtils.getFixture("jpx.jpf"));
        BufferedImage image = instance.decode(0);
        assertEquals(64, image.getWidth());
        assertEquals(56, image.getHeight());
        assertEquals(3, image.getSampleModel().getNumBands());
        if (SAVE_IMAGES) TestUtils.save(image);
    }

    @Test
    void decode1WithGray8BitImage() throws Exception {
        instance.setSource(TestUtils.getFixture("gray-8bit-orientation-0.jp2"));
        BufferedImage image = instance.decode(0);
        assertEquals(64, image.getWidth());
        assertEquals(56, image.getHeight());
        assertEquals(1, image.getSampleModel().getNumBands());
        if (SAVE_IMAGES) TestUtils.save(image);
    }

    @Test
    void decode1WithGray16BitImage() throws Exception {
        instance.setSource(TestUtils.getFixture("gray-16bit-orientation-0.jp2"));
        BufferedImage image = instance.decode(0);
        assertEquals(64, image.getWidth());
        assertEquals(56, image.getHeight());
        assertEquals(1, image.getSampleModel().getNumBands());
        if (SAVE_IMAGES) TestUtils.save(image);
    }

    @Test
    void decode1WithGrayAlpha8BitImage() throws Exception {
        instance.setSource(TestUtils.getFixture("graya-8bit-orientation-0.jp2"));
        BufferedImage image = instance.decode(0);
        assertEquals(400, image.getWidth());
        assertEquals(300, image.getHeight());
        assertEquals(4, image.getSampleModel().getNumBands());
        if (SAVE_IMAGES) TestUtils.save(image);
    }

    @Test
    void decode1WithGrayAlpha16BitImage() throws Exception {
        instance.setSource(TestUtils.getFixture("graya-16bit-orientation-0.jp2"));
        BufferedImage image = instance.decode(0);
        assertEquals(400, image.getWidth());
        assertEquals(300, image.getHeight());
        assertEquals(4, image.getSampleModel().getNumBands());
        if (SAVE_IMAGES) TestUtils.save(image);
    }

    @Test
    void decode1WithRGB8BitImage() throws Exception {
        BufferedImage image = instance.decode(0);
        assertEquals(150, image.getWidth());
        assertEquals(200, image.getHeight());
        assertEquals(3, image.getSampleModel().getNumBands());
        if (SAVE_IMAGES) TestUtils.save(image);
    }

    @Test
    void decode1WithRGB16BitImage() throws Exception {
        instance.setSource(TestUtils.getFixture("rgb-16bit-orientation-0.jp2"));
        BufferedImage image = instance.decode(0);
        assertEquals(64, image.getWidth());
        assertEquals(56, image.getHeight());
        assertEquals(3, image.getSampleModel().getNumBands());
        if (SAVE_IMAGES) TestUtils.save(image);
    }

    @Test
    void decode1WithRGBA8BitImage() throws Exception {
        instance.setSource(TestUtils.getFixture("rgba-8bit-orientation-0.jp2"));
        BufferedImage image = instance.decode(0);
        assertEquals(64, image.getWidth());
        assertEquals(56, image.getHeight());
        assertEquals(4, image.getSampleModel().getNumBands());
        if (SAVE_IMAGES) TestUtils.save(image);
    }

    @Test
    void decode1WithRGBA16BitImage() throws Exception {
        instance.setSource(TestUtils.getFixture("rgba-16bit-orientation-0.jp2"));
        BufferedImage image = instance.decode(0);
        assertEquals(64, image.getWidth());
        assertEquals(56, image.getHeight());
        assertEquals(4, image.getSampleModel().getNumBands());
        if (SAVE_IMAGES) TestUtils.save(image);
    }

    @Test
    void decode1WithICCProfile() throws Exception {
        instance.setSource(TestUtils.getFixture("colorspin.jp2"));
        BufferedImage image = instance.decode(0);
        if (SAVE_IMAGES) TestUtils.save(image);
    }

    /* read(int, ...) */

    @Test
    void decode2WithNonexistentImage() {
        instance.setSource(TestUtils.getFixture("bogus"));
        assertThrows(NoSuchFileException.class, () ->
                instance.decode(0,
                        new Region(0, 0, 9999, 9999),
                        new double[] { 1, 1 },
                        new ReductionFactor(),
                        null,
                        EnumSet.noneOf(DecoderHint.class)));
    }

    @Test
    void decode2WithEmptyImage() {
        instance.setSource(TestUtils.getFixture("empty"));
        assertThrows(SourceFormatException.class, () ->
                instance.decode(0,
                        new Region(0, 0, 9999, 9999),
                        new double[] { 1, 1 },
                        new ReductionFactor(),
                        null,
                        EnumSet.noneOf(DecoderHint.class)));
    }

    @Test
    void decode2WithInvalidImage() {
        instance.setSource(TestUtils.getFixture("unknown"));
        assertThrows(SourceFormatException.class, () ->
                instance.decode(0,
                        new Region(0, 0, 9999, 9999),
                        new double[] { 1, 1 },
                        new ReductionFactor(),
                        null,
                        EnumSet.noneOf(DecoderHint.class)));
    }

    @Test
    void decode2WithIllegalImageIndex() {
        assertThrows(IndexOutOfBoundsException.class,
                () -> instance.decode(1,
                        new Region(0, 0, 9999, 9999),
                        new double[] { 1, 1 },
                        new ReductionFactor(),
                        null,
                        EnumSet.noneOf(DecoderHint.class)));
    }

    @Test
    void decode2WithGray8BitWithOrientation0() throws Exception {
        instance.setSource(TestUtils.getFixture("gray-8bit-orientation-0.jp2"));
        final Region roi                      = new Region(0, 0, 0, 0, true);
        final double[] scales                 = { 1, 1 };
        final ReductionFactor reductionFactor = new ReductionFactor();
        final double[] diffScales             = new double[2];
        final Set<DecoderHint> hints          = EnumSet.noneOf(DecoderHint.class);

        BufferedImage image = instance.decode(0, roi, scales,
                reductionFactor, diffScales, hints);
        assertEquals(64, image.getWidth());
        assertEquals(56, image.getHeight());
        assertEquals(0, reductionFactor.factor);
        assertEquals(1, diffScales[0], DELTA);
        assertEquals(1, diffScales[1], DELTA);
        assertTrue(hints.contains(DecoderHint.ALREADY_ORIENTED));
        if (SAVE_IMAGES) TestUtils.save(image);
    }

    @Test
    void decode2WithGray8BitWithOrientation90() throws Exception {
        instance.setSource(TestUtils.getFixture("gray-8bit-orientation-90.jp2"));
        final Region roi                      = new Region(0, 0, 0, 0, true);
        final double[] scales                 = { 1, 1 };
        final ReductionFactor reductionFactor = new ReductionFactor();
        final double[] diffScales             = new double[2];
        final Set<DecoderHint> hints          = EnumSet.noneOf(DecoderHint.class);

        BufferedImage image = instance.decode(0, roi, scales,
                reductionFactor, diffScales, hints);
        assertEquals(64, image.getWidth());
        assertEquals(56, image.getHeight());
        assertEquals(0, reductionFactor.factor);
        assertEquals(1, diffScales[0], DELTA);
        assertEquals(1, diffScales[1], DELTA);
        assertTrue(hints.contains(DecoderHint.ALREADY_ORIENTED));
        if (SAVE_IMAGES) TestUtils.save(image);
    }

    @Test
    void decode2WithGray8BitWithOrientation180() throws Exception {
        instance.setSource(TestUtils.getFixture("gray-8bit-orientation-180.jp2"));
        final Region roi                      = new Region(0, 0, 0, 0, true);
        final double[] scales                 = { 1, 1 };
        final ReductionFactor reductionFactor = new ReductionFactor();
        final double[] diffScales             = new double[2];
        final Set<DecoderHint> hints          = EnumSet.noneOf(DecoderHint.class);

        BufferedImage image = instance.decode(0, roi, scales,
                reductionFactor, diffScales, hints);
        assertEquals(64, image.getWidth());
        assertEquals(56, image.getHeight());
        assertEquals(0, reductionFactor.factor);
        assertEquals(1, diffScales[0], DELTA);
        assertEquals(1, diffScales[1], DELTA);
        assertTrue(hints.contains(DecoderHint.ALREADY_ORIENTED));
        if (SAVE_IMAGES) TestUtils.save(image);
    }

    @Test
    void decode2WithGray8BitWithOrientation270() throws Exception {
        instance.setSource(TestUtils.getFixture("gray-8bit-orientation-270.jp2"));
        final Region roi                      = new Region(0, 0, 0, 0, true);
        final double[] scales                 = { 1, 1 };
        final ReductionFactor reductionFactor = new ReductionFactor();
        final double[] diffScales             = new double[2];
        final Set<DecoderHint> hints          = EnumSet.noneOf(DecoderHint.class);

        BufferedImage image = instance.decode(0, roi, scales,
                reductionFactor, diffScales, hints);
        assertEquals(64, image.getWidth());
        assertEquals(56, image.getHeight());
        assertEquals(0, reductionFactor.factor);
        assertEquals(1, diffScales[0], DELTA);
        assertEquals(1, diffScales[1], DELTA);
        assertTrue(hints.contains(DecoderHint.ALREADY_ORIENTED));
        if (SAVE_IMAGES) TestUtils.save(image);
    }

    @Test
    void decode2WithGray16BitWithOrientation0() throws Exception {
        instance.setSource(TestUtils.getFixture("gray-16bit-orientation-0.jp2"));
        final Region roi                      = new Region(0, 0, 0, 0, true);
        final double[] scales                 = { 1, 1 };
        final ReductionFactor reductionFactor = new ReductionFactor();
        final double[] diffScales             = new double[2];
        final Set<DecoderHint> hints          = EnumSet.noneOf(DecoderHint.class);

        BufferedImage image = instance.decode(0, roi, scales,
                reductionFactor, diffScales, hints);
        assertEquals(64, image.getWidth());
        assertEquals(56, image.getHeight());
        assertEquals(0, reductionFactor.factor);
        assertEquals(1, diffScales[0], DELTA);
        assertEquals(1, diffScales[1], DELTA);
        assertTrue(hints.contains(DecoderHint.ALREADY_ORIENTED));
        if (SAVE_IMAGES) TestUtils.save(image);
    }

    @Test
    void decode2WithGray16BitWithOrientation90() throws Exception {
        instance.setSource(TestUtils.getFixture("gray-16bit-orientation-90.jp2"));
        final Region roi                      = new Region(0, 0, 0, 0, true);
        final double[] scales                 = { 1, 1 };
        final ReductionFactor reductionFactor = new ReductionFactor();
        final double[] diffScales             = new double[2];
        final Set<DecoderHint> hints          = EnumSet.noneOf(DecoderHint.class);

        BufferedImage image = instance.decode(0, roi, scales,
                reductionFactor, diffScales, hints);
        assertEquals(64, image.getWidth());
        assertEquals(56, image.getHeight());
        assertEquals(0, reductionFactor.factor);
        assertEquals(1, diffScales[0], DELTA);
        assertEquals(1, diffScales[1], DELTA);
        assertTrue(hints.contains(DecoderHint.ALREADY_ORIENTED));
        if (SAVE_IMAGES) TestUtils.save(image);
    }

    @Test
    void decode2WithGray16BitWithOrientation180() throws Exception {
        instance.setSource(TestUtils.getFixture("gray-16bit-orientation-180.jp2"));
        final Region roi                      = new Region(0, 0, 0, 0, true);
        final double[] scales                 = { 1, 1 };
        final ReductionFactor reductionFactor = new ReductionFactor();
        final double[] diffScales             = new double[2];
        final Set<DecoderHint> hints          = EnumSet.noneOf(DecoderHint.class);

        BufferedImage image = instance.decode(0, roi, scales,
                reductionFactor, diffScales, hints);
        assertEquals(64, image.getWidth());
        assertEquals(56, image.getHeight());
        assertEquals(0, reductionFactor.factor);
        assertEquals(1, diffScales[0], DELTA);
        assertEquals(1, diffScales[1], DELTA);
        assertTrue(hints.contains(DecoderHint.ALREADY_ORIENTED));
        if (SAVE_IMAGES) TestUtils.save(image);
    }

    @Test
    void decode2WithGray16BitWithOrientation270() throws Exception {
        instance.setSource(TestUtils.getFixture("gray-16bit-orientation-270.jp2"));
        final Region roi                      = new Region(0, 0, 0, 0, true);
        final double[] scales                 = { 1, 1 };
        final ReductionFactor reductionFactor = new ReductionFactor();
        final double[] diffScales             = new double[2];
        final Set<DecoderHint> hints          = EnumSet.noneOf(DecoderHint.class);

        BufferedImage image = instance.decode(0, roi, scales,
                reductionFactor, diffScales, hints);
        assertEquals(64, image.getWidth());
        assertEquals(56, image.getHeight());
        assertEquals(0, reductionFactor.factor);
        assertEquals(1, diffScales[0], DELTA);
        assertEquals(1, diffScales[1], DELTA);
        assertTrue(hints.contains(DecoderHint.ALREADY_ORIENTED));
        if (SAVE_IMAGES) TestUtils.save(image);
    }

    @Test
    void decode2WithGrayAlpha8BitWithOrientation0() throws Exception {
        instance.setSource(TestUtils.getFixture("graya-8bit-orientation-0.jp2"));
        final Region roi                      = new Region(0, 0, 0, 0, true);
        final double[] scales                 = { 1, 1 };
        final ReductionFactor reductionFactor = new ReductionFactor();
        final double[] diffScales             = new double[2];
        final Set<DecoderHint> hints          = EnumSet.noneOf(DecoderHint.class);

        BufferedImage image = instance.decode(0, roi, scales,
                reductionFactor, diffScales, hints);
        assertEquals(400, image.getWidth());
        assertEquals(300, image.getHeight());
        assertEquals(0, reductionFactor.factor);
        assertEquals(1, diffScales[0], DELTA);
        assertEquals(1, diffScales[1], DELTA);
        assertTrue(hints.contains(DecoderHint.ALREADY_ORIENTED));
        if (SAVE_IMAGES) TestUtils.save(image);
    }

    @Test
    void decode2WithGrayAlpha8BitWithOrientation90() throws Exception {
        instance.setSource(TestUtils.getFixture("graya-8bit-orientation-90.jp2"));
        final Region roi                      = new Region(0, 0, 0, 0, true);
        final double[] scales                 = { 1, 1 };
        final ReductionFactor reductionFactor = new ReductionFactor();
        final double[] diffScales             = new double[2];
        final Set<DecoderHint> hints          = EnumSet.noneOf(DecoderHint.class);

        BufferedImage image = instance.decode(0, roi, scales,
                reductionFactor, diffScales, hints);
        assertEquals(400, image.getWidth());
        assertEquals(300, image.getHeight());
        assertEquals(0, reductionFactor.factor);
        assertEquals(1, diffScales[0], DELTA);
        assertEquals(1, diffScales[1], DELTA);
        assertTrue(hints.contains(DecoderHint.ALREADY_ORIENTED));
        if (SAVE_IMAGES) TestUtils.save(image);
    }

    @Test
    void decode2WithGrayAlpha8BitWithOrientation180() throws Exception {
        instance.setSource(TestUtils.getFixture("graya-8bit-orientation-180.jp2"));
        final Region roi                      = new Region(0, 0, 0, 0, true);
        final double[] scales                 = { 1, 1 };
        final ReductionFactor reductionFactor = new ReductionFactor();
        final double[] diffScales             = new double[2];
        final Set<DecoderHint> hints          = EnumSet.noneOf(DecoderHint.class);

        BufferedImage image = instance.decode(0, roi, scales,
                reductionFactor, diffScales, hints);
        assertEquals(400, image.getWidth());
        assertEquals(300, image.getHeight());
        assertEquals(0, reductionFactor.factor);
        assertEquals(1, diffScales[0], DELTA);
        assertEquals(1, diffScales[1], DELTA);
        assertTrue(hints.contains(DecoderHint.ALREADY_ORIENTED));
        if (SAVE_IMAGES) TestUtils.save(image);
    }

    @Test
    void decode2WithGrayAlpha8BitWithOrientation270() throws Exception {
        instance.setSource(TestUtils.getFixture("graya-8bit-orientation-270.jp2"));
        final Region roi                      = new Region(0, 0, 0, 0, true);
        final double[] scales                 = { 1, 1 };
        final ReductionFactor reductionFactor = new ReductionFactor();
        final double[] diffScales             = new double[2];
        final Set<DecoderHint> hints          = EnumSet.noneOf(DecoderHint.class);

        BufferedImage image = instance.decode(0, roi, scales,
                reductionFactor, diffScales, hints);
        assertEquals(400, image.getWidth());
        assertEquals(300, image.getHeight());
        assertEquals(0, reductionFactor.factor);
        assertEquals(1, diffScales[0], DELTA);
        assertEquals(1, diffScales[1], DELTA);
        assertTrue(hints.contains(DecoderHint.ALREADY_ORIENTED));
        if (SAVE_IMAGES) TestUtils.save(image);
    }

    @Test
    void decode2WithGrayAlpha16BitWithOrientation0() throws Exception {
        instance.setSource(TestUtils.getFixture("graya-16bit-orientation-0.jp2"));
        final Region roi                      = new Region(0, 0, 0, 0, true);
        final double[] scales                 = { 1, 1 };
        final ReductionFactor reductionFactor = new ReductionFactor();
        final double[] diffScales             = new double[2];
        final Set<DecoderHint> hints          = EnumSet.noneOf(DecoderHint.class);

        BufferedImage image = instance.decode(0, roi, scales,
                reductionFactor, diffScales, hints);
        assertEquals(400, image.getWidth());
        assertEquals(300, image.getHeight());
        assertEquals(0, reductionFactor.factor);
        assertEquals(1, diffScales[0], DELTA);
        assertEquals(1, diffScales[1], DELTA);
        assertTrue(hints.contains(DecoderHint.ALREADY_ORIENTED));
        if (SAVE_IMAGES) TestUtils.save(image);
    }

    @Test
    void decode2WithGrayAlpha16BitWithOrientation90() throws Exception {
        instance.setSource(TestUtils.getFixture("graya-16bit-orientation-90.jp2"));
        final Region roi                      = new Region(0, 0, 0, 0, true);
        final double[] scales                 = { 1, 1 };
        final ReductionFactor reductionFactor = new ReductionFactor();
        final double[] diffScales             = new double[2];
        final Set<DecoderHint> hints          = EnumSet.noneOf(DecoderHint.class);

        BufferedImage image = instance.decode(0, roi, scales,
                reductionFactor, diffScales, hints);
        assertEquals(400, image.getWidth());
        assertEquals(300, image.getHeight());
        assertEquals(0, reductionFactor.factor);
        assertEquals(1, diffScales[0], DELTA);
        assertEquals(1, diffScales[1], DELTA);
        assertTrue(hints.contains(DecoderHint.ALREADY_ORIENTED));
        if (SAVE_IMAGES) TestUtils.save(image);
    }

    @Test
    void decode2WithGrayAlpha16BitWithOrientation180() throws Exception {
        instance.setSource(TestUtils.getFixture("graya-16bit-orientation-180.jp2"));
        final Region roi                      = new Region(0, 0, 0, 0, true);
        final double[] scales                 = { 1, 1 };
        final ReductionFactor reductionFactor = new ReductionFactor();
        final double[] diffScales             = new double[2];
        final Set<DecoderHint> hints          = EnumSet.noneOf(DecoderHint.class);

        BufferedImage image = instance.decode(0, roi, scales,
                reductionFactor, diffScales, hints);
        assertEquals(400, image.getWidth());
        assertEquals(300, image.getHeight());
        assertEquals(0, reductionFactor.factor);
        assertEquals(1, diffScales[0], DELTA);
        assertEquals(1, diffScales[1], DELTA);
        assertTrue(hints.contains(DecoderHint.ALREADY_ORIENTED));
        if (SAVE_IMAGES) TestUtils.save(image);
    }

    @Test
    void decode2WithGrayAlpha16BitWithOrientation270() throws Exception {
        instance.setSource(TestUtils.getFixture("graya-16bit-orientation-270.jp2"));
        final Region roi                      = new Region(0, 0, 0, 0, true);
        final double[] scales                 = { 1, 1 };
        final ReductionFactor reductionFactor = new ReductionFactor();
        final double[] diffScales             = new double[2];
        final Set<DecoderHint> hints          = EnumSet.noneOf(DecoderHint.class);

        BufferedImage image = instance.decode(0, roi, scales,
                reductionFactor, diffScales, hints);
        assertEquals(400, image.getWidth());
        assertEquals(300, image.getHeight());
        assertEquals(0, reductionFactor.factor);
        assertEquals(1, diffScales[0], DELTA);
        assertEquals(1, diffScales[1], DELTA);
        assertTrue(hints.contains(DecoderHint.ALREADY_ORIENTED));
        if (SAVE_IMAGES) TestUtils.save(image);
    }

    @Test
    void decode2WithRGB8BitWithOrientation0() throws Exception {
        instance.setSource(TestUtils.getFixture("rgb-8bit-orientation-0.jp2"));
        final Region roi                      = new Region(85, 105, 30, 50);
        final double[] scales                 = { 0.45, 0.45 };
        final ReductionFactor reductionFactor = new ReductionFactor();
        final double[] diffScales             = new double[2];
        final Set<DecoderHint> hints          = EnumSet.noneOf(DecoderHint.class);

        BufferedImage image = instance.decode(0, roi, scales,
                reductionFactor, diffScales, hints);
        assertEquals(15, image.getWidth());
        assertEquals(25, image.getHeight());
        assertEquals(1, reductionFactor.factor);
        assertEquals(0.45 / 0.5, diffScales[0], DELTA);
        assertEquals(0.45 / 0.5, diffScales[1], DELTA);
        assertTrue(hints.contains(DecoderHint.ALREADY_ORIENTED));
        assertTrue(hints.contains(DecoderHint.NEEDS_DIFFERENTIAL_SCALE));
        if (SAVE_IMAGES) TestUtils.save(image);
    }

    @Test
    void decode2WithRGB8BitWithOrientation90() throws Exception {
        instance.setSource(TestUtils.getFixture("rgb-8bit-orientation-90.jp2"));
        final Region roi                      = new Region(85, 105, 30, 50);
        final double[] scales                 = { 0.45, 0.45 };
        final ReductionFactor reductionFactor = new ReductionFactor();
        final double[] diffScales             = new double[2];
        final Set<DecoderHint> hints          = EnumSet.noneOf(DecoderHint.class);

        BufferedImage image = instance.decode(0, roi, scales,
                reductionFactor, diffScales, hints);
        assertEquals(15, image.getWidth());
        assertEquals(25, image.getHeight());
        assertEquals(1, reductionFactor.factor);
        assertEquals(0.45 / 0.5, diffScales[0], DELTA);
        assertEquals(0.45 / 0.5, diffScales[1], DELTA);
        assertTrue(hints.contains(DecoderHint.ALREADY_ORIENTED));
        assertTrue(hints.contains(DecoderHint.NEEDS_DIFFERENTIAL_SCALE));
        if (SAVE_IMAGES) TestUtils.save(image);
    }

    @Test
    void decode2WithRGB8BitWithOrientation180() throws Exception {
        instance.setSource(TestUtils.getFixture("rgb-8bit-orientation-180.jp2"));
        final Region roi                      = new Region(85, 105, 30, 50);
        final double[] scales                 = { 0.45, 0.45 };
        final ReductionFactor reductionFactor = new ReductionFactor();
        final double[] diffScales             = new double[2];
        final Set<DecoderHint> hints          = EnumSet.noneOf(DecoderHint.class);

        BufferedImage image = instance.decode(0, roi, scales,
                reductionFactor, diffScales, hints);
        assertEquals(15, image.getWidth());
        assertEquals(25, image.getHeight());
        assertEquals(1, reductionFactor.factor);
        assertEquals(0.45 / 0.5, diffScales[0], DELTA);
        assertEquals(0.45 / 0.5, diffScales[1], DELTA);
        assertTrue(hints.contains(DecoderHint.ALREADY_ORIENTED));
        assertTrue(hints.contains(DecoderHint.NEEDS_DIFFERENTIAL_SCALE));
        if (SAVE_IMAGES) TestUtils.save(image);
    }

    @Test
    void decode2WithRGB8BitWithOrientation270() throws Exception {
        instance.setSource(TestUtils.getFixture("rgb-8bit-orientation-270.jp2"));
        final Region roi                      = new Region(85, 105, 30, 50);
        final double[] scales                 = { 0.45, 0.45 };
        final ReductionFactor reductionFactor = new ReductionFactor();
        final double[] diffScales             = new double[2];
        final Set<DecoderHint> hints          = EnumSet.noneOf(DecoderHint.class);

        BufferedImage image = instance.decode(0, roi, scales,
                reductionFactor, diffScales, hints);
        assertEquals(15, image.getWidth());
        assertEquals(25, image.getHeight());
        assertEquals(1, reductionFactor.factor);
        assertEquals(0.45 / 0.5, diffScales[0], DELTA);
        assertEquals(0.45 / 0.5, diffScales[1], DELTA);
        assertTrue(hints.contains(DecoderHint.ALREADY_ORIENTED));
        assertTrue(hints.contains(DecoderHint.NEEDS_DIFFERENTIAL_SCALE));
        if (SAVE_IMAGES) TestUtils.save(image);
    }

    @Test
    void decode2WithRGB16BitWithOrientation0() throws Exception {
        instance.setSource(TestUtils.getFixture("rgb-16bit-orientation-0.jp2"));
        final Region roi                      = new Region(0, 0, 0, 0, true);
        final double[] scales                 = { 1, 1 };
        final ReductionFactor reductionFactor = new ReductionFactor();
        final double[] diffScales             = new double[2];
        final Set<DecoderHint> hints          = EnumSet.noneOf(DecoderHint.class);

        BufferedImage image = instance.decode(0, roi, scales,
                reductionFactor, diffScales, hints);
        assertEquals(64, image.getWidth());
        assertEquals(56, image.getHeight());
        assertEquals(0, reductionFactor.factor);
        assertEquals(1, diffScales[0], DELTA);
        assertEquals(1, diffScales[1], DELTA);
        assertTrue(hints.contains(DecoderHint.ALREADY_ORIENTED));
        if (SAVE_IMAGES) TestUtils.save(image);
    }

    @Test
    void decode2WithRGB16BitWithOrientation90() throws Exception {
        instance.setSource(TestUtils.getFixture("rgb-16bit-orientation-90.jp2"));
        final Region roi                      = new Region(0, 0, 0, 0, true);
        final double[] scales                 = { 1, 1 };
        final ReductionFactor reductionFactor = new ReductionFactor();
        final double[] diffScales             = new double[2];
        final Set<DecoderHint> hints          = EnumSet.noneOf(DecoderHint.class);

        BufferedImage image = instance.decode(0, roi, scales,
                reductionFactor, diffScales, hints);
        assertEquals(64, image.getWidth());
        assertEquals(56, image.getHeight());
        assertEquals(0, reductionFactor.factor);
        assertEquals(1, diffScales[0], DELTA);
        assertEquals(1, diffScales[1], DELTA);
        assertTrue(hints.contains(DecoderHint.ALREADY_ORIENTED));
        if (SAVE_IMAGES) TestUtils.save(image);
    }

    @Test
    void decode2WithRGB16BitWithOrientation180() throws Exception {
        instance.setSource(TestUtils.getFixture("rgb-16bit-orientation-180.jp2"));
        final Region roi                      = new Region(0, 0, 0, 0, true);
        final double[] scales                 = { 1, 1 };
        final ReductionFactor reductionFactor = new ReductionFactor();
        final double[] diffScales             = new double[2];
        final Set<DecoderHint> hints          = EnumSet.noneOf(DecoderHint.class);

        BufferedImage image = instance.decode(0, roi, scales,
                reductionFactor, diffScales, hints);
        assertEquals(64, image.getWidth());
        assertEquals(56, image.getHeight());
        assertEquals(0, reductionFactor.factor);
        assertEquals(1, diffScales[0], DELTA);
        assertEquals(1, diffScales[1], DELTA);
        assertTrue(hints.contains(DecoderHint.ALREADY_ORIENTED));
        if (SAVE_IMAGES) TestUtils.save(image);
    }

    @Test
    void decode2WithRGB16BitWithOrientation270() throws Exception {
        instance.setSource(TestUtils.getFixture("rgb-16bit-orientation-270.jp2"));
        final Region roi                      = new Region(0, 0, 0, 0, true);
        final double[] scales                 = { 1, 1 };
        final ReductionFactor reductionFactor = new ReductionFactor();
        final double[] diffScales             = new double[2];
        final Set<DecoderHint> hints          = EnumSet.noneOf(DecoderHint.class);

        BufferedImage image = instance.decode(0, roi, scales,
                reductionFactor, diffScales, hints);
        assertEquals(64, image.getWidth());
        assertEquals(56, image.getHeight());
        assertEquals(0, reductionFactor.factor);
        assertEquals(1, diffScales[0], DELTA);
        assertEquals(1, diffScales[1], DELTA);
        assertTrue(hints.contains(DecoderHint.ALREADY_ORIENTED));
        if (SAVE_IMAGES) TestUtils.save(image);
    }

    @Test
    void decode2WithRGBA8BitWithOrientation0() throws Exception {
        instance.setSource(TestUtils.getFixture("rgba-8bit-orientation-0.jp2"));
        final Region roi                      = new Region(0, 0, 0, 0, true);
        final double[] scales                 = { 1, 1 };
        final ReductionFactor reductionFactor = new ReductionFactor();
        final double[] diffScales             = new double[2];
        final Set<DecoderHint> hints          = EnumSet.noneOf(DecoderHint.class);

        BufferedImage image = instance.decode(0, roi, scales,
                reductionFactor, diffScales, hints);
        assertEquals(64, image.getWidth());
        assertEquals(56, image.getHeight());
        assertEquals(0, reductionFactor.factor);
        assertEquals(1, diffScales[0], DELTA);
        assertEquals(1, diffScales[1], DELTA);
        assertTrue(hints.contains(DecoderHint.ALREADY_ORIENTED));
        if (SAVE_IMAGES) TestUtils.save(image);
    }

    @Test
    void decode2WithRGBA8BitWithOrientation90() throws Exception {
        instance.setSource(TestUtils.getFixture("rgba-8bit-orientation-90.jp2"));
        final Region roi                      = new Region(0, 0, 0, 0, true);
        final double[] scales                 = { 1, 1 };
        final ReductionFactor reductionFactor = new ReductionFactor();
        final double[] diffScales             = new double[2];
        final Set<DecoderHint> hints          = EnumSet.noneOf(DecoderHint.class);

        BufferedImage image = instance.decode(0, roi, scales,
                reductionFactor, diffScales, hints);
        assertEquals(64, image.getWidth());
        assertEquals(56, image.getHeight());
        assertEquals(0, reductionFactor.factor);
        assertEquals(1, diffScales[0], DELTA);
        assertEquals(1, diffScales[1], DELTA);
        assertTrue(hints.contains(DecoderHint.ALREADY_ORIENTED));
        if (SAVE_IMAGES) TestUtils.save(image);
    }

    @Test
    void decode2WithRGBA8BitWithOrientation180() throws Exception {
        instance.setSource(TestUtils.getFixture("rgba-8bit-orientation-180.jp2"));
        final Region roi                      = new Region(0, 0, 0, 0, true);
        final double[] scales                 = { 1, 1 };
        final ReductionFactor reductionFactor = new ReductionFactor();
        final double[] diffScales             = new double[2];
        final Set<DecoderHint> hints          = EnumSet.noneOf(DecoderHint.class);

        BufferedImage image = instance.decode(0, roi, scales,
                reductionFactor, diffScales, hints);
        assertEquals(64, image.getWidth());
        assertEquals(56, image.getHeight());
        assertEquals(0, reductionFactor.factor);
        assertEquals(1, diffScales[0], DELTA);
        assertEquals(1, diffScales[1], DELTA);
        assertTrue(hints.contains(DecoderHint.ALREADY_ORIENTED));
        if (SAVE_IMAGES) TestUtils.save(image);
    }

    @Test
    void decode2WithRGBA8BitWithOrientation270() throws Exception {
        instance.setSource(TestUtils.getFixture("rgba-8bit-orientation-270.jp2"));
        final Region roi                      = new Region(0, 0, 0, 0, true);
        final double[] scales                 = { 1, 1 };
        final ReductionFactor reductionFactor = new ReductionFactor();
        final double[] diffScales             = new double[2];
        final Set<DecoderHint> hints          = EnumSet.noneOf(DecoderHint.class);

        BufferedImage image = instance.decode(0, roi, scales,
                reductionFactor, diffScales, hints);
        assertEquals(64, image.getWidth());
        assertEquals(56, image.getHeight());
        assertEquals(0, reductionFactor.factor);
        assertEquals(1, diffScales[0], DELTA);
        assertEquals(1, diffScales[1], DELTA);
        assertTrue(hints.contains(DecoderHint.ALREADY_ORIENTED));
        if (SAVE_IMAGES) TestUtils.save(image);
    }

    @Test
    void decode2WithRGBA16BitWithOrientation0() throws Exception {
        instance.setSource(TestUtils.getFixture("rgba-16bit-orientation-0.jp2"));
        final Region roi                      = new Region(0, 0, 0, 0, true);
        final double[] scales                 = { 1, 1 };
        final ReductionFactor reductionFactor = new ReductionFactor();
        final double[] diffScales             = new double[2];
        final Set<DecoderHint> hints          = EnumSet.noneOf(DecoderHint.class);

        BufferedImage image = instance.decode(0, roi, scales,
                reductionFactor, diffScales, hints);
        assertEquals(64, image.getWidth());
        assertEquals(56, image.getHeight());
        assertEquals(0, reductionFactor.factor);
        assertEquals(1, diffScales[0], DELTA);
        assertEquals(1, diffScales[1], DELTA);
        assertTrue(hints.contains(DecoderHint.ALREADY_ORIENTED));
        if (SAVE_IMAGES) TestUtils.save(image);
    }

    @Test
    void decode2WithRGBA16BitWithOrientation90() throws Exception {
        instance.setSource(TestUtils.getFixture("rgba-16bit-orientation-90.jp2"));
        final Region roi                      = new Region(0, 0, 0, 0, true);
        final double[] scales                 = { 1, 1 };
        final ReductionFactor reductionFactor = new ReductionFactor();
        final double[] diffScales             = new double[2];
        final Set<DecoderHint> hints          = EnumSet.noneOf(DecoderHint.class);

        BufferedImage image = instance.decode(0, roi, scales,
                reductionFactor, diffScales, hints);
        assertEquals(64, image.getWidth());
        assertEquals(56, image.getHeight());
        assertEquals(0, reductionFactor.factor);
        assertEquals(1, diffScales[0], DELTA);
        assertEquals(1, diffScales[1], DELTA);
        assertTrue(hints.contains(DecoderHint.ALREADY_ORIENTED));
        if (SAVE_IMAGES) TestUtils.save(image);
    }

    @Test
    void decode2WithRGBA16BitWithOrientation180() throws Exception {
        instance.setSource(TestUtils.getFixture("rgba-16bit-orientation-180.jp2"));
        final Region roi                      = new Region(0, 0, 0, 0, true);
        final double[] scales                 = { 1, 1 };
        final ReductionFactor reductionFactor = new ReductionFactor();
        final double[] diffScales             = new double[2];
        final Set<DecoderHint> hints          = EnumSet.noneOf(DecoderHint.class);

        BufferedImage image = instance.decode(0, roi, scales,
                reductionFactor, diffScales, hints);
        assertEquals(64, image.getWidth());
        assertEquals(56, image.getHeight());
        assertEquals(0, reductionFactor.factor);
        assertEquals(1, diffScales[0], DELTA);
        assertEquals(1, diffScales[1], DELTA);
        assertTrue(hints.contains(DecoderHint.ALREADY_ORIENTED));
        if (SAVE_IMAGES) TestUtils.save(image);
    }

    @Test
    void decode2WithRGBA16BitWithOrientation270() throws Exception {
        instance.setSource(TestUtils.getFixture("rgba-16bit-orientation-270.jp2"));
        final Region roi                      = new Region(0, 0, 0, 0, true);
        final double[] scales                 = { 1, 1 };
        final ReductionFactor reductionFactor = new ReductionFactor();
        final double[] diffScales             = new double[2];
        final Set<DecoderHint> hints          = EnumSet.noneOf(DecoderHint.class);

        BufferedImage image = instance.decode(0, roi, scales,
                reductionFactor, diffScales, hints);
        assertEquals(64, image.getWidth());
        assertEquals(56, image.getHeight());
        assertEquals(0, reductionFactor.factor);
        assertEquals(1, diffScales[0], DELTA);
        assertEquals(1, diffScales[1], DELTA);
        assertTrue(hints.contains(DecoderHint.ALREADY_ORIENTED));
        if (SAVE_IMAGES) TestUtils.save(image);
    }

    /* readMetadata() */

    @Test
    void decodeMetadataWithNonexistentImage() {
        instance.setSource(TestUtils.getFixture("bogus"));
        assertThrows(NoSuchFileException.class,
                () -> instance.readMetadata(0));
    }

    @Test
    void decodeMetadataWithEmptyImage() {
        instance.setSource(TestUtils.getFixture("empty"));
        assertThrows(SourceFormatException.class, () ->
                instance.readMetadata(0));
    }

    @Test
    void decodeMetadataWithInvalidImage() {
        instance.setSource(TestUtils.getFixture("unknown"));
        assertThrows(SourceFormatException.class,
                () -> instance.readMetadata(0));
    }

    @Test
    void decodeMetadataWithIllegalImageIndex() {
        assertThrows(IndexOutOfBoundsException.class,
                () -> instance.readMetadata(9999));
    }

    @Test
    void decodeMetadataWithEXIF() throws Exception {
        instance.setSource(TestUtils.getFixture("exif.jp2"));
        Metadata metadata = instance.readMetadata(0);
        if (metadata.getEXIF().isPresent()) {
            Directory dir = metadata.getEXIF().get();
            assertEquals(5, dir.size());
        } else {
            fail("No EXIF metadata");
        }
    }

    @Test
    void decodeMetadataWithIPTC() throws Exception {
        instance.setSource(TestUtils.getFixture("iptc.jp2"));
        Metadata metadata = instance.readMetadata(0);
        if (!metadata.getIPTC().isEmpty()) {
            List<DataSet> dataSets = metadata.getIPTC();
            assertEquals(2, dataSets.size());
        } else {
            fail("No IPTC metadata");
        }
    }

    @Test
    void decodeMetadataWithXMP() throws Exception {
        instance.setSource(TestUtils.getFixture("xmp.jp2"));
        Metadata metadata = instance.readMetadata(0);
        if (metadata.getXMP().isPresent()) {
            String xmp = metadata.getXMP().get();
            assertTrue(xmp.startsWith("<rdf:RDF"));
            assertTrue(xmp.endsWith("</rdf:RDF>"));
        } else {
            fail("No XMP metadata");
        }
    }

    /* readSequence() */

    @Test
    void decodeSequence() {
        assertThrows(UnsupportedOperationException.class,
                () -> instance.decodeSequence());
    }

}
