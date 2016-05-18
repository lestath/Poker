package Game;

import java.io.Serializable;

/**
 * Klasa reprezentująca kartę do gry w pokera
 *	Odpowiedniki kolorów 
 *  0 - serce
 *  1 - żołądź
 *  2 - dzwonek
 *  3 - wino
 *  
 * Odpowiedniki figur
 *  0 - As 
 *  [1 -9] jako [2 - 10]
 *  10 - Walet
 *  11 - Dama
 *  12 - Król  
 */
public class Card implements Serializable {
	private static final long serialVersionUID = 1L;
    private int CardID; // identyfikator karty w talii
	private int Color;
	private int Number;

 /**
  * Konstruktor
  * @param col
  * 		Numer koloru karty
  * @param num
  * 		Numer figury karty
  * @param cardid
  * 		Identyfikator karty w talii
  */
	 public Card(int col,int num,int cardid){
		 this.Color = col;
		 this.Number = num;
		 this.CardID = cardid;
	 }
	 /**
	  * Metoda pobierająca numer koloru
	  * @return
	  * 		Zwraca numer koloru
	  */
	 public int getColor(){
		 return this.Color;
	 }
	 /**
	  * Metoda pobierająca numer figury
	  * @return
	  * 		Zwraca numer figury
	  */
	 public int getNumber(){
		 return this.Number;
	 }
	 /**
	  * Metoda pobierająca identyfikator karty
	  * @return
	  * 		Zwraca identyfikator karty
	  */
	public int getCardID() {
		return CardID;
	}
}
