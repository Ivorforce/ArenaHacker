package game;

import org.newdawn.slick.Font;

public abstract class Console
{

	public static void draw(Game game)
	{
		Font drawerFont = FontLoader.getDefaultLoader().getFont("Console");

		drawerFont.drawString(5, 10, game.levelName);
		drawerFont.drawString(5, 25, "T_" + game.ticks);
		drawerFont.drawString(5, 40, "Pos_" + (float) game.player.x + "~" + (float) game.player.y);
		if (game.laserHitPoint != null) drawerFont.drawString(5, 55, "L_" + (float) (game.player.x - game.laserHitPoint.x) + "~" + (float) (game.player.y - game.laserHitPoint.y));
		else drawerFont.drawString(5, 55, "L_NULL~NULL");
		if (game.boss == null) drawerFont.drawString(5, 70, "E_" + game.enemies.size() + "+" + game.enemySpawns.size());
		else drawerFont.drawString(5, 70, "E_" + game.boss.name);
		drawerFont.drawString(5, 85, "S_" + game.score);
		drawerFont.drawString(5, 100, "H_" + (float) (game.player.heat * 100) + "%");
	}
}
