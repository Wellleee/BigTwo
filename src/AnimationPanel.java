import java.awt.Graphics;
import java.awt.Image;

import javax.swing.ImageIcon;
import javax.swing.JPanel;


/**
 * To show the animation of distributing cards 
 * 
 * @author davidliu
 *
 */
public class AnimationPanel extends JPanel
{
	private static final long serialVersionUID = -2141698510129977894L;

	/**
	 * the x coordiante of the base card in heap
	 */
	static public final int STARTING_POS_X = 120;
	/**
	 * the y coordiante of the base card in heap
	 */
	static public final int STARTING_POS_Y = 20;
	/**
	 * the distance between every two cards in heap
	 */
	static public final int DIST_BET_CARD_IN_HEAP = 5;
	private int x;
	private int y;
	private int numOfCardsHeap;
	@Override
	public void paintComponent(Graphics g)
	{
		Image cardHeap = new ImageIcon("img/pukeImage/back.png").getImage();
		for(int i=0;i<numOfCardsHeap-1;i++)
		{
			g.drawImage(cardHeap, STARTING_POS_X+i*DIST_BET_CARD_IN_HEAP, STARTING_POS_Y, this);
		}
		g.drawImage(cardHeap, x, y, this);
	}

	/**
	 * Constructor of the animation panel
	 * 
	 * @param lines 
	 * 				the Y coordinates of each player's first card
	 */
	public AnimationPanel()
	{
		this.numOfCardsHeap = 13*4;
		this.x = STARTING_POS_X+(numOfCardsHeap-1)*DIST_BET_CARD_IN_HEAP;
		this.y = STARTING_POS_Y;
	}
	
	/**
	 * Rise the top card on the heap
	 */
	public void riseCard()
	{
		this.y = 0;
	}
	
	/**
	 * Distribute the top card to the given player
	 * 
	 * @param num
	 * 			index of the cards
	 */
	public void moveCardTo(int num)
	{
		this.x = STARTING_POS_X+(num)*21;
		this.y = -STARTING_POS_Y*3;
	}
	
	/**
	 * After distribution, let the image of card-back goes back to the heap; 
	 */
	public void cardBack()
	{
		this.x = STARTING_POS_X+(numOfCardsHeap-1)*DIST_BET_CARD_IN_HEAP;
		this.y = STARTING_POS_Y;
		this.numOfCardsHeap --; 
	}
	
}
