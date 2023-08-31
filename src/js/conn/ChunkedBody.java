package js.conn;

import java.io.*;

class ChunkedBody extends InputStream {
    private final byte[] rest;
    private int offset;
    private int count;
    private State state;
	
    private int len;
    private byte[] buffer;
    private int buffer_len;
	
    private final InputStream in;

    protected ChunkedBody(InputStream in, byte[] rest, int offset, int count) {
	this.in = in;	    
	this.rest = rest;
	this.offset = offset;
	this.count = count;
	this.state = State.IDLE;
	this.len = 0;
	this.buffer = new byte[4];
	this.buffer_len = 0;
    }

    @Override
    public int read() throws IOException {
	if(len == -1) {
	    return -1;
	}
	    
	int c;	    
	if(count > 0) {
	    count--;
	    c = (int) rest[offset++];
	} else {
	    c = in.read();
	}

	byte b = (byte) c;

	if(b == '\r') {
	    if(state == State.IDLE) state = State.R;
	    else if(state == State.R) state = State.R;
	    else if(state == State.RN) state = State.R;
	} else if(b == '\n') {
	    if(state == State.IDLE) state = State.IDLE;
	    else if(state == State.R) state = State.RN;
	    else if(state == State.RN) state = State.IDLE;
	} else {
	    if(state == State.IDLE) state = State.IDLE;
	    else if(state == State.R) state = State.IDLE;
	    else if(state == State.RN) state = State.IDLE;
	}

	if(len == 0) {
	    // looking for len
		
	    if(state == State.IDLE) {
		if(buffer_len > 4) {
		    //TODO: maybe indicate erorr of stream
		    return -1;
		}
		    
		// consume byte
		buffer[buffer_len++] = b;
		    
	    } else if(state == State.RN) {

		if(buffer_len > 0) {
		    // parse len		    
		    long length = Util.parseHexLong(buffer, 0, buffer_len);
		    if(length == -1) {
			//panic("Can not parse hexstirng in chunked encoding");
			return -1;
		    }

		    if(length == 0) {

			// finish
			len = -1;

			// consume trailing: '\r\n'
			if(c != -1) c = in.read();
			if(c != -1) in.read();
			    
			return -1;
		    }
		    
		    len = (int) length;
		    buffer_len = 0;
		} else {
		    // parse \r\n
		}
		    
	    } else {
		// do nothing 
	    }
		
	    return read();
	} else {
	    // consuming len

	    len--; // TODO: check for underflow
	}
	    
	return c;
    }

    @Override
    public void close() throws IOException {
	// pass
    }

}
