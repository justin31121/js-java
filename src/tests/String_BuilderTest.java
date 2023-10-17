package tests;

import js.string.*;

class String_BuilderTest {
    public static void main(String[] args) {
	String_Builder sb = new String_Builder();

	sb.append("Hello, World!");

	System.out.println(sb.toString());
    }
}
