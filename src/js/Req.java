package js;

import java.net.HttpURLConnection;
import javax.net.ssl.HttpsURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;
import java.util.List;
import java.net.URLDecoder;
import java.net.URLEncoder;

import java.io.*;

public class Req {

    private static final String USER_AGENT =
	"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/78.0.3904.108 Safari/537.36";
    
    private static final String FORM_DATA_CONTENT_TYPE =
	"multipart/form-data;boundary=*****";
    private static final String FORM_DATA_TWO_HYPHENS = "--";
    private static final String FORM_DATA_BOUNDARY = "*****";
    private static final String FORM_DATA_CRLF = "\r\n";

    public static class Result {
	public final int responseCode;
	public final boolean ok;
	public final byte[] data;
	public final long len;
	public final Map<String, List<String>> headers;

	private Result(final int responseCode, final boolean ok,
		       final byte[] data, final long len,
		       final Map<String, List<String>> headers) {
	    this.responseCode = responseCode;
	    this.ok = ok;
	    this.data = data;
	    this.len = len;
	    this.headers = headers;
	}

	static Result from(final HttpURLConnection connection)
	    throws IOException
	{
	    if(connection == null) return null;

	    final ByteArrayOutputStream out = new ByteArrayOutputStream();
	    int responseCode = connection.getResponseCode();
	    boolean ok = isOk(responseCode);
	    final InputStream in;
	    if(ok) {
		in = new BufferedInputStream(connection.getInputStream());
	    } else {
		in = new BufferedInputStream(connection.getErrorStream());
	    }
	    byte[] buf = new byte[8192];
	    int n = 0;
	    while(-1!=(n=in.read(buf))) {
		out.write(buf, 0, n);
	    }
	    out.close();
	    in.close();
	    connection.disconnect();

	    return new Result(responseCode, ok, out.toByteArray(),
			      connection.getContentLengthLong(),
			      connection.getHeaderFields());
	}
    }

    public static class Builder {
	private final HttpURLConnection connection;
	private byte[] bytes;
	
	Builder(final HttpURLConnection connection) {
	    this.connection = connection;
	    this.bytes = null;
	}

	public Builder set(final String key, final String value) {
	    if(connection == null) return this;
	    if(key == null) return null;
	    if(value == null) return null;
	    connection.setRequestProperty(key, value);
	    return this;
	}

	public Builder body(final byte[] bytes) {
	    if(bytes == null) return null;
	    this.bytes = bytes;
	    return this;
	}

	public Builder auth(final String auth, final String token) {
	    if(connection == null) return this;
	    if(auth == null) return null;
	    if(token == null) return null;
	    connection.setRequestProperty("Authorization", Io.concat(auth, " ", token));
	    return this;
	}

	public Builder basicAuth(final String token) {
	    if(connection == null) return this;
	    if(token == null) return null;
	    connection.setRequestProperty("Authorization",
					  Io.concat("Basic ", new String(Base64.getEncoder().encode(token.getBytes(StandardCharsets.UTF_8)))));
	    return this;
	}

	public Result build()
	    throws IOException
	{
	    if(connection == null) return null;
	    if(bytes != null) {
		connection.setDoOutput(true);
		final DataOutputStream dos = new DataOutputStream(connection.getOutputStream());
		dos.write(bytes);
		dos.flush();
		dos.close();
	    }
	    return Result.from(connection);
	}
    }

    public static final Builder builder(final String url, final String method)
	throws IOException
    {
	return new Builder(basicConnection(url, method));
    }

    public static final Result request(final String url, final String method)
	throws IOException
    {
	return Result.from(basicConnection(url, method));
    }

    public static final Result head(final String url)
	throws IOException
    {
	return Result.from(basicConnection(url, "HEAD"));
    }

    public static final Result get(final String url)
	throws IOException
    {
	return Result.from(basicConnection(url, "GET"));
    }

    public static final Result post(final String url, final byte[] bytes, final String contentType)
	throws IOException
    {
	if(bytes == null) return null;
	if(contentType == null) return null;
	final HttpURLConnection connection = basicConnection(url, "POST");
	if(connection == null) return null;
	//CONTENT TYPE
	connection.setRequestProperty("Content-Type", contentType);
	//CONTENT
	connection.setDoOutput(true);

	final DataOutputStream dos = new DataOutputStream(connection.getOutputStream());
	dos.write(bytes);
	dos.flush();
	dos.close();

	return Result.from(connection);
    }

    public static final Result postFormData(final String url, final byte[] bytes, final String fileName, final String attachmentName)
	throws IOException
    {
	if(bytes == null) return null;
	if(fileName == null) return null;
	if(attachmentName == null) return null;
	final HttpURLConnection connection = basicConnection(url, "POST");
	if(connection == null) return null;
	connection.setRequestProperty("Content-Type", FORM_DATA_CONTENT_TYPE);
	connection.setDoOutput(true);
	final DataOutputStream dos = new DataOutputStream(connection.getOutputStream());
	dos.writeBytes(Io.concat(FORM_DATA_TWO_HYPHENS, FORM_DATA_BOUNDARY, FORM_DATA_CRLF));
	dos.writeBytes(Io.concat("Content-Disposition: form-data; name=\"", attachmentName, "\";filename=\"", fileName, "\"", FORM_DATA_CRLF));
	dos.writeBytes(FORM_DATA_CRLF);
	dos.write(bytes);
	dos.writeBytes(FORM_DATA_CRLF);
	dos.writeBytes(Io.concat(FORM_DATA_TWO_HYPHENS, FORM_DATA_BOUNDARY, FORM_DATA_TWO_HYPHENS, FORM_DATA_CRLF));
	dos.flush();
	dos.close();
	return Result.from(connection);
    }

    public static final boolean isOk(int responseCode) {
	return 100 <= responseCode && responseCode <= 399;
    }

    public static final String utf8(final byte[] data) {
	if(data == null) return null;
	return new String(data, StandardCharsets.UTF_8);
    }

    public static final String decode(final String string) {
	if(string == null) return null;
	String result = null;
	try {
            result = URLDecoder.decode(string, StandardCharsets.UTF_8.toString());
        } catch (Exception e) {
            result = string;
        }
	return result;
    }

    public static final String encode(final String string) {
	if(string == null) return null;
	String result = null;
	try{
	    result = URLEncoder.encode(string, "UTF-8")
		.replaceAll("\\+", "%20")
		.replaceAll("\\%21", "!")
		.replaceAll("\\%27", "'")
		.replaceAll("\\%28", "(")
		.replaceAll("\\%29", ")")
		.replaceAll("\\%7E", "~");
	} catch (Exception e) {
	    result = string;
	}
	return result;
    }

    private static final HttpURLConnection basicConnection(final String url, final String method)
	throws IOException
    {
	if(url == null) return null;
	if(method == null) return null;
	final HttpURLConnection connection;
	if(url.indexOf("https://") == 0) {
	    connection = (HttpsURLConnection) new URL(url).openConnection();
	} else if(url.indexOf("http://") == 0) {
	    connection = (HttpURLConnection) new URL(url).openConnection();
	} else {
	    return null;
	}
	
	connection.setRequestProperty("User-Agent", USER_AGENT);
	connection.setRequestMethod(method);

	return connection;
    }
}
