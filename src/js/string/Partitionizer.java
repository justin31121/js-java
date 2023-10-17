package js.string;

import java.util.*;

public class Partitionizer implements Iterable<Token> {
    private final CharSequence sequence;
    private final int n;
	
    public Partitionizer(CharSequence sequence, int n) {
	this.sequence = sequence;
	this.n = n;
    }

    @Override
    public Iterator<Token> iterator() {
	return partition(sequence, n);
    }
  
    public static Iterator<Token> partition(CharSequence input, int n) {
	return new Iterator<Token>(){

	    Iterator<Token> tokens = Tokenizer.tokenize(input);
	    boolean tokensNext= tokens.hasNext();
	    int acc = 0;
	    int off = 0;
	    
	    @Override
	    public boolean hasNext() {
		return tokensNext || acc > 0;
	    }

	    @Override
	    public Token next() {
		if(tokensNext) {
		    Token range = tokens.next();
		    tokensNext = tokens.hasNext();

		    if(acc > 0 && acc + range.len > n) {
			Token result = new Token(off, acc);
			off += acc;
			acc = range.len;
			return result;
		    }

		    acc += range.len;
		    return next();
		} else {
		    Token result = new Token(off, acc);
		    off += acc;
		    acc = 0;
		    return result;
		}
	    }
	};
    }
 
  
}
