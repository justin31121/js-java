package js.http;

import java.io.File;

public class HttpResult {

    private final String content;
    private final File file;
    private final int code;
    
    public HttpResult(final Object message, final int code) {
	this.content = message.toString();
	this.file = null;
	this.code = code;
    }

    public HttpResult(final File file, final int code) {
	this.content = null;
	this.file = file;
	this.code = code;
    }

    public boolean isFile() {
	return file!=null;
    }

    public boolean isContent() {
	return content!=null;
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
