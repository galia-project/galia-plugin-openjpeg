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

import is.galia.image.Format;
import is.galia.image.MediaType;

import java.util.List;

final class Formats {

    static final byte[] JP2_CODESTREAM_SIGNATURE = new byte[] {
            (byte) 0xff, 0x4f, (byte) 0xff, 0x51 };
    static final byte[] JP2_FAMILY_SIGNATURE = new byte[] {
            0x00, 0x00, 0x00, 0x0c, 0x6a, 0x50, 0x20, 0x20, 0x0d, 0x0a,
            (byte) 0x87, 0x0a };
    static final byte[] JP2_BRAND_SIGNATURE = new byte[] { // offset 20
            'j', 'p', '2', ' ' };
    static final byte[] JPH_BRAND_SIGNATURE = new byte[] { // offset 20
            'j', 'p', 'h', ' ' };
    static final byte[] JPM_BRAND_SIGNATURE = new byte[] { // offset 20
            'j', 'p', 'm', ' ' };
    static final byte[] JPX_BRAND_SIGNATURE = new byte[] { // offset 20
            'j', 'p', 'x', ' ' };
    static final byte[] JPXB_BRAND_SIGNATURE = new byte[] { // offset 20
            'j', 'p', 'x', 'b' };

    static final Format J2C = new Format(
            "j2c",                                       // key
            "JPEG2000 Codestream",                       // name
            List.of(new MediaType("image", "jp2"),
                    new MediaType("image", "jpeg2000")), // media types
            List.of("j2c", "jpt", "jpc"),                // extensions
            true,                                        // isRaster
            false,                                       // isVideo
            true);                                       // supportsTransparency
    static final Format JP2 = new Format(
            "jp2",                                       // key
            "JPEG2000",                                  // name
            List.of(new MediaType("image", "jp2"),
                    new MediaType("image", "jpeg2000")), // media types
            List.of("jp2", "j2k", "jpg2"),               // extensions
            true,                                        // isRaster
            false,                                       // isVideo
            true);                                       // supportsTransparency
    static final Format JPH = new Format(
            "jph",                                  // key
            "HTJ2K",                                // name
            List.of(new MediaType("image", "jph")), // media types
            List.of("jph"),                         // extensions
            true,                                   // isRaster
            false,                                  // isVideo
            true);                                  // supportsTransparency
    static final Format JPX = new Format(
            "jpx",                                  // key
            "JPX",                                  // name
            List.of(new MediaType("image", "jpx")), // media types
            List.of("jpf", "jpx"),                  // extensions
            true,                                   // isRaster
            false,                                  // isVideo
            true);                                  // supportsTransparency

    private Formats() {}

}
