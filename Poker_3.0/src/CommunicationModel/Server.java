package CommunicationModel;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;

import Game.Card;
import Game.Hand;
import Game.Player;
import Game.Table;

/**
 *  Klasa serwera
 * 
 */
public class Server extends Table implements Runnable{
	
	private static final long serialVersionUID = 1L;
	private ServerSocket Sck;
	private Card[] DECK; // talia kart
	private boolean[] AVAILABLE; // tablica dostępnych kart;
	private boolean Accept; // flaga ustawiona na flase przerywa akceptację nowych klientów
	private boolean AllowToSentAllCards; // flaga ustawiona na true zwykle podczas sprawdzenia, zezwala serwerowi na rozesłanie wszystkich setów graczy do pojedynczego
										 // jeżeli false to wysyłane są tylko karty danego gracza
	private volatile ServerRequest[] ClientsThr; // tablica wątków klienckich
	private Queue<ServerRequest> ClientsQueue; // kolejka obserwatorów
	private int Port;
	private CyclesCounter Counter; // wątek licznika cykli
	private ObserverService Observer; // wątek obsługi dodawania obserwatorów
	private Exception Exc; // komunikat błędu
	private boolean ExcFlag; // flaga wystapienia błędów



	/**
	 * Konstruktor
	 * @param port 
	 * 			  Parametr przyjmuje numer portu na którym serwer lokalny będzie nasłuchiwał
	*/
	
	public Server(int port){
		super();
		this.DECK = new Card[52];
		this.AVAILABLE = new boolean[52];
		this.Accept = true;
		this.ClientsThr = new ServerRequest[4];
		this.Port = port;
		this.Counter = null;
		this.setClientsQueue(new LinkedList<ServerRequest>());
		this.Observer = null;
		this.AllowToSentAllCards = false;
		this.ExcFlag = false;
		this.Exc = null;
	}
	
	
	@Override
	public void run(){
		try {
			this.Sck = new ServerSocket(this.Port,4);
			// do usunięcia
			// przyjmowanie klientów w pętli
			System.out.println("Serwer nasłuch na porcie : "+this.Port);
			for(int i = 0;i<4;i++){
				 Socket sck = this.Sck.accept();
				 this.ClientsThr[i] = new ServerRequest(this,sck,i);
				 this.ClientsThr[i].start();
				 if(!this.Accept){
					 break;
				 }
			}
			System.out.println("Serwer : koniec akceptowania połączeń");
			Thread.sleep(500);
			this.Observer = new ObserverService(this); // uruchomienie wątku nasłuchu obserwatorów
			this.Observer.start();
			this.startGame(); // start gry
		} catch (Exception e) {
			this.ExcFlag = true;
			this.Exc = e;
		}
	}
	
	/** 
	 * Metoda ustawiająca i rozdająca karty graczom
	 */
	

	public void startGame(){
		// sprawdzenie czy jakiś gracz oczekuje jako obserwator, jeżeli tak to dołączymy go do gry
		ServerRequest thr = null;
		InfoPack pack = null;
		if(this.getClientsQueue().peek()!=null){
			for(int i=0;i<4;i++){
				if(this.getPlayer(i)==null){
					 thr = this.getClientsQueue().poll();
					if(thr != null){
					  if(thr.getWaitingPlayer()!=null){
						  thr.setClientId(i);
						  thr.getServ().setPlayer(thr.getWaitingPlayer(),i);
						  this.setClientsThr(thr,i);
						  this.setPlayer(thr.getWaitingPlayer(),i);
						  pack = new InfoPack("YOU_RE_IN_GAME");
						  pack.setPlayers(this.getPlayers());
						  thr.getServ().sentPack(i,pack);
						  thr = null;
						 
					  }
					}
				}
			}
		}
		
		
		this.AllowToSentAllCards = false; // odmowa na wysyłanie wszystkich zestawów kart do graczy
		
		// przydzielenie talii kart
	
		this.DECK = new Card[52];
		this.AVAILABLE = new boolean[52];
		int k = 0;
		for(int i=0;i<13;i++){
		 for(int j=0;j<4;j++){
			 this.DECK[k]=new Card(j,i,k);
			 this.AVAILABLE[k]=true; 
			 k=k+1;
		 }
		}
		
		
		// generatory losowania kart
		
		Random generator = new Random();
		int randVal = 0;
		for(int i = 0;i<4;i++){
			if(super.getPlayer(i)!=null){ // sprawdzamy czy gracz o danym identyfikatorze istnieje 
				System.out.println("Player : "+i);
				super.getPlayer(i).setH(new Hand());
				int j =0;
				while(j<5){
					randVal = generator.nextInt(51);
					if(this.AVAILABLE[randVal]){
						this.AVAILABLE[randVal]=false;
						super.getPlayer(i).getH().setCardInSet(j,this.DECK[randVal]);
						j=j+1;
					}
				}
			}
		}
		
		/* przydzielenie kart lokalnie na serwerze gotowe, czas na wysłanie 
		 * graczom ich kart od każdego gracza bierzemy 50pkt do puli
		*/
		
		//pobranie od każdego gracza po 50 pkt i dodanie do puli
		for(int i =0; i<4;i++){
			if(super.getPlayer(i)!=null){
				super.getPlayer(i).setPoints(super.getPlayer(i).getPoints()-50);
				super.setBank(super.getBank()+50);
			    super.getPlayer(i).getH().sortCards();
			}
		}

		
       for(int i=0;i<4;i++){
            if(this.getPlayer(i)!=null){
            	this.getPlayer(i).setState(1);
            	break;
            }
       }
         this.sentToAll("REFRESH");
         
		 if(this.Counter!=null){this.Counter.setRunning(false);}
		 this.Counter= null;
		 this.Counter = new CyclesCounter(this,3);
		 this.Counter.start();
	}
	
	/**
	 * Metoda sprawdza czy serwer akceptuje połączenia
	 * @return zwraca wartość pola Accept
	 */
	
	public boolean isAccept() {
		return Accept;
	}
	

	public void setAccept(boolean accept) {
		Accept = accept;
	}
	
	/**
	 *  Metoda przypisuje null do wątku klienckiego po jego zamknięciu
	 * @param index
	 * 				indeks klienta do zamknięcia
	 */
	public synchronized void closeClient(int index){
		this.ClientsThr[index] = null;
		this.setPlayer(null, index);
	}
	/**
	 * Metoda przesyłaja pakiet do klienta 
	 * 
	 * @param clientid
	 * 				Identyfikator klienta do którego wyślemy pakiet
	 * @param pack
	 * 				Pakiet do wysłania
	 */
	public void sentPack(int clientid, InfoPack pack){
		try {
			this.ClientsThr[clientid].getOut().writeObject(pack);
			pack = null;
		} catch (IOException e) {
			this.closeClient(clientid);
		}
	}
	
	
	/**
	 *  Metoda rozsyła aktualny stan stołu dla wszystkich klientów
	 * @param KEY
	 * 			Słowo kluczowe protokołu
	 */
	public void sentToAll(String KEY){
		 String keyword = KEY;
		 InfoPack pack = null; // słowo kluczowe protokołu dla pierwszego rozdania FIRST_DISTRIB
		 Player playe = null;
		 int x; // zmienna indeksów pozostałych graczy w tablicy pakietu graczy do wysłania indeks 0 przeznaczony dla gracza o ID któremu wysyłamy pakiet
		 for(int i = 0; i<4;i++){
			 pack = null;
			 pack =new InfoPack(keyword);
			 pack.setBank(super.getBank());
			 pack.setCycle(super.getCycle());
			 pack.setLastAction(super.getLastAction());
			 pack.setLastRaise(super.getLastRaise());
			 pack.setLastChatMessage(super.getLastChatMessage());
			 if(super.getPlayer(i)!=null){
				 playe = new Player(super.getPlayer(i).getNickName());
				 playe.setPoints(super.getPlayer(i).getPoints());
				 playe.setState(super.getPlayer(i).getState());
				 playe.setRaise(super.getPlayer(i).getRaise());
				 playe.setAction(super.getPlayer(i).getAction());
				 playe.setChanges(super.getPlayer(i).getChanges());
				 playe.setWinner(super.getPlayer(i).getWinner());
				 playe.setOwner(super.getPlayer(i).isOwner());
				 playe.setH(super.getPlayer(i).getH());
				 pack.setPlayer(playe,0);
				 x =1;
				 for(int j=0;j<4;j++){
					 if(i!=j){
						 if(super.getPlayer(j)!=null){
							 playe = new Player(super.getPlayer(j).getNickName());
							 playe.setPoints(super.getPlayer(j).getPoints());
							 playe.setState(super.getPlayer(j).getState());
							 playe.setRaise(super.getPlayer(j).getRaise());
							 playe.setAction(super.getPlayer(j).getAction());
							 playe.setChanges(super.getPlayer(j).getChanges());
							 playe.setWinner(super.getPlayer(j).getWinner());
							 playe.setOwner(super.getPlayer(j).isOwner());
							  if(this.AllowToSentAllCards){
								  playe.setH(super.getPlayer(j).getH());
							  }
							 pack.setPlayer(playe,x);
							 x = x+1;
						 }
					 }
				 }
				 this.sentPack(i,pack);
			 }
		 }
		 
		 if(KEY.equals("REFRESH")){
			 try {
				this.sentToObservers("OBSERVER_REFRESH");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		 }
		 
		 if(KEY.equals("CHAT")){
			 try {
				this.sentToObservers("CHAT");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		 }
	}
	
	/**
	 * Metoda wysyła aktualny stan stołu do wszystkich obserwatorów 
	 * @param key
	 * 			Słowo kluczowe protokołu
	 * @throws IOException
	 * 					Wyjątek wejścia/wyjścia
	 */
	public void sentToObservers(String key) throws IOException{
	 InfoPack pack = null; // słowo kluczowe protokołu dla pierwszego rozdania FIRST_DISTRIB
	 pack =new InfoPack(key);
		 pack.setBank(super.getBank());
		 pack.setCycle(super.getCycle());
		 pack.setLastAction(super.getLastAction());
		 pack.setLastRaise(super.getLastRaise());
		 pack.setLastChatMessage(super.getLastChatMessage());
		Player playe = null;
		int x = 0;
		for(int i= 0;i<4;i++){
			if(super.getPlayer(i)!=null){
				 playe = new Player(super.getPlayer(i).getNickName());
				 playe.setPoints(super.getPlayer(i).getPoints());
				 playe.setState(super.getPlayer(i).getState());
				 playe.setRaise(super.getPlayer(i).getRaise());
				 playe.setAction(super.getPlayer(i).getAction());
				 playe.setChanges(super.getPlayer(i).getChanges());
				 playe.setWinner(super.getPlayer(i).getWinner());
				 playe.setOwner(super.getPlayer(i).isOwner());
				 if(this.AllowToSentAllCards){
					playe.setH(super.getPlayer(i).getH());
				 }
				 pack.setPlayer(playe,x);
				 x = x+1;
			 }
		}
		ServerRequest srvreq = this.ClientsQueue.poll();
		Queue<ServerRequest> que = new LinkedList<ServerRequest>();
		while(srvreq!=null){
			srvreq.getOut().writeObject(pack);
			que.offer(srvreq);
			srvreq = this.ClientsQueue.poll();
		}
		this.ClientsQueue = que;
	    pack = null;
	}
	
	
	public ServerRequest[] getClientsThr() {
		return ClientsThr;
	}
	
	
	public void setClientsThr(ServerRequest clientsThr, int index) {
		ClientsThr[index] = clientsThr;
	}
	
	/**
	 * Metoda wysyła pakiet do wszystkich niepustych klientów w tablicy wątków klienckich
	 * @param pack
	 * 			Pakiet do wysłania
	 */
	public void sentPackToAll(InfoPack pack){
		 for(int j=0;j<4;j++){
			 if(this.ClientsThr[j]!=null){
				 try {
					this.ClientsThr[j].getOut().writeObject(pack);
				} catch (IOException e) {
					this.closeClient(j);
				}
			 }
		 }
	}
	
  public void setAvailiableByID(int id){
	  this.AVAILABLE[id] = true;
  }
  
  /**
   * Metoda pobierająca losową wolną kartę z talii
   * @return
   * 	Karta 
   */
  public Card getRandomFromDeck(){
	  Random generator = new Random();
	  int randVal = generator.nextInt(51);
	   while(!this.AVAILABLE[randVal]){
		  randVal = generator.nextInt(51);
	   }
	   this.AVAILABLE[randVal] = false;
	   return this.DECK[randVal];
  }
  
  /**
   *  Metoda zwraca najwyższy identyfikator spośród aktywnych klientów
   * @return
   * 	Identyfikator klienta
   */
  public int highestActivePalyer(){
		int activeplayers = 0;
		for(int i = 0;i<4;i++){
			if(this.ClientsThr[i]!=null){
			 if(this.getPlayer(i)!= null){
			  if(this.getPlayer(i).getAction()!=3){
				activeplayers = this.ClientsThr[i].getClientID();
			  }
			 }
			}
		}
		return activeplayers;
  }
  
  /**
   *  Metoda przygotowuje graczy i stół do rozpoczęcia nowej rundy
   * 
   */
  private void prepareToNewRound(){
	  for(int i=0;i<4;i++){
		  if(this.getPlayer(i)!=null){
			  this.getPlayer(i).setWinner(0);
			  this.getPlayer(i).setAction(0);
			  this.getPlayer(i).setRaise(0.00);
			  this.getPlayer(i).setChanges(0);
			  this.getPlayer(i).setState(0);
		  }
		  this.setCycle(0);
		  this.setLastAction(0);
		  this.setLastRaise(0.00);
	  }
  }
  

  /**
   *  
   *  Metoda porównuje karty graczy
   *  Po wykonaniu sprawdzenia rozsyła informacje o rozstrzygnięciu do klientów
   */
 
  public void compareCards(){
	  
	  int[] powers = new int[4]; // tablica mocy kart graczy 
	  boolean[] markedtodraw = new boolean[4]; // tablica typowanych do remisu
	  int winerid = 0; // zmienna przechowuje identyfikator gracza typowanego na zwycięzcę
	 
	  
	 // inicjalizacja tablicy remisów
	 for(int i=0;i<4;i++){
		 markedtodraw[i]=false;
	 }

	 int counter2 = 0; // licznik zremisowanych na zasadzie wysoka karta   
	  
	  
	  
	  
	   for(int i=0;i<4;i++){
			 if(this.ClientsThr[i]!=null){
				  if(this.getPlayer(i)!=null){
					 if(this.getPlayer(i).getAction()!=3){
					  powers[i] = this.getPlayer(i).getH().getHandPower();
					 }else{
						powers[i]=0; 
					 }
				  }else{
					  powers[i] = 0;
				  }
			 }else{
				 powers[i] = 0;
			 }
	   }
	   
	   
	   int maxval = powers[0];
	   
	   // typujemy najmocniejszy zbiór
	   for(int i=1;i<4;i++){
			 if(powers[i]>maxval){
				 maxval = powers[i];
				 winerid = i;
			 }  
	   }

	   // teraz sprawdzamy czy wytypowany max jest jedynym w tablicy
	   // jeżeli nie, to trzeba będzie oznaczyć graczy z talią tej samej mocy i porównać największe karty
	   
   	   int counter = 0; // licznik graczy o tej samej mocy kart (najwyższej)
	   boolean[] markedtowin =new boolean[4]; // tablica przechowująca wiadomość o tym którzy gracze mają takie same jednocześnie najmocniejsze talie
	    for(int i=0;i<4;i++){
	    	if(powers[i]==maxval){
	    		markedtowin[i] = true;
	    		counter = counter + 1;
	    	}else{
	    		markedtowin[i] = false;
	    	}
	    }

     if(counter > 1){
		maxval =0;
	    winerid = 0;

	    	for(int i=0;i<4;i++){
	    		if(markedtowin[i]){
	    		   if(this.getPlayer(i)!=null){
		    			if(this.getPlayer(i).getH().getHighestCardNum()>maxval){
		    				maxval = this.getPlayer(i).getH().getHighestCardNum();
		    				winerid = i;

		    			}
	    		   }
	    	     }
	    	}
	    	
	    	// oznaczenie graczy o tej samej mocy kart którzy mają takie same najwyższe karty, jeżeli tak to remis
	    	for(int i=0;i<4;i++){
	    		 if(markedtowin[i]){
	    		   if(this.getPlayer(i)!=null){
		    			if(this.getPlayer(i).getH().getHighestCardNum()==maxval){
		    				markedtodraw[i]=true;
		    				counter2 = counter2 +1 ;
		    				winerid = i;
		    			}
	    		   }
	    		 }
	    	}  	
     }
	    	

	    	
	    	if(counter2 > 1){
	    		//remis
	    		double div = (double)(this.getBank()/counter);
	    		this.setBank(0);
		    	for(int i=0;i<4;i++){
		    		   if(markedtodraw[i]){
		 	    		  if(this.getPlayer(i)!=null){
			    		   if(this.getPlayer(i).getAction()!=3){ 
		    			     this.getPlayer(i).setWinner(2);
	    				     this.getPlayer(i).setPoints(this.getPlayer(i).getPoints()+div);
			    		   }
		 	    		  }
		    		   }
		    	}   	
	    	}else{
	    		// jeden wygrany
	    		  if(this.getPlayer(winerid)!=null){
	    				 if(this.getPlayer(winerid).getAction()!=3){ 
	    					  this.getPlayer(winerid).setWinner(1);
	    					  this.getPlayer(winerid).setPoints(this.getPlayer(winerid).getPoints() + this.getBank());
	    					  this.setBank(0);
	    				 }
	    			  }
	    	}
	    
	     this.AllowToSentAllCards = true;	
	    // rozsyłamy pakiet
		this.sentToAll("REFRESH");
		try {
		    Thread.sleep(10000);
		}catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.prepareToNewRound();
		this.sentToAll("REFRESH");
		this.startGame(); 
  }
  
  public ServerSocket getSocket(){
	  return this.Sck;
  }

public Queue<ServerRequest> getClientsQueue() {
	return ClientsQueue;
}
public void setClientsQueue(Queue<ServerRequest> clientsQueue) {
	ClientsQueue = clientsQueue;
}


public boolean isAllowToSentAllCards() {
	return AllowToSentAllCards;
}


public void setAllowToSentAllCards(boolean allowToSentAllCards) {
	AllowToSentAllCards = allowToSentAllCards;
}


public Exception getExc() {
	return Exc;
}


public void setExc(Exception exc) {
	Exc = exc;
}


public boolean isExcFlag() {
	return ExcFlag;
}


public void setExcFlag(boolean excFlag) {
	ExcFlag = excFlag;
}




  
}
