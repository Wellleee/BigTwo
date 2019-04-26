import java.net.*;
import java.io.*;
import java.util.ArrayList;

public class BigTwoClient implements CardGame, NetworkGame
{

	/*
	 * Private variables 
	 */
	int numOfPlayers; 				//number of existing numbers
	Deck deck; 						//a shuffled deck, provided by the sever
	ArrayList<CardGamePlayer> handsOnTable; //list of hands played on table
	int playerID;					//index of local player
	String playerName;				//specifying the player's name
	String serverIP;
	int serverPort;
	Socket sock; 
	ObjectOutputStream oos; 		//an ObjectOutputStream for sending messages to the server
	int currentIdx;					//an integer specifying the index of the player for the current turn
	BigTwoTable bigTwoTable;		//GUI
	
	@Override
	public int getPlayerID() 
	{
		return this.playerID;
	}

	@Override
	public void setPlayerID(int playerID)
	{
		this.playerID = playerID;
	}

	@Override
	public String getPlayerName()
	{
		return this.playerName;
	}

	@Override
	public void setPlayerName(String playerName)
	{
		this.playerName = playerName;
	}

	@Override
	public String getServerIP()
	{
		return this.serverIP;
	}

	@Override
	public void setServerIP(String serverIP)
	{
		this.serverIP = serverIP;
	}

	@Override
	public int getServerPort()
	{
		return this.serverPort;
	}

	@Override
	public void setServerPort(int serverPort)
	{
		this.serverPort = serverPort;
	}

	@Override
	public void makeConnection()
	{
		// TODO Auto-generated method stub
	}

	@Override
	public void parseMessage(GameMessage message)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void sendMessage(GameMessage message)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public int getNumOfPlayers()
	{
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Deck getDeck()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ArrayList<CardGamePlayer> getPlayerList()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ArrayList<Hand> getHandsOnTable()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getCurrentIdx()
	{
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void start(Deck deck)
	{
		// TODO Auto-generated method stub

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

	public static void main(String[] args)
	{
		// TODO Auto-generated method stub

	}

	class ServerHandler implements Runnable
	{

		@Override
		public void run()
		{
			// TODO Auto-generated method stub
			
		}
		
	}
}
