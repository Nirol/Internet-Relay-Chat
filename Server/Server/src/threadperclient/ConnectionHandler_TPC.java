package threadperclient;

import java.io.IOException;
import java.net.Socket;
import encoding.Encoder;
import serializer.Serializer;
import server.ConnectionHandler;
import server.MessagingProtocol;

import java.util.ArrayList;
import java.util.LinkedList;

public class ConnectionHandler_TPC<T> extends ConnectionHandler<T> implements Runnable {
	
	private final Socket _socket;
	private LinkedList<byte[]> _massageQueue; 
	
	public ConnectionHandler_TPC (Socket s, Encoder encoder, Serializer<T> serializer, MessagingProtocol<T> protocol) {
		super(encoder, serializer, protocol);
		
		_socket = s;
		_massageQueue = new LinkedList<byte[]>();
	}

	public void run() {   		

		while (!_protocol.shouldClose() && !_socket.isClosed()) {       

			try {

				T msg = _serializer.nextToken();
				if (msg!=null)
				{
					_protocol.processMessage(msg);
				}
				
				int i = 10;
				while (i>0 && !_massageQueue.isEmpty()){
					byte[] buf = _massageQueue.poll();
					System.out.println("send line to client: "+ _encoder.fromBytes(buf));
					_socket.getOutputStream().write(buf, 0, buf.length);
					i--;
				}

			} catch (IOException e) {
				_protocol.connectionTerminated();
				System.out.println("_protocol.connectionTerminated");
				break;
			}
			
		}
		try {
			_socket.close();
		} catch (IOException ignored) {
		}
	
		System.out.println("thread done");
	}



	public synchronized void sendLines (ArrayList<byte[]> lines){
		for (int i=0;i<lines.size();i++){
			//System.out.println("push to msgQueue: " + _encoder.fromBytes(lines.get(i)));
			_massageQueue.addLast(lines.get(i));
		}
	}

}


