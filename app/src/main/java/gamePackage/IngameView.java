package gamePackage;

import java.awt.Dimension;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Hashtable;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import org.newdawn.slick.Color;
import org.newdawn.slick.Font;
import org.newdawn.slick.opengl.Texture;

public class IngameView
{
	public int type;

	Dimension fieldSize;

	public ArrayList<String> usedElements;

	Player player;

	public String typedKeys;
	public Hashtable<String, Boolean> keyboard;

	public IngameView(int viewType, Dimension size, Player player)
	{
		type = viewType;

		fieldSize = size;

		usedElements = new ArrayList<String>();
		typedKeys = "";

		this.player = player;

		keyboard = new Hashtable<String, Boolean>();
		keyboard.put("upKey", false);
		keyboard.put("downKey", false);
		keyboard.put("leftKey", false);
		keyboard.put("rightKey", false);
	}

	public void draw(Game game)
	{
		GL11.glColor3f(1, 1, 1);

		Font font = FontLoader.getDefaultLoader().getFont("Console");

		if (type == 0)
		{
			if (player.lives > 5000)
			{
				font.drawString(25, 20, "Chuck Noris-like");
				RenderHelper.drawRect(10, 21, 22, 33, TextureLoader.getDefaultLoader().getTexture("Heart.png"));
			}
			else if (player.lives > 500)
			{
				font.drawString(25, 20, "Too many");
				RenderHelper.drawRect(10, 21, 22, 33, TextureLoader.getDefaultLoader().getTexture("Heart.png"));
			}
			else if (player.lives < 6)
			{
				for (int i = 0; i < game.player.lives; i++)
				{
					RenderHelper.drawRect(10 + i * 15, 21, 10 + i * 15 + 12, 33, TextureLoader.getDefaultLoader().getTexture("Heart.png"));
				}
			}
			else
			{
				font.drawString(25, 20, "" + player.lives);
				RenderHelper.drawRect(10, 21, 22, 33, TextureLoader.getDefaultLoader().getTexture("Heart.png"));
			}

			font.drawString(10, 40, "Score: " + game.player.score, (game.player.score < 0 || game.rules.noScoreMode) ? Color.red : Color.blue);
			font.drawString(10, 60, "Highscore: " + game.player.getHighScore(game.rules.highScoreSlot), (game.player.score < game.player.getHighScore(game.rules.highScoreSlot) || game.rules.noScoreMode) ? Color.blue : Color.gray);

			String gameType = "Game Type:" + game.rules.title;

			int stringWidth = font.getWidth(gameType);

			GL11.glColor3f(0.3f, 0.3f, 0.3f);
			font.drawString((fieldSize.width - stringWidth) / 2, 20, gameType);
		}
		else if (type == 1)
		{
			String text = "Click or Space to Start!";
			GL11.glColor3f(1, 0, 0);

			int stringWidth = font.getWidth(text);

			font.drawString((fieldSize.width - stringWidth) / 2, (int) (fieldSize.height * 0.4), text);
		}
		else if (type == 2)
		{
			int r = 30;

			Texture fireTexture = null;
			Texture waterTexture = null;
			Texture airTexture = null;
			Texture earthTexture = null;

			int selected = !Keyboard.isKeyDown(Keyboard.KEY_W) ? !Keyboard.isKeyDown(Keyboard.KEY_S) ? !Keyboard.isKeyDown(Keyboard.KEY_A) ? !Keyboard.isKeyDown(Keyboard.KEY_D) ? -1 : 3 : 2 : 1 : 0;

			//Up (Fire)

			if (selected == 0 || game.getCursorPosition().distanceSq(fieldSize.width / 2, fieldSize.height / 2 - 100) < (r * r)) fireTexture = TextureLoader.getDefaultLoader().getTexture("FireActive.png");
			else fireTexture = TextureLoader.getDefaultLoader().getTexture("Fire.png");

			RenderHelper.drawRect(fieldSize.width / 2 - r, fieldSize.height / 2 - 100 - r, fieldSize.width / 2 + r, fieldSize.height / 2 - 100 + r, fireTexture);

			String fireString = "" + Game.countNumberOfItems("Fire", game.player.items);
			int stringWidthFire = font.getWidth(fireString);

			font.drawString((fieldSize.width - stringWidthFire) / 2, fieldSize.height / 2 - 100, fireString);

			//Down (Water)

			if (selected == 1 || game.getCursorPosition().distanceSq(fieldSize.width / 2, fieldSize.height / 2 + 100) < (r * r)) waterTexture = TextureLoader.getDefaultLoader().getTexture("WaterActive.png");
			else waterTexture = TextureLoader.getDefaultLoader().getTexture("Water.png");

			RenderHelper.drawRect(fieldSize.width / 2 - r, fieldSize.height / 2 + 100 - r, fieldSize.width / 2 + r, fieldSize.height / 2 + 100 + r, waterTexture);

			String waterString = "" + Game.countNumberOfItems("Water", game.player.items);
			int stringWidthWater = font.getWidth(fireString);

			font.drawString((fieldSize.width - stringWidthWater) / 2, fieldSize.height / 2 + 100, waterString);

			//Left (Air)

			if (selected == 2 || game.getCursorPosition().distanceSq(fieldSize.width / 2 - 100, fieldSize.height / 2) < (r * r)) airTexture = TextureLoader.getDefaultLoader().getTexture("AirActive.png");
			else airTexture = TextureLoader.getDefaultLoader().getTexture("Air.png");

			RenderHelper.drawRect(fieldSize.width / 2 - r - 100, fieldSize.height / 2 - r, fieldSize.width / 2 + r - 100, fieldSize.height / 2 + r, airTexture);

			String airString = "" + Game.countNumberOfItems("Air", game.player.items);
			int stringWidthAir = font.getWidth(airString);

			font.drawString((fieldSize.width - stringWidthAir) / 2 - 100, fieldSize.height / 2 - 10, airString);

			//Right (Earth)

			if (selected == 3 || game.getCursorPosition().distanceSq(fieldSize.width / 2 + 100, fieldSize.height / 2) < (r * r)) earthTexture = TextureLoader.getDefaultLoader().getTexture("EarthActive.png");
			else earthTexture = TextureLoader.getDefaultLoader().getTexture("Earth.png");

			RenderHelper.drawRect(fieldSize.width / 2 - r + 100, fieldSize.height / 2 - r, fieldSize.width / 2 + r + 100, fieldSize.height / 2 + r, earthTexture);

			String earthString = "" + Game.countNumberOfItems("Earth", game.player.items);
			int stringWidthEarth = font.getWidth(earthString);

			font.drawString((fieldSize.width - stringWidthEarth) / 2 + 100, fieldSize.height / 2, earthString);

			//Middle (Result)

			Texture resultTexture = TextureLoader.getDefaultLoader().getTexture("ElementsResult.png");

			RenderHelper.drawRect(fieldSize.width / 2 - 50, fieldSize.height / 2 - 50, fieldSize.width / 2 + 50, fieldSize.height / 2 + 50, resultTexture);

			for (int i = 0; i < usedElements.size(); i++)
			{
				GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
				if (usedElements.get(i).matches("Fire")) GL11.glColor3f(1, 0, 0);
				else if (usedElements.get(i).matches("Water")) GL11.glColor3f(0, 0, 1);
				else if (usedElements.get(i).matches("Air")) GL11.glColor3f(0.4f, 0.8f, 0.7f);
				else if (usedElements.get(i).matches("Earth")) GL11.glColor3f(0.8f, 0.6f, 0);

				int radius = 10;

				if (i == 0) RenderHelper.drawRect(fieldSize.width / 2 - radius, fieldSize.height / 2 - radius - 50, fieldSize.width / 2 + radius, fieldSize.height / 2 + radius - 50, null);
				else if (i == 1) RenderHelper.drawRect(fieldSize.width / 2 - radius + 50, fieldSize.height / 2 - radius, fieldSize.width / 2 + radius + 50, fieldSize.height / 2 + radius, null);
				else if (i == 2) RenderHelper.drawRect(fieldSize.width / 2 - radius, fieldSize.height / 2 - radius + 50, fieldSize.width / 2 + radius, fieldSize.height / 2 + radius + 50, null);
				else if (i == 3) RenderHelper.drawRect(fieldSize.width / 2 - radius - 50, fieldSize.height / 2 - radius, fieldSize.width / 2 + radius - 50, fieldSize.height / 2 + radius, null);
				else if (i == 4) RenderHelper.drawRect(fieldSize.width / 2 - radius, fieldSize.height / 2 - radius, fieldSize.width / 2 + radius, fieldSize.height / 2 + radius, null);
			}
		}
	}

	public void handleMouseInput(double mouseX, double mouseY)
	{
		if (Mouse.getEventButtonState() && Mouse.getEventButton() == 0)
		{
			if (type == 2 && usedElements.size() < 5)
			{
				for (int n = 0; n < 4; n++)
				{
					double xPlus = n != 2 ? n != 3 ? 0 : 100 : -100;
					double yPlus = n != 0 ? n != 1 ? 0 : 100 : -100;
					String element = n != 0 ? n != 1 ? n != 2 ? "Earth" : "Air" : "Water" : "Fire";

					int r = 30;

					double distance = Point.distanceSq(mouseX, mouseY, fieldSize.width / 2 + xPlus, fieldSize.height / 2 + yPlus);

					if (distance < (r * r) && distance > -(r * r))
					{
						for (int i = 0; i < player.items.size(); i++)
						{
							if (player.items.get(i).equalsIgnoreCase(element))
							{
								usedElements.add(element);

								player.items.remove(i);

								break;
							}
						}
					}
				}
			}
			if (type == 2)
			{
				if (Point.distanceSq(mouseX, mouseY, fieldSize.width / 2, fieldSize.height / 2) < 50 * 50)
				{
					if (usedElements.size() > 0)
					{
						player.items.add(usedElements.get(usedElements.size() - 1));
						usedElements.remove(usedElements.size() - 1);
					}
				}
			}
		}
	}

	public void handleKeyboardInput()
	{
		if (Keyboard.getEventKeyState() && Keyboard.getEventKey() == Keyboard.KEY_E && type == 2)
		{
			String element = !Keyboard.isKeyDown(Keyboard.KEY_W) ? !Keyboard.isKeyDown(Keyboard.KEY_S) ? !Keyboard.isKeyDown(Keyboard.KEY_A) ? !Keyboard.isKeyDown(Keyboard.KEY_D) ? null : "Earth" : "Air" : "Water" : "Fire";

			if (element != null)
			{
				for (int i = 0; i < player.items.size(); i++)
				{
					if (player.items.get(i).equalsIgnoreCase(element))
					{
						usedElements.add(element);

						player.items.remove(i);

						break;
					}
				}
			}
		}
	}
}
