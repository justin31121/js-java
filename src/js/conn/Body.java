package js.conn;

import java.io.*;

class Body extends InputStream {

    private final byte[] rest;
    private int offset;
    private int count;
	
    private final InputStream in;
    private long len;

    protected Body(InputStream in, long len, byte[] rest, int offset, int count) {
	this.in = in;
	this.len = len;
	    
	this.rest = rest;
	this.offset = offset;
	this.count = count;
    }
	
    @Override
    public int read() throws IOException {
	if(len == 0) {
	    return -1;
	}

	len--;
	if(count > 0) {
	    count--;
	    return (int) rest[offset++];
	}

	return in.read();
    }

    @Override
    public void close() throws IOException {
	len = -1;
    }
}
