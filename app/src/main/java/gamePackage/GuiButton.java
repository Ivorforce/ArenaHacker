package gamePackage;

import javax.swing.event.EventListenerList;

import org.lwjgl.opengl.GL11;
import org.newdawn.slick.Color;
import org.newdawn.slick.Font;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.opengl.Texture;

/**
 * A button that uses LWJGL to draw.
 */
public class GuiButton extends GuiElement
{

	private EventListenerList listeners;

	public boolean enabled;

	public Texture texture;
	public Texture textureHover;

	public String displayString;
	public Color displayStringColor;
	public Font displayStringFont;

	public void addListener(GuiButtonListener listener)
	{
		listeners.add(GuiButtonListener.class, listener);
	}

	public void removeListener(GuiButtonListener listener)
	{
		listeners.remove(GuiButtonListener.class, listener);
	}

	public GuiButton(Rectangle bounds, int identifier, Texture texture, Texture textureHover, String displayString, Color displayStringColor, Font displayStringFont)
	{
		super(bounds);

		listeners = new EventListenerList();

		enabled = true;

		this.identifier = identifier;

		this.texture = texture;
		this.textureHover = textureHover;

		this.displayString = displayString;
		this.displayStringColor = displayStringColor;
		this.displayStringFont = displayStringFont;
	}

	@Override
	public void mouseClicked(int mouseX, int mouseY, int button)
	{
		if (enabled)
		{
			for (GuiButtonListener listener : listeners.getListeners(GuiButtonListener.class))
			{
				listener.buttonPressed(this);
			}
		}
	}

	@Override
	public void draw(int mouseX, int mouseY)
	{
		GL11.glColor3f(1, 1, 1);

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
