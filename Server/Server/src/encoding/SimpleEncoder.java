package encoding;

import java.nio.charset.Charset;

public class SimpleEncoder implements Encoder {
	 
    private static final String DFL_CHARSET = "UTF-8";
    private Charset _charset;
    private boolean useTerminatingChar;
    private char t;

    
    /** simple constructor without parameters  for the SimpleEncoder
     * will use the default charset and will  not add a terminating char.
     * 
     */
    public SimpleEncoder() {
            this(DFL_CHARSET);
    }

    /**Constructor for simpleEncoder that receive only charset.
     * will  not add a terminating char.
     * @param charset - the charset given for the encoder to use.
     */
    public SimpleEncoder(String charset) {
    	_charset = Charset.forName(charset);
    	useTerminatingChar = false;
    }
    /**Constructor for simpleEncoder that receive only terminating char.
     *  the encoder will use default charset
     * @param t char to use as a terminating char.
     */
    public SimpleEncoder(char t) {
        this(DFL_CHARSET);
        useTerminatingChar = true;
        this.t = t;
    }
/**Constructor for simpleEncoder that receive both charset and terminating char.
 * 
 * @param charset
 * @param t
 */
	public SimpleEncoder(String charset, char t) {
        this(charset);
        useTerminatingChar = true;
        this.t = t;
	}
	/**
	 * convert string to bytes. Append predetermined terminating char to the string.
	 * @param s to encode
	 * @return
	 */
	@Override
    public byte [] toBytes(String s) {
		if (useTerminatingChar)
            return (s+t).getBytes(_charset);
		else
			return s.getBytes(_charset);
    }
	/** this method converts the given byte array into a string.
	 * 
	 * @param buf - a byte array to convert into string.
	 * @return String representation of the given byte array.
	 */
	@Override
    public String fromBytes(byte [] buf) {
            return new String(buf, 0, buf.length, _charset);
    }

	/**
	 * 
	 * @return the Charset used in this encoder.
	 */
	@Override
    public Charset getCharset()  {
            return _charset;
    }

}
