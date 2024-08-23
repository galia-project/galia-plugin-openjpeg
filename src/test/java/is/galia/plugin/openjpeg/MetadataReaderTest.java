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

import is.galia.codec.SourceFormatException;
import is.galia.codec.tiff.Directory;
import is.galia.codec.iptc.IIMReader;
import is.galia.plugin.openjpeg.test.TestUtils;
import is.galia.stream.PathImageInputStream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.imageio.stream.ImageInputStream;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class MetadataReaderTest {

    private static final Path DEFAULT_FIXTURE =
            TestUtils.getFixture("rgb-8bit-orientation-0.jp2");

    private MetadataReader instance;

    @BeforeEach
    public void setUp() {
        instance = new MetadataReader();
    }

    /* getComponentSize() */

    @Test
    void getComponentSizeWithValidImage() throws Exception {
        try (ImageInputStream is = new PathImageInputStream(DEFAULT_FIXTURE)) {
            instance.setSource(is);
            assertEquals(8, instance.getComponentSize());
        }
    }

    @Test
    void getComponentSizeWithInvalidImage1() throws Exception {
        Path file = TestUtils.getFixture("unknown");
        try (ImageInputStream is = new PathImageInputStream(file)) {
            instance.setSource(is);
            assertThrows(SourceFormatException.class, () -> instance.getComponentSize());
        }
    }

    @Test
    void getComponentSizeWithInvalidImage2() throws Exception {
        Path file = TestUtils.getFixture("unknown");
        try (ImageInputStream is = new PathImageInputStream(file)) {
            instance.setSource(is);
            assertThrows(SourceFormatException.class, () -> instance.getComponentSize());
        }
    }

    @Test
    void getComponentSizeWithEmptyImage() throws Exception {
        Path file = TestUtils.getFixture("empty");
        try (ImageInputStream is = new PathImageInputStream(file)) {
            instance.setSource(is);
            assertThrows(SourceFormatException.class, () -> instance.getComponentSize());
        }
    }

    @Test
    void getComponentSizeWithSourceNotSet() {
        assertThrows(IllegalStateException.class,
                () -> instance.getComponentSize());
    }

    /* getEXIF() */

    @Test
    void getEXIFWithValidImageContainingEXIF() throws Exception {
        Path file = TestUtils.getFixture("exif.jp2");
        try (ImageInputStream is = new PathImageInputStream(file)) {
            instance.setSource(is);
            Directory dir = instance.getEXIF();
            assertEquals(5, dir.size());
        }
    }

    @Test
    void getEXIFWithValidImageNotContainingEXIF() throws Exception {
        try (ImageInputStream is = new PathImageInputStream(DEFAULT_FIXTURE)) {
            instance.setSource(is);
            assertNull(instance.getEXIF());
        }
    }

    @Test
    void getEXIFWithInvalidImage1() throws Exception {
        Path file = TestUtils.getFixture("unknown");
        try (ImageInputStream is = new PathImageInputStream(file)) {
            instance.setSource(is);
            assertThrows(SourceFormatException.class, () -> instance.getEXIF());
        }
    }

    @Test
    void getEXIFWithInvalidImage2() throws Exception {
        Path file = TestUtils.getFixture("unknown");
        try (ImageInputStream is = new PathImageInputStream(file)) {
            instance.setSource(is);
            assertThrows(SourceFormatException.class, () -> instance.getEXIF());
        }
    }

    @Test
    void getEXIFWithEmptyImage() throws Exception {
        Path file = TestUtils.getFixture("empty");
        try (ImageInputStream is = new PathImageInputStream(file)) {
            instance.setSource(is);
            assertThrows(SourceFormatException.class, () -> instance.getEXIF());
        }
    }

    @Test
    void getEXIFWithSourceNotSet() {
        assertThrows(IllegalStateException.class, () -> instance.getEXIF());
    }

    /* getHeight() */

    @Test
    void getHeightWithValidImage() throws Exception {
        try (ImageInputStream is = new PathImageInputStream(DEFAULT_FIXTURE)) {
            instance.setSource(is);
            assertEquals(200, instance.getHeight());
        }
    }

    @Test
    void getHeightWithInvalidImage1() throws Exception {
        Path file = TestUtils.getFixture("unknown");
        try (ImageInputStream is = new PathImageInputStream(file)) {
            instance.setSource(is);
            assertThrows(SourceFormatException.class, () -> instance.getHeight());
        }
    }

    @Test
    void getHeightWithInvalidImage2() throws Exception {
        Path file = TestUtils.getFixture("unknown");
        try (ImageInputStream is = new PathImageInputStream(file)) {
            instance.setSource(is);
            assertThrows(SourceFormatException.class, () -> instance.getHeight());
        }
    }

    @Test
    void getHeightWithEmptyImage() throws Exception {
        Path file = TestUtils.getFixture("empty");
        try (ImageInputStream is = new PathImageInputStream(file)) {
            instance.setSource(is);
            assertThrows(SourceFormatException.class, () -> instance.getHeight());
        }
    }

    @Test
    void getHeightWithSourceNotSet() {
        assertThrows(IllegalStateException.class, () -> instance.getHeight());
    }

    /* getIPTC() */

    @Test
    void getIPTCWithValidImageContainingIPTC() throws Exception {
        Path file = TestUtils.getFixture("iptc.jp2");
        try (ImageInputStream is = new PathImageInputStream(file)) {
            instance.setSource(is);
            IIMReader reader = new IIMReader();
            reader.setSource(instance.getIPTC());
            assertEquals(2, reader.read().size());
        }
    }

    @Test
    void getIPTCWithValidImageNotContainingIPTC() throws Exception {
        try (ImageInputStream is = new PathImageInputStream(DEFAULT_FIXTURE)) {
            instance.setSource(is);
            assertNull(instance.getIPTC());
        }
    }

    @Test
    void getIPTCWithInvalidImage1() throws Exception {
        Path file = TestUtils.getFixture("unknown");
        try (ImageInputStream is = new PathImageInputStream(file)) {
            instance.setSource(is);
            assertThrows(SourceFormatException.class, () -> instance.getIPTC());
        }
    }

    @Test
    void getIPTCWithInvalidImage2() throws Exception {
        Path file = TestUtils.getFixture("unknown");
        try (ImageInputStream is = new PathImageInputStream(file)) {
            instance.setSource(is);
            assertThrows(SourceFormatException.class, () -> instance.getIPTC());
        }
    }

    @Test
    void getIPTCWithEmptyImage() throws Exception {
        Path file = TestUtils.getFixture("empty");
        try (ImageInputStream is = new PathImageInputStream(file)) {
            instance.setSource(is);
            assertThrows(SourceFormatException.class, () -> instance.getIPTC());
        }
    }

    @Test
    void getIPTCWithSourceNotSet() {
        assertThrows(IllegalStateException.class, () -> instance.getIPTC());
    }

    /* getNumComponents() */

    @Test
    void getNumComponentsWithValidImage() throws Exception {
        try (ImageInputStream is = new PathImageInputStream(DEFAULT_FIXTURE)) {
            instance.setSource(is);
            assertEquals(3, instance.getNumComponents());
        }
    }

    @Test
    void getNumComponentsWithInvalidImage1() throws Exception {
        Path file = TestUtils.getFixture("unknown");
        try (ImageInputStream is = new PathImageInputStream(file)) {
            instance.setSource(is);
            assertThrows(SourceFormatException.class, () -> instance.getNumComponents());
        }
    }

    @Test
    void getNumComponentsWithInvalidImage2() throws Exception {
        Path file = TestUtils.getFixture("unknown");
        try (ImageInputStream is = new PathImageInputStream(file)) {
            instance.setSource(is);
            assertThrows(SourceFormatException.class, () -> instance.getNumComponents());
        }
    }

    @Test
    void getNumComponentsWithEmptyImage() throws Exception {
        Path file = TestUtils.getFixture("empty");
        try (ImageInputStream is = new PathImageInputStream(file)) {
            instance.setSource(is);
            assertThrows(SourceFormatException.class, () -> instance.getNumComponents());
        }
    }

    @Test
    void getNumComponentsWithSourceNotSet() {
        assertThrows(IllegalStateException.class,
                () -> instance.getNumComponents());
    }

    /* getNumDecompositionLevels() */

    @Test
    void getNumDecompositionLevelsWithValidImage() throws Exception {
        try (ImageInputStream is = new PathImageInputStream(DEFAULT_FIXTURE)) {
            instance.setSource(is);
            assertEquals(5, instance.getNumDecompositionLevels());
        }
    }

    @Test
    void getNumDecompositionLevelsWithInvalidImage1() throws Exception {
        Path file = TestUtils.getFixture("unknown");
        try (ImageInputStream is = new PathImageInputStream(file)) {
            instance.setSource(is);
            assertThrows(SourceFormatException.class,
                    () -> instance.getNumDecompositionLevels());
        }
    }

    @Test
    void getNumDecompositionLevelsWithInvalidImage2() throws Exception {
        Path file = TestUtils.getFixture("unknown");
        try (ImageInputStream is = new PathImageInputStream(file)) {
            instance.setSource(is);
            assertThrows(SourceFormatException.class,
                    () -> instance.getNumDecompositionLevels());
        }
    }

    @Test
    void getNumDecompositionLevelsWithEmptyImage() throws Exception {
        Path file = TestUtils.getFixture("empty");
        try (ImageInputStream is = new PathImageInputStream(file)) {
            instance.setSource(is);
            assertThrows(SourceFormatException.class,
                    () -> instance.getNumDecompositionLevels());
        }
    }

    @Test
    void getNumDecompositionLevelsWithSourceNotSet() {
        assertThrows(IllegalStateException.class,
                () -> instance.getNumDecompositionLevels());
    }

    /* getNumLayers() */

    @Test
    void getNumLayersWithValidImage() throws Exception {
        try (ImageInputStream is = new PathImageInputStream(DEFAULT_FIXTURE)) {
            instance.setSource(is);
            assertEquals(1, instance.getNumLayers());
        }
    }

    @Test
    void getNumLayersWithInvalidImage1() throws Exception {
        Path file = TestUtils.getFixture("unknown");
        try (ImageInputStream is = new PathImageInputStream(file)) {
            instance.setSource(is);
            assertThrows(SourceFormatException.class,
                    () -> instance.getNumLayers());
        }
    }

    @Test
    void getNumLayersWithInvalidImage2() throws Exception {
        Path file = TestUtils.getFixture("unknown");
        try (ImageInputStream is = new PathImageInputStream(file)) {
            instance.setSource(is);
            assertThrows(SourceFormatException.class,
                    () -> instance.getNumLayers());
        }
    }

    @Test
    void getNumLayersWithEmptyImage() throws Exception {
        Path file = TestUtils.getFixture("empty");
        try (ImageInputStream is = new PathImageInputStream(file)) {
            instance.setSource(is);
            assertThrows(SourceFormatException.class,
                    () -> instance.getNumLayers());
        }
    }

    @Test
    void getNumLayersWithSourceNotSet() {
        assertThrows(IllegalStateException.class,
                () -> instance.getNumLayers());
    }

    /* getTileHeight() */

    @Test
    void getTileHeightWithValidImage() throws Exception {
        try (ImageInputStream is = new PathImageInputStream(DEFAULT_FIXTURE)) {
            instance.setSource(is);
            assertEquals(200, instance.getTileHeight());
        }
    }

    @Test
    void getTileHeightWithInvalidImage1() throws Exception {
        Path file = TestUtils.getFixture("unknown");
        try (ImageInputStream is = new PathImageInputStream(file)) {
            instance.setSource(is);
            assertThrows(SourceFormatException.class, () -> instance.getTileHeight());
        }
    }

    @Test
    void getTileHeightWithInvalidImage2() throws Exception {
        Path file = TestUtils.getFixture("unknown");
        try (ImageInputStream is = new PathImageInputStream(file)) {
            instance.setSource(is);
            assertThrows(SourceFormatException.class, () -> instance.getTileHeight());
        }
    }

    @Test
    void getTileHeightWithEmptyImage() throws Exception {
        Path file = TestUtils.getFixture("empty");
        try (ImageInputStream is = new PathImageInputStream(file)) {
            instance.setSource(is);
            assertThrows(SourceFormatException.class, () -> instance.getTileHeight());
        }
    }

    @Test
    void getTileHeightWithSourceNotSet() {
        assertThrows(IllegalStateException.class,
                () -> instance.getTileHeight());
    }

    /* getTileWidth() */

    @Test
    void getTileWidthWithValidImage() throws Exception {
        try (ImageInputStream is = new PathImageInputStream(DEFAULT_FIXTURE)) {
            instance.setSource(is);
            assertEquals(150, instance.getTileWidth());
        }
    }

    @Test
    void getTileWidthWithInvalidImage1() throws Exception {
        Path file = TestUtils.getFixture("unknown");
        try (ImageInputStream is = new PathImageInputStream(file)) {
            instance.setSource(is);
            assertThrows(SourceFormatException.class, () -> instance.getTileWidth());
        }
    }

    @Test
    void getTileWidthWithInvalidImage2() throws Exception {
        Path file = TestUtils.getFixture("unknown");
        try (ImageInputStream is = new PathImageInputStream(file)) {
            instance.setSource(is);
            assertThrows(SourceFormatException.class, () -> instance.getTileWidth());
        }
    }

    @Test
    void getTileWidthWithEmptyImage() throws Exception {
        Path file = TestUtils.getFixture("empty");
        try (ImageInputStream is = new PathImageInputStream(file)) {
            instance.setSource(is);
            assertThrows(SourceFormatException.class, () -> instance.getTileWidth());
        }
    }

    @Test
    void getTileWidthWithSourceNotSet() {
        assertThrows(IllegalStateException.class, () -> instance.getTileWidth());
    }

    /* getWidth() */

    @Test
    void getWidthWithValidImage() throws Exception {
        try (ImageInputStream is = new PathImageInputStream(DEFAULT_FIXTURE)) {
            instance.setSource(is);
            assertEquals(150, instance.getWidth());
        }
    }

    @Test
    void getWidthWithInvalidImage1() throws Exception {
        Path file = TestUtils.getFixture("unknown");
        try (ImageInputStream is = new PathImageInputStream(file)) {
            instance.setSource(is);
            assertThrows(SourceFormatException.class, () -> instance.getWidth());
        }
    }

    @Test
    void getWidthWithInvalidImage2() throws Exception {
        Path file = TestUtils.getFixture("unknown");
        try (ImageInputStream is = new PathImageInputStream(file)) {
            instance.setSource(is);
            assertThrows(SourceFormatException.class, () -> instance.getWidth());
        }
    }

    @Test
    void getWidthWithEmptyImage() throws Exception {
        Path file = TestUtils.getFixture("empty");
        try (ImageInputStream is = new PathImageInputStream(file)) {
            instance.setSource(is);
            assertThrows(SourceFormatException.class, () -> instance.getWidth());
        }
    }

    @Test
    void getWidthWithSourceNotSet() {
        assertThrows(IllegalStateException.class, () -> instance.getWidth());
    }

    /* getXMP() */

    @Test
    void getXMPWithSourceNotSet() {
        assertThrows(IllegalStateException.class, () -> instance.getXMP());
    }

    @Test
    void getXMPWithEmptyImage() throws Exception {
        Path file = TestUtils.getFixture("empty");
        try (ImageInputStream is = new PathImageInputStream(file)) {
            instance.setSource(is);
            assertThrows(SourceFormatException.class, () -> instance.getXMP());
        }
    }

    @Test
    void getXMPWithInvalidImage1() throws Exception {
        Path file = TestUtils.getFixture("unknown");
        try (ImageInputStream is = new PathImageInputStream(file)) {
            instance.setSource(is);
            assertThrows(SourceFormatException.class, () -> instance.getXMP());
        }
    }

    @Test
    void getXMPWithInvalidImage2() throws Exception {
        Path file = TestUtils.getFixture("unknown");
        try (ImageInputStream is = new PathImageInputStream(file)) {
            instance.setSource(is);
            assertThrows(SourceFormatException.class, () -> instance.getXMP());
        }
    }

    @Test
    void getXMPWithValidImageNotContainingXMP() throws Exception {
        try (ImageInputStream is = new PathImageInputStream(DEFAULT_FIXTURE)) {
            instance.setSource(is);
            assertNull(instance.getXMP());
        }
    }

    @Test
    void getXMPWithValidImageContainingXMP1() throws Exception {
        Path file = TestUtils.getFixture("xmp.jp2");
        try (ImageInputStream is = new PathImageInputStream(file)) {
            instance.setSource(is);

            String xmpStr = instance.getXMP();
            assertNotNull(xmpStr);
            assertTrue(xmpStr.startsWith("<rdf:RDF "));
            assertTrue(xmpStr.endsWith("</rdf:RDF>"));
        }
    }

    @Test
    void getXMPWithValidImageContainingXMP2() throws Exception {
        Path file = TestUtils.getFixture("rgb-8bit-orientation-90.jp2");
        try (ImageInputStream is = new PathImageInputStream(file)) {
            instance.setSource(is);

            String xmpStr = instance.getXMP();
            assertNotNull(xmpStr);
            assertTrue(xmpStr.startsWith("<rdf:RDF "));
            assertTrue(xmpStr.endsWith("</rdf:RDF>"));
        }
    }

}
