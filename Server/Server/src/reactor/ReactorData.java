package reactor;

import java.util.concurrent.ExecutorService;
import java.nio.channels.Selector;

import encoding.Encoder;
import serializer.SerializerFactory;


/**
 * a simple data structure that hold information about the reactor, including getter methods
 */
public class ReactorData<T> {

	private final ExecutorService _executor;
	private final Selector _selector;
	private final Encoder _encoder;
	private final MessagingProtocolFactory<T> _protocolMaker;
	private final SerializerFactory<T> _tokenizerMaker;


	public ExecutorService getExecutor() {
		return _executor;
	}

	public Selector getSelector() {
		return _selector;
	}

	public ReactorData(ExecutorService _executor, Selector _selector, MessagingProtocolFactory<T> protocol,
			SerializerFactory<T> tokenizer, Encoder encoder) {
		this._executor = _executor;
		this._selector = _selector;
		this._encoder = encoder;
		this._protocolMaker = protocol;
		this._tokenizerMaker = tokenizer;
	}

	public MessagingProtocolFactory<T> getProtocolMaker() {
		return _protocolMaker;
	}

	public SerializerFactory<T> getTokenizerMaker() {
		return _tokenizerMaker;
	}

	public Encoder getEncoder() {
		return this._encoder;
	}

}
