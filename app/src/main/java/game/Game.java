package game;


import java.awt.Dimension;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.Random;

import javax.swing.event.EventListenerList;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import org.newdawn.slick.Color;
import org.newdawn.slick.Font;

public class Game
{

	public Dimension windowSize;

	public int ticks;

	public float playTime;
	public int playTimeEnd;

	public int pastGameTime;
	public boolean gameEnded;
	public boolean gameEndedPositively;

	public int score;

	public int radius;

	public String levelName;

	public EntityPlayer player;
	public Point scrollPosition;
	private ArenaBackground background;

	public ArrayList<EnemyType> enemyTypes;

	public Boss boss;

	public ArrayList<EntityEnemy> enemies;
	public ArrayList<EntityRemainment> enemyRemainments;
	private ArrayList<EntityBullet> bullets;
	private ParticleSystem particleSystem;
	private ParticleSystem heatParticleSystem;
	public ArrayList<EnemySpawn> enemySpawns;

	private PowerupManager powerupManager;

	public Point laserHitPoint;

	private TimeManager timeManager;

	private EventListenerList listeners;

	public boolean lazerMode;

	public void addListener(GameListener listener)
	{
		listeners.add(GameListener.class, listener);
	}

	public void removeListener(GameListener listener)
	{
		listeners.remove(GameListener.class, listener);
	}

	public Game(int radius, ArrayList<EnemyType> enemyTypes, String levelName, String bossName, int playtimeEnd, Dimension windowSize)
	{
		this.windowSize = windowSize;

		System.out.println("Starting game...");

		listeners = new EventListenerList();

		this.radius = radius;
		this.enemyTypes = enemyTypes;

		this.levelName = levelName;

		player = new EntityPlayer();
		scrollPosition = new Point((int) player.x - windowSize.width / 2, (int) player.y - windowSize.height / 2);

		enemies = new ArrayList<EntityEnemy>();
		enemyRemainments = new ArrayList<EntityRemainment>();
		bullets = new ArrayList<EntityBullet>();
		particleSystem = new ParticleSystem();
		heatParticleSystem = new ParticleSystem();
		enemySpawns = new ArrayList<EnemySpawn>();

		powerupManager = new PowerupManager();

		timeManager = new TimeManager();

		SoundManager.getDefaultManager().addSource("Laser", "Laser.wav");
		SoundManager.getDefaultManager().setVolume("Laser", 0.2f);
		SoundManager.getDefaultManager().addSource("Shutdown", "Shutdown.wav");
		SoundManager.getDefaultManager().setVolume("Shutdown", 0.2f);
		SoundManager.getDefaultManager().addSource("TimeDistortion", "TimeDistortion.wav");
		SoundManager.getDefaultManager().addSource("PlayerDeath", "TimeDistortion.wav");

		if (bossName != null && bossName.length() > 0)
		{
			boss = new Boss(bossName);
			enemies.addAll(boss.createStartEnemies(this));
		}

		background = new ArenaBackground(TextureLoader.getDefaultLoader().getTexture("ArenaBackground.png"), new Dimension(1000, 1000), 0.5, radius);

		this.playTimeEnd = playtimeEnd;
		pastGameTime = -1;
	}

	public void update()
	{
		Random gen = new Random();

		ticks++;
		if (!gameEnded) playTime += timeManager.timeSpeed;

		if (pastGameTime == 0)
		{
			for (GameListener listener : listeners.getListeners(GameListener.class))
			{
				listener.gameEnded(new GameEvent(this, score, levelName, gameEndedPositively));
			}
		}
		if (pastGameTime > 0 && gameEnded) pastGameTime--;

		if (!player.checkDead() && (Keyboard.isKeyDown(Keyboard.KEY_SPACE) || Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT)))
		{
			timeManager.timeSpeed = 0.3;
		}
		else
		{
			timeManager.timeSpeed = 1;
		}

		if (Keyboard.isKeyDown(Keyboard.KEY_F1)) lazerMode = true;
		else lazerMode = false;

		timeManager.updateDistortion(powerupManager.getTimeDistortionFactor());

		double shakeStrength = (timeManager.timeDistortion - 0.2);
		if (shakeStrength > 0)
		{
			SoundManager.getDefaultManager().setVolume("TimeDistortion", (float) shakeStrength);
			SoundManager.getDefaultManager().loopSound("TimeDistortion");
		}

		if (timeManager.totalDistortion())
		{
			player.lifePoints = 0;
		}

		// -----------------------------------------------------------------Player----------------------------------------------------------

		player.move(Mouse.isButtonDown(1), radius);

		double desiredScroll_x = player.x - windowSize.width / 2;
		double desiredScroll_y = player.y - windowSize.height / 2;

		scrollPosition = new Point((int) (scrollPosition.x * 0.8 + desiredScroll_x * 0.2), (int) (scrollPosition.y * 0.8 + desiredScroll_y * 0.2));

		addHeat(player.heat / 5, player.heat / 2, player.x, player.y, 0, true);

		if (laserHitPoint != null)
		{
			int times = (int) (Point.distance(player.x, player.y, laserHitPoint.x, laserHitPoint.y) / 2);

			for (int i = 0; i < times; i++)
			{
				addHeat(0.01, 0.15, player.x + (laserHitPoint.x - player.x) * ((double) i / times), player.y + (laserHitPoint.y - player.y) * ((double) i / times), 0, true);
			}
		}

		if (player.checkDead() && !gameEnded)
		{
			playerDied();
		}
		
		if(player.checkDead() && gameEnded)
		{
			Random r = new Random();
			
			int n = 0;
			if(pastGameTime > 120) n = r.nextInt(2);
			
			if(pastGameTime == 120) 
			{
				n = 150;
				
				if (SoundManager.getDefaultManager().isPlayingSound("PlayerDeath")) SoundManager.getDefaultManager().stopSound("PlayerDeath");
				if(SoundManager.getDefaultManager().isPlayingSound("Shutdown")) SoundManager.getDefaultManager().rewindSound("Shutdown");
				SoundManager.getDefaultManager().playSound("Shutdown");
			}
			
			for (int i = 0; i < n; i++)
			{
				double angle = r.nextDouble() * Math.PI * 2;
				
				double x = Math.sin(angle) * 5f * r.nextDouble();
				double y = Math.cos(angle) * 5f * r.nextDouble();
						
				Particle2D particle = new Particle2D(player.x, player.y, x, y, Particle2D.standard, TextureLoader.getDefaultLoader().getTexture("ShieldSmall.png"), Color.red);
				particle.radius = 3;
				particleSystem.add(particle);
			}
		}

		// -----------------------------------------------------------------EnemyLogic----------------------------------------------------------

		for (EntityEnemy enemy : enemies)
		{
			enemy.updateLogic();
		}

		Point playerPosition = new Point((int) player.x, (int) player.y);

		powerupManager.updateLogic(playerPosition, player.radius);

		// -----------------------------------------------------------------Time-Distortional----------------------------------------------------------

		if (timeManager.isActiveFrame(ticks))
		{
			background.update();

			powerupManager.movePowerups(this);

			// -----------------------------------------------------------------Enemies----------------------------------------------------------

			for (EntityEnemy enemy : enemies)
			{
				enemy.move(this, playerPosition, (int) (timeManager.timeDistortion * 100));
				ArrayList<EntityBullet> enemyBullets = enemy.shoot(playerPosition);

				if (enemyBullets != null) bullets.addAll(enemyBullets);
			}

			for (EntityRemainment remainment : enemyRemainments)
			{
				remainment.update();
			}

			// -----------------------------------------------------------------Bullets----------------------------------------------------------

			for (EntityBullet bullet : bullets)
			{
				bullet.move((int) (timeManager.timeDistortion * 30), playerPosition);
			}

			if (boss == null && !(gameEnded && gameEndedPositively))
			{
				// -----------------------------------------------------------------Spawn----------------------------------------------------------

				ArrayList<EntityEnemy> spawningEnemies = new ArrayList<EntityEnemy>();

				for (EnemyType type : enemyTypes)
				{
					spawningEnemies.addAll(type.createEnemies());
				}

				for (EntityEnemy enemy : spawningEnemies)
				{
					spawnEnemy(enemy, 200);
				}

				for (EnemySpawn spawn : enemySpawns)
				{
					particleSystem.addAll(spawn.update());
				}
			}
			else if (boss != null)
			{
				boss.move(playerPosition, enemies);
			}

			// -----------------------------------------------------------------Particles----------------------------------------------------------

			particleSystem.moveParticles();
			heatParticleSystem.moveParticles();
		}

		// -----------------------------------------------------------------Laser----------------------------------------------------------

		if (Mouse.isButtonDown(0) && !player.checkDead())
		{
			Point mPos = new Point(Mouse.getX() + scrollPosition.x, windowSize.height - Mouse.getY() + scrollPosition.y);

			EntityEnemy hitEnemy = null;

			Vector2D hitEnemyVector = null;
			double hitEnemyS = 99999;

			for (EntityEnemy enemy : enemies)
			{
				Vector2D startToBall = new Vector2D(new Point((int) player.x, (int) player.y), new Point((int) enemy.x, (int) enemy.y));

				Vector2D startToEndLine = new Vector2D(new Point((int) player.x, (int) player.y), mPos);

				double a = startToEndLine.x * startToEndLine.x + startToEndLine.y * startToEndLine.y;
				double b = (startToBall.x * startToEndLine.x + startToBall.y * startToEndLine.y) / a;

				double d = b * b - ((startToBall.x * startToBall.x + startToBall.y * startToBall.y) - enemy.radius * enemy.radius) / a;

				if (d >= 0)
				{
					double dw = Math.sqrt(d);
					double s = b - dw;

					if (s < 0.00000001 || s > 1 - 0.00000001) s = b + dw;

					if (s > 0.00000001 && s < 1 - 0.00000001) // Laser hit
																// Enemy!
					{
						if (s < hitEnemyS)
						{
							hitEnemy = enemy;

							hitEnemyVector = startToEndLine;
							hitEnemyS = s;
						}
					}
				}
			}

			if (hitEnemy != null)
			{
				laserHitPoint = new Point((int) player.x, (int) player.y);

				laserHitPoint.x += (hitEnemyVector.x * hitEnemyS);
				laserHitPoint.y += (hitEnemyVector.y * hitEnemyS);

				if (hitEnemy.shieldPoints > 0)
				{
					hitEnemy.shieldPoints--;
				}
			}
			else
			{
				laserHitPoint = mPos;
			}

			addHeat(0.15, 0.15, laserHitPoint.x, laserHitPoint.y, 1, false);

			SoundManager.getDefaultManager().loopSound("Laser");
		}
		else
		{
			laserHitPoint = null;
			if (SoundManager.getDefaultManager().isPlayingSound("Laser")) SoundManager.getDefaultManager().stopSound("Laser");
		}

		player.updateHeat(Mouse.isButtonDown(0), powerupManager.getOverheatFactor());

		// -----------------------------------------------------------------Collisions----------------------------------------------------------

		if (!player.checkDead())
		{
			for (EntityEnemy enemy : enemies)
			{
				if (enemy.shieldPoints == 0 || powerupManager.advancedHacking())
				{
					if (Point.distanceSq(enemy.x, enemy.y, player.x, player.y) < (player.hackRadius * powerupManager.getHackRadiusFactor() + enemy.radius) * (player.hackRadius * powerupManager.getHackRadiusFactor() + enemy.radius))
					{
						enemy.lifePoints -= 1;
					}
				}

				if (Point.distanceSq(player.x, player.y, enemy.x, enemy.y) < (player.radius + enemy.radius) * (player.radius + enemy.radius))
				{
					player.lifePoints = 0;
				}
			}

			for (EntityBullet bullet : bullets)
			{
				if (powerupManager.advancedHacking() && Point.distanceSq(bullet.x, bullet.y, player.x, player.y) < (player.hackRadius * powerupManager.getHackRadiusFactor() + bullet.radius) * (player.hackRadius * powerupManager.getHackRadiusFactor() + bullet.radius))
				{
					bullet.lifePoints -= 1;
				}

				if (Point.distanceSq(bullet.x, bullet.y, player.x, player.y) < (bullet.radius + player.radius) * (bullet.radius + player.radius))
				{
					player.lifePoints = 0;
					;
				}
			}
		}

		// -----------------------------------------------------------------Cleanup----------------------------------------------------------

		ArrayList<EntityEnemy> removeEnemies = new ArrayList<EntityEnemy>();
		ArrayList<EntityRemainment> removeEnemieRemainments = new ArrayList<EntityRemainment>();
		ArrayList<EntityBullet> removeBullets = new ArrayList<EntityBullet>();
		ArrayList<EnemySpawn> removeSpawns = new ArrayList<EnemySpawn>();

		for (EntityEnemy enemy : enemies)
		{
			if (enemy.checkDead())
			{
				removeEnemies.add(enemy);
				enemyRemainments.add(new EntityRemainment(enemy));

				score += enemy.defeatScore;

				if(SoundManager.getDefaultManager().isPlayingSound("Shutdown")) SoundManager.getDefaultManager().rewindSound("Shutdown");
				SoundManager.getDefaultManager().playSound("Shutdown");

				if (gen.nextInt(50) == 0 && boss == null) powerupManager.addRandomPowerup(enemy.x, enemy.y);
			}
		}

		for (EntityRemainment remainment : enemyRemainments)
		{
			if (remainment.checkDone()) removeEnemieRemainments.add(remainment);
		}

		for (EntityBullet bullet : bullets)
		{
			if (bullet.checkDead(radius)) removeBullets.add(bullet);
		}

		for (EnemySpawn spawn : enemySpawns)
		{
			if (spawn.readyToSpawn())
			{
				if (!spawn.canceled) enemies.add(spawn.enemy);

				removeSpawns.add(spawn);
			}
		}

		enemies.removeAll(removeEnemies);

		if (boss != null)
		{
			for (EntityEnemy enemy : removeEnemies)
			{
				boss.enemyDied(enemy, enemies);
			}
		}

		bullets.removeAll(removeBullets);
		enemySpawns.removeAll(removeSpawns);

		if (boss != null)
		{
			if (boss.checkDead(enemies))
			{
				score += boss.defeatScore;

				boss = null;

				endGame(true, 60 * 3);
			}
		}

		if (playTime >= playTimeEnd && playTimeEnd > 0 && !gameEnded)
		{
			endGame(boss != null ? false : true, 60 * 3);
		}
	}

	private void playerDied()
	{
		if (!player.invincible)
		{
			endGame(false, 60 * 3);
		}
	}

	public void endGame(boolean playerWon, int pastGameTime)
	{
		if (!gameEnded)
		{
			gameEnded = true;
			gameEndedPositively = playerWon;
			this.pastGameTime = pastGameTime;

			Random r = new Random();

			if (playerWon)
			{
				for (EntityEnemy e : enemies)
				{
					e.lifePoints = 0;

					int n = r.nextInt(10) + 5;
					for (int i = 0; i < n; i++)
					{
						double angle = r.nextDouble() * Math.PI * 2;
						
						double x = Math.sin(angle) * e.radius / 7.0 * r.nextDouble();
						double y = Math.cos(angle) * e.radius / 7.0 * r.nextDouble();
								
						Particle2D particle = new Particle2D(e.x, e.y, x + e.speed_x, y + e.speed_y, Particle2D.standard, TextureLoader.getDefaultLoader().getTexture("ShieldSmall.png"), Color.blue);
						particle.radius = 2;
						particleSystem.add(particle);
					}
				}
				
				for(EntityBullet b : bullets)
				{
					 b.lifePoints = 0;
					 
						int n = r.nextInt(3) + 3;
						for (int i = 0; i < n; i++)
						{
							double angle = r.nextDouble() * Math.PI * 2;
							
							double x = Math.sin(angle) * b.radius / 7.0 * r.nextDouble();
							double y = Math.cos(angle) * b.radius / 7.0 * r.nextDouble();
									
							Particle2D particle = new Particle2D(b.x, b.y, x + b.speed_x, y + b.speed_y, Particle2D.standard, TextureLoader.getDefaultLoader().getTexture("ShieldSmall.png"), Color.blue);
							particle.radius = 2;
							particleSystem.add(particle);
						}
				}

				for (EnemySpawn sp : enemySpawns)
				{
					sp.canceled = true;
				}
			}
			else
			{
				player.lifePoints = 0;
				
				if(SoundManager.getDefaultManager().isPlayingSound("PlayerDeath")) SoundManager.getDefaultManager().rewindSound("PlayerDeath");
				SoundManager.getDefaultManager().playSound("PlayerDeath");
			}
		}
	}

	public void draw()
	{			
		Random gen = new Random();

		double shakeStrength = (timeManager.timeDistortion - 0.3) * 30;
		if (lazerMode) if (laserHitPoint != null) shakeStrength += 50; // FIRIN MAH LAZER!
		if (shakeStrength < 0) shakeStrength = 0;

		int shakeX = (int) ((gen.nextDouble() - 0.5) * shakeStrength);
		int shakeY = (int) ((gen.nextDouble() - 0.5) * shakeStrength);

		Point actualTranslation = new Point(-scrollPosition.x + shakeX, -scrollPosition.y + shakeY);

		double levelHackProgress = 0;
		if (boss != null) levelHackProgress = boss.hacked(enemies);
		else if (playTimeEnd > 0) levelHackProgress = (double) playTime / (double) playTimeEnd;

		background.draw(actualTranslation, levelHackProgress, windowSize);

		GL11.glPushMatrix();

		GL11.glTranslatef(actualTranslation.x, actualTranslation.y, 0);

		heatParticleSystem.drawParticles();

		if (!player.checkDead())
		{
			Color.red.bind();

			double hR = player.hackRadius * powerupManager.getHackRadiusFactor();
			RenderHelper.drawRect(player.x - hR, player.y - hR, player.x + hR, player.y + hR, TextureLoader.getDefaultLoader().getTexture("HackArea.png"));
		}

		for (EntityRemainment remainment : enemyRemainments)
		{
			remainment.draw();
		}

		Color.white.bind();
		for (EntityEnemy enemy : enemies)
		{
			enemy.draw();
		}

		for (EntityBullet bullet : bullets)
		{
			bullet.draw();
		}

		powerupManager.drawPowerups();

		if (!player.checkDead())
		{
			for (EntityEnemy enemy : enemies)
			{
				if (enemy.shieldPoints == 0 || powerupManager.advancedHacking())
				{
					if (Point.distanceSq(enemy.x, enemy.y, player.x, player.y) < (player.hackRadius * powerupManager.getHackRadiusFactor() + enemy.radius) * (player.hackRadius * powerupManager.getHackRadiusFactor() + enemy.radius))
					{
						GL11.glColor4f(1, powerupManager.advancedHacking() ? 0.4f : 0, 0, 0.4f);

						RenderHelper.drawLine(player.x, player.y, enemy.x, enemy.y, 3, Point.distance(enemy.x, enemy.y, player.x, player.y), TextureLoader.getDefaultLoader().getTexture("Laser.png"));
					}
				}
			}
		}

		if (!player.checkDead()) for (EntityBullet bullet : bullets)
		{
			if (powerupManager.advancedHacking() && Point.distanceSq(bullet.x, bullet.y, player.x, player.y) < (player.hackRadius * powerupManager.getHackRadiusFactor() + bullet.radius) * (player.hackRadius * powerupManager.getHackRadiusFactor() + bullet.radius))
			{
				GL11.glColor4f(1, powerupManager.advancedHacking() ? 0.4f : 0, 0, 0.4f);

				RenderHelper.drawLine(player.x, player.y, bullet.x, bullet.y, 3, Point.distance(bullet.x, bullet.y, player.x, player.y), TextureLoader.getDefaultLoader().getTexture("Laser.png"));
			}
		}

		particleSystem.drawParticles();

		player.draw();

		if (laserHitPoint != null)
		{
			GL11.glColor4f(powerupManager.getOverheatFactor() < 1 ? 0.5f : 1, 1, powerupManager.getOverheatFactor() < 1 ? 0.2f : 0, 0.2f);

			RenderHelper.drawLine(player.x, player.y, laserHitPoint.x, laserHitPoint.y, lazerMode ? 100 : 3, Point.distance(laserHitPoint.x, laserHitPoint.y, player.x, player.y), TextureLoader.getDefaultLoader().getTexture(lazerMode ? "FIRINMAHLAZER.png" : "Laser.png"));
		}

		GL11.glColor3f((float) levelHackProgress, 0.5f - (float) levelHackProgress / 2, 1f - (float) levelHackProgress);

		RenderHelper.drawRect(-radius * 1.5, -radius * 1.5, radius * 1.5, radius * 1.5, TextureLoader.getDefaultLoader().getTexture("ArenaWalls.png"));

		if (radius < windowSize.height)
		{
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
			GL11.glColor3f(0, 0, 0);

			RenderHelper.drawRect(-(radius + windowSize.width), -radius * 1.5 - windowSize.height, radius + windowSize.width, -radius * 1.5, null);
			RenderHelper.drawRect(-(radius + windowSize.width), radius * 1.5 + windowSize.height, radius + windowSize.width, radius * 1.5, null);
		}
		if (radius < windowSize.width)
		{
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
			GL11.glColor3f(0, 0, 0);

			RenderHelper.drawRect(-radius * 1.5 - windowSize.width, -(radius + windowSize.height), -radius * 1.5, radius + windowSize.height, null);
			RenderHelper.drawRect(radius * 1.5 + windowSize.width, -(radius + windowSize.height), radius * 1.5, radius + windowSize.height, null);
		}

		GL11.glPopMatrix();

		Console.draw(this);

		if (playTimeEnd > 0)
		{
			float ticks = playTimeEnd - playTime;

			float minutes = 0;
			float seconds = 0;
			float millis = 0;

			while (ticks >= 60 * 60)
			{
				minutes++;
				ticks -= 60 * 60;
			}

			while (ticks >= 60)
			{
				seconds++;
				ticks -= 60;
			}

			millis = ticks * 10 / 6;

			Font font = FontLoader.getDefaultLoader().getFont("IngameGiant");

			String timeLeftString = new Formatter().format("%02d:%02d:%02d", (int) minutes, (int) seconds, (int) millis).toString();
			float fade = (float) (playTimeEnd - playTime) / 600f;
			if (fade > 1) fade = 1;
			if (boss == null) fade = 1f - fade;

			font.drawString(windowSize.width / 2 - font.getWidth(timeLeftString) / 2, 20, timeLeftString, new Color(fade, 0.2f * (1f - fade), 1f - fade));
		}

		if (timeManager.timeDistortion > 0)
		{
			GL11.glColor4f(0, 0, 1, (float) timeManager.timeDistortion);

			RenderHelper.drawRect(0, 0, windowSize.width, windowSize.height, TextureLoader.getDefaultLoader().getTexture("TimeDistortion.png"));
		}
	}

	public void cleanup()
	{
		System.out.println("Cleaning up game...");

		if (SoundManager.getDefaultManager().isPlayingSound("Laser")) SoundManager.getDefaultManager().stopSound("Laser");
		SoundManager.getDefaultManager().removeSource("Laser");
		if (SoundManager.getDefaultManager().isPlayingSound("Shutdown")) SoundManager.getDefaultManager().stopSound("Shutdown");
		SoundManager.getDefaultManager().removeSource("Shutdown");
		if (SoundManager.getDefaultManager().isPlayingSound("TimeDistortion")) SoundManager.getDefaultManager().stopSound("TimeDistortion");
		SoundManager.getDefaultManager().removeSource("TimeDistortion");
		if (SoundManager.getDefaultManager().isPlayingSound("PlayerDeath")) SoundManager.getDefaultManager().stopSound("PlayerDeath");
		SoundManager.getDefaultManager().removeSource("PlayerDeath");
	}

	public void spawnEnemy(EntityEnemy enemy, int waitDuration)
	{
		if (waitDuration <= 0) enemies.add(enemy);
		else
		{
			enemySpawns.add(new EnemySpawn(enemy, waitDuration));
		}
	}

	public void addHeat(double heat, double maxHeat, double x, double y, double maxSpeed, boolean combinable)
	{
		if (heat != 0)
		{
			Random gen = new Random();

			boolean foundParticle = false;

			if (combinable)
			{
				for (Particle2D particle : heatParticleSystem.getParticles())
				{
					if (particle.getClass() == HeatParticle.class && (int) particle.x - (int) x < 4 && (int) particle.x - (int) x > -4 && (int) particle.y - (int) y < 4 && (int) particle.y - (int) y > -4)
					{
						HeatParticle hParticle = (HeatParticle) particle;

						if (hParticle.heat + heat > maxHeat && hParticle.heat < maxHeat) hParticle.heat = maxHeat;
						else if (hParticle.heat + heat < maxHeat) hParticle.heat += heat;

						foundParticle = true;

						break;
					}
				}
			}

			if (!foundParticle)
			{
				if (heat > maxHeat) heat = maxHeat;
				else heatParticleSystem.add(new HeatParticle((int) x, (int) y, (gen.nextDouble() - 0.5) * maxSpeed, (gen.nextDouble() - 0.5) * maxSpeed, TextureLoader.getDefaultLoader().getTexture("Heat.png"), heat));
			}
		}
	}
}
