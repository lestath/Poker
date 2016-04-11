package View;

import javax.swing.JLabel;


// klasa resetująca przekazaną etykietę po upływie określonego w konstruktorze czasu
public class ResetLab implements Runnable {

	JLabel Lab;
	int Ms;
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
