package datastructures;

public class SimpleLink<T> {
	protected T data;
	protected SimpleLink<T> next;
	protected SimpleLink<T> prev;

	SimpleLink(){
		this.data=null;
		next = null;
		prev = null;

	}
	/** this constructor create a new SimpleLink with data to inserg into the list.
	 * 
	 * @param data - the data to insert into the the link.
	 */
	public SimpleLink(T data){
		this.data = copyData(data); //works for primitives or immutable objects
		next = null;
		prev = null;
	}
	
	/**this constructor recive a link as paramater and copy its data to a new link
	 * 
	 * @param org original link to copy
	 */
	SimpleLink(SimpleLink<T> org){
		this.data = copyData(org.data);
		
		//Copy Tail
		SimpleLink<T> temp = org.prev;

		SimpleLink<T> i; // new link
		SimpleLink<T> j = this; // newLink.next

		while(temp != null){
			i = new SimpleLink<T>(temp.data);
			i.next = j;
			j.prev = i;
			// copy previous link
			j = i; 
			temp = temp.prev;
		}

		
		//Copy Head
		temp = org.next;
		j = this; // newLink.prev
		while(temp != null){
			i = new SimpleLink<T>(temp.data);
			i.prev = j;
			j.next = i;
			j = i;
			temp = temp.next;
		}
	}
	
	protected T copyData(T data){
		 return data;  //works for primitives or immutable objects
	}
	/**
	 * 
	 * @return the previous link or null if non exist
	 */
	public SimpleLink<T> getPrev(){
		return prev;
	}
	/**
	 * 
	 * * @return the next link or null if non exist
	 */
	public SimpleLink<T> getNext(){
		return next;
	}
	
	/**
	 * 
	 * @return the data contained in the link
	 */
	public T getData(){
		return data;
	}

	/** this method remove this specific link from the list.
	 * 
	 * @return the next link in list after the removed link
	 */
	public SimpleLink<T> removeLink() {

		if (this.prev!=null){
			this.prev.next=this.next;
		}
		if(this.next!=null){
			this.next.prev=this.prev;
		}
		return this.next;
	}

	
	public boolean equals(SimpleLink<T> other){
		return(this.data.equals(other.data));
	}
/**
 * 
 * @return the size of the list starting from this link.
 */
	public int size(){
		return this.size(1);
	}

	private int size(int s){
		if (this.next == null) return s;
		return this.next.size(s+1);
	}
/**this method search the list for data T, if a link containg data T
 * 
 * @param toFind
 * @return
 */
	public SimpleLink<T> search(T toFind){
		if (this.data.equals(toFind)) return this;
		else if (this.next == null) return null;
		return this.next.search(toFind);
	}

	public SimpleLink<T> addLast(T data){
		SimpleLink<T> toAdd = new SimpleLink<T>(data);

		SimpleLink<T> tmp = this;
		while (tmp.next!=null)tmp=tmp.next;
		tmp.next=toAdd;
		toAdd.prev=tmp;
		
		return toAdd;
	}
	
	
	/** make a new link containing the given data, and add the link first.
	 * 
	 * @param data - the data to add to the link
	 * @return the newely created link.
	 */
	public SimpleLink<T> addFirst(T data){
		SimpleLink<T> toAdd = new SimpleLink<T>(data);

		if (this.prev!=null)
			this.prev.next=toAdd;
		toAdd.prev=this.prev;
		toAdd.next=this;
		this.prev=toAdd;
		return toAdd;
	}



}

