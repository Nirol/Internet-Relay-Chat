package threadperclient;

import java.io.IOException;
import java.io.InputStreamReader;

import serializer.Serializer;

public class TPC_Serializer implements Serializer<String> {

	public final char _delimiter;
	private final InputStreamReader _isr;
	private char[] _buffer;
	private int _nextChar;

	public TPC_Serializer (InputStreamReader isr, char delimiter) {
		_delimiter = delimiter;
		_isr = isr;
		clearBuffer();
	}

	public String nextToken() throws IOException {
		boolean send = false;
		try {
			int c;
			while (_isr.ready()) {
				c = _isr.read();
				if (c==-1){
					throw new IOException("Input stream ended");
				} else if (c == _delimiter){
					send = true;
					break;
				} else{
					if (_nextChar == 512)
						throw new IOException("Message is too long");
					_buffer[_nextChar] = (char) c;
					_nextChar++;
				}
			}
		} catch (IOException e) {
			throw new IOException("Connection is dead");
		}

		if (send)
			return emptyBuffer();
		return null;
	}

	private String emptyBuffer(){
		StringBuilder sb = new StringBuilder(_nextChar);
		for(int i=0;i<_nextChar;i++){
			sb.append(_buffer[i]);
		}
		clearBuffer();
		return sb.toString();
	}

	private void clearBuffer(){
		_buffer = new char[512];
		_nextChar=0;
	}

}


