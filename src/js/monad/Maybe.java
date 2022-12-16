package js.monad;

import java.util.function.Function;

public class Maybe<T> implements Functor<T>, Monad<T> {

    private final boolean isJust;
    private final T t;

    private Maybe(T t, boolean isJust) {
	this.t = t;
	this.isJust = isJust;
    }

    public final <V> Maybe<V> bind(Function<T, Monad<V>> f) {
	if(isJust) return (Maybe<V>) f.apply(t);
	else return nothing();
    }

    public final Maybe<T> pure(T t) {
	return just(t);
    }

    public final <V> Maybe<V> fmap(Function<T,V> f) {
	if(isJust) return just(f.apply(t));
	else return nothing();
    }

    @Override
    public String toString() {
	if(isJust) return "Just "+t.toString();
	else return "Nothing";
    }

    public final T get() {
	if(!isJust) throw new RuntimeException("Can not unpack Nothing");
	return t;
    }

    public final T getNull() {
	return t;
    }

    public final boolean isJust() {
	return isJust;
    }

    public final boolean isNothing() {
	return !isJust;
    }

    public static final <T> Maybe<T> tryy(Producer<T> p) {
	try{
	    return just(p.produce());
	}
	catch(Exception e) {
	    return nothing();
	}
    }
    
    public static final <T> Maybe<T> just(T t) {
	Maybe<T> m = new Maybe<T>(t, t!=null);
	return m;
    }

    public static final <T> Maybe<T> nothing() {
	Maybe<T> m = new Maybe<T>(null, false);
	return m;
    }
}
