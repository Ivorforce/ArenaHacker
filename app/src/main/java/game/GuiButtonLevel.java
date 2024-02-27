package game;

import java.awt.Point;
import java.util.ArrayList;

import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.opengl.Texture;

public class GuiButtonLevel extends GuiButton
{

	public GuiButtonLevel(Point position, int index)
	{
		super(new Rectangle(position.x, position.y, 10, 10), index, TextureLoader.getDefaultLoader().getTexture("Bullet.png"), null, null, null, null);
	}

	public String name;

	public int gameRadius;
	public ArrayList<EnemyType> enemySpawnRates;

	public String bossName;

	public int timeLimit;

	@Override
	public void draw(int mouseX, int mouseY)
	{
		Texture finalTexture = texture;

		if (textureHover != null && enabled)
		{
			if (bounds.contains(mouseX, mouseY))
			{
				finalTexture = textureHover;
			}
		}

		if (finalTexture != null) RenderHelper.drawRect(bounds.getMinX(), bounds.getMinY(), bounds.getMaxX(), bounds.getMaxY(), finalTexture);

		if (displayString != null)
		{
			displayStringFont.drawString(bounds.getCenterX() - displayStringFont.getWidth(displayString) / 2, bounds.getCenterY() - displayStringFont.getHeight(displayString) / 2, displayString, displayStringColor);
		}
	}
}
