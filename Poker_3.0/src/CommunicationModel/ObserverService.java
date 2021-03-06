package CommunicationModel;


import java.io.IOException;
import java.net.Socket;



/**
 * 
 * Wątek obsługujący nadchodzących obserwatorów 
 */
public class ObserverService extends Thread{
	
	private Server Srv; //wskaźnik do serwera wywołującego wątek
	private boolean Open;
	/**
	 * Konstruktor
	 * @param s
	 *         Referencja na obiekt serwera
	 */
	public ObserverService(Server s){
		this.Srv = s;
		this.Open = true;
	}
	

	/**
	 * Metoda uruchamiająca nasłuch na obserwatorów
	 */
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
	
	/**
	 * Metoda sprawdza czy można dodawać obserwatorów
	 * @return
	 *        zwraca true jeżeli dodawanie obserwatorów jest włączone
	 */
	public boolean isOpen(){
		return this.Open;
	}
	
	/**
	 * Metoda ustawiająca pozwolenie na wejście obserwatorów
	 * @param open
	 *            parametr ustawiony na true ustawia zezwolenie na przyjecie obserwatorów
	 */
	public void setOpen(boolean open){
		this.Open = open;
		
	}
}
