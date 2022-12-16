package js;

import java.io.IOException;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.File;

import java.util.List;
import java.util.ArrayList;

public class Io {

    public static void exit(int exitCode) {
	System.exit(exitCode);
    }

    //PRINT
    public static void print(Object o) {
	Object[] os = null;
	try{
	    os = getBoxed(o);
	}
	catch(IOException io) {
	    os = (Object[] ) o;
	}
	catch(Exception e) {
	    
	}

	//Not an primitive
	if(os==null) {
	    System.out.print(o);
	    return;
	}
	
	print(os);
	
    }

    public static void print(Object[] os) {
	print("[");
	for(int i=0;i<os.length;i++) {
	    print(os[i]);
	    if(i!=os.length-1) {
		print(", ");
	    }
	}
	print("]");
    }

    public static void println(Object o) {	
	print(o);
	print("\n");
    }

    //READ FILE
    private static interface Function<T> {
	void operation(String line, T t);
    }

    private static <T> T forEachLine(String path, Function<T> f, T t) throws IOException {
	BufferedReader reader = new BufferedReader(new FileReader(path));

	String line;
	while((line = reader.readLine())!=null) {
	    f.operation(line, t);
	}

	return t;
    }

    private static <T> T forEachLine(File file, Function<T> f, T t) throws IOException {
	return forEachLine(file.getAbsolutePath(), f, t);
    }

    public static List<String> readFileByLine(String path) throws IOException {
	ArrayList<String> lines = new ArrayList<String>();

	Function<ArrayList<String>> f = (line, acc) -> {
	    acc.add(line);
	};
	
	return forEachLine(path, f, lines);
    }

    public static String readFile(String path) throws IOException {
	StringBuilder builder = new StringBuilder();

	Function<StringBuilder> f = (line, acc) -> {
	    acc.append(line);
	    acc.append('\n');
	};

	return forEachLine(path, f, builder).toString();
    }

    /*
    //COLLECT
    private static String collectWords(Object[] words, String delim) {
	StringBuilder builder = new StringBuilder();

	for(int i=0;i<words.length;i++) {
	    builder.append(words[i]);
	    if(i!=words.length-1) {
		builder.append(delim);
	    }
	}
	builder.append('\n');

	return builder.toString();
    }

    private static <T> String collectWords(List<T> words, String delim) {
	StringBuilder builder = new StringBuilder();

	int n = words.size();
	int i = 0;

	for(Object word : words) {
	    builder.append(word);
	    if(i!=n-1) {
		builder.append(delim);		
	    }
	    i++;
	}
	builder.append('\n');
	
	return builder.toString();
    }

    //WRITEFILE

    private static File writeFile(String path, String o, boolean append) throws IOException {
	BufferedWriter writer = new BufferedWriter(new FileWriter(path, append));
	writer.write(o.toString());
	writer.close();
	return new File(path);
    }

    public static File writeFile(String path, Object o, String delim, boolean append) throws IOException {
	Object[] os = null;
	try{
	    os = getBoxed(o);
	}
	catch(IOException io) {
	    os = (Object[] ) o;
	}
	catch(Exception e) {
	    
	}

	if(os==null) {
	    return writeFile(path, o.toString(), append);	    
	}
	else {
	    return writeFile(path, os, delim, append);
	}
    }

    public static File writeFile(String path, Object[] os, String delim, boolean append) throws IOException {
	return writeFile(path, collectWords(os, delim), append);
    }

    public static <T> File writeFile(String path, List<T> os, String delim, boolean append) throws IOException {
	return writeFile(path, collectWords(os, delim), append);
    }

    //WRITEFILE - OVERLOADING

    public static File writeFile(File file, Object o, String delim, boolean append) throws IOException {
	return writeFile(file.getAbsolutePath(), o, delim, append);
    }
											     
    public static File writeFile(File file, Object[] os, String delim, boolean append) throws IOException {
	return writeFile(file.getAbsolutePath(), collectWords(os, delim), append);
    }

    public static <T> File writeFile(File file, List<T> os, String delim, boolean append) throws IOException {
	return writeFile(file.getAbsolutePath(), collectWords(os, delim), append);
    }
    */

    //EXIST

    public static boolean existDir(String path) {
	File file = new File(path);
	return file.exists() && file.isDirectory();
    }

    public static boolean existFile(String path) {
	File file = new File(path);
	return file.exists() && !file.isDirectory();
    }

    //CREATE

    public static File createDir(File dir, String fileName) throws IOException {
	File file = new File(dir.getAbsolutePath()+"/"+fileName);
	if(file.exists()) {
	    throw new IOException("Dir already exists");
	}
	file.mkdirs();
	return file;
    }
    
    public static File createFile(File dir, String fileName) throws IOException {
	File file = new File(dir.getAbsolutePath()+"/"+fileName);
	if(file.exists()) {
	    throw new IOException("File already exists");
	}
	file.createNewFile();
	return file;
    }

    public static File createDir(String path) throws IOException {
	File file = new File(path);
	if(file.exists()) {
	    throw new IOException("Dir already exists");
	}
	file.mkdirs();
	return file;
    }
    
    public static File createFile(String path) throws IOException {
	File file = new File(path);
	if(file.exists()) {
	    throw new IOException("File already exists");
	}
	file.createNewFile();
	return file;
    }

    //DELETE

    public static void deleteDir(File dir) throws IOException {
	for(File file : dir.listFiles()) {
	    file.delete();
	} 
	dir.delete();	
    }

    public static void deleteDir(String path) throws IOException {
	File dir = new File(path);
	deleteDir(dir);
    }
    
    public static void deleteFile(File file) throws IOException {
	file.delete();
    }

    public static void deleteFile(String path) throws IOException {
	File file = new File(path);
	deleteFile(file);
    }

    
    //BOXING
    private static Object[] getBoxed(Object o) throws IOException, Exception {

	String wholeType = o.getClass().getSimpleName();
	
	int len = wholeType.length();
	String end = wholeType.substring(len-2, len);
	
	if(!"[]".equals(end)) {
	    throw new Exception("Is not an primitive array");
	}

	String type = wholeType.substring(0, len-2);
	
	switch(type) {
	case "byte":
	    byte[] bytesN = (byte[]) o;
	    Byte[] bytes = new Byte[bytesN.length];
	    for(int i=0;i<bytesN.length;i++) {
		bytes[i] = bytesN[i];
	    }
	    return bytes;
	case "short":
	    short[] shortsN = (short[]) o;
	    Short[] shorts = new Short[shortsN.length];
	    for(int i=0;i<shortsN.length;i++) {
		shorts[i] = shortsN[i];
	    }
	    return shorts;
	case "int":
	    int[] intsN = (int[]) o;
	    Integer[] ints = new Integer[intsN.length];
	    for(int i=0;i<intsN.length;i++) {
		ints[i] = intsN[i];
	    }
	    return ints;
	case "long":
	    long[] longsN = (long[]) o;
	    Long[] longs = new Long[longsN.length];
	    for(int i=0;i<longsN.length;i++) {
		longs[i] = longsN[i];
	    }
	    return longs;
	case "float":
	    float[] floatsN = (float[]) o;
	    Float[] floats = new Float[floatsN.length];
	    for(int i=0;i<floatsN.length;i++) {
		floats[i] = floatsN[i];
	    }
	    return floats;
	case "double":
	    double[] doublesN = (double[]) o;
	    Double[] doubles = new Double[doublesN.length];
	    for(int i=0;i<doublesN.length;i++) {
		doubles[i] = doublesN[i];
	    }
	    return doubles;
	case "boolean":
	    boolean[] booleansN = (boolean[]) o;
	    Boolean[] booleans = new Boolean[booleansN.length];
	    for(int i=0;i<booleansN.length;i++) {
		booleans[i] = booleansN[i];
	    }
	    return booleans;
	case "char":
	    char[] charsN = (char[]) o;
	    Character[] chars = new Character[charsN.length];
	    for(int i=0;i<charsN.length;i++) {
		chars[i] = charsN[i];
	    }
	    return chars;
	default:
	    throw new IOException("Try again");
	}
    }
}
