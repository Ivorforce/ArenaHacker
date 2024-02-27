package game;

import java.awt.Point;
import java.util.ArrayList;

import org.newdawn.slick.Color;
import org.newdawn.slick.opengl.Texture;

public class EntityEnemyCircular extends EntityEnemy
{

	public EntityEnemyCircular(int x, int y, int radius, int livePoints, int shieldPoints, Texture texture, Color color, Texture shieldTexture, Color shieldColor)
	{
		super(x, y, radius, livePoints, shieldPoints, texture, color, shieldTexture, shieldColor);
	}

	@Override
	public ArrayList<EntityBullet> shoot(Point playerPostition)
	{
		ArrayList<EntityBullet> bullets = new ArrayList<EntityBullet>();

		if (timeAlive % 200 == 0)
		{
			for (int i = 0; i < 8; i++)
			{
				int degrees = i * 45;

				double bulletSpeed_x = Math.sin((double) degrees / 180 * Math.PI);
				double bulletSpeed_y = Math.cos((double) degrees / 180 * Math.PI);

				bullets.add(new EntityBullet((int) x, (int) y, bulletSpeed_x, bulletSpeed_y, 5, new Color(0, 0.6f, 1f), EntityBullet.standard));
			}
		}

		return bullets;
	}
}
