package js.http;

import java.io.IOException;
import com.sun.net.httpserver.HttpExchange;

public interface HttpHandler {
    HttpResult handle(HttpExchange t) throws HttpException, IOException;
}
