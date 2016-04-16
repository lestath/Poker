package CommunicationModel;

import java.io.Serializable;

import Game.Table;
/**
 * Klasa reprezentująca pakiet wysyłany między serwerem a klientem. Dziedziczy po klasie Table
 */
public class InfoPack extends Table implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String Message;
	private int[] CardsToChange; // tablica kart do wymiany; 


	/**
	 * Konstruktor
	 * @param msg
	 * 			Słowo kluczowe protokołu komunikacj, na jego podstawie wykonywane są odpowiednie usługi
	 */
	public InfoPack(String msg){
		this.Message = msg;
		this.CardsToChange = new int[5];
	}
	
	/**
	 * Metoda zwraca słowo kluczowe protokołu
	 * @return
	 * 		Słowo kluczowe protokołu
	 */
	public String getMessage() {
		return Message;
	}

	/**
	 *  Metoda ustawia słowo kluczowe protokołu
	 * @param message
	 * 				Słowo kluczowe protokołu
	 */
	public void setMessage(String message) {
		Message = message;
	}
	/**
	 * Metoda zwraca tablicę w której oznaczone są karty do wymiany
	 * @return
	 * 		Tablica kart do wymiany (jeżeli 1 to karta o danym indeksie do wymiany, jeżeli 0 to karta o podanym indeksie nie będzie wymieniana)
	 */
	public int[] getCardsToChange() {
		return CardsToChange;
	}
	
	/**
	 * Metoda ustawia tablicę kart do wymiany
	 * @param cardsToChange
	 * 					Tablica kart do wymiany (jeżeli 1 to karta o danym indeksie do wymiany, jeżeli 0 to karta o podanym indeksie nie będzie wymieniana)
	 */
	public void setCardsToChange(int[] cardsToChange) {
		for(int i =0;i<cardsToChange.length;i++){
			this.CardsToChange[i]=cardsToChange[i];
		}
	}

}
