package CommunicationModel;

import java.io.Serializable;

import Game.Table;

public class InfoPack extends Table implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String Message;
	private int[] CardsToChange; // tablica kart do wymiany; 


	public InfoPack(String msg){
		this.Message = msg;
		this.CardsToChange = new int[5];
	}
	public String getMessage() {
		return Message;
	}

	public void setMessage(String message) {
		Message = message;
	}
	public int[] getCardsToChange() {
		return CardsToChange;
	}
	public void setCardsToChange(int[] cardsToChange) {
		for(int i =0;i<cardsToChange.length;i++){
			this.CardsToChange[i]=cardsToChange[i];
		}
	}

}
