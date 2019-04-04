package server;

import java.util.ArrayList;


import encoding.Encoder;
import serializer.Serializer;
/** interface that both versions of ConnectionHandlers implement.
 * 
 * both of the versions use Encoder, Serializer, and MessagingProtocol
 * and the methods SendLine and SendLines ( of String and byte[] ).
 * 
 * @author Nir
 *
 * @param <T>
 */
public abstract class ConnectionHandler<T> {
	
	protected final Encoder _encoder;
	protected final Serializer<T> _serializer;
	protected final MessagingProtocol<T> _protocol;
	
	public ConnectionHandler(Encoder encoder, Serializer<T> serializer, MessagingProtocol<T> protocol) {
		_encoder = encoder;
		_serializer = serializer;
		_protocol= protocol;
	}
	
	public abstract void sendLines (ArrayList<byte[]> lines);
	
	public void sendLine (byte[] line){
		ArrayList<byte[]> arr = new ArrayList<byte[]>();
		arr.add(line);
		sendLines(arr);
	}
	
	public void sendLine (String line){
		ArrayList<byte[]> arr = new ArrayList<byte[]>();
		arr.add(_encoder.toBytes(line));
		sendLines(arr);
	}

}
