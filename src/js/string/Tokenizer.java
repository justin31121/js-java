package js.string;

import java.util.*;

public class Tokenizer implements Iterable<Token> {

    private final CharSequence sequence;

    public Tokenizer(CharSequence sequence) {
	this.sequence = sequence;
    }

    @Override
    public Iterator<Token> iterator() {
	return tokenize(sequence);
    }
	
    public static Iterator<Token> tokenize(CharSequence input) {
	
	return new Iterator<Token>() {

	    int i = 0;	    
	    
	    @Override
	    public boolean hasNext() {
		return i < input.length();
	    }

	    private Token tokenizeDigit() {
		int j = 0;
		while(i + j < input.length()) {
		    char c = input.charAt(i + j);

		    if(Token.isDelim(c)) break;
		    if(!Character.isDigit(c) && c != '-') break;

		    j++;
		}

		i += j;
		return new Token(i - j, j);
	    }

	    private Token tokenizeAlpha() {
		int j = 0;
		while(i + j < input.length()) {
		    char c = input.charAt(i + j);

		    if(Token.isDelim(c)) break;
		    if(!Character.isAlphabetic(c) && c != '-') break;

		    j++;
		}

		i += j;
		return new Token(i - j, j);
	    }

	    @Override
	    public Token next() {
		char c = input.charAt(i);
		if(Token.isDelim(c)) {
		    return new Token(i++, 1);
		} else if(Character.isDigit(c)) {
		    return tokenizeDigit();
		} else if(Character.isAlphabetic(c)) {
		    return tokenizeAlpha();
		}
		
		throw new RuntimeException("Unhandled char: '"+c+"'");
	    }
	};
	
    }
       	
}
