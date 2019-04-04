package reactor;

import server.MessagingProtocol;

/** the method creates a new MessageProtocol.
 * 
 * @return new object that implements the MessageProtocol interface. 
 */
public interface MessagingProtocolFactory<T> {
   MessagingProtocol<T> create();
}
