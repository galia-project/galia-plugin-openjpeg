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
 * typedef OPJ_SIZE_T (*opj_stream_read_fn)(void *, OPJ_SIZE_T, void *)
 * }
 */
public class opj_stream_read_fn {

    opj_stream_read_fn() {
        // Should not be called directly
    }

    /**
     * The function pointer signature, expressed as a functional interface
     */
    public interface Function {
        long apply(MemorySegment p_buffer, long p_nb_bytes, MemorySegment p_user_data);
    }

    private static final FunctionDescriptor $DESC = FunctionDescriptor.of(
        openjpeg_h.C_LONG,
        openjpeg_h.C_POINTER,
        openjpeg_h.C_LONG,
        openjpeg_h.C_POINTER
    );

    /**
     * The descriptor of this function pointer
     */
    public static FunctionDescriptor descriptor() {
        return $DESC;
    }

    private static final MethodHandle UP$MH = openjpeg_h.upcallHandle(opj_stream_read_fn.Function.class, "apply", $DESC);

    /**
     * Allocates a new upcall stub, whose implementation is defined by {@code fi}.
     * The lifetime of the returned segment is managed by {@code arena}
     */
    public static MemorySegment allocate(opj_stream_read_fn.Function fi, Arena arena) {
        return Linker.nativeLinker().upcallStub(UP$MH.bindTo(fi), $DESC, arena);
    }

    private static final MethodHandle DOWN$MH = Linker.nativeLinker().downcallHandle($DESC);

    /**
     * Invoke the upcall stub {@code funcPtr}, with given parameters
     */
    public static long invoke(MemorySegment funcPtr,MemorySegment p_buffer, long p_nb_bytes, MemorySegment p_user_data) {
        try {
            return (long) DOWN$MH.invokeExact(funcPtr, p_buffer, p_nb_bytes, p_user_data);
        } catch (Throwable ex$) {
            throw new AssertionError("should not reach here", ex$);
        }
    }
}
