package game;

import java.awt.Point;
import java.util.Random;

import org.newdawn.slick.Color;

public class EntityPowerup extends Entity
{

	public static final int hackExtension = 0;
	public static final int noOverheat = 1;
	public static final int timeSafety = 2;
	public static final int bulletHacking = 2;

	public static final int radius = 10;

	public int effect;

	public EntityPowerup(int effect, double x, double y)
	{
		this.effect = effect;

		this.x = x;
		this.y = y;

		Random gen = new Random();

		this.speed_x = (gen.nextDouble() * 4) - 2.0;
		this.speed_y = (gen.nextDouble() * 4) - 2.0;
	}

	public void move(Game game)
	{
		x += speed_x;
		y += speed_y;

		speed_x = speed_x * 0.99;
		speed_y = speed_y * 0.99;

		if (Point.distanceSq(0, 0, x, y) >= (game.radius - radius) * (game.radius - radius))
		{
			Vector2D movementVector = new Vector2D(speed_x, speed_y);

			Vector2D newMovementVector = movementVector.mirrorAlongVector(new Vector2D(x, y));

			speed_x = -newMovementVector.x;
			speed_y = -newMovementVector.y;
		}
	}

	public void draw()
	{
		Color color = null;
		if (effect == hackExtension) color = Color.red;
		if (effect == noOverheat) color = Color.cyan;
		if (effect == timeSafety) color = Color.green;
		if (effect == bulletHacking) color = Color.yellow;

		color.bind();

		RenderHelper.drawRect(x - radius, y - radius, x + radius, y + radius, TextureLoader.getDefaultLoader().getTexture("Powerup.png"));
	}
}
