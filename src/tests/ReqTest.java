package tests;

import static js.Io.*;
import js.Req;

class ReqTest {
    public static void main(String[] args) throws Exception {
	final Req req = new Req();

	final String response = req.get("https://www.example.com");
	if(req.failed()) {
	    println(req.getResMessage()+" "+req.getResCode());
	    exit(1);
	}

	println(response);
    }
}
