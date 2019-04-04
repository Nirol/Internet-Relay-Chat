package threadperclient;

public class ThreadPerClient {
	public void apply(Runnable connectionHandler) {
	       new Thread(connectionHandler).start();
	}
 }