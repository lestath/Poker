package View;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;

import CommunicationModel.Server;

public class MainApp extends JFrame implements ActionListener{
	
	/**
	 * 
	 */
	
	private static final long serialVersionUID = 1L;
	private JButton NewTableBtn;
	private JButton JoinToGameBtn;
	private JTextField NickTf;
	private JTextField HostTf;
	private JTextField PortTf;
	private JTextField PointsTf;
	private JLabel MsgLab; // etykieta wiadomości zwrotnych
	private JLabel NickLab;
	private JLabel PortLab;
	private JLabel HostLab;
	private JLabel PointsLab;
	
	public MainApp(){
		super("Poker");
		this.setSize(new Dimension(210,250));
		this.setLayout(new FlowLayout(FlowLayout.LEADING));
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setResizable(false);
		this.setLocationRelativeTo(null);
		
		// inicjalizacja ustawienie atrybutów panelu wejściowego;

		Dimension btndim = new Dimension(200,24);
		
		
		// ustawienie elementów panelu wejściowego
			this.MsgLab = new JLabel("");
			this.MsgLab.setPreferredSize(new Dimension(190,24));
			

					
			this.NickLab = new JLabel("Nick :");
			this.NickLab.setPreferredSize(new Dimension(40,24));
			
			this.PointsLab = new JLabel("$ :");
			this.PointsLab.setPreferredSize(new Dimension(40,24));
			
			this.HostLab = new JLabel("Host :");
			this.HostLab.setPreferredSize(new Dimension(40,24));
			
			this.PortLab = new JLabel("Port :");
			this.PortLab.setPreferredSize(new Dimension(40,24));
			
			this.NickTf = new JTextField();
			this.NickTf.setPreferredSize(new Dimension(145,24));
			
			this.PointsTf = new JTextField("5000.00");
			this.PointsTf.setPreferredSize(new Dimension(145,24));
			
			this.HostTf = new JTextField("localhost");
			this.HostTf.setPreferredSize(new Dimension(145,24));
			
			this.PortTf = new JTextField("4444");
			this.PortTf.setPreferredSize(new Dimension(145,24));
			
			this.NewTableBtn = new JButton("Nowa Gra");
			this.NewTableBtn.setPreferredSize(btndim);
			this.NewTableBtn.setFocusPainted(false);
			this.NewTableBtn.addActionListener(this);
			
			
			this.JoinToGameBtn = new JButton("Dołącz do gry");
			this.JoinToGameBtn.setPreferredSize(btndim);
			this.JoinToGameBtn.setFocusPainted(false);
			this.JoinToGameBtn.addActionListener(this);
		
			this.add(this.MsgLab);
			this.add(this.NickLab);
			this.add(this.NickTf);
			this.add(this.PointsLab);
			this.add(this.PointsTf);
			this.add(this.HostLab);
			this.add(this.HostTf);
			this.add(this.PortLab);
			this.add(this.PortTf);
			this.add(this.NewTableBtn);
			this.add(this.JoinToGameBtn);
		
		
		// dodanie elementów dla panelu wejściowego;

		

		this.setVisible(true);
	}


	@Override
	public void actionPerformed(ActionEvent arg0) {
		Object src = arg0.getSource();
		if(this.NickTf.getText().isEmpty()){
			this.sentMsg("Podaj Nick",Color.red);
		}else if(this.HostTf.getText().isEmpty()){
			this.sentMsg("Podaj Host",Color.red);
		}else if(this.PortTf.getText().isEmpty()){
			this.sentMsg("Podaj Port",Color.red);
		}else if(this.PointsTf.getText().isEmpty()){
			this.sentMsg("Podaj Punkty",Color.red);
		}else{
			if(src == this.NewTableBtn){
				Server srv = null;
				boolean own = true;
				try{
						int port = Integer.parseInt(this.PortTf.getText());
						double startpoints = Double.parseDouble(this.PointsTf.getText());
						if(startpoints<300){
							this.sentMsg("Minimum 300$",Color.RED);
							return;
						}
						if(this.HostTf.getText().equals("localhost")){
							srv = new Server(port);
							new Thread(srv).start();
							Thread.sleep(500);
							if(!srv.isExcFlag()){
								new Thread(new GameFrame(this.NickTf.getText(),startpoints,this.HostTf.getText(),port,own,srv,this)).start();
							}else{
								this.sentMsg(srv.getExc().getMessage(),Color.red);
							}
						}
				 }catch(Exception pe){
					 pe.printStackTrace();
					 this.sentMsg("Błąd Portu lub punktów",Color.red); 
				 }
			}else if(src == this.JoinToGameBtn){
				 try{
					int port = Integer.parseInt(this.PortTf.getText());
					double startpoints = Double.parseDouble(this.PointsTf.getText());
					new Thread(new GameFrame(this.NickTf.getText(),startpoints,this.HostTf.getText(),port,false,null,this)).start();
				 }catch(Exception pe){
					 this.sentMsg("Błąd Portu lub punktów",Color.red); 
				 }
			}
		}
	}
	
// funkcja przesyła wiadomość do okna aplikacji
  public void sentMsg(String msg,Color col){
	  	this.MsgLab.setForeground(col);
		this.MsgLab.setText(msg);
  }
}
