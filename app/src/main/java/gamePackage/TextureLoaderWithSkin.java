package gamePackage;

import org.newdawn.slick.opengl.Texture;

public class TextureLoaderWithSkin extends TextureLoader
{
	public String defaultSkinPath;
	public String skinPath;
	
	@Override
	public Texture getTexture(String textureName)
	{
		defaultPath = skinPath;
		Texture tex = super.getTexture(textureName);
		
		if(tex == null)
		{
			defaultPath = defaultSkinPath;
			tex = super.getTexture(textureName);
		}
		
		return tex;
	}
}
