package js;

import js.string.*;
import java.util.*;
import java.lang.reflect.*;

public class Std {

    private static class PrimitiveTypeAndWrapper {	
	public final Class wrapper;
	public final Class primitive;

	public PrimitiveTypeAndWrapper(Class wrapper, Class primitive) {	    
	    this.wrapper = wrapper;
	    this.primitive = primitive;
	}
    }

    private static PrimitiveTypeAndWrapper[] primitiveTypes = {
	new PrimitiveTypeAndWrapper(Integer.class, int.class),
	new PrimitiveTypeAndWrapper(Byte.class, byte.class),
	new PrimitiveTypeAndWrapper(Character.class, char.class),
	new PrimitiveTypeAndWrapper(Boolean.class, boolean.class),
	new PrimitiveTypeAndWrapper(Double.class, double.class),
	new PrimitiveTypeAndWrapper(Float.class, float.class),
	new PrimitiveTypeAndWrapper(Long.class, long.class),
	new PrimitiveTypeAndWrapper(Short.class, short.class),
	new PrimitiveTypeAndWrapper(Void.class, void.class),
    };

    public static boolean isPrimitive(Class<?> c) {
	for(PrimitiveTypeAndWrapper types : primitiveTypes) {
	    if(types.wrapper.equals(c) ||
	       types.primitive.equals(c)) {
		return true;
	    }
	}
        return false;
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
   
    public static class Serializer {

	public static enum Type {
	    NULL,
	    PRIMITIVE,
	    STRING,
	    ITERABLE,
	    OBJECT,
	}

	public static interface Function {
	    void serialize(String_Builder sb,
			   Type type, 
			   Object object,
			   String string,
			   Iterator<?> iterator,
			   Field[] fields);
	}
		
    }

    public static void serialize(Object object,
				 String_Builder sb,
				 Serializer.Function f) {
	if(object == null) {
	    f.serialize(sb,
			Serializer.Type.NULL,
			null,
			null,
			null,
			null);
	    return;
	}

	Class<?> clazz = object.getClass();
	if(isPrimitive(clazz)) {
	    f.serialize(sb,
			Serializer.Type.PRIMITIVE,
			object,
			null,
			null,
			null);
	    return;
	}
	
	if(clazz.equals(java.lang.String.class)) {
	    f.serialize(sb,
			Serializer.Type.STRING,
			null,
		        (String) object,
			null,
			null);
	    return;
	}

	if(implementsInterface(object, java.lang.Iterable.class)) {
	    f.serialize(sb,
			 Serializer.Type.ITERABLE,
			 null,
			 null,
			 ((Iterable) object).iterator(),
			 null);
	    return;
	}

	f.serialize(sb,
		    Serializer.Type.OBJECT,
		    object,
		    null,
		    null,
		    clazz.getFields());
    }

    public static boolean isFieldInternal(Field field) {
	return
	    field.getName().equals("serialVersionUID") &&
	    field.getModifiers() == 25;
    }

    public static void jsonEscape(String_Builder sb,
				  String content) {
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
    
    public static Serializer.Function JsonSerializer = Std::jsonSerializerImpl;
    public static void jsonSerializerImpl(String_Builder sb,
					  Serializer.Type type,
					  Object object,
					  String string,
					  Iterator<?> iterator,
					  Field[] fields) {

	switch(type) {
	case NULL:
	    sb.append("null");
	    break;
	case PRIMITIVE:
	    sb.append(object);
	    break;
	case STRING:
	    sb.append("\"");
	    jsonEscape(sb, string);
	    sb.append("\"");
	    break;
	case ITERABLE:
	    sb.append("[");
	    while(iterator.hasNext()) {
		serialize(iterator.next(),
			  sb,
			  Std::jsonSerializerImpl);
		sb.append(",");
	    }
	    sb.setLength(sb.length() - 1);
	    sb.append("]");
	    break;
	case OBJECT:
	    sb.append("{");
	    int length = sb.length();
	    for(Field field : fields) {
		
		if(isFieldInternal(field)) {
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
		jsonEscape(sb, field.getName());
		sb.append("\":");
	        serialize(fieldObject, sb, Std::jsonSerializerImpl);
		sb.append(",");
	    }
	    if(sb.length() > length) {
		sb.setLength(sb.length()-1);
	    }
	    sb.append("}");

	    break;
	}
    }

    public static void xmlEscape(String_Builder sb,
				 String content) {
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

    public static Serializer.Function XmlSerializer = Std::xmlSerializerImpl;
    public static void xmlSerializerImpl(String_Builder sb,
					  Serializer.Type type,
					  Object object,
					  String string,
					  Iterator<?> iterator,
					  Field[] fields) {
	switch(type) {
	case NULL:
	    sb.append("NULL");
	    break;
	case PRIMITIVE:
	    sb.append(object);
	    break;
	case STRING:
	    xmlEscape(sb, string);
	    break;
	case ITERABLE:
	    sb.append("<items>");
	    while(iterator.hasNext()) {
		serialize(iterator.next(), sb, Std::xmlSerializerImpl);
	    }
	    sb.append("</items>");
	    break;
	case OBJECT:
	    sb.append("<item>");	    
	    for(Field field : fields) {

		if(isFieldInternal(field)) {
		    continue;
		}

		Object fieldObject;
		try {
		    fieldObject = field.get(object);
		} catch(IllegalAccessException e) {
		    e.printStackTrace();
		    continue;
		}

		String name = field.getName();
		sb.append("<")
		    .append(name)
		    .append(">");
		serialize(fieldObject, sb, Std::xmlSerializerImpl);
		sb.append("</")
		    .append(name)
		    .append(">");
	    }
	    sb.append("</item>");
	    break;
	}
    }

    public static Serializer.Function CsvSerializer = Std::csvSerializerImpl;
    public static void csvSerializerImpl(String_Builder sb,
					  Serializer.Type type,
					  Object object,
					  String string,
					  Iterator<?> iterator,
					  Field[] fields) {
	switch(type) {
	case NULL:
	    sb.append("NULL");
	    break;
	case PRIMITIVE:
	    sb.append(object);
	    break;
	case STRING:
	    sb.append("\"")
		.append(string)
		.append("\"");
	    break;
	case ITERABLE:
	    while(iterator.hasNext()) {
		serialize(iterator.next(), sb, Std::csvSerializerImpl);
		sb.append("\n");
	    }
	    break;
	case OBJECT:
	    int length = sb.length();	    
	    for(Field field : fields) {
		if(isFieldInternal(field)) {
		    continue;
		}
		
	        Object fieldObject;
		try {
		    fieldObject = field.get(object);
		} catch(IllegalAccessException e) {
		    e.printStackTrace();
		    continue;
		}

		serialize(fieldObject, sb, Std::csvSerializerImpl);
		sb.append(";");
	    }
	    if(sb.length() > length) {
		sb.setLength(sb.length() - 1);
	    }
	    break;
	}
    }

    private static String_Builder strStringBuilder = null;
    private static String_Builder getStrStringBuilder() {
	if(strStringBuilder == null) {
	    strStringBuilder = new String_Builder();
	}	
	return strStringBuilder;
    }

    public static String str(Object object,
			     Serializer.Function f) {
	String_Builder sb = getStrStringBuilder();
	serialize(object, sb, f);
        return sb.toStringFlush();
    }

    public static String str(Object object) {
	return str(object, JsonSerializer);
    }

    public static boolean stringEq(String a, String b) {
	if(a == null && b == null) {
	    return true;
	}
	if(a == null || b == null) {
	    return false;
	}
	
	return a.equals(b);
    }

    public static interface Eq<T> {
	boolean eq(T a, T b);
    }

    public static <T> int indexOf(Iterable<T> haystack, T needle, Eq<T> f) {
	int i = 0;
	for(T t : haystack) {
	    if(f.eq(needle, t)) {
		return i;
	    }
	    i++;
	}
	return -1;
    }

}
