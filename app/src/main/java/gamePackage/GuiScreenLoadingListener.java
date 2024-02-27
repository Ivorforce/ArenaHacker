package gamePackage;

import java.util.EventListener;

public interface GuiScreenLoadingListener extends EventListener
{
	public void doTask(String task, GuiScreenLoading loadingScreen);
}
