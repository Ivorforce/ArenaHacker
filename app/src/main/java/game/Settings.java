package game;

import java.util.Hashtable;

public class Settings
{

	public static Settings sharedSettings = new Settings();

	public Hashtable<String, String> settings;

	public Settings()
	{
		settings = new Hashtable<String, String>();
	}

	public void write()
	{
		
	}

	public void load()
	{
		
	}

}
