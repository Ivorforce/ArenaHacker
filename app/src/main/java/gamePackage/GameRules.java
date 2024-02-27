package gamePackage;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Random;

import org.newdawn.slick.Color;


public class GameRules
{
	String title;

	String highScoreSlot;

	public boolean timeScoreBonus;
	public int playerStartLives;

	public boolean spawnsEnemyFromBonus;
	public boolean bonusNeedsRangeFromEnemy;
	public boolean recreateBonuses;
	public int spellType;

	private Player player;
	public boolean showCross;

	public boolean noScoreMode;

	IntelligentEnemySpawner spawner;

	public GameRules(Player player, String gameType)
	{
		this.player = player;
		this.title = gameType;
		this.highScoreSlot = gameType;

		spawner = new IntelligentEnemySpawner();
		
		if (gameType.equalsIgnoreCase("Adventure"))
		{
			bonusNeedsRangeFromEnemy = false;
			recreateBonuses = true;
			spawnsEnemyFromBonus = false;
			playerStartLives = 5;
			timeScoreBonus = true;
			spellType = 2;
		}
		if (gameType.equalsIgnoreCase("Action"))
		{
			bonusNeedsRangeFromEnemy = false;
			recreateBonuses = true;
			spawnsEnemyFromBonus = false;
			playerStartLives = 5;
			timeScoreBonus = true;
			spellType = 1;
		}
		if (gameType.equalsIgnoreCase("Endurance"))
		{
			bonusNeedsRangeFromEnemy = false;
			recreateBonuses = true;
			spawnsEnemyFromBonus = false;
			playerStartLives = 3;
			timeScoreBonus = true;
			spellType = 0;
		}
		if (gameType.equalsIgnoreCase("Survival"))
		{
			bonusNeedsRangeFromEnemy = false;
			recreateBonuses = true;
			spawnsEnemyFromBonus = true;
			playerStartLives = 1;
			timeScoreBonus = false;
			spellType = 0;
		}
		if (gameType.equalsIgnoreCase("Chase"))
		{
			bonusNeedsRangeFromEnemy = true;
			recreateBonuses = true;
			spawnsEnemyFromBonus = false;
			playerStartLives = 3;
			timeScoreBonus = false;
			spellType = 0;
		}
	}

	public void addStartBalls(Game game)
	{
		if ((Boolean) player.settings.get("KeyboardPlayerAllowed"))
		{
			showCross = true;
		}
		else
		{
			showCross = false;
		}

		ArrayList<ArrayList<Ball>> startBalls = new ArrayList<ArrayList<Ball>>();

		startBalls = new ArrayList<ArrayList<Ball>>();

		startBalls.add(new ArrayList<Ball>());

		Boolean mouse = (Boolean) player.settings.get("MousePlayerAllowed");
		Boolean keyboard = (Boolean) player.settings.get("KeyboardPlayerAllowed");

		if (mouse)
		{
			Ball playerBall = new Ball(0.002, 0.002, Ball.momentum, 0, new Point(game.windowSize.width / 2 + 20, game.windowSize.height / 2 + 20), 15, game.windowSize);
			playerBall.playerBall = true;
			playerBall.killsFaction.add(2);
			playerBall.inertia = 0.95;
			playerBall.physical = true;
			playerBall.target = new Target("Mouse", Target.first);
			if (spellType > 0) playerBall.explosionRadius = 120;

			playerBall.drawLayer = 1;
			playerBall.texture = TextureLoader.getDefaultLoader().getAnimatedTexture("PlayerBall.png");

			startBalls.get(0).add(playerBall);
		}
		if (keyboard)
		{
			Ball playerBall = new Ball(0.002, 0.002, Ball.momentum, 0, new Point(game.windowSize.width / 2 - 20, game.windowSize.height / 2 - 20), 15, game.windowSize);
			playerBall.playerBall = true;
			playerBall.killsFaction.add(2);
			playerBall.inertia = 0.95;
			playerBall.physical = true;
			playerBall.target = new Target("Keyboard", Target.first);
			if (spellType > 0) playerBall.explosionRadius = 120;

			playerBall.drawLayer = 2;
			playerBall.texture = TextureLoader.getDefaultLoader().getAnimatedTexture("PlayerBall.png");

			startBalls.get(0).add(playerBall);
		}

		startBalls.add(new ArrayList<Ball>());
		if (title.matches("Endurance"))
		{
			Ball enduranceBall = new Ball(5, 5, Ball.linearTarget, 1, new Point(5, 5), 10, game.windowSize);
			enduranceBall.killsFaction.add(0);
			enduranceBall.target = new Target(0, Target.nearest, Target.onlyPlayer);
			enduranceBall.texture = TextureLoader.getDefaultLoader().getAnimatedTexture("EnemyBall.png");

			startBalls.get(1).add(enduranceBall);
		}
		else if (title.matches("Chase"))
		{
			Ball chaseBall = new Ball(0.012, 0.012, Ball.momentum, 1, new Point(5, 5), 15, game.windowSize);
			chaseBall.killsFaction.add(0);
			chaseBall.target = new Target(2, Target.first);
			chaseBall.texture = TextureLoader.getDefaultLoader().getAnimatedTexture("EnemyBall.png");

			startBalls.get(1).add(chaseBall);
		}

		Random gen = new Random();

		startBalls.add(new ArrayList<Ball>());
		if (!title.matches("Chase"))
		{
			Ball newBonus = new Ball(2, 2, Ball.antiDistanceNear, 2, new Point(gen.nextInt(game.windowSize.width - 100) + 50, gen.nextInt(game.windowSize.height - 100) + 50), 10, game.windowSize);
			newBonus.target = new Target(0, Target.nearest, Target.onlyPlayer);
			newBonus.texture = TextureLoader.getDefaultLoader().getAnimatedTexture("BonusBall.png");

			startBalls.get(2).add(newBonus);
		}
		else
		{
			while (true)
			{
				Ball newBonus = new Ball(0, 0, Ball.idle, 2, new Point(gen.nextInt(game.windowSize.height - 100) + 50, gen.nextInt(game.windowSize.height - 100) + 50), 10, game.windowSize);
				newBonus.target = new Target(0, Target.nearest, Target.onlyPlayer);
				newBonus.texture = TextureLoader.getDefaultLoader().getAnimatedTexture("BonusBall.png");

				double x_Dist = newBonus.x - startBalls.get(1).get(0).x;
				double y_Dist = newBonus.y - startBalls.get(1).get(0).y;

				// Berechnen der Distanz
				double distance = Math.sqrt((x_Dist * x_Dist) + (y_Dist * y_Dist));

				if (distance > startBalls.get(1).get(0).radius + newBonus.radius + 350)
				{
					startBalls.get(2).add(newBonus);
					break;
				}
			}
		}

		game.balls = startBalls;
	}

	public void run(Game game)
	{
		if (game.ticks == 0)
		{
			addStartBalls(game);
		}

		if (spellType > 0)
		{
			Random gen = new Random();

			for (int i = 1; i < 3; i++)
			{
				if (game.ticks >= i * 25000 && game.balls.get(2).size() <= i)
				{
					Ball newBonus = new Ball(2, 2, Ball.antiDistanceNear, 2, new Point(gen.nextInt(game.windowSize.width - 100) + 50, gen.nextInt(game.windowSize.height - 100) + 50), 10, game.windowSize);
					newBonus.target = new Target(0, Target.nearest, Target.onlyPlayer);
					newBonus.texture = TextureLoader.getDefaultLoader().getAnimatedTexture("BonusBall.png");

					game.spawnBall(newBonus, false);
				}
			}

			spawner.run(game, this);
		}
	}

	public void collision(Ball ball1, Ball ball2, Game game)
	{
		boolean noDamage = false;
		boolean burns = false;

		for (int i = 0; i < game.effects.size(); i++)
		{
			Effect effect = game.effects.get(i);

			if (effect != null)
			{
				if ((effect.type.equals("Invincible") || effect.type.indexOf("Burn") == 0) && effect.target.affectsBall(ball2, game))
				{
					noDamage = true;
				}

				if (effect.type.indexOf("Burn") == 0 && effect.target.affectsBall(ball1, game))
				{
					burns = true;
				}
				if (effect.type.equals("Harmless") && effect.target.affectsBall(ball1, game))
				{
					noDamage = true;
				}
			}
		}

		if ((ball1.killsFaction(ball2.faction) || burns) && !noDamage && ball1.faction != ball2.faction)
		{
			killBall(ball2, ball1, game);
		}
		else if ((ball1.physical && ball2.physical))
		{
			int maxRange = ball1.radius + ball2.radius;

			ball1.x += maxRange / (ball1.x - ball2.x);
			ball1.y += maxRange / (ball1.y - ball2.y);

			ball2.x += maxRange / (ball2.x - ball1.x);
			ball2.y += maxRange / (ball2.y - ball1.y);
		}
	}

	public void killBall(Ball targetBall, Ball killingBall, Game game)
	{
		Random gen = new Random();

		boolean vampire = false;
		double vampireSpeed = 0;

		for (int i = 0; i < game.effects.size(); i++)
		{
			Effect effect = game.effects.get(i);

			if (effect != null)
			{
				if (effect.type.indexOf("Vampire") == 0 && effect.target.affectsBall(killingBall, game))
				{
					vampire = true;
					vampireSpeed = Double.valueOf(effect.type.substring(7));
				}
				else if (effect.type.matches("LifeBound"))
				{
					if (effect.secondaryTarget != null && effect.secondaryTarget.affectsBall(targetBall, game))
					{
						killBall(effect.target.getBall(game, null), killingBall, game);
					}
				}
			}
		}

		if (targetBall.playerBall) //Player Ball
		{
			if(SoundManager.getDefaultManager().isPlayingSound("Death")) SoundManager.getDefaultManager().rewindSound("Death");
			SoundManager.getDefaultManager().playSound("Death");

			if (player.lives > 1)
			{
				player.score -= 100;
			}

			player.lives -= 1;

			game.createParticles(new Point((int) targetBall.x, (int) targetBall.y), targetBall.radius, null, 30, Particle.fire, targetBall.color, targetBall.texture, true);

			if (player.lives == 0)
			{
				ArrayList<Ball> playerBalls = game.getPlayerBalls(0);

				for (int i = 0; i < playerBalls.size(); i++)
				{
					game.balls.get(targetBall.faction).remove(playerBalls.get(i));
				}
				if (!noScoreMode)
				{
					for (int i = 0; i < player.score / 100; i++)
					{
						game.createParticles(new Point(gen.nextInt(game.windowSize.width), gen.nextInt(game.windowSize.height)), game.windowSize.width, null, 5, Particle.standard, Color.blue, TextureLoader.getDefaultLoader().getAnimatedTexture("PlayerBall.png"), false);
					}

					player.registerScore(highScoreSlot);
				}

				game.newGame();
			}
			else
			{
				game.addEffect(new Effect("Invincible", new Target(0, Target.all, Target.onlyPlayer), null, game.ticks, 300, null));
			}
		}
		else
		{
			if(SoundManager.getDefaultManager().isPlayingSound("Pop")) SoundManager.getDefaultManager().rewindSound("Pop");
			SoundManager.getDefaultManager().playSound("Pop");

			if (targetBall.faction != 0)
			{
				game.addScore(100);
			}

			if (spellType > 0 && targetBall.faction == 2)
			{
				if (gen.nextInt(4) == 0)
				{
					int element = gen.nextInt(4);
					if (element == 0)
					{
						player.items.add("Fire");
						game.addEffect(new Effect("StringFire", new Target(new Point((int) targetBall.x, (int) targetBall.y), Target.first), null, game.ticks, 150, null));
					}
					else if (element == 1)
					{
						player.items.add("Water");

						game.addEffect(new Effect("StringWater", new Target(new Point((int) targetBall.x, (int) targetBall.y), Target.first), null, game.ticks, 150, null));
					}
					else if (element == 2)
					{
						player.items.add("Earth");
						game.addEffect(new Effect("StringEarth", new Target(new Point((int) targetBall.x, (int) targetBall.y), Target.first), null, game.ticks, 150, null));
					}
					else if (element == 3)
					{
						player.items.add("Air");
						game.addEffect(new Effect("StringAir", new Target(new Point((int) targetBall.x, (int) targetBall.y), Target.first), null, game.ticks, 150, null));
					}
				}
			}

			game.createParticles(new Point((int) targetBall.x, (int) targetBall.y), targetBall.radius, null, 10, Particle.standard, targetBall.color, targetBall.texture, true);

			if (bonusNeedsRangeFromEnemy && recreateBonuses && targetBall.faction == 2)
			{
				game.balls.get(targetBall.faction).remove(targetBall);

				while (true)
				{
					Point newPoint = new Point(gen.nextInt(game.windowSize.width - 100) + 50, gen.nextInt(game.windowSize.height - 100) + 50);

					double distance = Point.distanceSq((int) killingBall.x, (int) killingBall.y, newPoint.x, newPoint.y);

					if (distance > (killingBall.radius + 10 + 250) * (killingBall.radius + 10 + 250))
					{
						Ball newBonus = new Ball(0, 0, Ball.idle, targetBall.faction, newPoint, 10, game.windowSize);
						newBonus.target = new Target(0, Target.nearest, Target.onlyPlayer);
						newBonus.texture = TextureLoader.getDefaultLoader().getAnimatedTexture("BonusBall.png");

						game.spawnBall(newBonus, false);

						break;
					}
				}
			}
			else if (game.rules.recreateBonuses && targetBall.faction == 2)
			{
				game.balls.get(targetBall.faction).remove(targetBall);

				Ball newBonus = new Ball(2, 2, Ball.antiDistanceNear, targetBall.faction, new Point(gen.nextInt(game.windowSize.width - 100) + 50, gen.nextInt(game.windowSize.height - 100) + 50), 10, game.windowSize);
				newBonus.target = new Target(0, Target.nearest, Target.onlyPlayer);
				newBonus.texture = TextureLoader.getDefaultLoader().getAnimatedTexture("BonusBall.png");

				game.spawnBall(newBonus, false);
			}
			else
			{
				game.balls.get(targetBall.faction).remove(targetBall);
			}

			if (spawnsEnemyFromBonus)
			{
				Ball newEnemy = new Ball(gen.nextDouble() * 4 - 2.0, gen.nextDouble() * 4 - 2.0, Ball.linearDirectionalBouncing, 1, new Point(gen.nextInt(game.windowSize.width - 100) + 50, gen.nextInt(game.windowSize.height - 100) + 50), 10, game.windowSize);
				newEnemy.killsFaction.add(0);
				newEnemy.inertia = 0;
				newEnemy.target = new Target(0, Target.nearest, Target.onlyPlayer);
				newEnemy.texture = TextureLoader.getDefaultLoader().getAnimatedTexture("EnemyBall.png");

				game.spawnBall(newEnemy, true);
			}

			else if (vampire)
			{
				Ball newFriendBall = new Ball(vampireSpeed, vampireSpeed, Ball.linearTarget, 0, new Point((int) targetBall.x, (int) targetBall.y), targetBall.radius, game.windowSize);
				newFriendBall.killsFaction.add(1);
				newFriendBall.target = new Target(1, Target.nearest);
				newFriendBall.color = killingBall.color;
				newFriendBall.disappearingFactor = 0.002;
				newFriendBall.showsSpecialEffects = false;

				game.addEffect(new Effect("Invincible", new Target(newFriendBall, Target.first), null, game.ticks, 500, null));

				game.spawnBall(newFriendBall, false);
			}
		}
		if (targetBall.explosionRadius > 0)
		{
			Ball explosion = new Ball(0, 0, -1, targetBall.faction, new Point((int) targetBall.x, (int) targetBall.y), targetBall.explosionRadius, game.windowSize);
			explosion.killsFaction.add(1);
			explosion.disappearingFactor = 0.01;
			explosion.color = targetBall.color;
			explosion.showsSpecialEffects = false;

			explosion.texture = TextureLoader.getDefaultLoader().getAnimatedTexture("SmokeA", ".png", 13);
			explosion.texture.imageSpeed = 0.15;
			explosion.texture.repeats = false;
			explosion.drawLayer = 2;

			game.spawnBall(explosion, false);

			game.addEffect(new Effect("Invincible", new Target(explosion, Target.first), null, game.ticks, -1, null));
			game.addEffect(new Effect("Shake1", new Target(targetBall.faction, Target.all), null, game.ticks, 7, null));

			game.createParticles(new Point((int) explosion.x, (int) explosion.y), explosion.radius, null, 30, Particle.fire, targetBall.color, targetBall.texture, true);
		}
	}

	public void combineItems(int fire, int water, int air, int earth, Game game, int faction, boolean advanced)
	{
		ArrayList<String> items = new ArrayList<String>();

		for (int i = 0; i < fire; i++)
			items.add("Fire");
		for (int i = 0; i < water; i++)
			items.add("Water");
		for (int i = 0; i < earth; i++)
			items.add("Earth");
		for (int i = 0; i < air; i++)
			items.add("Air");

		combineItems(items, game, faction, advanced);
	}

	public void combineItems(ArrayList<String> items, Game game, int faction, boolean advanced)
	{
		if (items.size() > 0)
		{
			int fire = Game.countNumberOfItems("Fire", items);
			int water = Game.countNumberOfItems("Water", items);
			int earth = Game.countNumberOfItems("Earth", items);
			int air = Game.countNumberOfItems("Air", items);

			boolean specialCast = false;
			if (advanced)
			{
				specialCast = true;

				String name = null;

				if (fire == 1 && water == 1 && earth == 1 && air == 1)
				{
					name = "+1 Life";
					player.lives++;
				}
				else if (fire == 2 && water == 0 && earth == 2 && air == 0)
				{
					name = "Friend-Ball";

					ArrayList<Ball> playerBalls = game.getPlayerBalls(faction);

					for (int ballNumber = 0; ballNumber < playerBalls.size(); ballNumber++)
					{
						Ball friend = new Ball(2.2, 2.2, Ball.linearTarget, 0, new Point((int) playerBalls.get(ballNumber).x, (int) playerBalls.get(ballNumber).y), 20, game.windowSize);
						friend.killsFaction.add(1);
						friend.disappearingFactor = 0.0004;
						friend.target = new Target(1, Target.nearest);
						friend.color = new Color(0.8f, 0.8f, 0.2f, 1);
						friend.showsSpecialEffects = false;

						game.spawnBall(friend, false);

						game.addEffect(new Effect("Burn1", new Target(friend, Target.first), null, game.ticks, -1, null));
					}
				}
				else if (fire == 1 && water == 0 && earth == 1 && air == 0)
				{
					name = "Juggernaught";

					ArrayList<Ball> playerBalls = game.getPlayerBalls(faction);

					for (int ballNumber = 0; ballNumber < playerBalls.size(); ballNumber++)
					{
						Ball friend = new Ball(0.002, 0.002, Ball.momentum, 0, new Point((int) game.balls.get(0).get(0).x, (int) game.balls.get(0).get(0).y), 25, game.windowSize);
						friend.killsFaction.add(1);
						friend.disappearingFactor = 0.0004;
						friend.inertia = 0.95;
						friend.target = new Target(playerBalls.get(ballNumber), Target.first);
						friend.color = new Color(0.8f, 0.8f, 0.2f, 1);
						friend.showsSpecialEffects = false;

						game.spawnBall(friend, false);

						game.addEffect(new Effect("Burn1", new Target(friend, Target.first), null, game.ticks, -1, null));
					}
				}
				else if (fire == 0 && water == 1 && earth == 0 && air == 2)
				{
					name = "Reverse Time";

					game.addEffect(new Effect("Speed-1", new Target(1, Target.all), null, game.ticks, 2000, null));
				}
				else if (fire == 0 && water == 2 && earth == 0 && air == 1)
				{
					name = "Freeze";

					game.addEffect(new Effect("Speed0.7", new Target(1, Target.all), null, game.ticks, 3000, null));
					game.addEffect(new Effect("Speed0.5", new Target(1, Target.all), null, game.ticks, 2300, null));
					game.addEffect(new Effect("Speed0", new Target(1, Target.all), null, game.ticks, 1600, null));
				}
				else if (fire == 0 && water == 1 && earth == 2 && air == 1)
				{
					name = "Magnet";

					game.addEffect(new Effect("Magnet2", new Target(game.getPlayerBalls(0), Target.all), new Target(2, Target.all), game.ticks, 3000, null));
				}
				else if (fire == 1 && water == 0 && earth == 2 && air == 1)
				{
					name = "Anti-Magnet";

					game.addEffect(new Effect("Magnet-0.15", new Target(game.getPlayerBalls(0), Target.all), new Target(1, Target.all), game.ticks, 2000, null));
				}
				else if (fire == 0 && water == 0 && earth == 1 && air == 2)
				{
					name = "Wind blast";

					int playerCount = game.getPlayerBalls(0).size();

					game.addEffect(new Effect("Magnet-" + (0.8 / playerCount), new Target(game.getPlayerBalls(0), Target.all), new Target(1, Target.all), game.ticks, 15, null));
					game.addEffect(new Effect("Magnet-" + (0.8 / playerCount), new Target(game.getPlayerBalls(0), Target.all), new Target(1, Target.all), game.ticks, 30, null));
					game.addEffect(new Effect("Magnet-" + (0.6 / playerCount), new Target(game.getPlayerBalls(0), Target.all), new Target(1, Target.all), game.ticks, 45, null));
					game.addEffect(new Effect("Magnet-" + (0.6 / playerCount), new Target(game.getPlayerBalls(0), Target.all), new Target(1, Target.all), game.ticks, 60, null));
					game.addEffect(new Effect("Magnet-" + (0.6 / playerCount), new Target(game.getPlayerBalls(0), Target.all), new Target(1, Target.all), game.ticks, 75, null));
					game.addEffect(new Effect("Magnet-" + (0.3 / playerCount), new Target(game.getPlayerBalls(0), Target.all), new Target(1, Target.all), game.ticks, 90, null));
				}
				else if (fire == 2 && water == 0 && earth == 2 && air == 1)
				{
					name = "Agressive Magnet";

					game.addEffect(new Effect("Magnet4", new Target(game.getPlayerBalls(0), Target.all), new Target(1, Target.all), game.ticks, 1500, null));
					game.addEffect(new Effect("Magnet1.2", new Target(game.getPlayerBalls(0), Target.all), new Target(2, Target.all), game.ticks, 1500, null));
				}
				else if (fire == 0 && water == 0 && earth == 3 && air == 1)
				{
					name = "Earthquake";

					game.addEffect(new Effect("Earthquake100", new Target(Integer.valueOf(faction), Target.all), null, game.ticks, 2500, "Earthquake"));
					game.addEffect(new Effect("Shake3", new Target(faction, Target.all), null, game.ticks, 2500, null));
				}
				else if (fire == 0 && water == 3 && earth == 1 && air == 0)
				{
					name = "Heavy Rain";

					game.addEffect(new Effect("Rain5", new Target(Integer.valueOf(faction), Target.all), null, game.ticks, 1500, "Rain"));
				}
				else if (fire == 1 && water == 0 && earth == 2 && air == 2)
				{
					name = "Vampire";

					game.addEffect(new Effect("Vampire2", new Target(game.getPlayerBalls(0), Target.all), null, game.ticks, 2000, null));
				}
				else if (fire == 2 && water == 1 && earth == 1 && air == 1)
				{
					name = "Elemental Wrath";

					ArrayList<Ball> playerBalls = game.getPlayerBalls(faction);

					int length = 1800;

					for (int ballNumber = 0; ballNumber < playerBalls.size(); ballNumber++)
					{
						for (int element = 0; element < 3; element++)
						{
							Ball friend = new Ball(1, 1, Ball.instant, 0, new Point((int) game.balls.get(0).get(0).x, (int) game.balls.get(0).get(0).y), 10, game.windowSize);
							friend.killsFaction.add(1);
							friend.disappearingFactor = 1.0 / length;
							friend.targetModification = Ball.targetModificationRotate;
							friend.rotationDistance = 40;
							friend.rotationDegreeIncrement = 1;
							friend.rotationDegrees = 120 * element;
							friend.target = new Target(playerBalls.get(ballNumber), Target.first);
							friend.showsSpecialEffects = false;

							game.spawnBall(friend, false);

							game.addEffect(new Effect("Invincible", new Target(friend, Target.first), null, game.ticks, -1, null));
						}
						for (int element = 0; element < 6; element++)
						{
							Ball friend = new Ball(1, 1, Ball.instant, 0, new Point((int) game.balls.get(0).get(0).x, (int) game.balls.get(0).get(0).y), 10, game.windowSize);
							friend.killsFaction.add(1);
							friend.disappearingFactor = 1.0 / length;
							friend.targetModification = Ball.targetModificationRotate;
							friend.rotationDistance = 100;
							friend.rotationDegreeIncrement = -1;
							friend.rotationDegrees = 60 * element;
							friend.target = new Target(playerBalls.get(ballNumber), Target.first);
							friend.showsSpecialEffects = false;

							game.spawnBall(friend, false);

							game.addEffect(new Effect("Invincible", new Target(friend, Target.first), null, game.ticks, -1, null));

							Ball friendSub = new Ball(1, 1, Ball.instant, 0, new Point((int) friend.x, (int) friend.y), 6, game.windowSize);
							friendSub.disappearingFactor = 1.0 / length / 2;
							friendSub.existence = 0.5;
							friendSub.targetModification = Ball.targetModificationRotate;
							friendSub.rotationDistance = 40;
							friendSub.rotationDegreeIncrement = -2;
							friendSub.rotationDegrees = 60 * element;
							friendSub.target = new Target(friend, Target.first);
							friendSub.showsSpecialEffects = false;

							game.spawnBall(friendSub, false);

							game.addEffect(new Effect("Invincible", new Target(friendSub, Target.first), null, game.ticks, -1, null));
						}
						for (int element = 0; element < 10; element++)
						{
							Ball friend = new Ball(1, 1, Ball.instant, 0, new Point((int) game.balls.get(0).get(0).x, (int) game.balls.get(0).get(0).y), 10, game.windowSize);
							friend.killsFaction.add(1);
							friend.disappearingFactor = 1.0 / length;
							friend.targetModification = Ball.targetModificationRotate;
							friend.rotationDistance = 200;
							friend.rotationDegreeIncrement = 1;
							friend.rotationDegrees = 36 * element;
							friend.target = new Target(playerBalls.get(ballNumber), Target.first);
							friend.showsSpecialEffects = false;

							game.spawnBall(friend, false);

							game.addEffect(new Effect("Invincible", new Target(friend, Target.first), null, game.ticks, -1, null));

							Ball friendSub = new Ball(1, 1, Ball.instant, 0, new Point((int) friend.x, (int) friend.y), 6, game.windowSize);
							friendSub.disappearingFactor = 1.0 / length / 2;
							friendSub.existence = 0.5;
							friendSub.targetModification = Ball.targetModificationRotate;
							friendSub.rotationDistance = 80;
							friendSub.rotationDegreeIncrement = 2;
							friendSub.rotationDegrees = 36 * element;
							friendSub.target = new Target(friend, Target.first);
							friendSub.showsSpecialEffects = false;

							game.spawnBall(friendSub, false);

							game.addEffect(new Effect("Invincible", new Target(friendSub, Target.first), null, game.ticks, -1, null));
						}
					}
				}
				else if (fire == 1 && water == 0 && earth == 1 && air == 1)
				{
					name = "Elemental Balls";

					ArrayList<Ball> playerBalls = game.getPlayerBalls(faction);

					for (int ballNumber = 0; ballNumber < playerBalls.size(); ballNumber++)
					{
						for (int element = 0; element < 3; element++)
						{
							Ball friend = new Ball(1, 1, Ball.instant, 0, new Point((int) game.balls.get(0).get(0).x, (int) game.balls.get(0).get(0).y), 10, game.windowSize);
							friend.killsFaction.add(1);
							friend.disappearingFactor = 0.0002;
							friend.targetModification = Ball.targetModificationRotate;
							friend.rotationDistance = 40;
							friend.rotationDegreeIncrement = 1;
							friend.rotationDegrees = 120 * element;
							friend.target = new Target(playerBalls.get(ballNumber), Target.first);
							friend.showsSpecialEffects = false;

							game.spawnBall(friend, false);

							game.addEffect(new Effect("Burn1", new Target(friend, Target.first), null, game.ticks, -1, null));
						}
					}
				}
				else if (fire == 1 && water == 3 && earth == 0 && air == 1)
				{
					name = "Score-Multiplier";

					game.addEffect(new Effect("Score3", new Target(0, Target.all), null, game.ticks, 2500, null));

					ArrayList<Ball> playerBalls = game.getPlayerBalls(faction);

					for (int ballNumber = 0; ballNumber < playerBalls.size(); ballNumber++)
					{
						for (int element = 0; element < 5; element++)
						{
							Ball scoreShowoff = new Ball(1, 1, Ball.instant, 0, new Point((int) game.balls.get(0).get(0).x, (int) game.balls.get(0).get(0).y), 7, game.windowSize);
							scoreShowoff.disappearingFactor = 0.0002;
							scoreShowoff.existence = 0.5;
							scoreShowoff.targetModification = Ball.targetModificationRotate;
							scoreShowoff.rotationDistance = 30;
							scoreShowoff.rotationDegreeIncrement = 2;
							scoreShowoff.rotationDegrees = 72 * element;
							scoreShowoff.target = new Target(playerBalls.get(ballNumber), Target.first);
							scoreShowoff.color = new Color(0, 0.8f, 0.3f);
							scoreShowoff.showsSpecialEffects = false;

							game.spawnBall(scoreShowoff, false);

							game.addEffect(new Effect("Invincible", new Target(scoreShowoff, Target.first), null, game.ticks, -1, null));

							Ball scoreShowoffSub = new Ball(1, 1, Ball.instant, 0, new Point((int) scoreShowoff.x, (int) scoreShowoff.y), 3, game.windowSize);
							scoreShowoffSub.disappearingFactor = 0.0002;
							scoreShowoffSub.existence = 0.5;
							scoreShowoffSub.targetModification = Ball.targetModificationRotate;
							scoreShowoffSub.rotationDistance = 20;
							scoreShowoffSub.rotationDegreeIncrement = 3;
							scoreShowoffSub.rotationDegrees = 72 * element;
							scoreShowoffSub.target = new Target(scoreShowoff, Target.first);
							scoreShowoffSub.color = new Color(0, 0.8f, 0.3f);
							scoreShowoffSub.showsSpecialEffects = false;

							game.spawnBall(scoreShowoffSub, false);

							game.addEffect(new Effect("Invincible", new Target(scoreShowoffSub, Target.first), null, game.ticks, -1, null));
						}
					}
				}
				else if (fire == 3 && water == 0 && earth == 0 && air == 1)
				{
					name = "Laser Defense";

					ArrayList<Integer> targetFactions = new ArrayList<Integer>();
					targetFactions.add(1);
					targetFactions.add(2);
					game.addEffect(new Effect("Laser0.009:220", new Target(game.getPlayerBalls(0), Target.all, Target.onlyPlayer), new Target(targetFactions, Target.nearest), game.ticks, 3500, null));

					ArrayList<Ball> playerBalls = game.getPlayerBalls(faction);

					for (int ballNumber = 0; ballNumber < playerBalls.size(); ballNumber++)
					{
						for (int element = 0; element < 3; element++)
						{
							Ball laserShowOff = new Ball(1, 1, Ball.instant, 0, new Point((int) game.balls.get(0).get(0).x, (int) game.balls.get(0).get(0).y), 10, game.windowSize);
							laserShowOff.disappearingFactor = 0.0002857;
							laserShowOff.existence = 0.5;
							laserShowOff.color = new Color(1, 0.3f, 0.3f);
							laserShowOff.targetModification = Ball.targetModificationRotate;
							laserShowOff.rotationDistance = 25;
							laserShowOff.rotationDegreeIncrement = -1;
							laserShowOff.rotationDegrees = 120 * element;
							laserShowOff.target = new Target(playerBalls.get(ballNumber), Target.first);
							laserShowOff.showsSpecialEffects = false;

							game.spawnBall(laserShowOff, false);

							game.addEffect(new Effect("Invincible", new Target(laserShowOff, Target.first), null, game.ticks, -1, null));
						}
					}
				}
				else if (fire == 3 && water == 1 && earth == 1 && air == 0)
				{
					name = "Mines";

					ArrayList<Integer> targetFactions = new ArrayList<Integer>();
					targetFactions.add(1);
					targetFactions.add(2);
					game.addEffect(new Effect("Minespawn160:15:80", new Target(game.getPlayerBalls(0), Target.all, Target.onlyPlayer), new Target(targetFactions, Target.all), game.ticks, 2000, null));
				}
				else if (fire == 3 && water == 0 && earth == 1 && air == 1)
				{
					name = "Laser Turret";

					ArrayList<Ball> playerBalls = game.getPlayerBalls(faction);

					for (int ballNumber = 0; ballNumber < playerBalls.size(); ballNumber++)
					{
						Ball friend = new Ball(0, 0, Ball.idle, 0, new Point((int) playerBalls.get(ballNumber).x, (int) playerBalls.get(ballNumber).y), 20, game.windowSize);
						friend.killsFaction.add(1);

						friend.texture = TextureLoader.getDefaultLoader().getAnimatedTexture("TurretLaser.png");

						friend.physical = true;
						friend.target = new Target(1, Target.nearest);
						friend.color = new Color(0.8f, 0.2f, 0.2f, 1);
						friend.showsSpecialEffects = false;

						game.spawnBall(friend, false);

						ArrayList<Integer> targetFactions = new ArrayList<Integer>();
						targetFactions.add(1);
						targetFactions.add(2);
						game.addEffect(new Effect("Laser0.006:200", new Target(friend, Target.first), new Target(targetFactions, Target.nearest), game.ticks, -1, null));

						for (int element = 0; element < 3; element++)
						{
							Ball laserShowOff = new Ball(1, 1, Ball.instant, 0, new Point((int) game.balls.get(0).get(0).x, (int) game.balls.get(0).get(0).y), 10, game.windowSize);
							laserShowOff.color = new Color(1, 0.3f, 0.3f);
							laserShowOff.targetModification = Ball.targetModificationRotate;
							laserShowOff.rotationDistance = 25;
							laserShowOff.rotationDegreeIncrement = -1;
							laserShowOff.rotationDegrees = 120 * element;
							laserShowOff.target = new Target(friend, Target.first);
							laserShowOff.showsSpecialEffects = false;

							game.spawnBall(laserShowOff, false);

							game.addEffect(new Effect("Invincible", new Target(laserShowOff, Target.first), null, game.ticks, -1, null));
							game.addEffect(new Effect("LifeBound", new Target(laserShowOff, Target.first), new Target(friend, Target.first), game.ticks, -1, null));
						}
					}
				}
				else if (fire == 1 && water == 0 && earth == 3 && air == 1)
				{
					name = "Turret";

					ArrayList<Ball> playerBalls = game.getPlayerBalls(faction);

					for (int ballNumber = 0; ballNumber < playerBalls.size(); ballNumber++)
					{
						Ball friend = new Ball(0, 0, Ball.idle, 0, new Point((int) playerBalls.get(ballNumber).x, (int) playerBalls.get(ballNumber).y), 20, game.windowSize);
						friend.killsFaction.add(1);

						friend.texture = TextureLoader.getDefaultLoader().getAnimatedTexture("Turret.png");

						friend.physical = true;
						friend.target = new Target(1, Target.nearest);
						friend.color = new Color(0.8f, 0.5f, 0.2f, 1);
						friend.showsSpecialEffects = false;

						game.spawnBall(friend, false);

						game.addEffect(new Effect("Turret125:5", new Target(friend, Target.first), new Target(1, Target.nearest), game.ticks, -1, null));
					}
				}
				else if (fire == 4 && water == 0 && earth == 0 && air == 1)
				{
					name = "Fireline";

					ArrayList<Ball> playerBalls = game.getPlayerBalls(faction);

					game.addEffect(new Effect("FireLine", new Target(playerBalls, Target.all), null, game.ticks, 200, null));
				}
				else if (fire == 1 && water == 0 && earth == 0 && air == 1)
				{
					name = "Elementrotation - Left";

					int fireCount = Game.countNumberOfItems("Fire", player.items);
					int waterCount = Game.countNumberOfItems("Water", player.items);
					int earthCount = Game.countNumberOfItems("Earth", player.items);
					int airCount = Game.countNumberOfItems("Air", player.items);

					player.items.clear();

					while (fireCount > 0)
					{
						player.items.add("Air");
						fireCount--;
					}
					while (waterCount > 0)
					{
						player.items.add("Earth");
						waterCount--;
					}
					while (earthCount > 0)
					{
						player.items.add("Fire");
						earthCount--;
					}
					while (airCount > 0)
					{
						player.items.add("Water");
						airCount--;
					}
				}
				else if (fire == 0 && water == 1 && earth == 1 && air == 0)
				{
					name = "Elementrotation - Right";

					int fireCount = Game.countNumberOfItems("Fire", player.items);
					int waterCount = Game.countNumberOfItems("Water", player.items);
					int earthCount = Game.countNumberOfItems("Earth", player.items);
					int airCount = Game.countNumberOfItems("Air", player.items);

					player.items.clear();

					while (fireCount > 0)
					{
						player.items.add("Earth");
						fireCount--;
					}
					while (waterCount > 0)
					{
						player.items.add("Air");
						waterCount--;
					}
					while (earthCount > 0)
					{
						player.items.add("Water");
						earthCount--;
					}
					while (airCount > 0)
					{
						player.items.add("Fire");
						airCount--;
					}
				}
				else if (fire == 2 && water == 0 && earth == 1 && air == 2)
				{
					name = "Grow";

					game.addEffect(new Effect("Radius1", new Target(0, Target.all, Target.onlyPlayer), null, game.ticks, 100, null));
					game.addEffect(new Effect("Radius1", new Target(0, Target.all, Target.onlyPlayer), null, game.ticks, 700, null));
					game.addEffect(new Effect("Radius-1", new Target(0, Target.all, Target.onlyPlayer), null, game.ticks, 800, null));
					game.addEffect(new Effect("Burn4", new Target(0, Target.all, Target.onlyPlayer), null, game.ticks, 800, null, TextureLoader.getDefaultLoader().getAnimatedTexture("FireA", ".png", 5)));
					game.addEffect(new Effect("Shake3", new Target(0, Target.all), null, game.ticks, 780, null));
					game.addEffect(new Effect("Speed0.3", new Target(0, Target.all, Target.onlyPlayer), null, game.ticks, 780, null));
				}
				else if (fire == 1 && water == 2 && earth == 0 && air == 0)
				{
					name = "Shield";

					ArrayList<Ball> playerBalls = game.getPlayerBalls(faction);

					for (int ballNumber = 0; ballNumber < playerBalls.size(); ballNumber++)
					{
						Ball shield = new Ball(0, 0, Ball.instant, 0, new Point((int) playerBalls.get(ballNumber).x, (int) playerBalls.get(ballNumber).y), Integer.valueOf(playerBalls.get(ballNumber).radius + 15), game.windowSize);
						shield.existence = 0.4;
						shield.texture = TextureLoader.getDefaultLoader().getAnimatedTexture("PlayerBall.png");
						shield.target = new Target(playerBalls.get(ballNumber), Target.first);					
						shield.explosionRadius = 100;
						shield.drawLayer = 0;

						game.spawnBall(shield, false);
					}
				}
				else specialCast = false;

				if (name != null)
				{
					game.addEffect(new Effect("HugeString" + name, new Target(new Point((int) (game.windowSize.width / 2), (int) (game.windowSize.height * 0.4)), Target.first), null, game.ticks, 500, null));
				}
			}
			if (!specialCast) //Usual cast
			{
				if (fire > 0)
				{
					game.addEffect(new Effect("Burn0.5", new Target(0, Target.all, Target.onlyPlayer), null, game.ticks, 150 + 250 * fire, null, TextureLoader.getDefaultLoader().getAnimatedTexture("FireA", ".png", 5)));
				}
				if (water > 0)
				{
					game.addEffect(new Effect("Speed" + 0.4 / water, new Target(1, Target.all), null, game.ticks, 850 * water, null));
				}
				if (earth > 0)
				{
					ArrayList<Ball> playerBalls = game.getPlayerBalls(faction);

					for (int ballNumber = 0; ballNumber < playerBalls.size(); ballNumber++)
					{
						Ball earthBall = new Ball(0, 0, -1, faction, new Point((int) game.balls.get(0).get(ballNumber).x, (int) game.balls.get(0).get(ballNumber).y), 50 + 85 * earth, game.windowSize);
						earthBall.killsFaction.add(1);
						earthBall.disappearingFactor = 0.01;
						earthBall.color = new Color(1, 0.8f, 0.5f, 1);
						earthBall.texture = TextureLoader.getDefaultLoader().getAnimatedTexture("EarthCrack.png");
						earthBall.showsSpecialEffects = false;
						earthBall.drawLayer = 0;

						game.spawnBall(earthBall, false);

						game.addEffect(new Effect("Invincible", new Target(earthBall, Target.first), null, game.ticks, -1, null));
						game.addEffect(new Effect("Shake1", new Target(faction, Target.all), null, game.ticks, 20, null));
					}
				}
				if (air > 0)
				{
					game.addEffect(new Effect("Invincible", new Target(0, Target.all, Target.onlyPlayer), null, game.ticks, 300 + 600 * air, null));
				}

				if (spellType == 1)
				{
					game.addEffect(new Effect("HugeString" + items, new Target(new Point((int) (game.windowSize.width / 2), (int) (game.windowSize.height * 0.4)), Target.all), null, game.ticks, 500, null));

					if ((fire != 0 ^ water != 0 ^ earth != 0 ^ air != 0) && (fire >= 3 || water >= 3 || earth >= 3 || air >= 3))
					{
						game.addEffect(new Effect("HugeString+1 Life", new Target(new Point((int) (game.windowSize.width / 2), (int) (game.windowSize.height * 0.46)), Target.all), null, game.ticks, 500, null));

						player.lives++;
					}
				}
			}
		}
	}
}
