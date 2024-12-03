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
 * struct opj_image_comptparm {
 *     OPJ_UINT32 dx;
 *     OPJ_UINT32 dy;
 *     OPJ_UINT32 w;
 *     OPJ_UINT32 h;
 *     OPJ_UINT32 x0;
 *     OPJ_UINT32 y0;
 *     OPJ_UINT32 prec;
 *     OPJ_UINT32 bpp;
 *     OPJ_UINT32 sgnd;
 * }
 * }
 */
public class opj_image_comptparm {

    opj_image_comptparm() {
        // Should not be called directly
    }

    private static final GroupLayout $LAYOUT = MemoryLayout.structLayout(
        openjpeg_h.C_INT.withName("dx"),
        openjpeg_h.C_INT.withName("dy"),
        openjpeg_h.C_INT.withName("w"),
        openjpeg_h.C_INT.withName("h"),
        openjpeg_h.C_INT.withName("x0"),
        openjpeg_h.C_INT.withName("y0"),
        openjpeg_h.C_INT.withName("prec"),
        openjpeg_h.C_INT.withName("bpp"),
        openjpeg_h.C_INT.withName("sgnd")
    ).withName("opj_image_comptparm");

    /**
     * The layout of this struct
     */
    public static final GroupLayout layout() {
        return $LAYOUT;
    }

    private static final OfInt dx$LAYOUT = (OfInt)$LAYOUT.select(groupElement("dx"));

    /**
     * Layout for field:
     * {@snippet lang=c :
     * OPJ_UINT32 dx
     * }
     */
    public static final OfInt dx$layout() {
        return dx$LAYOUT;
    }

    private static final long dx$OFFSET = 0;

    /**
     * Offset for field:
     * {@snippet lang=c :
     * OPJ_UINT32 dx
     * }
     */
    public static final long dx$offset() {
        return dx$OFFSET;
    }

    /**
     * Getter for field:
     * {@snippet lang=c :
     * OPJ_UINT32 dx
     * }
     */
    public static int dx(MemorySegment struct) {
        return struct.get(dx$LAYOUT, dx$OFFSET);
    }

    /**
     * Setter for field:
     * {@snippet lang=c :
     * OPJ_UINT32 dx
     * }
     */
    public static void dx(MemorySegment struct, int fieldValue) {
        struct.set(dx$LAYOUT, dx$OFFSET, fieldValue);
    }

    private static final OfInt dy$LAYOUT = (OfInt)$LAYOUT.select(groupElement("dy"));

    /**
     * Layout for field:
     * {@snippet lang=c :
     * OPJ_UINT32 dy
     * }
     */
    public static final OfInt dy$layout() {
        return dy$LAYOUT;
    }

    private static final long dy$OFFSET = 4;

    /**
     * Offset for field:
     * {@snippet lang=c :
     * OPJ_UINT32 dy
     * }
     */
    public static final long dy$offset() {
        return dy$OFFSET;
    }

    /**
     * Getter for field:
     * {@snippet lang=c :
     * OPJ_UINT32 dy
     * }
     */
    public static int dy(MemorySegment struct) {
        return struct.get(dy$LAYOUT, dy$OFFSET);
    }

    /**
     * Setter for field:
     * {@snippet lang=c :
     * OPJ_UINT32 dy
     * }
     */
    public static void dy(MemorySegment struct, int fieldValue) {
        struct.set(dy$LAYOUT, dy$OFFSET, fieldValue);
    }

    private static final OfInt w$LAYOUT = (OfInt)$LAYOUT.select(groupElement("w"));

    /**
     * Layout for field:
     * {@snippet lang=c :
     * OPJ_UINT32 w
     * }
     */
    public static final OfInt w$layout() {
        return w$LAYOUT;
    }

    private static final long w$OFFSET = 8;

    /**
     * Offset for field:
     * {@snippet lang=c :
     * OPJ_UINT32 w
     * }
     */
    public static final long w$offset() {
        return w$OFFSET;
    }

    /**
     * Getter for field:
     * {@snippet lang=c :
     * OPJ_UINT32 w
     * }
     */
    public static int w(MemorySegment struct) {
        return struct.get(w$LAYOUT, w$OFFSET);
    }

    /**
     * Setter for field:
     * {@snippet lang=c :
     * OPJ_UINT32 w
     * }
     */
    public static void w(MemorySegment struct, int fieldValue) {
        struct.set(w$LAYOUT, w$OFFSET, fieldValue);
    }

    private static final OfInt h$LAYOUT = (OfInt)$LAYOUT.select(groupElement("h"));

    /**
     * Layout for field:
     * {@snippet lang=c :
     * OPJ_UINT32 h
     * }
     */
    public static final OfInt h$layout() {
        return h$LAYOUT;
    }

    private static final long h$OFFSET = 12;

    /**
     * Offset for field:
     * {@snippet lang=c :
     * OPJ_UINT32 h
     * }
     */
    public static final long h$offset() {
        return h$OFFSET;
    }

    /**
     * Getter for field:
     * {@snippet lang=c :
     * OPJ_UINT32 h
     * }
     */
    public static int h(MemorySegment struct) {
        return struct.get(h$LAYOUT, h$OFFSET);
    }

    /**
     * Setter for field:
     * {@snippet lang=c :
     * OPJ_UINT32 h
     * }
     */
    public static void h(MemorySegment struct, int fieldValue) {
        struct.set(h$LAYOUT, h$OFFSET, fieldValue);
    }

    private static final OfInt x0$LAYOUT = (OfInt)$LAYOUT.select(groupElement("x0"));

    /**
     * Layout for field:
     * {@snippet lang=c :
     * OPJ_UINT32 x0
     * }
     */
    public static final OfInt x0$layout() {
        return x0$LAYOUT;
    }

    private static final long x0$OFFSET = 16;

    /**
     * Offset for field:
     * {@snippet lang=c :
     * OPJ_UINT32 x0
     * }
     */
    public static final long x0$offset() {
        return x0$OFFSET;
    }

    /**
     * Getter for field:
     * {@snippet lang=c :
     * OPJ_UINT32 x0
     * }
     */
    public static int x0(MemorySegment struct) {
        return struct.get(x0$LAYOUT, x0$OFFSET);
    }

    /**
     * Setter for field:
     * {@snippet lang=c :
     * OPJ_UINT32 x0
     * }
     */
    public static void x0(MemorySegment struct, int fieldValue) {
        struct.set(x0$LAYOUT, x0$OFFSET, fieldValue);
    }

    private static final OfInt y0$LAYOUT = (OfInt)$LAYOUT.select(groupElement("y0"));

    /**
     * Layout for field:
     * {@snippet lang=c :
     * OPJ_UINT32 y0
     * }
     */
    public static final OfInt y0$layout() {
        return y0$LAYOUT;
    }

    private static final long y0$OFFSET = 20;

    /**
     * Offset for field:
     * {@snippet lang=c :
     * OPJ_UINT32 y0
     * }
     */
    public static final long y0$offset() {
        return y0$OFFSET;
    }

    /**
     * Getter for field:
     * {@snippet lang=c :
     * OPJ_UINT32 y0
     * }
     */
    public static int y0(MemorySegment struct) {
        return struct.get(y0$LAYOUT, y0$OFFSET);
    }

    /**
     * Setter for field:
     * {@snippet lang=c :
     * OPJ_UINT32 y0
     * }
     */
    public static void y0(MemorySegment struct, int fieldValue) {
        struct.set(y0$LAYOUT, y0$OFFSET, fieldValue);
    }

    private static final OfInt prec$LAYOUT = (OfInt)$LAYOUT.select(groupElement("prec"));

    /**
     * Layout for field:
     * {@snippet lang=c :
     * OPJ_UINT32 prec
     * }
     */
    public static final OfInt prec$layout() {
        return prec$LAYOUT;
    }

    private static final long prec$OFFSET = 24;

    /**
     * Offset for field:
     * {@snippet lang=c :
     * OPJ_UINT32 prec
     * }
     */
    public static final long prec$offset() {
        return prec$OFFSET;
    }

    /**
     * Getter for field:
     * {@snippet lang=c :
     * OPJ_UINT32 prec
     * }
     */
    public static int prec(MemorySegment struct) {
        return struct.get(prec$LAYOUT, prec$OFFSET);
    }

    /**
     * Setter for field:
     * {@snippet lang=c :
     * OPJ_UINT32 prec
     * }
     */
    public static void prec(MemorySegment struct, int fieldValue) {
        struct.set(prec$LAYOUT, prec$OFFSET, fieldValue);
    }

    private static final OfInt bpp$LAYOUT = (OfInt)$LAYOUT.select(groupElement("bpp"));

    /**
     * Layout for field:
     * {@snippet lang=c :
     * OPJ_UINT32 bpp
     * }
     */
    public static final OfInt bpp$layout() {
        return bpp$LAYOUT;
    }

    private static final long bpp$OFFSET = 28;

    /**
     * Offset for field:
     * {@snippet lang=c :
     * OPJ_UINT32 bpp
     * }
     */
    public static final long bpp$offset() {
        return bpp$OFFSET;
    }

    /**
     * Getter for field:
     * {@snippet lang=c :
     * OPJ_UINT32 bpp
     * }
     */
    public static int bpp(MemorySegment struct) {
        return struct.get(bpp$LAYOUT, bpp$OFFSET);
    }

    /**
     * Setter for field:
     * {@snippet lang=c :
     * OPJ_UINT32 bpp
     * }
     */
    public static void bpp(MemorySegment struct, int fieldValue) {
        struct.set(bpp$LAYOUT, bpp$OFFSET, fieldValue);
    }

    private static final OfInt sgnd$LAYOUT = (OfInt)$LAYOUT.select(groupElement("sgnd"));

    /**
     * Layout for field:
     * {@snippet lang=c :
     * OPJ_UINT32 sgnd
     * }
     */
    public static final OfInt sgnd$layout() {
        return sgnd$LAYOUT;
    }

    private static final long sgnd$OFFSET = 32;

    /**
     * Offset for field:
     * {@snippet lang=c :
     * OPJ_UINT32 sgnd
     * }
     */
    public static final long sgnd$offset() {
        return sgnd$OFFSET;
    }

    /**
     * Getter for field:
     * {@snippet lang=c :
     * OPJ_UINT32 sgnd
     * }
     */
    public static int sgnd(MemorySegment struct) {
        return struct.get(sgnd$LAYOUT, sgnd$OFFSET);
    }

    /**
     * Setter for field:
     * {@snippet lang=c :
     * OPJ_UINT32 sgnd
     * }
     */
    public static void sgnd(MemorySegment struct, int fieldValue) {
        struct.set(sgnd$LAYOUT, sgnd$OFFSET, fieldValue);
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
