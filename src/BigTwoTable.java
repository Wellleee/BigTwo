import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;

/**
 * This class is used to create the GUI for the BigTwo Card game.
 * 
 * @author davidliu
 *
 */
public class BigTwoTable implements CardGameTable {
	private final static int MAX_CARD_NUM = 13; // maximum num of cards

	public BigTwoTable(CardGame cardGame) {
		// TODO
	}

	private ArrayList<CardGamePlayer> playerList; // the list of players
	private ArrayList<Hand> handsOnTable; // the list of hands played on the table
	private int activePlayer = -1; // the current active player
	private CardGame game; // the bigtwo game
	private boolean[] selected; // which cards are selected

	private JFrame frame; // main window
	private JPanel cardPanel; // panel for showing cards
	private JPanel msgPanel; // panel for the current status
	private JButton playButton; // button to play a hand
	private JButton passButton; // button to pass to the next player
	private JPanel msgArea; // panel to show the message after each game
	private Image[][] cardImage;// storing the card images
	private Image cardBackImage;// storing the card back image
	private Image[] avatars; // storing the avatars image

	@Override
	public void setActivePlayer(int activePlayer) {
		if (activePlayer < 0 || activePlayer >= playerList.size()) {
			this.activePlayer = -1;
		} else {
			this.activePlayer = activePlayer;
		}

	}

	@Override
	public int[] getSelected() {
		// TODO Auto-generated method stub
		CardGamePlayer currentPlayer = playerList.get(this.activePlayer);
		return null;
	}

	@Override
	public void resetSelected() {
		// TODO Auto-generated method stub

	}

	@Override
	public void repaint() {
		// TODO Auto-generated method stub

	}

	@Override
	public void printMsg(String msg) {
		// TODO Auto-generated method stub

	}

	@Override
	public void clearMsgArea() {
		// TODO Auto-generated method stub

	}

	@Override
	public void reset() {
		// TODO Auto-generated method stub

	}

	@Override
	public void enable() {
		// TODO Auto-generated method stub

	}

	@Override
	public void disable() {
		// TODO Auto-generated method stub

	}
	/**
	 * The inner class for the behaviour of the cardPanel
	 */
	class BigTwoPanel extends JPanel implements MouseListener
	{
		@Override
		public void mouseClicked(MouseEvent e)
		{
			//TODO
		}

		@Override
		protected void paintComponent(Graphics g)
		{
			//TODO
		}

		@Override
		public void mousePressed(MouseEvent e)
		{
			//nothing to be done
		}

		@Override
		public void mouseReleased(MouseEvent e)
		{
			//nothing to be done
		}

		@Override
		public void mouseEntered(MouseEvent e)
		{
			//nothing to be done
		}

		@Override
		public void mouseExited(MouseEvent e)
		{
			//nothing to be done
		}
	}
	/**
	 * Realize the behaviour of the play button when it is pressed
	 */
	class PlayButtonListener implements ActionListener
	{
		@Override
		public void actionPerformed(ActionEvent e)
		{
			//TODO	
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
			//TODO
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
			//TODO
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
			//TODO
		}
	}
}
