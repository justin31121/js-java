package js.general;

public class Tuple<F, S> {
    
    public final F fst;
    public final S snd;

    public Tuple(F fst, S snd) {
	this.fst = fst;
	this.snd = snd;
    }

    public static <F, S> Tuple<F, S> from(F fst, S snd) {
        return new Tuple<>(fst, snd);
    }
}
