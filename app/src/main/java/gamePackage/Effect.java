package gamePackage;

import java.util.ArrayList;
import java.util.Hashtable;


public class Effect
{
	public String type;
	public Target target;
	public Target secondaryTarget;

	public int startPlayTime;
	public int duration;

	public Hashtable<String, Object> additionalOptions;

	public String soundName;

	public Hashtable<Ball, AnimatedTexture> animatedTextures;
	public AnimatedTexture texture;

	public Effect(String type, Target target, Target secondaryTarget, int playtime, int duration, String soundName, Hashtable<String, Object> additionalOptions)
	{
		this.type = type;
		this.target = target;
		this.secondaryTarget = secondaryTarget;
		this.startPlayTime = playtime;
		this.duration = duration;

		this.soundName = soundName;

		this.additionalOptions = additionalOptions;
		this.animatedTextures = new Hashtable<Ball, AnimatedTexture>();
	}

	public Effect(String type, Target target, Target secondaryTarget, int playtime, int duration, String soundName)
	{
		this.type = type;
		this.target = target;
		this.secondaryTarget = secondaryTarget;
		this.startPlayTime = playtime;
		this.duration = duration;

		this.soundName = soundName;

		additionalOptions = new Hashtable<String, Object>();
		this.animatedTextures = new Hashtable<Ball, AnimatedTexture>();
	}

	public Effect(String type, Target target, Target secondaryTarget, int playtime, int duration, String soundName, Hashtable<String, Object> additionalOptions, AnimatedTexture texture)
	{
		this.type = type;
		this.target = target;
		this.secondaryTarget = secondaryTarget;
		this.startPlayTime = playtime;
		this.duration = duration;

		this.soundName = soundName;

		this.additionalOptions = additionalOptions;

		this.texture = texture;
		this.animatedTextures = new Hashtable<Ball, AnimatedTexture>();
	}

	public Effect(String type, Target target, Target secondaryTarget, int playtime, int duration, String soundName, AnimatedTexture texture)
	{
		this.type = type;
		this.target = target;
		this.secondaryTarget = secondaryTarget;
		this.startPlayTime = playtime;
		this.duration = duration;

		this.soundName = soundName;

		additionalOptions = new Hashtable<String, Object>();

		this.texture = texture;
		this.animatedTextures = new Hashtable<Ball, AnimatedTexture>();
	}

	public void update(Game game)
	{
		if (texture != null)
		{
			ArrayList<Ball> targets = target.getAllBalls(game);

			for (int i = 0; i < targets.size(); i++)
			{
				if (animatedTextures.get(targets.get(i)) != null)
				{
					animatedTextures.get(targets.get(i)).update();
				}
				else
				{
					animatedTextures.put(targets.get(i), texture);
				}
			}
		}
	}
}
