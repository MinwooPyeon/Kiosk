/*
 * frame.c
 *
 *  Created on: 2025. 10. 26.
 *  Updated on: 2025. 10. 27.
 *      Author: Park Joo Hyun
 */

#include "frame.h"
#include <string.h>
#include <stdint.h>
#include <stdbool.h>
#include <inttypes.h>

#include "esp_log.h"
#define TAG "frame"
#define PARSER_CANARY 0xDEADBEEF

static inline bool parser_sanity(frame_parser_t* p){
    if (!p) return false;
    if (p->canary1 != PARSER_CANARY || p->canary2 != PARSER_CANARY){
        ESP_LOGE(TAG, "parser canary corrupted (c1=0x%08x c2=0x%08x)", p->canary1, p->canary2);
        return false;
    }
    if (p->fill > FRAME_MAX_WIRE){
        ESP_LOGE(TAG, "parser fill overflow: %u > %u", (unsigned)p->fill, (unsigned)FRAME_MAX_WIRE);
        return false;
    }
    return true;
}


/* ===================== Utilities ===================== */
static inline uint16_t be16_rd(const uint8_t* p){ return (uint16_t)((p[0] << 8) | p[1]); }
static inline void     be16_wr(uint8_t* p, uint16_t v){ p[0] = (uint8_t)(v >> 8); p[1] = (uint8_t)(v & 0xFF); }

/* CRC-16/CCITT-FALSE: poly 0x1021, init 0xFFFF, refin/out=false, xorout=0x0000 */
uint16_t frame_crc16_ccitt(const uint8_t* data, size_t len){
    uint16_t crc = 0xFFFFu;
    for(size_t i=0;i<len;i++){
        crc ^= (uint16_t)data[i] << 8;
        for(int b=0;b<8;b++){
            if(crc & 0x8000) crc = (uint16_t)((crc << 1) ^ 0x1021);
            else             crc = (uint16_t)(crc << 1);
        }
    }
    return crc;
}

/* ===================== Build / Parse (single-shot) ===================== */
frame_err_t frame_build(uint8_t type,
                        const uint8_t* payload, uint16_t len,
                        uint8_t* out_buf, size_t out_cap, size_t* out_len)
{
    if(!out_buf){
        ESP_LOGE(TAG, "frame_build: out_buf=NULL");
        return FRAME_ERR_ARG;
    }
    if((len > 0 && !payload) || len > FRAME_MAX_PAYLOAD){
        ESP_LOGE(TAG, "frame_build: payload invalid len=%" PRIu16, len);
        return FRAME_ERR_OOB;
    }

    size_t need = FRAME_HDR_SIZE + len + FRAME_TLR_SIZE;
    if(out_cap < need){
        ESP_LOGE(TAG, "frame_build: out_cap too small (need=%u cap=%u)", (unsigned)need, (unsigned)out_cap);
        return FRAME_ERR_OOB;
    }

    /* header */
    out_buf[0] = FRAME_MAGIC_MSB;
    out_buf[1] = FRAME_MAGIC_LSB;
    out_buf[2] = FRAME_VER;
    out_buf[3] = type;
    be16_wr(&out_buf[4], len);

    /* payload */
    if(len) memcpy(&out_buf[6], payload, len);

    /* crc over VER..TYPE..LEN..PAYLOAD */
    uint16_t crc = frame_crc16_ccitt(&out_buf[2], 4u + len);
    be16_wr(&out_buf[6 + len], crc);

    if(out_len) *out_len = need;

    ESP_LOGD(TAG, "frame_build: type=0x%02X len=%" PRIu16 " crc=0x%04X need=%u", type, len, crc, (unsigned)need);
    return FRAME_OK;
}

frame_err_t frame_peek_len(const uint8_t* in_buf, size_t in_len, uint16_t* out_len){
    if(!in_buf || !out_len){
        ESP_LOGE(TAG, "frame_peek_len: invalid args");
        return FRAME_ERR_ARG;
    }
    if(in_len < FRAME_HDR_SIZE){
        ESP_LOGD(TAG, "frame_peek_len: truncated (have=%u need=%u)", (unsigned)in_len, (unsigned)FRAME_HDR_SIZE);
        return FRAME_ERR_TRUNC;
    }
    if(in_buf[0] != FRAME_MAGIC_MSB || in_buf[1] != FRAME_MAGIC_LSB || in_buf[2] != FRAME_VER){
        ESP_LOGW(TAG, "frame_peek_len: magic/version mismatch");
        return FRAME_ERR_MAGIC;
    }
    uint16_t len = be16_rd(&in_buf[4]);
    if(len > FRAME_MAX_PAYLOAD){
        ESP_LOGW(TAG, "frame_peek_len: len OOB (%" PRIu16 " > %u)", len, (unsigned)FRAME_MAX_PAYLOAD);
        return FRAME_ERR_OOB;
    }
    *out_len = len;
    return FRAME_OK;
}

frame_err_t frame_parse(const uint8_t* in_buf, size_t in_len, frame_t* out){
    if(!in_buf || !out){
        ESP_LOGE(TAG, "frame_parse: invalid args");
        return FRAME_ERR_ARG;
    }
    size_t min_need = FRAME_HDR_SIZE + FRAME_TLR_SIZE;
    if(in_len < min_need){
        ESP_LOGD(TAG, "frame_parse: truncated (have=%u need>=%u)", (unsigned)in_len, (unsigned)min_need);
        return FRAME_ERR_TRUNC;
    }
    if(in_buf[0] != FRAME_MAGIC_MSB || in_buf[1] != FRAME_MAGIC_LSB || in_buf[2] != FRAME_VER){
        ESP_LOGW(TAG, "frame_parse: magic/version mismatch");
        return FRAME_ERR_MAGIC;
    }

    uint8_t  type = in_buf[3];
    uint16_t len  = be16_rd(&in_buf[4]);
    if(len > FRAME_MAX_PAYLOAD){
        ESP_LOGW(TAG, "frame_parse: len OOB (%" PRIu16 " > %u)", len, (unsigned)FRAME_MAX_PAYLOAD);
        return FRAME_ERR_OOB;
    }

    size_t total = FRAME_HDR_SIZE + len + FRAME_TLR_SIZE;
    if(in_len < total){
        ESP_LOGD(TAG, "frame_parse: truncated (have=%u need=%u)", (unsigned)in_len, (unsigned)total);
        return FRAME_ERR_TRUNC;
    }

    uint16_t crc_rx = be16_rd(&in_buf[6 + len]);
    uint16_t crc_ok = frame_crc16_ccitt(&in_buf[2], 4u + len);
    if(crc_rx != crc_ok){
        ESP_LOGW(TAG, "frame_parse: crc mismatch rx=0x%04X ok=0x%04X", crc_rx, crc_ok);
        return FRAME_ERR_CRC;
    }

    out->type = type;
    out->len  = len;
    if(len) memcpy(out->payload, &in_buf[6], len);

    ESP_LOGD(TAG, "frame_parse: type=0x%02X len=%" PRIu16 " ok", type, len);
    return FRAME_OK;
}

/* ===================== Streaming Parser ===================== */

static inline void drop_left(uint8_t* buf, size_t* fill, size_t n){
    if(n == 0 || *fill == 0) return;
    if(n >= *fill){ *fill = 0; return; }
    memmove(buf, buf + n, *fill - n);
    *fill -= n;
}

static size_t append_to_buf(frame_parser_t* p, const uint8_t* data, size_t n){
    if (!parser_sanity(p)) return 0;
    size_t can  = (p->fill < FRAME_MAX_WIRE) ? (FRAME_MAX_WIRE - p->fill) : 0;
    size_t take = (n < can) ? n : can;
    if (take && data){
        memcpy(p->buf + p->fill, data, take);
        p->fill += take;
    } else if (n && !take){
        ESP_LOGE(TAG, "append overflow: n=%u fill=%u", (unsigned)n, (unsigned)p->fill);
    }
    return take;
}

/* MAGIC(A5 5A) 위치로 정렬: 찾으면 buf[0]==A5, buf[1]==5A */
static bool seek_magic(frame_parser_t* p, bool* dropped){
    *dropped = false;
    while(p->fill >= 2){
        size_t i = 0;
        for(; i + 1 < p->fill; ++i){
            if(p->buf[i]==FRAME_MAGIC_MSB && p->buf[i+1]==FRAME_MAGIC_LSB){
                if(i>0){ drop_left(p->buf, &p->fill, i); *dropped = true; }
                return true; /* MAGIC 정렬 완료 */
            }
        }
        /* MAGIC 미발견: 마지막 1바이트만 남기고 노이즈 제거 */
        size_t drop = (p->fill > 1) ? (p->fill - 1) : 0;
        if(drop){ drop_left(p->buf, &p->fill, drop); *dropped = true; }
        return false; /* 더 입력 필요 */
    }
    return false; /* 0~1 바이트 */
}

static inline bool have_min_header(const frame_parser_t* p){
    return p->fill >= FRAME_HDR_SIZE;
}

static bool check_version_or_resync(frame_parser_t* p, bool* resynced){
    *resynced = false;
    if(!have_min_header(p)) return false; /* 더 필요 */
    if(p->buf[2] == FRAME_VER) return true;
    drop_left(p->buf, &p->fill, 1); /* 1바이트 드롭하여 재동기화 */
    *resynced = true;
    return false;
}

static bool read_len_or_resync(frame_parser_t* p, uint16_t* out_len, bool* resynced){
    *resynced = false;
    if(!have_min_header(p)) return false;
    uint16_t len = be16_rd(&p->buf[4]);
    if(len <= FRAME_MAX_PAYLOAD){ *out_len = len; return true; }
    drop_left(p->buf, &p->fill, 1); /* 비정상 길이 → 1바이트 드롭 */
    *resynced = true;
    return false;
}

static inline bool have_full_frame(const frame_parser_t* p, uint16_t len){
    size_t need = FRAME_HDR_SIZE + len + FRAME_TLR_SIZE;
    return p->fill >= need;
}

static bool check_crc_or_resync(frame_parser_t* p, uint16_t len, bool* resynced){
    *resynced = false;
    size_t off_crc = FRAME_HDR_SIZE + len;
    uint16_t crc_rx = be16_rd(&p->buf[off_crc]);
    uint16_t crc_ok = frame_crc16_ccitt(&p->buf[2], 4u + len);
    if(crc_rx == crc_ok) return true;
    drop_left(p->buf, &p->fill, 1);
    *resynced = true;
    return false;
}

static void emit_frame_and_consume(frame_parser_t* p, uint16_t len, frame_t* out){
    out->type = p->buf[3];
    out->len  = len;
    if(len) memcpy(out->payload, &p->buf[6], len);
    size_t total = FRAME_HDR_SIZE + len + FRAME_TLR_SIZE;
    drop_left(p->buf, &p->fill, total);
}

/* ---- Public API ---- */
void frame_parser_init(frame_parser_t* p){
    if(!p) return;
    p->canary1 = PARSER_CANARY;
    p->canary2 = PARSER_CANARY;
    p->fill = 0;
    p->scan = 0;
    ESP_LOGI(TAG, "parser_init");
}

const char* frame_status_str(frame_parse_status_t st){
    switch(st){
    case FP_EMIT:             return "EMIT";
    case FP_MORE:             return "MORE";
    case FP_RESYNC_MAGIC:     return "RESYNC_MAGIC";
    case FP_RESYNC_VERSION:   return "RESYNC_VERSION";
    case FP_RESYNC_LEN_OOB:   return "RESYNC_LEN_OOB";
    case FP_RESYNC_CRC_FAIL:  return "RESYNC_CRC_FAIL";
    case FP_OVERFLOW:         return "OVERFLOW";
    case FP_ARG_ERROR:        return "ARG_ERROR";
    default:                  return "UNKNOWN";
    }
}

frame_parse_status_t frame_parser_feed(frame_parser_t* p, const uint8_t* data, size_t n, frame_t* out, size_t* consumed)
{
	ESP_LOGI(TAG, "Frame Entered");
    if(consumed) *consumed = 0;
    if(!p || (!data && n) || !out){
        ESP_LOGE(TAG, "feed: invalid args (p=%p data=%p n=%u out=%p)", (void*)p, data, (unsigned)n, (void*)out);
        return FP_ARG_ERROR;
    }

    /* 1) 입력 추가 */
    size_t took = append_to_buf(p, data, n);
    if(consumed) *consumed = took;
    if(n && took == 0){
        ESP_LOGW(TAG, "feed: overflow (fill=%u, max=%u)", (unsigned)p->fill, (unsigned)FRAME_MAX_WIRE);
        return FP_OVERFLOW;
    }

    /* 2) MAGIC 정렬 */
    bool dropped = false;
    if(!seek_magic(p, &dropped)){
        frame_parse_status_t st = dropped ? FP_RESYNC_MAGIC : FP_MORE;
        ESP_LOGD(TAG, "feed: %s (fill=%u)", frame_status_str(st), (unsigned)p->fill);
        return st;
    }

    /* 3) 헤더 확보 확인 */
    if(!have_min_header(p)){
        ESP_LOGD(TAG, "feed: MORE (need header) fill=%u", (unsigned)p->fill);
        return FP_MORE;
    }

    /* 4) 버전 확인 */
    bool resynced = false;
    if(!check_version_or_resync(p, &resynced)){
        frame_parse_status_t st = resynced ? FP_RESYNC_VERSION : FP_MORE;
        ESP_LOGW(TAG, "feed: %s", frame_status_str(st));
        return st;
    }

    /* 5) LEN 추출 */
    uint16_t len = 0;
    if(!read_len_or_resync(p, &len, &resynced)){
        frame_parse_status_t st = resynced ? FP_RESYNC_LEN_OOB : FP_MORE;
        ESP_LOGW(TAG, "feed: %s", frame_status_str(st));
        return st;
    }

    /* 6) 전체 프레임 확보 여부 */
    if(!have_full_frame(p, len)){
        ESP_LOGD(TAG, "feed: MORE (need total=%u, fill=%u)", (unsigned)(FRAME_HDR_SIZE + len + FRAME_TLR_SIZE), (unsigned)p->fill);
        return FP_MORE;
    }

    /* 7) CRC 확인 */
    if(!check_crc_or_resync(p, len, &resynced)){
        ESP_LOGW(TAG, "feed: %s", frame_status_str(FP_RESYNC_CRC_FAIL));
        return FP_RESYNC_CRC_FAIL;
    }

    /* 8) 프레임 배출 */
    emit_frame_and_consume(p, len, out);
    ESP_LOGI(TAG, "feed: EMIT type=0x%02X len=%" PRIu16 " (remain fill=%u)", out->type, out->len, (unsigned)p->fill);
    return FP_EMIT;
}