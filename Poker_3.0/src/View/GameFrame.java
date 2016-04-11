package View;


import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import CommunicationModel.Client;
import CommunicationModel.InfoPack;
import CommunicationModel.Server;


public class GameFrame extends JFrame implements Runnable,ActionListener,KeyListener{

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Client Cli;
	private GraphPanel Gp;
	private JPanel BtnPanel;
	private JButton StartGameBtn; //przycisk wcześniejszego rozpoczęcia gry [start gry] dostępny jedynie dla właściciela stołu
	private JButton ExitGameBtn; // przycisk bezpiecznego wyjścia z gry
	private JButton ImInBtn; // przycisk [wchodzę]
	private JButton RaiseBtn; // przycisk [podbijam]
	private JButton ChangeBtn; // przycisk [wymiana]
	private JButton CheckBtn; // przycisk [sprawdzam]
	private JButton PassBtn; // przycisk [pass]
	private JButton ChatBtn; // przycisk [ok] dla wysłania wiadomości chat
	private JLabel PointsLab; // etykietka dolara;
	private volatile JLabel MsgLab;
	private JTextField PointsTf; //pole tekstowe do podbijania stawki
	private Server Srv; //
	private MainApp MainFrame;
	private JLabel PlayerInfoLab;
	private JScrollPane ChatSp; // panel scrollowany służący jako kontener dla chatu klientów
	private JTextArea ChatTa; // przestrzeń tekstowa do wyświetlania wiadomości z chatu użytkowników
	private JTextField ChatTf; // pole tekstowe gdzie klient wpisuje wiadomość do wysłania na chat
	
	public GameFrame(String nick,double startpoints, String host, int port,boolean owner,Server serv,MainApp mainfrm){
		super("Poker");
		this.setSize(900,550);
	    this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
	    this.addWindowListener(new WindowAdapter() {
	        @Override
	        public void windowClosing(WindowEvent event) {
	            exitProcedure();
	        }

	    });
		BufferedImage img = null;
		try {
		    img = ImageIO.read(getClass().getResource("/img/bg.jpg"));
		} catch (IOException e) {
		    e.printStackTrace();
		}
		Image dimg = img.getScaledInstance(900, 550, Image.SCALE_SMOOTH);
		ImageIcon imageIcon = new ImageIcon(dimg);
		JLabel backg = new JLabel(imageIcon);
		backg.setPreferredSize(new Dimension(900,550));
 		setContentPane(backg);
		
		this.setLayout(new FlowLayout(FlowLayout.LEADING));

		this.setLocationRelativeTo(null);
		this.setResizable(false);
		this.Srv = null;
		this.Gp = new GraphPanel(700,500,this); // inicjalizacja panelu graficznego
		this.Cli = new Client(nick,startpoints,host,port,this,owner); // inicjalizacja klienta 
	    
		// powiązanie klienta z panelem graficznym
	     this.Gp.setCli(this.Cli);
		
		
		this.setMainFrame(mainfrm);
		
		new Thread(this.Cli).start();
		
		this.BtnPanel = new JPanel();
		this.BtnPanel.setPreferredSize(new Dimension(185,500));
		this.BtnPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
		this.BtnPanel.setOpaque(false);
		 
		 Dimension btndim = new Dimension(180,24);
		 this.StartGameBtn = new JButton("Start Gry");
		 this.StartGameBtn.setPreferredSize(btndim);
		 this.StartGameBtn.setFocusPainted(false);
		 this.StartGameBtn.addActionListener(this);
		 this.StartGameBtn.setBackground(Color.BLACK);
		 this.StartGameBtn.setForeground(Color.WHITE);
		 
		 this.ExitGameBtn = new JButton("Wyjdź z Gry");
		 this.ExitGameBtn.setPreferredSize(btndim);
		 this.ExitGameBtn.setFocusPainted(false);
		 this.ExitGameBtn.addActionListener(this);
		 this.ExitGameBtn.setBackground(Color.BLACK);
		 this.ExitGameBtn.setForeground(Color.WHITE);
		 
		 // etykieta informacji o graczu
		 this.PlayerInfoLab = new JLabel();
		 this.PlayerInfoLab.setPreferredSize(new Dimension(170,40));

		 // etykieta przekazująca informacje 
		 this.MsgLab = new JLabel();
		 this.MsgLab.setPreferredSize(new Dimension(170,60));

		 // pole podbijania stawki
		 this.PointsLab = new JLabel("$");
		 this.setPreferredSize(new Dimension(50,24));
		 
		 this.PointsTf = new JTextField("0.00");
		 this.PointsTf.setPreferredSize(new Dimension(130,24));
		 this.PointsTf.addKeyListener(this);
		 this.PointsTf.setEditable(false);
		 
		 
		 // przyciski gry
		 this.ImInBtn = new JButton("Wchodzę");
		 this.ImInBtn.setPreferredSize(btndim);
		 this.ImInBtn.setFocusPainted(false);
		 this.ImInBtn.addActionListener(this);
		 this.ImInBtn.setBackground(Color.BLACK);
		 this.ImInBtn.setForeground(Color.WHITE);

		 
		 this.RaiseBtn = new JButton("Podbijam");
		 this.RaiseBtn.setPreferredSize(btndim);
		 this.RaiseBtn.setFocusPainted(false);
		 this.RaiseBtn.addActionListener(this);
		 this.RaiseBtn.setBackground(Color.BLACK);
		 this.RaiseBtn.setForeground(Color.WHITE);
		 
		 this.ChangeBtn = new JButton("Wymiana");
		 this.ChangeBtn.setPreferredSize(btndim);
		 this.ChangeBtn.setFocusPainted(false);
		 this.ChangeBtn.addActionListener(this);
		 this.ChangeBtn.setBackground(Color.BLACK);
		 this.ChangeBtn.setForeground(Color.WHITE);
		 
		 this.CheckBtn = new JButton("Sprawdzam");
		 this.CheckBtn.setPreferredSize(btndim);
		 this.CheckBtn.setFocusPainted(false);
		 this.CheckBtn.addActionListener(this);
		 this.CheckBtn.setBackground(Color.BLACK);
		 this.CheckBtn.setForeground(Color.WHITE);
	    
		 this.PassBtn = new JButton("Pass");
		 this.PassBtn.setPreferredSize(btndim);
		 this.PassBtn.setFocusPainted(false);
		 this.PassBtn.addActionListener(this);
		 this.PassBtn.setBackground(Color.BLACK);
		 this.PassBtn.setForeground(Color.WHITE);
		 
		 this.disableGameBtns(); // wyłączenie przycisków gry
		 
		 
		 // inicjalizacja elementów Chatu użytkowników
		 	  
		  this.ChatTa = new JTextArea();
		  this.ChatTa.setEditable(false);
		  this.ChatTa.setLineWrap(true);
		  this.ChatTa.setWrapStyleWord(true);
		 
		  
		  this.ChatSp = new JScrollPane(this.ChatTa);
		  this.ChatSp.setPreferredSize(new Dimension(180,120));
		  this.ChatSp.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		  this.ChatSp.setVisible(true);



		  this.ChatTf = new JTextField();
		  this.ChatTf.setPreferredSize(new Dimension(120,24));
		  this.ChatTf.setVisible(true);
		  this.ChatTf.addKeyListener(this);
		  
		  this.ChatBtn = new JButton("^");
		  this.ChatBtn.setPreferredSize(new Dimension(50,24));
		  this.ChatBtn.setFocusPainted(false);
		  this.ChatBtn.addActionListener(this);
		  this.ChatBtn.setBackground(Color.BLACK);
		  this.ChatBtn.setForeground(Color.WHITE);
		  
		 
		   if(serv == null){
			   this.StartGameBtn.setVisible(false);
			   this.Srv = null;
		   }else{
			   this.Srv = serv;
			   this.Cli.setSrv(this.Srv);
		   }
	 
		this.BtnPanel.add(this.PlayerInfoLab);
		this.BtnPanel.add(this.MsgLab);
		this.BtnPanel.add(this.PointsLab);
		this.BtnPanel.add(this.PointsTf);
		this.BtnPanel.add(this.ImInBtn);
		this.BtnPanel.add(this.RaiseBtn);
		this.BtnPanel.add(this.ChangeBtn);
		this.BtnPanel.add(this.CheckBtn);
		this.BtnPanel.add(this.PassBtn);
		this.BtnPanel.add(this.ChatSp);
		this.BtnPanel.add(this.ChatTf);
		this.BtnPanel.add(this.ChatBtn);
		this.BtnPanel.add(this.StartGameBtn);
		this.BtnPanel.add(this.ExitGameBtn);

		
		add(this.Gp);
		add(this.BtnPanel);
			if(!owner){
				this.disableGameBtns();
			}
	    this.setVisible(true);   
	    this.PlayerInfoLab.setForeground(Color.WHITE);
	    this.PlayerInfoLab.setText("Gracz : "+nick);
	}

	
	private void exitProcedure() {
		this.Cli.sentPack(new InfoPack("BYE"));
	}
	

	public JLabel getPlayerInfoLab() {
		return PlayerInfoLab;
	}

	public void setPlayerInfoLab(JLabel playerInfoLab) {
		PlayerInfoLab = playerInfoLab;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		Object src = e.getSource();
		if(src == this.StartGameBtn){
			this.Srv.setAccept(false);
			this.StartGameBtn.setEnabled(false);
		}else if(src == this.ExitGameBtn){
				 this.Cli.sentPack(new InfoPack("BYE"));
		}else if(src == this.RaiseBtn){
			this.raiseService();
		}else if(src == this.ImInBtn){
			this.imInService();
		}else if(src == this.PassBtn){
			this.passService();
		}else if(src == this.ChangeBtn){
			this.changeCardsService();
		}else if(src == this.CheckBtn){
			this.checkService();
		}else if(src == this.ChatBtn){
			this.chatMessageSentService();
		}	
	}

	public MainApp getMainFrame() {
		return MainFrame;
	}

	public void setMainFrame(MainApp mainFrame) {
		MainFrame = mainFrame;
	}
	
	public GraphPanel getGp(){
		return this.Gp;
	}
	
	
	//metoda wyłącza przyciski gry
	public void disableGameBtns(){
		this.ImInBtn.setEnabled(false);
		this.RaiseBtn.setEnabled(false);
		this.ChangeBtn.setEnabled(false);
		this.CheckBtn.setEnabled(false);
		this.PassBtn.setEnabled(false);
		this.PointsTf.setEditable(false);
	}
	//metoda włącza przyciski gry
	public void enableGameBtns(){
		this.ImInBtn.setEnabled(true);
		this.RaiseBtn.setEnabled(true);
		if(this.Cli.getPlayer(0).getChanges()<1){
			this.ChangeBtn.setEnabled(true);
		}else{
			this.ChangeBtn.setEnabled(false);
		}
		this.CheckBtn.setEnabled(true);
		this.PassBtn.setEnabled(true);
		this.PointsTf.setEditable(true);
	}
	
	
	// metoda przekazuje wiadomości do okna gry
	public void setMsg(String msg,Color col){
		this.MsgLab.setForeground(col);
		this.MsgLab.setText(msg);
		new Thread(new ResetLab(this.MsgLab,3000)).start();
	}
	
	
	
	public JButton getImInBtn() {
		return ImInBtn;
	}

	public void setImInBtn(JButton imInBtn) {
		ImInBtn = imInBtn;
	}

	public JButton getRaiseBtn() {
		return RaiseBtn;
	}

	public void setRaiseBtn(JButton raiseBtn) {
		RaiseBtn = raiseBtn;
	}

	public JButton getChangeBtn() {
		return ChangeBtn;
	}

	public void setChangeBtn(JButton changeBtn) {
		ChangeBtn = changeBtn;
	}

	public JButton getCheckBtn() {
		return CheckBtn;
	}

	public void setCheckBtn(JButton checkBtn) {
		CheckBtn = checkBtn;
	}

	public JButton getPassBtn() {
		return PassBtn;
	}

	public void setPassBtn(JButton passBtn) {
		PassBtn = passBtn;
	}

	public JTextField getPointsTf() {
		return PointsTf;
	}

	public void setPointsTf(JTextField pointsTf) {
		PointsTf = pointsTf;
	}
	
	public JTextArea getChatTa(){
		return this.ChatTa;
	}
    
	public JScrollPane getChatSp(){
		return this.ChatSp;
	}
	
	
	// metoda obsługi zdarzenia po wciśnięciu przycisku [podbijam]
	private void raiseService(){
		double raise = 0.00;
		try{
			raise = Double.parseDouble(this.PointsTf.getText());
			if(this.Cli.getPlayer(0).getPoints()-raise<0){
				this.setMsg("Nie masz tyle",Color.RED);
				return;
			}
			if(raise<=this.Cli.getLastRaise()){
				this.setMsg("<html>Wartość podbicia<br>musi być większa<br>niż ostatnie<html>",Color.RED);
				return;
			}
		}catch(Exception excp){
			this.setMsg("Niepoprawny format",Color.RED);
			return;
		}
		this.Cli.getPlayer(0).setAction(1);
		this.Cli.getPlayer(0).setRaise(raise);
		this.Cli.getPlayer(0).setState(0);
		this.Cli.getPlayer(0).setPoints(this.Cli.getPlayer(0).getPoints()-raise);
		InfoPack pack = new InfoPack("RAISE");
		pack.setPlayer(this.Cli.getPlayer(0),0);
		pack.setLastAction(1);
		pack.setLastRaise(raise);
		this.Cli.sentPack(pack);
		pack = null;
	}
	
	// metoda obsługi zdarzenia po wciśnięciu przycisku [wchodzę]
	private void imInService(){
		if(this.Cli.getPlayer(0).getPoints()-this.Cli.getLastRaise()<0){
			this.setMsg("Nie masz tyle",Color.RED);
			return;
		}
		this.Cli.getPlayer(0).setAction(5);
		this.Cli.getPlayer(0).setRaise(this.Cli.getLastRaise());
		this.Cli.getPlayer(0).setState(0);
		this.Cli.getPlayer(0).setPoints(this.Cli.getPlayer(0).getPoints()-this.Cli.getLastRaise());
		InfoPack pack = new InfoPack("RAISE");
		pack.setPlayer(this.Cli.getPlayer(0),0);
		pack.setLastAction(5);
		pack.setLastRaise(this.Cli.getLastRaise());
		this.Cli.sentPack(pack);
		pack = null;
	}
	
	
	// metoda obsługi zdarzenia po wciśnięciu przycisku [pass]
	private void passService(){
		this.Cli.getPlayer(0).setAction(3);
		this.Cli.getPlayer(0).setState(0);
		InfoPack pack = new InfoPack("PASS");
		pack.setPlayer(this.Cli.getPlayer(0),0);
		this.Cli.sentPack(pack);
		pack = null;
	}
	
	// metoda obsługi zdarzenia po wcisnięciu [wymiana]
	private void changeCardsService(){
		int[] cardstochange = this.Gp.getCardsToChange();
		boolean isempty = true;
		 for(int i=0; i<cardstochange.length;i++){
			 if(cardstochange[i]==1){
				 isempty = false;
				 break;
			 }
		 }
		 if(isempty){
			 this.setMsg("<html>Nie oznaczono kart <br>do wymiany</html>",Color.RED);
			 return;
		 }else{
				this.Cli.getPlayer(0).setAction(2);
				this.Cli.getPlayer(0).setChanges(this.Cli.getPlayer(0).getChanges()+1);
				InfoPack pack = new InfoPack("CHANGE");
				pack.setCardsToChange(cardstochange);
				pack.setPlayer(this.Cli.getPlayer(0),0);
				this.Cli.sentPack(pack);
				pack = null;
		 }
	}
	
	// metoda wysyła wiadomość o sprawdzeniu po uprzednim sprawdzeniu warunków
	private void checkService(){
		if(this.Cli.getLastAction()==5 || this.Cli.getLastAction()==3 || this.Cli.getLastAction()==1){
			if((this.Cli.getPlayer(0).getPoints()-this.Cli.getLastRaise()-50)<0){
				this.setMsg("Nie masz tyle",Color.RED);
				return;
			}
			this.Cli.getPlayer(0).setAction(4);
			this.Cli.getPlayer(0).setRaise(this.Cli.getLastRaise()+50);
			this.Cli.getPlayer(0).setState(0);
			this.Cli.getPlayer(0).setPoints(this.Cli.getPlayer(0).getPoints()-this.Cli.getLastRaise()-50);
			this.disableGameBtns();
			InfoPack pack = new InfoPack("CHECK");
			pack.setPlayer(this.Cli.getPlayer(0),0);
			pack.setLastAction(4);
			pack.setLastRaise(this.Cli.getLastRaise()+50);
			this.Cli.sentPack(pack);
			pack = null;
			
		}else{
			this.setMsg("Błąd",Color.RED);
			return;	
		}
	}
	
	
	// metoda obsługująca wysłanie wiadomości chat
	private void chatMessageSentService(){
		InfoPack pack = new InfoPack("CHAT");
		pack.setLastChatMessage(this.ChatTf.getText());
		pack.setPlayer(this.Cli.getPlayer(0),0);
	    this.Cli.sentPack(pack);
	    pack = null;
	    this.ChatTf.setText("");
	}

	
	// metoda obsługująca wciśnięcie klawiszy
	@Override
	public void keyPressed(KeyEvent arg0) {
		Object source = arg0.getSource();
		int keyc = arg0.getKeyCode();
		 if(keyc == KeyEvent.VK_ENTER){
		    if(source == this.ChatTf){	 
			 this.chatMessageSentService();
		    }else if(source == this.PointsTf){
		     if(this.PointsTf.isEditable()){
		    	 this.raiseService();
		     }
		    }
		 }
	}

	@Override
	public void keyReleased(KeyEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyTyped(KeyEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	
	protected void finalize(){
		if(this.Cli!=null){
			this.Cli.sentPack(new InfoPack("BYE"));
		}
	}

}
