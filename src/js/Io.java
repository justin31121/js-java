package js;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.zip.*;
import java.lang.reflect.*;

public class Io {

    public static void FAIL(final String message) {
	println(message);
	exit(1);
    }

    public static String repeat(int n, Object o) {
	if(n<1) return "";
	StringBuilder builder = new StringBuilder();
	for(int i=0;i<n;i++) builder.append(String.valueOf(o));
	return builder.toString();
    }

    public static void exit(int exitCode) {
	System.exit(exitCode);
    }

    public static void todo() {
	throw new RuntimeException("TODO");
    }

    public static void todo(String message) {
	throw new RuntimeException("TODO: "+message);
    }
    
    public static void _assert(boolean condition, Object message) {
	if(!condition) {
	    if(message != null) {
		throw new RuntimeException(String.valueOf(message));
	    } else {
		throw new RuntimeException();
	    }
	}
    }

    public static void _assert(boolean condition) {
	_assert(condition, null);
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

    public static void println(Object ...os) {
	for(int i=0;i<os.length;i++) {
	    print(os[i]);
	    print(' ');
	}
	print("\n");
    }

    //ZIP
    public static class Blob {
	public final byte[] data;
	public final String name;

	public Blob(byte[] data, String name) {
	    this.data = data;
	    this.name = name;
	}

	public Blob(byte[] data) {
	    this.data = data;
	    this.name = null;
	}
    }

    public static List<Blob> unzip(byte[] zippedBytes) throws IOException {
	List<Blob> blobs = new ArrayList<>();

	ZipInputStream zipStream = new ZipInputStream(new ByteArrayInputStream(zippedBytes));
	ZipEntry zipEntry = null;
	while((zipEntry = zipStream.getNextEntry()) != null) {
	    if(!zipEntry.isDirectory()) {
		blobs.add(new Blob(toBytes(zipStream, false), zipEntry.getName()));
	    }
	}
	zipStream.close();

	return blobs;
    }

    

    //READ FILE
    private static interface Function<T> {
	void operation(String line, T t);
    }

    private static <T> T forEachLine(String path, Function<T> f, T t) throws IOException {
	BufferedReader reader =
	    new BufferedReader(new InputStreamReader(new FileInputStream(path), StandardCharsets.UTF_8));

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

    public static String _readFile(String path) throws IOException {
	StringBuilder builder = new StringBuilder();

	Function<StringBuilder> f = (line, acc) -> {
	    acc.append(line);
	    acc.append('\n');
	};

	return forEachLine(path, f, builder).toString();
    }

    public static String readFile(String path) throws IOException {
	BufferedReader reader =
	    new BufferedReader(new InputStreamReader(new FileInputStream(path), StandardCharsets.UTF_8));

        StringBuilder sb = new StringBuilder();
	String line;
	while((line = reader.readLine()) != null) {
	    sb.append(line)
		.append("\n");
	}
	reader.close();

	return sb.toString();
    }

    public static String concat(Object ...os) {
	return join("", os);
    }

    public static String join(String delimiter, Object ...os) {
	if(os.length == 1) return String.valueOf(os[0]);
	StringBuilder builder = new StringBuilder(String.valueOf(os[0]));
	for(int i=1;i<os.length;i++) {
	    builder.append(delimiter)
		.append(String.valueOf(os[i]));
	}
	return builder.toString();
    }

    public static void writeObject(OutputStream os, Object o) throws IOException {
	ObjectOutputStream oos = new ObjectOutputStream(os);
	oos.writeObject(o);
	oos.close();
    }
    
    public static void writeObject(String filePath, Object o) throws IOException {
        writeObject(new FileOutputStream(filePath), o);
    }

    public static Object readObject(String filePath) throws IOException, ClassNotFoundException {
	return readObject(new FileInputStream(filePath));
    }

    public static Object readObject(InputStream is) throws IOException, ClassNotFoundException {
	ObjectInputStream ios = new ObjectInputStream(is);
	Object o = ios.readObject();
	ios.close();
	return o;
    }

    public static <T extends OutputStream> T writeTo(InputStream is, T os)  throws IOException {

	int n;
	byte[] buffer = new byte[4096];
	while((n = is.read(buffer, 0, buffer.length)) != -1) {
	    os.write(buffer, 0, n);
	}
	os.flush();

	return os;
    }

    public static byte[] toBytes(InputStream is, boolean close) throws IOException {
	ByteArrayOutputStream baos = new ByteArrayOutputStream();

	int n;
	byte[] buffer = new byte[4096];
	while((n = is.read(buffer, 0, buffer.length)) != -1) {
	    baos.write(buffer, 0, n);
	}
	baos.flush();
	byte[] data = baos.toByteArray();
	baos.close();
	if(close) is.close();
	return data;
    }

    public static byte[] toBytes(InputStream is) throws IOException {
	return toBytes(is, true);
    }

    public static byte[] toBytes(OutputStream os, boolean close) throws IOException {
	ByteArrayOutputStream baos = new ByteArrayOutputStream();
	baos.writeTo(os);
	byte[] data = baos.toByteArray();
	baos.close();
	if(close) os.close();
	return data;
    }

    public static byte[] toBytes(OutputStream os) throws IOException {
	return toBytes(os, true);
    }

    public static void writeFile(final String filePath, final String content) throws IOException {
	String[] parts = filePath.split("/");
	
	StringBuilder builder = new StringBuilder();
	for(int i=0;i<parts.length-1;i++) builder.append(parts[i]).append("/");
	new File(builder.toString()).mkdirs();
	
	final BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filePath), StandardCharsets.UTF_8));
	writer.write(content);
	writer.close();
    }

    public static void writeFile(final String filePath, final byte[] bytes) throws IOException {
	String[] parts = filePath.split("/");
	
	StringBuilder builder = new StringBuilder();
	for(int i=0;i<parts.length-1;i++) builder.append(parts[i]).append("/");
	new File(builder.toString()).mkdirs();
	
	FileOutputStream outputStream = new FileOutputStream(new File(filePath));
	outputStream.write(bytes);
	outputStream.close();
    }

    public static byte[] slurpFile(final String filePath) throws IOException {
	File file = new File(filePath);
	byte[] bytes = new byte[(int) file.length()];
	FileInputStream inputStream = new FileInputStream(file);
	inputStream.read(bytes);
	inputStream.close();
	return bytes;
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

    // ================================================================================================

    private static final Map<Class<?>, Class<?>> WRAPPER_TYPE_MAP;
    static {
	WRAPPER_TYPE_MAP = new HashMap<Class<?>, Class<?>>(16);
	WRAPPER_TYPE_MAP.put(Integer.class, int.class);
	WRAPPER_TYPE_MAP.put(Byte.class, byte.class);
	WRAPPER_TYPE_MAP.put(Character.class, char.class);
	WRAPPER_TYPE_MAP.put(Boolean.class, boolean.class);
	WRAPPER_TYPE_MAP.put(Double.class, double.class);
	WRAPPER_TYPE_MAP.put(Float.class, float.class);
	WRAPPER_TYPE_MAP.put(Long.class, long.class);
	WRAPPER_TYPE_MAP.put(Short.class, short.class);
	WRAPPER_TYPE_MAP.put(Void.class, void.class);
    }

    public static boolean isPrimitive(Class<?> c) {
	return WRAPPER_TYPE_MAP.containsKey(c) || WRAPPER_TYPE_MAP.containsValue(c);
    }

    public static boolean isPrimitive(Object object) {
	return isPrimitive(object.getClass());
    }

    public static boolean implementsInterface(Class<?> clazz, Class<?> interfaceClass) {
	return interfaceClass.isAssignableFrom(clazz);
    }

    public static boolean implementsInterface(Object object, Class<?> interfaceClass) {
	return implementsInterface(object.getClass(), interfaceClass);
    }

    public static interface Serializer {
	void appendNull(StringBuilder sb);
	void appendPrimitive(StringBuilder sb, Object primitive);
	void appendString(StringBuilder sb, String string);
	void appendList(StringBuilder sb, Iterator<?> iterator);
	void appendObject(StringBuilder sb, Object object, Field[] fields);
    }

    public static final void appendSerialized(Serializer serializer, StringBuilder sb, Object object) {
	
	if(object == null) {
	    serializer.appendNull(sb);
	    return;
	}

	Class<?> clazz = object.getClass();
	if(isPrimitive(clazz)) {
	    serializer.appendPrimitive(sb, object);
	    return;
	}
	
	if(clazz.equals(java.lang.String.class)) {
	    serializer.appendString(sb, (String) object);
	    return;
	}

	if(implementsInterface(object, java.lang.Iterable.class)) {
	    serializer.appendList(sb, ((Iterable) object).iterator());
	    return;
	}

	serializer.appendObject(sb, object, clazz.getFields());
    }

    public static class XmlSerializer implements Serializer {
	public void appendNull(StringBuilder sb) {
	    sb.append("NULL");
	}
	
	public void appendPrimitive(StringBuilder sb, Object primitive) {
	    sb.append(primitive);
	}
	
	public void appendString(StringBuilder sb, String string) {
	    sb.append(string);
	}
	
	public void appendList(StringBuilder sb, Iterator<?> iterator) {
	    sb.append("<items>");
	    while(iterator.hasNext()) {
		appendSerialized(this, sb, iterator.next());
	    }
	    sb.append("</items>");
	}
	
	public void appendObject(StringBuilder sb, Object object, Field[] fields) {

	    String objectName = object.getClass().getSimpleName();
	    sb.append("<item>");
	    
	    for(Field field : fields) {

		String name = field.getName();
		if(name.equals("serialVersionUID") && field.getModifiers() == 25) {
		    continue;
		}	    
		
		Object fieldObject;
		try {
		    fieldObject = field.get(object);
		} catch(IllegalAccessException e) {
		    e.printStackTrace();
		    continue;
		}

		sb.append("<")
		    .append(name)
		    .append(">");
		appendSerialized(this, sb, fieldObject);
		sb.append("</")
		    .append(name)
		    .append(">");
		
	    }

	    sb.append("</item>");
	}
    }

    public static void appendEscapeForXml(StringBuilder sb, String content) {
	
	for(int i=0;content!=null && i<content.length();i++) {
	    char c = content.charAt(i);
	    switch(c) {
	    case '\"':
		sb.append("&quot;");
		break;
	    case '\'':
		sb.append("&apos;");
		break;
	    case '<':
		sb.append("&lt;");
		break;
	    case '>':
		sb.append("&gt;");
		break;
	    case '&':
		sb.append("&amp;");
		break;
	    default:
		sb.append(c);
		break;
	    }
	}
	
    }

    public static class CsvSerializer implements Serializer {

	public final String delim;

	public CsvSerializer(String delim) {
	    this.delim = delim;
	}
	
	public void appendNull(StringBuilder sb) {
	    sb.append("NULL");
	}
	
	public void appendPrimitive(StringBuilder sb, Object primitive) {
	    sb.append(primitive);
	}
	
	public void appendString(StringBuilder sb, String string) {
	    sb.append("\"")
		.append(string)
		.append("\"");
	}
	
	public void appendList(StringBuilder sb, Iterator<?> iterator) {
	    while(iterator.hasNext()) {
		appendSerialized(this, sb, iterator.next());
		sb.append("\n");
	    }
	}
	
	public void appendObject(StringBuilder sb, Object object, Field[] fields) {
	    int length = sb.length();
	    
	    for(Field field : fields) {

		String name = field.getName();
		if(name.equals("serialVersionUID") && field.getModifiers() == 25) {
		    continue;
		}
		
	        Object fieldObject;
		try {
		    fieldObject = field.get(object);
		} catch(IllegalAccessException e) {
		    e.printStackTrace();
		    continue;
		}

		appendSerialized(this, sb, fieldObject);
		sb.append(delim);
	    }
	    if(sb.length() > length) {
		sb.setLength(sb.length() - 1);
	    }
	    
	}
    }

    public static class JsonSerializer implements Serializer {
	public void appendNull(StringBuilder sb) {
	    sb.append("null");
	}
	
	public void appendPrimitive(StringBuilder sb, Object primitive) {
	    sb.append(primitive);
	}

	public void appendString(StringBuilder sb, String string) {
	    sb.append("\"");
	    appendEscapeForJson(sb, string);
	    sb.append("\"");
	}
	
	public void appendList(StringBuilder sb, Iterator<?> iterator) {
	    sb.append("[");
	    while(iterator.hasNext()) {
		appendSerialized(this, sb, iterator.next());
		sb.append(",");
	    }
	    sb.setLength(sb.length() - 1);
	    sb.append("]");
	}
	
	public void appendObject(StringBuilder sb, Object object, Field[] fields) {
	    sb.append("{");
	    int length = sb.length();
	    for(Field field : fields) {
	    
		String name = field.getName();
		if(name.equals("serialVersionUID") && field.getModifiers() == 25) {
		    continue;
		}	    

		Object fieldObject;
		try {
		    fieldObject = field.get(object);
		} catch(IllegalAccessException e) {
		    e.printStackTrace();
		    continue;
		}	    
	    
		sb.append("\"");
		appendEscapeForJson(sb, name);
		sb.append("\":");
		appendSerialized(this, sb, fieldObject);
		sb.append(",");    
	    }
	    if(sb.length() > length) {
		sb.setLength(sb.length()-1);
	    }
	    sb.append("}");
	}
    }

    public static void appendEscapeForJson(StringBuilder sb, String content) {
	for(int j=0;content!=null && j<content.length();j++) {
	    char c = content.charAt(j);
	    switch(c) {
	    case '\n':
		sb.append("\\n");
		break;
	    case '\r':
		sb.append("\\n");
		break;
	    case '\"':
		sb.append("\\\"");
		break;
	    case '\t':
		sb.append("\\t");
		break;
	    case '/':
		sb.append("\\/");
		break;
	    case '\\':
		sb.append("\\\\");
		break;
	    case '\b':
		sb.append("\\b");
		break;
	    case '\f':
		sb.append("\\f");
		break;
	    default:
		sb.append(c);
		break;
	    }
	}	
    }

}
