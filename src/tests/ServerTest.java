package tests;

import static js.Io.*;

import js.http.*;
import static js.http.Util.*;

import java.io.*;

class ServerTest {
    public static void main(String[] args) throws IOException {

	int port = 8080;
	
	Server server = new Server();
	server.serve("/", "GET", new FileHandler(".\\src\\js"));
	server.setPort(port);

	if(!server.start()) {
	    println("ERORR: Can not start Server");
	    exit(1);
	}

	println("Server running: http://localhost:"+port);
    }
}
