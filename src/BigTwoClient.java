import java.net.*;
import java.io.*;
import java.util.ArrayList;

public class BigTwoClient implements CardGame, NetworkGame
{
	//constant
	/**
	 * The total number of players in each game
	 */
	public static final int TOTAL_NUM_OF_PLAYERS = 4;
	
	

	//Private variables
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
			return;
		}
		try
		{
			oos = new ObjectOutputStream(sock.getOutputStream());	
		} catch (Exception e)
		{
			System.out.println("Fail to send request to the sever");
			errorMsg = "Fail to send request to the sever";
			errorFlag = true;
			return;
		}
		//create a new thread for downward communication
		Runnable commuJob = new ServerHandler();
		Thread commuThread = new Thread(commuJob);
		commuThread.start();
		//send the JOIN
		CardGameMessage connecting = new CardGameMessage(CardGameMessage.JOIN, -1, this.playerName);
		if(!sock.isClosed())	sendMessage(connecting);
		System.out.println("Send Join");
		//send the READY
		connecting = new CardGameMessage(CardGameMessage.READY,-1,null);
		if(!sock.isClosed())	sendMessage(connecting);
		System.out.println("Send Ready");
	}

	/**
	 * Sever the connection with the server
	 */
	public void severConnection()
	{
		try
		{
			sock.close();
		} catch (Exception e) {}
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
				System.out.println("Received JOIN from Server");
				this.playerList.get(message.getPlayerID()).setName((String)message.getData());
				break;
			case CardGameMessage.MOVE:
				System.out.println("Received MOVE from Server");
				checkMove(message.getPlayerID(), (int [])message.getData());
				//currentIdx++; done in checkmove
				break;
			case CardGameMessage.MSG:
				System.out.println("Received MSG from Server");
				bigTwoTable.printChat((String)message.getData());
				break;
			case CardGameMessage.PLAYER_LIST:
				System.out.println("Received PLAYER_LIST from Server");
				this.setPlayerID(message.getPlayerID());
				String [] nameList = (String[])message.getData();
				for(int i=0;i<nameList.length;i++)
				{
					playerList.add(new CardGamePlayer(nameList[i]));
					if(nameList[i]!=null)	numOfPlayers++;
				}
				playerList.get(playerID).setName(playerName);
			case CardGameMessage.QUIT:
				System.out.println("Received QUIT from Server: "+message.getPlayerID());
				if(playerID != message.getPlayerID())
				{
					this.playerList.get(message.getPlayerID()).setName(null);
					this.playerList.get(message.getPlayerID()).removeAllCards();
					bigTwoTable.printMsg("Player <"+playerList.get(message.getPlayerID()).getName()+"> quit the game.");
					if(!endOfGame())
					{
						//stop the game
						this.handsOnTable = new ArrayList<Hand>();
						this.currentIdx = -1;
						this.deck = null;
					}
				}
				CardGameMessage readyMessage = new CardGameMessage(CardGameMessage.READY, this.playerID, null);
				sendMessage(readyMessage);
				break;
			case CardGameMessage.READY:
				System.out.println("Received READY from Server");
				bigTwoTable.printMsg("Player <"+playerList.get(message.getPlayerID()).getName()+" is ready.");
				break;
			case CardGameMessage.START:
				System.out.println("Received START from Server");
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

	/**
	 * specify the current active player
	 * 
	 * @param currentIdx the currentIdx to set
	 */
	public void setCurrentIdx(int currentIdx)
	{
		this.currentIdx = currentIdx;
		if(currentIdx >= 0 && currentIdx < TOTAL_NUM_OF_PLAYERS)
			bigTwoTable.printMsg(playerList.get(currentIdx).getName()+"'s Turn");
	}

	@Override
	public void start(Deck deck)
	{
		this.deck = deck;
		System.out.println(deck.toString());
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
				bigTwoTable.printMsg("Player <"+playerList.get(playerID).getName()+">: {"+hand.getType()+"} ");
				bigTwoTable.printMsg(hand.toString());
				bigTwoTable.setActivePlayer((playerID+1)%TOTAL_NUM_OF_PLAYERS);
			}
			else
			{
				bigTwoTable.printMsg("Player <"+playerList.get(playerID).getName()+">: Not a legal move");
			}//end of a non-pass moving
		}
		else //pass
		{
			if(handsOnTable.size()!=0 && playerList.get(playerID) != handsOnTable.get(handsOnTable.size()-1).getPlayer())
			{
				bigTwoTable.printMsg("Player <"+playerList.get(playerID).getName()+">: {Pass}");
				bigTwoTable.setActivePlayer((playerID+1)%4);
			}
			else
			{
				bigTwoTable.printMsg("Player <"+playerList.get(playerID).getName()+">: Not a legal pass");
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
			try
			{
				Thread.sleep(500);
			} catch (Exception e) {}
			this.setPlayerID(-1);
			bigTwoTable.reJoinGame();
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

	/**
	 * Main method to start the online version of the Big Two game
	 * 
	 * @param args
	 */
	public static void main(String[] args)
	{
		//With in the main stream, create a new client instance for handling
		//a. the GUI
		//b. the game logic
		//c. the upstream message
		BigTwoClient gameClient = new BigTwoClient();
		//BigTwoDeck deck = new BigTwoDeck(); done in parseMessage, downStreamThread
		//game.start(deck); done in parseMessage, downStreamThread
		//gameClient.makeConnection(); invoke by dialogue
		String [] IPPort = gameClient.bigTwoTable.promoptConnection();
		gameClient.setServerIP(IPPort[0]);
		gameClient.setServerPort(Integer.parseInt(IPPort[1]));
		gameClient.makeConnection();
		gameClient.bigTwoTable.go();
		while(true)
		{
			if(gameClient.errorFlag)
			{
				gameClient.bigTwoTable.errorPopup(gameClient.errorMsg);
				gameClient.errorFlag = false;
				gameClient.errorMsg = "";
			}
		}
	}
	
	/**
	 * This is the inner class for the communication thread
	 * 
	 * @author davidLiu
	 *
	 */
	private class ServerHandler implements Runnable
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
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
		
	}
}
