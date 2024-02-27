package game;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Random;

import org.newdawn.slick.Color;

public class Boss
{

	public String name;
	public int defeatScore;

	public Boss(String name)
	{
		this.name = name;

		if (name.equals("DataBlob"))
		{
			defeatScore = 100;
		}
	}

	public void move(Point playerPosition, ArrayList<EntityEnemy> enemies)
	{
		Random gen = new Random();

		ArrayList<EntityEnemy> addParts = new ArrayList<EntityEnemy>();

		for (EntityEnemy enemy : enemies)
		{
			if (enemy.bossIdentifier != null)
			{
				if (name.equals("DataBlob"))
				{
					if (enemy.bossIdentifier.equals("BigBlob"))
					{
						enemy.speed_x += (playerPosition.x - enemy.x) / 200000;
						enemy.speed_y += (playerPosition.y - enemy.y) / 200000;
					}
					else if (enemy.bossIdentifier.equals("MediumBlob"))
					{
						if (gen.nextInt(5000) == 0)
						{
							EntityEnemy mediumBlob = new EntityEnemy((int) enemy.x + gen.nextInt((enemy.radius - 20) * 2) - (enemy.radius - 20), (int) enemy.y + gen.nextInt((enemy.radius - 20) * 2) - (enemy.radius - 20), 30, 120, 0, TextureLoader.getDefaultLoader().getTexture("Enemy.png"), new Color(0, 0, 1f), TextureLoader.getDefaultLoader().getTexture("Shield.png"), new Color(0, 1f, 1f));

							mediumBlob.affectedByFriction = false;

							mediumBlob.speed_x = -enemy.speed_x;
							mediumBlob.speed_y = -enemy.speed_y;

							addParts.add(mediumBlob);
						}
					}
				}
			}
		}
	}

	public void enemyDied(EntityEnemy enemy, ArrayList<EntityEnemy> enemies)
	{
		Random gen = new Random();

		if (name.equals("DataBlob"))
		{
			if (enemy.bossIdentifier.equals("BigBlob"))
			{
				int mediumRadius = 30;
				for (int i = 0; i < 120; i++)
				{
					int degrees = i * 3;

					double newX = Math.sin((double) degrees / 180 * Math.PI);
					double newY = Math.cos((double) degrees / 180 * Math.PI);

					EntityEnemy mediumBlob = new EntityEnemy((int) (enemy.x + newX * (enemy.radius - mediumRadius) * ((double) (i % 6) / 5)), (int) (enemy.y + newY * (enemy.radius - mediumRadius) * ((double) (i % 6) / 5)), mediumRadius, 120, 0, TextureLoader.getDefaultLoader().getTexture("EnemySmall.png"), new Color(0, 0, 1f), TextureLoader.getDefaultLoader().getTexture("ShieldSmall.png"), new Color(0, 1f, 1f));

					mediumBlob.bossIdentifier = "MediumBlob";

					mediumBlob.affectedByFriction = false;

					mediumBlob.speed_x = gen.nextDouble() - 0.5 + enemy.speed_x;
					mediumBlob.speed_y = gen.nextDouble() - 0.5 + enemy.speed_y;

					enemies.add(mediumBlob);
				}
			}
			else if (enemy.bossIdentifier.equals("MediumBlob"))
			{
				for (int i = 0; i < 3; i++)
				{
					EntityEnemy smallBlob = new EntityEnemyCircular((int) enemy.x, (int) enemy.y, 12, 50, 0, TextureLoader.getDefaultLoader().getTexture("EnemySmall.png"), new Color(0, 0, 1f), TextureLoader.getDefaultLoader().getTexture("ShieldSmall.png"), new Color(0, 1f, 1f));

					smallBlob.bossIdentifier = "SmallBlob";

					smallBlob.affectedByFriction = false;

					smallBlob.defeatScore = 0;

					smallBlob.speed_x = gen.nextDouble() - 0.5 + enemy.speed_x;
					smallBlob.speed_y = gen.nextDouble() - 0.5 + enemy.speed_y;

					enemies.add(smallBlob);
				}
			}
		}
	}

	public boolean checkDead(ArrayList<EntityEnemy> enemies)
	{
		boolean part = false;

		for (EntityEnemy enemy : enemies)
		{
			if (enemy.bossIdentifier != null)
			{
				part = true;
				break;
			}
		}
		if (!part) { return true; }

		return false;
	}

	public ArrayList<EntityEnemy> createStartEnemies(Game game)
	{
		ArrayList<EntityEnemy> returnEnemies = new ArrayList<EntityEnemy>();
		if (name.equals("DataBlob"))
		{
			EntityEnemy blob = new EntityEnemy(0, -game.radius + 210, 200, 500, 0, TextureLoader.getDefaultLoader().getTexture("Enemy.png"), new Color(0, 0, 1f), TextureLoader.getDefaultLoader().getTexture("Shield.png"), new Color(0, 1f, 1f));
			blob.affectedByFriction = false;
			blob.bossIdentifier = "BigBlob";
			blob.defeatScore = 10;
			returnEnemies.add(blob);
		}

		return returnEnemies;
	}

	public double hacked(ArrayList<EntityEnemy> enemies)
	{
		if (name.equals("DataBlob"))
		{
			int count = 0;

			for (EntityEnemy enemy : enemies)
			{
				if (enemy.bossIdentifier.equals("BigBlob"))
				{
					count += 120 * 4 + 50;
				}
				else if (enemy.bossIdentifier.equals("MediumBlob"))
				{
					count += 4;
				}
				else
				{
					count++;
					;
				}
			}

			return 1.0 - ((double) count / (120 * 4 + 50));
		}

		return 1;
	}
}
