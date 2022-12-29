package js;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.BufferedInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.Socket;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.Base64;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509ExtendedTrustManager;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

/**
 * @author jschartner
 *
 */
public class Req
{
    private final String basicAuth;

    private final String crlf = "\r\n";
    private final String twoHyphens = "--";
    private final String boundary = "*****";

    private boolean logging = true;
    private boolean failed = false;
    private String cookies = null;
    private String apiKey = null;
    private String xcsrf = null;
    
    private int responseCode;
    private String responseMessage;
    private Map<String, List<String>> responseHeaders;

    private Map<String, String> requestHeaders;

    public Req() {
	logging = false;
	responseCode = -1;
	responseMessage = null;
	basicAuth = null;
	HttpsURLConnection.setFollowRedirects(false);
	requestHeaders = null;
    }
    
    public Req(boolean logging) {
	this.logging = logging;
	responseCode = -1;
	responseMessage = null;
	basicAuth = null;
	HttpsURLConnection.setFollowRedirects(false);
	requestHeaders = null;
    }
    
    public Req(String basicAuth) {
	logging = false;
	responseCode = -1;
	responseMessage = null;
	final byte[] encodedAuth = Base64.getEncoder().encode(basicAuth.getBytes(StandardCharsets.UTF_8));
	this.basicAuth = "Basic " + new String(encodedAuth);
	HttpsURLConnection.setFollowRedirects(false);
	requestHeaders = null;
    }

    public Req(String basicAuth, boolean logging) {
	this.logging = logging;
	responseCode = -1;
	responseMessage = null;
	final byte[] encodedAuth = Base64.getEncoder().encode(basicAuth.getBytes(StandardCharsets.UTF_8));
	this.basicAuth = "Basic " + new String(encodedAuth);
	HttpsURLConnection.setFollowRedirects(false);
	requestHeaders = null;
    }

    public boolean failed() {
	return failed;
    }

    public int getResCode()
    {
	return responseCode;
    }

    public String getResMessage()
    {
	return responseMessage;
    }

    public Map<String, List<String>> getResHeaders() {
	return responseHeaders;
    }

    public boolean getLogging()
    {
	return logging;
    }

    public Req setLogging(final boolean logging)
    {
	this.logging = logging;
	return this;
    }

    public String getCookies() {
	return cookies;
    }

    public Req setCookies(String cookies) {
	this.cookies = cookies;
	return this;
    }

    public Req resetCookies() {
	cookies = null;
	return this;
    }

    public String getXcsrf() {
	return xcsrf;
    }

    public Req setXcsrf(String xcsrf) {
	this.xcsrf = xcsrf;
	return this;
    }

    public Req resetXcsrf() {
	xcsrf = null;
	return this;
    }

    public String getApiKey() {
	return apiKey;
    }

    public Req setApiKey(String apiKey) {
	this.apiKey = apiKey;
	return this;
    }

    public Req resetApiKey() {
	apiKey = null;
	return this;
    }

    public Req addRequestHeader(String key, String value) {
	if(requestHeaders == null) {
	    requestHeaders = new HashMap<String, String>();
	}
	requestHeaders.put(key, value);
	return this;
    }

    public Req clearRequestHeader(String key) {
	if(requestHeaders == null) return this;
	requestHeaders.remove(key);
	if(requestHeaders.size() == 0) requestHeaders = null;
	return this;
    }

    public Req clearRequestHeaders() {
	requestHeaders = null;
	return this;
    }

    public Map<String, String> getRequestHeaders() {
	return requestHeaders;
    }

    public byte[] requestBytes(final String urlString, final String method)
	throws MalformedURLException, ProtocolException, IOException, UnsupportedEncodingException
    {
	final HttpURLConnection con = getClient(urlString, method, null);

	return responseOutputBytes(con);
    }

    public String get(final String urlString)
	throws MalformedURLException, ProtocolException, IOException, UnsupportedEncodingException
    {
    	return request(urlString, "GET");
    }

    public byte[] getBytes(final String urlString)
	throws MalformedURLException, ProtocolException, IOException, UnsupportedEncodingException
    {
	return requestBytes(urlString, "GET");
    }

    public String delete(final String urlString)
	throws MalformedURLException, ProtocolException, IOException, UnsupportedEncodingException
    {
	return request(urlString, "DELETE");
    }

    public String post(final String urlString,
		       final File file, final String fileName, final String attachmentName)
	throws MalformedURLException, ProtocolException, IOException, UnsupportedEncodingException
    {
	return request(urlString, "POST", file, fileName, attachmentName);
    }

    public String post(final String urlString, final String body, final String contentType)
	throws MalformedURLException, ProtocolException, IOException, UnsupportedEncodingException
    {
	return request(urlString, "POST", body, contentType);
    }
    
    public String request(final String urlString, final String method)
	throws MalformedURLException, ProtocolException, IOException, UnsupportedEncodingException
    {
    	final HttpURLConnection con = getClient(urlString, method, null);
	return responseOutput(con);
    }

    public String request(final String urlString, final String method,
			  final File file, final String fileName, final String attachmentName)
	throws MalformedURLException, ProtocolException, IOException
    {
	final HttpURLConnection con = getClient(urlString, method, "multipart/form-data;boundary=*****");
	con.setUseCaches(false);
	con.setDoOutput(true);
	con.setRequestProperty("Cache-Control", "no-cache");

	//BODY
	final DataOutputStream request = new DataOutputStream(con.getOutputStream());
	request.writeBytes(twoHyphens + boundary + crlf);
	request.writeBytes("Content-Disposition: form-data; name=\"" + attachmentName + "\";filename=\"" + fileName + "\"" + crlf);
	request.writeBytes(crlf);

	//Image
	final byte[] bytes = Files.readAllBytes(file.toPath());

	request.write(bytes);

	request.writeBytes(crlf);
	request.writeBytes(twoHyphens + boundary + twoHyphens + crlf);
	request.flush();
	request.close();

	return responseOutput(con);
    }

    public String request(final String urlString, final String method, final String body, final String contentType)
	throws MalformedURLException, ProtocolException, UnsupportedEncodingException, IOException
    {
	final HttpURLConnection con = getClient(urlString, method, contentType);

	//BODY
	if (body != null && body.length() > 0)
	    {
		con.setDoOutput(true);
		final OutputStream os = con.getOutputStream();
		final OutputStreamWriter osw = new OutputStreamWriter(os, "UTF-8");
		osw.write(body);
		osw.flush();
		osw.close();
		os.close();
	    }

	if (logging)
	    {
		System.out.println("\t" + body);
	    }

	return responseOutput(con);
    }

    private static final String toParametersString(final Map<String, String> parameters) {
	if(parameters == null) return "";
	if(parameters.size() == 0) return "";
	final StringBuilder builder = new StringBuilder("?");
	int count = 0;
	for(Map.Entry<String, String> entry : parameters.entrySet()) {
	    builder.append(entry.getKey())
		.append("=")
		.append(entry.getValue());
	    if(count != parameters.size() - 1) {
		builder.append("&");
	    }
	    count++;
	}
	return builder.toString();
    }

    private String responseOutput(HttpURLConnection con) throws UnsupportedEncodingException, IOException {
	con.connect();

	responseCode = con.getResponseCode();
	responseMessage = con.getResponseMessage();

	if(logging) {
	    System.out.print("\t" + responseCode);
	    if(responseMessage!=null) {
		System.out.print(" " + responseMessage);
	    }
	    System.out.println("");
 	}

	final StringBuilder builder = new StringBuilder();
	BufferedReader br;
	InputStreamReader isr;
	if (100 <= responseCode && responseCode <= 399) {
	    isr = new InputStreamReader(con.getInputStream(), "UTF-8");
	    failed = false;
	}
	else {
	    isr = new InputStreamReader(con.getErrorStream(), "UTF-8");
	    failed = true;
	}
	br = new BufferedReader(isr);

	String line;
	while ((line = br.readLine()) != null) {
	    builder.append(line + "\n");
	}

	responseHeaders = con.getHeaderFields();

	con.disconnect();
	return builder.toString();
    }

    private byte[] responseOutputBytes(HttpURLConnection con) throws IOException {
	con.connect();

	responseCode = con.getResponseCode();
	responseMessage = con.getResponseMessage();

	if(logging) {
	    System.out.print("\t" + responseCode);
	    if(responseMessage!=null) {
		System.out.print(" " + responseMessage);
	    }
	    System.out.println("");
 	}

	final ByteArrayOutputStream out = new ByteArrayOutputStream();
	final InputStream in;
	if (100 <= responseCode && responseCode <= 399) {
	    in = new BufferedInputStream(con.getInputStream());
	    failed = false;
	}
	else {
	    in = new BufferedInputStream(con.getErrorStream());
	    failed = true;
	}
	
	byte[] buf = new byte[1024];
	int n = 0;
	while(-1!=(n=in.read(buf))) {
	    out.write(buf, 0, n);
	}
	out.close();
	in.close();

	responseHeaders = con.getHeaderFields();

	con.disconnect();

	return out.toByteArray();
    }
    
    private HttpURLConnection getClient(final String url, final String method, final String contentType) throws MalformedURLException, IOException, ProtocolException
    {
	HttpURLConnection client;
	if (url.length() >= 5 && url.toCharArray()[4] == 's')
	    {
		client = (HttpsURLConnection) new URL(url.replace(" ", "%20")).openConnection();
	    }
	else
	    {
		client = (HttpURLConnection) new URL(url.replace(" ", "%20")).openConnection();
	    }

	client.setRequestProperty("User-Agent",
				  "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/78.0.3904.108 Safari/537.36");

	//client.setRequestProperty("User-Agent", "PostmanRuntime/7.29.2");

	client.setRequestProperty("Connection", "Keep-Alive");
	client.setUseCaches(true);
	client.setRequestMethod(method);

	if(requestHeaders != null) {
	    for(Map.Entry<String, String> entry : requestHeaders.entrySet()) {
		client.setRequestProperty(entry.getKey(), entry.getValue());
	    }
	}

	//clientTENT TYPE
	if (contentType != null && contentType.length() > 0)
	    {
		client.setRequestProperty("Content-Type", contentType);
	    }

	//COOKIES
	if (cookies != null && cookies.length() > 0)
	    {
		client.setRequestProperty("Cookie", cookies);
	    }

	if (basicAuth != null)
	    {
		client.setRequestProperty("Authorization", basicAuth);
	    }

	//ApiKey
	if (apiKey != null && apiKey.length() > 0)
	    {
		client.setRequestProperty("x-api-key", apiKey);
	    }

	//xcsrf
	if(xcsrf!=null && xcsrf.length() > 0) {
	    client.setRequestProperty("X-CSRF-TOKEN", xcsrf);
	}

	return client;
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
	} catch (KeyManagementException | NoSuchAlgorithmException e) {
	    e.printStackTrace ();
	}
	HttpsURLConnection.setDefaultSSLSocketFactory (sc.getSocketFactory());
    }
}
