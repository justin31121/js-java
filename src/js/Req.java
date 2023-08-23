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

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.Base64;
import java.util.ArrayList;

import java.net.Socket;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509ExtendedTrustManager;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

import java.io.*;

public class Req {

    private static final String USER_AGENT =
	"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/78.0.3904.108 Safari/537.36";
    
    private static final String FORM_DATA_CONTENT_TYPE =
	"multipart/form-data;boundary=*****";
    private static final String FORM_DATA_TWO_HYPHENS = "--";
    private static final String FORM_DATA_BOUNDARY = "*****";
    private static final String FORM_DATA_CRLF = "\r\n";

    // Disconnects connection to HttpURLConnection, after InputStream is closed
    private static class Stream extends BufferedInputStream {

	private final HttpURLConnection connection;

	public Stream(InputStream inputStream, HttpURLConnection connection) {
	    super(inputStream);
	    this.connection = connection;
	}

	@Override
	public void close() throws IOException {
	    super.close();
	    connection.disconnect();
	}
    }

    public static class Result {
	public final int responseCode;
	public final boolean ok;
	public final byte[] data;
	public final long len;
	public final Map<String, List<String>> headers;
	public final InputStream inputStream;

	private Result(final int responseCode, final boolean ok,
		       final byte[] data, final long len,
		       final Map<String, List<String>> headers) {
	    this.responseCode = responseCode;
	    this.ok = ok;
	    this.data = data;
	    this.inputStream = null;
	    this.len = len;
	    this.headers = headers;
	}

	private Result(final int responseCode, final boolean ok,
		       final InputStream inputStream, final long len,
		       final Map<String, List<String>> headers) {
	    this.responseCode = responseCode;
	    this.ok = ok;
	    this.data = null;
	    this.inputStream = inputStream;
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
	    
	    try{
		byte[] buf = new byte[8192];
		int n = 0;
		while(-1!=(n=in.read(buf))) {
		    out.write(buf, 0, n);
		}
	    } catch(IOException e) {
		
	    }

	    out.close();
	    in.close();
	    connection.disconnect();

	    byte[] bytes = out.toByteArray();

	    return new Result(responseCode, ok, bytes,
			      bytes.length,
			      connection.getHeaderFields());
	}
	
	static Result lazyFrom(final HttpURLConnection connection)
	    throws IOException
	{
	    if(connection == null) return null;

	    int responseCode = connection.getResponseCode();
	    boolean ok = isOk(responseCode);
	    final InputStream in;
	    if(ok) {
		in = new Stream(connection.getInputStream(), connection);
	    } else {
		in = new Stream(connection.getErrorStream(), connection);
	    }
	    //connection.disconnect();

	    return new Result(responseCode, ok, in,
			      connection.getContentLengthLong(),
			      connection.getHeaderFields());
	}
    }

    public static class Settings {
	List<String> entries;
	
	public Settings() {
	    entries = new ArrayList<>();
	}

	public Settings auth(final String auth, final String token) {
	    if(auth == null) return null;
	    if(token == null) return null;
	    entries.add("Authorization");
	    entries.add(Io.concat(auth, " ", token));
	    return this;
	}

	public Settings basicAuth(final String token) {
	    if(token == null) return null;
	    entries.add("Authorization");
	    entries.add(Io.concat("Basic ", new String(Base64.getEncoder().encode(token.getBytes(StandardCharsets.UTF_8)))));
	    return this;
	}

	public Settings set(String key, String value) {
	    if(key == null) return null;
	    if(value == null) return null;
	    entries.add(key);
	    entries.add(value);
	    return this;
	}
    }

    public static class Builder {
	private final HttpURLConnection connection;
	private byte[] bytes;
	private String fileName;
	private String attachmentName;
	
	Builder(final HttpURLConnection connection) {
	    this.connection = connection;
	    this.bytes = null;
	}

	Builder(final Settings settings, final HttpURLConnection connection) {	    
	    this.connection = connection;
	    this.bytes = null;
	    for(int i=0;i<settings.entries.size();i+=2) {
		final String key = settings.entries.get(i);
		final String value = settings.entries.get(i+1);
		connection.setRequestProperty(key, value);
	    }
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
	    this.fileName = null;
	    this.attachmentName = null;
	    return this;
	}

	public Builder formData(final byte[] bytes, final String fileName, final String attachmentName) {
	    if(bytes == null) return null;
	    if(fileName == null) return null;
	    if(attachmentName == null) return null;
	    this.bytes = bytes;
	    this.fileName = fileName;
	    this.attachmentName = attachmentName;
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

	private boolean prepareBuild() throws IOException {
	    if(connection == null) return false;
	    if(bytes != null) {
		if(fileName != null && attachmentName != null) {
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
		} else {
		    connection.setDoOutput(true);
		    final DataOutputStream dos = new DataOutputStream(connection.getOutputStream());
		    dos.write(bytes);
		    dos.flush();
		    dos.close();
		}
	    }
	    return true;
	}

	public Result lazyBuild() throws IOException {
	    if(!prepareBuild()) {
		return null;
	    }
	    return Result.lazyFrom(connection);	    
	}

	public Result build()
	    throws IOException
	{
	    if(!prepareBuild()) {
		return null;
	    }
	    return Result.from(connection);
	}
    }

    public static final Settings settings() {
	return new Settings();
    }

    public static final Builder builder(final Settings settings, final String url, final String method)
	throws IOException
	{
	    return new Builder(settings, basicConnection(url, method));
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

    public static final Result request(final Settings settings, final String url, final String method)
	throws IOException
    {
	return new Builder(settings, basicConnection(url, method)).build();
    }

    public static final Result head(final Settings settings, final String url)
	throws IOException
	{
	    return request(settings, url, "HEAD");
	}

    public static final Result head(final String url)
	throws IOException
    {
	return Result.from(basicConnection(url, "HEAD"));
    }

    public static final Result get(final Settings settings, final String url)
	throws IOException
    {
	return request(settings, url, "GET");
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

    public static final String stringUtf8(final byte[] data) {
	if(data == null) return null;
	return new String(data, StandardCharsets.UTF_8);
    }

    public static final byte[] bytesUtf8(final String data) {
	if(data == null) return null;
	return data.getBytes(StandardCharsets.UTF_8);
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

    public static void allowMethods(String... methods) {
        try {
            Field methodsField = HttpURLConnection.class.getDeclaredField("methods");

            Field modifiersField = Field.class.getDeclaredField("modifiers");
            modifiersField.setAccessible(true);
            modifiersField.setInt(methodsField, methodsField.getModifiers() & ~Modifier.FINAL);

            methodsField.setAccessible(true);

            String[] oldMethods = (String[]) methodsField.get(null);
            Set<String> methodsSet = new LinkedHashSet<>(Arrays.asList(oldMethods));
            methodsSet.addAll(Arrays.asList(methods));
            String[] newMethods = methodsSet.toArray(new String[0]);

            methodsField.set(null/*static field*/, newMethods);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new IllegalStateException(e);
        }
    }

    public static void disableSSL () {
	TrustManager [] trustAllCerts = new TrustManager [] {new X509ExtendedTrustManager () {
		@Override
		public void checkClientTrusted (X509Certificate [] chain, String authType, Socket socket) {

		}

		@Override
		public void checkServerTrusted (X509Certificate [] chain, String authType, Socket socket) {

		}

		@Override
		public void checkClientTrusted (X509Certificate [] chain, String authType, SSLEngine engine) {

		}

		@Override
		public void checkServerTrusted (X509Certificate [] chain, String authType, SSLEngine engine) {

		}

		@Override
		public java.security.cert.X509Certificate [] getAcceptedIssuers () {
		    return null;
		}

		@Override
		public void checkClientTrusted (X509Certificate [] certs, String authType) {
		}

		@Override
		public void checkServerTrusted (X509Certificate [] certs, String authType) {
		}

	    }};

	SSLContext sc = null;
	try {
	    sc = SSLContext.getInstance ("SSL");
	    sc.init (null, trustAllCerts, new java.security.SecureRandom ());
	} catch (Exception e) {
	    e.printStackTrace ();
	}
	HttpsURLConnection.setDefaultSSLSocketFactory (sc.getSocketFactory());
    }
}
