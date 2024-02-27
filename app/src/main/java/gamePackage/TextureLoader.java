package gamePackage;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
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

	public static void setDefaultLoader(TextureLoader loader)
	{
		defaultLoader = loader;
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

			// TODO Fix this, i.e. don't set it here!
			String format = textureName.substring(textureName.lastIndexOf(".") + 1);
			InputStream stream = getClass().getClassLoader().getResourceAsStream(defaultPath + textureName);

			if (stream == null) {
				// May be an error but may also just be skin-specific miss
//				System.out.println("Texture not found: " + defaultPath + textureName);
				return null;
			}

			try
			{
//				stream = Files.newInputStream(Path.of("/Users/lukas/dev/Dodgegame/app/build/resources/main/images/Standard/" + textureName));
				texture = org.newdawn.slick.opengl.TextureLoader.getTexture(format, stream);
			}
			catch (Exception e)
			{
				System.out.println(e);
			}

			if (texture != null)
			{
				textures.put(textureName, texture);
			}
			else return null;
		}

		return textures.get(textureName);
	}

	public AnimatedTexture getAnimatedTexture(String textureName, String fileSuffix, int numberOfImages)
	{
		Texture[] tex = new Texture[numberOfImages];

		for (int i = 0; i < numberOfImages; i++)
		{
			tex[i] = getTexture(textureName + (i + 1) + fileSuffix);
		}
		AnimatedTexture t = new AnimatedTexture(tex);

		return t;
	}

	public AnimatedTexture getAnimatedTexture(String textureName)
	{
		AnimatedTexture t = new AnimatedTexture(new Texture[] { getTexture(textureName) });

		return t;
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
