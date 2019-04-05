
/**
 * This is used to model a hand of Triple. 
 * 
 * @author davidliu
 *
 */
public class Triple extends Hand
{
	private static final long serialVersionUID = 2109395258622002051L;

	/**
	 * Constructor for the triple class
	 * 
	 * @param player The player who play the triple
	 * @param cards  The cards that composes the triple
	 */
	public Triple (CardGamePlayer player, CardList cards)
	{
		super(player, cards);
	}

	@Override
	public boolean isValid()
	{
		this.sort();
		int rank1 = this.getCard(0).getRank();
		if(rank1 == this.getCard(1).getRank() && rank1 == this.getCard(2).getRank())
		{
			return true;
		}
		return false;
	}

	@Override
	public String getType()
	{
		return "Triple";
	}
}
