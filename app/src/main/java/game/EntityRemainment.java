package game;


import java.awt.Point;
import java.util.ArrayList;

import org.lwjgl.opengl.GL11;
import org.newdawn.slick.Color;
import org.newdawn.slick.opengl.Texture;

public class EntityRemainment
{
	
	public int radius;

	public Texture texture;
	public Color color;

	private double xPlus;
	private double yPlus;

	private double speed_x;
	private double speed_y;

	private ArrayList<EntityRemainmentPart> oldPositions;

	public EntityRemainment(EntityEnemy enemy)
	{
		radius = enemy.radius;
		texture = enemy.texture;
		color = enemy.color;

		speed_x = enemy.speed_x;
		speed_y = enemy.speed_y;

		oldPositions = new ArrayList<EntityRemainmentPart>();

		int count = 0;
		for (Point point : enemy.oldPositions)
		{
			oldPositions.add(new EntityRemainmentPart(point, ((float) count / enemy.oldPositions.size() * 100) / 255f));

			count++;
		}

		oldPositions.add(new EntityRemainmentPart(new Point((int) enemy.x, (int) enemy.y), 1));
	}

	public void update()
	{
		xPlus += speed_x;
		yPlus += speed_y;

		speed_x *= 0.98;
		speed_y *= 0.98;

		ArrayList<EntityRemainmentPart> removeParts = new ArrayList<EntityRemainmentPart>();

		for (EntityRemainmentPart part : oldPositions)
		{
			part.alpha *= 0.98;
			part.alpha -= 0.01;

			if (part.alpha <= 0) removeParts.add(part);
		}

		oldPositions.removeAll(removeParts);
	}

	public void draw()
	{
		GL11.glPushMatrix();
		GL11.glTranslated(xPlus, yPlus, 0);
		
		for(EntityRemainmentPart part : oldPositions)
		{
			GL11.glColor4f((float) color.getRed() / 255f, (float) color.getGreen() / 255f, (float) color.getBlue() / 255f, part.alpha);

			RenderHelper.drawRect(part.position.x - radius, part.position.y - radius, part.position.x + radius, part.position.y + radius, texture);
		}
		
		GL11.glPopMatrix();
	}

	public boolean checkDone()
	{
		if (oldPositions.size() <= 0) return true;
		return false;
	}
}
