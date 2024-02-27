package game;

import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.opengl.Texture;

public class GuiSlider extends GuiButton
{

	public double sliderPosition;

	public Texture sliderHandleTexture;

	public GuiSlider(Rectangle bounds, int buttonIndex, Texture backgroundTexture, Texture handleTexture)
	{
		super(bounds, buttonIndex, backgroundTexture, null, null, null, null);

		sliderHandleTexture = handleTexture;
	}

	@Override
	public void mouseClicked(int mouseX, int mouseY, int button)
	{
		if (enabled)
		{
			sliderPosition = (((double) mouseX - (bounds.getMinX() + bounds.getHeight() / 2)) / (bounds.getWidth() - bounds.getHeight()));

			if (sliderPosition > 1) sliderPosition = 1;
			if (sliderPosition < 0) sliderPosition = 0;
		}

		super.mouseClicked(mouseX, mouseY, button);
	}

	@Override
	public void mouseDragged(int mouseX, int mouseY, int button)
	{
		if (enabled)
		{
			sliderPosition = (((double) mouseX - (bounds.getMinX() + bounds.getHeight() / 2)) / (bounds.getWidth() - bounds.getHeight()));

			if (sliderPosition > 1) sliderPosition = 1;
			if (sliderPosition < 0) sliderPosition = 0;
		}

		super.mouseClicked(mouseX, mouseY, button);
	}

	@Override
	public void draw(int mouseX, int mouseY)
	{
		super.draw(mouseX, mouseY);

		int sliderX = (int) ((bounds.getMinX() + bounds.getHeight() / 2) + sliderPosition * (bounds.getWidth() - bounds.getHeight()));

		RenderHelper.drawRect(sliderX - bounds.getHeight() / 2, bounds.getMinY(), sliderX + bounds.getHeight() / 2, bounds.getMaxY(), sliderHandleTexture);
	}

}
