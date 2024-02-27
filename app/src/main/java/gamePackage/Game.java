package gamePackage;

import java.awt.Dimension;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Random;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import org.newdawn.slick.Color;
import org.newdawn.slick.Font;

public class Game implements LTConsoleListener
{
	public String gameType;

	public GameRules rules;

	public Player player;

	public Dimension windowSize;

	public int ticks;

	public ArrayList<ArrayList<Ball>> balls; //Usually: 0 = Player; 1 = Enemy; 2 = Bonus;
	public ArrayList<Effect> effects;
	public ArrayList<Particle> particles;
	public ArrayList<Particle> deadParticles;

	public Point crossPosition;

	public ArrayList<IngameView> igViews;

	public boolean noScoreMode;
	public int scoreDelay;

	public Game(String gameType, Dimension windowSize, Player player)
	{
		System.out.println("Starting up game...");

		this.gameType = gameType;
		this.windowSize = windowSize;
		this.player = player;

		rules = new GameRules(player, gameType);

		balls = new ArrayList<ArrayList<Ball>>();
		effects = new ArrayList<Effect>();
		player.items = new ArrayList<String>();

		particles = new ArrayList<Particle>();
		deadParticles = new ArrayList<Particle>();

		igViews = new ArrayList<IngameView>();
		igViews.add(new IngameView(0, windowSize, player));

		SoundManager soundManager = SoundManager.getDefaultManager();
		soundManager.addSource("Death", "Death.wav");
		soundManager.addSource("Earthquake", "Earthquake.wav");
		soundManager.addSource("Laser", "Laser.wav");
		soundManager.addSource("Pop", "Pop.wav");
		soundManager.addSource("Rain", "Rain.wav");

		newGame();
	}

	public void newGame()
	{
		ticks = 0;

		for (int i = 0; i < effects.size(); i++)
		{
			String soundName = (String) effects.get(i).soundName;
			if (soundName != null)
			{
				boolean soundUsed = false;

				for (int effectNumber = 0; effectNumber < effects.size(); effectNumber++)
				{
					if (effects.get(effectNumber).soundName != null)
					{
						if (effects.get(i).soundName.matches(effects.get(effectNumber).soundName) && effects.get(i) != effects.get(effectNumber)) soundUsed = true;
					}
				}
				if (!soundUsed)
				{
					SoundManager.getDefaultManager().stopSound(effects.get(i).soundName);
				}
			}
		}
		effects.clear();
		player.items.clear();

		player.lives = rules.playerStartLives;

		crossPosition = new Point(windowSize.width / 2, windowSize.height / 2);

		igViews.add(new IngameView(1, windowSize, player));

		addEffect((new Effect("Pause", new Target("Balls", Target.all), null, ticks, 0, null)));
		addEffect(new Effect("Invincible", new Target(0, Target.all), null, ticks, 100, null));

		noScoreMode = false;
	}

	public void update()
	{
		boolean gamePaused = false;
		boolean particlesPaused = false;

		for (int i = 0; i < effects.size(); i++)
		{
			Effect effect = effects.get(i);
			if (effect != null)
			{
				if (effect.type.matches("Pause") || effect.type.matches("PauseSpecial"))
				{
					gamePaused = true;

					if (effect.target.getString().equals("All"))
					{
						particlesPaused = true;
					}
				}
			}
		}

		if (!particlesPaused)
		{
			for (int i = 0; i < particles.size(); i++)
			{
				particles.get(i).update(ticks);
				if (particles.get(i).checkDead())
				{
					Boolean advancedParticlesAllowed = (Boolean) player.settings.get("AdvancedParticlesAllowed");
					if (advancedParticlesAllowed && particles.get(i).willBecomeDead)
					{
						particles.get(i).setDead();

						deadParticles.add(particles.get(i));
					}

					particles.remove(i);
					i--;
				}
			}
			while (deadParticles.size() > 2000)
			{
				deadParticles.remove(0);
			}
		}
		if (!gamePaused)
		{
			rules.run(this);

			ticks++;

			moveCrossPoint();

			if (player.items.size() >= 3 && rules.spellType == 1)
			{
				rules.combineItems(player.items, this, 0, false);

				player.items.clear();
			}

			for (int i = 0; i < effects.size(); i++)
			{
				Effect effect = effects.get(i);

				if ((effect.duration < ticks - effect.startPlayTime && effect.duration >= 0) || (effect.target == null) || (effect.target.targetIsDead(this)))
				{
					if (effect.soundName != null)
					{
						boolean soundUsed = false;

						for (int effectNumber = 0; effectNumber < effects.size(); effectNumber++)
						{
							if (effects.get(effectNumber).soundName != null)
							{
								if (effect.soundName.matches(effects.get(effectNumber).soundName) && effect != effects.get(effectNumber)) soundUsed = true;
							}
						}
						if (!soundUsed)
						{
							SoundManager.getDefaultManager().stopSound(effect.soundName);
						}
					}

					if (effect.type.matches("Death"))
					{
						ArrayList<Ball> affectedBalls = effect.target.getAllBalls(this);
						for (int ballNumber = 0; ballNumber < affectedBalls.size(); ballNumber++)
						{
							Ball affectedBall = affectedBalls.get(ballNumber);

							if (effect.secondaryTarget != null)
							{
								rules.killBall(affectedBall, effect.secondaryTarget.getBall(this, new Point((int) affectedBall.x, (int) affectedBall.y)), this);
							}
							else
							{
								balls.get(affectedBall.faction).remove(affectedBall);
							}
						}
					}

					effects.remove(effect);

					i--;
				}
				else
				{
					effect.update(this);

					if (effect.type.indexOf("Earthquake") == 0)
					{
						Random gen = new Random();

						if (gen.nextInt(65) == 0)
						{
							int strength = Integer.valueOf(effect.type.substring(10));

							Ball earthBall = new Ball(0, 0, Ball.idle, effect.target.getBall(this, null).faction, new Point(gen.nextInt(windowSize.width), gen.nextInt(windowSize.height)), (int) (gen.nextDouble() * strength * 0.4 + strength * 0.8), windowSize);
							earthBall.killsFaction.add(1);
							earthBall.disappearingFactor = 0.01;
							earthBall.color = new Color(0.8f, 0.6f, 0);
							earthBall.texture = TextureLoader.getDefaultLoader().getAnimatedTexture("EarthCrack.png");
							earthBall.showsSpecialEffects = false;

							spawnBall(earthBall, false);

							addEffect(new Effect("Invincible", new Target(earthBall, Target.first), null, ticks, -1, null));
						}
					}
					else if (effect.type.indexOf("Rain") == 0)
					{
						Random gen = new Random();
						if (gen.nextInt(25) == 0)
						{
							int strength = Integer.valueOf(effect.type.substring(4));

							Ball waterBall = new Ball(0, 5 + gen.nextDouble() * 2 - 1.0, Ball.linearDirectional, effect.target.getBall(this, null).faction, new Point(gen.nextInt(windowSize.width), 0), (int) (gen.nextDouble() * strength * 0.4 + strength * 0.8), windowSize);
							waterBall.killsFaction.add(1);
							waterBall.disappearingFactor = 0.0003;
							waterBall.existence = 0.5;
							waterBall.color = new Color(0.1f, 0.1f, 1, 1);
							waterBall.showsSpecialEffects = false;

							spawnBall(waterBall, false);

							addEffect(new Effect("Invincible", new Target(waterBall, Target.first), null, ticks, -1, null));
						}
					}
				}
			}

			if (rules.timeScoreBonus)
			{
				scoreDelay++;

				if (scoreDelay >= 20)
				{
					addScore(1);

					scoreDelay = 0;
				}
			}

			for (int faction = 0; faction < balls.size(); faction++)
			{
				for (int ballNumber = 0; ballNumber < balls.get(faction).size(); ballNumber++)
				{
					Ball ball = balls.get(faction).get(ballNumber);

					if (ball != null)
					{
						double speedFactor = 1;

						for (int i = 0; i < effects.size(); i++)
						{
							Effect effect = effects.get(i);

							if (effect.type.indexOf("Speed") == 0 && effect.target.affectsBall(ball, this))
							{
								String number = effect.type.substring(5);
								speedFactor = Double.valueOf(number);
							}
							if (effect.type.indexOf("Magnet") == 0 && effect.target.affectsBall(ball, this))
							{
								double magnetStrength = Double.valueOf(effect.type.substring(6));

								ArrayList<Ball> secondaryTargets = effect.secondaryTarget.getAllBalls(this);

								for (int otherBallNumber = 0; otherBallNumber < secondaryTargets.size(); otherBallNumber++)
								{
									double distance = Point.distance((int) ball.x, (int) ball.y, (int) secondaryTargets.get(otherBallNumber).x, (int) secondaryTargets.get(otherBallNumber).y);
									if (distance < (ball.radius * 2))
									{
										distance = ball.radius * 2;
									}
									double totalStrength = magnetStrength / (distance / 600);
									if (totalStrength > magnetStrength)
									{
										totalStrength = magnetStrength;
									}

									Ball.moveBallWithMagnet(secondaryTargets.get(otherBallNumber), new Point((int) ball.x, (int) ball.y), totalStrength);
								}
							}
						}

						moveBall(ball, speedFactor);

						for (int i = 0; i < effects.size(); i++)
						{
							Effect effect = effects.get(i);

							if (effect.type.indexOf("Burn") == 0 && effect.target.affectsBall(ball, this) && ball.showsSpecialEffects)
							{
								if (effect.duration - (ticks - effect.startPlayTime) > 100 || effect.duration < 0)
								{
									Random gen = new Random();

									Double fireParticleChance = Double.valueOf(effect.type.substring(4));

									while (fireParticleChance > 1)
									{
										createParticles(new Point((int) ball.x, (int) ball.y), ball.radius, new Point(gen.nextInt(2) - 1, -2), 1, Particle.fire, ball.color, ball.texture, gen.nextInt(200) == 0);

										fireParticleChance -= 1;
									}

									if (gen.nextInt((int) (1.0 / Double.valueOf(fireParticleChance))) == 0)
									{
										createParticles(new Point((int) ball.x, (int) ball.y), ball.radius, new Point(gen.nextInt(2) - 1, -2), 1, Particle.fire, ball.color, ball.texture, gen.nextInt(200) == 0);
									}
								}
							}
							else if (effect.type.indexOf("Laser") == 0 && effect.target.affectsBall(ball, this))
							{
								String fireSpeed = effect.type.substring(5, effect.type.indexOf(":"));

								Random gen = new Random();
								if (gen.nextInt((int) (1.0 / Double.valueOf(fireSpeed))) == 0)
								{
									String number = effect.type.substring(effect.type.indexOf(":") + 1);
									int range = Integer.valueOf(number);

									Ball nearestBall = effect.secondaryTarget.getBall(this, new Point((int) ball.x, (int) ball.y));

									if (nearestBall != null && Point.distanceSq((int) ball.x, (int) ball.y, (int) nearestBall.x, (int) nearestBall.y) <= (range * range))
									{
										rules.killBall(nearestBall, ball, this);

										if (SoundManager.getDefaultManager().isPlayingSound("Laser")) SoundManager.getDefaultManager().rewindSound("Laser");
										SoundManager.getDefaultManager().playSound("Laser");

										addEffect(new Effect("LineRed", new Target(new Point((int) ball.x, (int) ball.y), Target.first), new Target(new Point((int) nearestBall.x, (int) nearestBall.y), Target.first), ticks, 20, null));
									}
								}
							}
							else if (effect.type.indexOf("Turret") == 0 && effect.target.affectsBall(ball, this))
							{
								String fireDelay = effect.type.substring(6, effect.type.indexOf(":"));

								Integer currentDelay = (Integer) effect.additionalOptions.get("CurrentDelay");

								if (currentDelay == null)
								{
									currentDelay = 0;
								}

								if (currentDelay < Integer.valueOf(fireDelay))
								{
									effect.additionalOptions.put("CurrentDelay", Integer.valueOf(currentDelay + 1));
								}
								else
								{
									effect.additionalOptions.put("CurrentDelay", 0);

									String bulletSpeedString = effect.type.substring(effect.type.indexOf(":") + 1);
									int bulletSpeed = Integer.valueOf(bulletSpeedString);

									Ball nearestBall = effect.secondaryTarget.getBall(this, new Point((int) ball.x, (int) ball.y));

									if (nearestBall != null)
									{
										Ball bullet = new Ball(bulletSpeed, bulletSpeed, Ball.linearDirectional, 0, new Point((int) ball.x, (int) ball.y), 10, windowSize);
										bullet.killsFaction.add(1);
										bullet.color = new Color(0.8f, 0.8f, 0.2f, 1);
										bullet.showsSpecialEffects = false;

										double distanceX = nearestBall.x - bullet.x;
										double distanceY = nearestBall.y - bullet.y;

										double xPlus = distanceX;
										double yPlus = distanceY;
										if (xPlus < 0) xPlus = bullet.x - nearestBall.x;
										if (yPlus < 0) yPlus = bullet.y - nearestBall.y;

										if (xPlus == 0)
										{
											bullet.speed_x = 0;
										}
										else
										{
											bullet.speed_x = distanceX / (xPlus + yPlus) * bullet.generalSpeed_x;
										}
										if (yPlus == 0)
										{
											bullet.speed_y = 0;
										}
										else
										{
											bullet.speed_y = distanceY / (xPlus + yPlus) * bullet.generalSpeed_y;
										}

										Random gen = new Random();
										bullet.speed_x += gen.nextDouble() * bullet.speed_x * 0.2 - bullet.speed_x * 0.1;
										bullet.speed_y += gen.nextDouble() * bullet.speed_y * 0.2 - bullet.speed_y * 0.1;

										addEffect(new Effect("Invincible", new Target(bullet, Target.first), null, ticks, -1, null));

										spawnBall(bullet, false);
									}
								}
							}
							else if (effect.type.indexOf("Minespawn") == 0 && effect.target.affectsBall(ball, this))
							{
								String fireDelay = effect.type.substring(9, effect.type.indexOf(":"));

								Integer currentDelay = (Integer) effect.additionalOptions.get("CurrentDelay");

								if (currentDelay == null)
								{
									currentDelay = 0;
								}
								if (currentDelay < Integer.valueOf(fireDelay))
								{
									effect.additionalOptions.put("CurrentDelay", Integer.valueOf(currentDelay + 1));
								}
								else
								{
									effect.additionalOptions.put("CurrentDelay", 0);

									String typeSub = effect.type.substring(effect.type.indexOf(":") + 1);

									String ballRadius = typeSub.substring(0, typeSub.indexOf(":"));
									String explosionRadius = typeSub.substring(typeSub.indexOf(":") + 1);

									Ball mine = new Ball(0, 0, Ball.idle, ball.faction, new Point((int) ball.x, (int) ball.y), Integer.valueOf(ballRadius), windowSize);
									mine.explosionRadius = Integer.valueOf(explosionRadius);
									mine.texture = TextureLoader.getDefaultLoader().getAnimatedTexture("MineBall.png");

									spawnBall(mine, false);
								}
							}
							else if (effect.type.indexOf("FireLine") == 0 && effect.target.affectsBall(ball, this))
							{
								Integer currentDelay = (Integer) effect.additionalOptions.get("CurrentDelay");

								if (currentDelay == null)
								{
									currentDelay = 0;
								}
								if (currentDelay < 3)
								{
									effect.additionalOptions.put("CurrentDelay", Integer.valueOf(currentDelay + 1));
								}
								else
								{
									effect.additionalOptions.put("CurrentDelay", 0);

									Ball fireBall = new Ball(0, 0, Ball.idle, 0, new Point((int) ball.x, (int) ball.y), (int) (ball.radius * 0.8), windowSize);

									fireBall.color = new Color(ball.color.getRed(), ball.color.getGreen(), ball.color.getBlue(), 70);

									spawnBall(fireBall, false);

									addEffect(new Effect("Burn0.1", new Target(fireBall, Target.first), null, ticks, -1, null, TextureLoader.getDefaultLoader().getAnimatedTexture("FireA", ".png", 5)));

									addEffect(new Effect("Death", new Target(fireBall, Target.first), null, ticks, 4000, null));
								}
							}
							else if (effect.type.indexOf("Radius") == 0 && effect.target.affectsBall(ball, this))
							{
								String radiusAddition = effect.type.substring(6);

								ball.radius += Double.valueOf(radiusAddition);
							}
						}

						//-----------------------------------------------------------------Start checking for Collisions

						for (int otherFaction = 0; otherFaction < balls.size(); otherFaction++)
						{
							for (int otherBallNumber = 0; otherBallNumber < balls.get(otherFaction).size(); otherBallNumber++)
							{
								Ball otherBall = balls.get(otherFaction).get(otherBallNumber);
								if (otherBall != ball)
								{
									if (ball.checkCollision(otherBall)) //Ball Collision!
									{
										rules.collision(ball, otherBall, this);
									}
								}
							} //--------------------------------------------------------------Stop Checking for Collisions
						}

						if (balls.get(faction).get(ballNumber).checkDead())
						{
							balls.get(faction).remove(ballNumber);

							ballNumber--;
						}
					}
				}
			}
		}
	}

	public void draw()
	{
		GL11.glColor3f(1, 1, 1);
		if (TextureLoader.getDefaultLoader().getTexture("GameBackground.png") != null)
		{
			RenderHelper.drawRect(0, 0, windowSize.width, windowSize.height, TextureLoader.getDefaultLoader().getTexture("GameBackground.png"));
		}

		for (int i = 0; i < particles.size(); i++)
		{
			particles.get(i).draw();
		}
		for (int i = 0; i < deadParticles.size(); i++)
		{
			int lowestDistance = 50000;

			for (int ballNumber = 0; ballNumber < balls.get(0).size(); ballNumber++)
			{
				Ball ball = balls.get(0).get(ballNumber);

				int distance = (int) (Point.distanceSq((int) deadParticles.get(i).x, (int) deadParticles.get(i).y, (int) ball.x, (int) ball.y) / ball.existence);
				if (distance < lowestDistance)
				{
					lowestDistance = distance;
				}
			}

			if (lowestDistance <= 40000) //200*200
			{
				deadParticles.get(i).radius = (40000 - lowestDistance) / 2800; //200*200

				deadParticles.get(i).draw();
			}
		}

		ArrayList<Ball> bottomBalls = new ArrayList<Ball>();
		ArrayList<Ball> middleBalls = new ArrayList<Ball>();
		ArrayList<Ball> topBalls = new ArrayList<Ball>();

		for (int faction = 0; faction < balls.size(); faction++)
		{
			for (int ballNumber = 0; ballNumber < balls.get(faction).size(); ballNumber++)
			{
				Ball ball = balls.get(faction).get(ballNumber);

				if (ball.drawLayer == 0) bottomBalls.add(ball);
				else if (ball.drawLayer == 2) topBalls.add(ball);
				else middleBalls.add(ball);
			}
		}

		for (int arrayNumber = 0; arrayNumber < 3; arrayNumber++)
		{
			ArrayList<Ball> currentArray = null;
			if (arrayNumber == 0) currentArray = bottomBalls;
			else if (arrayNumber == 1) currentArray = middleBalls;
			else if (arrayNumber == 2) currentArray = topBalls;

			for (int ballNumber = 0; ballNumber < currentArray.size(); ballNumber++)
			{
				Ball ball = currentArray.get(ballNumber);

				if (ball != null)
				{
					boolean vampire = false;
					AnimatedTexture fire = null;

					float timeLeftInvincible = 0;
					for (int i = 0; i < effects.size(); i++)
					{
						Effect effect = effects.get(i);

						if (effect != null)
						{
							if (effect.type.matches("Invincible") && effect.target.affectsBall(ball, this) && ball.showsSpecialEffects)
							{
								timeLeftInvincible = 1 - (float) (effect.duration - (ticks - effect.startPlayTime)) / effect.duration;
							}
							if (effect.type.indexOf("Speed") == 0 && effect.target.affectsBall(ball, this) && ball.showsSpecialEffects)
							{
								double factor = Double.valueOf(effect.type.substring(5));
								
								if (factor <= 0) GL11.glColor4f(0.2f, 0.2f, 0.2f, 0.5f);
								else if (factor > 0 && factor < 1) GL11.glColor4f(0.0f, 0.0f, 1.0f, 0.5f);
								else if (factor == 1) GL11.glColor4f(0.0f, 0.0f, 0.0f, 0.0f);
								else GL11.glColor4f(0.2f, 0.2f, 0.0f, 0.5f);

								RenderHelper.drawRect((int) ball.x - (int) (ball.radius * 1.5), (int) ball.y - (int) (ball.radius * 1.5), (int) ball.x + (int) (ball.radius * 1.5), (int) ball.y + (int) (ball.radius * 1.5), TextureLoader.getDefaultLoader().getTexture("Frost.png"));
							}
							if (effect.type.indexOf("Burn") == 0 && effect.target.affectsBall(ball, this) && ball.showsSpecialEffects)
							{
								fire = effect.animatedTextures.get(ball);
							}
							if (effect.type.indexOf("Vampire") == 0 && effect.target.affectsBall(ball, this) && ball.showsSpecialEffects)
							{
								vampire = true;
							}
						}
					}

					if (timeLeftInvincible > 0 && timeLeftInvincible <= 1)
					{
						ball.existence = timeLeftInvincible;
					}

					ball.draw();

					if (vampire)
					{
						RenderHelper.drawRect((int) ball.x - (ball.radius / 3), (int) ball.y - (ball.radius / 3), (int) ball.x + (ball.radius / 3), (int) ball.y + (ball.radius / 3), TextureLoader.getDefaultLoader().getTexture("Burning.png"));
					}

					if (fire != null)
					{
						int width = (int) (ball.radius * 2.6);
						int height = (int) ((double) width / fire.getCurrentTexture().getWidth() * fire.getCurrentTexture().getHeight());

						RenderHelper.drawRect((int) (ball.x - (width / 2)), (int) (ball.y - (height * 0.6) - ball.radius * 0.6), (int) (ball.x + (width / 2)), (int) (ball.y + (height * 0.6) - ball.radius * 0.6), fire.getCurrentTexture());
					}

				}
			}
		}

		for (int i = 0; i < effects.size(); i++)
		{
			Effect effect = effects.get(i);
			if (effect != null)
			{
				if (effect.type.indexOf("String") == 0 || effect.type.indexOf("HugeString") == 0 || effect.type.indexOf("Image") == 0) //Draw Effect:String
				{
					if (!TextureLoader.getDefaultLoader().defaultPath.equals("images/Glow/")) GL11.glColor3f(0, 0, 0);
					else GL11.glColor3f(1, 1, 1);

					float timeLeft = (float) (ticks - effect.startPlayTime) / effect.duration;

					if (effect.type.indexOf("String") == 0 || effect.type.indexOf("HugeString") == 0)
					{
						String text = effect.type.substring(effect.type.indexOf("String") == 0 ? 6 : 10);

						Font font = effect.type.indexOf("String") == 0 ? FontLoader.getDefaultLoader().getFont("Standard") : FontLoader.getDefaultLoader().getFont("IngameGiant");

						int stringWidth = font.getWidth(text.toString());

						font.drawString(effect.target.getPoint(this, null).x - stringWidth / 2, (int) (effect.target.getPoint(this, null).y - timeLeft * 50), text);
					}
					else
					{
						//RenderHelper.drawRect(effect.target.getPoint(this, null).x, (int) (effect.target.getPoint(this, null).y - timeLeft * 50);
						//g.drawImage(mediaManager.images.get(effect.type.substring(5)), effect.target.getPoint(this, null).x, (int) (effect.target.getPoint(this, null).y - timeLeft * 50), this);
					}
				}
				else if (effect.type.indexOf("Oval") == 0)
				{
					Integer radius = Integer.valueOf(effect.type.substring(4, effect.type.indexOf(":")));

					float timeLeft = (float) (ticks - effect.startPlayTime) / effect.duration;

					String color = effect.type.substring(effect.type.indexOf(":") + 1);

					if (color.equalsIgnoreCase("red")) GL11.glColor4f(1, 0, 0, (1f - timeLeft) * 0.1f);
					if (color.equalsIgnoreCase("green")) GL11.glColor4f(0, 1, 0, (1f - timeLeft) * 0.1f);
					if (color.equalsIgnoreCase("blue")) GL11.glColor4f(0, 0, 1, (1f - timeLeft) * 0.1f);

					RenderHelper.drawRect(effect.target.getPoint(this, null).x - radius, effect.target.getPoint(this, null).y - radius, effect.target.getPoint(this, null).x + radius, effect.target.getPoint(this, null).y + radius, null);
				}
				else if (effect.type.indexOf("Line") == 0)
				{
					float timeLeft = (float) (ticks - effect.startPlayTime) / effect.duration;

					String color = effect.type.substring(4);

					if (timeLeft >= 0 && timeLeft <= 1)
					{
						if (color.equalsIgnoreCase("red")) GL11.glColor4f(1, 0, 0, (1f - timeLeft) * 0.1f);
						if (color.equalsIgnoreCase("green")) GL11.glColor4f(0, 1, 0, (1f - timeLeft) * 0.1f);
						if (color.equalsIgnoreCase("blue")) GL11.glColor4f(0, 0, 1, (1f - timeLeft) * 0.1f);
					}

					GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
					
					double distance = Point.distance(effect.target.getPoint(this, null).x, effect.target.getPoint(this, null).y, effect.secondaryTarget.getPoint(this, null).x, effect.secondaryTarget.getPoint(this, null).y);
					RenderHelper.drawLine(effect.target.getPoint(this, null).x, effect.target.getPoint(this, null).y, effect.secondaryTarget.getPoint(this, null).x, effect.secondaryTarget.getPoint(this, null).y, 5, distance, null);
				}
			}
		}

		for (int i = 0; i < igViews.size(); i++)
		{
			igViews.get(i).draw(this);
		}

		//Crosshair

		if (rules.showCross)
		{
			GL11.glColor3f(0.7f, 0.7f, 0.7f);

			GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
			
			RenderHelper.drawLine(crossPosition.x, crossPosition.y - 7, crossPosition.x, crossPosition.y + 7, 3, 14, null);
			RenderHelper.drawLine(crossPosition.x - 7, crossPosition.y, crossPosition.x + 7, crossPosition.y, 3, 14, null);

			for (int i = 0; i < igViews.size(); i++)
			{
				igViews.get(i).draw(this);
			}
		}
	}

	public Point getCursorPosition()
	{
		return new Point(Mouse.getX(), windowSize.height - Mouse.getY());
	}

	public Point getCrossPosition()
	{
		return crossPosition;
	}

	public void cleanup()
	{
		System.out.println("Cleaning up game...");

		if (SoundManager.getDefaultManager().isPlayingSound("Death")) SoundManager.getDefaultManager().stopSound("Death");
		SoundManager.getDefaultManager().removeSource("Death");
		if (SoundManager.getDefaultManager().isPlayingSound("Earthquake")) SoundManager.getDefaultManager().stopSound("Earthquake");
		SoundManager.getDefaultManager().removeSource("Earthquake");
		if (SoundManager.getDefaultManager().isPlayingSound("Laser")) SoundManager.getDefaultManager().stopSound("Laser");
		SoundManager.getDefaultManager().removeSource("Laser");
		if (SoundManager.getDefaultManager().isPlayingSound("Pop")) SoundManager.getDefaultManager().stopSound("Pop");
		SoundManager.getDefaultManager().removeSource("Pop");
		if (SoundManager.getDefaultManager().isPlayingSound("Rain")) SoundManager.getDefaultManager().stopSound("Rain");
		SoundManager.getDefaultManager().removeSource("Rain");
	}

	public void createParticles(Point position, int positionRange, Point direction, int number, int type, Color color, AnimatedTexture texture, boolean willBecomeDead)
	{
		Boolean particlesAllowed = (Boolean) player.settings.get("ParticlesAllowed");
		if (particlesAllowed)
		{
			Random gen = new Random();

			if (direction != null)
			{
				for (int i = 0; i < number; i++)
				{
					Particle newParticle = new Particle(position.x + (gen.nextInt(positionRange * 2) - positionRange), position.y + (gen.nextInt(positionRange * 2) - positionRange), (gen.nextDouble() * (double) direction.x * 1.2 + (double) direction.x / 10), (gen.nextDouble() * (double) direction.y * 1.2 + (double) direction.y / 10), type, color, ticks);
					newParticle.willBecomeDead = willBecomeDead;
					if (texture != null) newParticle.texture = texture;

					particles.add(newParticle);
				}
			}
			else
			{
				for (int i = 0; i < number; i++)
				{
					Particle newParticle = new Particle(position.x + (gen.nextInt(21) - 10), position.y + (gen.nextInt(21) - 10), (gen.nextDouble() * 3 - 1.5), (gen.nextDouble() * 3 - 1.5), type, color, ticks);
					newParticle.willBecomeDead = willBecomeDead;
					if (texture != null) newParticle.texture = texture;

					particles.add(newParticle);
				}
			}
		}
	}

	private void moveBall(Ball ball, double speedFactor)
	{
		Point targetPoint = ball.target.getPoint(this, new Point((int) ball.x, (int) ball.y));

		if (targetPoint != null)
		{
			ball.update(targetPoint, speedFactor);
		}
		else
		{
			ball.update(new Point((int) ball.x, (int) ball.y), 0);
		}
	}

	private void moveCrossPoint()
	{
		double crossSpeedFactor = 1;
		if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT))
		{
			crossSpeedFactor = 2;
		}

		int speedX = 0;
		int speedY = 0;
		if (Keyboard.isKeyDown(Keyboard.KEY_W))
		{
			speedY -= 3;
		}
		if (Keyboard.isKeyDown(Keyboard.KEY_S))
		{
			speedY += 3;
		}
		if (Keyboard.isKeyDown(Keyboard.KEY_A))
		{
			speedX -= 3;
		}
		if (Keyboard.isKeyDown(Keyboard.KEY_D))
		{
			speedX += 3;
		}
		if (speedX != 0 && speedY != 0)
		{
			speedX = (int) (speedX / Math.sqrt(2));
			speedY = (int) (speedY / Math.sqrt(2));
		}

		crossPosition.x += speedX * crossSpeedFactor;
		crossPosition.y += speedY * crossSpeedFactor;

		if (crossPosition.x > windowSize.width)
		{
			crossPosition.x = windowSize.width;
		}
		if (crossPosition.y > windowSize.height)
		{
			crossPosition.y = windowSize.height;
		}
		if (crossPosition.x < 0)
		{
			crossPosition.x = 0;
		}
		if (crossPosition.y < 0)
		{
			crossPosition.y = 0;
		}
	}

	public boolean unpauseGame(boolean special)
	{
		boolean unpaused = false;

		int igType = 0;

		if (!special) igType = 1;
		else igType = 2;

		for (int i = 0; i < igViews.size(); i++)
		{
			if (igViews.get(i).type == igType)
			{
				if (igType == 2)
				{
					rules.combineItems(igViews.get(i).usedElements, this, 0, true);
				}
				else
				{
					deadParticles.clear();
					particles.clear();

					player.score = 0;
				}

				igViews.remove(i);

				i--;
			}
		}

		String effect = null;

		if (!special) effect = "Pause";
		else effect = "PauseSpecial";

		for (int i = 0; i < effects.size(); i++)
		{
			String type = (String) effects.get(i).type;
			if (type.equalsIgnoreCase(effect))
			{
				effects.remove(i);
				i--;

				unpaused = true;
			}
		}

		return unpaused;
	}

	public void addEffect(Effect effect)
	{
		if (effect.soundName != null)
		{
			SoundManager.getDefaultManager().loopSound(effect.soundName);
		}

		effects.add(effect);
	}

	public static int countNumberOfItems(String item, ArrayList<String> array)
	{
		int number = 0;

		for (int i = 0; i < array.size(); i++)
		{
			if (array.get(i).equalsIgnoreCase(item))
			{
				number++;
			}
		}

		return number;
	}

	public void addEffectDuration(Effect effect)
	{
		String newType = (String) effect.type;
		Object newTarget = effect.target;
		Object newSecTarget = effect.target;

		boolean foundEffect = false;

		for (int i = 0; i < effects.size(); i++)
		{
			String type = (String) effects.get(i).type;
			Object target = effects.get(i).target;
			Object secTarget = effects.get(i).secondaryTarget;

			if (type.equalsIgnoreCase(newType) && target.equals(newTarget) && secTarget.equals(newSecTarget))
			{
				foundEffect = true;

				effects.get(i).duration += effect.duration;

				break;
			}
		}

		if (!foundEffect)
		{
			effects.add(effect);
		}
	}

	public ArrayList<Ball> getPlayerBalls(int faction)
	{
		ArrayList<Ball> playerBalls = new ArrayList<Ball>();

		if (balls.size() > faction)
		{
			for (int i = 0; i < balls.get(faction).size(); i++)
			{
				if (balls.get(faction).get(i).playerBall)
				{
					playerBalls.add(balls.get(faction).get(i));
				}
			}
		}

		return playerBalls;
	}

	public ArrayList<Ball> getNonPlayerBalls(int faction)
	{
		ArrayList<Ball> nonPlayerBalls = new ArrayList<Ball>();
		for (int i = 0; i < balls.get(faction).size(); i++)
		{
			if (!balls.get(faction).get(i).playerBall)
			{
				nonPlayerBalls.add(balls.get(faction).get(i));
			}
		}

		return nonPlayerBalls;
	}

	public void addScore(int score)
	{
		double multiplier = 1;

		for (int i = 0; i < effects.size(); i++)
		{
			Effect effect = effects.get(i);

			if (effect.type.indexOf("Score") == 0)
			{
				String number = effect.type.substring(5);

				multiplier = multiplier * Double.valueOf(number);
			}
		}

		player.score += score * multiplier;
	}

	public void tryShowingElementsScreen()
	{
		if (rules.spellType == 2)
		{
			if (!unpauseGame(true))
			{
				boolean gamePaused = false;

				for (int i = 0; i < effects.size(); i++)
				{
					Effect effect = effects.get(i);
					if (effect != null)
					{
						if (effect.type.matches("Pause"))
						{
							gamePaused = true;
						}
					}
				}

				if (!gamePaused)
				{
					addEffect(new Effect("PauseSpecial", new Target("All", Target.all), null, ticks, 0, null));

					igViews.add(new IngameView(2, windowSize, player));
				}
			}
			else
			{
				for (int i = 0; i < igViews.size(); i++)
				{
					if (igViews.get(i).type == 2)
					{
						igViews.remove(i);

						break;
					}
				}
			}
		}
	}

	public void spawnBall(Ball ball, boolean safely) //Safely will Use Existence and Harmless
	{
		if (safely)
		{
			ball.existence = 0.005;
			ball.disappearingFactor = -0.005;

			addEffect(new Effect("Harmless", new Target(ball, Target.first), null, ticks, 200, null));
		}

		balls.get(ball.faction).add(ball);
	}

	public void handleMouseInput()
	{
		if (Mouse.getEventButtonState())
		{
			if (Mouse.getEventButton() == 0) unpauseGame(false);
			if (Mouse.getEventButton() == 1) tryShowingElementsScreen();
		}

		for (IngameView view : igViews)
			view.handleMouseInput(Mouse.getX(), windowSize.width - Mouse.getY());
	}

	public void handleKeyboardInput()
	{
		if (Keyboard.getEventKeyState())
		{
			if (Keyboard.getEventKey() == Keyboard.KEY_SPACE)
			{
				boolean gamePaused = false;

				for (int i = 0; i < effects.size(); i++)
				{
					Effect effect = effects.get(i);
					if (effect != null)
					{
						if (effect.type.matches("Pause") || effect.type.matches("PauseSpecial"))
						{
							gamePaused = true;
						}
					}
				}

				 if(gamePaused) 
				 {
					 unpauseGame(false);
					 unpauseGame(true);
				 }
				 else tryShowingElementsScreen();
			}
			if (Keyboard.getEventKey() == Keyboard.KEY_C && Keyboard.isKeyDown(Keyboard.KEY_LSHIFT))
			{
				LTConsole newConsole = new LTConsole();
				newConsole.setVisible(true);
				newConsole.addConsoleListener(this);
			}
		}

		for (IngameView view : igViews)
			view.handleKeyboardInput();
	}

	public void runConsoleScript(LTConsoleEvent e)
	{
		rules.noScoreMode = true;

		System.out.println("Run Cheat:" + e.getScript());

		String script = e.getScript();

		if (script.indexOf("add") != -1)
		{
			String toAdd = script.substring(4);

			if (toAdd.indexOf("fire") != -1) player.items.add("Fire");
			else if (toAdd.indexOf("water") != -1) player.items.add("Water");
			else if (toAdd.indexOf("air") != -1) player.items.add("Air");
			else if (toAdd.indexOf("earth") != -1) player.items.add("Earth");
			else if (toAdd.indexOf("playtime") != -1)
			{
				ticks += 50000;
			}
		}
		else if (script.indexOf("elementsftw") != -1)
		{
			for (int i = 0; i < 400; i++)
			{
				if (i < 100) player.items.add("Fire");
				else if (i < 200) player.items.add("Water");
				else if (i < 300) player.items.add("Earth");
				else player.items.add("Air");
			}
		}
		else if (script.indexOf("livesftw") != -1)
		{
			player.lives += 100;
		}
		else if (script.indexOf("scoreftw") != -1)
		{
			player.score += 10000000;
		}
		else if (script.indexOf("cast") != -1)
		{
			int faction = 0;

			if (script.indexOf(" ") != 4) faction = Integer.valueOf(script.substring(4, script.indexOf(" ")));
			;

			String elementScript = script.substring(4);

			int fire = 0;
			int water = 0;
			int earth = 0;
			int air = 0;

			int fireIndex = elementScript.indexOf("f");
			if (fireIndex < elementScript.length() && fireIndex >= 0)
			{
				fire = Integer.valueOf(elementScript.substring(fireIndex + 1, fireIndex + 2));
			}
			int waterIndex = elementScript.indexOf("w");
			if (waterIndex < elementScript.length() && waterIndex >= 0)
			{
				water = Integer.valueOf(elementScript.substring(waterIndex + 1, waterIndex + 2));
			}
			int earthIndex = elementScript.indexOf("e");
			if (earthIndex < elementScript.length() && earthIndex >= 0)
			{
				earth = Integer.valueOf(elementScript.substring(earthIndex + 1, earthIndex + 2));
			}
			int airIndex = elementScript.indexOf("a");
			if (airIndex < elementScript.length() && airIndex >= 0)
			{
				air = Integer.valueOf(elementScript.substring(airIndex + 1, airIndex + 2));
			}

			rules.combineItems(fire, water, air, earth, this, faction, true);
		}
		else if (script.indexOf("beer++") != -1)
		{
			addEffect(new Effect("Drunk", new Target(0, Target.all), null, ticks, -1, null));
		}
	}
}
