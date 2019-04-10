import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JTextArea;



public class CardGameLauncher implements CardGameTable
{
	static final public int WIDTH_OF_CARD = 105;
	static final public int HEIGHT_OF_CARD = 150;
	static final public int DIST_BET_CARD = 30;
	static final public int DIST_AVAT_CARD = 120;
	static final public int DIST_UNSELECTED_TOP = 20;
	static final public int DIST_SELECTED_TOP = 0;

	/*Decoration*/
	static private Font menuFont;
	static private Font buttonFont;
	static private Font msgFont;
	static private int totalPlayerNum;
	{
		menuFont = new Font(null, Font.BOLD, 17);
		buttonFont = new Font(null, Font.BOLD, 22);
		msgFont = new Font(null, Font.ITALIC, 17);
		totalPlayerNum = 0;
	}
	
	/**
	 * The inner class for each player's panel
	 */
	class CardBoard extends JPanel implements MouseListener
	{
		int playerNum;
		private boolean selected[];
		private int number = 0;

		/**
		 * The constructor of the cardboard
		 * 
		 * @param number how many cards are the player initialized to be
		 */
		CardBoard(int number)
		{
			selected = new boolean [number];
			this.number = number;
			playerNum = totalPlayerNum;
			totalPlayerNum++;
		}
		
		@Override
		public void paintComponent(Graphics g)
		{
			Image icon = new ImageIcon("img/Avator/"+playerNum+".png").getImage();
			g.drawImage(icon, 0, DIST_UNSELECTED_TOP, 100, 100+DIST_UNSELECTED_TOP, 0, 0, 1280, 1280, this);
			for(int i=0;i<this.number;i++)
			{
				if(activePlayer == playerNum)
				{
					//TODO
					Image cardTemp = new ImageIcon("img/pukeImage/"+playerNum+"_"+i+".png").getImage();
					g.drawImage(cardTemp, DIST_AVAT_CARD+i*DIST_BET_CARD, selected[i]?DIST_SELECTED_TOP:DIST_UNSELECTED_TOP, WIDTH_OF_CARD, HEIGHT_OF_CARD, this);	
				}
				else
				{
					Image cardTemp = new ImageIcon("img/pukeImage/back.png").getImage();
					g.drawImage(cardTemp, DIST_AVAT_CARD+i*DIST_BET_CARD, DIST_UNSELECTED_TOP, WIDTH_OF_CARD, HEIGHT_OF_CARD, this);
				}
			}
		}

		@Override
		public void mouseClicked(MouseEvent e) 
		{
			if(activePlayer != playerNum)	return;
			int mouseX = e.getX()-DIST_AVAT_CARD;
			int mouseY = e.getY()-DIST_SELECTED_TOP;
			if(mouseX <0 || mouseY<0 || mouseX>DIST_BET_CARD*(number-1)+WIDTH_OF_CARD || mouseY>DIST_UNSELECTED_TOP+HEIGHT_OF_CARD)
				return;
			//horizontally, number+2 areas
			//vertically, 3 areas;
			int areaX = (int)mouseX/DIST_BET_CARD; //i.e. one third of width
			int areaY = mouseY<DIST_UNSELECTED_TOP?0:(mouseY<HEIGHT_OF_CARD?1:2);
			switch (areaY) {
				case 0:
					for(int i = areaX;i>areaX-5 && i>=0;i--)
					{
						if(selected[i>= number?number-1:i])
						{
							selected[i>= number?number-1:i] = false;
							break;
						}
					}
					break;
				case 1:
					int cardIdx = areaX >= number?number-1:areaX;
					selected[cardIdx] = selected[cardIdx]?false:true;
					break;
				case 2:
					for(int i = areaX;i>areaX-5 && i>=0;i--)
					{
						if(!selected[i>= number?number-1:i])
						{
							selected[i>= number?number-1:i] = true;
							break;
						}
					}
					break;
				default:
					break;
			}
			frame.repaint();
		}

		@Override
		public void mousePressed(MouseEvent e) {}

		@Override
		public void mouseReleased(MouseEvent e) {}

		@Override
		public void mouseEntered(MouseEvent e) {}

		@Override
		public void mouseExited(MouseEvent e) {}
		
	}

	/**
	 * Realize the behaviour of the play button when it is pressed
	 */
	class PlayButtonListener implements ActionListener
	{
		@Override
		public void actionPerformed(ActionEvent e)
		{
			//game.makeMove(activePlayer, getSelected());
			frame.repaint();	
		}
	}

	/**
	 * Realize the behaviour of the pass button when it is pressed
	 */
	class PassButtonListener implements ActionListener
	{
		@Override
		public void actionPerformed(ActionEvent e)
		{
			//game.makeMove(activePlayer, null);
			frame.repaint();
		}
	}
	
	/**
	 * Quit the game. The quit menu item is located in the Game menu which is located in the
	 * menu bar at the top of the frame. 
	 */
	class QuitMenuItemListener implements ActionListener
	{
		@Override
		public void actionPerformed(ActionEvent e)
		{
			disable();
			frame.setVisible(false);
			System.exit(0);
		}
	}

	/**
	 * Restart the game. The restart menu item is inside the Game menu which is located in the
	 * menu bar at the top of the frame. 
	 */
	class RestartMenuItemListener implements ActionListener
	{
		@Override
		public void actionPerformed(ActionEvent e)
		{
			disable();
			//clean all the players' cards and the hands on table
			//re-distribute the cards
			frame.repaint();
			setActivePlayer(2);
			enable();
			frame.repaint();
		}
	}
	
	private JFrame frame;
	private JMenuBar gameMenuBar;
	private JMenu gameMenu;
	private JMenuItem restartMenuItem;
	private JMenuItem quitMenuItem;
	private JPanel cardBoard;
	private CardBoard cardBoardOne;
	private CardBoard cardBoardTwo;
	private CardBoard cardBoardThree;
	private CardBoard cardBoardFour;
	
	private JPanel buttonPanel;
	private JButton playButton;
	private JButton passButton;
	
	private JTextArea textArea;

	private int activePlayer;
	
	/**
	 * constructor for the this table GUI
	 */
	public CardGameLauncher(int activePlayer)
	{
		frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		cardBoard = new JPanel();
		gameMenuBar = new JMenuBar();
		gameMenu = new JMenu("Game");
		gameMenu.setFont(menuFont);
		restartMenuItem = new JMenuItem("Restart");
		quitMenuItem = new JMenuItem("Quit");

		gameMenu.add(restartMenuItem);
		gameMenu.add(quitMenuItem);
		gameMenuBar.add(gameMenu);

		cardBoard.setBackground(Color.DARK_GRAY);
		
		cardBoardOne = new CardBoard(13);
		cardBoard.add(cardBoardOne);

		cardBoardTwo = new CardBoard(13);
		cardBoard.add(cardBoardTwo);

		cardBoardThree = new CardBoard(13);
		cardBoard.add(cardBoardThree);

		cardBoardFour = new CardBoard(13);
		cardBoard.add(cardBoardFour);


		cardBoard.setLayout(new BoxLayout(this.cardBoard, BoxLayout.Y_AXIS));
		
		buttonPanel = new JPanel();
		playButton = new JButton("Play");
		playButton.setFont(buttonFont);
		passButton = new JButton("Pass");
		passButton.setFont(buttonFont);
		buttonPanel.add(playButton);
		buttonPanel.add(passButton);
		
		textArea = new JTextArea("New Game: BigTwo\t\t\t\t\n");
		textArea.setFont(msgFont);
		
		frame.add(cardBoard);
		frame.add(gameMenuBar, BorderLayout.NORTH);
		frame.add(textArea,BorderLayout.EAST);
		frame.add(buttonPanel,BorderLayout.SOUTH);
		frame.setSize(1200, 900);
		frame.setLocation(300,100);
		frame.setVisible(true);

		this.activePlayer = activePlayer;
	}

	/**
	 * set the activeplayer
	 * 
	 * @param activePlayer index of the activeplayer
	 */
	public void setActivePlayer(int activePlayer)
	{
		this.activePlayer = activePlayer;
	}
	
	/**
	 * all the interations enabled
	 */
	public void enable()
	{
		cardBoardOne.addMouseListener(cardBoardOne);
		cardBoardTwo.addMouseListener(cardBoardTwo);
		cardBoardThree.addMouseListener(cardBoardThree);
		cardBoardFour.addMouseListener(cardBoardFour);
		restartMenuItem.addActionListener(new RestartMenuItemListener());
		quitMenuItem.addActionListener(new QuitMenuItemListener());
		playButton.addActionListener(new PlayButtonListener());
		passButton.addActionListener(new PassButtonListener());
	}

	/**
	 * all the interaction disabled
	 */
	public void disable()
	{
		activePlayer = -1;
		cardBoardOne.removeMouseListener(cardBoardOne);
		cardBoardTwo.removeMouseListener(cardBoardTwo);
		cardBoardThree.removeMouseListener(cardBoardThree);
		cardBoardFour.removeMouseListener(cardBoardFour);
		restartMenuItem.removeActionListener(restartMenuItem.getActionListeners()[0]);
		quitMenuItem.removeActionListener(quitMenuItem.getActionListeners()[0]);
	}

	public void resetSelected()
	{
		cardBoardOne.selected = new boolean [cardBoardOne.number];
		cardBoardTwo.selected = new boolean [cardBoardTwo.number];
		cardBoardThree.selected = new boolean [cardBoardThree.number];
		cardBoardFour.selected = new boolean [cardBoardFour.number];
	}
	

	@Override
	public int[] getSelected()
	{
		boolean [] activePlayerSelected = null;
		switch(activePlayer)
		{
			case 0: 
				activePlayerSelected = cardBoardOne.selected;
				break;
			case 1: 
				activePlayerSelected = cardBoardTwo.selected;
				break;
			case 2: 
				activePlayerSelected = cardBoardThree.selected;
				break;
			case 3:
				activePlayerSelected = cardBoardFour.selected;
				break;
			default:
				return null;
		}
		ArrayList<Integer> selectedCardList = new ArrayList<Integer>();
		for(int i=0;i<activePlayerSelected.length;i++)
		{
			if(activePlayerSelected[i])
				selectedCardList.add(i);
		}
		int [] selectedCards = new int [selectedCardList.size()];
		for(int cardIdx:selectedCardList)
		{
			selectedCards[selectedCardList.indexOf(cardIdx)] = cardIdx;
		}
		return selectedCards;
	}

	@Override
	public void repaint()
	{
		frame.repaint();
	}

	@Override
	public void printMsg(String msg)
	{
		this.textArea.append(msg+"\n");
	}

	@Override
	public void clearMsgArea()
	{
		this.textArea.setText(null);
	}

	@Override
	public void reset()
	{
		//TODO
	}
	
	/**
	 * this is a inner tester for the table GUI
	 */
	static public void main(String [] args)
	{
		CardGameLauncher cardGameGUI = new CardGameLauncher(1);
		cardGameGUI.enable();
	}
}
