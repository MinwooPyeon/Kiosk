/*
 * metrics.h
 *
 *  Created on: 2025. 10. 26.
 *      Author: SSAFY
 */

#ifndef MAIN_INCLUDE_METRICS_H_
#define MAIN_INCLUDE_METRICS_H_

#include <stdint.h>

typedef struct{
	uint32_t http_ok, http_err;
	uint32_t bytes_tx, bytes_rx;
	uint32_t sse_clients;
}metrics_snapshot_t;

void metrics_inc_ok(void);
void metrics_inc_err(void);
void metrics_add_bytes(uint32_t tx, uint32_t rx);
void metrics_sse_clients_set(uint32_t n);
void metrics_get(metrics_snapshot_t* out);

#endif /* MAIN_INCLUDE_METRICS_H_ */
