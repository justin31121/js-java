package http_test;

import js.http.Server;
import js.http.HttpHandler;
import static js.http.Util.*;
import java.util.Scanner;
import js.Req;
import java.util.Map;

import org.json.*;

class Main {
    private static Req req = new Req();
    
    public static void main(String[] args) {
	Req.disableSSL();
	
	Server server = new Server("./lig.keystore");
	server.serve("/", "./rsc");
	server.serve("/product", "GET", t -> {
		final String route = getRoute(t, "/product");
		System.out.println(route);

		final Map<String, String> parameters = getParameters(t);
		for(Map.Entry<String, String> entry : parameters.entrySet()) {
		    System.out.println(entry.getKey()+": "+entry.getValue());
		}

		final String parametersString = getParametersString(t);
		System.out.println(parametersString);
		
		return ok("Fine");
	    });

	if(!server.start()) System.exit(1);

	try {
	    req.get("https://localhost/product/123456?key=value&key2=value2");
	} catch(Exception e) {
	    e.printStackTrace();
	}
	
	server.stop();
	System.exit(0);
    }
}
