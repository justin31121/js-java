package js.conn;

import java.io.*;
import java.net.*;
import javax.net.ssl.*;
import javax.net.*;

public class Conn implements Closeable {
    private static final short HTTP_PORT  =  80;
    private static final short HTTPS_PORT = 443;

    final String host;
    final short port;
    final boolean ssl;

    protected final boolean variyingPort;

    private final Socket socket;
    protected final OutputStream out;
    protected final InputStream in;
       	
    public Conn(String host, short port, boolean ssl) throws IOException {
	this.host = host;
	this.port = port;
	this.ssl = ssl;

	if(ssl) {
	    variyingPort = port != HTTPS_PORT;
	    SocketFactory sslSocketFactory = SSLSocketFactory.getDefault();
	    socket = (SSLSocket) sslSocketFactory.createSocket(host, port);
	} else {
	    variyingPort = port != HTTP_PORT;
	    socket = new Socket(host, port);
	}

	out = new BufferedOutputStream(socket.getOutputStream());
	in = new BufferedInputStream(socket.getInputStream());
    }

    public Conn(String host, boolean ssl) throws IOException {
	this(host, ssl ? HTTPS_PORT : HTTP_PORT, ssl);
    }
    
    public Request prepare(String method, String path) {
	return new Request(this, method, path);
    }


    public Conn(String host) throws IOException {
	this(host, HTTPS_PORT, true);
    }

    @Override
    public void close() throws IOException {
	out.close();
	in.close();
	socket.close();
    }

}
