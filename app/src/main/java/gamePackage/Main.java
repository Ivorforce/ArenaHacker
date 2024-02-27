package gamePackage;

import static org.lwjgl.opengl.GL11.GL_BLEND;
import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_TEST;
import static org.lwjgl.opengl.GL11.GL_MODELVIEW;
import static org.lwjgl.opengl.GL11.GL_ONE_MINUS_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_PROJECTION;
import static org.lwjgl.opengl.GL11.GL_SMOOTH;
import static org.lwjgl.opengl.GL11.GL_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.glBlendFunc;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glLoadIdentity;
import static org.lwjgl.opengl.GL11.glMatrixMode;
import static org.lwjgl.opengl.GL11.glOrtho;
import static org.lwjgl.opengl.GL11.glShadeModel;
import static org.lwjgl.opengl.GL11.glViewport;

import java.awt.Dimension;
import java.io.File;
import java.util.Random;

import org.lwjgl.LWJGLException;
import org.lwjgl.LWJGLUtil;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.newdawn.slick.Color;
import org.newdawn.slick.Font;
import org.newdawn.slick.geom.Rectangle;

import paulscode.sound.SoundSystemConfig;
import paulscode.sound.codecs.CodecJOrbis;
import paulscode.sound.codecs.CodecWav;
import paulscode.sound.libraries.LibraryLWJGLOpenAL;

public class Main implements GuiScreenLoadingListener, GuiScreenListener, GameListener
{

	public Dimension windowSize = new Dimension(800, 800);

	private Game game;
	private GuiScreen currentGui;
	public Player player;

	public boolean gameRunning = false;

	private boolean escapeWasUsed;

	private static String version = "0.2";

	public static void main(String[] args)
	{
		Main main = new Main();
		main.execute();
	}

	public Main()
	{
		System.out.println("Game setting up...\n");

		LWJGLNativesHelper.copyOutsideAndLoad();

		gameRunning = true;

		try
		{
			Display.setDisplayMode(new DisplayMode(windowSize.width, windowSize.height));
			Display.setTitle("Dodgegame");
			Display.create();

			glShadeModel(GL_SMOOTH);

			glEnable(GL_TEXTURE_2D);
			glDisable(GL_DEPTH_TEST);
			glEnable(GL_BLEND);
			glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

			GL11.glClearColor(1, 1, 1, 1);
			
			glMatrixMode(GL_PROJECTION); // Set Matrix mode to projection
			glLoadIdentity(); // Resets current Matrix

			glOrtho(0, windowSize.width, windowSize.height, 0, -1, 1);
			glViewport(0, 0, windowSize.width, windowSize.height);
		}
		catch (LWJGLException e)
		{
			System.out.println("Error in initialization - Shutting down");
			e.printStackTrace();
			gameRunning = false;
			return;
		}

		GuiScreenLoading loadingScreen = new GuiScreenLoading("MainLoadingScreen", windowSize, null, 5, false);
		loadingScreen.addTaskListener(this);
		currentGui = loadingScreen;

		((GuiScreenLoading) currentGui).addTasks(new String[] { "", "", "LoadFonts", "LoadImages", "LoadSounds", "PlayMusic", "LoadMainMenu" }, new String[] { "", "", "Loading Fonts...", "Loading Images...", "Loading Sounds...", "Playing music...", "Loading Main Menu..." });

		player = new Player();
		player.loadSettings();
		player.settings.put("GameSpeed", 0.5);
	}

	public void execute()
	{
		while (gameRunning)
		{
			Display.sync(60); // FPS

			if (Display.isCloseRequested()) gameRunning = false;

			update();
			update();
			draw();

			Display.update();
		}

		player.saveSettings();
		
		System.out.println("Shutting down...");
		SoundManager.getDefaultManager().cleanup();
		Display.destroy();

		System.out.println("Game ended without errors.");
		System.exit(0);
	}

	public void update()
	{
		if (game != null && Keyboard.isKeyDown(Keyboard.KEY_ESCAPE) && !escapeWasUsed)
		{
			if (currentGui == null) currentGui = createMenu("PauseMenu");
			else currentGui = null;
		}
		else if (currentGui != null && Keyboard.isKeyDown(Keyboard.KEY_ESCAPE) && currentGui.parentScreen != null && !escapeWasUsed)
		{
			currentGui = currentGui.parentScreen;
		}
		if (Keyboard.isKeyDown(Keyboard.KEY_ESCAPE))
		{
			escapeWasUsed = true;
		}
		else
		{
			escapeWasUsed = false;
		}

		for (; Mouse.next();)
		{
			if (currentGui != null) currentGui.handleMouseInput();
			else if (game != null) game.handleMouseInput();

			if (Mouse.getEventButtonState())
			{
				if (Mouse.getEventButton() == 2 && game != null)
				{
					if (currentGui == null) currentGui = createMenu("PauseMenu");
					else currentGui = null;
				}
			}
		}
		for (; Keyboard.next();)
		{
			if (currentGui != null) currentGui.handleKeyboardInput();
			else if (game != null) game.handleKeyboardInput();
		}

		if (game != null && currentGui == null) game.update();
		if (currentGui != null)
		{
			currentGui.update();
		}
	}

	public void draw()
	{
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // Reset the screen
		glMatrixMode(GL_MODELVIEW); // Sets Matrix Mode to modelview
		glLoadIdentity(); // Resets current Matrix

		int shakingStrength = 0;
		boolean gamePaused = false;

		if (game != null && currentGui == null)
		{
			for (int i = 0; i < game.effects.size(); i++)
			{
				Effect effect = game.effects.get(i);

				if (effect.type.indexOf("Shake") == 0)
				{
					if (!gamePaused)
					{
						shakingStrength = Integer.valueOf(effect.type.substring(5));
					}
				}
				else if (effect.type.indexOf("Pause") == 0 || effect.type.indexOf("PauseSpecial") == 0)
				{
					gamePaused = true;
					shakingStrength = 0;
				}
			}
		}

		Random r = new Random();
		GL11.glTranslated(r.nextDouble() * shakingStrength * 2 - shakingStrength, r.nextDouble() * shakingStrength * 2 - shakingStrength, 0);

		if (currentGui != null && (currentGui.identifier.equals("MainMenu") || currentGui.identifier.equals("Options") || currentGui.identifier.equals("Help"))) RenderHelper.drawRect(0, 0, windowSize.width, windowSize.height, TextureLoader.getDefaultLoader().getTexture("MenuBackground.png"));

		if (game != null) game.draw();

		if (currentGui != null)
		{
			currentGui.draw(Mouse.getX(), windowSize.height - Mouse.getY());
		}
	}

	public GuiScreen createMenu(String name)
	{
		GuiScreen screen = null;

		Font standardFont = FontLoader.getDefaultLoader().getFont("Standard");
		//		UnicodeFont titleFont = FontLoader.getDefaultLoader().getFont("Title");

		TextureLoader tL = TextureLoader.getDefaultLoader();

		if (name.equals("MainMenu"))
		{
			screen = new GuiScreen("MainMenu", windowSize, null);

			GuiLabel versionLabel = new GuiLabel(new Rectangle(30, 80, 200, 30), new String[] { "Version " + version }, Color.black, standardFont);
			screen.addGuiElement(versionLabel);

			GuiButton logo = new GuiButton(new Rectangle(200, 150, 400, 80), -1, tL.getTexture("DodgegameLogo.png"), null, null, null, null);
			logo.enabled = false;
			screen.addGuiElement(logo);

			GuiLabel masterLabel = new GuiLabel(new Rectangle(275, 235, 240, 30), new String[] { "A game by Ivorius" }, Color.white, standardFont);
			screen.addGuiElement(masterLabel);

			GuiButton adventureModeButton = new GuiButton(new Rectangle(250, 300, 300, 40), 20, tL.getTexture("ButtonIdle.png"), tL.getTexture("ButtonHover.png"), "Adventure Mode", Color.white, standardFont);
			screen.addGuiElement(adventureModeButton);

			GuiButton actionModeButton = new GuiButton(new Rectangle(250, 350, 300, 40), 21, tL.getTexture("ButtonIdle.png"), tL.getTexture("ButtonHover.png"), "Action Mode", Color.white, standardFont);
			screen.addGuiElement(actionModeButton);

			GuiButton enduranceModeButton = new GuiButton(new Rectangle(250, 400, 300, 40), 22, tL.getTexture("ButtonIdle.png"), tL.getTexture("ButtonHover.png"), "Endurance Mode", Color.white, standardFont);
			screen.addGuiElement(enduranceModeButton);

			GuiButton survivalModeButton = new GuiButton(new Rectangle(250, 450, 300, 40), 23, tL.getTexture("ButtonIdle.png"), tL.getTexture("ButtonHover.png"), "Survival Mode", Color.white, standardFont);
			screen.addGuiElement(survivalModeButton);

			GuiButton chaseModeButton = new GuiButton(new Rectangle(250, 500, 300, 40), 24, tL.getTexture("ButtonIdle.png"), tL.getTexture("ButtonHover.png"), "Chase Mode", Color.white, standardFont);
			screen.addGuiElement(chaseModeButton);

			GuiButton optionsButton = new GuiButton(new Rectangle(250, 550, 300, 40), 1, tL.getTexture("ButtonIdle.png"), tL.getTexture("ButtonHover.png"), "Options", Color.white, standardFont);
			screen.addGuiElement(optionsButton);

			GuiButton helpButton = new GuiButton(new Rectangle(250, 600, 300, 40), 2, tL.getTexture("ButtonIdle.png"), tL.getTexture("ButtonHover.png"), "Help", Color.white, standardFont);
			screen.addGuiElement(helpButton);

			GuiButton exitButton = new GuiButton(new Rectangle(250, 650, 300, 40), 6, tL.getTexture("ButtonIdle.png"), tL.getTexture("ButtonHover.png"), "Exit", Color.white, standardFont);
			screen.addGuiElement(exitButton);
		}
		if (name.equals("Options"))
		{
			screen = new GuiScreen("Options", windowSize, currentGui);

			GuiLabel skinLabel = new GuiLabel(new Rectangle(250, 100, 300, 40), new String[] { "Skin" }, Color.white, standardFont);
			screen.addGuiElement(skinLabel);

			GuiButton skinStandardButton = new GuiButton(new Rectangle(250, 125, 90, 30), 30, tL.getTexture("ButtonIdle.png"), tL.getTexture("ButtonHover.png"), "Standard", Color.white, standardFont);
			screen.addGuiElement(skinStandardButton);

			GuiButton skinDoodleButton = new GuiButton(new Rectangle(355, 125, 90, 30), 31, tL.getTexture("ButtonIdle.png"), tL.getTexture("ButtonHover.png"), "Doodle", Color.white, standardFont);
			skinDoodleButton.enabled = true;
			screen.addGuiElement(skinDoodleButton);

			GuiButton skinGlowButton = new GuiButton(new Rectangle(460, 125, 90, 30), 32, tL.getTexture("ButtonIdle.png"), tL.getTexture("ButtonHover.png"), "Glow", Color.white, standardFont);
			skinGlowButton.enabled = true;
			screen.addGuiElement(skinGlowButton);

			GuiSlider volumeSlider = new GuiSlider(new Rectangle(250, 200, 300, 40), 7, tL.getTexture("ButtonIdle.png"), tL.getTexture("ButtonHover.png"));
			volumeSlider.sliderPosition = (Double) player.settings.get("MasterVolume");
			screen.addGuiElement(volumeSlider);

			GuiLabel masterLabel = new GuiLabel(new Rectangle(250, 235, 300, 40), new String[] { "Master Volume" }, Color.white, standardFont);
			screen.addGuiElement(masterLabel);

			GuiSlider musicVolumeSlider = new GuiSlider(new Rectangle(250, 280, 300, 40), 8, tL.getTexture("ButtonIdle.png"), tL.getTexture("ButtonHover.png"));
			musicVolumeSlider.sliderPosition = (Double) player.settings.get("MusicVolume");
			screen.addGuiElement(musicVolumeSlider);

			GuiLabel musicLabel = new GuiLabel(new Rectangle(250, 315, 300, 40), new String[] { "Music Volume" }, Color.white, standardFont);
			screen.addGuiElement(musicLabel);

			GuiCheckbox particlesEnabledCheckbox = new GuiCheckbox(new Rectangle(250, 370, 300, 20), 40, "Particles", Color.white, standardFont);
			particlesEnabledCheckbox.isOn = (Boolean) player.settings.get("ParticlesAllowed");
			screen.addGuiElement(particlesEnabledCheckbox);

			GuiCheckbox advancedParticlesEnabledCheckbox = new GuiCheckbox(new Rectangle(250, 400, 300, 20), 41, "Advanced Particles", Color.white, standardFont);
			advancedParticlesEnabledCheckbox.isOn = (Boolean) player.settings.get("AdvancedParticlesAllowed");
			screen.addGuiElement(advancedParticlesEnabledCheckbox);

			GuiCheckbox mousePlayerEnabledCheckbox = new GuiCheckbox(new Rectangle(250, 430, 300, 20), 42, "Mouse Player", Color.white, standardFont);
			mousePlayerEnabledCheckbox.isOn = (Boolean) player.settings.get("MousePlayerAllowed");;
			mousePlayerEnabledCheckbox.enabled = (Boolean) player.settings.get("KeyboardPlayerAllowed");
			screen.addGuiElement(mousePlayerEnabledCheckbox);

			GuiCheckbox keyboardPlayerEnabledCheckbox = new GuiCheckbox(new Rectangle(250, 460, 300, 20), 43, "Keyboard Player", Color.white, standardFont);
			keyboardPlayerEnabledCheckbox.isOn = (Boolean) player.settings.get("KeyboardPlayerAllowed");
			keyboardPlayerEnabledCheckbox.enabled = (Boolean) player.settings.get("MousePlayerAllowed");
			screen.addGuiElement(keyboardPlayerEnabledCheckbox);

			GuiButton resetButton = new GuiButton(new Rectangle(250, 500, 300, 40), 13, tL.getTexture("ButtonIdle.png"), tL.getTexture("ButtonHover.png"), "Reset Scores", Color.white, standardFont);
			screen.addGuiElement(resetButton);

			GuiButton backButton = new GuiButton(new Rectangle(250, 600, 300, 40), 3, tL.getTexture("ButtonIdle.png"), tL.getTexture("ButtonHover.png"), "Back", Color.white, standardFont);
			screen.addGuiElement(backButton);
		}
		if (name.equals("Help"))
		{
			screen = new GuiScreen("Help", windowSize, currentGui);

			String[] helpString = new String[] { "Controls", "Mouse (P1) / WASD (P2)", "Don't touch anything red", "Collect the blue dots" };
			GuiLabel label = new GuiLabel(new Rectangle(250, 150, 300, 200), helpString, Color.white, standardFont);
			screen.addGuiElement(label);

			GuiButton backButton = new GuiButton(new Rectangle(250, 400, 300, 40), 3, tL.getTexture("ButtonIdle.png"), tL.getTexture("ButtonHover.png"), "Back", Color.white, standardFont);
			screen.addGuiElement(backButton);
		}
		if (name.equals("GameOver"))
		{
			screen = new GuiScreen("GameOver", windowSize, currentGui);

			GuiButton restartButton = new GuiButton(new Rectangle(220, 200, 200, 30), 10, tL.getTexture("ButtonIdle.png"), tL.getTexture("ButtonHover.png"), "Restart Level", Color.white, standardFont);
			screen.addGuiElement(restartButton);

			GuiButton quitButton = new GuiButton(new Rectangle(220, 235, 200, 30), 11, tL.getTexture("ButtonIdle.png"), tL.getTexture("ButtonHover.png"), "Quit to map", Color.white, standardFont);
			screen.addGuiElement(quitButton);
		}
		if (name.equals("PauseMenu"))
		{
			screen = new GuiScreen("PauseMenu", windowSize, currentGui);

			GuiButton resumeButton = new GuiButton(new Rectangle(250, 300, 300, 40), 12, tL.getTexture("ButtonIdle.png"), tL.getTexture("ButtonHover.png"), "Resume", Color.white, standardFont);
			screen.addGuiElement(resumeButton);

			GuiButton restartButton = new GuiButton(new Rectangle(250, 350, 300, 40), 10, tL.getTexture("ButtonIdle.png"), tL.getTexture("ButtonHover.png"), "Restart Level", Color.white, standardFont);
			screen.addGuiElement(restartButton);

			GuiButton quitButton = new GuiButton(new Rectangle(250, 400, 300, 40), 11, tL.getTexture("ButtonIdle.png"), tL.getTexture("ButtonHover.png"), "Quit to Main Menu", Color.white, standardFont);
			screen.addGuiElement(quitButton);
		}
		if (name.equals("LoadingScreen"))
		{
			GuiScreenLoading loadingScreen = new GuiScreenLoading("MainLoadingScreen", windowSize, null, 5, false);
			loadingScreen.addTaskListener(this);

			screen = loadingScreen;
		}

		if (screen != null) screen.addListener(this);

		return screen;
	}

	public void startGame(String type)
	{
		game = new Game(type, windowSize, player);

		currentGui = null;
	}

	@Override
	public void elementClicked(GuiElement element)
	{
		if (element.identifier == 1)
		{
			currentGui = createMenu("Options");
		}
		else if (element.identifier == 2)
		{
			currentGui = createMenu("Help");
		}
		else if (element.identifier == 3)
		{
			if (currentGui.parentScreen != null) currentGui = currentGui.parentScreen;
		}
		else if (element.identifier == 4)
		{
			currentGui = null;

			game.cleanup();
		}
		else if (element.identifier == 5)
		{
			currentGui = createMenu("MainMenu");
		}
		else if (element.identifier == 6)
		{
			gameRunning = false;
		}
		else if (element.identifier == 7)
		{
			GuiSlider slider = (GuiSlider) element;

			SoundManager.getDefaultManager().setMasterVolume((float) slider.sliderPosition);
			player.settings.put("MasterVolume", (double) slider.sliderPosition);
		}
		else if (element.identifier == 8)
		{
			GuiSlider slider = (GuiSlider) element;

			SoundManager.getDefaultManager().setMusicVolume((float) slider.sliderPosition);
			player.settings.put("MusicVolume", (double) slider.sliderPosition);
		}
		else if (element.identifier == 10)
		{
			currentGui = null;

			if (game != null) game.cleanup();

			startGame(game.gameType);
		}
		else if (element.identifier == 11)
		{
			if (game != null) game.cleanup();
			game = null;

			currentGui = null;
			currentGui = createMenu("MainMenu");
		}
		else if (element.identifier == 12)
		{
			currentGui = null;
		}
		else if (element.identifier == 13)
		{
			player.resetSettings();

			TextureLoader.getDefaultLoader().releaseAllTextures();
			doTask("LoadImages", null);

			currentGui = createMenu("MainMenu");
		}
		else if (element.identifier == 20)
		{
			startGame("Adventure");
		}
		else if (element.identifier == 21)
		{
			startGame("Action");
		}
		else if (element.identifier == 22)
		{
			startGame("Endurance");
		}
		else if (element.identifier == 23)
		{
			startGame("Survival");
		}
		else if (element.identifier == 24)
		{
			startGame("Chase");
		}
		else if (element.identifier == 30)
		{
			player.settings.put("Skin", "Standard");
			TextureLoader.getDefaultLoader().releaseAllTextures();
			doTask("LoadFonts", null);
			doTask("LoadImages", null);

			currentGui = createMenu("MainMenu");
		}
		else if (element.identifier == 31)
		{
			player.settings.put("Skin", "Doodle");
			TextureLoader.getDefaultLoader().defaultPath = "images/Doodle";
			TextureLoader.getDefaultLoader().releaseAllTextures();
			doTask("LoadFonts", null);
			doTask("LoadImages", null);

			currentGui = createMenu("MainMenu");
		}
		else if (element.identifier == 32)
		{
			player.settings.put("Skin", "Glow");
			TextureLoader.getDefaultLoader().defaultPath = "images/Glow";
			TextureLoader.getDefaultLoader().releaseAllTextures();
			doTask("LoadFonts", null);
			doTask("LoadImages", null);

			currentGui = createMenu("MainMenu");
		}
		else if (element.identifier == 40)
		{
			player.settings.put("ParticlesAllowed", ((GuiCheckbox) element).isOn);
		}
		else if (element.identifier == 41)
		{
			player.settings.put("AdvancedParticlesAllowed", ((GuiCheckbox) element).isOn);
		}
		else if (element.identifier == 42)
		{
			player.settings.put("MousePlayerAllowed", ((GuiCheckbox) element).isOn);
			((GuiCheckbox)currentGui.getElementWithID(43)).enabled = (Boolean) player.settings.get("MousePlayerAllowed");
		}
		else if (element.identifier == 43)
		{
			player.settings.put("KeyboardPlayerAllowed", ((GuiCheckbox) element).isOn);
			((GuiCheckbox)currentGui.getElementWithID(42)).enabled = (Boolean) player.settings.get("KeyboardPlayerAllowed");
		}
	}

	@Override
	public void elementDraggedOver(GuiElement element)
	{
		if (element.identifier == 7)
		{
			GuiSlider slider = (GuiSlider) element;

			SoundManager.getDefaultManager().setMasterVolume((float) slider.sliderPosition);
			player.settings.put("MasterVolume", (double) slider.sliderPosition);
		}
		else if (element.identifier == 8)
		{
			GuiSlider slider = (GuiSlider) element;

			SoundManager.getDefaultManager().setMusicVolume((float) slider.sliderPosition);
			player.settings.put("MusicVolume", (double) slider.sliderPosition);
		}
	}

	@Override
	public void doTask(String task, GuiScreenLoading loadingScreen)
	{
		if (task.equals("LoadFonts"))
		{
			java.awt.Color color = java.awt.Color.black;
			if (((String) player.settings.get("Skin")).equals("Glow"))
			{
				color = java.awt.Color.white;
			}

			FontLoader.getDefaultLoader().registerFont(new java.awt.Font("Gungsuh", java.awt.Font.BOLD, 12), color, "Standard");
			FontLoader.getDefaultLoader().registerFont(new java.awt.Font("Gungsuh", java.awt.Font.BOLD, 12), color, "Console");
			FontLoader.getDefaultLoader().registerFont(new java.awt.Font("Gungsuh", java.awt.Font.BOLD, 15), color, "Title");
			FontLoader.getDefaultLoader().registerFont(new java.awt.Font("Gungsuh", java.awt.Font.BOLD, 40), color, "IngameGiant");

			if(loadingScreen != null)
			{
				loadingScreen.titleFont = FontLoader.getDefaultLoader().getFont("Title");
				loadingScreen.title = "Starting up Dodgegame";
				loadingScreen.textColor = Color.black;
				loadingScreen.descriptionsFont = FontLoader.getDefaultLoader().getFont("Standard");
			}
		}
		else if (task.equals("LoadImages"))
		{
			TextureLoader.setDefaultLoader(new TextureLoaderWithSkin());
			((TextureLoaderWithSkin) TextureLoader.getDefaultLoader()).skinPath = "images/" + (String) player.settings.get("Skin") + "/";
			((TextureLoaderWithSkin) TextureLoader.getDefaultLoader()).defaultSkinPath = "images/Standard/";

			TextureLoader.getDefaultLoader().getAnimatedTexture("FireA", ".png", 5);
			TextureLoader.getDefaultLoader().getAnimatedTexture("SmokeA", ".png", 12);

			if (loadingScreen != null) loadingScreen.setupProgressBar();
		}
		else if (task.equals("LoadSounds"))
		{
			SoundSystemConfig.setSoundFilesPackage("resources/sounds/");
			SoundManager.libraryClass = LibraryLWJGLOpenAL.class;
			SoundManager.addCodec(CodecWav.class, "wav");
			SoundManager.addCodec(CodecJOrbis.class, "ogg");
		}
		else if (task.equals("PlayMusic"))
		{
			SoundManager.getDefaultManager().setMusicVolume(0.5f);
			//playRandomMusic();
		}
		else if (task.equals("LoadMainMenu"))
		{
			currentGui = createMenu("MainMenu");

			System.out.println("\nFinished setting up Game!");
		}
	}

	@Override
	public void gameEnded(Game game, boolean playerWon, int score)
	{
		if (!playerWon)
		{
			currentGui = createMenu("GameOver");
		}
		else
		{
			//Add highscore

			game.cleanup();
			game = null;

			currentGui = null;
			currentGui = createMenu("MainMenu");
			currentGui = createMenu("CampaignMap");
		}
	}
}
