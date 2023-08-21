package js.http;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.io.ByteArrayInputStream;

public class HttpResult {

    private final InputStream inputStream;
    private final byte[] bytes;
    private final int code;

    public HttpResult(final int code) {
	this.inputStream = null;
	this.bytes = null;
	this.code = code;
    }

    public HttpResult(final String string, final int code) {
	this(string.getBytes(StandardCharsets.UTF_8), code);
    }
    

    public HttpResult(final byte[] bytes, final int code) {
	this.inputStream = null;
	this.bytes = bytes;
	this.code = code;
    }

    public HttpResult(final InputStream inputStream, final int code) {
	this.inputStream = inputStream;
	this.bytes = null;
	this.code = code;
    }

    public boolean hasInputStream() {
	return inputStream != null;
    }

    public boolean hasBytes() {
	return bytes != null;
    }

    public InputStream getInputStream() {
	return inputStream;
    }

    public byte[] getBytes() {
	return bytes;
    }

    public int getCode() {
	return code;
    }
}
