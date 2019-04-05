
/**
 * This is used to model a hand of Straight. 
 * 
 * @author davidliu
 *
 */
public class Straight extends Hand
{
	/**
	 * Constructor for the Straight class
	 * 
	 * @param player
	 * 			The player who play the Straight
	 * @param cards
	 * 			The cards that composes the Straight
	 */
	public Straight(CardGamePlayer player, CardList cards)
	{
		super(player, cards);
	}

	//no need to override the getTopCard method: simply compare the rank and return the highest rank
	//no need to override the beats method: simply compare the top card for 2 straights

	@Override
	public boolean isValid()
	{
		this.sort();
		for(int i=1;i<5;i++)
		{
			if(this.getCard(i).getRank()!=this.getCard(i-1).getRank()+1)	return false;
		}
		int j=1;
		for(;j<5;j++)
		{
			if(this.getCard(j).getSuit()!=this.getCard(j-1).getSuit())
				break;
		}
		if(j==5)	return false;
		return true;
	}

	@Override
	public String getType()
	{
		return "Straight";
	}
}
