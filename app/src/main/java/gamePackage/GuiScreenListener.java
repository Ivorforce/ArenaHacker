package gamePackage;

import java.util.EventListener;

public interface GuiScreenListener extends EventListener{

	public void elementClicked(GuiElement element);
	public void elementDraggedOver(GuiElement element);
}
