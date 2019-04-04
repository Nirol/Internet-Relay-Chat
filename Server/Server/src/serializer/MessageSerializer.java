package serializer;

import java.nio.ByteBuffer;
import java.nio.charset.CharacterCodingException;


public interface MessageSerializer<T> extends Serializer<T>  {

   
   /**
    * Add some bytes to the message stream.
    * @param bytes an array of bytes to be appended to the message stream.
    */
   void addBytes(ByteBuffer bytes);

   /**
    * Is there a complete message ready?.
    * @return true the next call to nextMessage() will not return null, false otherwise.
    */
   boolean hasMessage();

   /**
    * Convert the String message into bytes representation, taking care of encoding and framing.
    * @return a ByteBuffer with the message content converted to bytes, after framing information has been added.
    */
   ByteBuffer getBytesForMessage(T msg) throws CharacterCodingException;

}
