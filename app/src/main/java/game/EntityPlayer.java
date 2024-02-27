package game;

import java.awt.Point;
import java.util.Random;

import org.lwjgl.input.Keyboard;
import org.newdawn.slick.Color;

public class EntityPlayer extends Entity
{

	public int hackRadius = 140;
	public int laserRange = 50;

	public double heat;

	private ParticleSystem particleSystem;

	public boolean invincible = false;

	public int lifePoints;

	public EntityPlayer()
	{
		radius = 2;

		lifePoints = 1;

		particleSystem = new ParticleSystem();
	}

	public void move(boolean slowly, int boundsRadius)
	{
		Random gen = new Random();

		if (!checkDead())
		{
			if (!slowly)
			{
				if (Keyboard.isKeyDown(Keyboard.KEY_W) || Keyboard.isKeyDown(Keyboard.KEY_UP)) speed_y -= 0.13;
				if (Keyboard.isKeyDown(Keyboard.KEY_S) || Keyboard.isKeyDown(Keyboard.KEY_DOWN)) speed_y += 0.13;
				if (Keyboard.isKeyDown(Keyboard.KEY_A) || Keyboard.isKeyDown(Keyboard.KEY_LEFT)) speed_x -= 0.13;
				if (Keyboard.isKeyDown(Keyboard.KEY_D) || Keyboard.isKeyDown(Keyboard.KEY_RIGHT)) speed_x += 0.13;

				double overheat = (heat - 0.2) * 0.8;
				if (overheat < 0) overheat = 0;

				speed_y += (gen.nextDouble() - 0.5) * overheat;
				speed_x += (gen.nextDouble() - 0.5) * overheat;

				speed_x *= 0.98;
				speed_y *= 0.98;
			}
			else
			{
				int desiredSpeed_x = 0;
				int desiredSpeed_y = 0;

				if (Keyboard.isKeyDown(Keyboard.KEY_W) || Keyboard.isKeyDown(Keyboard.KEY_UP)) desiredSpeed_y -= 1;
				if (Keyboard.isKeyDown(Keyboard.KEY_S) || Keyboard.isKeyDown(Keyboard.KEY_DOWN)) desiredSpeed_y += 1;
				if (Keyboard.isKeyDown(Keyboard.KEY_A) || Keyboard.isKeyDown(Keyboard.KEY_LEFT)) desiredSpeed_x -= 1;
				if (Keyboard.isKeyDown(Keyboard.KEY_D) || Keyboard.isKeyDown(Keyboard.KEY_RIGHT)) desiredSpeed_x += 1;

				speed_x = speed_x * 0.9 + desiredSpeed_x * 0.1;
				speed_y = speed_y * 0.9 + desiredSpeed_y * 0.1;

				double overheat = (heat - 0.2) * 0.4;
				if (overheat < 0) overheat = 0;

				speed_y += (gen.nextDouble() - 0.5) * overheat;
				speed_x += (gen.nextDouble() - 0.5) * overheat;
			}

			if (Point.distanceSq(x, y, 0, 0) > boundsRadius * boundsRadius)
			{
				x = -x;
				y = -y;
			}

			x += speed_x;
			y += speed_y;

			double pX = speed_x;
			if (pX < 0) pX = -pX;
			double pY = speed_y;
			if (pY < 0) pY = -pY;
			double fullSpeed = pX + pY;
			double plusSpeed = 0.2;
			if (fullSpeed < 0) plusSpeed = -plusSpeed;

			particleSystem.add(new Particle2D((int) x, (int) y, gen.nextDouble() * (fullSpeed / 4 + plusSpeed) * 2 - (fullSpeed / 4 + plusSpeed), gen.nextDouble() * (fullSpeed / 4 + plusSpeed) * 2 - (fullSpeed / 4 + plusSpeed), Particle2D.standard, TextureLoader.getDefaultLoader().getTexture("Shield.png"), Color.red));
		}

		particleSystem.moveParticles();
	}

	public void draw()
	{
		particleSystem.drawParticles();

		Color.red.bind();

		if (!checkDead()) RenderHelper.drawRect(x - 4, y - 4, x + 4, y + 4, TextureLoader.getDefaultLoader().getTexture("ShieldSmall.png"));
	}

	public void updateHeat(boolean laser, double factor)
	{
		if (laser) heat += 0.0015 * factor;
		else heat -= 0.003;

		if (heat > 1) heat = 1;
		else if (heat < 0) heat = 0;
	}

	public boolean checkDead()
	{
		return lifePoints <= 0;
	}
}
