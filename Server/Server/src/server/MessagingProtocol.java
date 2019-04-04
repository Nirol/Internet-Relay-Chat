package server;

public interface MessagingProtocol<T>{
  

	/**
     * Process a given message.
     * 
     * @return the answer to send back, or null if no answer is required
     */
    void processMessage(T msg);
 
    /**
    * determine whether the given message is the termination message
    * @param msg the message to examine
    * @return true if the message is the termination message, false otherwise
    */
    boolean isEnd(T msg);
 
    /**
     * @return true if the connection should be terminated
     */
    boolean shouldClose();
 
    /**
     * called when the connection was not gracefully shut down.
     */
    void connectionTerminated();

/**
 * Binds a client with it's connection handler (socket)
 * @param connectionHandler The client's connection handler
 */
	void bind(ConnectionHandler<T> connectionHandler);
}