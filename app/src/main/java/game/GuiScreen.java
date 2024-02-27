package game;

import java.awt.Dimension;
import java.util.ArrayList;

import javax.swing.event.EventListenerList;

import org.lwjgl.input.Mouse;
import org.newdawn.slick.geom.Rectangle;

public class GuiScreen extends GuiElement{
	
	public GuiScreen parentScreen;
	
	protected EventListenerList listeners;
	
	public String identifier;
	public ArrayList<GuiElement> guiElements;
	public ArrayList<GuiElement> guiElementsToAdd;
	public ArrayList<GuiElement> guiElementsToRemove;
		
	protected GuiElement dragElement;
				
	public void addListener(GuiScreenListener listener)
	{
		listeners.add(GuiScreenListener.class, listener);
	}

	public void removeListener(GuiScreenListener listener)
	{
		listeners.remove(GuiScreenListener.class, listener);
	}
	
	public GuiScreen(String identifier, Dimension windowSize, GuiScreen parentScreen)
	{
		super(new Rectangle(0, 0, (float)windowSize.getWidth(), (float)windowSize.getHeight()));
		
		listeners = new EventListenerList();
		
		guiElements = new ArrayList<GuiElement>();
		guiElementsToAdd = new ArrayList<GuiElement>();
		guiElementsToRemove = new ArrayList<GuiElement>();
		
		this.identifier = identifier;
		this.parentScreen = parentScreen;
	}
	
	public void update()
	{
		guiElements.addAll(guiElementsToAdd);
		guiElementsToAdd.clear();
		guiElements.removeAll(guiElementsToRemove);
		guiElementsToRemove.clear();
		
		for(GuiElement element : guiElements) element.update();
	}

	public void handleMouseInput()
	{
		if(Mouse.getEventButtonState()) 
		{
			mouseClicked(Mouse.getX(), (int)bounds.getHeight() - Mouse.getY(), Mouse.getEventButton());
		}
		else if(Mouse.isButtonDown(0) && (Mouse.getEventDX() != 0 || Mouse.getEventDY() != 0))
		{
			mouseDragged(Mouse.getX(), (int)bounds.getHeight() - Mouse.getY(), 0);
		}
		else if(Mouse.getDX() != 0 || Mouse.getDY() != 0)
		{
			mouseHovered(Mouse.getX(), (int)bounds.getHeight() - Mouse.getY());
		}
		
		if(!Mouse.isButtonDown(0)) dragElement = null;
	}
	
	public void handleKeyboardInput()
	{
		
	}

	public void draw(int mouseX, int mouseY)
	{
		for(GuiElement element : guiElements) element.drawElement(mouseX, mouseY);
	}
	
	@Override
	public void mouseClicked(int mouseX, int mouseY, int button)
	{
		for(GuiElement element : guiElements)
		{
			if(element.bounds.contains(mouseX, mouseY)) 
			{
				elementClicked(mouseX, mouseY, button, element);
				dragElement = element;
			}
		}
	}
	
	public void elementClicked(int mouseX, int mouseY, int button, GuiElement element)
	{
		element.mouseClicked(mouseX, mouseY, button);
		
		for(GuiScreenListener listener : listeners.getListeners(GuiScreenListener.class))
		{
			listener.elementClicked(element);
		}
	}

	@Override
	public void mouseDragged(int mouseX, int mouseY, int button)
	{
		if(dragElement != null) elementDraggedOver(mouseX, mouseY, button, dragElement);
	}

	public void elementDraggedOver(int mouseX, int mouseY, int button, GuiElement element)
	{
		element.mouseDragged(mouseX, mouseY, button);
		
		for(GuiScreenListener listener : listeners.getListeners(GuiScreenListener.class))
		{
			listener.elementDraggedOver(element);
		}
	}
	
	@Override
	public void mouseHovered(int mouseX, int mouseY)
	{
		for(GuiElement element : guiElements)
		{
			if(bounds.contains(mouseX, mouseY)) 
			{
				element.mouseHovered(mouseX, mouseY);
			}
		}
	}
	
	public void addGuiElement(GuiElement element)
	{
		guiElementsToAdd.add(element);
		guiElementsToRemove.remove(element);
	}

	public void removeGuiElement(GuiElement element)
	{
		guiElementsToRemove.add(element);
		guiElementsToAdd.remove(element);
	}
}
