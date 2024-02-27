package game;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Random;

public class PowerupManager
{

	private ArrayList<EntityPowerup> powerups;

	private int hackExtensionTimeLeft;
	private int noOverheatTimeLeft;
	private int timeSafetyTimeLeft;
	private int advancedHackingTimeLeft;

	public PowerupManager()
	{
		powerups = new ArrayList<EntityPowerup>();
	}

	public void addRandomPowerup(double x, double y)
	{
		Random gen = new Random();

		int r = gen.nextInt(4);

		powerups.add(new EntityPowerup(r != 0 ? r != 1 ? r != 2 ? EntityPowerup.hackExtension : EntityPowerup.noOverheat : EntityPowerup.timeSafety : EntityPowerup.bulletHacking, x, y));
	}

	public void movePowerups(Game game)
	{
		for (EntityPowerup powerup : powerups)
		{
			powerup.move(game);
		}
	}

	public void updateLogic(Point playerPosition, int playerRadius)
	{
		ArrayList<EntityPowerup> removePowerups = new ArrayList<EntityPowerup>();

		for (EntityPowerup powerup : powerups)
		{
			if (Point.distance(playerPosition.x, playerPosition.y, powerup.x, powerup.y) < playerRadius + EntityPowerup.radius)
			{
				castEffect(powerup.effect);

				removePowerups.add(powerup);
			}
		}

		powerups.removeAll(removePowerups);

		if (hackExtensionTimeLeft > 0) hackExtensionTimeLeft--;
		if (noOverheatTimeLeft > 0) noOverheatTimeLeft--;
		if (timeSafetyTimeLeft > 0) timeSafetyTimeLeft--;
		if (advancedHackingTimeLeft > 0) advancedHackingTimeLeft--;
	}

	public void drawPowerups()
	{
		for (EntityPowerup powerup : powerups)
		{
			powerup.draw();
		}
	}

	public void castEffect(int effect)
	{
		if (effect == EntityPowerup.hackExtension) hackExtensionTimeLeft += 400;
		if (effect == EntityPowerup.noOverheat) noOverheatTimeLeft += 800;
		if (effect == EntityPowerup.timeSafety) timeSafetyTimeLeft += 350;
		if (effect == EntityPowerup.bulletHacking) advancedHackingTimeLeft += 350;
	}

	public double getHackRadiusFactor()
	{
		if (hackExtensionTimeLeft > 0) return 2;
		else return 1;
	}

	public double getOverheatFactor()
	{
		if (noOverheatTimeLeft > 0) return 0.1;
		else return 1;
	}

	public double getTimeDistortionFactor()
	{
		if (timeSafetyTimeLeft > 0) return 0.1;
		else return 1;
	}

	public boolean advancedHacking()
	{
		if (advancedHackingTimeLeft > 0) return true;
		else return false;
	}
}
