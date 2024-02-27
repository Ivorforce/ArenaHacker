package game;

import org.newdawn.slick.Color;
import org.newdawn.slick.geom.Rectangle;

public class GuiButtonGameMode extends GuiButton
{
	public GuiButtonLevel level;

	public String gameMode;

	public GuiButtonGameMode(Rectangle bounds, int buttonIndex, GuiButtonLevel level, String gameMode)
	{
		super(bounds, buttonIndex, TextureLoader.getDefaultLoader().getTexture("ButtonIdle.png"), TextureLoader.getDefaultLoader().getTexture("ButtonHover.png"), gameMode, Color.white, FontLoader.getDefaultLoader().getFont("Standard"));

		this.level = level;
		this.gameMode = gameMode;
	}

}
