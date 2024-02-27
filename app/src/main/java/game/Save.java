package game;

import java.util.ArrayList;
import java.util.Hashtable;

public class Save
{

	private Hashtable<String, Integer> upgrades;
	private Hashtable<String, ArrayList<Integer>> scores;

	public String mapName;

	public static ArrayList<Save> getSaves()
	{
		ArrayList<Save> saves = new ArrayList<Save>();

		return saves;
	}

	public void write()
	{

	}

	public void load()
	{
		
	}
	
	public Save(String mapName, Hashtable<String, Integer> startUpgrades)
	{
		upgrades = startUpgrades;
		scores = new Hashtable<String, ArrayList<Integer>>();

		this.mapName = mapName;
	}

	public int getHighestScore(String name)
	{
		int highestScore = 0;

		if (scores.get(name) != null)
		{
			for (Integer score : scores.get(name))
			{
				if (score > highestScore) highestScore = score;
			}
		}

		return highestScore;
	}

	public void addHighscore(String name, int score)
	{
		if (scores.get(name) == null) scores.put(name, new ArrayList<Integer>());

		scores.get(name).add(score);
	}

	public void upgrade(String name, int value)
	{
		if (upgrades.get(name) == null) upgrades.put(name, value);
		else upgrades.put(name, value + upgrades.get(name));
	}

	public int getUpgrade(String name)
	{
		if (upgrades.get(name) == null) return 0;
		else return upgrades.get(name);
	}
}
