package View;

import javax.swing.JLabel;


/**
 *  Klasa resetująca przekazaną etykietę po upływie określonego w konstruktorze czasu
 */
public class ResetLab implements Runnable {

	JLabel Lab;
	int Ms;
	/**
	 *  Konstruktor obiektu resetującego
	 * @param lab
	 * 			etykieta, którą chcemy zresetować
	 * @param milisec
	 * 			czas po jakim nastapi reset podawany w ms
	 */
	ResetLab(JLabel lab,int milisec){
		this.Lab = lab;
		this.Ms = milisec;
	}
	@Override
	public void run() {
		try {
			Thread.sleep(this.Ms);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Lab.setText(" ");
	}

}
