import java.awt.BorderLayout;
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
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JTextArea;

/**
 * This class is used to create the GUI for the BigTwo Card game.
 * 
 * @author davidliu
 *
 */
public class BigTwoTable implements CardGameTable
{
	private final static int MAX_CARD_NUM = 13; // maximum num of cards
	private final static int HORIZONTAL_DIST_OF_CARDS = 10; //x in the playerboard
	private final static int EACH_CARD_EDGE = 10;

	//The GUI Table is created. 
	public BigTwoTable(CardGame cardGame)
	{
		this.game = cardGame;
		
		/*Main GUI frame*/
		frame = new JFrame("Big Two");
		
		/*menu bar*/
		menuBar = new JMenuBar();
		gamMenu = new JMenu("Game");
		menuBar.add(gamMenu);
		restarItem = new JMenuItem("Restart");
		quitItem = new JMenuItem("Quit");
		gamMenu.add(restarItem);
		gamMenu.add(quitItem);

		/*left panel*/
		playingPanel = new JPanel(new BoxLayout(playingPanel, BoxLayout.Y_AXIS));
		playBoards = new PlayerBoard [game.getNumOfPlayers()];
		clickable = false;
		for(int i=0; i<game.getNumOfPlayers(); i++)
		{
			playBoards[i] = new PlayerBoard(i); //to be drawn
			playingPanel.add(playingPanel.add(playBoards[i]));
		}
		handsBoard = new JPanel(); //to be drawn
		playingPanel.add(handsBoard);
		buttonBoard = new JPanel(); //Flow layout
		playButton = new JButton("Play");
		passButton = new JButton("Pass");
		buttonBoard.add(playButton);
		buttonBoard.add(passButton);
		playingPanel.add(buttonBoard);
		//add all of the above components to the frame;
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.add(menuBar, BorderLayout.NORTH);
		frame.add(msgArea, BorderLayout.EAST);
		frame.add(playingPanel,BorderLayout.CENTER);

		frame.setVisible(true);
	}

	private CardGame game; // the bigtwo game
	private boolean[] selected; // which cards are selected
	private int activePlayer; //idx of currently active player

	private JFrame frame; // main window

	private JMenuBar menuBar; //menu bar
	private JMenu gamMenu; //game menu
	private JMenuItem restarItem; // restart under the game menu
	private JMenuItem quitItem; // quit the game

	private JTextArea msgArea; //space for output the game info

	private JPanel playingPanel; //panel for cards, hands and buttons
	private boolean clickable;
	private PlayerBoard [] playBoards; //all the four players
	private JPanel handsBoard; //showing the top of hands on table
	private JPanel buttonBoard; //showing the Play and the Pass button

	private JButton playButton;
	private JButton passButton; //buttons in the button board


	@Override
	public void setActivePlayer(int activePlayer)
	{
		if (activePlayer < 0 || activePlayer >= game.getNumOfPlayers())
		{
			this.activePlayer = -1;
		}
		else
		{
			this.activePlayer = activePlayer;
		}

	}

	@Override
	public int[] getSelected()
	{
		ArrayList<Integer> selectedCardList = new ArrayList<Integer>();
		for(int i=0;i<this.selected.length;i++)
		{
			if(selected[i])
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
	public void resetSelected()
	{
		for(int i=0; i<selected.length; i++)
		{
			selected[i] = false;
		}
	}

	@Override
	public void repaint()
	{
		// implementation of GUI.go()
		this.buttonBoard.repaint();
		this.handsBoard.repaint();
		for(int i=0;i<playBoards.length;i++)
		{
			playBoards[i].repaint();
		}
		this.playingPanel.repaint();
	}

	@Override
	public void printMsg(String msg)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void clearMsgArea()
	{
		msgArea.setText(null);
	}

	@Override
	public void reset()
	{
		//first remove all the hands on table
		game.getHandsOnTable().clear();
		//then remove all the cards from the players
		for(int i=0; i<game.getNumOfPlayers();i++)
			game.getPlayerList().get(i).removeAllCards();
		//then reset the cards
		game.getDeck().initialize();
		game.getDeck().shuffle();
		for(int i=0;i<13;i++)
		{
			for(int j=0;j<game.getPlayerList().size();j++)
			{
				Card card = game.getDeck().removeCard(0);
				game.getPlayerList().get(j).addCard(card);
				if(card.getRank()==2 && card.getSuit()==0)
					activePlayer = j; //the one with Diamond 3 is the first player
			}
		}
		for(int i=0;i<game.getPlayerList().size();i++)
		{
			game.getPlayerList().get(i).sortCardsInHand();
		}
	}

	@Override
	public void enable()
	{
		this.clickable = true;
		passButton.addActionListener(new PassButtonListener());
		playButton.addActionListener(new PlayButtonListener());
	}

	@Override
	public void disable()
	{
		this.clickable = false;
		passButton.removeActionListener(new PassButtonListener());
		playButton.removeActionListener(new PlayButtonListener());
	}
	/**
	 * The inner class for the behaviour of the cardPanel
	 */
	class PlayerBoard extends JPanel implements MouseListener
	{
		private static final long serialVersionUID = -1414283557475818226L;
		private int playerIdx;
		private boolean [] beClicked;
		PlayerBoard(int playerIdx)
		{
			this.playerIdx = playerIdx;
			this.beClicked = new boolean [game.getPlayerList().get(playerIdx).getNumOfCards()];
			for(int i=0;i<beClicked.length;i++)
			{
				beClicked[i]=false;
			}
		}

		@Override
		public void mouseClicked(MouseEvent e)
		{
			//TODO
		}

		@Override
		protected void paintComponent(Graphics g)
		{
			if(this.playerIdx==activePlayer)
			{
				Image cardImage = null;
				for(int i=0; i<game.getPlayerList().get(this.playerIdx).getNumOfCards(); i++)
				{
					Card card = game.getPlayerList().get(this.playerIdx).getCardsInHand().getCard(i);
					int rank = card.getRank();
					int suit = card.getSuit();
					String pathToIcon = "../img/pukeImage/"+suit+"_"+rank+".png";
					cardImage = new ImageIcon(pathToIcon).getImage();
					g.drawImage(cardImage, selected[i]?0:HORIZONTAL_DIST_OF_CARDS, 40+i*EACH_CARD_EDGE, this);
				}
			}
			else
			{
				Image cardBackImage = new ImageIcon("../img/pukeImage/back.png").getImage();
				for(int i=0; i<game.getPlayerList().get(this.playerIdx).getNumOfCards(); i++)
				{
					g.drawImage(cardBackImage,HORIZONTAL_DIST_OF_CARDS,40+i*EACH_CARD_EDGE,this);
				}
			}
			Image avactorImage = new ImageIcon("../img/Avator/"+playerIdx+".png").getImage();
			g.drawImage(avactorImage, 0, 0, this);
		}

		@Override
		public void mousePressed(MouseEvent e){}

		@Override
		public void mouseReleased(MouseEvent e){}

		@Override
		public void mouseEntered(MouseEvent e){}

		@Override
		public void mouseExited(MouseEvent e){}
	}
	/**
	 * Realize the behaviour of the play button when it is pressed
	 */
	class PlayButtonListener implements ActionListener
	{
		@Override
		public void actionPerformed(ActionEvent e)
		{
			for(int i=0;i<game.getPlayerList().get(activePlayer).getNumOfCards();i++)
			{
				selected[i]=playBoards[activePlayer].beClicked[i];
			}
			activePlayer++;
			activePlayer %= game.getNumOfPlayers();
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
			resetSelected();
			activePlayer++;
			activePlayer %= game.getNumOfPlayers();
			frame.repaint();
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
			reset();
			JFrame popUpMsg = new JFrame("Restarting...");
			JLabel restarting = new JLabel("The game is restarted");
			popUpMsg.add(restarting);
			popUpMsg.setVisible(true);
			for(int t=0;t<9;t++)
			{
				try
				{
					Thread.sleep(500);
				} catch (Exception E) {}
				if(t%3==0)			restarting.setText("The game is restarted.");
				else if(t%3==1)		restarting.setText("The game is restarted..");
				else				restarting.setText("The game is restarted...");
			}
			popUpMsg.setVisible(false);
			frame.repaint();
			enable();
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
}
