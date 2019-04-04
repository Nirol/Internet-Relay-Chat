package reactor;

import java.io.IOException;
import java.nio.ByteBuffer;

import serializer.MessageSerializer;
import server.MessagingProtocol;

/**
 * This class supplies some data to the protocol, which then processes the data,
 * possibly returning a reply. This class is implemented as an executor task.
 * 
 */
public class ProtocolTask<T> implements Runnable {

	private final MessagingProtocol<T> _protocol;
	private final MessageSerializer<T> _serializer;
	//private final ConnectionHandler_Reactor<T> _handler;

	public ProtocolTask(final MessagingProtocol<T> protocol, final MessageSerializer<T> tokenizer){
		//, final ConnectionHandler_Reactor<T> h) {
		this._protocol = protocol;
		this._serializer = tokenizer;
		//this._handler = h;
	}

	// we synchronize on ourselves, in case we are executed by several threads
	// from the thread pool.
	public synchronized void run() {
		// go over all complete messages and process them.
		try {
			while (_serializer.hasMessage()) {
				T msg;
				msg = _serializer.nextToken();				
				this._protocol.processMessage(msg); ///ignore response
			}
		} catch (IOException e) {
			System.out.println("Serializer crashed!");
		}
	}

	public void addBytes(ByteBuffer b) {
		_serializer.addBytes(b);
	}
}
