package CommunicationModel;
/*
 * klasa wątku zliczającego cykle w grze
 */
public class CyclesCounter extends Thread {
    private Server Srv; // wskaźnik serwera
    private int Cycles; // pole wskazuje ile cykli trwa runda
    boolean Running; 

      
	public CyclesCounter(Server srv, int cycles) {
		Srv = srv;
		this.Cycles = cycles;
		Running = true;
	}
	
	@Override
	public void run(){
		int la =0;
		int ccl = this.Srv.getCycle();
		while(ccl<this.Cycles){
			ccl = this.Srv.getCycle();
			la = this.Srv.getLastAction();
			try {
				Thread.sleep(200);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if(la==4){
				return;
			}
			if(!Running){return;}
			continue;
		}
		this.Srv.compareCards();
		return;
	}
	
	public void setRunning(boolean r){
		this.Running = r;
	}
	
}
