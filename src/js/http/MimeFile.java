package js.http;

import java.io.File;

class MimeFile extends File { 

    private final String mime;
    
    public MimeFile(String name) {
        super(name);
        mime = determineMime(name);
    }

    private String determineMime(String fileName) {
        char[] word = fileName.toCharArray();

        int i = word.length-1;
        while(i>=0 && word[i]!='.') i--;
        if(i==-1) return null;

        String type = fileName.substring(i, word.length);
        switch(type) {
	case ".json":
	    return "application/json";
        case ".html":
            return "text/html";
        case ".js":
            return "text/javascript";
        case ".css":
            return "text/css";
	case ".png":
	    return "image/png";
	case ".jpeg":
	case ".jpg":
	case ".jpe":
	    return "image/jpeg";
	case "image/x-ixon":
	    return "image/x-icon";
        case ".svg":
            return "image/svg+xml";
        default:
            System.out.println("unreachable getMIME: "+type);
            return "text/plain";
        }
    }

    public String getMime() {
        return mime;
    }
}
