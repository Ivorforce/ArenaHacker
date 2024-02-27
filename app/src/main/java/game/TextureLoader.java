package game;

import java.io.IOException;
import java.util.Hashtable;

import org.newdawn.slick.opengl.Texture;

public class TextureLoader
{

	private static TextureLoader defaultLoader;

	private Hashtable<String, Texture> textures;

	public String defaultPath;

	public static TextureLoader getDefaultLoader()
	{
		if (defaultLoader == null) defaultLoader = new TextureLoader();

		return defaultLoader;
	}

	public TextureLoader()
	{
		textures = new Hashtable<String, Texture>();
	}

	public Texture getTexture(String textureName)
	{
		if (!textures.containsKey(textureName))
		{
			Texture texture = null;

			try
			{
				texture = org.newdawn.slick.opengl.TextureLoader.getTexture(textureName.substring(textureName.lastIndexOf(".") + 1), getClass().getClassLoader().getResourceAsStream(defaultPath + textureName));
			}
			catch (IOException e)
			{
				System.out.println("Error finding file:" + textureName);
			}

			if (texture != null)
			{
				textures.put(textureName, texture);
			}
			else
			{
				System.out.println("File not found:" + textureName);
				throw new RuntimeException();
			}
		}

		return textures.get(textureName);
	}

	public void releaseAllTextures()
	{
		for (Texture t : textures.values())
		{
			t.release();
		}

		textures.clear();
	}
}
