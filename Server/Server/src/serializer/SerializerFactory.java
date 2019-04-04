package serializer;
/** factory interface for creating a new MessageSerializer.
 * 
 * 
 * @author Nir
 *
 * @param <T>
 */
public interface SerializerFactory<T> {
	
	/** the method creates a new MessageSerializer.
	 * 
	 * @return new object that implements the MessageSerializer interface. 
	 */
   MessageSerializer<T> create();
}
