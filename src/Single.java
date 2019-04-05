
/**
 * This is used to model a hand of Single. 
 * 
 * @author davidliu
 *
 */
public class Single extends Hand
{
	private static final long serialVersionUID = 178749520333021015L;

	/**
	 * Constructor for the Single class
	 * 
	 * @param player The player who play the Single
	 * @param cards  The cards that composes the Single
	 */
	public Single(CardGamePlayer player, CardList cards)
	{
		super(player, cards);
	}

	@Override
	public boolean isValid()
	{
		return true;
	}

	@Override
	public String getType()
	{
		return "Single";
	}
}
