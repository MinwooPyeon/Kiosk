/*
 * metrics.c
 *
 *  Created on: 2025. 10. 26.
 *      Author: SSAFY
 */
 
#include "metrics.h"
#include <string.h>

static metrics_snapshot_t s_m;

void metrics_inc_ok(void){ s_m.http_ok++; }
void metrics_inc_err(void){ s_m.http_err++; }
void metrics_add_bytes(uint32_t tx, uint32_t rx){ s_m.bytes_tx+=tx; s_m.bytes_rx+=rx; }
void metrics_sse_clients_set(uint32_t n){ s_m.sse_clients=n; }
void metrics_get(metrics_snapshot_t* out){ *out=s_m; }
