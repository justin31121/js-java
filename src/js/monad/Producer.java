package js.monad;

public interface Producer<T> {
    T produce() throws Exception;
}
