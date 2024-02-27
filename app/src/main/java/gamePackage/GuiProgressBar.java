package gamePackage;

import java.awt.Image;
import java.awt.image.ImageObserver;

import org.lwjgl.opengl.GL11;
import org.newdawn.slick.Color;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.opengl.Texture;

public class GuiProgressBar extends GuiElement implements ImageObserver
{

	public double progress;

	public Texture segmentTexture;
	public Texture backgroundTexture;
	
	public Color backgroundColor;
	public Color leftColor;
	public Color rightColor;

	public int segments;

	public GuiProgressBar(Rectangle bounds, int segments, Texture segmentImage, Texture background, Color leftColor, Color rightColor)
	{
		super(bounds);

		this.segmentTexture = segmentImage;
		this.backgroundTexture = background;

		this.segments = segments;

		progress = 1;

		this.leftColor = leftColor;
		this.rightColor = rightColor;

		this.backgroundColor = leftColor;
	}

	@Override
	public void draw(int mouseX, int mouseY)
	{
		backgroundColor.bind();

		RenderHelper.drawRect(bounds.getMinX(), bounds.getMinY(), bounds.getMaxX(), bounds.getMaxY(), backgroundTexture);

		if (segments > 0)
		{
			for (int i = 0; i < segments * progress; i++)
			{
				float r = rightColor.r * ((float) i / segments) + leftColor.r * ((float) (segments - i) / segments);
				float g = rightColor.g * ((float) i / segments) + leftColor.g * ((float) (segments - i) / segments);
				float b = rightColor.b * ((float) i / segments) + leftColor.b * ((float) (segments - i) / segments);
				float a = rightColor.a * ((float) i / segments) + leftColor.a * ((float) (segments - i) / segments);

				GL11.glColor4f(r, g, b, a);
				RenderHelper.drawRect(bounds.getMinX() + (bounds.getWidth() / segments) * i, bounds.getMinY(), bounds.getMinX() + (bounds.getWidth() / segments) * (i + 1), bounds.getMaxY(), segmentTexture);
			}
		}
		else
		{
			rightColor.bind();

			RenderHelper.drawRect(bounds.getMinX(), bounds.getMinY(), bounds.getMinX() + (int) (bounds.getWidth() * progress), bounds.getMaxY(), segmentTexture);
		}
	}

	public double getProgress()
	{
		return progress;
	}

	public void setProgress(double progress)
	{
		this.progress = progress;

		if (this.progress > 1) this.progress = 1;
		if (this.progress < 0) this.progress = 0;
	}

	@Override
	public boolean imageUpdate(Image arg0, int arg1, int arg2, int arg3, int arg4, int arg5)
	{

		return false;
	}

}
