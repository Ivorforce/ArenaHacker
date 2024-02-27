package gamePackage;

import java.util.ArrayList;

import org.newdawn.slick.opengl.Texture;

public class AnimatedTexture
{

	public ArrayList<Texture> textures;
	public int currentFrame;

	public double imageSpeed;
	private int skippedTextures;

	public boolean repeats;

	public AnimatedTexture(ArrayList<Texture> textures)
	{
		this.textures = textures;

		imageSpeed = 1;

		repeats = true;
	}

	public AnimatedTexture(Texture[] textures)
	{
		this.textures = new ArrayList<Texture>();

		for (Texture aTexture : textures)
		{
			this.textures.add(aTexture);
		}

		imageSpeed = 1;

		repeats = true;
	}

	public void update()
	{
		currentFrame += (int) imageSpeed % 1;

		double leftSpeed = imageSpeed - ((int) imageSpeed % 1);

		skippedTextures++;

		if (1 / leftSpeed <= skippedTextures)
		{
			skippedTextures = 0;

			currentFrame++;
		}

		if (currentFrame >= textures.size())
		{
			if (repeats) currentFrame = 0;
			else currentFrame = textures.size() - 1;
		}
	}

	public Texture getCurrentTexture()
	{
		if (textures.size() != 0) { return textures.get(currentFrame); }

		return null;
	}
}
