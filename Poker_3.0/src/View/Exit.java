package View;
/**
 *  Wątek obsługujący poprawne wyjście z okna aplikacji, wprowadza opóźnienie pozwalające 
 *  na wyświetlenie komunikatu przed zamknięciem
 */
public class Exit implements Runnable{
 
private int Time;
GameFrame Frame;
	
 public Exit(GameFrame frm,int time){
	 this.Time = time;
	 this.Frame = frm;
 }

@Override
	public void run() {
		try {
			Thread.sleep(Time);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.Frame.dispose();
	}
}
