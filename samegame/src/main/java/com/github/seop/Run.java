package com.github.seop;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

//남은 시간을 구해 갱신하는 스레드
class TimerTh extends Thread {
	private String str;
	private int mm = 2, ss = 00;
	private MainWindow mw;
	private JLabel time;
	
	TimerTh(MainWindow mw, JLabel time) {
		this.mw = mw;
		this.time = time;
	}
	public void run() {
		try {
			while(!Thread.currentThread().isInterrupted()) {
				str = String.format("남은 시간 : %1$02d:%2$02d", mm, ss);
				time.setText(str);
				Thread.sleep(1000);		
				
				if(ss == 0) {
					if(mm == 0) {
						mw.timeOver();
						this.interrupt();
					}
					mm--;
					ss = 60;
				}
				ss--;
			}
		} catch(InterruptedException ee) {
			
		} finally {
			
		}
	}
}

//메인 윈도우, 형태 전담
class MainWindow extends JFrame implements ActionListener, MouseListener, MouseMotionListener {
	private static final long serialVersionUID = 1L;
	private JRootPane rootpane;
	private Container con;
	private JPanel mainpane = new JPanel(new BorderLayout());
	private JPanel p1 = new JPanel(new BorderLayout());
	private JPanel p1_1 = new JPanel(new BorderLayout());
	private JLabel status = new JLabel();
	private JLabel time = new JLabel();
	private JButton start_bt = new JButton("시작");
	private JButton restart_bt = new JButton("다시 시작");
	private JButton exit_bt = new JButton("종료");
	
	private Draw dd;
	private int remainedbox;
	
	private JMenuBar jmb = new JMenuBar();
	private JMenu file = new JMenu("게임");
	private JMenuItem f_start = new JMenuItem("시작");
	private JMenuItem f_restart = new JMenuItem("다시하기");
	private JMenuItem f_exit = new JMenuItem("종료");
	private JMenu help = new JMenu("도움말");
	private JMenuItem h_info = new JMenuItem("정보");
	
	private TimerTh tth;
	
	MainWindow () {
		super("SameGame v0.2 beta");
		this.setSize(807, 648);
		dd = new Draw();
		remainedbox = dd.getCountBox();
		this.init();
		this.start();
		this.setResizable(false);
		this.setLocation(50, 50);
		this.setVisible(true);
	}
	
	public void init() {
		rootpane = this.getRootPane();
		con = rootpane.getContentPane();
		this.setJMenuBar(jmb);
		KeyStroke f_start_ks = KeyStroke.getKeyStroke('S', InputEvent.CTRL_MASK);
		f_start.setAccelerator(f_start_ks);
		file.add(f_start);
		KeyStroke f_restart_ks = KeyStroke.getKeyStroke('R', InputEvent.CTRL_MASK);
		f_restart.setAccelerator(f_restart_ks);
		file.add(f_restart);
		file.addSeparator();
		KeyStroke f_exit_ks = KeyStroke.getKeyStroke('X', InputEvent.CTRL_MASK);
		f_exit.setAccelerator(f_exit_ks);
		file.add(f_exit);
		KeyStroke h_info_ks = KeyStroke.getKeyStroke('I', InputEvent.CTRL_MASK);
		h_info.setAccelerator(h_info_ks);
		help.add(h_info);
		file.setMnemonic('F');
		help.setMnemonic('H');
		jmb.add(file);
		jmb.add(help);
		
		restart_bt.setEnabled(false);
		f_restart.setEnabled(false);
		FlowLayout fl = new FlowLayout(FlowLayout.LEFT);
		JPanel fff = new JPanel(fl);
		
		fff.add(start_bt);
		fff.add(restart_bt);
		fff.add(exit_bt);
		status.setPreferredSize(new Dimension(150,24));
		status.setText("남은 상자 수 : " + String.valueOf(remainedbox));
		status.setHorizontalAlignment(JLabel.CENTER);
		status.setVerticalAlignment(JLabel.CENTER);
		FlowLayout fl2 = new FlowLayout(FlowLayout.CENTER);
		JPanel ts = new JPanel(fl2);
		
		time.setText("남은 시간 : 02:00");
		time.setVerticalAlignment(JLabel.CENTER);
		time.setHorizontalAlignment(JLabel.RIGHT);
		time.setPreferredSize(new Dimension(150,24));
		ts.add(time);
		ts.add(status);
		p1_1.add("East", ts);
		p1_1.add("Center", fff);
		p1.add("North", p1_1);
		
		dd.setBackground(new Color(0xffffff));
		p1.add("Center", dd);
		
		mainpane.add(p1);
		con.add(mainpane);
	}
	
	public void start() {
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.addMouseListener(this);
		dd.addMouseListener(this);
		dd.addMouseMotionListener(this);
		f_start.addActionListener(this);
		f_restart.addActionListener(this);
		f_exit.addActionListener(this);
		h_info.addActionListener(this);
		start_bt.addActionListener(this);
		restart_bt.addActionListener(this);
		exit_bt.addActionListener(this);
	}	
	
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == f_start || e.getSource() == start_bt) {
			dd.setStart();
			tth = new TimerTh(this, time);
			tth.start();
			start_bt.setEnabled(false);
			f_start.setEnabled(false);
			restart_bt.setEnabled(true);
			f_restart.setEnabled(true);
		} else if(e.getSource() == f_restart || e.getSource() == restart_bt) {
			dd.setRestart();
			if(tth.isAlive()) {
				tth.interrupt();
			}
			time.setText("남은 시간 : 02:00");
			tth = new TimerTh(this, time);
			tth.start();
			remainedbox = dd.getCountBox();
			status.setText("남은 상자 수 : " + String.valueOf(remainedbox));
		} else if(e.getSource() == f_exit || e.getSource() == exit_bt) {
			System.exit(0);
		} else if(e.getSource() == h_info) {
			JOptionPane.showMessageDialog(this, "SameGame v0.2 beta\n\n" +
					"= 단축키 =\nCTRL + S : 게임 시작\n" +
					"CTRL + R : 다시 시작\n" +
					"CTRL + X : 종료\n" +
					"CTRL + I : 정보\n\n" +
					"제작 : 2010.01.18 ", 
					"정보", JOptionPane.INFORMATION_MESSAGE);
		}
	}
	
	public void mouseClicked(MouseEvent e) {
		//지울 수 있는 상자가 있는지 여부를 가져와 판별
		if(dd.setBox(e.getX(), e.getY())) {
			remainedbox = dd.getCountBox();
			status.setText("남은 상자 수 : " + String.valueOf(remainedbox));
		} else {
			remainedbox = dd.getCountBox();
			status.setText("남은 상자 수 : " + String.valueOf(remainedbox));
			this.noMore();
		}
	}
	public void mousePressed(MouseEvent e) {
		
	}
	public void mouseReleased(MouseEvent e) {

	}
	public void mouseEntered(MouseEvent e) {
		
	}
	public void mouseExited(MouseEvent e) {
		
	}
	public void mouseDragged(MouseEvent e) {
		
	}
	public void mouseMoved(MouseEvent e) {
		//마우스 포인터가 위치해 있는 상자에  주위에 같은 색의 상자가  있는지 검색
		dd.searchBox(e.getX(), e.getY());
	}
	public void timeOver() {
		dd.setTimeOver();
		JOptionPane.showMessageDialog(this, "시간이 초과되었습니다.", "정보", JOptionPane.INFORMATION_MESSAGE);
	}
	private void noMore() {
		tth.interrupt();
		dd.setTimeOver();
		JOptionPane.showMessageDialog(this, "지울 수 있는 상자가 더 이상 없습니다.", "정보", JOptionPane.INFORMATION_MESSAGE);
	}
	
}

public class Run {
	public static void main(String[] ar) {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch(Exception ee) {}
		new MainWindow();
	}
}