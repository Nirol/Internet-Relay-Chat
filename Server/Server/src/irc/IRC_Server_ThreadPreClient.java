package irc;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import encoding.Encoder;
import encoding.SimpleEncoder;
import serializer.Serializer;
import server.MessagingProtocol;
import threadperclient.ConnectionHandler_TPC;
import threadperclient.TPC_Serializer;
import threadperclient.ThreadPerClient;

class IRC_Server_ThreadPreClient {
	

	
    public static void main(String[] args) throws NumberFormatException, IOException {
    	
        ThreadPerClient tpc = new ThreadPerClient();
        
		int port = 6667;
        
        Encoder encoder = new SimpleEncoder("UTF-8",'\n');
        ServerSocket sSocket = new ServerSocket(port);
        
    	ServerState state = new ServerState(encoder);
        
        System.out.println("Server running");
        while (true) {        
            Socket s = sSocket.accept();
    		
            Serializer<String> serializer = new TPC_Serializer(new InputStreamReader(s.getInputStream(),encoder.getCharset()),'\n');
            MessagingProtocol<String> protocol = new IRC_Protocol(state);
            ConnectionHandler_TPC<String> connectionHandler = new ConnectionHandler_TPC<String>(s, encoder, serializer, protocol);
            
    		String openMsg = "Welcome to the SPL Internet Relay Chat Network. Your host is "+ s.getLocalAddress()+ " running version 1.0 ";		
    		connectionHandler.sendLine(openMsg);
         
            protocol.bind(connectionHandler);
            tpc.apply(connectionHandler);
        }
    }
}