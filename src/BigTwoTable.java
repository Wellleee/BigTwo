import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
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
import javax.swing.JTextField;
import javax.swing.JTextPane;

public class BigTwoTable implements CardGameTable {
	/**
	 * the width of card image
	 */
	static final public int WIDTH_OF_CARD = 105;
	/**
	 * the height of the card
	 */
	static final public int HEIGHT_OF_CARD = 150;
	/**
	 * the horizontal distance between each two cards
	 */
	static final public int DIST_BET_CARD = 21;
	/**
	 * the distance between leftmost card to the left edge of the avator image
	 */
	static final public int DIST_AVAT_CARD = 120;
	/**
	 * the horizontal distance of an unselected card
	 */
	static final public int DIST_UNSELECTED_TOP = 20;
	/**
	 * the horizontal distance of a selected card
	 */
	static final public int DIST_SELECTED_TOP = 0;
	/**
	 * the height of each player panel
	 */
	static final public int PLAYER_PANEL_DIST = 200;
	/**
	 * the maximum number of cards held by each player
	 */
	static final public int MAX_CARD_IN_HAND = 13;

	/* Decoration */
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
	class CardBoard extends JPanel implements MouseListener {
		private static final long serialVersionUID = 1L;
		/**
		 * the index of the player of this cardborad
		 */
		int playerNum;
		private boolean selected[];
		/**
		 * the number of cards being selected
		 */
		private int number = 0;

		/**
		 * The constructor of the cardboard
		 * 
		 * @param number how many cards are the player initialized to be
		 */
		CardBoard(int number) {
			selected = new boolean[number];
			this.number = number;
			playerNum = totalPlayerNum;
			totalPlayerNum++;
		}

		@Override
		public void paintComponent(Graphics g)
		{
			if(game.getPlayerList().size()<=this.playerNum)					return; //not enough guys yet
			g.drawString(game.getPlayerList().get(playerNum).getName(), 0, 0);
			Image icon = new ImageIcon("img/Avator/" + playerNum + ".png").getImage();
			g.drawImage(icon, 0, DIST_UNSELECTED_TOP, 100, 100 + DIST_UNSELECTED_TOP, 0, 0, 1280, 1280, this);
			for (int i = 0; i < game.getPlayerList().get(playerNum).getNumOfCards(); i++) {
				if (((BigTwoClient) game).getPlayerID() == playerNum || disclose) {
					int rank = game.getPlayerList().get(playerNum).getCardsInHand().getCard(i).getRank();
					int suit = game.getPlayerList().get(playerNum).getCardsInHand().getCard(i).getSuit();
					Image cardTemp = new ImageIcon("img/pukeImage/" + suit + "_" + rank + ".png").getImage();
					g.drawImage(cardTemp, DIST_AVAT_CARD + i * DIST_BET_CARD,
							selected[i] ? DIST_SELECTED_TOP : DIST_UNSELECTED_TOP, WIDTH_OF_CARD, HEIGHT_OF_CARD, this);
				} else {
					Image cardTemp = new ImageIcon("img/pukeImage/back.png").getImage();
					g.drawImage(cardTemp, DIST_AVAT_CARD + i * DIST_BET_CARD, DIST_UNSELECTED_TOP, WIDTH_OF_CARD,
							HEIGHT_OF_CARD, this);
				}
			}
		}

		@Override
		public void mouseClicked(MouseEvent e) {
			if (((BigTwoClient) game).getPlayerID() != playerNum)
				return;
			int mouseX = e.getX() - DIST_AVAT_CARD;
			int mouseY = e.getY() - DIST_SELECTED_TOP;
			int number = game.getPlayerList().get(playerNum).getNumOfCards();
			if (mouseX < 0 || mouseY < 0 || mouseX > DIST_BET_CARD * (number - 1) + WIDTH_OF_CARD
					|| mouseY > DIST_UNSELECTED_TOP + HEIGHT_OF_CARD)
				return;
			// horizontally, number+2 areas
			// vertically, 3 areas;
			int areaX = (int) mouseX / DIST_BET_CARD; // i.e. one third of width
			int areaY = mouseY < DIST_UNSELECTED_TOP ? 0 : (mouseY < HEIGHT_OF_CARD ? 1 : 2);
			switch (areaY) {
			case 0:
				for (int i = areaX; i > areaX - 5 && i >= 0; i--) {
					if (selected[i >= number ? number - 1 : i]) {
						selected[i >= number ? number - 1 : i] = false;
						break;
					}
				}
				break;
			case 1:
				int cardIdx = areaX >= number ? number - 1 : areaX;
				selected[cardIdx] = selected[cardIdx] ? false : true;
				break;
			case 2:
				for (int i = areaX; i > areaX - 5 && i >= 0; i--) {
					if (!selected[i >= number ? number - 1 : i]) {
						selected[i >= number ? number - 1 : i] = true;
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
		public void mousePressed(MouseEvent e) {
		}

		@Override
		public void mouseReleased(MouseEvent e) {
		}

		@Override
		public void mouseEntered(MouseEvent e) {
		}

		@Override
		public void mouseExited(MouseEvent e) {
		}

	}

	/**
	 * For Drawing the board showing the top hand on table
	 */
	class HandsBoard extends JPanel {
		private static final long serialVersionUID = -8080570155611239398L;

		@Override
		public void paintComponent(Graphics g) {
			Image cardInHand = null;
			int numOfHand = game.getHandsOnTable().size();
			if (numOfHand != 0) {
				for (int i = 0; i < game.getHandsOnTable().get(numOfHand - 1).size(); i++) {
					int rank = game.getHandsOnTable().get(numOfHand - 1).getCard(i).getRank();
					int suit = game.getHandsOnTable().get(numOfHand - 1).getCard(i).getSuit();
					cardInHand = new ImageIcon("img/pukeImage/" + suit + "_" + rank + ".png").getImage();
					g.drawImage(cardInHand, (i + 3) * DIST_BET_CARD, DIST_UNSELECTED_TOP, this);
				}
			}
		}
	}

	/**
	 * Realize the behavior of the play button when it is pressed
	 */
	class PlayButtonListener implements ActionListener
	{
		@Override
		public void actionPerformed(ActionEvent e)
		{
			int[] selectedCards = getSelected();
			if (selectedCards == null || selectedCards.length == 0)
			{
				printMsg("You have to choose at least a card, or pass");
			}
			else
			{
				game.makeMove(((BigTwoClient) game).getPlayerID(), selectedCards);
				resetSelected();
				frame.repaint();
			}
		}
	}

	/**
	 * Realize the behavior of the pass button when it is pressed
	 */
	class PassButtonListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			game.makeMove(((BigTwoClient) game).getPlayerID(), null);
			resetSelected();
			frame.repaint();
		}
	}

	/**
	 * Quit the game. The quit menu item is located in the Game menu which is
	 * located in the menu bar at the top of the frame.
	 */
	class QuitMenuItemListener implements ActionListener
	{
		@Override
		public void actionPerformed(ActionEvent e)
		{
			frame.setVisible(false);
			System.exit(0);
		}
	}

	/**
	 * Restart the game. The restart menu item is inside the Game menu which is
	 * located in the menu bar at the top of the frame.
	 */
	class ConnectionMenuItemListener implements ActionListener
	{
		@Override
		public void actionPerformed(ActionEvent e)
		{
			disable();
			clearMsgArea();

			JFrame confirmation = new JFrame("Re-Connecting");
			JPanel showingPanel = new JPanel();
			JTextPane text = new JTextPane();
			text.setText("Restarting, please wait...");
			showingPanel.add(text);
			confirmation.add(showingPanel);
			confirmation.setSize(600, 100);
			confirmation.setLocation(700, 500);
			confirmation.setVisible(true);

			((BigTwoClient) game).severConnection();
			((BigTwoClient) game).makeConnection();
			disclose = false;
			confirmation.setVisible(false);
		}
	}

	class MessageSendListener implements KeyListener
	{

		@Override
		public void keyTyped(KeyEvent e)
		{
			//TODO
		}

		@Override
		public void keyPressed(KeyEvent e) {}

		@Override
		public void keyReleased(KeyEvent e){}
		
	}

	private CardGame game;
	
	private JFrame frame;
	private JMenuBar gameMenuBar;
	private JMenu gameMenu;
	private JMenuItem connectionMenuItem;
	private JMenuItem quitMenuItem;
	private JPanel cardBoard;
	private CardBoard cardBoardOne;
	private CardBoard cardBoardTwo;
	private CardBoard cardBoardThree;
	private CardBoard cardBoardFour;
	
	private HandsBoard handsBoard;

	private JPanel buttonPanel;
	private JButton playButton;
	private JButton passButton;
	private JLabel inputLabel;
	private JTextField inputField;
	
	private JTextArea textArea;
	private JTextArea chatArea;
	private JPanel textPanel;

	private boolean disclose;
	
	private int numOfPrints = 0;
	
	/**
	 * constructor for the this table GUI
	 */
	public BigTwoTable(CardGame game)
	{
		this.game = game;

		frame = new JFrame("BigTwo");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		cardBoard = new JPanel();
		gameMenuBar = new JMenuBar();
		gameMenu = new JMenu("Game");
		gameMenu.setFont(menuFont);
		connectionMenuItem = new JMenuItem("Connecting");
		quitMenuItem = new JMenuItem("Quit");

		gameMenu.add(connectionMenuItem);
		gameMenu.add(quitMenuItem);
		gameMenuBar.add(gameMenu);

		cardBoard.setBackground(Color.DARK_GRAY);
		
		cardBoardOne = new CardBoard(MAX_CARD_IN_HAND);
		cardBoard.add(cardBoardOne);

		cardBoardTwo = new CardBoard(MAX_CARD_IN_HAND);
		cardBoard.add(cardBoardTwo);

		cardBoardThree = new CardBoard(MAX_CARD_IN_HAND);
		cardBoard.add(cardBoardThree);

		cardBoardFour = new CardBoard(MAX_CARD_IN_HAND);
		cardBoard.add(cardBoardFour);

		handsBoard = new HandsBoard();
		cardBoard.add(handsBoard);

		cardBoard.setLayout(new BoxLayout(this.cardBoard, BoxLayout.Y_AXIS));
		
		buttonPanel = new JPanel();
		playButton = new JButton("Play");
		playButton.setFont(buttonFont);
		passButton = new JButton("Pass");
		passButton.setFont(buttonFont);
		inputLabel = new JLabel("Message");
		inputLabel.setFont(menuFont);
		inputField = new JTextField();
		inputField.setMinimumSize(new Dimension(20, 300));

		buttonPanel.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();

		c.gridx = 0;
		c.gridy = 0;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.weighty = 0.1;
		c.insets = new Insets(2, 2, 70, 70);
		c.anchor = GridBagConstraints.CENTER;
		buttonPanel.add(playButton, c);

		c.gridx = 1;
		c.insets.right = 250;
		buttonPanel.add(passButton, c);

		c.gridx = 2;
		c.insets.right = 0;
		buttonPanel.add(inputLabel, c);

		c.gridx = 3;
		c.insets.left = 0;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.ipadx = 500;
		buttonPanel.add(inputField, c);
		
		textArea = new JTextArea("New Game: BigTwo\t\t\t\t\n");
		textArea.setFont(msgFont);
		chatArea = new JTextArea("Chat Box: BigTwo\t\t\t\t\n");
		chatArea.setFont(msgFont);
		textPanel = new JPanel();
		textPanel.add(textArea);
		textPanel.add(chatArea);

		textPanel.setLayout(new BoxLayout(this.textPanel,BoxLayout.Y_AXIS));

		frame.add(cardBoard);
		frame.add(gameMenuBar, BorderLayout.NORTH);
		frame.add(textPanel,BorderLayout.EAST);
		frame.add(buttonPanel,BorderLayout.SOUTH);
		frame.setSize(1500, 1000);
		frame.setLocation(200,100);
		frame.setVisible(true);
	}

	
	/**
	 * all the interations enabled
	 */
	public void go()
	{
		cardBoardOne.addMouseListener(cardBoardOne);
		cardBoardTwo.addMouseListener(cardBoardTwo);
		cardBoardThree.addMouseListener(cardBoardThree);
		cardBoardFour.addMouseListener(cardBoardFour);
		connectionMenuItem.addActionListener(new ConnectionMenuItemListener());
		quitMenuItem.addActionListener(new QuitMenuItemListener());
		playButton.addActionListener(new PlayButtonListener());
		passButton.addActionListener(new PassButtonListener());
		frame.addKeyListener(new MessageSendListener());
	}

	@Override
	public void enable()
	{
		playButton.setEnabled(true);
		passButton.setEnabled(true);
	}

	@Override
	public void disable()
	{
		playButton.setEnabled(false);
		passButton.setEnabled(false);
	}

	/**
	 * set all the cards to be unselected
	 */
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
		switch(((BigTwoClient)game).getPlayerID())
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
		if(numOfPrints >= 20) 
		{
			clearMsgArea();
			this.textArea.append("New Game: BigTwo\t\t\t\t\n");
			numOfPrints = 0;
		}
		this.textArea.append(msg+"\n");
		numOfPrints ++;
	}

	@Override
	public void clearMsgArea()
	{
		this.textArea.setText(null);
	}

	@Override
	public void reset()
	{
		disclose = false;
		//clean all the players' cards and the hands on table
		for(CardGamePlayer ply : game.getPlayerList())
		{
			ply.removeAllCards();
		}
		game.getHandsOnTable().clear();
		frame.repaint();
		try
		{
			Thread.sleep(50);
		}catch(Exception E) {}
		
		//invoke the animation
		AnimationPanel cardDistribution = new AnimationPanel();

		cardDistribution.setBackground(Color.BLUE);
		cardBoard.remove(handsBoard);
		cardBoard.add(cardDistribution);
		frame.repaint();
		//re-distribute the card
		int playerWithD3 = -1;
		setActivePlayer(playerWithD3);
		game.getHandsOnTable().clear();
		for(int i=0;i<4;i++)
		{
			game.getPlayerList().get(i).removeAllCards();
		}
		frame.repaint();
		game.getDeck().shuffle();
		for(int i=0;i<13;i++)
		{
			for(int j=0;j<4;j++)
			{
				cardDistribution.riseCard();
				frame.repaint();
				try
				{
					Thread.sleep(100);
				}catch(Exception E){}
				//cardDistribution.moveCardTo(i);
				//frame.repaint();
				//try
				//{
				//	Thread.sleep(100);
				//}catch(Exception E){}
				
				Card cardToAdd = game.getDeck().getCard(j+4*i);
				game.getPlayerList().get(j).addCard(cardToAdd);
				
				if(cardToAdd.getRank()==2 && cardToAdd.getSuit()==0)
					playerWithD3 = j;
				
				cardDistribution.cardBack();
				frame.repaint();
				try
				{
					Thread.sleep(100);
				}catch(Exception E){}
			}
		}
		for(int i=0;i<4;i++)
		{
			game.getPlayerList().get(i).sortCardsInHand();
		}
		setActivePlayer(playerWithD3);
		cardBoard.remove(cardDistribution);
		cardBoard.add(handsBoard);
	}

	/**
	 * show all the cards remained in all players
	 */
	public void discloseAllPlayers()
	{
		disclose = true;
	}

	/**
	 * Print message to the chatting block
	 */
	public void printChat(String str)
	{
		//TODO
	}

	@Override
	public void setActivePlayer(int activePlayer)
	{
		((BigTwoClient)game).setPlayerID(activePlayer);
	}
}
