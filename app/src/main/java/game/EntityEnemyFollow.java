package game;

import java.awt.Point;

import org.newdawn.slick.Color;
import org.newdawn.slick.opengl.Texture;

public class EntityEnemyFollow extends EntityEnemy
{

	public EntityEnemyFollow(int x, int y, int radius, int livePoints, int shieldPoints, Texture texture, Color color, Texture shieldTexture, Color shieldColor)
	{
		super(x, y, radius, livePoints, shieldPoints, texture, color, shieldTexture, shieldColor);
	}

	@Override
	public void move(Game game, Point playerPosition, int motionBlurSamples)
	{
		speed_x += (playerPosition.x - x) / 13000;
		speed_y += (playerPosition.y - y) / 13000;

		super.move(game, playerPosition, motionBlurSamples);
	}

}
