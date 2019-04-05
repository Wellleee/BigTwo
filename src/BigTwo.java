import java.util.ArrayList;

/**
 * This class is used to model a Big Two card game. It stores the a deck of cards, 
 * players, hands of cards on table, the index of current player and a console as
 * an interface for players. 
 * 
 * @author davidliu
 *
 */
public class BigTwo implements CardGame
{
	/**
	 * Constructor for the BigTwo class
	 */
	public BigTwo()
	{
		playerList = new ArrayList<CardGamePlayer>();
		handsOnTable = new ArrayList<Hand>();

		for(int i=0;i<4;i++)
		{
			CardGamePlayer gamePlayer = new CardGamePlayer("Player"+i);
			playerList.add(gamePlayer);
		}
		bigTwoTable = new BigTwoTable(this);
	}
	
	private Deck deck; //a deck of cards
	private ArrayList<CardGamePlayer> playerList; //a list of player
	private ArrayList<Hand> handsOnTable; //a list of hands
	private int currentIdx; //an integer specifying the index of the current player
	private BigTwoTable bigTwoTable; //a BigTwoConsole object for providing the user interface
	
	/**
	 * A method for retrieving the deck of cards being used
	 * 
	 * @return
	 * 		the deck used in current game
	 */
	public Deck getDeck()
	{
		return this.deck;
	}
	
	/**
	 * A method for retrieving the list of players
	 * 
	 * @return
	 * 		the array list for players in current game
	 */
	public ArrayList<CardGamePlayer> getPlayerList()
	{
		return this.playerList;
	}
	
	/**
	 * A method for retrieving the list of hands played on the table
	 * 
	 * @return
	 * 		The array list of hands on table in current game
	 */
	public ArrayList<Hand> getHandsOnTable()
	{
		return this.handsOnTable;
	}
	
	/**
	 * A method for retrieving the index of the current player
	 * 
	 * @return
	 * 		the index of the current player
	 */
	public int getCurrentIdx()
	{
		return this.currentIdx;
	}

	@Override
	/**
	 * A method for starting the game with a (shuffled) deck of
	 * cards supplied as the argument. It implements the Big Two
	 * game logics
	 *  
	 * @param deck
	 * 			The shuffled deck of cards
	 */
	public void start(Deck deck)
	{
		for(int i=0;i<playerList.size();i++)
		{
			playerList.get(i).removeAllCards();
		}
		this.currentIdx = -1;
		this.deck = deck;
		handsOnTable.clear();
		//then distribute the cards
		for(int i=0;i<13;i++)
		{
			for(int j=0;j<playerList.size();j++)
			{
				Card card = deck.removeCard(0);
				playerList.get(j).addCard(card);
				if(card.getRank()==2 && card.getSuit()==0)
					currentIdx = j; //the one with Diamond 3 is the first player
			}
		}
		for(int i=0;i<playerList.size();i++)
		{
			playerList.get(i).sortCardsInHand();
		}
		//Interaction begin
		bigTwoTable.setActivePlayer(currentIdx);
		bigTwoTable.repaint();
		boolean someOneWin = false;
		while(!someOneWin)
		{
			//each turn
			boolean legalMove = false;
			boolean pass	= false;
			Hand currentHand = null;
			CardList cardsElement = null;

			//first select the cards to compose a valid hand
			//once a valid hand is composed, try to beat the upmost hand on table
			//now, a hand that can beats the upmost hand on table is generated
			//then, add it to the handsOnTalbe and elimate the correspoding cards from the player
			while(!legalMove)
			{
				int cardsIdx [] = bigTwoTable.getSelected();
				if(cardsIdx==null) //pass
				{
					pass = handsOnTable.size()==0 || handsOnTable.get(handsOnTable.size()-1).getPlayer()==playerList.get(currentIdx)?
					       false:true;
					//player for last hand == current player ? pass is illegal : pass is legal
					legalMove = pass;
				}
				else
				{
					cardsElement = playerList.get(currentIdx).play(cardsIdx);
					currentHand  = composeHand(playerList.get(currentIdx),cardsElement);
					if(currentHand!=null)//null hand i.e. invalid hand
					{
						if(handsOnTable.size()==0) //first player
						{
							legalMove = currentHand.contains(new BigTwoCard(0,2));
						}
						else if(handsOnTable.get(handsOnTable.size()-1).getPlayer()==playerList.get(currentIdx)) //player of the last hand
						{
							legalMove = true;//if the current player is the one who made the last move, he can play anything
						}
						else
						{
							legalMove = currentHand.beats(handsOnTable.get(handsOnTable.size()-1));
						}//	END: whether it is legal
					}//END: whether it is valid
				}//END: whether it is a legal pass, a legal move or an illegal move
				if(!legalMove)	System.out.println("Not a legal move");
			}
			
			if(pass)	System.out.println("{pass}");
			else
			{
				System.out.print("{"+currentHand.getType()+"} ");
				for(int i=0;i<currentHand.size()-1;i++)
				{
					System.out.print("["+currentHand.getCard(i).toString()+"] ");
				}
				System.out.print("["+currentHand.getCard(currentHand.size()-1).toString()+"]\n");
				handsOnTable.add(currentHand);
				playerList.get(currentIdx).removeCards(cardsElement);
			}//END: print out the current hand and add it to the table
			//After each turn, check for the end
			for(CardGamePlayer onePlayer : playerList)
			{
				if(onePlayer.getCardsInHand().isEmpty())
				{
					someOneWin = true;
					break;
				}
			}//END: check for the number of cards in hand

			currentIdx = (currentIdx+1)%4;
			bigTwoTable.setActivePlayer(currentIdx);
			bigTwoTable.repaint();
		}//END: every turn
		System.out.println("Game ends");
		for(int i=0;i<playerList.size();i++)
		{
			System.out.print("Player "+i+" ");
			if(playerList.get(i).getCardsInHand().isEmpty())
				System.out.print("wins the game.\n");
			else
				System.out.print("has "+playerList.get(i).getCardsInHand().size()+" cards in hand.\n");
		}//END: final print
	}
	
	/**
	 * The main function. It should create a Big Two card game, 
	 * create and shuffle a deck of cards, and start the game with
	 * the deck of cards
	 */
	public static void main(String[] args)
	{
		BigTwo game = new BigTwo();
		BigTwoDeck deck = new BigTwoDeck();
		deck.shuffle();
		game.start(deck);
	}
	
	/**
	 * A method for returning a valid hand from the specified list
	 * of cards of the player
	 * 
	 * @param player
	 * 			The current player who is going to play the cards
	 * @param cards
	 * 			The cards from which a valid hand is going to be
	 * 			returned from
	 * @return
	 * 		The valid hands from the given cards; if no valid hand,
	 * 		return null
	 */
	public static Hand composeHand(CardGamePlayer player, CardList cards)
	{
		//check whether the cards is valid or not
		Hand hand = null;
		if(cards.size()==1)			hand = new Single(player,cards);
		else if (cards.size()==2)	hand = new Pair(player,cards);
		else if (cards.size()==3)	hand = new Triple(player, cards);
		else //suppose to have 5 cards
		{
			if(cards.size()!=5)		return null;
			//otherwise it can be flush, fullhouse, Quad, Straight or StraightFlush
			Flush flushHand = new Flush(player, cards);
			if(flushHand.isValid())
			{
				hand = flushHand;
			}
			else
			{
				FullHouse fullHouseHand = new FullHouse(player, cards);
				if(fullHouseHand.isValid())
				{
					hand = fullHouseHand;
				}
				else
				{
					Quad quadHand = new Quad(player, cards);
					if(quadHand.isValid())
					{
						hand = quadHand;
					}
					else
					{
						Straight straigthHand = new Straight(player, cards);
						if(straigthHand.isValid())
						{
							hand = straigthHand;
						}
						else
						{
							hand = new StraightFlush(player, cards);	
						}
					}
				}
			}

		}

		if(hand==null || !hand.isValid())	return null;
		return hand;
	}

	@Override
	public int getNumOfPlayers()
	{
		return this.playerList.size();
	}

	@Override
	public void makeMove(int playerID, int[] cardIdx)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void checkMove(int playerID, int[] cardIdx)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean endOfGame()
	{
		// TODO Auto-generated method stub
		return false;
	}
}
