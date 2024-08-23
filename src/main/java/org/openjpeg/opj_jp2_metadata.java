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
 * struct opj_jp2_metadata {
 *     OPJ_INT32 not_used;
 * }
 * }
 */
public class opj_jp2_metadata {

    opj_jp2_metadata() {
        // Should not be called directly
    }

    private static final GroupLayout $LAYOUT = MemoryLayout.structLayout(
        openjpeg_h.C_INT.withName("not_used")
    ).withName("opj_jp2_metadata");

    /**
     * The layout of this struct
     */
    public static final GroupLayout layout() {
        return $LAYOUT;
    }

    private static final OfInt not_used$LAYOUT = (OfInt)$LAYOUT.select(groupElement("not_used"));

    /**
     * Layout for field:
     * {@snippet lang=c :
     * OPJ_INT32 not_used
     * }
     */
    public static final OfInt not_used$layout() {
        return not_used$LAYOUT;
    }

    private static final long not_used$OFFSET = 0;

    /**
     * Offset for field:
     * {@snippet lang=c :
     * OPJ_INT32 not_used
     * }
     */
    public static final long not_used$offset() {
        return not_used$OFFSET;
    }

    /**
     * Getter for field:
     * {@snippet lang=c :
     * OPJ_INT32 not_used
     * }
     */
    public static int not_used(MemorySegment struct) {
        return struct.get(not_used$LAYOUT, not_used$OFFSET);
    }

    /**
     * Setter for field:
     * {@snippet lang=c :
     * OPJ_INT32 not_used
     * }
     */
    public static void not_used(MemorySegment struct, int fieldValue) {
        struct.set(not_used$LAYOUT, not_used$OFFSET, fieldValue);
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

