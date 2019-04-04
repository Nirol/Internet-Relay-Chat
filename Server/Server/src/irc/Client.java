package irc;

import server.ConnectionHandler;

public class Client {
	
	private int id;
	private ConnectionHandler<String> connectionHandler;
	private String username;
	private String nickname;
	private Channel channel;
	private boolean regNick;
	private boolean regUser;
	private boolean quit;
	
/** Constructor. 
 * 
 * @param id - the client id in the server.
 */
public Client(int id){
	this.id=id;
	 regNick=false;
	 regUser=false;
	 channel = null;
}

/** this method change the client String nickname to a new one.
 * 
 * @param nickname - the new nickname given to the client.
 */
	public void setNick(String nickname) {	
		this.nickname=nickname;	
		regNick=true;
	}
	/** this method change the client String username to a new one.
	 * 
	 * @param username - the new username given to the client.
	 */
	public void setUser(String username) {		
		this.username=username;
		regUser=true;
	}


/**
 * 
 * @return true if the client register a nick at least once.
 */
	public boolean isNickRegistered(){
		return this.regNick;
	}

	public boolean isUserRegistered() {
		return this.regUser;
	}

	public String getNick() {
		
		return nickname;
	}
	
	public String getUsername() {
		
		return username;
	}
	/** getter method for the channel field of the client.
	 * 
	 * @return the channel object reference.
	 */

	public Channel getChannel() {
	
		return channel;
	}
	public boolean equals(Object other){
	    if (other == null) return false;
	    if (other == this) return true;
	    if (!(other instanceof Client))return false;
	    Client otherMyClass = (Client)other;
	    return this.nickname==otherMyClass.nickname;
	}
/** remove the client from his channel.
 * 
 */
	public void removeChannel() {
	this.channel=null;
		
	}
/** change the channel of the client
 * 
 * @param chn the new channel of the client
 */
	public void setChannel(Channel chn) {
		this.channel=chn;
		
	}
	
	/**
	 * 
	 * @return the client id
	 */
	public int getId(){
		return id;
	}
	/**
	 *  bind to the client objec reference to the connectionHandler that hold the
	 *  connection of the client.
	 * @param ch
	 */
	public void bindConnectionHandler(ConnectionHandler<String> ch) {
		this.connectionHandler = ch;
	}
	
	/**
	 * 
	 * @return the client reference to the connectionHandler.
	 */
	public ConnectionHandler<String> getConnectionHandler(){
		return this.connectionHandler;
	}
	/**
	 *  update the client status to quit the server.
	 */
	public void setQuit() {
		quit=true;
		
	}
	/**
	 * 
	 * @return true if the client quit the server.
	 */
	public boolean hasQuit(){
		return quit;
	}
	

}


