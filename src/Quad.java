
/**
 * This is used to model a hand of Quad.
 * 
 * @author davidliu
 *
 */
public class Quad extends Hand
{
	private static final long serialVersionUID = -2709724225956229438L;

	/**
	 * Constructor for the Quad class
	 * 
	 * @param player The player who play the Quad
	 * @param cards  The cards that composes the Quad
	 */
	public Quad (CardGamePlayer player, CardList cards)
	{
		super(player, cards);
	}

	@Override
	public Card getTopCard()
	{
		//this.sort(); sorted already in isValid()
		/*after sorted, the mid three are of the same rank
		  either the first or the second is the same as the middle three*/
		/*CASE A XXXXY: return the second last*/
		if(this.getCard(0).getRank()==this.getCard(1).getRank())
			return this.getCard(3);
		else
		/*CASE B YXXXX: return the first*/
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
			case "FullHouse":
				return true;
			default:
				return super.beats(hand);
				//default: either hand.getType()=="Straight" -- compare by the top card
				//         or hand.getType() is one of the others -- type mismatch, and return false
		}
	}

	@Override
	public boolean isValid()
	{
		this.sort();
		if(this.getCard(1).getRank()!=this.getCard(2).getRank())	return false;
		if(this.getCard(2).getRank()!=this.getCard(3).getRank())	return false;
		if(this.getCard(0).getRank()==this.getCard(1).getRank()
		&& this.getCard(3).getRank()!=this.getCard(4).getRank())	return true;
		if(this.getCard(4).getRank()==this.getCard(3).getRank()
		&& this.getCard(0).getRank()!=this.getCard(1).getRank())	return true;
		return false;
	}

	@Override
	public String getType()
	{
		return "Quad";
	}
}
