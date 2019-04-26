import java.util.ArrayList;

public class BigTwoClient implements CardGame, NetworkGame
{

	@Override
	public int getPlayerID() 
	{
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setPlayerID(int playerID)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public String getPlayerName()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setPlayerName(String playerName)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public String getServerIP()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setServerIP(String serverIP)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public int getServerPort()
	{
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setServerPort(int serverPort)
	{
		// TODO Auto-generated method stub

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

}
