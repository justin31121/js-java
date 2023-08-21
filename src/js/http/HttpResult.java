package js.http;

import java.io.File;

public class HttpResult {

    private final String content;
    private final File file;
    private final byte[] bytes;
    private final int code;
    
    public HttpResult(final String message, final int code) {
	this.content = message.toString();
	this.file = null;
	this.bytes = null;
	this.code = code;
    }

    public HttpResult(final File file, final int code) {
	this.content = null;
	this.bytes = null;
	this.file = file;
	this.code = code;
    }

    public HttpResult(final byte[] bytes, final int code) {
	this.content = null;
	this.file = null;
	this.bytes = bytes;
	this.code = code;
    }

    public boolean isFile() {
	return file!=null;
    }

    public boolean isContent() {
	return content!=null;
    }

    public boolean isBytes() {
	return bytes!=null;
    }

    public byte[] getBytes() {
	if(isBytes()) throw new RuntimeException("Can not get content, when HttpResult contains a file");
	return bytes;	
    }

    public String getContent() {
	if(isFile()) throw new RuntimeException("Can not get content, when HttpResult contains a file");
	return content;
    }

    public File getFile() {
	if(isContent()) throw new RuntimeException("Can not get file, when HttpResult contains a content");
	return file;
    }

    public int getCode() {
	return code;
    }
}
