package js.string;

import java.util.stream.*;

// https://developer.classpath.org/doc/java/lang/StringBuilder-source.html

// java.lang.StringBuilder
// + public String_Builder append(CharSequence, int, int)
// + bigger DEFAULT_CAP
public class String_Builder implements CharSequence {

    private static final int DEFAULT_CAP = 1024;
    
    private char[] data;
    private int len;

    @Override
    public int length() {
	return len;
    }

    @Override
    public char charAt(int i) {
	if(i < 0 || i >= len) {
	    throw new IndexOutOfBoundsException("Index :"+i+", Size: "+len);
	}

	return data[i];
    }

    @Override
    public IntStream chars() {
	return toString().chars();
    }

    @Override
    public IntStream codePoints() {
	return toString().chars();
    }

    @Override
    public CharSequence subSequence(int start, int end) {
	return toString().subSequence(start, end);
    }

    public void setLength(int len) {
	if(len < 0 || len > this.len) {
	    throw new RuntimeException("Can not extend len: "+this.len+" to: "+len);
	}

	this.len = len;
    }

    public String_Builder append(char c) {
	if(len == data.length) {
	    char[] new_data = new char[data.length * 2];
	    for(int i=0;i<data.length;i++) new_data[i] = data[i];
	    data = new_data;
	}
	data[len++] = c;

	return this;
    }	

    public String_Builder append(CharSequence seq) {
	for(int i=0;i<seq.length();i++) {
	    append(seq.charAt(i));
	}

	return this;
    }

    public String_Builder append(CharSequence seq, int off, int len) {
	for(int i=0;i<len;i++) {
	    append(seq.charAt(off + i));
	}
	    
	return this;
    }

    public String_Builder append(Object object) {
	return append(String.valueOf(object));
    }

    public String_Builder(int cap) {
	data = new char[cap];
	len = 0;
    }

    public String_Builder() {
	this(DEFAULT_CAP);
    }

    @Override
    public String toString() {
	return new String(data, 0, len);
    }

}
