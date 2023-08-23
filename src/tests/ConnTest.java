package tests;

import java.net.*;
import javax.net.ssl.*;
import javax.net.*;
import java.io.*;
import java.util.*;

import js.conn.*;

public class ConnTest {

    static void panic(String mess) {
	System.err.println("ERORR: "+mess);
	System.exit(1);
    }

    public static void main1(String[] args) throws IOException {
	Conn conn = new Conn("localhost", (short) 8080, false);

	Result result = conn.prepare("GET", "/")
	    .headers(true)
	    .submit();
	if(!result.ok) {
	    panic("request failed");
	}
	System.out.println(result.responseCode);
	System.out.println(result.headers);

	byte[] buf = new byte[1024];
	int n;
	while( (n = result.inputStream.read(buf) ) != -1 ) {
	    System.out.println(new String(buf, 0, n));
	}
	
	conn.close();

    }

    public static void main2(String[] args) throws IOException {
	Conn conn = new Conn("www.example.com", true);

	Result result = conn.prepare("GET", "/").submit();
	if(!result.ok) {
	    panic("request failed");
	}

	byte[] buf = new byte[1024];
	int n;
	while( (n = result.inputStream.read(buf) ) != -1 ) {
	    System.out.println(new String(buf, 0, n));
	}
	
	conn.close();
    }


    public static void main(String[] args) throws IOException {
	
	Conn conn = new Conn("httpdump.app", true);

	byte[] buf = new byte[1024];
	int n;

	{
	    Request request =
		conn.prepare("GET", "/")
		.headers(true);
	    
	    Result result =request.submit();
	    
	    System.out.println(result.headers);
	    while((n = result.inputStream.read(buf)) != - 1) {
		System.out.println(new String(buf, 0, n));
	    }

	    if(!result.ok) {
		panic("Request has failed");
	    }
	}

	{
	    String payload = "{\"key\":\"value\"}";

	    Request request =
		conn.prepare("POST", "/dumps/22a79211-2032-4fad-aa6c-5f1e480d83d9")
		.set("Content-Type", "application/json")
		.headers(true);

	    OutputStream outputStream = request.getOutputStream();
	    outputStream.write(payload.getBytes());
	    outputStream.flush();
	    
	    Result result = request.submit();
	    
	    System.out.println(result.headers);
	    while((n = result.inputStream.read(buf)) != - 1) {
		System.out.println(new String(buf, 0, n));
	    }

	    if(!result.ok) {
		panic("Request has failed");
	    }
	}
	
	conn.close();
    }
    
}
