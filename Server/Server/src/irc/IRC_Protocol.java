package irc;



import datastructures.SimpleLink;
import server.ConnectionHandler;
import server.MessagingProtocol;

public class IRC_Protocol implements MessagingProtocol<String> {

	private boolean _shouldClose;	
	public Client client;
	ServerState state;


	public IRC_Protocol() {
		_shouldClose = false;		
	}
	/** the constructor of the irc protocol
	 * 
	 * @param state the ServerState object of the running server.
	 */
	public IRC_Protocol(ServerState state) {
		_shouldClose = false;	
		this.state=state;		
		client = new Client(state.newClientId());
		state.addClient(client);

	}

	/**
	 * return the _shouldClose field of the protocol.
	 */
	public boolean shouldClose() {
		return _shouldClose;
	}

	/** this method used to inform the protocol that connection to the client has ended.
	 *  also, the method make sure, in case the client has not left the server with the command QUIT
	 *  that the client will be deleted from the server.
	 */
	public void connectionTerminated() {
		_shouldClose = true;
		forceQuit(client);
	}
	
	private void forceQuit(Client client) {
		if (!client.hasQuit()){

			sendData(client, "Client " + client.getNick() + " has quit abruptly");
			state.quitServer(client);		
		}
	}

	/** 
	 * return string response of the protocol to the client Comment.
	 */
	public void processMessage(String msg) {		
		if (msg != null) process(msg);
		
	}

	// this method just check if the massage received from the client is a specific command or data massage
	private void process(String msg){	
		String splitMsg[]=msg.split(" ");

		Command msgEnum=commandEnum(splitMsg[0]);			
		if (!checkUserCommandOrder(msgEnum)){		
			client.getConnectionHandler().sendLine("451 You have not registered");
		} else if (!checkParam(msgEnum, splitMsg)){
			client.getConnectionHandler().sendLine("461 "+msgEnum+": Not enough parameters"); 
			// ERR NEEDMOREPARAMS
		} else {
			switch (msgEnum) {

			case NICK:  NickCommand(splitMsg);
			break;
			case USER:  UserCommand(splitMsg);
			break;
			case QUIT:  QuitCommand(splitMsg);
			break;
			case JOIN:  JoinCommand(splitMsg);
			break;
			case PART:  PartCommand(splitMsg);
			break;
			case NAMES: NamesCommand(splitMsg);
			break;
			case LIST:  ListCommand();
			break;
			case KICK:  KickCommand(splitMsg);
			break;
			case DATA:  DataMessage(msg); 	
			break;
			}			
		}
	}
	/**this method check if the client supplied a required parameter to a command.
	 * 
	 * @param msgEnum enum type of command
	 * @param massage the massage the client sent
	 * @return true if the client supplied the needed parameter for the command.
	 */
	private boolean checkParam(Command msgEnum, String[] massage) {
		if (msgEnum==Command.KICK||msgEnum==Command.JOIN||msgEnum==Command.PART||msgEnum==Command.USER){
			if (massage.length==1) return false;
		}
		return true;
	}

	/** this method check if the client is registered before he try
	 * to use any other command or send text massages.
	 * 
	 * @param msgEnum- the command the user sent
	 * @return true if the client is registered or trying to register.
	 */
	private boolean checkUserCommandOrder(Command msgEnum) {
		if (msgEnum==Command.NICK){
			if (!client.isUserRegistered()&&client.isNickRegistered()) return false;
			return true;
		}
		if (!client.isNickRegistered()){		

			return false;		
		}
		if (!client.isUserRegistered()){

			if (msgEnum==Command.USER) return true;			

			return false;
		}
		return true;
	}



	private void DataMessage(String msg) {
		System.out.println("data massage");
		sendData(client, client.getNick() + ": " + msg);
	}
	/** this method send massages to all clients in specific channel
	 * except the client who originally send the massage
	 * 
	 * @param client the client who sent the massage to the channel he joined to.
	 * @param msg the massage the client sent.
	 */
	
	public void sendData(Client client, String msg) {	

		if (client.getChannel() == null) return;
		
		SimpleLink<Client> clientLink = client.getChannel().getClient();
		
		while (clientLink != null){		
			if (clientLink.getData() != client)
				clientLink.getData().getConnectionHandler().sendLine(msg);
			clientLink=clientLink.getNext();
		}
		
	}
	
	private String buildQuitMessage(Client client, String[] splitMsg){
		if (splitMsg.length > 1){
			String msg = buildString(splitMsg, 1);
			return client.getNick()+" has left the channel: " + msg;

		}
		return client.getNick()+" has left the channel";
	}
	
	/**
	 * 
	 * @param stringArray of words to be converted to a single string.
	 * @param i index of first word 
	 * @return
	 */
	private String buildString(String[] stringArray, int i) {
		if (i >= stringArray.length) return null;
		StringBuilder stringBuilder = new StringBuilder();
		for (; i < stringArray.length-1 ; i++){
			stringBuilder.append(stringArray[i] + " ");	
		}
		stringBuilder.append(stringArray[stringArray.length-1]);
		
		return stringBuilder.toString();		
	}
	
	

	/**
	 * @param splitMsg contain the message the user sent.
	 * @return the server respond to the command.
	 */

	private void NickCommand(String[] splitMsg) {	
		String res;
		if (splitMsg.length==1){
			res = "431 No nickname given";
			// ERR NONICKNAMEGIVEN
		} else {
			res =  state.setNick(client, splitMsg[1]);	
		}
		client.getConnectionHandler().sendLine(res);
	}

	/** this method make sure the client added a user name after the command USER
	 * if so, the protocol ask the server to change the client username.
	 * 
	 * @param splitMsg contain the message the user sent.
	 * @return the server respond to the command.
	 */

	private void UserCommand(String[] splitMsg) {		 
		String res;
		if (client.isUserRegistered())
			res = "462 You may not reregister";	
		else
			res = state.setUser(client, buildString(splitMsg,1));		
		
		client.getConnectionHandler().sendLine(res);
	}
	
	/**this method update the server state that the client closed the communication.
	 * @param splitMsg the massage the user sent to the channel togther with the QUIT command
	 * @return 
	 */
	private void QuitCommand(String[] splitMsg) {		
		System.out.println("quit massage");
		System.out.println("splitMsg length is: "+splitMsg.length);
		
		client.getConnectionHandler().sendLine("QUIT");		
		
		sendData(client, buildQuitMessage(client, splitMsg));
		
		state.quitServer(client);		

		_shouldClose = true;
	}

	
	/**this method move the client into the channel given in the massage.
	 * 
	 * @param splitMsg hold the name of the channel the user want to join.
	 * @return the server respond to the command.
	 */
	private void JoinCommand(String[] splitMsg) {	
		String ch = splitMsg[1];
		// if splitMsg.length < 2 a not-enough-parameters reply is returned beforehand

		if (!ch.startsWith("#")) return;   
		// Ignore illegal channel names

		boolean sucsessJoin = state.joinChannel(client, ch);	
		if (sucsessJoin)
			//print clients in the channel
			client.getConnectionHandler().sendLines(state.namesOfChannel(ch));
	}
	
	/**this method remove the client from the channel given in the massage
	 * the client must be in the channel in order to leave it.
	 * 
	 * @param splitMsg - the channel the client want to leave.
	 * @return the server respond to the command.
	 */
	private void PartCommand(String[] splitMsg) {		
		String channel = splitMsg[1];
		String res = state.partChannel(client,channel, false );		
		client.getConnectionHandler().sendLine(res);
	}
	
	
	private void KickCommand(String[] splitMsg) {			
		String res = state.kick(client, splitMsg[1]);
		client.getConnectionHandler().sendLine(res);
	}

	/**
	 *  this method send to the client list of all open channels on the irc server.
	 */
	private void ListCommand() {
		state.list(client);
	}
	
	/** this method send to the client either all clients on specific channel
	 * the channel name given in the massage from the client
	 * @param splitMsg - the massage of the client.
	 * @return string with the nicknames of all clients on server or on specific channel.
	 */
	private void NamesCommand(String[] splitMsg){
		if (splitMsg.length==1){
			//No parameters were passed with NAMES command
			client.getConnectionHandler().sendLines(state.allNames());
		} else {
			String ch = splitMsg[1];
			client.getConnectionHandler().sendLines(state.namesOfChannel(ch));	
		}
	}


	/**
	 * @param s = string representing a command.
	 * @return the correct Command enum for the parameter s. 
	 */
	private static  Command commandEnum(String s){		
		if (s.equals("NICK")) return Command.NICK;
		else if(s.equals("USER")) return Command.USER;
		else if (s.equals("QUIT")) return Command.QUIT;
		else if (s.equals("JOIN"))return Command.JOIN;
		else if (s.equals("PART"))return Command.PART;
		else if (s.equals("NAMES"))return Command.NAMES;
		else if (s.equals("LIST"))return Command.LIST;
		else if (s.equals("KICK")) return Command.KICK;
		else return Command.DATA;
	}





	@Override
	public boolean isEnd(String msg) {
		String splitMsg[]=msg.split(" ");
		return splitMsg[1].equals("QUIT");		
	}

	@Override
	public void bind(ConnectionHandler<String> ch) {
		this.client.bindConnectionHandler(ch);
	}

}

