package game;

import java.awt.Dimension;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Random;

import org.lwjgl.opengl.GL11;
import org.newdawn.slick.Color;
import org.newdawn.slick.opengl.Texture;

public class ArenaBackground
{

	public Texture texture;
	public Dimension textureSize;
	public double parallax;

	public int gameRadius;

	private int timeAlive;

	private ArrayList<Entity> flowingData;

	public ArenaBackground(Texture texture, Dimension textureSize, double parallax, int gameRadius)
	{
		this.texture = texture;
		this.textureSize = textureSize;

		this.parallax = parallax;

		this.gameRadius = gameRadius;

		flowingData = new ArrayList<Entity>();
	}

	public void update()
	{
		timeAlive++;

		Random gen = new Random();

		if (gen.nextInt((int) (gameRadius / 0.0002)) == 0)
		{
			Entity data = new Entity();
			data.x = gen.nextInt(gameRadius * 2) - gameRadius;
			data.y = gen.nextInt(gameRadius * 2) - gameRadius;

			int speedDirection = gen.nextInt(4);

			if (speedDirection == 0) data.speed_x = 4;
			else if (speedDirection == 1) data.speed_x = -4;
			else if (speedDirection == 2) data.speed_y = 4;
			else data.speed_y = -4;

			flowingData.add(data);
		}

		ArrayList<Entity> removeData = new ArrayList<Entity>();

		for (Entity data : flowingData)
		{
			data.x += data.speed_x;
			data.y += data.speed_y;

			if (data.x > gameRadius || data.y > gameRadius || data.x < -gameRadius || data.y < -gameRadius) removeData.add(data);
		}

		flowingData.removeAll(removeData);
	}

	public void draw(Point translation, double hacking, Dimension size)
	{
		Random gen = new Random();

		Point actualTranslation = new Point(translation.x + (int) ((gen.nextDouble() - 0.5) * hacking * 4), translation.y + (int) ((gen.nextDouble() - 0.5) * hacking * 4));

		GL11.glPushMatrix();

		Color unhackedColor = Color.white;
		Color hackedColor = Color.red;

		double timeAliveSin = Math.sin((float) timeAlive / 90 * Math.PI);

		double hackedInfluence = hacking - timeAliveSin * 0.2;
		double unhackedInfluence = 1.0 - hackedInfluence;

		Color color = new Color((int) (unhackedColor.getRed() * unhackedInfluence + hackedColor.getRed() * hackedInfluence), (int) (unhackedColor.getGreen() * unhackedInfluence + hackedColor.getGreen() * hackedInfluence), (int) (unhackedColor.getBlue() * unhackedInfluence + hackedColor.getBlue() * hackedInfluence), (int) (unhackedColor.getAlpha() * unhackedInfluence + hackedColor.getAlpha() * hackedInfluence));

		color.bind();

		GL11.glTranslated(-((-actualTranslation.x * parallax) % textureSize.width), -((-actualTranslation.y * parallax) % textureSize.height), 0);

		int rowT = 0; //Translation for negative area
		if (-translation.y < 0) rowT = -1;
		int columnT = 0; //Translation for negative area
		if (-translation.x < 0) columnT = -1;

		for (int row = 0; row < ((double) size.height / textureSize.height) + 1; row++)
		{
			for (int column = 0; column < ((double) size.width / textureSize.width) + 1; column++)
			{
				RenderHelper.drawRect(textureSize.width * (column + columnT), textureSize.height * (row + rowT), textureSize.width * (column + columnT) + textureSize.width, textureSize.height * (row + rowT) + textureSize.height, texture);
			}
		}

		GL11.glColor4f(0.4f, 0.7f, 1, 0.5f);

		for (Entity data : flowingData)
		{
			RenderHelper.drawRect(data.x - 5, data.y - 5, data.x + 5, data.y + 5, TextureLoader.getDefaultLoader().getTexture("Shield.png"));
		}

		GL11.glPopMatrix();
	}
}
