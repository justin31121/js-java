package js.monad;

public class List<T> {
    private T t;
    private List<T> next;

    private List(T t, List<T> next) {
	this.t = t;
	this.next = next;
    }

    @Override
    public String toString() {
	String acc = "[";
	List<T> current = this;
	boolean empty = current.isEmpty();
	while(true) {	    
	    acc += empty ? "]" : current.t.toString();
	    current = current.next;
	    if(empty) break;
	    empty = current.isEmpty();
	    if(!empty) acc +=", ";
	}
	return acc;
    }

    public final boolean isEmpty() {
	return next==null;
    }

    public static final <T> List<T> cons(T t, List<T> ts) {
	return new List<T>(t, ts);
    }

    public static final <T> List<T> empty() {
	return new List<>(null, null);
    }

    public static final List<Integer> listOf(int ...args) {
	List<Integer> begin = empty();
	for(int i=args.length-1;i>=0;--i) begin = cons(args[i], begin);
	return begin;
    }
}
