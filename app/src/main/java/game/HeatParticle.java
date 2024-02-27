package game;

import org.lwjgl.opengl.GL11;
import org.newdawn.slick.Color;
import org.newdawn.slick.opengl.Texture;

public class HeatParticle extends Particle2D
{
	public double heat;

	public HeatParticle(int xPos, int yPos, double speedX, double speedY, Texture texture, double heat)
	{
		super(xPos, yPos, speedX, speedY, Particle2D.standard, texture, Color.red);

		this.heat = heat;
	}

	@Override
	public void move()
	{
		heat *= 0.985;
		heat -= 0.0001;

		if (heat < 0) heat = 0;

		x += speed_x;
		y += speed_y;

		speed_x *= 0.98;
		speed_y *= 0.98;
	}

	@Override
	public boolean checkDead()
	{

		if (heat == 0) return true;

		return false;
	}

	@Override
	public void draw()
	{

		float alpha = (float) heat * 0.5f;
		if (alpha > 1) alpha = 1;

		GL11.glColor4f(1, 1, 1, alpha);

		RenderHelper.drawRect(x - 100 * heat, y - 100 * heat, x + 100 * heat, y + 100 * heat, texture);
	}
}
