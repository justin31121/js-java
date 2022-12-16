package js.http;

import static js.http.Util.*;
import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;

class FileHandler implements HttpHandler {
    private final String path;

    public FileHandler(final String path) {
	this.path = path;
    }

    private final MimeFile getFile(String fileId) {
	String location = path + fileId;
        MimeFile file;
        try{
            file = new MimeFile(location);
            if(!file.isFile()) return null;
        }
        catch(Exception e) {
            return null;
        }
        return file;
    }

    @Override
    public HttpResult handle(final HttpExchange t) throws IOException {
	String method = t.getRequestMethod();
        if(!"GET".equals(method)) return notFound();

	String fileId = t.getRequestURI().getPath();
        if(fileId==null || fileId.length()==0 || "/".equals(fileId)) {
            fileId = "/index.html";
        }

        MimeFile file = getFile(fileId);
        if(file!=null) {
            t.getResponseHeaders().set("Content-Type", file.getMime()+"; charset=utf-8");
            return ok(file);
        }

	return notFound();
    }
}
