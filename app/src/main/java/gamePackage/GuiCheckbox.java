package gamePackage;

import java.awt.Point;

import org.lwjgl.opengl.GL11;
import org.newdawn.slick.Color;
import org.newdawn.slick.Font;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.opengl.Texture;

public class GuiCheckbox extends GuiElement
{

	public boolean isOn;
	public boolean enabled = true;
	
	public Texture checkboxDisabledTexture;
	public Texture checkboxEnabledTexture;
	
	public String displayString;
	public Color displayStringColor;
	public Font displayStringFont;
	
	public GuiCheckbox(Rectangle bounds, int id, String displayString, Color displayStringColor, Font displayStringFont)
	{
		this(bounds, id, displayString, displayStringColor, displayStringFont, null, null);
	}

	public GuiCheckbox(Rectangle bounds, int id, String displayString, Color displayStringColor, Font displayStringFont, Texture checkboxDisabledTexture, Texture checkboxEnabledTexture)
	{
		super(bounds);
		
		this.identifier = id;
		this.displayString = displayString;
		this.displayStringColor = displayStringColor;
		this.displayStringFont = displayStringFont;
		this.checkboxDisabledTexture = checkboxDisabledTexture;
		this.checkboxEnabledTexture = checkboxEnabledTexture;
	}
	
	@Override
	public void mouseClicked(int mouseX, int mouseY, int button)
	{
		if(enabled) isOn = !isOn;
	}
	
	@Override
	public void draw(int mouseX, int mouseY)
	{
		if((isOn && checkboxEnabledTexture == null) || (!isOn && checkboxDisabledTexture == null))
		{
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
			
			GL11.glColor3f(0, 0, 0);
			RenderHelper.drawRect(bounds.getMinX(), bounds.getMinY(), bounds.getMinX() + bounds.getHeight(), bounds.getMaxY(), null);
			GL11.glColor3f(1, 1, 1);
			RenderHelper.drawRect(bounds.getMinX() + 3, bounds.getMinY() + 3, bounds.getMinX() + bounds.getHeight() - 3, bounds.getMaxY() - 3, null);
		}
		if(isOn)
		{
			if(checkboxEnabledTexture != null) RenderHelper.drawRect(bounds.getMinX(), bounds.getMinY(), bounds.getMinX() + bounds.getHeight(), bounds.getMaxY(), checkboxEnabledTexture);
			else
			{
				GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);

				double dist = Point.distance(bounds.getMinX(), bounds.getMinY(), bounds.getMinX() + bounds.getHeight(), bounds.getMaxY());
				
				GL11.glColor3f(0, 0, 0);
				RenderHelper.drawLine(bounds.getMinX() + 3, bounds.getMinY() + 3, bounds.getMinX() + bounds.getHeight() - 3, bounds.getMaxY() - 3, 3, dist, null);

				GL11.glColor3f(0, 0, 0);
				RenderHelper.drawLine(bounds.getMinX() + 3, bounds.getMaxY() - 3, bounds.getMinX() + bounds.getHeight() - 3, bounds.getMinY() + 3, 3, dist, null);
			}
		}
		else if(checkboxDisabledTexture != null) RenderHelper.drawRect(bounds.getMinX(), bounds.getMinY(), bounds.getMinX() + bounds.getHeight(), bounds.getMaxY(), checkboxDisabledTexture);
		
		displayStringFont.drawString(bounds.getCenterX() - displayStringFont.getWidth(displayString) / 2, bounds.getMinY(), displayString, displayStringColor);
	}
}
