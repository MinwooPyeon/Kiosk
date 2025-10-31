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

bool lic_mgr_manager_login(const char* id, const char* pw){
	if(!id || !pw) return false;
	if(strcmp(id, s_mgr_id)==0 && strcmp(pw, s_mgr_pw) == 0) return true;
	return false;
}

static void make_license_key(char* out, size_t out_sz, int idx){
	snprintf(out, out_sz, "LIC_%04_%08X", idx, (unsigned)idx*26544357lu);
}

bool lic_mgr_issue(const char* app_id, const char* issued_to, uint32_t max_activations, uint32_t valid_days, char* out_license_key, size_t out_sz){
	if(!app_id || !issued_to || !out_license_key) return false;

	int slot = -1;
	for(int i=0;i<LIC_MAX_ENTRIES;i++){
		if(s_lic[i].license_key == 0){
			slot = i;
			break;
		}
	}

	if(slot < 0) return false;

	license_entry_t* e = &s_lic[slot];
	memset(e, 0, sizeof(*e));

	strncpy(e->app_id, app_id, sizeof(e->app_id)-1);
	strncpy(e->issued_to, issued_to, sizeof(e->issued_to)-1);
	e->max_activations = max_activations;
	e->valid_days      = valid_days;
	e->activations     = 0;
	e->revoked         = false;

	make_license_key(e->license_key, sizeof(e->license_key), slot);

	strncpy(out_license_key, e->license_key, out_sz - 1);
	out_license_key[out_sz - 1] = 0;
	return true;
}
