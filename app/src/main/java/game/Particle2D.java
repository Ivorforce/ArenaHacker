package game;

import org.lwjgl.opengl.GL11;
import org.newdawn.slick.Color;
import org.newdawn.slick.opengl.Texture;

public class Particle2D
{ //Objekt, dass einen Partikel darstellt: Kann sich zeichnen und verhalten

	public static int standard = 0;
	public static int longLife = 1;

	public double x;
	public double y;

	public double radius;

	public double speed_x;
	public double speed_y;

	protected int type;

	protected Texture texture;
	Color color;

	public Particle2D(double xPos, double yPos, double speedX, double speedY, int type, Texture texture, Color color)
	{
		x = xPos;
		y = yPos;

		speed_x = speedX;
		speed_y = speedY;

		this.type = type;

		radius = 3;

		this.texture = texture;
		this.color = color;
	}

	public void move()
	{
		if (type == standard)
		{
			x = x + speed_x;
			y = y + speed_y;

			speed_x = speed_x * 0.97;
			speed_y = speed_y * 0.97;
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
	}

	public void draw()
	{
		if (color == null) GL11.glColor3f(1, 1, 1);
		else color.bind();

		RenderHelper.drawRect(x - radius, y - radius, x + radius, y + radius, texture);
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

		return false;
	}
}
