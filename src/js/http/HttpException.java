package js.http;

public class HttpException extends Exception {    
    private final HttpResult result;
    private final Exception e;
    
    public HttpException(final String message, final int code) {
	super();
	this.e = null;
	result = new HttpResult(message, code);
    }

    public HttpException(final Exception e, final String message, final int code) {
	super();	
	this.e = e;
	result = new HttpResult(message, code);
    }

    public HttpResult toHttpResult() {
	return result;
    }

    public void printStackTrace() {	
	if(e!=null) e.printStackTrace();
    }
}
