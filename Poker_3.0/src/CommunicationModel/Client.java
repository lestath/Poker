package CommunicationModel;

import java.awt.Color;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.text.DecimalFormat;

import Game.Player;
import Game.Table;
import View.Exit;
import View.GameFrame;
/**
 * Klasa wysyła żądania klienta, oraz obsługuje odpowiedzi serwera
 */
public class Client extends Table implements Runnable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private int Port;
	private String Host;
	private ObjectOutputStream out;
	private ObjectInputStream in;
	private volatile InfoPack InPack;
	private volatile InfoPack OutPack;
	private boolean Running;
	private GameFrame Frame;
	private Socket Sck;
	@SuppressWarnings("unused")
	private Server Srv; // wskaźnik serwera jeżeli gracz jest właścicielem stołu, w innym wypadku null
    
	/**
	 * Konstruktor 
	 * @param nick
	 * 			 nazwa klienta
	 * @param startpoints
	 * 			 ilość punktów z jaką gracza rozpocznie rozgrywkę
	 * @param host
	 * 			nazwa, lub adres IP hosta do którego podłączy się klient
	 * @param port
	 * 			numer portu na którym nasłuchuje serwer (domyślnie 4444)
	 * @param frm
	 * 			wskaźnik interfejsu użytkownika
	 */
	public Client(String nick,double startpoints,String host, int port, GameFrame frm){
		super();
		super.setPlayer(new Player(nick),0);
		this.getPlayer(0).setPoints(startpoints);
		this.getPlayer(0).setOwner(false);
		this.Running = true;
		this.Frame = frm;
		this.InPack = null;
		this.OutPack = null;
		this.Host = host;
		this.Port = port;
		this.Srv = null;
	}
	/**
	 * Konstruktor 
	 * @param nick
	 * 			 nazwa klienta
	 * @param startpoints
	 * 			 ilość punktów z jaką gracza rozpocznie rozgrywkę
	 * @param host
	 * 			nazwa, lub adres IP hosta do którego podłączy się klient
	 * @param port
	 * 			numer portu na którym nasłuchuje serwer (domyślnie 4444)
	 * @param frm
	 * 			wskaźnik interfejsu użytkownika
	 * @param owner
	 * 			flaga wskazująca czy gracz jest jednocześnie właścicielem serwera (tj. czy założył dany stół) 
	 */
	public Client(String nick,double startpoints,String host, int port, GameFrame frm,boolean owner){
		this(nick,startpoints,host,port,frm);
		this.getPlayer(0).setOwner(owner);
	}
	
	
	/**
	 * Sprawdza czy klient jest w stanie czynnym
	 * @return
	 * 		zwraca stan klienta, jeżeli true to klient jest w stanie aktywności
	 */
	public boolean isRunning() {
		return Running;
	}
	/**
	 * Ustawia stan aktywności klienta
	 * @param running
	 * 				parametr aktywności
	 */
	public void setRunning(boolean running) {
		Running = running;
	}
	
	/**
	 * Ustawia wskaźnik serwera
	 * @param s
	 * 			Wskaźnik na Serwer którego właścicielem jest dany klient
	 */
	public void setSrv(Server s){
		this.Srv = s;
	}
	
	
	/**
	 * Metoda uruchamiana automatycznie przy starcie wątku. Prowadzi zapętlony nasłuch na pakiety otrzymywane od serwera.
	 * W zależności od otrzymanej komendy wykonywane są odpowiednie metody obsługi 
	 */
	@Override
	public void run() {
		// utworzenie i wysłanie pakietu powitalnego wraz z przedstawieniem gracza
		try {
			this.Sck = new Socket(this.Host,this.Port);
			this.in = new ObjectInputStream(this.Sck.getInputStream());
			this.out = new ObjectOutputStream(this.Sck.getOutputStream());
			this.OutPack =new InfoPack("HELLO");
			this.OutPack.setPlayer(this.getPlayer(0),0);
			this.out.writeObject(this.OutPack);
			
			//nasłuch na pakiety w pętli 
			while(this.Running){
				this.InPack =(InfoPack)in.readObject();
				if(InPack!=null){
					// protokół odboru danych
					if(InPack.getMessage().equals("BYE")){
						this.byeService();
					}else if(InPack.getMessage().equals("SUCCESS")){
						this.Frame.getMainFrame().sentMsg("Poprawnie Połączono",Color.GREEN);
				//	}else if(InPack.getMessage().equals("FIRSTDISTRIB")){
						// this.firstdistribService();
					}else if(InPack.getMessage().equals("REFRESH")){
						this.refreshService();
					}else if(InPack.getMessage().equals("PLAYEROUT")){
						this.Frame.getGp().setPlayerOutMsg(this.InPack.getPlayer(0).getNickName());
					}else if(InPack.getMessage().equals("NEW_CARDS")){
						this.newcardsService();
					}else if(InPack.getMessage().equals("OBSERVER_REFRESH")){
						this.ObserverRefreshService();
					}else if(InPack.getMessage().equals("YOU_RE_IN_GAME")){
						this.youReInGameService();
					}else if(InPack.getMessage().equals("CHAT")){ // obsługa otrzymania wiadomości z chatu
						this.chatService();
					}
					this.InPack = null;
				}
			}
			
			
			
		} catch (IOException | ClassNotFoundException e) {
			this.Frame.setMsg("Błąd Połączenia",Color.RED);
			new Thread(new Exit(this.Frame,2000)).start();
			this.Running = false;
		}
	}
	
	

	/**
	 *  Metoda obsługi czatu, dodaje otrzymaną wiadomość do pola czatu
	 */
	private void chatService() {
		String msg = this.InPack.getLastChatMessage();
		this.Frame.getChatTa().append(msg);
		this.Frame.getChatTa().setCaretPosition(
									this.Frame.getChatTa().getDocument().getLength()
								);
	}

	/**
	 * Zmienia stan klienta z obserwatora na gracza czynnego
	 */
	private void youReInGameService() {
		this.Frame.getGp().setObserverMode(false);
		
	}

	/**
	 * Metoda obsługi odświeżenia informacji dla obserwatora stołu
	 */
	private void ObserverRefreshService() {
		 this.setPlayers(InPack.getPlayers());
		 this.setBank(InPack.getBank());
		 this.setCycle(InPack.getCycle());
		 this.setLastAction(InPack.getLastAction());
		 this.setLastRaise(this.InPack.getLastRaise());
		 this.Frame.disableGameBtns();
		 this.Frame.getGp().setObserverMode(true);
		 this.Frame.getGp().setPlayFlag(true);
		 this.Frame.getGp().repaint();
	}

	/**
	 * 
	 * @return
	 * 		Zwraca obiektowy strumień wyjściowy klienta
	 */
	public ObjectOutputStream getOut() {
		return out;
	}
	
	/**
	 *  Metoda wysyła pakiet do serwera
	 * @param pack
	 * 			Obiekt pakietu, który zostanie przesłany do serwera
	 */
	public void sentPack(InfoPack pack){
		InfoPack p = pack;
		try {
			this.out.writeObject(p);
		} catch (IOException e) {
			this.Frame.setMsg("Błąd Połączenia",Color.RED);
			new Thread(new Exit(this.Frame,2000)).start();
			this.Running = false;
		}
		p= null;
	}
	
	
	/** 
	 * Metoda ustawia parametry elementów ramki gracza w zależności od ostatniej akcji na stole 
	 * @param act
	 * 			numer ostatnio wykonanej akcji klienta
	 */
	public void setBtnsAfterAction(int act){
		DecimalFormat dec = new DecimalFormat("#.##");
		switch(act){
		    case 0:
				this.Frame.enableGameBtns();
				this.Frame.getImInBtn().setEnabled(false);
		    break;
			case 1 :
				this.Frame.getPointsTf().setText(dec.format(this.getLastRaise()));
				this.Frame.enableGameBtns();
			break;
			case 2 :
				this.Frame.getPointsTf().setText(dec.format(this.getLastRaise()));
				this.Frame.enableGameBtns();
			break;
			case 5 :
				this.Frame.getPointsTf().setText(dec.format(this.getLastRaise()));
				this.Frame.enableGameBtns();
			break;
			default :
				this.Frame.enableGameBtns();
			break;
		}
	}
	
	/** 
	 * Metoda obsługi polecenia refresh
	 */
	private void refreshService(){
		if(InPack.getPlayer(0).getAction()==6){this.Frame.getGp().setObserverMode(false);}
		String playerinfo = "";
	    if(!this.Frame.getGp().isObserverMode()){
	    	playerinfo = "<html>Gracz : "+this.getPlayer(0).getNickName()+"<br></html>";
			this.Frame.getPlayerInfoLab().setText(playerinfo);
	    }
	    
		 this.setPlayers(InPack.getPlayers());
		 this.setBank(InPack.getBank());
		 this.setCycle(InPack.getCycle());
		 this.setLastAction(InPack.getLastAction());
		 this.setLastRaise(this.InPack.getLastRaise());
		 
		 // sprawdzenie, czy na stole nie został tylko jeden gracz jeżeli tak to trzeba zabrać mu dostęp do przycisków
		 int count = 0;
		 for(int i =0;i<4;i++){
			 if(InPack.getPlayers()[i]!=null){
				 count = count +1;
			 }
		 }
		  if(this.getPlayer(0).getState()==1){
			 this.setBtnsAfterAction(this.getLastAction());
		  }else{
			  this.Frame.disableGameBtns();
		  }
		 if(count <2){
			 this.Frame.disableGameBtns();
		 }
		 this.Frame.getGp().setPlayFlag(true);
		 this.Frame.getGp().repaint();
	}
	
/*	
	// metoda obsługująca pierwsze rozdanie
	private void firstdistribService(){
		this.setPlayers(InPack.getPlayers());
		 this.getPlayer(0).getH().sortCards(); // sortujemy karty według figur
		  if(this.getPlayer(0).getState()==1){
			  this.setBtnsAfterAction(this.getLastAction());
		  }else{
			  this.Frame.disableGameBtns();
		  }
		 this.setBank(InPack.getBank());
		 this.getPlayer(0).setHandPower(this.getPlayer(0).getH().getHandPower());
		 this.Frame.getGp().setPlayFlag(true);
		 this.Frame.getGp().repaint();
		 if(this.Frame.getStartBtn()!=null){	
				this.Frame.getStartBtn().setEnabled(false);
		}
	}
	
*/
	/** 
	 * Metoda obsługi otrzymania nowych kart po wymianie
	 */
	private void newcardsService(){
		InfoPack pack = this.InPack;
		this.getPlayer(0).setH(pack.getPlayer(0).getH());
		this.getPlayer(0).getH().sortCards();
		  if(this.getPlayer(0).getState()==1){
			 this.setBtnsAfterAction(2);
		  }else{
			  this.Frame.disableGameBtns();
		  }
		this.getPlayer(0).setHandPower(this.getPlayer(0).getH().getHandPower());
		this.Frame.getGp().resetToChangeTab();
		this.Frame.getGp().repaint();
	}
	
	
	/** 
	 * Metoda obsługi wyjścia klienta
	 * @throws IOException
	 * 					Wyjątek wejścia/wyjścia
	 */
	private void byeService() throws IOException{
		this.Sck.close();
		this.Running = false;
		this.Frame.getMainFrame().sentMsg("Poprawnie Odłączono",Color.GREEN);
		this.Frame.dispose();
	}
}
