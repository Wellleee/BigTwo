
/**
 * This class is a subclass of the Card class, and is used to model a card used in a
 * Big Two card game. 
 * 
 * @author Yunhao Liu
 */
public class BigTwoCard extends Card
{
	/**
	 * This is the constructor for the BigTwoCard
	 * 
	 * @param suit
	 * 			the suit of the given card, from 0 to 3
	 * 			<p>
	 *            0 = Diamond, 1 = Club, 2 = Heart, 3 = Spade
	 * @param rank
	 * 			the rank of the given card, from 0 to 12
	 * 			<p>
	 *           0 = 'A', 1 = '2', 2 = '3', ..., 8 = '9', 9 = '0', 10 = 'J', 11
	 *           = 'Q', 12 = 'K'
	 */
	public BigTwoCard(int suit, int rank)
	{
		super(suit,rank);
	}
	/**
	 * Compares this card with the specified card for order.
	 * 
	 * @param card
	 * 			the card to be compared
	 * @return a negative integer, zero, or a positive integer as this card is
	 *         less than, equal to, or greater than the specified card
	 */
	public int compareTo(Card card)
	{
		int thisCardValue = this.suit+4*((this.rank+11)%13);
		int OtherCardValue = card.getSuit()+4*((card.getRank()+11)%13);
		return thisCardValue - OtherCardValue;
	}
}
