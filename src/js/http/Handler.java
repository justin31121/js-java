package js.http;

import java.io.IOException;
import com.sun.net.httpserver.HttpExchange;

@FunctionalInterface
public interface Handler {
    void handle(HttpExchange t) throws IOException;
}
