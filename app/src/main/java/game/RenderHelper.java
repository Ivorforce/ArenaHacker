package game;

import static org.lwjgl.opengl.GL11.*;

import org.newdawn.slick.opengl.Texture;

public abstract class RenderHelper {

	public static void drawRect(double x1, double y1, double x2, double y2, Texture texture)
	{
		if(texture != null) glBindTexture(GL_TEXTURE_2D, texture.getTextureID());
				
		glBegin(GL_QUADS);
		
		if(texture != null) glTexCoord2f(0, 0);
		glVertex2d(x1, y1);
		if(texture != null) glTexCoord2f(texture.getWidth(), 0);
		glVertex2d(x2, y1);
		if(texture != null) glTexCoord2f(texture.getWidth(), texture.getHeight());
		glVertex2d(x2, y2);
		if(texture != null) glTexCoord2f(0, texture.getHeight());
		glVertex2d(x1, y2);
		
		glEnd();
	}

	public static void drawLine(double x1, double y1, double x2, double y2, double width, double length, Texture texture)
	{
		if(length != 0)
		{
			if(texture != null) glBindTexture(GL_TEXTURE_2D, texture.getTextureID());

			double sideX = -(double)(((y2 - y1) / length) * width / 2);
			double sideY =  (double)(((x2 - x1) / length) * width / 2);
			
			glBegin(GL_QUADS);
			
			if(texture != null) glTexCoord2f(0, 0);
			glVertex2d(x1 - sideX, y1 - sideY);
			if(texture != null) glTexCoord2f(texture.getWidth(), 0);
			glVertex2d(x1 + sideX, y1 + sideY);
			if(texture != null) glTexCoord2f(texture.getWidth(), texture.getHeight());
			glVertex2d(x2 + sideX, y2 + sideY);
			if(texture != null) glTexCoord2f(0, texture.getHeight());
			glVertex2d(x2 - sideX, y2 - sideY);
			
			glEnd();
		}
	}	
}
