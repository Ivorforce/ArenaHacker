package gamePackage;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Random;

import org.newdawn.slick.Color;


public class IntelligentEnemySpawner
{

	public void run(Game game, GameRules rules)
	{
		Random gen = new Random();

		boolean enemySpawning = false;

		if (rules.spellType == 1) enemySpawning = gen.nextInt(830) == 0;
		else enemySpawning = gen.nextDouble() < (0.0005 + (game.ticks * 0.0000003));
		
		if (enemySpawning)
		{
			if (gen.nextInt(35) == 0 && !(rules.spellType == 1)) //-------------------------------------Special Spawn!!!--------------------------------
			{
				int specialSelection = gen.nextInt(4);

				if (specialSelection == 0) //----------------------Circle---------------------	
				{
					ArrayList<Ball> players = game.getPlayerBalls(0);
					if (players.size() > 0)
					{
						Ball randomPlayer = players.get(gen.nextInt(players.size()));

						Point target = new Point((int) randomPlayer.x, (int) randomPlayer.y);

						for (int i = 0; i < 30; i++)
						{
							Point ballPoint = new Point((int) (Math.sin((double) (12 * i) / 180 * Math.PI) * 300 + target.x), (int) (Math.cos((double) (12 * i) / 180 * Math.PI) * 300 + target.y));

							if (!(ballPoint.x < 0 || ballPoint.y < 0 || ballPoint.x > game.windowSize.width || ballPoint.y > game.windowSize.height))
							{
								Ball newEnemy = new Ball(0.5, 0.5, Ball.linearTarget, 1, ballPoint, 15, game.windowSize);
								newEnemy.killsFaction.add(0);
								newEnemy.target = new Target(0, Target.nearest, Target.onlyPlayer);
								newEnemy.texture = TextureLoader.getDefaultLoader().getAnimatedTexture("EnemyBall.png");

								game.spawnBall(newEnemy, true);
							}
						}
					}
				}
				else if (specialSelection == 1 || specialSelection == 2) //----------------------Wall---------------------	
				{
					boolean isX = gen.nextBoolean();
					boolean isMin = gen.nextBoolean();
					boolean shift = gen.nextBoolean();

					for (int i = 0; i < 19; i++)
					{
						Point ballPoint = null;

						double speedX = 0;
						double speedY = 0;

						if(specialSelection == 2) isMin = i % 2 == (shift ? 0 : 1);
						
						if (isX)
						{
							if (isMin)
							{
								ballPoint = new Point(0, (i + 1) * (game.windowSize.width / 20));
								speedX = 0.5;
							}
							else
							{
								ballPoint = new Point(game.windowSize.width, (i + 1) * (game.windowSize.width / 20));
								speedX = -0.5;
							}
						}
						else
						{
							if (isMin)
							{
								ballPoint = new Point((i + 1) * (game.windowSize.width / 20), 0);
								speedY = 0.5;
							}
							else
							{
								ballPoint = new Point((i + 1) * (game.windowSize.width / 20), game.windowSize.height);
								speedY = -0.5;
							}
						}

						Ball newEnemy = new Ball(speedX, speedY, Ball.linearDirectionalBouncing, 1, ballPoint, 8, game.windowSize);
						newEnemy.killsFaction.add(0);
						newEnemy.texture = TextureLoader.getDefaultLoader().getAnimatedTexture("EnemyBall.png");

						game.spawnBall(newEnemy, true);
					}
				}
				else if(specialSelection == 3) //----------------------'Force Field'---------------------	
				{
					Ball center = new Ball(0, 0, Ball.idle, 1, new Point(gen.nextInt(game.windowSize.width), gen.nextInt(game.windowSize.height)), 8, game.windowSize);
					center.killsFaction.add(0);
					
					double radius = 20 + gen.nextDouble() * 80;
					int max = (int)(radius / 8f);
					
					game.spawnBall(center, true);

					for (int element = 0; element < max; element++)
					{
						Ball rotatingEnemy = new Ball(1, 1, Ball.instant, 1, new Point((int) game.balls.get(0).get(0).x, (int) game.balls.get(0).get(0).y), 10, game.windowSize);
						rotatingEnemy.color = new Color(1, 0.3f, 0.3f);
						rotatingEnemy.targetModification = Ball.targetModificationRotate;
						rotatingEnemy.rotationDistance = radius;
						rotatingEnemy.rotationDegreeIncrement = -1;
						rotatingEnemy.rotationDegrees = (360f / max) * element;
						rotatingEnemy.target = new Target(center, Target.first);
						rotatingEnemy.killsFaction.add(0);

						game.spawnBall(rotatingEnemy, true);
					}
				}				
			}
			else
			//-------------------------------------Usual Spawn--------------------------------
			{
				int radius = gen.nextInt(15) + 5;
				Point point = new Point(gen.nextInt(game.windowSize.width - 100) + 50, gen.nextInt(game.windowSize.height - 100) + 50);

				int movementTypeR = gen.nextInt(3);

				int movementType = 0;
				if(movementTypeR == 0) movementType = Ball.linearTarget;
				if(movementTypeR == 1) movementType = Ball.random;
				if(movementTypeR == 2) movementType = Ball.linearDirectionalBouncing;
				
				double speed = 0;
				if (movementType == Ball.linearTarget) speed = gen.nextDouble() * 1.5 + 0.5;
				else if (movementType == Ball.random) speed = gen.nextDouble() * 1.5 + 0.5;
				else if (movementType == Ball.linearDirectionalBouncing) speed = gen.nextDouble() * 4 - 2;

				Ball newEnemy = new Ball(speed, speed, movementType, 1, point, radius, game.windowSize);
				newEnemy.killsFaction.add(0);
				newEnemy.target = new Target(0, Target.nearest, Target.onlyPlayer);
				newEnemy.texture = TextureLoader.getDefaultLoader().getAnimatedTexture("EnemyBall.png");

				game.spawnBall(newEnemy, true);
			}
		}
	}
}
