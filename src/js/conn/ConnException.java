package js.conn;

import java.io.IOException;

public class ConnException extends IOException {
    public ConnException(String message) {
	super(message);
    }
}
