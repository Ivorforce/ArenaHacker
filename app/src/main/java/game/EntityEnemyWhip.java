package game;

import java.awt.Point;
import java.util.ArrayList;

import org.newdawn.slick.Color;
import org.newdawn.slick.opengl.Texture;

public class EntityEnemyWhip extends EntityEnemyFollow
{

	public EntityEnemyWhip(int x, int y, int radius, int livePoints, int shieldPoints, Texture texture, Color color, Texture shieldTexture, Color shieldColor)
	{
		super(x, y, radius, livePoints, shieldPoints, texture, color, shieldTexture, shieldColor);
	}

	@Override
	public ArrayList<EntityBullet> shoot(Point playerPostition)
	{

		ArrayList<EntityBullet> bullets = new ArrayList<EntityBullet>();

		if (timeAlive % 5 == 0)
		{
			double fullSpeed = Point.distance(0, 0, speed_x, speed_y);

			bullets.add(new EntityBullet((int) x, (int) y, speed_x + speed_x / fullSpeed, speed_y + speed_y / fullSpeed, 2, new Color(0, 0.6f, 1f), EntityBullet.accelerate));
		}

		return bullets;
	}
}
