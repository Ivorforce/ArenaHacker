package gamePackage;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Properties;

public class Player
{ //Objekt, das den Spieler darstellt: Speichert Punkte, Einstellungen und Leben

	public int score;
	private Hashtable<String, Integer> highScores;
	public int lives;
	public ArrayList<String> items;

	public Hashtable<String, Object> settings;

	Player()
	{
		lives = 0;
		highScores = new Hashtable<String, Integer>();
	}

	public void registerScore(String slot)
	{
		if (score > getHighScore(slot))
		{
			highScores.put(slot, score);
		}
	}

	public int getHighScore(String slot)
	{
		Integer score = highScores.get(slot);

		if (score == null) score = 0;

		return score;
	}

	public void saveSettings()
	{
		File settingsDirectory = getSettingsDirectory();

		File settingsFile = new File(settingsDirectory, "Settings.properties");

		FileOutputStream outStream = null;
		try
		{
			outStream = new FileOutputStream(settingsFile);
		}
		catch (FileNotFoundException e)
		{
			System.out.println("Could not save settings...");
		}

		Properties settingsProps = new Properties();

		for (Object key : getStandardProperties().keySet())
		{
			String keyString = (String) key;

			settingsProps.setProperty(keyString, settings.get(keyString).toString());
		}

		try
		{
			settingsProps.store(outStream, "Created by Ivorius");
		}
		catch (IOException e)
		{
			System.out.println("Could not save settings...");
		}

		//-----------------------------------------------------------Scores----------------------------------------------------------

		File scoresFile = new File(settingsDirectory, "Scores.properties");

		Properties scoresProps = new Properties();

		FileOutputStream scoresOut = null;
		try
		{
			scoresOut = new FileOutputStream(scoresFile);
		}
		catch (FileNotFoundException e)
		{
			System.out.println("Could not save scores...");
		}

		for (Object key : highScores.keySet())
		{
			String keyString = (String) key;

			scoresProps.setProperty(keyString, "" + getHighScore(keyString));
		}

		try
		{
			scoresProps.store(scoresOut, "Created by Ivorius");
		}
		catch (IOException e)
		{
			System.out.println("Could not save scores...");
		}
	}

	public void loadSettings()
	{
		settings = new Hashtable<String, Object>();

		File settingsDirectory = getSettingsDirectory();

		File settingsFile = new File(settingsDirectory, "Settings.properties");

		Properties standardProps = getStandardProperties();
		Properties settingsProps = new Properties(standardProps);

		settingsProps.putAll(standardProps);

		FileInputStream inStream = null;
		try
		{
			inStream = new FileInputStream(settingsFile);
		}
		catch (FileNotFoundException e1)
		{
			System.out.println("Could not find settings...");
		}

		if (inStream != null)
		{
			try
			{
				settingsProps.load(inStream);
			}
			catch (IOException e)
			{
				System.out.println("Could not find settings...");
			}
		}

		for (Object key : getStandardProperties().keySet())
		{
			String keyString = (String) key;
			Object setting = null;

			Integer settingInt = null;
			try
			{
				settingInt = Integer.valueOf(settingsProps.getProperty(keyString));
			}
			catch (NumberFormatException e)
			{
			}
			if (settingInt != null) setting = settingInt;

			if (setting == null)
			{
				Double settingDouble = null;
				try
				{
					settingDouble = Double.valueOf(settingsProps.getProperty(keyString));
				}
				catch (NumberFormatException e)
				{
				}
				if (settingDouble != null) setting = settingDouble;
			}

			if (setting == null && (settingsProps.getProperty(keyString).matches("true") || settingsProps.getProperty(keyString).matches("false")))
			{
				Boolean settingBool = null;
				try
				{
					settingBool = Boolean.valueOf(settingsProps.getProperty(keyString));
				}
				catch (NumberFormatException e)
				{
				}
				if (settingBool != null) setting = settingBool;
			}

			if (setting == null)
			{
				setting = settingsProps.getProperty(keyString);
			}

			if (setting != null) settings.put(keyString, setting);
		}

		//-----------------------------------------------------------Scores----------------------------------------------------------

		File scoresFile = new File(settingsDirectory, "Scores.properties");

		Properties scoresProps = new Properties();

		FileInputStream scoresStream = null;
		try
		{
			scoresStream = new FileInputStream(scoresFile);
		}
		catch (FileNotFoundException e1)
		{
			System.out.println("Could not find scores...");
		}

		if (scoresStream != null)
		{
			try
			{
				scoresProps.load(scoresStream);
			}
			catch (IOException e)
			{
				System.out.println("Could not find scores...");
			}
		}

		for (Object key : scoresProps.keySet())
		{
			String keyString = (String) key;
			highScores.put(keyString, Integer.valueOf(scoresProps.getProperty(keyString)));
		}
	}

	public void resetSettings()
	{
		System.out.println("Reset Game");

		File settingsDirectory = getSettingsDirectory();

		File settingsFile = new File(settingsDirectory, "Settings.properties");
		settingsFile.delete();
		File scoresFile = new File(settingsDirectory, "Scores.properties");
		scoresFile.delete();

		highScores = new Hashtable<String, Integer>();
		settings = new Hashtable<String, Object>();

		loadSettings();
	}

	public static File getSettingsDirectory()
	{

		String userHome = System.getProperty("user.home");
		if (userHome == null)
		{
			System.out.println("Could not find User Home!");
		}
		File home = new File(userHome);
		File settingsDirectory = new File(home, ".dodgegame");
		if (!settingsDirectory.exists())
		{
			try
			{
				if (!settingsDirectory.mkdir()) System.out.println("Could not create Settings Directory!");
			}
			catch (IllegalStateException e)
			{
				System.out.println("Could not create Settings Directory!");
			}
		}
		return settingsDirectory;
	}

	public static Properties getStandardProperties()
	{
		Properties settingsProps = new Properties();

		settingsProps.put("GameSpeed", Double.valueOf(0.5).toString());
		
		settingsProps.put("MasterVolume", Double.valueOf(1.0).toString());
		settingsProps.put("MusicVolume", Double.valueOf(0.5).toString());

		settingsProps.put("ParticlesAllowed", Boolean.valueOf(true).toString());
		settingsProps.put("AdvancedParticlesAllowed", Boolean.valueOf(true).toString());
		settingsProps.put("MousePlayerAllowed", Boolean.valueOf(true).toString());
		settingsProps.put("KeyboardPlayerAllowed", Boolean.valueOf(false).toString());

		settingsProps.put("Skin", "Standard");

		return settingsProps;
	}
}
