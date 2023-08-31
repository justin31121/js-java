package js.conn;

import java.io.*;

class ChunkedOutputStream extends OutputStream {

    private final OutputStream out;
    private final byte[] len_buffer;

    private final byte[] buffer;
    private int buffer_len;
	
    public ChunkedOutputStream(OutputStream out) {
	this.out = out;
	this.len_buffer = new byte[6];
	this.len_buffer[4] = '\r';
	this.len_buffer[5] = '\n';

	this.buffer = new byte[1 << 13];
	this.buffer_len = 0;
    }

    @Override
    public void write(int b) throws IOException {
	// Can not implement this function without the
	// context of a buffer
	//panic("implement write");
    }

    @Override
    public void write(byte[] data, int off, int len) throws IOException {

	if(len + buffer_len <= buffer.length) {
	    int s = off;	    
	    for(int i=0;i<len;i++) buffer[buffer_len++] = data[s++];

	    if(buffer_len == buffer.length) {
		if(!Util.stringifyHex(buffer.length, len_buffer, 0, 4)) {
		    //panic("can not stringify len");
		}

		out.write(len_buffer);
		out.write(buffer);
		out.write(len_buffer, 4, 2);
		buffer_len = 0;
	    }	    	    

	    
	} else {

	    int s = off;
	    while(len + buffer_len > buffer.length) {
		int diff = buffer.length - buffer_len;
		for(int i=0;i<diff;i++) buffer[buffer_len++] = data[s++];
		len -= diff;

		if(!Util.stringifyHex(buffer.length, len_buffer, 0, 4)) {
		    //panic("can not stringify len");
		}

		out.write(len_buffer);
		out.write(buffer);
		out.write(len_buffer, 4, 2);
		buffer_len = 0;
	    }

	    for(int i=0;i<len;i++) buffer[buffer_len++] = data[s++];
	}

    }

    @Override
    public void write(byte[] data) throws IOException {
	write(data, 0, data.length);
    }

    @Override
    public void flush() throws IOException {

	if(buffer_len > 0) {
	    if(!Util.stringifyHex(buffer_len, len_buffer, 0, 4)) {
		//panic("can not stringify len");
	    }

	    out.write(len_buffer);
	    out.write(buffer, 0, buffer_len);
	    out.write(len_buffer, 4, 2);
	    buffer_len = 0;
	}
	
	out.write(Util.ZERO_RNRN);
    }
	
}
