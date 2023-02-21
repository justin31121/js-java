import static js.Io.*;
import js.Req;
import java.io.*;
import java.util.*;

import javax.xml.parsers.*;
import org.w3c.dom.*;
import org.xml.sax.*;

//javac -cp "c:\\users\\jschartner\\Documents\\js-java\\libs\\*;." Main.java && java -cp "c:\\users\\jschartner\\Documents\\js-java\\libs\\*;." Main && rm Main.class
class Main {

    static String responseCache = "test.txt";
    static String keyWord = "Hello, World";

    static String getContent(final boolean force) throws IOException {
	if(new File(responseCache).exists() && !force) {
	    return slurpFile(responseCache);
	} else {
	    final Req.Result result =
		Req.get(concat("https://www.google.com/search?client=firefox-b-d&q=",
			       Req.encode(keyWord)));
	    if(!result.ok) {
		println("FAIL", result.responseCode);
		exit(1);
	    }
	    
	    final String response = Req.utf8(result.data);
	    writeFile(responseCache, response);
	    return response;
	}
    }
    static String getContent() throws IOException { return getContent(false); }

    static enum TokenType {
	//SYMBOLS
	ANGLE_OPEN,
	ANGLE_CLOSE,
	PAREN_OPEN,
	PAREN_CLOSE,
	CURLY_OPEN,
	CURLY_CLOSE,
	BRACKET_OPEN,
	BRACKET_CLOSE,
	
	DOUBLE_QUOTATION,
	QUOTATION,
	EQUALS,
	EXCLAMATION,
	QUESTION,
	ZIRKUMFLEX,
	DOLLAR,

	POINT,
	TILDE,
	BACK_TICK,
	PERCENT,
	AT,
	HASH,
	STAR,
	SLASH,
	BACK_SLASH,
	SEMICOLON,
	COMMA,
	DOT,	
	COLON,
	UNDERSCORE,
	
	MINUS,
	PLUS,
	
	AND_SIGN,
	OR_SIGN,
	
	//STRINGS
	DOCTYPE,

	WORD,
	HTTP,
	RELPATH
	;

	static int count() { return values().length; }
	int ord() { return ordinal(); }
	static TokenType from(int i) { return values()[i]; }
    }

    static class Token {
	final Tokenizer tokenizer;
	final TokenType type;
	final int start;
	final int len;
	final String content;

	Token(final Tokenizer tokenizer,
	      final TokenType type,
	      final int start,
	      final int len) {
	    this.tokenizer = tokenizer;
	    this.type = type;
	    this.start = start;
	    this.len = len;
	    this.content = null;
	}

	Token(final TokenType type,
	      final String content) {
	    this.tokenizer = null;
	    this.type = type;
	    this.start = -1;
	    this.len = -1;
	    this.content = content;
	}

	Token(final TokenType type) {
	    this(type, null);
	}

	public boolean equals(final String target) {
	    if(target == null) {
		return false;
	    }

	    int n = len;
	    int m = target.length();

	    if(n != m) {
		return false;
	    }

	    for(int i=0;i<n;i++) {
		if(tokenizer.text[start+i] != target.charAt(i)) {
		    return false;
		}
	    }
	    
	    return true;
 	}

	@Override
	public String toString() {

	    if(start != -1 && len != -1 && tokenizer != null) {
		return new StringBuilder(String.valueOf(type))
		    .append(" - '")
		    .append(Arrays.copyOfRange(tokenizer.text, start, start + len))
		    .append("'")
		    .toString();		
	    }
	    else {
		return new StringBuilder(String.valueOf(type))
		    .append(" - '")
		    .append(content)
		    .append("'")
		    .toString();
	    }
	}
    }

    static class Tokenizer implements Iterable<Token> {

	//SYMBOLS
	private static char[] symbols = new char[] {
	    '<', '>', '\"', '!', '=', '/', '(', ')', '{', '}', ';',
	    '&', '|', ':', '\'', '[', ']', ',', '.', '_', '+', '-',
	    '?', '^', '\\', '$', '*', '#', '@', '%', '`', '~', '·'
	};
	private static int[] symbolsToken = new int[] {
	    TokenType.ANGLE_OPEN.ord(),
	    TokenType.ANGLE_CLOSE.ord(),
	    TokenType.DOUBLE_QUOTATION.ord(),
	    TokenType.EXCLAMATION.ord(),
	    TokenType.EQUALS.ord(),
	    TokenType.SLASH.ord(),
	    TokenType.PAREN_OPEN.ord(),
	    TokenType.PAREN_CLOSE.ord(),
	    TokenType.CURLY_OPEN.ord(),
	    TokenType.CURLY_CLOSE.ord(),	    
	    TokenType.SEMICOLON.ord(),
	    TokenType.AND_SIGN.ord(),
	    TokenType.OR_SIGN.ord(),
	    TokenType.COLON.ord(),
	    TokenType.QUOTATION.ord(),
	    TokenType.BRACKET_OPEN.ord(),
	    TokenType.BRACKET_CLOSE.ord(),
	    TokenType.COMMA.ord(),
	    TokenType.DOT.ord(),
	    TokenType.UNDERSCORE.ord(),	
	    TokenType.MINUS.ord(),
	    TokenType.PLUS.ord(),
	    TokenType.QUESTION.ord(),
	    TokenType.ZIRKUMFLEX.ord(),
	    TokenType.BACK_SLASH.ord(),
	    TokenType.DOLLAR.ord(),
	    TokenType.STAR.ord(),
	    TokenType.HASH.ord(),
	    TokenType.AT.ord(),
	    TokenType.PERCENT.ord(),
	    TokenType.BACK_TICK.ord(),
	    TokenType.TILDE.ord(),
	    TokenType.POINT.ord(),
	};
	
	//STRING
	private static String[] strings = new String[] {
	    "!doctype"
	};
	private static int[] stringsToken = new int[] {
	    TokenType.DOCTYPE.ord()
	};	
		
	final int len;
	final char[] text;
	private int pos;
	private Token last;

	Tokenizer(final String text) {
	    this.text = text.toCharArray();
	    this.len = this.text.length;
	    this.pos = 0;
	    this.last = null;
	}

	private void trimLeft() {
	    while(pos < len && Character.isWhitespace(text[pos])) {
		pos++;
	    }
	}

	private static boolean isAlpha(char c) {
	    return Character.isLetter(c) || Character.isDigit(c) || (8211 <= c && c <= 8250) || (c == 160);
	    //return ('0' <= c && c <= '9') || ('a' <= c && c <= 'z') || ('A' <= c && c <= 'Z');
	}

	@Override
	public Iterator<Token> iterator() {
	    final Tokenizer tokenizer = this;	    
	    return new Iterator<Token>() {
		@Override
		public boolean hasNext() {
		    trimLeft();
		    if(pos == len) {
			return false;
		    }

		    //STRINGS
		    for(int i=0;i<strings.length;i++) {
			final String string = strings[i];
			final int n = string.length();
			if(n > (len - pos)) { // TEST that
			    continue;
			}

			boolean ok = true;
			for(int j=0;j<n;j++) {
			    if(text[pos + j] != string.charAt(j)) {
				ok = false;
				break;
			    }
			}

			if(ok) {
			    last = new Token(tokenizer, TokenType.from(stringsToken[i]), pos, n);
			    pos += n;
			    return true;
			}
		    }

		    //SYMBOLS
		    for(int i=0;i<symbols.length;i++) {
			if(symbols[i] == text[pos]) {			    
			    last = new Token(tokenizer, TokenType.from(symbolsToken[i]), pos, 1);
			    pos += 1;
			    return true;
			}
		    }

		    //HTTP
		    {
			final String httpPrefix = "http://";
			if(len - pos > httpPrefix.length()) {
			    boolean ok = true;
			    for(int i=0;i<httpPrefix.length();i++) {
				if(text[pos + i] != httpPrefix.charAt(i)) {
				    ok = false;
				    break;
				}
			    }
			    if(ok) {
				int i = pos + httpPrefix.length();
				while(i<len && (isAlpha(text[i])|| text[i]=='.' || text[i] == '/')) {
				    i++;
				}
				if(i - pos >  0) {
				    last = new Token(tokenizer, TokenType.HTTP, pos, i - pos);
				    pos = i;
				    return true;
				}
			    }
			}  
		    }

		    //PATH
		    {
			if(text[pos] == '/') {
			    int i=pos;
			    while(i<len && (isAlpha(text[i]) || text[i]=='/' || text[i]=='_' || text[i]=='.' )) {
				i++;
			    }
			    if(i - pos >  0) {
				last = new Token(tokenizer, TokenType.RELPATH, pos, i - pos);
				pos = i;
				return true;
			    }
			}
		    }

		    //WORD
		    {
			int i=pos;
			while(i<len &&
			      isAlpha(text[i])
			      //text[i] != '<' && text[i] != '>' && text[i] !='"' && text[i] != '/' && text[i] != '='
			      ) {
			    i++;
			}
			if(i - pos >  0) {
			    last = new Token(tokenizer, TokenType.WORD, pos, i - pos);
			    pos = i;
			    return true;
			}
		    }

		    return false;
		}

		@Override
		public Token next() {
		    if(last == null) {
			if(!hasNext()) {
			    throw new RuntimeException("The tokenizer is empty");
			}			
		    }
		    
		    Token result = last;
		    last = null;
		    
		    return result;
		}
	    };	    
	}
    }

    static void expectTokens(final Iterator<Token> tokens, final Token ...targetTokens) {
	
	for(Token targetToken : targetTokens) {
	    if(!tokens.hasNext()) {
		throw new RuntimeException("Expected TokenType: "+String.valueOf(targetToken)+". But got eof.");
	    }
	    
	    Token token = tokens.next();
	    if(token.type != targetToken.type) {
		throw new RuntimeException("Expected TokenType: "+String.valueOf(targetToken.type)+". But got "+String.valueOf(token.type));
	    }

	    if(targetToken.content != null) {
		if(!token.equals(targetToken.content)) {
		    throw new RuntimeException("Expected Token: "+String.valueOf(targetToken)+". But got "+String.valueOf(token));
		}
	    }
	}
	
    }

    static void expectNodeOpen(final Iterator<Token> tokens) {
	expectTokens(tokens, new Token(TokenType.ANGLE_OPEN));
	Token main = tokens.next();
	println(main);
	Token token = tokens.next();
	while(token.type != TokenType.ANGLE_CLOSE) {
	    if(token.type != TokenType.WORD) {
		throw new RuntimeException("Expected TokenType: "+TokenType.WORD+". But got "+token);
	    }
	    println("\t", token);
	    expectTokens(tokens,
			 new Token(TokenType.EQUALS),
			 new Token(TokenType.DOUBLE_QUOTATION));
	    Token content = tokens.next();
	    while(content.type != TokenType.DOUBLE_QUOTATION) {
		println("\t\t", content);
		content = tokens.next();
	    }
	    token = tokens.next();
	}
    }

    static void expectNodeClose(final Iterator<Token> tokens) {
	expectTokens(tokens,
		     new Token(TokenType.ANGLE_OPEN),
		     new Token(TokenType.SLASH),
		     new Token(TokenType.WORD),
		     new Token(TokenType.ANGLE_CLOSE));
    }
    
    public static void main(String[] args) throws Exception {
	final String response = "<!doctype html><html itemscope=\"\" itemtype=\"http://schema.org/SearchResultsPage\" lang=\"de\"></html>";
	Iterator<Token> tokens = new Tokenizer(response).iterator();

	expectTokens(tokens,
		     new Token(TokenType.ANGLE_OPEN),
		     new Token(TokenType.DOCTYPE),
		     new Token(TokenType.WORD, "html"),
		     new Token(TokenType.ANGLE_CLOSE));
	expectNodeOpen(tokens);
	expectNodeClose(tokens);
    }
}
