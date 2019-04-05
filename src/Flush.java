
/**
 * This is used to model a hand of Flush. 
 * 
 * @author davidliu
 *
 */
public class Flush extends Hand
{
	/**
	 * Constructor for the Flush class
	 * 
	 * @param player
	 * 			The player who play the Flush
	 * @param cards
	 * 			The cards that composes the Flush
	 */
	public Flush(CardGamePlayer player, CardList cards)
	{
		super(player, cards);
	}

	//no need to override the getTopCard method here
		//suits is the same, card with  the highest rank will also win in compare to
	
	@Override
	public boolean beats(Hand hand)
	{
		if(hand.getType()=="Straight")								return true;
		else if(hand.getType()=="Flush")
		{
			if(this.getTopCard().compareTo(hand.getTopCard())>0)	return true;
		}
		return false;
	}

	@Override
	public boolean isValid()
	{
		this.sort();
		int suit1 = this.getCard(0).getSuit();
		for(int i=1;i<5;i++)
		{
			if(this.getCard(i).getSuit() != suit1)	return false;
		}
		int j=1;
		for(;j<5;j++)
		{
			if(this.getCard(j).getRank()!=this.getCard(j-1).getRank()+1)
				break;
		}
		if(j==5)	return false;
		return true;
	}

	@Override
	public String getType()
	{
		return "Flush";
	}
}
