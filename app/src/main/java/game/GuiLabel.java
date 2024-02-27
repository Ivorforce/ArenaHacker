package game;

import org.lwjgl.opengl.GL11;
import org.newdawn.slick.Color;
import org.newdawn.slick.Font;
import org.newdawn.slick.geom.Rectangle;

/**
 * A button that uses LWJGL to draw.
 */
public class GuiLabel extends GuiElement
{

	public String[] displayStringLines;
	public Color displayStringColor;
	public Font displayStringFont;

	public GuiLabel(Rectangle bounds, String[] displayStringLines, Color displayStringColor, Font displayStringFont)
	{
		super(bounds);

		this.displayStringLines = displayStringLines;
		this.displayStringColor = displayStringColor;
		this.displayStringFont = displayStringFont;
	}

	@Override
	public void draw(int mouseX, int mouseY)
	{
		GL11.glColor3f(1, 1, 1);

		for (int i = 0; i < displayStringLines.length; i++)
		{
			float y = (float) (i + 1) / (displayStringLines.length + 2);

			displayStringFont.drawString(bounds.getCenterX() - displayStringFont.getWidth(displayStringLines[i]) / 2, bounds.getMinY() + y * bounds.getHeight() - displayStringFont.getHeight(displayStringLines[i]) / 2, displayStringLines[i], displayStringColor);
		}
	}
}
