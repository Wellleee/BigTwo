
/**
 * This is used to model a hand of FullHouse. 
 * 
 * @author davidliu
 *
 */
public class FullHouse extends Hand
{
	private static final long serialVersionUID = -5273312878278139440L;

	/**
	 * Constructor for the FullHouse class
	 * 
	 * @param player The player who play the FullHand
	 * @param cards  The cards that composes the FullHand
	 */
	public FullHouse(CardGamePlayer player, CardList cards)
	{
		super(player, cards);
	}

	@Override
	public Card getTopCard()
	{
		//sort(); already in isValid()
		/*CASE A XXXYY: return the third one*/
		if(this.getCard(2).getRank()==this.getCard(1).getRank())
			return this.getCard(2);
		/*CASE B XXYYY: return the last one*/
		else
			return this.getCard(4);
	}

	@Override
	public boolean beats(Hand hand)
	{
		switch(hand.getType())
		{
			case "Straight":
				return true;
			case "Flush":
				return true;
			default:
				return super.beats(hand);
				//if both are flush, i.e. same type
				//thus, simply compare the top card
				//otherwise, false
		}
	}

	@Override
	public boolean isValid()
	{
		this.sort();
		if(this.getCard(0).getRank()!=this.getCard(1).getRank())	return false;
		if(this.getCard(3).getRank()!=this.getCard(4).getRank())	return false;
		if(this.getCard(2).getRank()!=this.getCard(1).getRank()
		&& this.getCard(2).getRank()!=this.getCard(3).getRank())	return false;
		return true; 
	}

	@Override
	public String getType()
	{
		return "FullHouse";
	}
}
