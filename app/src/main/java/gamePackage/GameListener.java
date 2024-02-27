package gamePackage;

import java.util.EventListener;

public interface GameListener extends EventListener
{
	public void gameEnded(Game game, boolean playerWon, int score);
}
