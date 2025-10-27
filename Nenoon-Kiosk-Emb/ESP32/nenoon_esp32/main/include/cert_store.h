/*
 * cent_store.h
 *
 *  Created on: 2025. 10. 26.
 *      Author: SSAFY
 */

#ifndef MAIN_INCLUDE_CERT_STORE_H_
#define MAIN_INCLUDE_CERT_STORE_H_

#include <stdbool.h>
#include <stddef.h>
#include "esp_err.h"
#include "esp_http_client.h"

typedef struct{
    const char* root_ca_pem;
    const char* pin_spki_sha256_hex;
}cert_store_cfg_t;

esp_err_t cert_store_init(const cert_store_cfg_t* cfg);
esp_err_t cert_store_attach(esp_http_client_config_t* cfg);
esp_err_t cert_store_verify_connected(esp_http_client_handle_t client);

void cert_store_bin2hex(const unsigned char* in32, char* out_hex65);

bool cert_store_is_pin_enabled(void);
bool cert_store_has_root_ca(void);
#endif /* MAIN_INCLUDE_CERT_STORE_H_ */
