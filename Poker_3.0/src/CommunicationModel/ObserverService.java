package CommunicationModel;


import java.io.IOException;
import java.net.Socket;



/**
 * 
 * @author lestath
 * Wątek obsługujący przychodzenie obserwatorów 
 */
public class ObserverService extends Thread{
	
	private Server Srv; //wskaźnik do serwera wywołującego wątek
	private boolean Open;
	
	public ObserverService(Server s){
		this.Srv = s;
		this.Open = true;
	}
	

	@Override 
	public void run(){
		try {
			ServerRequest srvreq = null;
			Socket sck;
			while(this.Open){
				sck = Srv.getSocket().accept();
				srvreq = new ServerRequest(this.Srv,sck,100);
				srvreq.start();
				Srv.getClientsQueue().offer(srvreq);
			}
			
		} catch (IOException e) {
			this.Open = false;
			this.Srv = null;
		}
	}
	
	public boolean isOpen(){
		return this.Open;
	}
	
	public void setOpen(boolean open){
		this.Open = open;
		
	}
}
