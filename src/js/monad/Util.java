package js.monad;
import java.util.List;
import java.util.ListIterator;
import java.util.function.Function;
import java.util.function.Consumer;
import java.lang.reflect.Array;
import java.lang.reflect.ParameterizedType;
public class Util {
    public static final int head(int[] args) { try{ return args[0]; } catch(Exception e) { throw new RuntimeException("Can not take the head from an empty List"); } }
 public static final byte head(byte[] args) { try{ return args[0]; } catch(Exception e) { throw new RuntimeException("Can not take the head from an empty List"); } }
 public static final short head(short[] args) { try{ return args[0]; } catch(Exception e) { throw new RuntimeException("Can not take the head from an empty List"); } }
 public static final long head(long[] args) { try{ return args[0]; } catch(Exception e) { throw new RuntimeException("Can not take the head from an empty List"); } }
 public static final boolean head(boolean[] args) { try{ return args[0]; } catch(Exception e) { throw new RuntimeException("Can not take the head from an empty List"); } }
 public static final char head(char[] args) { try{ return args[0]; } catch(Exception e) { throw new RuntimeException("Can not take the head from an empty List"); } }
 public static final double head(double[] args) { try{ return args[0]; } catch(Exception e) { throw new RuntimeException("Can not take the head from an empty List"); } }
 public static final float head(float[] args) { try{ return args[0]; } catch(Exception e) { throw new RuntimeException("Can not take the head from an empty List"); } }
    public static final <V> V head(V[] vs) {
 try{
     return vs[0];
 }
 catch(Exception e) {
     throw new RuntimeException("Can not take the head from an empty List");
 }
    }
    public static final <V> V head(List<V> vs) {
 try{
     return vs.get(0);
 }
 catch(Exception e) {
     throw new RuntimeException("Can not take the head from an empty List");
 }
    }
    public static final int[] tail(int[] vs) { if(vs.length==0) throw new RuntimeException("Can not take the head from an empty List"); final int[] _vs = new int[vs.length-1]; for(int i=1;i<vs.length;i++) _vs[i-1] = vs[i]; return _vs; }
 public static final byte[] tail(byte[] vs) { if(vs.length==0) throw new RuntimeException("Can not take the head from an empty List"); final byte[] _vs = new byte[vs.length-1]; for(int i=1;i<vs.length;i++) _vs[i-1] = vs[i]; return _vs; }
 public static final short[] tail(short[] vs) { if(vs.length==0) throw new RuntimeException("Can not take the head from an empty List"); final short[] _vs = new short[vs.length-1]; for(int i=1;i<vs.length;i++) _vs[i-1] = vs[i]; return _vs; }
 public static final long[] tail(long[] vs) { if(vs.length==0) throw new RuntimeException("Can not take the head from an empty List"); final long[] _vs = new long[vs.length-1]; for(int i=1;i<vs.length;i++) _vs[i-1] = vs[i]; return _vs; }
 public static final boolean[] tail(boolean[] vs) { if(vs.length==0) throw new RuntimeException("Can not take the head from an empty List"); final boolean[] _vs = new boolean[vs.length-1]; for(int i=1;i<vs.length;i++) _vs[i-1] = vs[i]; return _vs; }
 public static final char[] tail(char[] vs) { if(vs.length==0) throw new RuntimeException("Can not take the head from an empty List"); final char[] _vs = new char[vs.length-1]; for(int i=1;i<vs.length;i++) _vs[i-1] = vs[i]; return _vs; }
 public static final double[] tail(double[] vs) { if(vs.length==0) throw new RuntimeException("Can not take the head from an empty List"); final double[] _vs = new double[vs.length-1]; for(int i=1;i<vs.length;i++) _vs[i-1] = vs[i]; return _vs; }
 public static final float[] tail(float[] vs) { if(vs.length==0) throw new RuntimeException("Can not take the head from an empty List"); final float[] _vs = new float[vs.length-1]; for(int i=1;i<vs.length;i++) _vs[i-1] = vs[i]; return _vs; }
    @SuppressWarnings("unchecked")
    public static final <V> V[] tail(V[] vs) {
 final Class<?> c = vs.getClass().getComponentType();
 if(vs.length==0) throw new RuntimeException("Can not take the head from an empty List");
 final V[] _vs = (V[]) Array.newInstance(c, vs.length-1);
 for(int i=1;i<vs.length;i++) _vs[i-1] = vs[i];
 return _vs;
    }
    public static final <V> List<V> tail(List<V> vs) {
 final List<V> ls = vs;
 try{
     vs.remove(0);
 }
 catch(Exception e) {
     throw new RuntimeException("Can remove the head from an empty List");
 }
 return ls;
    }
    public static final <V> V foldl(FoldlFunction<V, Integer> f, V v, int[] objs) { for(int i=0;i<objs.length;i++) v = f.fold(v, objs[i]); return v;}
 public static final <V> V foldl(FoldlFunction<V, Byte> f, V v, byte[] objs) { for(int i=0;i<objs.length;i++) v = f.fold(v, objs[i]); return v;}
 public static final <V> V foldl(FoldlFunction<V, Short> f, V v, short[] objs) { for(int i=0;i<objs.length;i++) v = f.fold(v, objs[i]); return v;}
 public static final <V> V foldl(FoldlFunction<V, Long> f, V v, long[] objs) { for(int i=0;i<objs.length;i++) v = f.fold(v, objs[i]); return v;}
 public static final <V> V foldl(FoldlFunction<V, Boolean> f, V v, boolean[] objs) { for(int i=0;i<objs.length;i++) v = f.fold(v, objs[i]); return v;}
 public static final <V> V foldl(FoldlFunction<V, Character> f, V v, char[] objs) { for(int i=0;i<objs.length;i++) v = f.fold(v, objs[i]); return v;}
 public static final <V> V foldl(FoldlFunction<V, Double> f, V v, double[] objs) { for(int i=0;i<objs.length;i++) v = f.fold(v, objs[i]); return v;}
 public static final <V> V foldl(FoldlFunction<V, Float> f, V v, float[] objs) { for(int i=0;i<objs.length;i++) v = f.fold(v, objs[i]); return v;}
    public static final <V, K> V foldl(FoldlFunction<V, K> f, V v, K[] objs) {
 for(int i=0;i<objs.length;i++) v = f.fold(v, objs[i]);
 return v;
    }
    public static final <V, K> V foldl(FoldlFunction<V, K> f, V v, List<K> ks) {
 for(K k : ks) v = f.fold(v, k);
 return v;
    }
    public static final <V> V foldr(FoldrFunction<Integer, V> f, V v, int[] arr) { for(int i=arr.length-1;i>=0;--i) v = f.fold(arr[i], v); return v; }
 public static final <V> V foldr(FoldrFunction<Byte, V> f, V v, byte[] arr) { for(int i=arr.length-1;i>=0;--i) v = f.fold(arr[i], v); return v; }
 public static final <V> V foldr(FoldrFunction<Short, V> f, V v, short[] arr) { for(int i=arr.length-1;i>=0;--i) v = f.fold(arr[i], v); return v; }
 public static final <V> V foldr(FoldrFunction<Long, V> f, V v, long[] arr) { for(int i=arr.length-1;i>=0;--i) v = f.fold(arr[i], v); return v; }
 public static final <V> V foldr(FoldrFunction<Boolean, V> f, V v, boolean[] arr) { for(int i=arr.length-1;i>=0;--i) v = f.fold(arr[i], v); return v; }
 public static final <V> V foldr(FoldrFunction<Character, V> f, V v, char[] arr) { for(int i=arr.length-1;i>=0;--i) v = f.fold(arr[i], v); return v; }
 public static final <V> V foldr(FoldrFunction<Double, V> f, V v, double[] arr) { for(int i=arr.length-1;i>=0;--i) v = f.fold(arr[i], v); return v; }
 public static final <V> V foldr(FoldrFunction<Float, V> f, V v, float[] arr) { for(int i=arr.length-1;i>=0;--i) v = f.fold(arr[i], v); return v; }
    public static final <V, K> V foldr(FoldrFunction<K, V> f, V v, K[] objs) {
 for(int i=objs.length-1;i>=0;--i) v = f.fold(objs[i], v);
 return v;
    }
    public static final <V, K> V foldr(FoldrFunction<K, V> f, V v, List<K> ks) {
 ListIterator<K> iter = ks.listIterator(ks.size());
 while(iter.hasPrevious()) v = f.fold(iter.previous(), v);
 return v;
    }
    public static final int[] filter(Function<Integer, Boolean> f, int[] arr) { int n = 0; final boolean[] valid = new boolean[arr.length]; for(int i=0;i<arr.length;i++) { boolean okay = f.apply(arr[i]); valid[i] = okay; if(okay) n++; } final int[] rs = new int[n]; int p = 0; for(int i=0;i<arr.length;i++) { if(valid[i]) rs[p++] = arr[i]; } return rs;}
 public static final byte[] filter(Function<Byte, Boolean> f, byte[] arr) { int n = 0; final boolean[] valid = new boolean[arr.length]; for(int i=0;i<arr.length;i++) { boolean okay = f.apply(arr[i]); valid[i] = okay; if(okay) n++; } final byte[] rs = new byte[n]; int p = 0; for(int i=0;i<arr.length;i++) { if(valid[i]) rs[p++] = arr[i]; } return rs;}
 public static final short[] filter(Function<Short, Boolean> f, short[] arr) { int n = 0; final boolean[] valid = new boolean[arr.length]; for(int i=0;i<arr.length;i++) { boolean okay = f.apply(arr[i]); valid[i] = okay; if(okay) n++; } final short[] rs = new short[n]; int p = 0; for(int i=0;i<arr.length;i++) { if(valid[i]) rs[p++] = arr[i]; } return rs;}
 public static final long[] filter(Function<Long, Boolean> f, long[] arr) { int n = 0; final boolean[] valid = new boolean[arr.length]; for(int i=0;i<arr.length;i++) { boolean okay = f.apply(arr[i]); valid[i] = okay; if(okay) n++; } final long[] rs = new long[n]; int p = 0; for(int i=0;i<arr.length;i++) { if(valid[i]) rs[p++] = arr[i]; } return rs;}
 public static final boolean[] filter(Function<Boolean, Boolean> f, boolean[] arr) { int n = 0; final boolean[] valid = new boolean[arr.length]; for(int i=0;i<arr.length;i++) { boolean okay = f.apply(arr[i]); valid[i] = okay; if(okay) n++; } final boolean[] rs = new boolean[n]; int p = 0; for(int i=0;i<arr.length;i++) { if(valid[i]) rs[p++] = arr[i]; } return rs;}
 public static final char[] filter(Function<Character, Boolean> f, char[] arr) { int n = 0; final boolean[] valid = new boolean[arr.length]; for(int i=0;i<arr.length;i++) { boolean okay = f.apply(arr[i]); valid[i] = okay; if(okay) n++; } final char[] rs = new char[n]; int p = 0; for(int i=0;i<arr.length;i++) { if(valid[i]) rs[p++] = arr[i]; } return rs;}
 public static final double[] filter(Function<Double, Boolean> f, double[] arr) { int n = 0; final boolean[] valid = new boolean[arr.length]; for(int i=0;i<arr.length;i++) { boolean okay = f.apply(arr[i]); valid[i] = okay; if(okay) n++; } final double[] rs = new double[n]; int p = 0; for(int i=0;i<arr.length;i++) { if(valid[i]) rs[p++] = arr[i]; } return rs;}
 public static final float[] filter(Function<Float, Boolean> f, float[] arr) { int n = 0; final boolean[] valid = new boolean[arr.length]; for(int i=0;i<arr.length;i++) { boolean okay = f.apply(arr[i]); valid[i] = okay; if(okay) n++; } final float[] rs = new float[n]; int p = 0; for(int i=0;i<arr.length;i++) { if(valid[i]) rs[p++] = arr[i]; } return rs;}
 @SuppressWarnings("unchecked")
    public static final <V> V[] filter(Function<V, Boolean> f, V[] objs) {
 int n = 0;
 final boolean[] valid = new boolean[objs.length];
 for(int i=0;i<objs.length;i++) {
     boolean okay = f.apply(objs[i]);
     valid[i] = okay;
     if(okay) n++;
 }
 final Class<?> c = objs.getClass().getComponentType();
 final V[] rs = (V[]) Array.newInstance(c, n);
 int p = 0;
 for(int i=0;i<objs.length;i++) if(valid[i]) rs[p++] = objs[i];
 return rs;
    }
    @SuppressWarnings("unchecked")
    public static final <K> List<K> filter(Function<K, Boolean> f, List<K> ks) {
 final List<K> _ks;
 try{
     _ks = (List<K>) ks.getClass().getConstructor().newInstance();
 }
 catch(Exception e) {
     throw new RuntimeException("Can not construct class");
 }
 for(K k : ks) if(f.apply(k)) _ks.add(k);
 return _ks;
    }
    @SuppressWarnings("unchecked") public static final <V> V[] map(Function<Integer, V> f, int[] arr) { if(arr.length==0) return (V[]) new Object[0]; final Class<?> c = f.apply(arr[0]).getClass(); final V[] vs = (V[]) Array.newInstance(c, arr.length); for(int i=0;i<arr.length;i++) vs[i] = f.apply(arr[i]); return vs; }
 @SuppressWarnings("unchecked") public static final <V> V[] map(Function<Byte, V> f, byte[] arr) { if(arr.length==0) return (V[]) new Object[0]; final Class<?> c = f.apply(arr[0]).getClass(); final V[] vs = (V[]) Array.newInstance(c, arr.length); for(int i=0;i<arr.length;i++) vs[i] = f.apply(arr[i]); return vs; }
 @SuppressWarnings("unchecked") public static final <V> V[] map(Function<Short, V> f, short[] arr) { if(arr.length==0) return (V[]) new Object[0]; final Class<?> c = f.apply(arr[0]).getClass(); final V[] vs = (V[]) Array.newInstance(c, arr.length); for(int i=0;i<arr.length;i++) vs[i] = f.apply(arr[i]); return vs; }
 @SuppressWarnings("unchecked") public static final <V> V[] map(Function<Long, V> f, long[] arr) { if(arr.length==0) return (V[]) new Object[0]; final Class<?> c = f.apply(arr[0]).getClass(); final V[] vs = (V[]) Array.newInstance(c, arr.length); for(int i=0;i<arr.length;i++) vs[i] = f.apply(arr[i]); return vs; }
 @SuppressWarnings("unchecked") public static final <V> V[] map(Function<Boolean, V> f, boolean[] arr) { if(arr.length==0) return (V[]) new Object[0]; final Class<?> c = f.apply(arr[0]).getClass(); final V[] vs = (V[]) Array.newInstance(c, arr.length); for(int i=0;i<arr.length;i++) vs[i] = f.apply(arr[i]); return vs; }
 @SuppressWarnings("unchecked") public static final <V> V[] map(Function<Character, V> f, char[] arr) { if(arr.length==0) return (V[]) new Object[0]; final Class<?> c = f.apply(arr[0]).getClass(); final V[] vs = (V[]) Array.newInstance(c, arr.length); for(int i=0;i<arr.length;i++) vs[i] = f.apply(arr[i]); return vs; }
 @SuppressWarnings("unchecked") public static final <V> V[] map(Function<Double, V> f, double[] arr) { if(arr.length==0) return (V[]) new Object[0]; final Class<?> c = f.apply(arr[0]).getClass(); final V[] vs = (V[]) Array.newInstance(c, arr.length); for(int i=0;i<arr.length;i++) vs[i] = f.apply(arr[i]); return vs; }
 @SuppressWarnings("unchecked") public static final <V> V[] map(Function<Float, V> f, float[] arr) { if(arr.length==0) return (V[]) new Object[0]; final Class<?> c = f.apply(arr[0]).getClass(); final V[] vs = (V[]) Array.newInstance(c, arr.length); for(int i=0;i<arr.length;i++) vs[i] = f.apply(arr[i]); return vs; }
    @SuppressWarnings("unchecked")
    public static final <K, V> V[] map(Function<K, V> f, K[] objs) {
 final Class<?> c;
 try{
     final Class<?> ofArray = objs.getClass().getComponentType();
     c = f.apply((K) ofArray.getConstructor().newInstance()).getClass();
 }
 catch(Exception e) {
     throw new RuntimeException("Can not construct class");
 }
 final V[] vs = (V[]) Array.newInstance(c, objs.length);
 for(int i=0;i<objs.length;i++) vs[i] = f.apply(objs[i]);
 return vs;
    }
    @SuppressWarnings("unchecked")
    public static final <V, K> List<V> map(Function<K,V> f, List<K> ks) {
 final List<V> vs;
 try{
     vs = (List<V>) ks.getClass().getConstructor().newInstance();
 }
 catch(Exception e) {
     throw new RuntimeException("Can not construct class");
 }
 for(K k : ks) vs.add(f.apply(k));
 return vs;
    }
    public static final <V, K> Functor<V> map(Function<K, V> f, Functor<K> ks) {
 return ks.fmap(f);
    }
}
