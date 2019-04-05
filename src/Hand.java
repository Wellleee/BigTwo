
/**
 * This class is used to model a hand of cards, and contains methods for getting the
 * player of this hand, checking if it is a valid hand, getting the type of this hand,
 * getting the top card of this hand, and checking if it beats a specified hand. 
 * 
 * @author davidliu
 *
 */
public abstract class Hand extends CardList 
{
	private static final long serialVersionUID = 3708415018664975012L;
	private CardGamePlayer player; // the player who plays this hand
	/**
	 * Constructor for the hand class
	 * 
	 * @param player
	 * 			The player who play the hand
	 * @param cards
	 * 			The cards that composes the hand
	 */
	public Hand(CardGamePlayer player, CardList cards)
	{
		this.player = player;
		for(int i=0;i<cards.size();i++)
		{
			this.addCard(cards.getCard(i));
		}
	}
	/**
	 * A method for retrieving the player of this hand
	 * 
	 * @return
	 * 		The player who plays the hand
	 */
	public CardGamePlayer getPlayer()
	{
		return this.player; 
	}
	/**
	 * A method for retrieving the top card of this hand
	 * 
	 * @return
	 * 		The top card of the current hand
	 */
	public Card getTopCard()
	{
		/*generally, get the card which wins in all the compariasion against the other cards
		  to be overriden in the hand with 5 cards*/
		//this.sort(); already in isValid()
		int topIdx = 0;
		for(int i=1;i<this.size();i++)
		{
			if(this.getCard(i).compareTo(this.getCard(topIdx))>0) //win in the comparision
																 //between current card and the winner
			{
				topIdx = i;
			}
		}
		return this.getCard(topIdx);
	}
	/**
	 * A method for checking if this hand beats a specified hand
	 * 
	 * @param hand
	 * 			The given hand for comparison
	 * @return
	 * 		True when this hand beats the given hand
	 * 		False otherwise (includes a. the hands match but the given one looses
	 * 								  b. the hands do not match)
	 */
	public boolean beats(Hand hand)
	{
		//simply compare the top card here
		//to be overiden in the hand with 5 cards
		
		if(this.getType()==hand.getType()
		&& this.getTopCard().compareTo(hand.getTopCard())>0)
		{
			return true;
		}
		return false;
	}
	/**
	 * A method for checking if this is a valid hand
	 * 
	 * @return
	 * 		True when this hand is valid
	 * 		False otherwise
	 */
	public abstract boolean isValid();
	/**
	 * A method for returning a string specifying the type of this hand
	 * 
	 * @return
	 * 		A string represent the type, i.e. 
	 * 			Single, Pair, Triple, Straight, Flush, FullHouse, Quad and StraightFlush
	 */
	public abstract String getType();
}
