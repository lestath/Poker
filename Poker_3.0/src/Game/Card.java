package Game;

import java.io.Serializable;
/*
 * Colors 
 *  0 - heart
 *  1 - club
 *  2 - diamond
 *  3 - spade
 *  
 * Numbers
 *  0 - Ace 
 *  [1 -9] as [2 - 10]
 *  10 - Jack
 *  11 - Queen
 *  12 - King  
 */

public class Card implements Serializable {
 /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
    private int CardID; // identyfikator karty w talii
	private int Color;
	private int Number;

 
	 public Card(int col,int num,int cardid){
		 this.Color = col;
		 this.Number = num;
		 this.CardID = cardid;
	 }
	 
	 public int getColor(){
		 return this.Color;
	 }
	 
	 public int getNumber(){
		 return this.Number;
	 }

	public int getCardID() {
		return CardID;
	}
}
