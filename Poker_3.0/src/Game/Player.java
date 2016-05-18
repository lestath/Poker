package Game;

import java.io.Serializable;

/**
 * 
 * Klasa reprezentuje gracza
 *
 */

public class Player implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String NickName;
	private double Raise;
	private double Points;
	private int Changes; // ilosć wymian
	private int HandPower; // moc ręki gracza
	private int Winner; // jezeli true to gracz zwyciężył rozgrywkę
	private boolean Owner; // flaga ustawiona na true, jeżeli ten klient jest jednocześnie właścicielem serwera
	/* 
	 * 0 - waiting  
	 * 1 - his turn  
	 */
	private int State;
	/*
	 * 0 - none
	 * 1 - raise
	 * 2 - change
	 * 3 - pass
	 * 4 - check
	 * 5 - in
	 * 6 - from observer to player (gracz wchodzi na stół z pozycji obserwatora)
	 */
	private int Action;
	private Hand H;
	
	
	/**
	 * Konstruktoe z jednym parametrem
	 * @param Nick
	 * 			Nazwa gracza
	 */
	public Player(String Nick){
		this.Action =0;
		this.H = null;
		this.NickName = Nick;
		this.Points = 0.00;
		this.setRaise(0.00);
		this.Owner = false;
		this.State = 0;
		this.setChanges(0);
		this.setWinner(0);
	}

	/**
	 * Konstruktor z dwoma parametrami
	 * @param Nick
	 * 			Nazwa gracza
	 * @param owner
	 * 			Flaga wskazując, czy gracz jest właścicielem serwera
	 */
	public Player(String Nick,boolean owner){
		this(Nick);
		this.Owner = owner;
	}
	
	public String getNickName() {
		return NickName;
	}

	public void setNickName(String nickName) {
		NickName = nickName;
	}

	public double getPoints() {
		return Points;
	}

	public void setPoints(double points) {
		Points = points;
	}

	public int getState() {
		return State;
	}

	public void setState(int state) {
		State = state;
	}

	public int getAction() {
		return Action;
	}

	public void setAction(int action) {
		Action = action;
	}

	public Hand getH() {
		return H;
	}

	public void setH(Hand h) {
		H = h;
	}

	public double getRaise() {
		return Raise;
	}

	public void setRaise(double raise) {
		Raise = raise;
	}

	public int getChanges() {
		return Changes;
	}

	public void setChanges(int changes) {
		Changes = changes;
	}

	public int getHandPower() {
		return HandPower;
	}

	public void setHandPower(int handPower) {
		HandPower = handPower;
	}

	public int getWinner() {
		return Winner;
	}

	public void setWinner(int winner) {
		Winner = winner;
	}

	public boolean isOwner() {
		return Owner;
	}

	public void setOwner(boolean owner) {
		Owner = owner;
	}

}
