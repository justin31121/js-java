package js.conn;

import java.io.*;
import java.util.*;

public class Result {
    public final int responseCode;
    public final boolean ok;
    public final InputStream inputStream;
    public final Map<String, String> headers;
    public final long len;

    protected Result(int responseCode, long len, Map<String, String> headers, InputStream inputStream) {
	this.ok = 100 <= responseCode && responseCode <= 399;
	this.responseCode = responseCode;
	this.len = len;
	this.headers = headers;
	this.inputStream = inputStream;
    }
}
