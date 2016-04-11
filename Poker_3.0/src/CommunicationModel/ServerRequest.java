package CommunicationModel;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import Game.Card;
import Game.Player;
public class ServerRequest extends Thread{
	private int ClientID;
	private ObjectOutputStream out;
	private ObjectInputStream in;
	private boolean Running;
	private Socket ClientSck;
	private InfoPack OutPack;
	private Server Serv;
	private Player WaitingPlayer; // pole pomocnicze przechowuje informacje o graczu jeżeli oczekuje on na wejście do gry jako obserwator
	
	public ServerRequest(Server srv ,Socket sck, int i){
		this.ClientID = i;
		this.ClientSck = sck;
		this.Running = true;
		this.OutPack = null;
		this.WaitingPlayer = null;
		this.Serv = srv;
		try {
			out = new ObjectOutputStream(ClientSck.getOutputStream());
			in = new ObjectInputStream(ClientSck.getInputStream());
		} catch (IOException e) {
					this.byeService(true);
		}
		 
	}
	
	
	@Override
	public void run() {
		
		// czytanie wejścia w pętli
	  try{
		while(this.Running){
				InfoPack p= (InfoPack)in.readObject();
				if(p!=null){
					// obsługa protokołu komunikacji
					 if(p.getMessage().equals("HELLO")){
						if(this.ClientID !=100){
						 this.helloService(p);
						}else{
						 this.observerHelloService(p);
						}
					 }else if(p.getMessage().equals("BYE")){
						 this.byeService(false);
					 }else if(p.getMessage().equals("RAISE")){ // obsługa podbicia
						 this.raiseService(p);                                     //do usunięcia
					 }else if(p.getMessage().equals("PASS")){ // obsługa podbicia
						 this.passService(p);
					 }else if(p.getMessage().equals("CHANGE")){ // obsługa wymiany kart
						 this.changeCardsService(p);
					 }else if(p.getMessage().equals("CHECK")){ // obsługa sprawdzenie
						 this.checkService(p);
					 }else if(p.getMessage().equals("CHAT")){ // obsługa czatu
						 this.chatService(p);
					 }
				}
			}
		
		    // do usunięcia
		    System.out.println("Koniec nasłuchu na klienta : "+this.ClientID);
	  }catch(Exception e){
			this.byeService(true);
	  }
	  
	}
	
	// metoda obsługi rozesłania wiadomości z chatu
	private void chatService(InfoPack p) {
		String nick="";
		if(this.ClientID!=100){
		 nick =this.Serv.getPlayer(this.ClientID).getNickName();
		}else if(this.WaitingPlayer!=null){
		 nick = this.WaitingPlayer.getNickName();
		}
		 this.Serv.setLastChatMessage("["+nick+"] : "+p.getLastChatMessage()+"\n");
		 this.Serv.sentToAll("CHAT");
	}


	private void observerHelloService(InfoPack p) throws IOException {
		 this.setWaitingPlayer(p.getPlayer(0));
         this.OutPack = new InfoPack("OBSERVER_REFRESH");
         this.OutPack.setPlayers(this.Serv.getPlayers());
         this.OutPack.setBank(this.Serv.getBank());
		 this.OutPack.setLastAction(this.Serv.getLastAction());
		 this.OutPack.setLastRaise(this.Serv.getLastRaise());
		 this.OutPack.setCycle(this.Serv.getCycle());
	     this.out.writeObject(OutPack);
	     this.OutPack = null;
		
	}


	// metoda zwraca strumień wyjśca na socket klienta
	public ObjectOutputStream getOut(){
		return this.out;
	}
	
	// metoda obsługi podbicia i wejścia -- podbicie i wejście róznią się warunkami na etapie kliknięcia w zdarzenie w oknie uzytkownika
	// ich obsługa na serwerze pozostaje wspólna
	private void raiseService(InfoPack pa){
		 InfoPack pack = pa;
		 this.Serv.setLastAction(pack.getLastAction());
		 this.Serv.setLastRaise(pack.getLastRaise());
		 this.Serv.setPlayer(pack.getPlayer(0),this.ClientID);
		 this.Serv.setBank(this.Serv.getBank()+pack.getPlayer(0).getRaise());
		 this.changeRound(); // przekazanie tury na rzecz innego gracza
		 this.Serv.sentToAll("REFRESH");
	}
	
	/**
	 *  metoda obsługi zakończenia wątku klienta
	 *  @param excflag
	 *  			 parametr flagi wyjątku, jeżeli ustawiony na true to oznacza, że metoda została wywołana z obsługi
	 *  			 wyjątku
	 */
	 public void byeService(boolean excflag){
		   	if(this.ClientID!=100){ 
				if(this.Serv.getPlayer(ClientID).getState()==1){
					this.changeRound(); // przekazanie tury na rzecz innego gracza
				}
				 Player play = new Player(this.Serv.getPlayer(this.ClientID).getNickName());
					if(excflag){
					    System.out.println("weszło z wyjątku");
						this.Serv.closeClient(ClientID);
					} 
				 this.OutPack = new InfoPack("PLAYEROUT");
				 this.OutPack.setPlayer(play,0);
				 this.Serv.sentPackToAll(this.OutPack);
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			if(!excflag){
				 this.OutPack = new InfoPack("BYE");
				 try {
					this.out.writeObject(OutPack);
					this.ClientSck.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				 this.Running = false;
				 this.Serv.closeClient(this.ClientID);
				 this.OutPack = null;
				 try {
					Thread.sleep(3000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			this.Serv.sentToAll("REFRESH");
		  }else{
				  this.OutPack = new InfoPack("BYE");
				  try {
					this.out.writeObject(OutPack);
					this.ClientSck.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			      this.Running = false;
			      this.WaitingPlayer = null;
		  }
	   
		  int counter = 0; 
		  for(int i=0;i<4;i++){
			  if(this.Serv.getPlayer(i)!=null){
				  counter = counter +1;
			  }
		  }
		  
		  if(counter == 0){ // obsługa sytuacji gdy ostatni gracz odchodzi od stołu
			  try {
				this.Serv.getSocket().close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		  }else if(counter == 1){ // obsługa sytuacji gdy na stole zostaje jeden gracz
			 if(this.ClientID!=100){
			  this.Serv.compareCards();
			  this.Serv.startGame();
			}
		  }
	 }
	
	
	// metoda obsługi pakietu powitalnego od klienta
	private void helloService(InfoPack pa) throws IOException{
		 InfoPack pack = pa;
		 this.Serv.setPlayer(pack.getPlayer(0),this.ClientID);
	     this.OutPack = new InfoPack("SUCCESS");
	     this.out.writeObject(OutPack);
	     this.OutPack = null;

	}
	
	
	// metoda obsługująca zmianę tury na rzecz kolejnego gracza
	private void changeRound(){
        int highestactiveplayer = this.Serv.highestActivePalyer(); // pobieramy nawyższy identefikator wśród aktywnych klientów i dodajemy jeden
		this.Serv.getPlayer(this.ClientID).setState(0);
        System.out.println("HAP :"+highestactiveplayer);
		boolean flag = true; 
        for(int i=this.ClientID+1;i<=4;i++){
			if((this.ClientID==highestactiveplayer && flag) || i==4){
				i=0;
				this.Serv.setCycle(this.Serv.getCycle()+1);
				flag = false;
			}
			if(this.Serv.getPlayer(i)!=null){
				if(this.Serv.getPlayer(i).getAction()!=3){
					 this.Serv.getPlayer(i).setState(1);
					 return;
				}
			}
		}
	}
	
	// obsługa pasowania przez gracza
	private void passService(InfoPack pa){
		 int active = 0;
		 InfoPack pack =pa;
		 this.Serv.getPlayer(this.ClientID).setAction(pack.getPlayer(0).getAction());
		 this.Serv.getPlayer(this.ClientID).setState(0);
		 for(int i=0;i<4;i++){
			 if(this.Serv.getPlayer(i)!=null){
				 if(this.Serv.getPlayer(i).getAction()!=3){
					 active = active +1;
				 }
			 }
		 }
		 if(active<2){
			 this.Serv.compareCards();
		 }else{
			 this.changeRound();
			 this.Serv.sentToAll("REFRESH"); 
		 }
	}
	
	// obsługa wymiany kart przez gracza
	private void changeCardsService(InfoPack in){ 
		this.Serv.setPlayer(in.getPlayer(0),this.ClientID);
		this.Serv.getPlayer(this.ClientID).getH().sortCards();
		Card c[] = new Card[5]; 
		for(int i=0;i<in.getCardsToChange().length;i++){
			if(in.getCardsToChange()[i]==1){
			 c[i]= this.Serv.getRandomFromDeck();
			}
		 }
		for(int i=0;i<5;i++){
			if(in.getCardsToChange()[i]==1){
			 this.Serv.setAvailiableByID(
			  this.Serv.getPlayer(this.ClientID).getH().getCardFromSet(i).getCardID()
			 );
			}
		 }
		for(int i=0;i<5;i++){
			if(in.getCardsToChange()[i]==1){
			  this.Serv.getPlayer(this.ClientID).getH().setCardInSet(i, c[i]);
			}
		 }
		InfoPack pack = new InfoPack("NEW_CARDS");
        pack.setPlayer(this.Serv.getPlayer(this.ClientID),0);
        try {
			this.out.writeObject(pack);
		} catch (IOException e) {
				this.byeService(true);
		}
        this.Serv.sentToAll("REFRESH");
	}

 /**
  * @param in
  * 		pakiet otrzymany od klienta
  */

	private void checkService(InfoPack in) throws InterruptedException{
		 InfoPack pack = in;
		 this.Serv.setLastAction(pack.getLastAction());
		 this.Serv.setLastRaise(pack.getLastRaise());
		 this.Serv.setPlayer(pack.getPlayer(0),this.ClientID);
		 this.Serv.setBank(this.Serv.getBank()+pack.getPlayer(0).getRaise());
		 pack =null;
		 this.Serv.sentToAll("REFRESH");
		 Thread.sleep(1000);
		 this.Serv.compareCards();
	}

	public int getClientID(){
		return this.ClientID;
	}
	
	public void setRunning(boolean ru){
		this.Running = ru;
	}


	public Player getWaitingPlayer() {
		return WaitingPlayer;
	}


	public void setWaitingPlayer(Player waitingPlayer) {
		WaitingPlayer = waitingPlayer;
	}
	
	public void setClientId(int id){
		this.ClientID = id;
	}
	
	// metoda zwraca wskaźnik na serwer powiązany z tym wątkiem
	public Server getServ(){
		return this.Serv;
	}
}
