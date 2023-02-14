#include <stdio.h>
#include <stdbool.h>
#include <SDL2/SDL.h>

#define STB_TRUETYPE_IMPLEMENTATION
#include "stb_truetype.h"

#define WIDTH 400
#define HEIGHT 400

typedef unsigned long u64;
typedef long s64;
typedef unsigned int u32;
typedef int s32;
typedef unsigned char u8;
typedef char s8;

typedef struct {
    u8 *ascii;
    s32 *ascii_x;
    s32 *ascii_y;
    s32 *ascii_w;
    s32 *ascii_h;
    u32 height;
    u32 ascii_width;
}Font;

bool font_init(Font *font, const char *font_path, u32 font_height) {
    if(!font) {
	return false;
    }

    if(!font_path) {
	return false;
    }
    
    //READ FILE BEGIN
    FILE *f = fopen(font_path, "rb");
    if(!f) {
	return false;
    }
    
    if(fseek(f, 0, SEEK_END) < 0) {
	fclose(f);
	return false;
    }

    s64 m = ftell(f);
    if(m < 0) {
	fclose(f);
	return false;
    }  

    if(fseek(f, 0, SEEK_SET) < 0) {
	fclose(f);
	return false;
    }

    u8 *ttf_buffer = (u8 *) malloc((u32) m + 1);
    if(!ttf_buffer) {
	fclose(f);
	return false;
    }

    u64 _m = (u64) m;
    u64 n = fread(ttf_buffer, 1, _m, f);
    if(n != _m) {
	fclose(f);
	return false;
    }
    ttf_buffer[n] = 0;
    fclose(f);
    //READ FILE END
    
    stbtt_fontinfo font_info;        
    if(!stbtt_InitFont(&font_info, ttf_buffer, stbtt_GetFontOffsetForIndex(ttf_buffer,0))) {
	free(ttf_buffer);
	return false;
    }

    //TODO: combine all mallocs
    u32 ascii_vals = 128 - 32;
    font->ascii = (u8 *) malloc(ascii_vals * font_height * font_height * sizeof(u8));
    if(!font->ascii) {
	free(ttf_buffer);
	return false;
    }

    font->ascii_x = (s32 *) malloc(ascii_vals * sizeof(s32));
    if(!font->ascii_x) {
	free(font->ascii);
	free(ttf_buffer);
	return false;
    }

    font->ascii_y = (s32 *) malloc(ascii_vals * sizeof(s32));
    if(!font->ascii_y) {
	free(font->ascii);
	free(font->ascii_x);
	free(ttf_buffer);
	return false;
    }

    font->ascii_w = (s32 *) malloc(ascii_vals * sizeof(s32));
    if(!font->ascii_w) {
	free(font->ascii);
	free(font->ascii_x);
	free(font->ascii_y);
	free(ttf_buffer);
	return false;
    }

    font->ascii_h = (s32 *) malloc(ascii_vals * sizeof(s32));
    if(!font->ascii_h) {
	free(font->ascii);
	free(font->ascii_x);
	free(font->ascii_y);
	free(font->ascii_w);
	free(ttf_buffer);
	return false;
    }

    float scale = stbtt_ScaleForPixelHeight(&font_info, font_height);
    u32 ascii_width = ascii_vals * font_height;
    s32 _font_height = (s32) font_height;
    font->height = font_height;
    font->ascii_width = ascii_width;

    for(int c=32;c<=126;c++) {

	u32 glyph_off = font_height * (c - 32);
	
	int w, h, x, y;
	u8 *bitmap =
	    stbtt_GetCodepointSDF(&font_info, scale, c,  0, 128, 255.0, &w, &h, &x, &y);
	    //stbtt_GetCodepointBitmap(&font_info, 0, scale, c, &w, &h, &x, &y);

	font->ascii_w[c-32] = w;
	font->ascii_h[c-32] = h;
	font->ascii_x[c-32] = x;
	font->ascii_y[c-32] = y;

	for (s32 j=0;j<_font_height;++j) {
	    for(s32 i=0;i<_font_height;++i) {
		if(bitmap && i<w && j<h) {
		    unsigned char d = bitmap[j*w+i];
		    font->ascii[j*ascii_width+glyph_off+i] = d;
		} else {
		    font->ascii[j*ascii_width+glyph_off+i] = 0x00;
		}
	    }
	}
    
	if(bitmap) stbtt_FreeBitmap(bitmap, font_info.userdata);
    }

    free(ttf_buffer);
    return true;
}

u32 font_estimate_len(const Font *font, const char* cstr) {
    u32 x0 = 0;    
    while(*cstr) {
	char c = (*cstr++);
	if(c == ' ') x0 += font->height / 4;
	else x0 += font->ascii_w[c - 32] + font->ascii_x[c - 32];
    }
    return x0;
}

void font_free(Font *font) {
    if(!font) {
	return;
    }
    
    free(font->ascii);
    free(font->ascii_x);
    free(font->ascii_y);
    free(font->ascii_w);
    free(font->ascii_h);
}

static inline u32 rgba_mix(u32 color, u32 base) {
    u8 color_alpha = (0xff000000 & color) >> 24;

    u8 base_red = 0xff & base;
    u8 red = color_alpha * ((0xff & color) - base_red) / 0xff + base_red;

    u8 base_green = (0xff00 & base) >> 8;
    u8 green = color_alpha * (((0xff00 & color) >> 8) - base_green) / 0xff + base_green;
		
    u8 base_blue = (0xff0000 & base) >> 16;
    u8 blue = color_alpha * (((0xff0000 & color) >> 16) - base_blue) / 0xff + base_blue;

    return (color_alpha << 24) | (blue << 16) | (green << 8) | (red);
}

void rect(u32 *data, u32 width, u32 height, u32 x0, u32 y0, u32 w, u32 h, u32 color) {
    for(u32 dy=0;dy<h;dy++) {
	for(u32 dx=0;dx<w;dx++) {
	    u32 x = dx + x0;
	    u32 y = dy + y0;
	    if(y>=height || x>=width) continue;	    
	   		
	    data[y*width+x] = rgba_mix(color, data[y*width+x]);
	}
    }
}

void font_render(const Font *font, const char* cstr, u32 *pixels, u32 width, u32 height,
		 u32 x0, u32 y0, u32 color, bool wrap) {

    u32 h = font->height;
    color &= 0x00ffffff;
    
    int i = 0;
    while(cstr[i]) {
	char c = cstr[i];
	if(c == ' ' ) {
	    x0 += font->height / 4;
	    i++;
	    continue;
	}
	
	u32 glyph_off = (c - 32) * font->height;

	u32 w = font->ascii_w[c - 32] * h / font->height;
    
	for(u32 dy=0;dy<h;dy++) {
	    for(u32 dx=0;dx<w;dx++) {
		s32 x = (s32) x0 + (s32) + dx + font->ascii_x[c - 32];
		s32 y = (s32) y0 + (s32) + dy + (font->ascii_y[c - 32] * (s32) h / (s32) font->height);

		if(wrap) {
		    x %= (s32) width;
		    y %= (s32) height;
		}
		if(x<0 || x>=(s32) width || y<0 || y>=(s32) height) continue;

		u32 sx = dx * font->ascii_w[c-32]/ w;
		u32 sy = dy * font->height/ h;
	    
		u8 d = font->ascii[sy*font->ascii_width+glyph_off+sx];
		if(!d) continue;		
		pixels[y*WIDTH+x] = rgba_mix(0x00000000 | (d << 24) | color, pixels[y*WIDTH+x]);
	    }
	}
	x0 += w + font->ascii_x[c - 32];
	i++;
    }
}

static u32 padding = 0;

void render(u32 *data, const Font *font) {
    for(u32 y=0;y<HEIGHT;y++) {
	for(u32 x=0;x<WIDTH;x++) {
	    data[y*WIDTH + x] = 0xff181818;
	}
    }

    rect(data, WIDTH, HEIGHT, WIDTH/2 - 50 + 25, HEIGHT / 2 - 50 + 25, 100, 100, 0x5533ddff);
    rect(data, WIDTH, HEIGHT, WIDTH/2 - 100 + 25, HEIGHT / 2 - 100 + 25, 100, 100, 0x55ffdd33);
    
    const char *cstr = "playas circle - reezy.m4a";
    u32 width = font_estimate_len(font, cstr);

    font_render(font, cstr, data, WIDTH, HEIGHT, WIDTH / 2 - width / 2 + padding, HEIGHT - font->height * 3 + font->height / 2,
		0xffeeeeee, true);

    padding = (padding + 1) % WIDTH;
}

int main(int argc, char **argv) {
    (void) argc;
    (void) argv;

    Font font;
    if(!font_init(&font, "c:/windows/fonts/72-Regular.ttf", 20)) {
	fprintf(stderr, "ERROR: Can not load font\n");
	exit(1);
    }

    if(SDL_Init(SDL_INIT_VIDEO) < 0 ) {
	fprintf(stderr, "SDL-ERROR: %s\n", SDL_GetError());
	exit(1);
    }

    SDL_Window *window = NULL;
    if(!(window = SDL_CreateWindow("Font", 3000, 400, WIDTH, HEIGHT, 0))) {
	fprintf(stderr, "SDL-ERROR: %s\n", SDL_GetError());
	exit(1);	
    }

    SDL_Renderer *renderer = NULL;
    if(!(renderer = SDL_CreateRenderer(window, -1, SDL_RENDERER_SOFTWARE))) {
	fprintf(stderr, "SDL-ERROR: %s\n", SDL_GetError());
	exit(1);	
    }

    SDL_Texture *texture = NULL;
    if(!(texture = SDL_CreateTexture(renderer,
				     SDL_PIXELFORMAT_RGBA32,
				     SDL_TEXTUREACCESS_STREAMING,
				     WIDTH, HEIGHT))) {
	fprintf(stderr, "SDL-ERROR: %s\n", SDL_GetError());
	exit(1);
    }

    bool quit = false;
    SDL_Rect window_rect = {0, 0, WIDTH, HEIGHT};
   
    SDL_Event event;
    while(!quit) {
	u32 begin = SDL_GetTicks();
	
	while(SDL_PollEvent(&event)) {	
	    switch(event.type) {
	    case SDL_QUIT: {
		quit = true;
	    } break;
	    case SDL_KEYDOWN: {
		if(event.key.type == SDL_KEYDOWN && event.key.keysym.sym == SDLK_q) {
		    quit = true;
		}
	    } break;
	    }
	}


	void *texture_data = NULL;
	int texture_pitch = 0;
	if(SDL_LockTexture(texture, &window_rect, &texture_data, &texture_pitch) < 0) {
	    fprintf(stderr, "SDL-ERROR: %s\n", SDL_GetError());
	    exit(1);
	}
	//DRAW
	render((u32 *) texture_data, &font);
	
	SDL_UnlockTexture(texture);

	//RENDER
	if(SDL_RenderClear(renderer) < 0) {
	    fprintf(stderr, "SDL-ERROR: %s\n", SDL_GetError());
	    exit(1);	    
	}
	if(SDL_RenderCopy(renderer, texture, &window_rect, &window_rect) < 0) {
	    fprintf(stderr, "SDL-ERROR: %s\n", SDL_GetError());
	    exit(1);	    
	}
	SDL_RenderPresent(renderer);

	SDL_Delay(17);
    }

    //font_free(font);
    //SDL_DestroyTexture(texture);
    //SDL_DestroyRenderer(renderer);
    //SDL_DestroyWindow(window);
    //SDL_Quit();
    
    return 0;
}

