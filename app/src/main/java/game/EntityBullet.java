package game;

import java.awt.Point;
import java.util.ArrayList;

import org.lwjgl.opengl.GL11;
import org.newdawn.slick.Color;
import org.newdawn.slick.opengl.Texture;

public class EntityBullet extends Entity
{

	public static final int standard = 0;
	public static final int accelerate = 1;

	public Texture texture;
	public Color color;

	public int movementType;

	private ArrayList<Point> oldPositions;
	private int positionsSkipped;

	public int lifePoints;

	public EntityBullet(int x, int y, double speed_x, double speed_y, int radius, Color color, int movementType)
	{
		this.x = x;
		this.y = y;

		this.radius = radius;

		this.speed_x = speed_x;
		this.speed_y = speed_y;

		this.texture = getDefaultTexture();
		this.color = color;

		this.movementType = movementType;

		lifePoints = 50;

		oldPositions = new ArrayList<Point>();
	}

	public void move(int motionBlurSamples, Point playerPosition)
	{
		if (positionsSkipped >= 4)
		{
			oldPositions.add(new Point((int) x, (int) y));
			positionsSkipped = 0;
		}
		else positionsSkipped++;

		while (oldPositions.size() > motionBlurSamples / 4)
			oldPositions.remove(0);

		if (movementType == accelerate)
		{
			speed_x *= 1.002;
			speed_y *= 1.002;
		}

		x += speed_x;
		y += speed_y;
	}

	public void draw()
	{
		int count = 0;
		for (Point pos : oldPositions)
		{
			GL11.glColor4f((float) color.getRed() / 255f, (float) color.getGreen() / 255f, (float) color.getBlue() / 255f, ((float) count / oldPositions.size() * 0.5f));

			RenderHelper.drawRect(pos.x - radius, pos.y - radius, pos.x + radius, pos.y + radius, texture);

			count++;
		}

		color.bind();
		RenderHelper.drawRect(x - radius, y - radius, x + radius, y + radius, texture);
	}

	public boolean checkDead(int gameRadius)
	{
		if (lifePoints <= 0) return true;

		if (Point.distanceSq(0, 0, x, y) > gameRadius * gameRadius) return true;
		if (speed_x == 0 && speed_y == 0) return true;

		return false;
	}

	public static Texture getDefaultTexture()
	{
		return TextureLoader.getDefaultLoader().getTexture("Bullet.png");
	}
}
