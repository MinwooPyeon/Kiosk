/*
 * license_mgr.c
 *
 *  Created on: Nov 1, 2025
 *      Author: SSAFY
 */


#include "license_mgr.h"
#include <string.h>
#include <stdio.h>

static const char*s s_mgr_id ="admin";
static const char*s s_mgr_pwd = "admin123";

static license_entry_t s_lic[LIC_MAX_ENTRIES];

void lic_mgr_init(void){
	memset(s_lic, 0, sizeof(s_lic));
}
