package js.monad;

public interface FoldlFunction<V, K> {
    V fold(V v, K k);
}
