package js.http;

import com.sun.net.httpserver.HttpExchange;
import java.io.OutputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.File;
import java.io.FileInputStream;
import java.util.concurrent.Callable;

import org.json.JSONObject;

import java.util.Map;
import java.util.HashMap;

public class Util {

    public static final <T> T notNull(Function<T> f) throws HttpException {
	try{
	    T t = f.produce();
	    if(t==null) throw new HttpException("isNull", 400);
	    return t;
	}
	catch(Exception ex) {
	    throw new HttpException(ex, "isNull", 400);
	}
    }
    
    public static final String requestBody(final HttpExchange t) throws IOException {
        StringBuilder builder = new StringBuilder(512);

        InputStreamReader inputStreamReader = new InputStreamReader(t.getRequestBody(), "utf-8");
        BufferedReader reader = new BufferedReader(inputStreamReader);

        int b;
        while((b = reader.read())!=-1) {
            builder.append((char) b);
        }
	
        return builder.toString();
    }

    public static final JSONObject requestJSON(final HttpExchange t) throws IOException, HttpException {
	String body = requestBody(t);
	try{
	    return new JSONObject(body);
	}
	catch(Exception e) {
	    throw new HttpException(e, "Can not parse JSON", 400);
	}
    }

    public static final String requestAttribute(final HttpExchange t, final String key) {
	return t.getRequestHeaders().getFirst(key);
    }

    public static final HttpResult ok() {
	return new HttpResult("", 200);
    }

    public static final HttpResult ok(final Object message) {
	return new HttpResult(message, 200);
    }

    public static final HttpResult ok(final JSONObject json) {
	return new HttpResult(json.toString().replace("\"NULL\"", "null"), 200);
    }

    public static final HttpResult ok(final File file) {
	return new HttpResult(file, 200);
    }

    public static final HttpResult notFound(final String mess) {
	return new HttpResult(mess, 404);
    }

    public static final HttpResult notFound() {
	return new HttpResult("Not found", 404);
    }

    public static final HttpResult internalError() {
	return new HttpResult("Internal Error", 500);
    }

    public static final Map<String, String> getParameters(final HttpExchange t) throws HttpException {
	final String uri = t.getRequestURI().toString();
	final Map<String, String> parameters = new HashMap<String, String>();
	int p = uri.indexOf("?");
	if(p != -1) {
	    String[] pairs = uri.substring(p+1, uri.length()).split("&");
	    for(int i=0;i<pairs.length;i++) {
		String[] assignment = pairs[i].split("=");
		if(assignment.length != 2) throw new HttpException("Malformed Parameters", 404);
		parameters.put(assignment[0], assignment[1]);
	    }
	}

	return parameters;
    }

    public static final String getParametersString(final HttpExchange t) {
	final String uri = t.getRequestURI().toString();
	int p = uri.indexOf("?");
	if(p == -1) return "";
	return uri.substring(p, uri.length());
    }

    public static final String getRoute(final HttpExchange t, final String prefix) {	
	final String uri = t.getRequestURI().toString();
	if(uri.indexOf(prefix) != 0) return null;
	int len = prefix.length();
	int p = uri.indexOf("?", prefix.length());
	if(p == -1) {
	    return uri.substring(len, uri.length());
	}

	return uri.substring(len, p);
    }

    public static final void respond(HttpExchange t, final HttpResult result) throws IOException {
	if(result.isContent()) respond(t, result.getContent(), result.getCode());
	else respond(t, result.getFile(), result.getCode());
    }

    public static final void respond(HttpExchange t, String message, int rCode) throws IOException {
        OutputStream os = t.getResponseBody();
        if(message==null) {
            message = "null";
        }
        byte[] bytes = message.getBytes("UTF-8");
        t.sendResponseHeaders(rCode, bytes.length);
        os.write(bytes);
        os.close();
    }

    public static final void respond(HttpExchange t, File file, int rCode) throws IOException {
        t.sendResponseHeaders(200, 0);
        OutputStream output = t.getResponseBody();
        FileInputStream fs = new FileInputStream(file);
        byte[] bytes = new byte[0x10000];
        int count = 0;
        while((count = fs.read(bytes)) >= 0) {
            output.write(bytes, 0, count);
        }

        output.flush();
        output.close();
        fs.close();
    }
}
