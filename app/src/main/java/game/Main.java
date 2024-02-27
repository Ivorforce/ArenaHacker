package game;

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
import java.util.Hashtable;
import java.util.Random;

import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.newdawn.slick.Color;
import org.newdawn.slick.Font;
import org.newdawn.slick.geom.Rectangle;

import paulscode.sound.SoundSystemConfig;
import paulscode.sound.codecs.CodecJOrbis;
import paulscode.sound.codecs.CodecWav;
import paulscode.sound.libraries.LibraryLWJGLOpenAL;

/*
 * Design
 * 
 * Plot:
 * Every state builds Doomsday computers
 * All the computer of the are only clients, there is one core processor
 * The more Doomsday computers are destroyed, the harder the others get
 * Hack special computer parts (graphics card etc.) to get advantages, the parts are bosses and have emotions
 * They have to stay hidden and thus continue doing their work, but also support you as much as possible
 * 
 * 
 * Accessing servers via Network: only Bullets as enemies (Classes are not transferable!)
 * Cable: Level with progression, you have to go through a pre-made level
 * Hacking a Computer (-part): Stay in an arena
 * 
 * Multiplayer -> Can have any number of players
 * Any player can have subplayers (Small blade, with some small abilities, has to move into enemies to damage them)
 * 
 * Bullets = Data types
 * 
 * Byte -> Can only go straight in any direction
 * Boolean -> Can only go left or right
 * Integer -> Goes straight in any direction, with different speeds
 * Float -> Bullets that can stay incredibly slow for a long time (bullet hell style)
 * Enumeration -> Bullets with special movement behaviour
 * String -> Laser
 * 
 * Array = Enemy spawner with limited enemies, can be hacked to kill all remaining enemies
 * Array Class = Enemy containing bullets, they bounce around in it and are let free once the array class is killed
 * 
 * Enemies = Classes (Can spawn bullets)
 * 
 * Class references -> Classes are invincible, but will be destroyed if the class owning the first one is destroyed
 * Allocator -> Creates enemies until killed, can be hacked to spawn units for the player (have to be unlocked)
 * 
 * 
 * Bosses = Programs, have personality, become friends after hacking
 * 
 */

public class Main implements GameListener, GuiScreenListener, GuiScreenLoadingListener
{

	public Dimension size = new Dimension(640, 480);

	private boolean gameRunning;
	private Save activeSave;
	private GuiButtonGameMode lastGameButtonUsed;

	private Game game;
	private GuiScreen currentGui;

	private ParticleSystem particleSystem;

	private boolean escapeWasUsed;

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
			Display.setDisplayMode(new DisplayMode(size.width, size.height));
			Display.setTitle("Arena Hacker");
			Display.create();

			glShadeModel(GL_SMOOTH);

			glEnable(GL_TEXTURE_2D);
			glDisable(GL_DEPTH_TEST);
			glEnable(GL_BLEND);
			glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

			glMatrixMode(GL_PROJECTION); // Set Matrix mode to projection
			glLoadIdentity(); // Resets current Matrix

			glOrtho(0, size.width, size.height, 0, -1, 1);
			glViewport(0, 0, size.width, size.height);
		}
		catch (LWJGLException e)
		{
			System.out.println("Error in initialization - Shutting down");
			e.printStackTrace();
			gameRunning = false;
			return;
		}

		GuiScreenLoading loadingScreen = new GuiScreenLoading("MainLoadingScreen", size, null, 5, false);
		loadingScreen.addTaskListener(this);
		currentGui = loadingScreen;
		
		((GuiScreenLoading) currentGui).addTasks(new String[] { "", "", "LoadFonts", "LoadImages", "LoadSounds", "PlayMusic", "LoadMainMenu" }, new String[] { "", "", "Loading Fonts...", "Loading Images...", "Loading Sounds...", "Playing music...", "Loading Main Menu..." });
	}

	public void execute()
	{
		while (gameRunning)
		{
			Display.sync(60); // FPS

			glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // Reset the screen
			glMatrixMode(GL_MODELVIEW); // Sets Matrix Mode to modelview
			glLoadIdentity(); // Resets current Matrix

			if (Display.isCloseRequested()) gameRunning = false;

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
			}
			for (; Keyboard.next();)
			{
				if (currentGui != null) currentGui.handleKeyboardInput();
			}

			if (game != null && currentGui == null) game.update();
			if (currentGui != null)
			{
				if (currentGui.identifier.equals("MainMenu") || currentGui.identifier.equals("Options") || currentGui.identifier.equals("Help"))
				{
					Random gen = new Random();
					particleSystem.add(new Particle2D(gen.nextInt(size.width), 0, 0, 15 + gen.nextDouble() * 4 - 2.0, Particle2D.standard, TextureLoader.getDefaultLoader().getTexture("Particle.png"), Color.blue));
					if (gen.nextInt(100) == 0) particleSystem.add(new Particle2D(gen.nextInt(size.width), 0, 0, 15 + gen.nextDouble() * 4 - 2, Particle2D.standard, TextureLoader.getDefaultLoader().getTexture("Particle.png"), Color.red));
					particleSystem.moveParticles();
				}

				currentGui.update();
			}

			if (game != null) game.draw();

			if (currentGui != null)
			{
				if (currentGui.identifier.equals("MainMenu") || currentGui.identifier.equals("Options") || currentGui.identifier.equals("Help"))
				{
					particleSystem.drawParticles();
				}

				currentGui.draw(Mouse.getX(), size.height - Mouse.getY());
			}

			Display.update();
		}

		System.out.println("Shutting down...");
		SoundManager.getDefaultManager().cleanup();
		Display.destroy();

		System.out.println("Game ended without errors.");
		System.exit(0);
	}

	public void startLevel(GuiButtonGameMode gameModeButton)
	{
		playRandomMusic();

		currentGui = null;

		int levelTime = 0;
		if (gameModeButton.gameMode.equals("Standard"))
		{
			levelTime = gameModeButton.level.timeLimit * 60;
		}
		else if (gameModeButton.gameMode.equals("Endless")) levelTime = -1;

		if (game != null) game.cleanup();
		game = new Game(gameModeButton.level.gameRadius, gameModeButton.level.enemySpawnRates, gameModeButton.level.name, gameModeButton.level.bossName, levelTime, size);
		game.addListener(this);
	}

	@Override
	public void gameEnded(GameEvent e)
	{
		if (lastGameButtonUsed.gameMode.equals("Endless"))
		{
			currentGui = createMenu("GameOver");

			//			activeSave.addHighscore(e.getLevelName(), e.getScore());
		}
		else if (!e.hasPlayerWon())
		{
			currentGui = createMenu("GameOver");
		}
		else
		{
			activeSave.addHighscore(e.getLevelName(), e.getScore());

			game.cleanup();
			game = null;

			currentGui = null;
			currentGui = createMenu("MainMenu");
			currentGui = createMenu("CampaignMap");
		}
	}

	@Override
	public void elementClicked(GuiElement element)
	{
		if (element instanceof GuiButton)
		{
			GuiButton button = (GuiButton) element;

			if (button.buttonIndex == 0)
			{
				activeSave = new Save("Campaign", new Hashtable<String, Integer>());

				currentGui = createMenu("CampaignMap");
			}
			else if (button.buttonIndex == 1)
			{
				currentGui = createMenu("Options");
			}
			else if (button.buttonIndex == 2)
			{
				currentGui = createMenu("Help");
			}
			else if (button.buttonIndex == 3)
			{
				if (currentGui.parentScreen != null) currentGui = currentGui.parentScreen;
			}
			else if (button.buttonIndex == 4)
			{
				currentGui = null;

				game.cleanup();
			}
			else if (button.buttonIndex == 5)
			{
				currentGui = createMenu("CampaignMap");
			}
			else if (button.buttonIndex == 6)
			{
				gameRunning = false;
			}
			else if (button.buttonIndex == 7)
			{
				GuiSlider slider = (GuiSlider) element;

				SoundManager.getDefaultManager().setMasterVolume((float) slider.sliderPosition);
			}
			else if (button.buttonIndex == 8)
			{
				GuiSlider slider = (GuiSlider) element;

				SoundManager.getDefaultManager().setMusicVolume((float) slider.sliderPosition);
			}
			else if (button.buttonIndex == 9)
			{
				lastGameButtonUsed = (GuiButtonGameMode) button;
				startLevel(lastGameButtonUsed);
			}
			else if (button.buttonIndex == 10)
			{
				currentGui = null;

				startLevel(lastGameButtonUsed);
			}
			else if (button.buttonIndex == 11)
			{
				if (game != null) game.cleanup();
				game = null;

				currentGui = null;
				currentGui = createMenu("MainMenu");
				currentGui = createMenu("CampaignMap");
			}
			else if (button.buttonIndex == 12)
			{
				currentGui = null;
			}
		}
	}

	@Override
	public void elementDraggedOver(GuiElement element)
	{
		if (element instanceof GuiButton)
		{
			GuiButton button = (GuiButton) element;

			if (button.buttonIndex == 7)
			{
				GuiSlider slider = (GuiSlider) element;

				SoundManager.getDefaultManager().setMasterVolume((float) slider.sliderPosition);
			}
			else if (button.buttonIndex == 8)
			{
				GuiSlider slider = (GuiSlider) element;

				SoundManager.getDefaultManager().setMusicVolume((float) slider.sliderPosition);
			}
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
			screen = new GuiScreen("MainMenu", size, null);

			GuiButton logo = new GuiButton(new Rectangle(100, 20, 440, 109), -1, tL.getTexture("Logo.png"), null, null, null, null);
			logo.enabled = false;
			screen.addGuiElement(logo);

			GuiLabel masterLabel = new GuiLabel(new Rectangle(200, 120, 240, 30), new String[] { "A game by ivorius" }, Color.white, standardFont);
			screen.addGuiElement(masterLabel);

			GuiButton startGameButton = new GuiButton(new Rectangle(200, 200, 240, 30), 0, tL.getTexture("ButtonIdle.png"), tL.getTexture("ButtonHover.png"), "Start game", Color.white, standardFont);
			screen.addGuiElement(startGameButton);

			GuiButton optionsButton = new GuiButton(new Rectangle(200, 235, 240, 30), 1, tL.getTexture("ButtonIdle.png"), tL.getTexture("ButtonHover.png"), "Options", Color.white, standardFont);
			screen.addGuiElement(optionsButton);

			GuiButton helpButton = new GuiButton(new Rectangle(200, 270, 240, 30), 2, tL.getTexture("ButtonIdle.png"), tL.getTexture("ButtonHover.png"), "Help", Color.white, standardFont);
			screen.addGuiElement(helpButton);

			GuiButton exitButton = new GuiButton(new Rectangle(200, 305, 240, 30), 6, tL.getTexture("ButtonIdle.png"), tL.getTexture("ButtonHover.png"), "Exit", Color.white, standardFont);
			screen.addGuiElement(exitButton);
		}
		if (name.equals("Options"))
		{
			screen = new GuiScreen("Options", size, currentGui);

			GuiSlider volumeSlider = new GuiSlider(new Rectangle(200, 150, 240, 30), 7, tL.getTexture("ButtonIdle.png"), tL.getTexture("ButtonHover.png"));
			volumeSlider.sliderPosition = SoundManager.getDefaultManager().getMasterVolume();
			screen.addGuiElement(volumeSlider);

			GuiLabel masterLabel = new GuiLabel(new Rectangle(200, 175, 240, 30), new String[] { "Master Volume" }, Color.white, standardFont);
			screen.addGuiElement(masterLabel);

			GuiSlider musicVolumeSlider = new GuiSlider(new Rectangle(200, 230, 240, 30), 8, tL.getTexture("ButtonIdle.png"), tL.getTexture("ButtonHover.png"));
			musicVolumeSlider.sliderPosition = SoundManager.getDefaultManager().getMusicVolume();
			screen.addGuiElement(musicVolumeSlider);

			GuiLabel musicLabel = new GuiLabel(new Rectangle(200, 255, 240, 30), new String[] { "Music Volume" }, Color.white, standardFont);
			screen.addGuiElement(musicLabel);

			GuiButton backButton = new GuiButton(new Rectangle(200, 305, 240, 30), 3, tL.getTexture("ButtonIdle.png"), tL.getTexture("ButtonHover.png"), "Back", Color.white, standardFont);
			screen.addGuiElement(backButton);
		}
		if (name.equals("Help"))
		{
			screen = new GuiScreen("Help", size, currentGui);

			String[] helpString = new String[] { "Controls", "WASD to move", "Shift / Space to slow downt time", "Left mouse button for laser", "Right mouse button to move slowly" };
			GuiLabel label = new GuiLabel(new Rectangle(0, 70, 640, 200), helpString, Color.white, standardFont);
			screen.addGuiElement(label);

			GuiButton backButton = new GuiButton(new Rectangle(200, 305, 240, 30), 3, tL.getTexture("ButtonIdle.png"), tL.getTexture("ButtonHover.png"), "Back", Color.white, standardFont);
			screen.addGuiElement(backButton);
		}
		if (name.equals("CampaignMap"))
		{
			GuiCampaignMap map = new GuiCampaignMap("CampaignMap", size, currentGui, activeSave);
			screen = map;
		}
		if (name.equals("GameOver"))
		{
			screen = new GuiScreen("GameOver", size, currentGui);

			GuiButton restartButton = new GuiButton(new Rectangle(220, 200, 200, 30), 10, tL.getTexture("ButtonIdle.png"), tL.getTexture("ButtonHover.png"), "Restart Level", Color.white, standardFont);
			screen.addGuiElement(restartButton);

			GuiButton quitButton = new GuiButton(new Rectangle(220, 235, 200, 30), 11, tL.getTexture("ButtonIdle.png"), tL.getTexture("ButtonHover.png"), "Quit to map", Color.white, standardFont);
			screen.addGuiElement(quitButton);
		}
		if (name.equals("PauseMenu"))
		{
			screen = new GuiScreen("PauseMenu", size, currentGui);

			GuiButton resumeButton = new GuiButton(new Rectangle(220, 165, 200, 30), 12, tL.getTexture("ButtonIdle.png"), tL.getTexture("ButtonHover.png"), "Resume", Color.white, standardFont);
			screen.addGuiElement(resumeButton);

			GuiButton restartButton = new GuiButton(new Rectangle(220, 200, 200, 30), 10, tL.getTexture("ButtonIdle.png"), tL.getTexture("ButtonHover.png"), "Restart Level", Color.white, standardFont);
			screen.addGuiElement(restartButton);

			GuiButton quitButton = new GuiButton(new Rectangle(220, 235, 200, 30), 11, tL.getTexture("ButtonIdle.png"), tL.getTexture("ButtonHover.png"), "Quit to map", Color.white, standardFont);
			screen.addGuiElement(quitButton);
		}
		if (name.equals("LoadingScreen"))
		{
			GuiScreenLoading loadingScreen = new GuiScreenLoading("MainLoadingScreen", size, null, 5, false);
			loadingScreen.addTaskListener(this);

			screen = loadingScreen;
		}

		if (screen != null) screen.addListener(this);

		return screen;
	}

	public static void playRandomMusic()
	{
		String[] musicList = new String[] { "music/empty.wav" };
		Random gen = new Random();

		SoundManager.getDefaultManager().setBackgroundMusic(musicList[gen.nextInt(musicList.length)]);
	}

	public static void playMusic(String name)
	{
		SoundManager.getDefaultManager().setBackgroundMusic(name);
	}

	@Override
	public void doTask(String task, GuiScreenLoading loadingScreen)
	{
		if (task.equals("LoadFonts"))
		{
			FontLoader.getDefaultLoader().registerFont(new java.awt.Font("Gungsuh", java.awt.Font.BOLD, 12), java.awt.Color.white, "Standard");
			FontLoader.getDefaultLoader().registerFont(new java.awt.Font("Gungsuh", java.awt.Font.BOLD, 15), java.awt.Color.white, "Title");
			FontLoader.getDefaultLoader().registerFont(new java.awt.Font("Gungsuh", java.awt.Font.BOLD, 40), java.awt.Color.white, "IngameGiant");
			FontLoader.getDefaultLoader().registerFont(new java.awt.Font("Gungsuh", java.awt.Font.PLAIN, 9), java.awt.Color.gray, "Console");

			loadingScreen.titleFont = FontLoader.getDefaultLoader().getFont("Title");
			loadingScreen.title = "Starting up ArenaShooter";
			loadingScreen.textColor = Color.white;
			loadingScreen.descriptionsFont = FontLoader.getDefaultLoader().getFont("Standard");
		}
		else if (task.equals("LoadImages"))
		{
			TextureLoader.getDefaultLoader().defaultPath = "images/";
			
			String[] preloadImages = new String[]{"ArenaBackground.png", "ArenaWalls.png", "BarBackground.png", "BarSegment.png", "ButtonHover.png", "ButtonIdle.png",
					"FIRINMAHLAZER.png", "HackArea.png", "Heat.png", "Laser.png", "Logo.png", "Particle.png", "SliderBackground.png", "TimeDistortion.png", "World.png"};
			
			for(String i : preloadImages) TextureLoader.getDefaultLoader().getTexture(i);
			
			loadingScreen.setupProgressBar();
		}
		else if (task.equals("LoadSounds"))
		{
			SoundSystemConfig.setSoundFilesPackage("sounds/");
			SoundManager.libraryClass = LibraryLWJGLOpenAL.class;
			SoundManager.addCodec(CodecWav.class, "wav");
			SoundManager.addCodec(CodecJOrbis.class, "ogg");
		}
		else if (task.equals("PlayMusic"))
		{
			SoundManager.getDefaultManager().setMusicVolume(0.5f);
			playRandomMusic();
		}
		else if (task.equals("LoadMainMenu"))
		{
			particleSystem = new ParticleSystem();

			currentGui = null;
			currentGui = createMenu("MainMenu");

			System.out.println("\nFinished setting up Game!");
		}
	}
}
