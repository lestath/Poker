package View;

/**
 * 
 * Wątek odliczający czas w dół od podanego w konstruktorze w sekundach
 * Wykorzystywany gdy gracz otrzymuje turę i jeżeli nie wykona żadnych działań przed upływem czasu to automatycznie pasuje.
 *
 */
public class MyTimer extends Thread{

	private int Seconds;
	private boolean Stop;
	private boolean Exi;
	private GameFrame Frm;
	

	/**
	 * Konstruktor
	 * @param seconds
	 * 				Liczba sekund, od której rozpoczyna się odliczanie
	 */
	public MyTimer(int seconds){
		this.Seconds = seconds;
		this.Stop = true;
		this.Exi = true;
	}
	/**
	 * Metoda zatrzymująca odliczanie
	 */
	public void stopTimer(){
	  this.Stop = true;
	}
	
	/**
	 * Metoda startująca timer
	 */
	public void startTimer(){
	  this.Stop = false;
	}
	
	/**
	 * Metoda zwraca ramkę, która wywołała timer
	 * @return
	 * 		Ramka, która wywołała timer
	 */
	public GameFrame getFrm() {
		return Frm;
	}
	/**
	 * Metoda ustawia ramkę, która wywołuje timer
	 * @param frm
	 * 			Parametr ramki
	 */
	public void setFrm(GameFrame frm) {
		Frm = frm;
	}
	
	/**
	 * Metoda kończy działanie timera
	 */
	public void exit(){
		this.Exi = false;
	}
	
	/**
	 * Metoda wywoływana przy starcie wątku, stanowi implementację wątku timera
	 */
	public void run() {
		while(this.Exi){
		  if(!this.Stop){
			for(int t=this.Seconds;t>0;t--){
				if(this.Stop){break;}
				if(!this.Exi){return;}
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					return;
				}
				this.Frm.getTimeLab().setText("Czas : "+t+"s");
			}
			if(!this.Exi){return;}
			if(!this.Stop){this.Frm.passService();}
			
		  }
		  	try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
					return;
			}
		  continue;
	  }
	}

	
}
