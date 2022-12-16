package js.http;

import static js.http.Util.notFound;

class MetaHandler {

    private final String context;
    private final static String[] methods
	= {"GET", "POST", "DELETE", "PUT"};
    private final HttpHandler[] handlers = new HttpHandler[methods.length];
    
    public MetaHandler(final String context) {
	this.context = context;
    }

    public final void set(final String method, final HttpHandler handler) {
	int pos = -1;
	for(int i=0;i<methods.length;i++) {
	    if(method.equals(methods[i])) {
		pos = i;
		break;
	    }
	}

	if(pos<0) {
	    System.out.println("[WARNING] No suitable HTTP-Method found for: "+method);
	    return;
	}

	handlers[pos] = handler;
    }

    public final HttpHandler get() {
	return t -> {
	    String method = t.getRequestMethod();
	    for(int i=0;i<methods.length;i++) {
		if(handlers[i]!=null && method.equals(methods[i])) {
		    return handlers[i].handle(t);
		}
	    }
	    return notFound();
	};
    }
}
