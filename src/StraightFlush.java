
/**
 * This is used to model a hand of Straight Flush. 
 * 
 * @author davidliu
 *
 */
public class StraightFlush extends Hand
{
	/**
	 * Constructor for the StraightFlush class
	 * 
	 * @param player
	 * 			The player who play the StraightFlush
	 * @param cards
	 * 			The cards that composes the StraightFlush
	 */
	public StraightFlush (CardGamePlayer player, CardList cards)
	{
		super(player, cards);
	}

	//no need to override the getTopCard method: simply compare the rank and return the highest rank
	
	@Override
	public boolean beats(Hand hand)
	{
		switch(hand.getType())
		{
			case "Straight":
				return true;
			case "Flush":
				return true;
			case "FullHouse":
				return true;
			case "Quad":
				return true;
			default:
				return super.beats(hand);
		}
	}

	@Override
	public boolean isValid()
	{
		this.sort();
		//First determine whether all the suits are the same
		//Exactly the same as the logic of checking Flush
		int suit1 = this.getCard(0).getSuit();
		for(int i=1;i<5;i++)
		{
			if(this.getCard(i).getSuit() != suit1)	return false;
		}
		//Then determine whether all the ranks are in sequence
		//Exactly the same as the logic of checking Straight
		for(int i=1;i<5;i++)
		{
			if(this.getCard(i).getRank()!=this.getCard(i-1).getRank()+1)	return false;
		}
		//up to here, both tests are passed, i.e. valid
		return true;
	}

	@Override
	public String getType()
	{
		return "StraightFlush";
	}
}
