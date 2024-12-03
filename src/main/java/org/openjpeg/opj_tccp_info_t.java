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
 * typedef struct opj_tccp_info {
 *     OPJ_UINT32 compno;
 *     OPJ_UINT32 csty;
 *     OPJ_UINT32 numresolutions;
 *     OPJ_UINT32 cblkw;
 *     OPJ_UINT32 cblkh;
 *     OPJ_UINT32 cblksty;
 *     OPJ_UINT32 qmfbid;
 *     OPJ_UINT32 qntsty;
 *     OPJ_UINT32 stepsizes_mant[97];
 *     OPJ_UINT32 stepsizes_expn[97];
 *     OPJ_UINT32 numgbits;
 *     OPJ_INT32 roishift;
 *     OPJ_UINT32 prcw[33];
 *     OPJ_UINT32 prch[33];
 * } opj_tccp_info_t
 * }
 */
public class opj_tccp_info_t extends opj_tccp_info {

    opj_tccp_info_t() {
        // Should not be called directly
    }
}
