package tests;

import static js.Io.*;
import js.monad.*;
import static js.monad.Maybe.*;

class MaybeTest {
    public static void main(String[] args) {

	Maybe<Integer> m = just(34);
	
	println("Hello, MaybeTest");
	println(m);
    }
}
