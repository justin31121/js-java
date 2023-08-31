package js.conn;

import java.io.*;
import java.util.*;

public class Request {
	    
    final String method;
    final String path;
    private Conn conn;
    private boolean headers;
    private boolean exposed;
    private final StringBuilder builder;

    protected Request(Conn conn, String method, String path) {
	this.method = method;
	this.path = path;
	this.conn = conn;
	this.builder = new StringBuilder();
	this.headers = false;
	this.exposed = false;

	builder
	    .append(method).append(" ").append(path).append(" HTTP/1.1\r\n")
	    .append("Host: ").append(conn.host);
	if(conn.variyingPort) {
	    builder.append(":").append(conn.port);
	}
	builder.append("\r\n");    
    }

    public OutputStream getOutputStream() throws IOException {
	if(!exposed) {
	    builder.append("Transfer-Encoding: Chunked\r\n");
	    builder.append("\r\n");
	    exposed = true;
	    conn.out.write(builder.toString().getBytes());
	}

	return new ChunkedOutputStream(conn.out);
    }

    public Request headers(boolean headers) {
	this.headers = headers;
	return this;
    }

    public Request set(String key, String value) {
	if(exposed) return null;
	builder.append(key).append(": ").append(value).append("\r\n");
	return this;
    }

    public Result submit(byte[] data, String contentType) throws IOException {
	if(exposed) {
	    throw new ConnException("Can not submit data on a 'request', on wich getOutputStream was called!");
	}
	builder.append("Content-Length").append(": ").append(data.length).append("\r\n");
	if(contentType != null) {
	    builder.append("Content-Type").append(": ").append(contentType).append("\r\n");
	}
	builder.append("\r\n");
	conn.out.write(builder.toString().getBytes());
	conn.out.write(data);
	conn.out.flush();
		
	return read();
    }

    public Result submit() throws IOException {
	       		
	if(!exposed) {		    
	    builder.append("\r\n");
	    exposed = true;		    
	    conn.out.write(builder.toString().getBytes());
	}
	conn.out.flush();

	return read();
    }

    public Result read() throws IOException {		
	Map<String, String> headerMap = null;
	if(headers) {
	    headerMap = new HashMap<>();
	} 

	byte[] buf = new byte[8192];
	int n;

	State state	= State.IDLE;
	Pair pair       = Pair.KEY;
	
	byte key[]	= new byte[1024];
	int key_len	= 0;
	byte value[]	= new byte[1024];
	int value_len	= 0;

	long content_length = 0;
	long response_code = -1;
	boolean chunked_encoding = false;

	int i = 0;
	while((n = conn.in.read(buf)) != -1) {
	    for(i=0;i<n;i++) {
		State before = state;
		if(buf[i] == '\r') {
		    if(state == State.IDLE) state = State.R;
		    else if(state == State.R) state = State.IDLE;
		    else if(state == State.RN) state = State.RNR;
		    else if(state == State.RNR) state = State.IDLE;
		    else if(state == State.BODY) state = State.BODY;
		} else if(buf[i] == '\n') {
		    if(state == State.IDLE) state = State.IDLE;
		    else if(state == State.R) state = State.RN;
		    else if(state == State.RN) state = State.IDLE;
		    else if(state == State.RNR) state = State.BODY;
		    else if(state == State.BODY) state = State.BODY;
		} else {
		    if(state == State.IDLE) state = State.IDLE;
		    else if(state == State.R) state = State.IDLE;
		    else if(state == State.RN) state = State.IDLE;
		    else if(state == State.RNR) state = State.IDLE;
		    else if(state == State.BODY) state = State.BODY;
		}

		if(state == State.IDLE && before == State.RN) {
		    pair = Pair.KEY;
		}

		if(pair == Pair.KEY) {
		    if(buf[i] == ':') {			
			pair = Pair.VALUE;
		    } else if(buf[i] == '\r') {

				
			if(!Util.matches(key, 0, Util.HTTP1.length, Util.HTTP1)) {
			    System.out.println(new String(key, 0, key_len));
			    throw new ConnException("Request is malformed");
			}

			response_code = Util.parseLong(key, Util.HTTP1.length, 3);
			if(response_code == -1) {
			    throw new ConnException("Could not parse responseCode");	    
			}
				    
			if(headers) {
			    headerMap.put(null,
					  new String(key, 0, key_len));
			}
		    } else {
			if(key_len >= key.length) {
			    byte[] new_key = new byte[key.length * 2];
			    for(int j=0;j<key_len;j++) new_key[j] = key[j];
			    key = new_key;
			}
			key[key_len++] = buf[i];
		    }
		} else if(pair == Pair.VALUE) {
		    if(buf[i] == '\r') {

			if(headers) {
			    headerMap.put(new String(key, 0, key_len),
					  new String(value, 1, value_len - 1));
			}

			if(Util.matches(key, 0, key_len, Util.CONTENT_LENGTH)) {			    
			    long len = Util.parseLong(value, 1, value_len - 1);
			    if(len == -1) {
				// error: can not parse content-length
				throw new ConnException("Could not parse Content-Length");
			    }
			    content_length = len;
			} else if(Util.matches(key, 0, key_len, Util.TRANSFER_ENCODING) &&
				  Util.matches(value, 1, value_len - 1, Util.CHUNKED)) {
			    if(content_length != 0) {
				throw new ConnException("Both Chunked-Encoding and Content-Length, were provided");
			    }
			    chunked_encoding = true;
			}
			
			pair = Pair.INVALID;
			value_len = 0;
			key_len = 0;
		    } else {
			if(value_len >= value.length) {
			    byte[] new_value = new byte[value.length * 2];
			    for(int j=0;j<value_len;j++) new_value[j] = value[j];
			    value = new_value;
			}
			value[value_len++] = buf[i];
		    }
		}

		if(state == State.BODY) {
		    break;
		}
	    }
	    if(state == State.BODY) {
		break;
	    }	    
	}

	if(state != State.BODY) {
	    throw new ConnException("Could not parse HTTP-headers");
	}	
	if(response_code == -1) {
	    throw new ConnException("Could not find Response-Code");	    
	}

	Result result = null;
	if(content_length > 0) {
	    i++;
	    result = new Result((int) response_code, content_length, headerMap, new Body(conn.in, content_length, buf, i, n - i));
	} else if(chunked_encoding) {
	    i++;
	    result =new Result((int) response_code, content_length, headerMap, new ChunkedBody(conn.in, buf, i, n - i));
	} else {
	    result =new Result((int) response_code, content_length, headerMap, new Body(null, content_length, null, 0, 0));
	}
				
	conn = null;
	return result;
    }

}

