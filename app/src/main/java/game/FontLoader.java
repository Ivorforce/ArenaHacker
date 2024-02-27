package game;

import java.awt.Color;
import java.util.Hashtable;

import org.newdawn.slick.Font;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.UnicodeFont;
import org.newdawn.slick.font.effects.ColorEffect;

public class FontLoader
{

	private static FontLoader defaultLoader;

	private Hashtable<String, Font> fonts;

	public static FontLoader getDefaultLoader()
	{
		if (defaultLoader == null) defaultLoader = new FontLoader();

		return defaultLoader;
	}

	public FontLoader()
	{
		fonts = new Hashtable<String, Font>();
	}

	@SuppressWarnings("unchecked")
	public boolean registerFont(java.awt.Font font, Color color, String identifier)
	{
		UnicodeFont unicodeFont = new UnicodeFont(font); 

		unicodeFont.addGlyphs(32, 127);	 //Needed Unicode characters
		unicodeFont.getEffects().add(new ColorEffect(color));
		unicodeFont.setDisplayListCaching(true);

		try
		{
			unicodeFont.loadGlyphs();
		}
		catch (SlickException e)
		{
			return false;
		}

		fonts.put(identifier, unicodeFont);

		return true;
	}

	public Font getFont(String fontIdentifier)
	{
		if (!fonts.containsKey(fontIdentifier))
		{
			registerFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 12), Color.gray, fontIdentifier);
		}

		return fonts.get(fontIdentifier);
	}
}
