package encoding;
import java.nio.charset.Charset;

public interface Encoder {
	/**
	 * convert string to bytes. Append predetermined terminating char to the string.
	 * @param s to encode
	 * @return
	 */
	public byte [] toBytes(String s);
/** this method converts the given byte array into a string.
 * 
 * @param buf - a byte array to convert into string.
 * @return String representation of the given byte array.
 */
	public String fromBytes(byte [] buf);
	
	
	/**
	 * 
	 * @return the Charset used in this encoder.
	 */
	public Charset getCharset();
}
