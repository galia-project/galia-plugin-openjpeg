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
 * struct opj_packet_info {
 *     OPJ_OFF_T start_pos;
 *     OPJ_OFF_T end_ph_pos;
 *     OPJ_OFF_T end_pos;
 *     double disto;
 * }
 * }
 */
public class opj_packet_info {

    opj_packet_info() {
        // Should not be called directly
    }

    private static final GroupLayout $LAYOUT = MemoryLayout.structLayout(
        openjpeg_h.C_LONG_LONG.withName("start_pos"),
        openjpeg_h.C_LONG_LONG.withName("end_ph_pos"),
        openjpeg_h.C_LONG_LONG.withName("end_pos"),
        openjpeg_h.C_DOUBLE.withName("disto")
    ).withName("opj_packet_info");

    /**
     * The layout of this struct
     */
    public static final GroupLayout layout() {
        return $LAYOUT;
    }

    private static final OfLong start_pos$LAYOUT = (OfLong)$LAYOUT.select(groupElement("start_pos"));

    /**
     * Layout for field:
     * {@snippet lang=c :
     * OPJ_OFF_T start_pos
     * }
     */
    public static final OfLong start_pos$layout() {
        return start_pos$LAYOUT;
    }

    private static final long start_pos$OFFSET = 0;

    /**
     * Offset for field:
     * {@snippet lang=c :
     * OPJ_OFF_T start_pos
     * }
     */
    public static final long start_pos$offset() {
        return start_pos$OFFSET;
    }

    /**
     * Getter for field:
     * {@snippet lang=c :
     * OPJ_OFF_T start_pos
     * }
     */
    public static long start_pos(MemorySegment struct) {
        return struct.get(start_pos$LAYOUT, start_pos$OFFSET);
    }

    /**
     * Setter for field:
     * {@snippet lang=c :
     * OPJ_OFF_T start_pos
     * }
     */
    public static void start_pos(MemorySegment struct, long fieldValue) {
        struct.set(start_pos$LAYOUT, start_pos$OFFSET, fieldValue);
    }

    private static final OfLong end_ph_pos$LAYOUT = (OfLong)$LAYOUT.select(groupElement("end_ph_pos"));

    /**
     * Layout for field:
     * {@snippet lang=c :
     * OPJ_OFF_T end_ph_pos
     * }
     */
    public static final OfLong end_ph_pos$layout() {
        return end_ph_pos$LAYOUT;
    }

    private static final long end_ph_pos$OFFSET = 8;

    /**
     * Offset for field:
     * {@snippet lang=c :
     * OPJ_OFF_T end_ph_pos
     * }
     */
    public static final long end_ph_pos$offset() {
        return end_ph_pos$OFFSET;
    }

    /**
     * Getter for field:
     * {@snippet lang=c :
     * OPJ_OFF_T end_ph_pos
     * }
     */
    public static long end_ph_pos(MemorySegment struct) {
        return struct.get(end_ph_pos$LAYOUT, end_ph_pos$OFFSET);
    }

    /**
     * Setter for field:
     * {@snippet lang=c :
     * OPJ_OFF_T end_ph_pos
     * }
     */
    public static void end_ph_pos(MemorySegment struct, long fieldValue) {
        struct.set(end_ph_pos$LAYOUT, end_ph_pos$OFFSET, fieldValue);
    }

    private static final OfLong end_pos$LAYOUT = (OfLong)$LAYOUT.select(groupElement("end_pos"));

    /**
     * Layout for field:
     * {@snippet lang=c :
     * OPJ_OFF_T end_pos
     * }
     */
    public static final OfLong end_pos$layout() {
        return end_pos$LAYOUT;
    }

    private static final long end_pos$OFFSET = 16;

    /**
     * Offset for field:
     * {@snippet lang=c :
     * OPJ_OFF_T end_pos
     * }
     */
    public static final long end_pos$offset() {
        return end_pos$OFFSET;
    }

    /**
     * Getter for field:
     * {@snippet lang=c :
     * OPJ_OFF_T end_pos
     * }
     */
    public static long end_pos(MemorySegment struct) {
        return struct.get(end_pos$LAYOUT, end_pos$OFFSET);
    }

    /**
     * Setter for field:
     * {@snippet lang=c :
     * OPJ_OFF_T end_pos
     * }
     */
    public static void end_pos(MemorySegment struct, long fieldValue) {
        struct.set(end_pos$LAYOUT, end_pos$OFFSET, fieldValue);
    }

    private static final OfDouble disto$LAYOUT = (OfDouble)$LAYOUT.select(groupElement("disto"));

    /**
     * Layout for field:
     * {@snippet lang=c :
     * double disto
     * }
     */
    public static final OfDouble disto$layout() {
        return disto$LAYOUT;
    }

    private static final long disto$OFFSET = 24;

    /**
     * Offset for field:
     * {@snippet lang=c :
     * double disto
     * }
     */
    public static final long disto$offset() {
        return disto$OFFSET;
    }

    /**
     * Getter for field:
     * {@snippet lang=c :
     * double disto
     * }
     */
    public static double disto(MemorySegment struct) {
        return struct.get(disto$LAYOUT, disto$OFFSET);
    }

    /**
     * Setter for field:
     * {@snippet lang=c :
     * double disto
     * }
     */
    public static void disto(MemorySegment struct, double fieldValue) {
        struct.set(disto$LAYOUT, disto$OFFSET, fieldValue);
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

