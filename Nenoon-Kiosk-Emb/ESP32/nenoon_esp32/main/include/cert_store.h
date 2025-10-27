/*
 * cert_store.h
 *
 * - Root CA: attach to esp_http_client_config_t (cert_pem or crt_bundle_attach)
 * - SPKI pinning: preflight verify via mbedTLS (no private esp_transport)
 *  Created on: 2025. 10. 26.
 *  Updated on: 2025. 10. 27.
 *      Author: Park Joo Hyun
 */

#ifndef MAIN_INCLUDE_CERT_STORE_H_
#define MAIN_INCLUDE_CERT_STORE_H_

#include <stdbool.h>
#include <stddef.h>
#include <stdint.h>
#include "esp_err.h"
#include "esp_http_client.h"

typedef struct{
	const char* ca_pem;
	uint8_t 	pin_spki_sha256[32];
	bool 		have_pin;
	bool 		use_crt_bundle;
}cert_store_cfg_t;

void cert_store_init(const cert_store_cfg_t* cfg);
esp_err_t cert_store_attach(esp_http_client_config_t* http_cfg);
esp_err_t cert_store_preverify_spki(const char* url);

#endif /* MAIN_INCLUDE_CERT_STORE_H_ */
