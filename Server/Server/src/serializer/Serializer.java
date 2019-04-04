package serializer;
import java.io.IOException;  

public interface  Serializer<T> {

	/**
	 * @return the next token, or null if no token is available. Pay attention
	 *         that a null return value does not indicate the stream is closed,
	 *         just that there is no message pending.
	 * @throws IOException to indicate that the connection is closed.
	 */
	T nextToken() throws IOException;

}
