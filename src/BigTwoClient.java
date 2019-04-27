import java.net.*;
import java.io.*;
import java.util.ArrayList;

public class BigTwoClient implements CardGame, NetworkGame
{

	/*
	 * Private variables 
	 */
	private String errorMsg;				//Error message to be shown on the GUI
	private boolean errorFlag; 				//Flag to determine whether error occurs
	private int numOfPlayers; 				//number of existing numbers
	private Deck deck; 						//a shuffled deck, provided by the sever
	private ArrayList<CardGamePlayer> playerList;
	private ArrayList<Hand> handsOnTable; //list of hands played on table
	private int playerID;					//index of local player
	private String playerName;				//specifying the player's name
	private String serverIP;
	private int serverPort;
	private Socket sock; 
	private ObjectOutputStream oos; 		//an ObjectOutputStream for sending messages to the server
	private int currentIdx;					//an integer specifying the index of the player for the current turn
	private BigTwoTable bigTwoTable;		//GUI
	

	/**
	 * Constructor for the BigTwoClient
	 */
	public BigTwoClient()
	{
		this.numOfPlayers = 0; //before connection, this player is not in the game as well
		this.deck = null;
		this.playerList = new ArrayList<CardGamePlayer>();
		this.handsOnTable = new ArrayList<Hand>();
		this.playerID = -1; //default index
		this.currentIdx = -1;
		this.bigTwoTable = new BigTwoTable(this);
		this.serverPort = -1;
		//others: null
		//sock and oos: going to be refered to created instances in the main
	}

	@Override
	public int getPlayerID() 
	{
		return this.playerID;
	}

	@Override
	public synchronized void setPlayerID(int playerID)
	{
		this.playerID = playerID;
	}

	@Override
	public String getPlayerName()
	{
		return this.playerName;
	}

	@Override
	public synchronized void setPlayerName(String playerName)
	{
		this.playerName = playerName;
	}

	@Override
	public String getServerIP()
	{
		return this.serverIP;
	}

	@Override
	public synchronized void setServerIP(String serverIP)
	{
		this.serverIP = serverIP;
	}

	@Override
	public int getServerPort()
	{
		return this.serverPort;
	}

	@Override
	public synchronized void setServerPort(int serverPort)
	{
		this.serverPort = serverPort;
	}

	@Override
	public void makeConnection()
	{
		//create a new ObjectOutputStream instance
		try
		{
			sock = new Socket(this.serverIP, this.serverPort);	
		} catch (Exception e)
		{
			System.out.println("Fail to connect the server");
			errorMsg = "Fail to connect the server";
			errorFlag = true;
		}
		try
		{
			oos = new ObjectOutputStream(sock.getOutputStream());	
		} catch (Exception e)
		{
			System.out.println("Fail to send request to the sever");
			errorMsg = "Fail to send request to the sever";
			errorFlag = true;
		}
		//create a new thread for downward communication
		Runnable commuJob = new ServerHandler();
		Thread commuThread = new Thread(commuJob);
		commuThread.start();
		//send the JOIN
		CardGameMessage connecting = new CardGameMessage(CardGameMessage.JOIN, -1, this.playerName);
		if(!sock.isClosed())	sendMessage(connecting);
		//send the READY
		connecting.setPlayerID(this.playerID);
		connecting.setData(null);
		connecting.setType(CardGameMessage.READY);
		if(!sock.isClosed())	sendMessage(connecting);
	}

	@Override
	public synchronized void parseMessage(GameMessage message)
	{
		switch (message.getType())
		{
			case CardGameMessage.FULL:
				bigTwoTable.printMsg("The sever is FULL\nCannot join");
				try 
				{
					sock.close();
				} catch (Exception e)
				{
					e.printStackTrace();
				}
				break;
			case CardGameMessage.JOIN:
			//When receiving JOIN, i.e. a new comer
			//should add the player into the player list
				CardGamePlayer newPlayer = new CardGamePlayer((String)message.getData());
				this.playerList.add(message.getPlayerID(),newPlayer);
				this.numOfPlayers++;
				break;
			case CardGameMessage.MOVE:
				checkMove(message.getPlayerID(), (int [])message.getData());
				//currentIdx++; done in checkmove
				break;
			case CardGameMessage.MSG:
				bigTwoTable.printChat((String)message.getData());
				break;
			case CardGameMessage.PLAYER_LIST:
				this.setPlayerID(message.getPlayerID());
				String [] nameList = (String[])message.getData();
				if(nameList==null)	break;
				for(int i=0;i<nameList.length;i++)
				{
					playerList.add(new CardGamePlayer(nameList[i]));
					numOfPlayers++;
				}
				break;
			case CardGameMessage.QUIT:
				this.playerList.remove(message.getPlayerID());
				this.numOfPlayers--;
				bigTwoTable.printMsg("Player "+message.getPlayerID()+" quit the game.");
				if(!endOfGame())
				{
					//stop the game
					this.handsOnTable = null;
					this.currentIdx = -1;
					this.deck = null;
				}
				CardGameMessage readyMessage = new CardGameMessage(CardGameMessage.READY, this.playerID, null);
				sendMessage(readyMessage);
				break;
			case CardGameMessage.READY:
				bigTwoTable.printMsg("Player "+message.getPlayerID()+" is ready.");
				break;
			case CardGameMessage.START:
				start((Deck)message.getData());
				//bigTwoTable.printMsg("Game start"); done in start
				break;
			default:
				errorFlag = true;
				errorMsg = "Cannot parsing the server response";
				break;
		}
		bigTwoTable.repaint();
	}

	@Override
	public void sendMessage(GameMessage message)
	{
		try
		{
			oos.writeObject(message);
		}
		catch(IOException ioE)
		{
			ioE.printStackTrace();
		}
	}

	@Override
	public int getNumOfPlayers()
	{
		return this.numOfPlayers;
	}

	@Override
	public Deck getDeck()
	{
		return this.deck;
	}

	@Override
	public ArrayList<CardGamePlayer> getPlayerList()
	{
		return this.playerList;
	}

	@Override
	public ArrayList<Hand> getHandsOnTable()
	{
		return this.handsOnTable;
	}

	@Override
	public int getCurrentIdx()
	{
		return this.currentIdx;
	}

	@Override
	public void start(Deck deck)
	{
		this.deck = deck;
		//then distribute the cards
		bigTwoTable.reset();
		//Interaction begin
		bigTwoTable.repaint();
	}

	@Override
	public void makeMove(int playerID, int[] cardIdx)
	{
		CardGameMessage move = new CardGameMessage(CardGameMessage.MOVE, playerID, cardIdx);
		sendMessage(move);
	}

	@Override
	public void checkMove(int playerID, int[] cardIdx)
	{
		CardGamePlayer player = playerList.get(playerID);
		CardList cardInHand = player.play(cardIdx);
		boolean legalMove = false;
		if(cardInHand != null) // not a pass
		{
			Hand hand = composeHand(player, cardInHand);
			if(hand != null) //not invalid
			{
				if(handsOnTable.size()==0)//first player
				{
					legalMove = hand.contains(new BigTwoCard(0, 2))?true:false;
				}
				else if(hand.beats(handsOnTable.get(handsOnTable.size()-1)) || 
				   handsOnTable.get(handsOnTable.size()-1).getPlayer() == player)
				//win or pass to the same person
				{
					legalMove = true;	
				}
			}//end of legal checking
			if(legalMove)
			{
				handsOnTable.add(hand);
				player.removeCards(cardInHand);
				bigTwoTable.printMsg("{"+hand.getType()+"} ");
				bigTwoTable.printMsg(hand.toString());
				bigTwoTable.setActivePlayer((playerID+1)%getNumOfPlayers());
			}
			else
			{
				bigTwoTable.printMsg("Not a legal move");
			}//end of a non-pass moving
		}
		else //pass
		{
			if(handsOnTable.size()!=0 && playerList.get(playerID) != handsOnTable.get(handsOnTable.size()-1).getPlayer())
			{
				bigTwoTable.printMsg("{Pass}");
				bigTwoTable.setActivePlayer((playerID+1)%getNumOfPlayers());
			}
			else
			{
				bigTwoTable.printMsg("Not a legal move");
			}
		}
		//bigTwoTable.repaint() ---- no need, already included in setActiveplayer
		if(endOfGame())
		{
			bigTwoTable.printMsg("Game ends");
			for(int i=0;i<playerList.size();i++)
			{
				bigTwoTable.printMsg("Player "+i+" ");
				if(playerList.get(i).getCardsInHand().isEmpty())
					bigTwoTable.printMsg("wins the game. ");
				else
					bigTwoTable.printMsg("has "+playerList.get(i).getCardsInHand().size()+" cards in hand.\n");
			}//END: final print
			bigTwoTable.disable();
			bigTwoTable.discloseAllPlayers();
			bigTwoTable.repaint();
		}
	}

	@Override
	public boolean endOfGame()
	{
		//copy from the BigTwo
		//check for the end
		for(CardGamePlayer onePlayer : playerList)
		{
			if(onePlayer.getCardsInHand().isEmpty())
			{
				return true;
			}
		}
		return false;
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

	public static void main(String[] args)
	{
		//With in the main stream, create a new client instance for handling
		//a. the GUI
		//b. the game logic
		//c. the upstream message
		BigTwoClient gameClient = new BigTwoClient();
		//BigTwoDeck deck = new BigTwoDeck(); done in parseMessage, downStreamThread
		//game.start(deck); done in parseMessage, downStreamThread
	}

	class ServerHandler implements Runnable
	{
		@Override
		public void run()
		{
			try
			{
				ObjectInputStream ois = new ObjectInputStream(sock.getInputStream());
				while(!sock.isClosed())
				{
					CardGameMessage messageReceived = (CardGameMessage)ois.readObject();
					parseMessage(messageReceived);
				}
			} catch (Exception e)
			{
				e.printStackTrace();
			}
		}
		
	}
}
