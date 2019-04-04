package irc;

import datastructures.SimpleLink;


public class Channel {
	SimpleLink<Client> clients;
	Client chanop;
	String name;
	int id;

/**Channel constructor.
 * 
 * @param client - the client who first created this channel on the server, will be channel OP.
 * @param channel - String  representing the new name  of the Channel.
 */

	public Channel(Client client, String channel) {
		chanop=client;
		name=channel;
		clients=new SimpleLink<Client>(client);
	}
	/**
	 * 
	 * @return the number of Clients in the channel.
	 */
	public int size(){
		if (clients==null) return 0;
		return clients.size();
	}


/**equals method that compare channel names.
 *  return true if the channels have the same name.
 */

	public boolean equals(Object other){
		if (other == null) return false;
		if (other == this) return true;
		if (!(other instanceof Client))return false;
		Channel otherMyClass = (Channel)other;
		return this.name==otherMyClass.name;

	}



/**
 * 
 * @return String represintation of the channel name.
 */
	public String getName() {

		return name;
	}



/**this method remove a spesific client from the channel
 * 
 * @param client - client object to remove from the channel.
 */
	public void removeClient(Client client) {
		if (clients!=null) {
			SimpleLink<Client> clientLink = clients.search(client);
			if (clientLink.getPrev()==null)clients=clients.removeLink();
			else {
				clientLink.removeLink();
			}
		}

	}
	/**this method return the Linked List holding the clients in the channel.
	 * 
	 * @return the first link in the linked list.
	 */
	public SimpleLink<Client> getClient(){
		return clients;
	}

	/** 
	 * 
	 * @return a String that start by 353 and channel name, following by the names of the clients in the channel.
	 */
	public String clientNames() {
		StringBuilder clientsBuilder = new StringBuilder();
		SimpleLink<Client> clientLink=getClient();
		clientsBuilder.append("353 "+name+" ");
		while (clientLink!=null){		
			if (clientLink.getData()==chanop) clientsBuilder.append("@");
			clientsBuilder.append(clientLink.getData().getNick());
			
			clientsBuilder.append(" ");
			clientLink=clientLink.getNext();
		}
		return clientsBuilder.toString();
	}
	
	/**
	 * 
	 * @return the client object who is the channel OP.
	 */
	public Client getChanop() {

		return chanop;
	}
	/** this method add a new client to the channel
	 * 
	 * @param client - a client to add to the channel.
	 */
	public void addClient(Client client) {
		if (clients==null) clients = new SimpleLink<Client>(client);
		else clients.addLast(client);

	}
	/** 
	 * 
	 * @return true in case the channel operator is in the channel.
	 * return false otherwise.
	 */
	public boolean opFound() {
		if (clients==null) return false;
		SimpleLink<Client> tmp = clients.search(chanop);
		return tmp!=null;
	}
}