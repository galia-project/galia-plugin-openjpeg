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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.stream.ImageInputStream;
import java.io.IOException;
import java.lang.foreign.FunctionDescriptor;
import java.lang.foreign.MemorySegment;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;

import static org.openjpeg.openjpeg_h.C_POINTER;
import static org.openjpeg.openjpeg_h.OPJ_BOOL;
import static org.openjpeg.openjpeg_h.OPJ_FALSE;
import static org.openjpeg.openjpeg_h.OPJ_OFF_T;
import static org.openjpeg.openjpeg_h.OPJ_SIZE_T;
import static org.openjpeg.openjpeg_h.OPJ_TRUE;

final class StreamFunctions {

    private static final Logger LOGGER =
            LoggerFactory.getLogger(StreamFunctions.class);

    static MethodHandle READ_FUNCTION, SEEK_FUNCTION, SKIP_FUNCTION,
            FREE_USER_DATA_FUNCTION;

    static final FunctionDescriptor READ_FUNCTION_DESCRIPTOR =
            FunctionDescriptor.of(OPJ_SIZE_T, C_POINTER, OPJ_SIZE_T, C_POINTER);
    static final FunctionDescriptor SEEK_FUNCTION_DESCRIPTOR =
            FunctionDescriptor.of(OPJ_BOOL, OPJ_OFF_T, C_POINTER);
    static final FunctionDescriptor SKIP_FUNCTION_DESCRIPTOR =
            FunctionDescriptor.of(OPJ_OFF_T, OPJ_OFF_T, C_POINTER);
    static final FunctionDescriptor FREE_USER_DATA_FUNCTION_DESCRIPTOR =
            FunctionDescriptor.ofVoid(C_POINTER);

    static void initializeClass() {
        try {
            READ_FUNCTION = MethodHandles.lookup().findStatic(
                    StreamFunctions.class, "read",
                    READ_FUNCTION_DESCRIPTOR.toMethodType());
            SEEK_FUNCTION = MethodHandles.lookup().findStatic(
                    StreamFunctions.class, "seek",
                    SEEK_FUNCTION_DESCRIPTOR.toMethodType());
            SKIP_FUNCTION = MethodHandles.lookup().findStatic(
                    StreamFunctions.class, "skip",
                    SKIP_FUNCTION_DESCRIPTOR.toMethodType());
            FREE_USER_DATA_FUNCTION = MethodHandles.lookup().findStatic(
                    StreamFunctions.class, "freeUserData",
                    FREE_USER_DATA_FUNCTION_DESCRIPTOR.toMethodType());
        } catch (NoSuchMethodException | IllegalAccessException e) {
            LOGGER.error(e.getMessage());
        }
    }

    static long read(MemorySegment buffer, long size, MemorySegment userData) {
        ImageInputStream is = fetchInputStream(userData);
        try {
            size = Math.min(size, is.length() - is.getStreamPosition());
            if (size < 1) {
                return -1;
            }
            byte[] bytes = new byte[(int) size];
            int read = is.read(bytes, 0, (int) size);
            if (read > 0) {
                buffer.reinterpret(size).asByteBuffer().put(bytes);
                //for (int i = 0; i < read; i++) {
                //    buffer.set(ValueLayout.JAVA_BYTE, i, bytes[i]);
                //}
            }
            return read;
        } catch (IOException e) {
            LOGGER.error("read(): {}", e.getMessage());
            return -1;
        }
    }

    static int seek(long pos, MemorySegment userData) {
        ImageInputStream inputStream = fetchInputStream(userData);
        try {
            final long length = inputStream.length();
            if (length < 0) {
                throw new IOException("Seeking requires an " +
                        ImageInputStream.class.getSimpleName() +
                        " implementation whose length() method returns a " +
                        "positive value.");
            } else if (pos < 0 || pos > length) {
                return OPJ_FALSE();
            }
            inputStream.seek(pos);
            return OPJ_TRUE();
        } catch (IOException e) {
            LOGGER.error("seek(): {}", e.getMessage());
            return 0;
        }
    }

    static long skip(long numBytes, MemorySegment userData) {
        ImageInputStream is = fetchInputStream(userData);
        try {
            return is.skipBytes(numBytes);
        } catch (IOException e) {
            LOGGER.error("skip(): {}", e.getMessage());
            return -1;
        }
    }

    static void freeUserData(MemorySegment userData) {
    }

    private static ImageInputStream fetchInputStream(MemorySegment userData) {
        final String instanceID = userData.getString(0);
        OpenJPEGDecoder decoder = OpenJPEGDecoder.LIVE_INSTANCES.get(instanceID);
        return decoder.getInputStream();
    }

    private StreamFunctions() {}

}
