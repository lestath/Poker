package View;

/**
 * 
 * Wątek odliczający czas w dół od podanego w konstruktorze w sekundach
 * Wykorzystywany gdy gracz otrzymuje turę i jeżeli nie wykona żadnych działań przed upływem czasu to automatycznie passuje.
 *
 */
public class MyTimer extends Thread{

	private int Seconds;
	private boolean Stop;
	private boolean Exi;
	private GameFrame Frm;
	

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
	
	
	public GameFrame getFrm() {
		return Frm;
	}
	public void setFrm(GameFrame frm) {
		Frm = frm;
	}
	
	public void exit(){
		this.Exi = false;
	}
	
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
