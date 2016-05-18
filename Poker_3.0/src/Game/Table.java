package Game;

import java.io.Serializable;


/**
 * 
 * Klasa reprezentująca stół
 *
 */
public class Table implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private double Bank;
	private int LastAction; // zmienna ostatnio wykonanej akcji
	private double LastRaise; // zmienna ostatniego podbicia
	private int Cycle; // numer cyklu gr (kiedy każdy gracz wykona ruch cykl zwiększa się)
	private String LastChatMessage; //ostatnia wiadomość na chat 
	private Player[] Players;
	
	/**
	 * Konstruktor bezparametrowy
	 */
	public Table(){
		this.setLastAction(0);
		this.setLastRaise(0.00);
		this.Players = new Player[4];
		this.Cycle = 0;
		for(int i =0; i<4;i++){
			this.Players[i] = null;
		}
		
	}
	
	public double getBank() {
		return Bank;
	}
	public void setBank(double bank) {
		Bank = bank;
	}
	public Player[] getPlayers() {
		return Players;
	}
	public void setPlayers(Player[] players) {
		for(int i=0;i<this.Players.length;i++){
			this.Players[i]=players[i];
		}
	}
	
	public Player getPlayer(int index){
		return Players[index];
	}
	
	public void setPlayer(Player play, int index){
		this.Players[index] = play;
	}

	public int getLastAction() {
		return LastAction;
	}

	public void setLastAction(int lastAction) {
		LastAction = lastAction;
	}

	public double getLastRaise() {
		return LastRaise;
	}

	public void setLastRaise(double lastRaise) {
		LastRaise = lastRaise;
	}

	public int getCycle() {
		return Cycle;
	}

	public void setCycle(int cycle) {
		Cycle = cycle;
	}

	public String getLastChatMessage() {
		return LastChatMessage;
	}

	public void setLastChatMessage(String lastChatMessage) {
		LastChatMessage = lastChatMessage;
	}
}
