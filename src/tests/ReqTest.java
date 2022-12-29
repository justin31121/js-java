package tests;

import static js.Io.*;
import js.Req;

class ReqTest {
    public static void main(String[] args) throws Exception {
	Req.Result result = Req.request("https://www.youtube.com/watch?v=jfKfPfyJRdk", "GET");
	assert result != null;
	if(!result.ok) {
	    print(concat("ERROR: Request failed: ", result.headers.get(null)));
	    exit(1);
	}
	if(result.data == null) {
	    print("ERROR: Request has no body");
	    exit(1);	    
	}

	final String body = new String(result.data);
	int end = body.indexOf(".m3u8");

	int i=end;
	while(i>= 0 && body.charAt(i)!=':') i--;

	println(concat("https", body.substring(i, end), ".m3u8"));
    }
}
