package game;


import java.awt.Point;
import java.util.ArrayList;

import org.lwjgl.opengl.GL11;
import org.newdawn.slick.Color;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.opengl.Texture;

public class EntityEnemy extends Entity
{

	public int lifePoints;
	public int maxLifePoints;
	public int shieldPoints;
	public int maxShieldPoints;

	public Texture texture;
	public Color color;
	public GuiProgressBar healthBar;

	public Texture shieldTexture;
	public Color shieldColor;
	public GuiProgressBar shieldBar;

	public int timeAlive;

	public boolean affectedByFriction;
	public boolean bouncesOffWalls;

	public String bossIdentifier;

	public int defeatScore;

	public ArrayList<Point> oldPositions;

	public EntityEnemy(int x, int y, int radius, int livePoints, int shieldPoints, Texture texture, Color color, Texture shieldTexture, Color shieldColor)
	{
		this.x = x;
		this.y = y;

		this.radius = radius;

		this.lifePoints = livePoints;
		this.maxLifePoints = livePoints;
		this.shieldPoints = shieldPoints;
		this.maxShieldPoints = shieldPoints;

		this.texture = texture;
		this.color = color;

		this.shieldTexture = shieldTexture;
		this.shieldColor = shieldColor;

		Texture barSegment = TextureLoader.getDefaultLoader().getTexture("BarSegment.png");
		Texture barBG = TextureLoader.getDefaultLoader().getTexture("BarBackground.png");

		if (maxShieldPoints != 0) shieldBar = new GuiProgressBar(new Rectangle((int) x - maxShieldPoints / 4, (int) y + radius + 2, maxShieldPoints / 2, 6), maxShieldPoints / 10, barSegment, barBG, new Color(0f, 0.5f, 0.9f), new Color(0f, 0.7f, 1f));
		if (shieldBar != null) shieldBar.backgroundColor = new Color(0f, 0.2f, 0.2f);
		healthBar = new GuiProgressBar(new Rectangle((int) x - maxLifePoints / 4, (int) y + radius - 5, maxLifePoints / 2, 6), maxLifePoints / 10, barSegment, barBG, new Color(1f, 0.4f, 0.4f), new Color(1f, 1f, 0f));
		healthBar.backgroundColor = new Color(0.2f, 0.1f, 0.1f);

		affectedByFriction = true;
		bouncesOffWalls = true;

		timeAlive = 1;

		defeatScore = 1;

		oldPositions = new ArrayList<Point>();
	}

	public void move(Game game, Point playerPosition, int motionBlurSamples)
	{
		oldPositions.add(new Point((int) x, (int) y));
		while (oldPositions.size() > motionBlurSamples)
			oldPositions.remove(0);

		timeAlive++;

		x += speed_x;
		y += speed_y;

		if (affectedByFriction)
		{
			speed_x = speed_x * 0.98;
			speed_y = speed_y * 0.98;
		}
		if (bouncesOffWalls)
		{
			if (Point.distanceSq(0, 0, x, y) >= (game.radius - radius) * (game.radius - radius))
			{
				Vector2D movementVector = new Vector2D(speed_x, speed_y);

				Vector2D newMovementVector = movementVector.mirrorAlongVector(new Vector2D(x, y));

				speed_x = -newMovementVector.x;
				speed_y = -newMovementVector.y;
			}
		}
	}

	public void updateLogic()
	{
		if (shieldBar != null)
		{
			shieldBar.bounds.setLocation((int) x - maxShieldPoints / 4, (int) y + radius + 2);
			shieldBar.progress = (double) shieldPoints / maxShieldPoints;

			if (shieldPoints == 0) shieldBar.backgroundColor.a = 0.2f;
		}
		if (healthBar != null)
		{
			healthBar.bounds.setLocation((int) x - maxLifePoints / 4, (int) y + radius - 5);
			healthBar.progress = (double) lifePoints / maxLifePoints;

			if (lifePoints == 0) healthBar.backgroundColor.a = 0.2f;
		}

		shieldColor.a = (float) shieldPoints / maxShieldPoints;
	}

	public ArrayList<EntityBullet> shoot(Point playerPosition)
	{
		return null;
	}

	public void draw()
	{
		int count = 0;
		for (Point pos : oldPositions)
		{
			GL11.glColor4f((float)color.getRed() / 255f, (float)color.getGreen() / 255f, (float)color.getBlue() / 255f, ((float) count / oldPositions.size() * 0.5f));

			RenderHelper.drawRect(pos.x - radius, pos.y - radius, pos.x + radius, pos.y + radius, texture);

			count++;
		}

		color.bind();
		RenderHelper.drawRect(x - radius, y - radius, x + radius, y + radius, texture);

		if (maxShieldPoints > 0)
		{
			shieldColor.bind();

			RenderHelper.drawRect(x - radius * 1.2, y - radius * 1.2, x + radius * 1.2, y + radius * 1.2, shieldTexture);
		}

		if (shieldBar != null) shieldBar.draw(0, 0);
		if (healthBar != null) healthBar.draw(0, 0);
	}

	public boolean checkDead()
	{
		if (lifePoints <= 0) return true;
		return false;
	}
}
