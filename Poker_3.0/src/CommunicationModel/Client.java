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
	
	public Client(String nick,double startpoints,String host, int port, GameFrame frm,boolean owner){
		this(nick,startpoints,host,port,frm);
		this.getPlayer(0).setOwner(owner);
	}
	
	public boolean isRunning() {
		return Running;
	}
	public void setRunning(boolean running) {
		Running = running;
	}
	
	public void setSrv(Server s){
		this.Srv = s;
	}
	
	
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
					}else if(InPack.getMessage().equals("FIRSTDISTRIB")){
						 this.firstdistribService();
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
	
	

	// metoda obsługi czatu, dodaje otrzymaną wiadomość do pola czatu
	private void chatService() {
		String msg = this.InPack.getLastChatMessage();
		this.Frame.getChatTa().append(msg);
		this.Frame.getChatTa().setCaretPosition(
									this.Frame.getChatTa().getDocument().getLength()
								);
	}

	private void youReInGameService() {
		this.Frame.getGp().setObserverMode(false);
		
	}

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

	public ObjectOutputStream getOut() {
		return out;
	}
	
	// funkcja wysyła pakiet do serwera
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
	
	
	// metoda ustawia parametry elementów ramki gracza w zależności od ostatniej akcji na stole 
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
	
	// metoda obsługi polecenia refresh
	private void refreshService(){
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
		  if(this.getPlayer(0).getState()==1){
			 this.setBtnsAfterAction(this.getLastAction());
		  }else{
			  this.Frame.disableGameBtns();
		  }
		 this.Frame.getGp().setPlayFlag(true);
		 this.Frame.getGp().repaint();
	}
	
	
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
	}
	
	
	// metoda obsługi otrzymania nowych kart po wymianie
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
	
	
	// metoda obsługi wyjścia klienta
	private void byeService() throws IOException{
		this.Sck.close();
		this.Running = false;
		this.Frame.getMainFrame().sentMsg("Poprawnie Odłączono",Color.GREEN);
		this.Frame.dispose();
	}
	
	protected void finalize(){
		System.out.println("Koniec Klienta");
	}
}
