package js.monad;

import java.util.function.Function;

public interface Functor<T> {
    public <V> Functor<V> fmap(Function<T, V> f);
    public Functor<T> pure(T t);
}
