package irc;


import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import encoding.Encoder;
	/** The ServerState object holds the information regarding all active users and channels.
	 * 
	 * 
	 */
public class ServerState {
	private Hashtable<Integer, Client> clients;
	private Hashtable<String, Client> clientsByNick;
	private Hashtable<String, Client> clientsByUser;
	private Hashtable<String, Channel> channels;
	private int clientCounter;
	private Encoder encoder;

	public ServerState(Encoder encoder){
		clients = new Hashtable<Integer, Client>();
		clientsByNick = new Hashtable<String, Client>();
		clientsByUser = new Hashtable<String, Client>();
		channels = new Hashtable<String, Channel>();
		this.encoder = encoder;
		clientCounter=0;
	}
	/** this method print all the clients on the server
	 * 
	 * @return String containing all of the clients nick names
	 */
	public ArrayList<byte[]> allNames() {			
		
		ArrayList<byte[]> res = new ArrayList<byte[]>();
		StringBuilder names = new StringBuilder();
		
		for (Enumeration<Client> e = clientsByNick.elements(); e.hasMoreElements();)
			names.append(clientInLobby(e.nextElement()));


		res.add(encoder.toBytes("Clients in Lobby: "+ names.toString()));



		for (Enumeration<Channel> e = channels.elements(); e.hasMoreElements();)
			res.add(encoder.toBytes(channelNames(e.nextElement())));
		res.add(encoder.toBytes("366 End of /NAMES"));
		
		return res;

	}
	private String clientInLobby(Client client) {
		if (client.getChannel()==null && client.getNick()!=null) return client.getNick()+" ";
		return "";
	}
	/** this method print all the clients nick names in the given channel
	 * 
	 * @param channel - the name of the channel
	 * @return list of all client Nicks in channel
	 */
	public ArrayList<byte[]> namesOfChannel(String query) {
		
		ArrayList<byte[]> res = new ArrayList<byte[]>();
		
		Channel channel = channels.get(query);
		
		if (channel == null){
			res.add(encoder.toBytes("403 "+query+":No such channel"));
		}
		else {
			res.add(encoder.toBytes(channelNames(channel)));
			res.add(encoder.toBytes("366 End of /NAMES"));
		}
		
		return res;
	}
	private String channelNames(Channel channel){

		return channel.clientNames();
	}

	/**this method delete the client from the channel and from the server.
	 * 
	 * @param channel the client is in right now.
	 * @param client - the client who quit the server
	 * @param splitMsg - the massage the client sent to the channel with the QUIT command.
	 */
	public void quitServer(Client client) {
		
		Channel channel = client.getChannel();
		
		if (channel != null){
			if (channel.size()>1){
				channel.removeClient(client);			
			}
			else{
				channels.remove(channel.getName());
			}
		}
		client.setQuit();
		clients.remove(client.getId());
		if (client.isNickRegistered())clientsByNick.remove(client.getNick());
		if (client.isUserRegistered())clientsByUser.remove(client.getUsername());
	}



	/**this method find/create the channel the client want to join to.
	 * if the client is already in a channel, than first the client removed from that channel.
	 * @param client the client that want to join a channel
	 * @param channel the channel the client want to join too.
	 */
	public boolean joinChannel(Client client, String channel) {

		Channel chn = findChannel(client, channel);	
		if (client.getChannel()!=null){
		if (client.getChannel().getName().equals(channel)) return false;
		partChannel(client,client.getChannel().getName(),true);
		}
		client.setChannel(chn);	
		if(chn.getChanop()!=client||(chn.getChanop()==client&&!chn.opFound()))chn.addClient(client);

		return true;


	}
	/**this method check if the channel name the client want to join allready exist
	 * @param client
	 * @param channel the name of the channel the client want to join
	 * @return the channel object the user want to join.
	 */
	private Channel findChannel(Client client, String channel) {
		Channel chn= channels.get(channel);
		if (chn==null){
			chn = new Channel(client, channel);
			channels.put(channel, chn);			
		}
		return chn;
	}


	private boolean nickExist(String nickname) {
		return clientsByNick.containsKey(nickname);	

	}

	private boolean userExist(String username) {
		return clientsByUser.containsKey(username);

	}

	/** the function set a client nickname.
	 * if the nickname allready exist on the server, the method send back an error massage.
	 * 
	 * @param client- the client who require new nickname.
	 * @param nickname required nickname.
	 * @return either error massage in case the name already exist, or 401 massage Nick accepted.
	 */

	public String setNick(Client client, String nickname) {

		if (nickExist(nickname)) return "433 "+nickname+": Nickname is already in use"; //ERR NICKNAMEINUSE
		else{
			if (client.isNickRegistered()){
				clientsByNick.remove(client.getNick());
			}
			client.setNick(nickname);
			clientsByNick.put(nickname, client);
			return "401";  // RPL NICKACCEPTED
		}

	}
	/** the function set a client username.
	 * if the username allready exist on the server, the method send back an error massage.
	 * @param client - the client who require setting new username service.
	 * @param username the username the client want to have.
	 * @return either error massage in case the name already exist, or 402 massage username accepted.
	 */

	public String setUser(Client client, String userName) {
		if (userExist(userName)) return "462 You may not register"; // ERR ALREADYREGISTRED
		else {
			if (client.isUserRegistered()){
				clientsByUser.remove(client.getUsername());
			}
			client.setUser(userName);
			clientsByUser.put(userName, client);
			return "402"; //RPL USERACCEPTED
		}

	}
	

	public String partChannel(Client client, String channel, boolean forcedPart) {
		if (client.getChannel()==null) return "403 "+channel+": No such channel";	
		if (!client.getChannel().getName().matches(channel)) return "403 "+channel+": No such channel";         //ERR NOSUCHCHANNEL
		client.getChannel().removeClient(client);		
		if (client.getChannel().size()==0){
			channels.remove(channel); // close the channel;
		}
		client.removeChannel();

		if (forcedPart) {		
			client.getConnectionHandler().sendLine(encoder.toBytes("405 You have left the channel: "+channel));
		}
		return "405 You have left the channel: "+channel; // RPL PARTSUCCESS
	}

	public void addClient(Client client) {		
		clients.put(clientCounter, client);

	}



	public String kick(Client clientKicker, String nicknameToKick) {
		Channel channel = clientKicker.getChannel();
		if (clientKicker!=channel.getChanop()) return "482 "+channel.getName()+": You're not channel operator";  // ERR CHANOPRIVSNEEDED
		Client toKick = clientsByNick.get(nicknameToKick); 
		if (toKick!=null) partChannelKick(toKick, channel.getName());
		return "404 ";
	}

	private void partChannelKick(Client client, String channel) {
		if (client.getChannel()!=null){
			if (!client.getChannel().getName().matches(channel)) return;      
			client.getChannel().removeClient(client);		
			if (client.getChannel().size()==0){
				channels.remove(channel); // close the channel;
			}
			
			client.getConnectionHandler().sendLine(encoder.toBytes("405 You have left the channel: "+channel));
			client.removeChannel();
		}				
	}
	public int newClientId() {
		clientCounter++;	
		return clientCounter;
	}
	/** this mehtod return list of existing channels names
	 * @param client - client asking for a list of channels names.
	 * @return - list of channel names
	 */
	public void list(Client client) {
		ArrayList<byte[]> ans = new ArrayList<byte[]>();		
		ans.add(encoder.toBytes("321"));
		for (Enumeration<Channel> e = channels.elements(); e.hasMoreElements();)
			ans.add(encoder.toBytes("322 "+(e.nextElement().getName())));

		ans.add(encoder.toBytes("323 End of /LIST"));

		client.getConnectionHandler().sendLines(ans);

	}

}