package game;

import java.util.EventListener;

public interface GameListener extends EventListener
{
	public void gameEnded(GameEvent e);
}
