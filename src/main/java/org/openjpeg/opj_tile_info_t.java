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
 * typedef struct opj_tile_info {
 *     double *thresh;
 *     int tileno;
 *     int start_pos;
 *     int end_header;
 *     int end_pos;
 *     int pw[33];
 *     int ph[33];
 *     int pdx[33];
 *     int pdy[33];
 *     opj_packet_info_t *packet;
 *     int numpix;
 *     double distotile;
 *     int marknum;
 *     opj_marker_info_t *marker;
 *     int maxmarknum;
 *     int num_tps;
 *     opj_tp_info_t *tp;
 * } opj_tile_info_t
 * }
 */
public class opj_tile_info_t extends opj_tile_info {

    opj_tile_info_t() {
        // Should not be called directly
    }
}

