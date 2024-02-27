package game;

import paulscode.sound.SoundSystem;
import paulscode.sound.SoundSystemConfig;
import paulscode.sound.SoundSystemException;

public class SoundManager
{

	@SuppressWarnings("rawtypes")
	public static Class libraryClass;

	private static SoundManager defaultSystem;

	private SoundSystem soundSystem;
	
	private float musicVolume = 1.0f;

	public static SoundManager getDefaultManager()
	{
		if (defaultSystem == null) defaultSystem = new SoundManager();

		return defaultSystem;
	}

	public SoundManager()
	{
		try
		{
			SoundSystemConfig.addLibrary(libraryClass);
		}
		catch (SoundSystemException e)
		{
			System.err.println("Error linking with the Soundsystem plug-ins.");
		}

		soundSystem = new SoundSystem();
	}

	@SuppressWarnings("rawtypes")
	public static void addCodec(Class codecClass, String fileExtension)
	{
		try
		{
			SoundSystemConfig.setCodec(fileExtension, codecClass);
		}
		catch (SoundSystemException e)
		{
			e.printStackTrace();
		}
	}

	public void addSource(String sourceName, String filePath)
	{
		soundSystem.newStreamingSource(false, sourceName, filePath, true, 0, 0, 0, SoundSystemConfig.ATTENUATION_NONE, 0);
	}

	public void setBackgroundMusic(String fileName)
	{
		if(isPlayingSound("Music")) stopSound("Music");
		soundSystem.removeSource("Music");
		
		soundSystem.backgroundMusic("Music", fileName, true);
		soundSystem.setVolume("Music", musicVolume);
	}

	public void playSound(String sourceName)
	{
		if (!isPlayingSound(sourceName)) soundSystem.play(sourceName);
		soundSystem.setLooping(sourceName, false);
	}

	public void beginPlayingSound(String sourceName)
	{
		playSound(sourceName);
		rewindSound(sourceName);
	}

	public void beginLoopingSound(String sourceName)
	{
		loopSound(sourceName);
		rewindSound(sourceName);
	}

	public void loopSound(String sourceName)
	{
		if (!isPlayingSound(sourceName)) soundSystem.play(sourceName);
		soundSystem.setLooping(sourceName, true);
	}

	public boolean isPlayingSound(String sourceName)
	{
		return (soundSystem.playing(sourceName));
	}

	public void rewindSound(String sourceName)
	{
		soundSystem.rewind(sourceName);
	}

	public void stopSound(String sourceName)
	{
		soundSystem.stop(sourceName);
	}

	public void setMasterVolume(float volume)
	{
		soundSystem.setMasterVolume(volume);
	}

	public double getMasterVolume()
	{
		return soundSystem.getMasterVolume();
	}

	public void setMusicVolume(float volume)
	{
		soundSystem.setVolume("Music", volume);
		musicVolume = volume;
	}

	public double getMusicVolume()
	{
		return musicVolume;
	}

	public void setVolume(String sourceName, float volume)
	{
		soundSystem.setVolume(sourceName, volume);
	}

	public void removeSource(String sourceName)
	{
		soundSystem.removeSource(sourceName);
	}

	public void cleanup()
	{
		soundSystem.cleanup();
	}
}
