// Generated by jextract

package org.openjpeg;

import java.lang.invoke.*;
import java.lang.foreign.*;
import java.nio.ByteOrder;
import java.util.*;
import java.util.function.*;
import java.util.stream.*;

import static java.lang.foreign.ValueLayout.*;
import static java.lang.foreign.MemoryLayout.PathElement.*;

/**
 * {@snippet lang=c :
 * struct opj_codestream_index {
 *     OPJ_OFF_T main_head_start;
 *     OPJ_OFF_T main_head_end;
 *     OPJ_UINT64 codestream_size;
 *     OPJ_UINT32 marknum;
 *     opj_marker_info_t *marker;
 *     OPJ_UINT32 maxmarknum;
 *     OPJ_UINT32 nb_of_tiles;
 *     opj_tile_index_t *tile_index;
 * }
 * }
 */
public class opj_codestream_index {

    opj_codestream_index() {
        // Should not be called directly
    }

    private static final GroupLayout $LAYOUT = MemoryLayout.structLayout(
        openjpeg_h.C_LONG_LONG.withName("main_head_start"),
        openjpeg_h.C_LONG_LONG.withName("main_head_end"),
        openjpeg_h.C_LONG_LONG.withName("codestream_size"),
        openjpeg_h.C_INT.withName("marknum"),
        MemoryLayout.paddingLayout(4),
        openjpeg_h.C_POINTER.withName("marker"),
        openjpeg_h.C_INT.withName("maxmarknum"),
        openjpeg_h.C_INT.withName("nb_of_tiles"),
        openjpeg_h.C_POINTER.withName("tile_index")
    ).withName("opj_codestream_index");

    /**
     * The layout of this struct
     */
    public static final GroupLayout layout() {
        return $LAYOUT;
    }

    private static final OfLong main_head_start$LAYOUT = (OfLong)$LAYOUT.select(groupElement("main_head_start"));

    /**
     * Layout for field:
     * {@snippet lang=c :
     * OPJ_OFF_T main_head_start
     * }
     */
    public static final OfLong main_head_start$layout() {
        return main_head_start$LAYOUT;
    }

    private static final long main_head_start$OFFSET = 0;

    /**
     * Offset for field:
     * {@snippet lang=c :
     * OPJ_OFF_T main_head_start
     * }
     */
    public static final long main_head_start$offset() {
        return main_head_start$OFFSET;
    }

    /**
     * Getter for field:
     * {@snippet lang=c :
     * OPJ_OFF_T main_head_start
     * }
     */
    public static long main_head_start(MemorySegment struct) {
        return struct.get(main_head_start$LAYOUT, main_head_start$OFFSET);
    }

    /**
     * Setter for field:
     * {@snippet lang=c :
     * OPJ_OFF_T main_head_start
     * }
     */
    public static void main_head_start(MemorySegment struct, long fieldValue) {
        struct.set(main_head_start$LAYOUT, main_head_start$OFFSET, fieldValue);
    }

    private static final OfLong main_head_end$LAYOUT = (OfLong)$LAYOUT.select(groupElement("main_head_end"));

    /**
     * Layout for field:
     * {@snippet lang=c :
     * OPJ_OFF_T main_head_end
     * }
     */
    public static final OfLong main_head_end$layout() {
        return main_head_end$LAYOUT;
    }

    private static final long main_head_end$OFFSET = 8;

    /**
     * Offset for field:
     * {@snippet lang=c :
     * OPJ_OFF_T main_head_end
     * }
     */
    public static final long main_head_end$offset() {
        return main_head_end$OFFSET;
    }

    /**
     * Getter for field:
     * {@snippet lang=c :
     * OPJ_OFF_T main_head_end
     * }
     */
    public static long main_head_end(MemorySegment struct) {
        return struct.get(main_head_end$LAYOUT, main_head_end$OFFSET);
    }

    /**
     * Setter for field:
     * {@snippet lang=c :
     * OPJ_OFF_T main_head_end
     * }
     */
    public static void main_head_end(MemorySegment struct, long fieldValue) {
        struct.set(main_head_end$LAYOUT, main_head_end$OFFSET, fieldValue);
    }

    private static final OfLong codestream_size$LAYOUT = (OfLong)$LAYOUT.select(groupElement("codestream_size"));

    /**
     * Layout for field:
     * {@snippet lang=c :
     * OPJ_UINT64 codestream_size
     * }
     */
    public static final OfLong codestream_size$layout() {
        return codestream_size$LAYOUT;
    }

    private static final long codestream_size$OFFSET = 16;

    /**
     * Offset for field:
     * {@snippet lang=c :
     * OPJ_UINT64 codestream_size
     * }
     */
    public static final long codestream_size$offset() {
        return codestream_size$OFFSET;
    }

    /**
     * Getter for field:
     * {@snippet lang=c :
     * OPJ_UINT64 codestream_size
     * }
     */
    public static long codestream_size(MemorySegment struct) {
        return struct.get(codestream_size$LAYOUT, codestream_size$OFFSET);
    }

    /**
     * Setter for field:
     * {@snippet lang=c :
     * OPJ_UINT64 codestream_size
     * }
     */
    public static void codestream_size(MemorySegment struct, long fieldValue) {
        struct.set(codestream_size$LAYOUT, codestream_size$OFFSET, fieldValue);
    }

    private static final OfInt marknum$LAYOUT = (OfInt)$LAYOUT.select(groupElement("marknum"));

    /**
     * Layout for field:
     * {@snippet lang=c :
     * OPJ_UINT32 marknum
     * }
     */
    public static final OfInt marknum$layout() {
        return marknum$LAYOUT;
    }

    private static final long marknum$OFFSET = 24;

    /**
     * Offset for field:
     * {@snippet lang=c :
     * OPJ_UINT32 marknum
     * }
     */
    public static final long marknum$offset() {
        return marknum$OFFSET;
    }

    /**
     * Getter for field:
     * {@snippet lang=c :
     * OPJ_UINT32 marknum
     * }
     */
    public static int marknum(MemorySegment struct) {
        return struct.get(marknum$LAYOUT, marknum$OFFSET);
    }

    /**
     * Setter for field:
     * {@snippet lang=c :
     * OPJ_UINT32 marknum
     * }
     */
    public static void marknum(MemorySegment struct, int fieldValue) {
        struct.set(marknum$LAYOUT, marknum$OFFSET, fieldValue);
    }

    private static final AddressLayout marker$LAYOUT = (AddressLayout)$LAYOUT.select(groupElement("marker"));

    /**
     * Layout for field:
     * {@snippet lang=c :
     * opj_marker_info_t *marker
     * }
     */
    public static final AddressLayout marker$layout() {
        return marker$LAYOUT;
    }

    private static final long marker$OFFSET = 32;

    /**
     * Offset for field:
     * {@snippet lang=c :
     * opj_marker_info_t *marker
     * }
     */
    public static final long marker$offset() {
        return marker$OFFSET;
    }

    /**
     * Getter for field:
     * {@snippet lang=c :
     * opj_marker_info_t *marker
     * }
     */
    public static MemorySegment marker(MemorySegment struct) {
        return struct.get(marker$LAYOUT, marker$OFFSET);
    }

    /**
     * Setter for field:
     * {@snippet lang=c :
     * opj_marker_info_t *marker
     * }
     */
    public static void marker(MemorySegment struct, MemorySegment fieldValue) {
        struct.set(marker$LAYOUT, marker$OFFSET, fieldValue);
    }

    private static final OfInt maxmarknum$LAYOUT = (OfInt)$LAYOUT.select(groupElement("maxmarknum"));

    /**
     * Layout for field:
     * {@snippet lang=c :
     * OPJ_UINT32 maxmarknum
     * }
     */
    public static final OfInt maxmarknum$layout() {
        return maxmarknum$LAYOUT;
    }

    private static final long maxmarknum$OFFSET = 40;

    /**
     * Offset for field:
     * {@snippet lang=c :
     * OPJ_UINT32 maxmarknum
     * }
     */
    public static final long maxmarknum$offset() {
        return maxmarknum$OFFSET;
    }

    /**
     * Getter for field:
     * {@snippet lang=c :
     * OPJ_UINT32 maxmarknum
     * }
     */
    public static int maxmarknum(MemorySegment struct) {
        return struct.get(maxmarknum$LAYOUT, maxmarknum$OFFSET);
    }

    /**
     * Setter for field:
     * {@snippet lang=c :
     * OPJ_UINT32 maxmarknum
     * }
     */
    public static void maxmarknum(MemorySegment struct, int fieldValue) {
        struct.set(maxmarknum$LAYOUT, maxmarknum$OFFSET, fieldValue);
    }

    private static final OfInt nb_of_tiles$LAYOUT = (OfInt)$LAYOUT.select(groupElement("nb_of_tiles"));

    /**
     * Layout for field:
     * {@snippet lang=c :
     * OPJ_UINT32 nb_of_tiles
     * }
     */
    public static final OfInt nb_of_tiles$layout() {
        return nb_of_tiles$LAYOUT;
    }

    private static final long nb_of_tiles$OFFSET = 44;

    /**
     * Offset for field:
     * {@snippet lang=c :
     * OPJ_UINT32 nb_of_tiles
     * }
     */
    public static final long nb_of_tiles$offset() {
        return nb_of_tiles$OFFSET;
    }

    /**
     * Getter for field:
     * {@snippet lang=c :
     * OPJ_UINT32 nb_of_tiles
     * }
     */
    public static int nb_of_tiles(MemorySegment struct) {
        return struct.get(nb_of_tiles$LAYOUT, nb_of_tiles$OFFSET);
    }

    /**
     * Setter for field:
     * {@snippet lang=c :
     * OPJ_UINT32 nb_of_tiles
     * }
     */
    public static void nb_of_tiles(MemorySegment struct, int fieldValue) {
        struct.set(nb_of_tiles$LAYOUT, nb_of_tiles$OFFSET, fieldValue);
    }

    private static final AddressLayout tile_index$LAYOUT = (AddressLayout)$LAYOUT.select(groupElement("tile_index"));

    /**
     * Layout for field:
     * {@snippet lang=c :
     * opj_tile_index_t *tile_index
     * }
     */
    public static final AddressLayout tile_index$layout() {
        return tile_index$LAYOUT;
    }

    private static final long tile_index$OFFSET = 48;

    /**
     * Offset for field:
     * {@snippet lang=c :
     * opj_tile_index_t *tile_index
     * }
     */
    public static final long tile_index$offset() {
        return tile_index$OFFSET;
    }

    /**
     * Getter for field:
     * {@snippet lang=c :
     * opj_tile_index_t *tile_index
     * }
     */
    public static MemorySegment tile_index(MemorySegment struct) {
        return struct.get(tile_index$LAYOUT, tile_index$OFFSET);
    }

    /**
     * Setter for field:
     * {@snippet lang=c :
     * opj_tile_index_t *tile_index
     * }
     */
    public static void tile_index(MemorySegment struct, MemorySegment fieldValue) {
        struct.set(tile_index$LAYOUT, tile_index$OFFSET, fieldValue);
    }

    /**
     * Obtains a slice of {@code arrayParam} which selects the array element at {@code index}.
     * The returned segment has address {@code arrayParam.address() + index * layout().byteSize()}
     */
    public static MemorySegment asSlice(MemorySegment array, long index) {
        return array.asSlice(layout().byteSize() * index);
    }

    /**
     * The size (in bytes) of this struct
     */
    public static long sizeof() { return layout().byteSize(); }

    /**
     * Allocate a segment of size {@code layout().byteSize()} using {@code allocator}
     */
    public static MemorySegment allocate(SegmentAllocator allocator) {
        return allocator.allocate(layout());
    }

    /**
     * Allocate an array of size {@code elementCount} using {@code allocator}.
     * The returned segment has size {@code elementCount * layout().byteSize()}.
     */
    public static MemorySegment allocateArray(long elementCount, SegmentAllocator allocator) {
        return allocator.allocate(MemoryLayout.sequenceLayout(elementCount, layout()));
    }

    /**
     * Reinterprets {@code addr} using target {@code arena} and {@code cleanupAction} (if any).
     * The returned segment has size {@code layout().byteSize()}
     */
    public static MemorySegment reinterpret(MemorySegment addr, Arena arena, Consumer<MemorySegment> cleanup) {
        return reinterpret(addr, 1, arena, cleanup);
    }

    /**
     * Reinterprets {@code addr} using target {@code arena} and {@code cleanupAction} (if any).
     * The returned segment has size {@code elementCount * layout().byteSize()}
     */
    public static MemorySegment reinterpret(MemorySegment addr, long elementCount, Arena arena, Consumer<MemorySegment> cleanup) {
        return addr.reinterpret(layout().byteSize() * elementCount, arena, cleanup);
    }
}

