package js.monad;

import java.util.function.Function;

public interface Monad<T> {
    public <V> Monad<V> bind(Function<T, Monad<V>> f);   
}
