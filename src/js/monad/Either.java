package js.monad;

import java.util.function.Function;

public class Either<T, V> implements Functor<V>, Monad<V> {
    private final boolean isRight;
    private final T left;
    private final V right;

    @Override
    public String toString() {
	if(isRight) return "Right "+right.toString();
	else return "Left "+left.toString();
    }

    private Either(final T left) {
	this.left = left;
	this.right = null;
	this.isRight = false;	
    }

    private Either(final V right, final boolean isRight) {
	this.left = null;
	this.right = right;
	this.isRight = true;
    }

    public final Object get() {
	if(isRight) return right;
	else return left;
    }

    public final boolean isLeft() {
	return !isRight;
    }

    public final boolean isRight() {
	return isRight;
    }

    public final T getLeft() {
	if(isRight) throw new RuntimeException("Can unpack Left when Either is Right");
	return left;
    }

    public final T getLeftNull() {
	return left;
    }

    public final V getRight() {
	if(!isRight) throw new RuntimeException("Can unpack Right when Either is Left");
	return right;
    }
    
    public final V getRightNull() {
	return right;
    }

    @SuppressWarnings("unchecked")
    public final <K> Either<T, K> bind(Function<V, Monad<K>> f) {
	if(isRight) return (Either<T, K>) f.apply(right);
	return left(left);
    }

    public final <K> Either<T, K> fmap(Function<V, K> f) {
	if(isRight) return right(f.apply(right));
	else return left(left);
   }

    public final Either<T, V> pure(V v) {
	return right(v);
    }

    public static final <T,V> Either<T, V> left(final T left) {
	return new Either<>(left);
    }

    public static final <T, V> Either<T, V> right(final V right) {
	return new Either<>(right, true);
    }

    public static final <V> Either<Exception, V> catchh(Producer<V> p) {
	try{
	    return right(p.produce());
	}
	catch(Exception e) {
	    return left(e);
	}
    }
}
