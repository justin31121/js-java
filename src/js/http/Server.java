package js.http;

import javax.net.ssl.SSLContext;
import java.security.KeyStore;
import java.io.FileInputStream;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLParameters;

import com.sun.net.httpserver.HttpsServer;
import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpExchange;
import java.net.InetSocketAddress;
import com.sun.net.httpserver.HttpsConfigurator;
import com.sun.net.httpserver.HttpsParameters;
import java.security.NoSuchAlgorithmException;
import java.security.KeyStoreException;
import java.security.KeyManagementException;
import java.security.cert.CertificateException;
import java.security.UnrecoverableKeyException;

import java.util.concurrent.Executors;
import java.io.IOException;
import java.io.FileNotFoundException;

import java.util.HashMap;
import java.util.Map;

import static js.http.Util.*;

public class Server {

    private static final int HTTP_PORT  = 80;
    private static final int HTTPS_PORT = 443;

    private final String pathToKeyStore;
    private int threads = 1;
    private int port = -1;
    private HttpServer server;
    private boolean running;
    private HashMap<String, HttpHandler> httpHandlers = new HashMap<String, HttpHandler>();
    private HashMap<String, MetaHandler> handlers = new HashMap<String, MetaHandler>();

    public Server() {
	running = false;
	pathToKeyStore = null;
    }

    public Server(final String pathToKeyStore) {
	this.pathToKeyStore = pathToKeyStore;
    }

    public void setPort(int port) {
	this.port = port;
    }

    public int getPort() {
	return port;
    }

    public void setThreads(int threads) {
	this.threads = threads;
    }

    public int getThreads() {
	return threads;
    }    

    public final void serve(final String context, final String path) {
	httpHandlers.put(context, new FileHandler(path));
    }
    
    public final void serve(final String context, final HttpHandler handler) {
	httpHandlers.put(context, handler);
    }

    public final void serve(final String context, final String method, final HttpHandler handlerF) {
	MetaHandler handler = handlers.get(context);
	if(handler==null) {
	    handler = new MetaHandler(context);
	    handlers.put(context, handler);
	}

	handler.set(method, handlerF);
    }

    private static final com.sun.net.httpserver.HttpHandler modify(final HttpHandler handler) {
	return new com.sun.net.httpserver.HttpHandler() {
	    public void handle(HttpExchange t) throws IOException {
		HttpResult result;
		try{
		    result = handler.handle(t);
		}
		catch(HttpException e) {
		    e.printStackTrace();
		    result = e.toHttpResult();
		}
		catch(Exception ex) {
		    ex.printStackTrace();
		    result = internalError();
		}
		respond(t, result);
	    }
	};
    }
    
    public final boolean start() {
	try{
	    if(pathToKeyStore != null) {
		if(port==-1) port = HTTPS_PORT;
		HttpsServer https = HttpsServer.create(new InetSocketAddress(port), 0);
		ssl(https, pathToKeyStore);
		server = https;
	    }
	    else {
		if(port==-1) port = HTTP_PORT;
		server = HttpServer.create(new InetSocketAddress(port), 0);
	    }
	}
	catch(Exception e) {
	    return false;
	}

	for(Map.Entry<String, MetaHandler> entry : handlers.entrySet()) {
	    MetaHandler meta = entry.getValue();
	    HttpHandler handler = meta.get();
	    meta = null;
	    server.createContext(entry.getKey(), modify(handler));
	}
	handlers = null;
	
	for(Map.Entry<String, HttpHandler> entry : httpHandlers.entrySet()) {
	    server.createContext(entry.getKey(), modify(entry.getValue()));
	}
	httpHandlers = null;

	server.setExecutor(Executors.newFixedThreadPool(threads));
	server.start();

	running = true;
	return true;
    }

    private static void ssl(HttpsServer server, String path)
	throws NoSuchAlgorithmException, KeyStoreException, FileNotFoundException,
	       CertificateException, KeyManagementException, UnrecoverableKeyException, IOException
    {
        SSLContext sslContext = SSLContext.getInstance("TLS");

        char[] password = "simulator".toCharArray();
        KeyStore ks = KeyStore.getInstance("JKS");
        FileInputStream fis = new FileInputStream(path);
        ks.load(fis, password);

        KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
        kmf.init(ks, password);

        TrustManagerFactory tmf = TrustManagerFactory.getInstance("SunX509");
        tmf.init(ks);

        sslContext.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);
        server.setHttpsConfigurator(new HttpsConfigurator(sslContext) {
		public void configure(HttpsParameters params) {
		    try{
			SSLContext c = SSLContext.getDefault();
			SSLEngine engine = c.createSSLEngine();
			params.setNeedClientAuth(false);
			//Params.setCipherSuites(engine.getEnabledCipherSuites());
			params.setProtocols(engine.getEnabledProtocols());

			SSLParameters defaultSSLParameters = c.getDefaultSSLParameters();
			params.setSSLParameters(defaultSSLParameters);
		    }
		    catch(Exception ex) {
			System.out.println("Failed to create HTTPS port");
		    }
		}
            });
    }

    public final void stop() {
	if(!running) return;
	server.stop(1);
	running = false;
    }
}
