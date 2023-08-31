package js.conn;

public class Util {

    static final byte[] CONTENT_LENGTH =
    {'c', 'o', 'n', 't', 'e', 'n', 't',
     '-',
     'l', 'e', 'n', 'g', 't', 'h'
    };

    static final byte[] TRANSFER_ENCODING = {
	't', 'r', 'a', 'n', 's', 'f', 'e', 'r',
	'-',
	'e', 'n', 'c', 'o', 'd', 'i', 'n', 'g'
    };

    static final byte[] CHUNKED = {
	'c', 'h', 'u', 'n', 'k', 'e', 'd'
    };
 
    static final byte[] HTTP1 =
    {'H', 'T', 'T', 'P', '/', '1', '.', '1', ' ' };

    static final byte[] ZERO_RNRN =
    { '0', '\r', '\n', '\r', '\n'};

    
    public static boolean stringifyHex(int n, byte[] buf, int offset, int count) {
	
	if(count == 0) {
	    return false;
	}

	int m = 0;
	while(n > 0) {
	    int f = n % 16;
	    buf[offset + count - m++ - 1] =
		(byte) (f<10 ? f + '0' : f + 'W');
	    n /= 16;
	}
	while(m < count) buf[offset + count - m++ - 1] = '0';

	return true;
    }

    // returns -1 on error
    // returns n >= 0 on success
    public static long parseHexLong(byte[] buf, int offset, int count) {
	if(count == 0) {
	    return -1;
	}

	int k = offset;
	long n = 0;
	for(int i=0;i<count;i++) {
	    byte c = buf[k++];

	    n *= 16;
	    if('0' <= c && c <= '9') {
		n += c - '0';
	    } else if('a' <= c && c <= 'f') {
		n += c - 'W';
	    } else if('A' <= c && c <= 'F') {
		n += c - '7';
	    } else {
		return -1;
	    }
	    
	}
	return n;
    }

    // returns -1 on error
    // returns n >= 0 on success
    public static long parseLong(byte[] buf, int offset, int count) {
	if(count == 0) {
	    return -1;
	}

	int k = offset;
	long n = 0;
	for(int i=0;i<count;i++) {
	    byte c = buf[k++];
	    if(c < '0' || '9' < c) {
		return -1;
	    }
	    n *= 10;
	    n += c - '0';
	}
	return n;
    }

    public static boolean matches(byte[] buf, int offset, int count, byte[] string) {

	if(string.length != count ) {
	    return false;
	}

	int k = offset;
	int j = 0;
	for(int i=0;i<count;i++) {
	    byte s = string[j++];
	    byte t = buf[k++];
	    if('a' <= s && s <= 'z' &&
	       'A' <= t && t <= 'Z') {
		t = (byte) (t + ' ');
	    }

	    if(s != t) {
		return false;
	    }
	}

	return true;
    }

}
