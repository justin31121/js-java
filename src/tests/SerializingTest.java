package tests;

import static js.Io.*;
import static js.Std.*;
import js.string.*;

class SerializingTest {

    public static class Point {
	public final int x;
	public final int y;

	public Point(int x, int y) {
	    this.x = x;
	    this.y = y;
	}
    }
    
    public static void main(String[] args) {

	Point p = new Point(69, 420);

	println(str(p));
	println(str(p, JsonSerializer));
	println(str(p, XmlSerializer));
	println(str(p, CsvSerializer));

	String_Builder sb = new String_Builder();
	sb.append("This ")
	    .append(p, JsonSerializer)
	    .append(" is a Point");
	println(sb);
    }
    
}
