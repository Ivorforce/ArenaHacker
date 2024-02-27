package game;

import java.awt.Dimension;
import java.util.ArrayList;

import org.newdawn.slick.Color;
import org.newdawn.slick.Font;
import org.newdawn.slick.geom.Rectangle;

public class GuiScreenLoading extends GuiScreen
{
	public ArrayList<String> taskList;
	public ArrayList<String> descriptionsList;
	public int maxTaskMillis;

	public String title;
	public Font titleFont;
	public Font descriptionsFont;
	Color textColor;

	public int maxTasks;

	public GuiProgressBar progressBar;

	public GuiScreenLoading(String identifier, Dimension windowSize, GuiScreen parentScreen, int maxTaskMillis, boolean progressBarCanLoad)
	{
		super(identifier, windowSize, parentScreen);

		maxTaskMillis = 5;

		this.maxTaskMillis = maxTaskMillis;

		taskList = new ArrayList<String>();
		descriptionsList = new ArrayList<String>();

		addGuiElement(progressBar = new GuiProgressBar(new Rectangle(50, windowSize.height - 100, windowSize.width - 100, 20), 0, null, null, Color.blue, Color.red));
		progressBar.invisible = !progressBarCanLoad;
		progressBar.backgroundColor = Color.black;
		if (progressBarCanLoad) setupProgressBar();
	}

	public void addTaskListener(GuiScreenLoadingListener listener)
	{
		listeners.add(GuiScreenLoadingListener.class, listener);
	}

	public void removeTaskListener(GuiScreenLoadingListener listener)
	{
		listeners.remove(GuiScreenLoadingListener.class, listener);
	}

	public void removeTaskListener()
	{

	}

	@Override
	public void update()
	{
		super.update();

		doTasks();
	}

	@Override
	public void draw(int mouseX, int mouseY)
	{
		super.draw(mouseX, mouseY);

		if (title != null && titleFont != null)
		{
			titleFont.drawString(bounds.getCenterX() - titleFont.getWidth(title) / 2, bounds.getMinY() + 50, title, textColor);
		}
		if (descriptionsFont != null)
		{
			descriptionsFont.drawString(bounds.getCenterX() - descriptionsFont.getWidth(descriptionsList.get(0)) / 2, bounds.getMaxY() - 30, descriptionsList.get(0), textColor);
		}
	}

	public void doTasks()
	{
		long startTime = System.currentTimeMillis();

		while (taskList.size() > 0)
		{
			for (GuiScreenLoadingListener listener : listeners.getListeners(GuiScreenLoadingListener.class))
			{
				listener.doTask(taskList.get(0), this);
			}

			taskList.remove(0);
			descriptionsList.remove(0);

			progressBar.progress = (double) taskList.size() / (double) maxTasks;

			if (System.currentTimeMillis() - startTime > maxTaskMillis) break;
		}
	}

	public void addTasks(String[] tasks, String[] descriptions)
	{
		for (int i = 0; i < tasks.length; i++)
		{
			taskList.add(tasks[i]);
			descriptionsList.add(descriptions[i]);

			maxTasks++;
		}

		if (!progressBar.invisible) setupProgressBar();
	}

	public boolean hasTasksLeft()
	{
		return taskList.size() > 0;
	}

	public void setupProgressBar()
	{
		progressBar.segmentTexture = TextureLoader.getDefaultLoader().getTexture("BarSegment.png");
		progressBar.backgroundTexture = TextureLoader.getDefaultLoader().getTexture("BarBackground.png");
		progressBar.segments = maxTasks;

		progressBar.invisible = false;
	}
}
