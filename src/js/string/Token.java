package js.string;

public class Token {
    public final int off;
    public final int len;

    public Token(int off, int len) {
	this.off = off;
	this.len = len;
    }

    public static boolean isDelim(char c) {
	return c == ',' || c== ' ' || c== '.';
    }

    @Override
    public String toString() {
	return "Token { off::"+off+" len::"+len+" }";
    }
}
