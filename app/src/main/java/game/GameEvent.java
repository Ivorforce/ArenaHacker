package game;

import java.util.EventObject;

public class GameEvent extends EventObject
{

	private static final long serialVersionUID = 1L;

	private int score;
	private String levelName;

	private boolean playerWon;

	public GameEvent(Object source, int score, String name, boolean playerWon)
	{
		super(source);
		this.score = score;
		levelName = name;

		this.playerWon = playerWon;
	}

	public String getLevelName()
	{
		return levelName;
	}

	public int getScore()
	{
		return score;
	}

	public boolean hasPlayerWon()
	{
		return playerWon;
	}

}
