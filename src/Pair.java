
/**
 * This is used to model a hand of Pair. 
 * 
 * @author davidliu
 *
 */
public class Pair extends Hand
{
	private static final long serialVersionUID = -7735787877198652522L;

	/**
	 * Constructor for the pair class
	 * 
	 * @param player The player who play the pair
	 * @param cards  The cards that composes the pair
	 */
	public Pair(CardGamePlayer player, CardList cards)
	{
		super(player, cards);
	}

	@Override
	public boolean isValid()
	{
		this.sort();
		if(this.getCard(0).getRank()==this.getCard(1).getRank())
		{
			return true;
		}
		return false;
	}

	@Override
	public String getType()
	{
		return "Pair";
	}
}
