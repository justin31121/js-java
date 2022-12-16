package js.monad;

public interface FoldrFunction<K, V> {
    V fold(K k, V v);
}
