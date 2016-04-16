package View;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.text.DecimalFormat;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

import CommunicationModel.Client;

public class GraphPanel extends JPanel implements MouseListener{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private BufferedImage TokenImg; // obrazek żeton tury ustawiany przy graczu który ma swoją kolej ruchu
	private URL TokenUrl; // adres obrazka żeton tury
	private boolean PlayFlag;
	private Client Cli;
	private URL[] cardurl;//tablica ścieżek dla kart
	private BufferedImage[] CARDS_IMG; //tablica obrazków kart 
	private Dimension[] DimCardsTab; // tablica przechowuje pozycje kart gracza w panelu graficznym
	private String OutPlayer; // nick gracza opuszczającego grę
	private int[] CardsToChange; // tablica kart oznaczonych do wymiany
	private boolean ObserverMode; // flaga trybu obserwatora
	private GameFrame Frame; //okienko gracza
	
	public GraphPanel(int w,int h,GameFrame frm){
		this.ObserverMode = false;
		this.setPreferredSize(new Dimension(w,h));
		this.setOpaque(false);
		this.PlayFlag = false;
		this.Cli = null;
		this.TokenUrl = getClass().getResource("/img/token.png");
		this.CARDS_IMG = new BufferedImage[5]; // tablica obrazków kart gracza
		this.cardurl = new URL[5]; // tablica adresów do obrazków
		this.OutPlayer = null;
		this.DimCardsTab = new Dimension[5];
		this.CardsToChange = new int[5];
		this.addMouseListener(this);
		this.setObserverMode(false);
		this.Frame = frm;
		for(int i=0;i<5;i++){
			this.DimCardsTab[i] = new Dimension(0,0);
			this.CardsToChange[i] = 0;
		}
		try {
			this.TokenImg =ImageIO.read(this.TokenUrl);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.repaint();
	}

	// metoda rysująca na panelu
	@Override
	protected void paintComponent(Graphics g){
		Graphics2D g2d = (Graphics2D)g;
		g2d.setFont(new Font("DejaVu Sans",Font.BOLD,13));
		g2d.setColor(Color.white);
		if(this.PlayFlag){
			if(!this.ObserverMode){
			 g2d.setColor(Color.WHITE);
			 this.showClientHandPower(g2d);
			 this.showClientCards(g2d);
			}else if(this.isEnd()){
			 this.showClientCards(g2d);	
			}
			this.showBank(g2d);
			this.placeRestPalyers(g2d,0);
			 if(this.OutPlayer!=null){
				 this.Frame.setMsg("<html>Gracz: "+this.OutPlayer+"<br>Opuścił grę</html>",Color.ORANGE);
				 this.OutPlayer = null;
			 }
			
		}
	}
	


	/* 
	 * metoda zwraca true, jeżeli nastapił koniec rundy i wyłoniono zwycięzców
	 * sprawdza poprzez przejrzenie pola Winner w tablicy graczy 
	 * 
	 */  
	private boolean isEnd() {
		for(int i = 0;i<4;i++){
			if(this.Cli!=null){
				if(this.Cli.getPlayer(i)!=null){
					if(this.Cli.getPlayer(i).getWinner()>0){
						return true;
					}
				}
			}
		}
		return false;
	}

	public void setPlayFlag(boolean f){
		this.PlayFlag = f;
	}

	public void setCli(Client cli) {
		Cli = cli;
	}
	
	/*
	 *  metoda wyłożenia kart klienta
	 *  jeżeli nastapił koniec tury wykłada również karty pozostałych
	 */
	
	private void showClientCards(Graphics2D g2d){
	   boolean endround = this.isEnd();
	   int w =0;// szerokość na stole
	   int h =0;//wysokosć na stole
	 
	   for(int j=0;j<4;j++){ //iteruje po graczach , jeżeli to nie koniec rozdania to zaończy się po jednej iteracji
		 if(this.Cli.getPlayer(j)!=null){
			 // wypozycjonowanie kart w oknie  dla odpowiednich graczy 
			 switch(j){
			     case 0:
			    	 w = 180;
			    	 h = 370;
			     break;
				 case 1:
					 h =50;
				 break;
				 case 2:
					 w = 20;
					 h = 250;
				 break;
				 case 3 :
					 w = 240;
					 h = 130; 
			     break; 
			 }
			 
				 for(int i=0;i<5;i++){  // iteruje po kartach
					String ur = new String("/img/cards/");
				 	ur = ur+this.Cli.getPlayer(j).getH().getCardFromSet(i).getColor();
				 	ur = ur+this.Cli.getPlayer(j).getH().getCardFromSet(i).getNumber();
				 	ur = ur+".png";
			 		cardurl[i] = getClass().getResource(ur);
			 		try {
			 			this.CARDS_IMG[i] = ImageIO.read(cardurl[i]);
			 			if(this.CardsToChange[i]==0){
			 				this.DimCardsTab[i].setSize((i*70)+w,h);
			 			}else{
			 				this.DimCardsTab[i].setSize((i*70)+w,h-10);
			 			}
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
						g2d.drawImage(this.CARDS_IMG[i],this.DimCardsTab[i].getSize().width,this.DimCardsTab[i].getSize().height,this);
			 	}	
				if(this.Frame.getStartBtn()!=null){	
						this.Frame.getStartBtn().setEnabled(false);
				}
			 }
		 if(!endround){
			 return;
		  }else{
			  this.Frame.disableGameBtns();
		  }
	   }
	}
	// metoda dekoduje cyfrowy kod akcji klienta na słowny
	private String extractActionStatus(int s){
		String status = null;
		switch(s){
			case 0 :
				status = new String("Czekam");
			break;
			case 1 :
				status = new String("Podbijam");
			break;
			case 2 :
				status = new String("Wymieniam");
			break;
			case 3 :
				status = new String("Pass");
			break;
			case 4 :
				status = new String("Sprawdzam");
			break;
			case 5 :
				status = new String("Wchodzę");
			break;
			default:
				status = new String("Czekam");
			break;
			
		}
		return status;
	}
	
	
	// metoda umieszcza pozostałych graczy wokół stołu
	/*
	 * @param zmienna panelu graficznego
	 * @param zmienna ustalająca od którego elementu tablicy nastepuje rysowanie graczy
	 */
	private void placeRestPalyers(Graphics2D g2d,int start){
		DecimalFormat df= new DecimalFormat("#.##");
		 String action;// łańcuch znaków akcji;
		 
		int w =330;
		int h = 300;
		int tokenw = 0; // położenie na serokości stołu dla żeton tury
		int tokenh=0;; //położenie na wysokości stołu dla żeton tury
		
		 for(int j=start;j<4;j++){
			 switch(j){
			     case 0:
			    	 w = 320;
			    	 h = 290;
			    	 tokenh = h - 60;
			    	 tokenw = w;
			     break;
				 case 1:
					 h =20;
					 tokenh=110;
					 tokenw=w;
				 break;
				 case 2:
					 w = 30;
					 h = 200;
					 tokenh = 170;
					 tokenw = w+100;
				 break;
				 case 3 :
					 w = 570;
					 h = 200; 
					 tokenw = 490;
					 tokenh = h;
			     break;
			 }
			 
			 if(this.Cli.getPlayer(j)!=null){
				 if(this.Cli.getPlayer(j).getWinner()==1){
					 g2d.setColor(Color.ORANGE);
				 }
				 g2d.drawString(this.Cli.getPlayer(j).getNickName(),w,h); 
				 String points = df.format(this.Cli.getPlayer(j).getPoints());
				  action = this.extractActionStatus(this.Cli.getPlayer(j).getAction());
				  if(this.Cli.getPlayer(j).getAction() == 1 || this.Cli.getPlayer(j).getAction() == 5){
					  action = action + " "+ df.format(this.Cli.getPlayer(j).getRaise());
				  }
				 g2d.drawString(
							"$ : "+
							points,
							w,
							h+24
							);
					g2d.drawString(
							"Status: "+
							action,
							w,
							h+40
							);
					if(this.Cli.getPlayer(j).getState()==1){
						g2d.drawImage(this.TokenImg,tokenw,tokenh,this);
					}
					
				 // informacje o zwycięzcach
				 if(this.Cli.getPlayer(0).getWinner()==1){
					 this.Frame.setMsg("<html>ZAGARNIASZ PULĘ</html>",Color.GREEN);
				 }else if(this.Cli.getPlayer(j).getWinner()==1){	 
					 this.Frame.setMsg("<html> WYGRYWA GRACZ <br> "+this.Cli.getPlayer(j).getNickName()+"</html>",Color.MAGENTA);
				 }else if(this.Cli.getPlayer(j).getWinner()==2){
					 this.Frame.setMsg("REMIS",Color.orange);
				 }
				 g2d.setColor(Color.WHITE);
			 }
		 }
	}
	
	// metoda pokazuje aktualną pulę na stole
	private void showBank(Graphics2D g2d){
		DecimalFormat df = new DecimalFormat("#.##");
		String points = df.format(this.Cli.getBank());
		g2d.setColor(Color.WHITE);
		g2d.drawString("Bank",320,170);
		g2d.drawString("$ : "+points,320,190);
		g2d.drawString("Cycle: "+this.Cli.getCycle(),320,210);
	}
	
	private void showClientHandPower(Graphics2D g2d){
		if(this.Cli.getPlayer(0)!=null){
		  if(this.Cli.getPlayer(0).getH()!=null){
				String handpow = " ";
				switch(this.Cli.getPlayer(0).getH().getHandPower()){
				 case 1:
					 handpow = "Wysoka Karta";
				 break;
				 case 2:
					 handpow = "Para";
				 break;
				 case 3:
					 handpow = "Dwie Pary";
				 break;
				 case 4:
					 handpow = "Trójka";
				 break;
				 case 5:
					 handpow = "Strit";
				 break;
				 case 6:
					 handpow = "Kolor";
				 break;
				 case 7:
					 handpow = "Full";
				 break;
				 case 8:
					 handpow = "Kareta";
				 break;
				 case 9:
					 handpow = "Poker";
				 break;
				 case 10:
					 handpow = "Poker Królewski";
				 break;
				 default :
					 handpow = " ";
				 break;
				}
				g2d.drawString(handpow,300,470);
		  }
		}
	}
	
	// medota pokazuje info o opuszczeniu gry przez podanego gracza
	public void setPlayerOutMsg(String name){
		this.OutPlayer = name;
		this.repaint();
	}
	
	

	public int[] getCardsToChange() {
		return this.CardsToChange;
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		int px = 0;
		int py = 0;
		px = e.getX();
		py = e.getY();
		for(int i=0;i<5;i++){
			if(this.CARDS_IMG[i]!=null){
				if(px >= this.DimCardsTab[i].getSize().width){
					if(px <= (this.DimCardsTab[i].getSize().width+this.CARDS_IMG[i].getWidth())){
						if(py >= this.DimCardsTab[i].getSize().height){
							if(py <= (this.DimCardsTab[i].getSize().height+this.CARDS_IMG[i].getHeight())){
								if(this.CardsToChange[i]==1){
									this.CardsToChange[i]=0;
								}else{
									this.CardsToChange[i]=1;
								}
							 this.repaint();
							}
						}
					}
					
				}
			}
		}
		
	}

	
	public void resetToChangeTab(){
		for(int i=0;i<this.CardsToChange.length;i++){
			this.CardsToChange[i] = 0;
		}
	}
	
	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	public boolean isObserverMode() {
		return ObserverMode;
	}

	public void setObserverMode(boolean observerMode) {
		ObserverMode = observerMode;
	}


}
