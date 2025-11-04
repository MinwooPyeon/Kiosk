/*
 * lic_dispatch.c
 *
 *  Created on: Nov 4, 2025
 *      Author: SSAFY
 */
#include "license_mgr.h"
#include "lic_dispatch.h"

#include <string.h>
#include <stdio.h>
#include <stdbool.h>

/* 1. Manager Login */
void lic_dispatch_mgr_login(const uint8_t* payload, uint16_t pl_len,
                                   uint8_t* out_payload, uint16_t* out_len)
{
    char id[32]={0}, pw[32]={0};

    const char* colon = memchr(payload, ':', pl_len);
    if (colon) {
        size_t idn = (size_t)(colon - (const char*)payload);
        size_t pwn = pl_len - idn - 1;
        if (idn >= sizeof(id)) idn = sizeof(id) - 1;
        if (pwn >= sizeof(pw)) pwn = sizeof(pw) - 1;
        memcpy(id, payload, idn); id[idn] = 0;
        memcpy(pw, colon + 1, pwn); pw[pwn] = 0;
    }

    bool ok = lic_mgr_manager_login(id, pw);
    out_payload[0] = ok ? 1 : 0;
    *out_len = 1;
}

/* 2. License Issuance: "app:issued_to" */
void lic_dispatch_issue(const uint8_t* payload, uint16_t pl_len,
                               uint8_t* out_payload, uint16_t* out_len)
{
    char app[32]={0}, to[64]={0};

    const char* colon = memchr(payload, ':', pl_len);
    if (colon) {
        size_t an = (size_t)(colon - (const char*)payload);
        size_t tn = pl_len - an - 1;
        if (an >= sizeof(app)) an = sizeof(app)-1;
        if (tn >= sizeof(to))  tn = sizeof(to)-1;
        memcpy(app, payload, an); app[an]=0;
        memcpy(to, colon+1, tn);  to[tn]=0;
    }

    char lic[LIC_KEY_MAX_LEN];
    bool ok = lic_mgr_issue(app, to, 5, 30, lic, sizeof(lic));
    if (ok) {
        out_payload[0] = 1;
        size_t ln = strlen(lic);
        memcpy(&out_payload[1], lic, ln);
        *out_len = 1 + ln;
    } else {
        out_payload[0] = 0;
        *out_len = 1;
    }
}

/* 3. Revoke: payload 전체가 license_key */
void lic_dispatch_revoke(const uint8_t* payload, uint16_t pl_len,
                                uint8_t* out_payload, uint16_t* out_len)
{
    char lic[LIC_KEY_MAX_LEN]={0};
    size_t ln = (pl_len < sizeof(lic)-1) ? pl_len : (sizeof(lic)-1);
    memcpy(lic, payload, ln); lic[ln]=0;

    bool ok = lic_mgr_revoke(lic);
    out_payload[0] = ok ? 1 : 0;
    *out_len = 1;
}

/* 4. Validate: payload 전체가 license_key */
void lic_dispatch_validate(const uint8_t* payload, uint16_t pl_len,
                                  uint8_t* out_payload, uint16_t* out_len)
{
    char lic[LIC_KEY_MAX_LEN]={0};
    size_t ln = (pl_len < sizeof(lic)-1) ? pl_len : (sizeof(lic)-1);
    memcpy(lic, payload, ln); lic[ln]=0;

    bool ok = lic_mgr_validate(lic);
    out_payload[0] = ok ? 1 : 0;
    *out_len = 1;
}

/* 5. Get Challenge: 여기선 license_key만 받아서 challenge 생성 */
void lic_dispatch_get_challenge(const uint8_t* payload, uint16_t pl_len,
                                       uint8_t* out_payload, uint16_t* out_len)
{
    char lic[LIC_KEY_MAX_LEN]={0};
    size_t ln = (pl_len < sizeof(lic)-1) ? pl_len : (sizeof(lic)-1);
    memcpy(lic, payload, ln); lic[ln]=0;

    uint8_t chal[32];
    uint16_t clen = 0;
    bool ok = lic_mgr_get_challenge(lic, NULL, NULL, "dummy-ssaid", NULL, chal, &clen);
    if (ok) {
        out_payload[0] = 1;
        memcpy(&out_payload[1], chal, clen);
        *out_len = 1 + clen;
    } else {
        out_payload[0] = 0;
        *out_len = 1;
    }
}

/* 6. Get JWT */
void lic_dispatch_get_jwt(const uint8_t* payload, uint16_t pl_len,
                                 uint8_t* out_payload, uint16_t* out_len)
{
    char lic[LIC_KEY_MAX_LEN]={0};
    size_t ln = (pl_len < sizeof(lic)-1) ? pl_len : (sizeof(lic)-1);
    memcpy(lic, payload, ln); lic[ln]=0;

    char jwt[128];
    bool ok = lic_mgr_get_jwt(lic, NULL, "dummy-ssaid", NULL, NULL,
                              jwt, sizeof(jwt));
    if (ok) {
        out_payload[0] = 1;
        size_t jn = strlen(jwt);
        memcpy(&out_payload[1], jwt, jn);
        *out_len = 1 + jn;
    } else {
        out_payload[0] = 0;
        *out_len = 1;
    }
}
