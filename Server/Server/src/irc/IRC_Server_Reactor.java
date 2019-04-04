package irc;

import encoding.Encoder;
import encoding.SimpleEncoder;
import reactor.Reactor;

public class IRC_Server_Reactor {

	/**
	 * Main program, used for demonstration purposes. Create and run a
	 * Reactor-based server for the Echo protocol. Listening port number and
	 * number of threads in the thread pool are read from the command line.
	 */
	public static void main(String args[]) {

		int port = 6667;
		
		try {
			int poolSize = 10;
// new encoder.
			Encoder encoder = new SimpleEncoder('\n');
			
			Reactor<String> reactor = Reactor.startServer(port, poolSize, encoder);

			Thread thread = new Thread(reactor);
			thread.start();
			Reactor.logger.info("Reactor is ready on port " + reactor.getPort());
			thread.join();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
}
