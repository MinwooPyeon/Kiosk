/*
 * media_json.c
 *
 *  Created on: 2025. 11. 10.
 *      Author: SSAFY
 */

#include "media_json.h"
#include "esp_log.h"
#include <string.h>
#include <stdlib.h>

#define USB_ADVERT_MAX_FILES 10
static const char* TAG = "media_json";

static inline const void* memmem_simple(const void* h, size_t hlen,
                                        const void* n, size_t nlen){
    if(!h||!n||!nlen||nlen>hlen) return NULL;
    const unsigned char* H=(const unsigned char*)h;
    const unsigned char* N=(const unsigned char*)n;
    size_t last = hlen - nlen;
    for(size_t i=0;i<=last;i++)
        if(H[i]==N[0] && memcmp(&H[i],N,nlen)==0) return &H[i];
    return NULL;
}
static inline const char* skip_ws_b(const char* p, const char* end){
    while(p<end && (*p==' '||*p=='\n'||*p=='\r'||*p=='\t')) p++;
    return p;
}
static inline uint64_t parse_uint_b(const char* p, const char* end){
    uint64_t v=0; while(p<end && *p>='0'&&*p<='9'){ v=v*10+(uint64_t)(*p-'0'); p++; } return v;
}

uint32_t media_json_parse_gen_or(const json_view_t* v, uint32_t defv)
{
    const char key_gen[]="\"gen\"";
    const char* js=v->js; const char* js_end=js+v->js_len;
    const char* pg=(const char*)memmem_simple(js,v->js_len,key_gen,sizeof(key_gen)-1);
    if(!pg) return defv;
    size_t remain=(size_t)(js_end-pg);
    const char* c=(const char*)memchr(pg,':',remain); if(!c) return defv;
    c=skip_ws_b(c+1,js_end);
    return (uint32_t)parse_uint_b(c,js_end);
}

void media_json_parse_index(const json_view_t* v, media_index_t* out)
{
    static media_item_t items[USB_ADVERT_MAX_FILES];
    const char* js=v->js; const char* js_end=js+v->js_len;

    out->count=0; out->items=NULL;

    const char key_files[]="\"files\"";
    const char* kf=(const char*)memmem_simple(js,v->js_len,key_files,sizeof(key_files)-1);
    if(!kf){ out->gen=1; return; }

    size_t remain=(size_t)(js_end-kf);
    const char* colon=(const char*)memchr(kf,':',remain);
    if(!colon){ out->gen=1; return; }

    const char* p=skip_ws_b(colon+1, js_end);
    if(p>=js_end || *p!='['){ out->gen=1; return; }
    p++;

    uint32_t count=0;
    while(p<js_end && count<USB_ADVERT_MAX_FILES){
        p=skip_ws_b(p,js_end);
        if(p>=js_end) break;
        if(*p==']'){ p++; break; }
        if(*p!='{'){
            const char* next_comma=(const char*)memchr(p,',',(size_t)(js_end-p));
            const char* next_rb=(const char*)memchr(p,']',(size_t)(js_end-p));
            if(!next_comma && !next_rb) break;
            p= next_comma && (!next_rb || next_comma<next_rb) ? next_comma+1 : next_rb;
            continue;
        }
        p++;
        memset(&items[count],0,sizeof(items[count]));
        while(p<js_end && *p!='}'){
            p=skip_ws_b(p,js_end);
            if(p>=js_end||*p=='}') break;

            if((js_end-p)>=4 && strncmp(p,"\"id\"",4)==0){
                const char* c=(const char*)memchr(p,':',(size_t)(js_end-p)); if(!c) break;
                c=skip_ws_b(c+1,js_end);
                if(c<js_end && *c=='\"'){
                    c++;
                    char* dst=items[count].id;
                    while(c<js_end && *c!='\"' && (dst-items[count].id)<(ptrdiff_t)sizeof(items[count].id)-1) *dst++=*c++;
                    *dst=0; if(c<js_end && *c=='\"') c++; p=c;
                } else p=c;
            }
            else if((js_end-p)>=6 && strncmp(p,"\"name\"",6)==0){
                const char* c=(const char*)memchr(p,':',(size_t)(js_end-p)); if(!c) break;
                c=skip_ws_b(c+1,js_end);
                if(c<js_end && *c=='\"'){
                    c++;
                    char* dst=items[count].name;
                    while(c<js_end && *c!='\"' && (dst-items[count].name)<(ptrdiff_t)sizeof(items[count].name)-1) *dst++=*c++;
                    *dst=0; if(c<js_end && *c=='\"') c++; p=c;
                } else p=c;
            }
            else if((js_end-p)>=6 && strncmp(p,"\"size\"",6)==0){
                const char* c=(const char*)memchr(p,':',(size_t)(js_end-p)); if(!c) break;
                c=skip_ws_b(c+1,js_end);
                items[count].size=(uint32_t)parse_uint_b(c,js_end);
                while(c<js_end && *c>='0'&&*c<='9') c++;
                p=c;
            }
            else{
                const char* next=p; while(next<js_end && *next!=',' && *next!='}') next++; p=next;
            }
            p=skip_ws_b(p,js_end);
            if(p<js_end && *p==',') p++;
        }
        if(p<js_end && *p=='}') p++;
        items[count].index=count;
        count++;

        p=skip_ws_b(p,js_end);
        if(p<js_end && *p==','){ p++; continue; }
        if(p<js_end && *p==']'){ p++; break; }
    }

    out->count = count;
    if(count==0){ out->items=NULL; return; }
    out->items=(media_item_t*)malloc(sizeof(media_item_t)*count);
    if(!out->items){ ESP_LOGE(TAG,"media index: oom"); out->count=0; }
    else memcpy(out->items, items, sizeof(media_item_t)*count);
}


