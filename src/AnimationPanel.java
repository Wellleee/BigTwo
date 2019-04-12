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
	static public final int CoordinateX = 200;
	static public final int CoordinateY = 200;
	static public final int DistOfAnimation = 100;
	private int x;
	private int y;
	private int [] lines;
	@Override
	public void paintComponent(Graphics g)
	{
		Image cardHeap = new ImageIcon("img/pukeImage/back.png").getImage();
		g.drawImage(cardHeap, 120, 20, this);
		g.drawImage(cardHeap, x, y, this);
	}

	/**
	 * Constructor of the animation panel
	 * 
	 * @param lines 
	 * 				the Y coordinates of each player's first card
	 */
	public AnimationPanel(int [] lines)
	{
		this.x = CoordinateX;
		this.y = CoordinateY-5;
		this.lines = lines;
	}
	
	/**
	 * Distribute the top card to the given player
	 * 
	 * @param playerId
	 * 				Which player to get card
	 */
	public void moveCardTo(int playerId)
	{
		this.x = 4*CoordinateX/5;
		this.y = lines[playerId%4];
	}
	
	/**
	 * After distribution, let the image of card-back goes back to the heap; 
	 */
	public void cardBack()
	{
		this.x = CoordinateX;
		this.y = CoordinateY-5;
	}
	
}
