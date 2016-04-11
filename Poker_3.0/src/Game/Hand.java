package Game;

import java.io.Serializable;

public class Hand implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Card[] Cards;
	
	public Hand(){
		this.Cards = new Card[5];
	}
	
	public void setCardInSet(int index, Card c){
		this.Cards[index] = c;
	}
	
	public Card getCardFromSet(int index){
		return this.Cards[index];
	}
	
	
	// metoda sortuje przez wstawianie karty w ręku
	public void sortCards(){
	  if(this.Cards!=null){
		if(this.Cards.length>1){
		     Card pom;
		     int j;
		     for(int i=1; i<this.Cards.length; i++)
		     {
		             //wstawienie elementu w odpowiednie miejsce
		             pom = this.Cards[i]; //ten element będzie wstawiony w odpowiednie miejsce
		             j = i-1;
		 
		             //przesuwanie elementów większych od pom
		             while(j>=0 && this.Cards[j].getNumber()>pom.getNumber()) 
		             {
		                        this.Cards[j+1] = this.Cards[j]; //przesuwanie elementów
		                        --j;
		             }
		             this.Cards[j+1] = pom; //wstawienie pom w odpowiednie miejsce
		     }     
		}
	  }
	}
	
	/*
	 * 10 - poker królewski
	 * 9 - poker
	 * 8 - kareta
	 * 7 - full
	 * 6 - kolor
	 * 5 - strit
	 * 4 - trójka
	 * 3 - dwie pary
	 * 2 - para
	 * 1 - wysoka karta
	 * 0 - ekwiwalent null
	 *
	 */
	// metoda zwraca moc kart od 0 do 9 gdzie 9 to poker królewski
	// sprawdzamy w dół czyli czaczynamy od sprawdzenia czy układ jest najmocniejszym zbiorem
	// tylko wtedy metody sprawdzające zwrócą poprawne wyniki
	public int getHandPower(){
		this.sortCards(); // dla bezpieczeństwa sortujemy układ
		if(this.Cards!=null){
			boolean color = this.isColor(); // do zmiennej flagowej zapisujemy stan czy układ jest kolorem			                                // gdyż nie możemy zwrócić koloru przed sprawdzeniem wyższych 											// od niego układów	
			if(color){
				if(this.isRoyalPoker()){return 10;}
				if(this.isPoker()){return 9;}
			}
			if(this.isCarriage()){return 8;}
			if(this.isFull()){return 7;}
			if(color){return 6;}
			if(this.isStrit()){return 5;}
			if(this.isThree()){return 4;}
			if(this.isTwoPair()){return 3;}
			if(this.isPair()){return 2;}
			return 1;
		}else{
		  return 0;	
		}
	}
	
	public int getHighestCardNum(){
		int max = 0;
		 for(int i=0;i<this.Cards.length;i++){
			 if(this.Cards[i].getNumber()==0){return 13;}
			 if(this.Cards[i].getNumber()>max){
				 max = this.Cards[i].getNumber();
			 }
		 }
		return max;
	}
	
	// metoda sprawdza czy układ jest pokerem królewskim karty muszą być posortowane i przed użyciem 
	// tej metody warunek koloru musi być true;
	private boolean isRoyalPoker(){
        if(this.Cards[0].getNumber()!=0){return false;}
         for(int i=1;i<this.Cards.length;i++){
        	 if(this.Cards[i].getNumber()!=i+8){return false;}
         }
		return true;
	}
	
	// metoda sprawdza czy układ jest pokerem  karty muszą być posortowane i przed użyciem 
	// tej metody warunek koloru musi być true;
	private boolean isPoker(){
		if(this.Cards[0].getNumber()!=0){return false;}
        for(int i=0;i<this.Cards.length;i++){
         if((i+1)==this.Cards.length){break;}
       	 if(this.Cards[i].getNumber()!=(this.Cards[i+1].getNumber()-1)){return false;}
        }
		return true;
	}
	
	// metoda sprawdza czy układ jest karetą
	private boolean isCarriage(){
		if(this.Cards[0].getNumber() == this.Cards[1].getNumber()){
		 for(int i=1;i<(this.Cards.length-1);i++){
			 if((i+1) == (this.Cards.length-1)){break;}
			 if(this.Cards[i].getNumber()!=this.Cards[i+1].getNumber()){return false;}
		 }	
		}else{
			for(int i=1;i<(this.Cards.length);i++){
				 if((i+1) == (this.Cards.length)){break;}
				 if(this.Cards[i].getNumber()!=this.Cards[i+1].getNumber()){return false;}
			 }	
		}
		return true;
	}
	
	// metoda sprawdza czy układ kart jest fullem, karty muszą być posortowane
	private boolean isFull(){
		if(this.Cards[0].getNumber()==this.Cards[1].getNumber()){
			if(this.Cards[2].getNumber()==this.Cards[0].getNumber()){
				if(this.Cards[3].getNumber() == this.Cards[4].getNumber()){return true;}
			}else{
				if(this.Cards[2].getNumber() == this.Cards[3].getNumber()){
					if(this.Cards[2].getNumber()==this.Cards[4].getNumber()){return true;}
				}	
			}
		}
		return false;
	}
	
	// metoda sprawdza czy układ kart jest kolorem
	private boolean isColor(){
		for(int i=0;i<this.Cards.length;i++){
			if((i+1)==Cards.length){break;}
			if(this.Cards[i].getColor()!=this.Cards[i+1].getColor()){return false;}
		}
			return true;
	}
	
	// metoda sprawdza czy układ jest stritem
	// w stricie AS może być liczony jako nasłabsza lub najmocniejsza karta
	private boolean isStrit(){
		int help = 0;
		if(this.Cards[0].getNumber()==0){
			if(this.Cards[1].getNumber()!=1){
			  if(this.Cards[this.Cards.length-1].getNumber()!=12){	
				return false;
			  }
			}
			
			help =1;
		}
        for(int i=help;i<this.Cards.length;i++){
            if((i+1)==this.Cards.length){break;}
          	 if(this.Cards[i].getNumber()!=(this.Cards[i+1].getNumber()-1)){return false;}
           }
   		return true;
	}
	
	// metoda sprawdza czy ukłąd jest trójką (trzy karty z tą samą figurą)
	private boolean isThree(){
		int counter =0;
		for(int i=0;i<this.Cards.length;i++){
			if(i+1==this.Cards.length){break;}
			if(this.Cards[i].getNumber()==this.Cards[i+1].getNumber()){
				counter = counter +1;
				if(counter == 2){return true;}
			}else{
				counter = 0;
			}
		}
			return false;
	}
	
	// metoda sprawdza czy w układzie są dwie pary
	private boolean isTwoPair(){
		boolean firstflag= false ;
		for(int i=0;i<this.Cards.length;i++){
			if((i+1)==this.Cards.length){break;}
			if(this.Cards[i].getNumber()==this.Cards[i+1].getNumber()){
				if(firstflag){return true;}
				firstflag = true;
			}
		}
		return false;
	}
	
	private boolean isPair(){
		for(int i=0;i<this.Cards.length;i++){
			if((i+1)==this.Cards.length){break;}
			if(this.Cards[i].getNumber()==this.Cards[i+1].getNumber()){
				return true;
			}
		}
		return false;
	}
}
