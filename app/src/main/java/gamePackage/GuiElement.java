package gamePackage;

import org.newdawn.slick.geom.Rectangle;

public class GuiElement
{

	public Rectangle bounds;
	
	public boolean invisible;
	
	public int identifier;

	public GuiElement(Rectangle bounds)
	{
		this.bounds = bounds;
	}

	public void update()
	{

	}

	public void drawElement(int mouseX, int mouseY)
	{
		if(!invisible) draw(mouseX, mouseY);
	}

	public void draw(int mouseX, int mouseY)
	{

	}

	public void mouseClicked(int mouseX, int mouseY, int button)
	{

	}

	public void mouseDragged(int mouseX, int mouseY, int button)
	{

	}

	public void mouseHovered(int mouseX, int mouseY)
	{

	}
}
