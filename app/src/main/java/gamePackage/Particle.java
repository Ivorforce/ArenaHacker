package gamePackage;

import java.util.ArrayList;
import java.util.Random;

import org.newdawn.slick.Color;


public class Particle
{ 
	public static int standard = 0;
	public static int longLife = 1;
	public static int fire = 2;

	public double x;
	public double y;

	public int radius;

	public double speed_x;
	public double speed_y;

	private int type;
	public Color color;
	public Color startColor;

	public int startPlayTime;

	public ArrayList<Particle> subParticles;
	private int spawnDelay;

	AnimatedTexture texture;

	public boolean willBecomeDead;

	public Particle(int xPos, int yPos, double speedX, double speedY, int type, Color color, int playTime)
	{
		startPlayTime = playTime;

		x = xPos;
		y = yPos;

		speed_x = speedX;
		speed_y = speedY;

		this.type = type;

		this.color = color;
		this.startColor = color;

		radius = 3;

		if (type == fire)
		{
			subParticles = new ArrayList<Particle>();
		}
	}

	public void update(int ticks)
	{
		if (type == standard)
		{
			x = x + speed_x;
			y = y + speed_y;

			speed_x = speed_x * 0.98;
			speed_y = speed_y * 0.98;
		}
		else if (type == longLife)
		{
			if (speed_x > 0.11 && speed_y > 0.11)
			{
				x = x + speed_x;
				y = y + speed_y;

				speed_x = speed_x * 0.94;
				speed_y = speed_y * 0.94;
			}
			else
			{
				x = x + speed_x;
				y = y + speed_y;

				speed_x = speed_x * 0.995;
				speed_y = speed_y * 0.995;
			}
		}
		if (type == fire)
		{
			double colorStrength = (double) (ticks - startPlayTime) / 100;
			if (colorStrength > 1) colorStrength = 1;
			if (colorStrength < 0) colorStrength = 0;
			colorStrength = 1.0 - colorStrength;

			this.color = new Color((int) (startColor.getRed() * colorStrength), (int) (startColor.getGreen() * colorStrength), (int) (startColor.getBlue() * colorStrength), (int) (0.5 + startColor.getBlue() * colorStrength * 0.5));

			speed_x = speed_x * 0.98;
			speed_y = speed_y * 0.98;

			x = x + speed_x;
			y = y + speed_y + 0.25;

			if (spawnDelay <= 0)
			{
				Random gen = new Random();

				Particle newParticle = new Particle((int) x, (int) y, ((gen.nextDouble() * 2 - 1.0) * speed_x), ((gen.nextDouble() * 2 - 1.0) * speed_y), Particle.standard, new Color(color.getRed(), color.getGreen(), color.getBlue(), 50), ticks);

				newParticle.radius = 10;
				newParticle.texture = TextureLoader.getDefaultLoader().getAnimatedTexture("SmokeA", ".png", 13);
				newParticle.texture.repeats = false;
				newParticle.texture.imageSpeed = 0.1;
				subParticles.add(newParticle);
				spawnDelay = 20;
			}
			else
			{
				spawnDelay--;
			}
		}

		if (subParticles != null)
		{
			for (int i = 0; i < subParticles.size(); i++)
			{
				subParticles.get(i).update(ticks);
			}
		}

		if (texture != null) texture.update();
	}

	public void draw()
	{
		color.bind();
		
		if (type == standard || type == longLife || type == fire)
		{
			RenderHelper.drawRect((int) x - radius, (int) y - radius, x + radius, y + radius, texture.getCurrentTexture());
		}
		if (subParticles != null)
		{
			for (int i = 0; i < subParticles.size(); i++)
			{
				subParticles.get(i).draw();
			}
		}
	}

	public boolean checkDead()
	{
		if (type == standard)
		{
			if (speed_x < 0.1 && speed_x > -0.1 && speed_y < 0.1 && speed_y > -0.1) { return true; }
		}
		else if (type == longLife)
		{
			if (speed_x < 0.01 && speed_x > -0.01 && speed_y < 0.01 && speed_y > -0.01) { return true; }
		}
		else if (type == fire)
		{
			if (speed_x < 0.1 && speed_x > -0.1 && speed_y < 0.1 && speed_y > -0.1) { return true; }
		}

		return false;
	}

	public void setDead()
	{
		color = new Color(color.getRed(), color.getGreen(), color.getBlue(), 30);

		if (subParticles != null)
		{
			subParticles.clear();
		}
	}
}
