package game;

import java.awt.Dimension;
import java.awt.Point;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.lwjgl.opengl.GL11;
import org.newdawn.slick.Color;
import org.newdawn.slick.Font;
import org.newdawn.slick.geom.Rectangle;

public class GuiCampaignMap extends GuiScreen
{

	public GuiButtonLevel selectedButton;

	public GuiButtonGameMode[] gameModeButtons;
	public GuiLabel levelNameLabel;
	public GuiButtonLevel[] levelButtons;

	public Save save;

	public GuiCampaignMap(String identifier, Dimension windowSize, GuiScreen parentScreen, Save save)
	{
		super(identifier, windowSize, parentScreen);

		this.save = save;

		gameModeButtons = new GuiButtonGameMode[4];

		GuiButton backButton = new GuiButton(new Rectangle(30, 30, 150, 30), 3, TextureLoader.getDefaultLoader().getTexture("ButtonIdle.png"), TextureLoader.getDefaultLoader().getTexture("ButtonHover.png"), "Back", Color.white, FontLoader.getDefaultLoader().getFont("Standard"));
		addGuiElement(backButton);

		String mapFile = null;

		try
		{
			mapFile = readFileAsString("models/" + save.mapName + ".map");
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}

		if (mapFile != null)
		{
			String[] levelStrings = mapFile.split("\\|-"); // Split the String by |-

			levelButtons = new GuiButtonLevel[levelStrings.length];

			for (int i = 0; i < levelStrings.length; i++)
			{
				String positionString = getValueFromLevel("MapPosition", levelStrings[i]);
				int posX = Integer.valueOf(positionString.substring(0, positionString.indexOf("|")));
				int posY = Integer.valueOf(positionString.substring(positionString.indexOf("|") + 1));
				Point position = new Point(posX, posY);

				GuiButtonLevel newLevel = new GuiButtonLevel(position, -1);

				newLevel.name = getValueFromLevel("Name", levelStrings[i]);

				newLevel.gameRadius = Integer.valueOf(getValueFromLevel("Radius", levelStrings[i]));

				String[] enemyTypeStrings = getValueFromLevel("Enemies", levelStrings[i]).split(",");
				ArrayList<EnemyType> enemyTypes = new ArrayList<EnemyType>();
				for (String type : enemyTypeStrings)
				{
					enemyTypes.add(new EnemyType(type.substring(0, type.indexOf("(")), Double.valueOf(type.substring(type.indexOf("(") + 1, type.indexOf(")"))), (int) (newLevel.gameRadius * 0.6)));
				}
				newLevel.enemySpawnRates = enemyTypes;

				newLevel.bossName = getValueFromLevel("Boss", levelStrings[i]);

				newLevel.timeLimit = Integer.valueOf(getValueFromLevel("TimeLimit", levelStrings[i]));

				guiElements.add(newLevel);
				levelButtons[i] = newLevel;
			}
		}
	}

	public void draw(int mouseX, int mouseY)
	{
		RenderHelper.drawRect(0, 0, bounds.getWidth(), bounds.getHeight(), TextureLoader.getDefaultLoader().getTexture("World.png"));

		for (GuiElement element : guiElements)
		{
			if (element instanceof GuiButtonLevel)
			{
				GuiButtonLevel button = (GuiButtonLevel) element;
				int levelDoneStatus = save.getHighestScore(button.name) > 0 ? 2 : 0;

				GL11.glColor3f(levelDoneStatus != 0 ? levelDoneStatus != 1 ? 1 : 0.5f : 0, levelDoneStatus != 2 ? 0.5f : 0, levelDoneStatus != 0 ? levelDoneStatus != 1 ? 0 : 0.5f : 1);
			}
			else GL11.glColor3f(1, 1, 1);

			element.draw(mouseX, mouseY);
		}

		if (selectedButton != null)
		{

		}
	}

	private String readFileAsString(String filePath) throws java.io.IOException
	{
		StringBuffer fileData = new StringBuffer(1000);
		BufferedReader reader = new BufferedReader(new InputStreamReader(this.getClass().getClassLoader().getResourceAsStream(filePath)));
		char[] buf = new char[1024];
		int numRead = 0;
		while ((numRead = reader.read(buf)) != -1)
		{
			String readData = String.valueOf(buf, 0, numRead);
			fileData.append(readData);
			buf = new char[1024];
		}
		reader.close();
		return fileData.toString();
	}

	private static String getValueFromLevel(String key, String levelString)
	{
		if (levelString.contains(key + ":"))
		{
			String stringPlus = levelString.substring(levelString.indexOf(key + ":") + key.length() + 1);
			String string = stringPlus.substring(0, stringPlus.indexOf(";"));

			return string;
		}

		return "";
	}

	@Override
	public void mouseHovered(int mouseX, int mouseY)
	{
		super.mouseHovered(mouseX, mouseY);

		if (gameModeButtons[0] == null)
		{
			for (GuiButtonLevel element : levelButtons)
			{
				if (element instanceof GuiButtonLevel && Point.distanceSq(element.bounds.getCenterX(), element.bounds.getCenterY(), mouseX, mouseY) < 5 * 5)
				{
					selectedButton = element;

					int w = 40;
					int h = 25;
					GuiButtonGameMode standardMode = new GuiButtonGameMode(new Rectangle(element.bounds.getCenterX() - w, element.bounds.getCenterY() - h * 2 - 20, w * 2, h * 2), 9, (GuiButtonLevel) element, "Standard");
					gameModeButtons[0] = standardMode;
					addGuiElement(standardMode);

					GuiButtonGameMode endlessMode = new GuiButtonGameMode(new Rectangle(element.bounds.getCenterX() - w, element.bounds.getCenterY() + 20, w * 2, h * 2), 9, (GuiButtonLevel) element, "Endless");
					gameModeButtons[1] = endlessMode;
					addGuiElement(endlessMode);

					Font font = FontLoader.getDefaultLoader().getFont("Standard");
					int nameWidth = font.getWidth(element.name);
					levelNameLabel = new GuiLabel(new Rectangle(element.bounds.getCenterX() - nameWidth / 2, element.bounds.getCenterY(), nameWidth, font.getHeight(element.name)), new String[] { element.name }, Color.white, font);
					addGuiElement(levelNameLabel);
				}
			}
		}
		else
		{
			if (Point.distanceSq(selectedButton.bounds.getCenterX(), selectedButton.bounds.getCenterY(), mouseX, mouseY) > 90 * 90)
			{
				selectedButton = null;

				for (int i = 0; i < gameModeButtons.length; i++)
				{
					if (gameModeButtons[i] != null) removeGuiElement(gameModeButtons[i]);
					gameModeButtons[i] = null;

					removeGuiElement(levelNameLabel);
					levelNameLabel = null;
				}
			}
		}
	}
}
