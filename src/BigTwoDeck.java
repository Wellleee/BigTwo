
/**
 * This is used to model a deck of cards used in a Big Two card game
 * 
 * @author davidliu
 *
 */
public class BigTwoDeck extends Deck
{
	/**
	 * This a method for initializing a deck of Big Two cards. It should 
	 * remove all cards from the deck, create 52 Big Two cards and add
	 * them to the deck. 
	 */
	public void initialize()
	{
		this.removeAllCards();
		for(int i=0;i<4;i++)
		{
			for(int j=0;j<13;j++)
			{
				this.addCard(new BigTwoCard(i,j));
			}
		}
	}
}
