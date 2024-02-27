package game;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Random;

import org.newdawn.slick.Color;

public class EnemyType
{

	public String type;

	public double spawnRate;
	public int spawnRange;

	private Random gen;

	public EnemyType(String type, double rate, int spawnRange)
	{
		this.type = type;
		spawnRate = rate;

		this.spawnRange = spawnRange;

		gen = new Random();
	}

	public ArrayList<EntityEnemy> createEnemies()
	{
		ArrayList<EntityEnemy> createdEnemies = new ArrayList<EntityEnemy>();

		if (gen.nextInt((int) (1.0 / spawnRate)) == 0)
		{
			createdEnemies.add(createOneEnemy(new Point(gen.nextInt((int) spawnRange * 2) - spawnRange, gen.nextInt((int) spawnRange * 2) - spawnRange)));
		}

		return createdEnemies;
	}

	public EntityEnemy createOneEnemy(Point position)
	{
		if (type.equals("Standard")) { return new EntityEnemyFollow(position.x, position.y, 15, 100, 0, TextureLoader.getDefaultLoader().getTexture("EnemySmall.png"), new Color(0, 0, 1f), TextureLoader.getDefaultLoader().getTexture("ShieldSmall.png"), new Color(0, 1f, 1f)); }
		if (type.equals("Bounce"))
		{
			EntityEnemy bounceEnemy = new EntityEnemy(position.x, position.y, 15, 100, 0, TextureLoader.getDefaultLoader().getTexture("EnemySmall.png"), new Color(0, 0, 1f), TextureLoader.getDefaultLoader().getTexture("ShieldSmall.png"), new Color(0, 1f, 1f));
			bounceEnemy.affectedByFriction = false;

			int sX = gen.nextInt(4);
			bounceEnemy.speed_x = sX != 0 ? sX != 1 ? sX != 2 ? -1 : -0.5 : 0.5 : 1;
			int sY = gen.nextInt(4);
			bounceEnemy.speed_y = sY != 0 ? sY != 1 ? sY != 2 ? -1 : -0.5 : 0.5 : 1;

			return bounceEnemy;
		}
		else if (type.equals("Shield"))
		{
			return new EntityEnemyFollow(position.x, position.y, 15, 20, 50, TextureLoader.getDefaultLoader().getTexture("EnemySmall.png"), new Color(0, 0, 1f), TextureLoader.getDefaultLoader().getTexture("ShieldSmall.png"), new Color(0, 1f, 1f));
		}
		else if (type.equals("SuperShield"))
		{
			return new EntityEnemyFollow(position.x, position.y, 12, 30, 200, TextureLoader.getDefaultLoader().getTexture("EnemySmall.png"), new Color(0, 0, 1f), TextureLoader.getDefaultLoader().getTexture("ShieldSmall.png"), new Color(0, 1f, 1f));
		}
		else if (type.equals("CircleShoot"))
		{
			EntityEnemy circleShootEnemy = new EntityEnemyCircular(position.x, position.y, 20, 150, 0, TextureLoader.getDefaultLoader().getTexture("EnemySmall.png"), new Color(0, 0, 1f), TextureLoader.getDefaultLoader().getTexture("ShieldSmall.png"), new Color(0, 1f, 1f));
			circleShootEnemy.affectedByFriction = false;
			int sX = gen.nextInt(4);
			circleShootEnemy.speed_x = sX != 0 ? sX != 1 ? sX != 2 ? -1 : -0.5 : 0.5 : 1;
			int sY = gen.nextInt(4);
			circleShootEnemy.speed_y = sY != 0 ? sY != 1 ? sY != 2 ? -1 : -0.5 : 0.5 : 1;

			return circleShootEnemy;
		}
		else if (type.equals("WhipShoot"))
		{
			return new EntityEnemyWhip(position.x, position.y, 20, 30, 10, TextureLoader.getDefaultLoader().getTexture("EnemySmall.png"), new Color(0, 0, 1f), TextureLoader.getDefaultLoader().getTexture("ShieldSmall.png"), new Color(0, 1f, 1f));
		}
		else if (type.equals("Big")) { return new EntityEnemyFollow(position.x, position.y, 50, 200, 100, TextureLoader.getDefaultLoader().getTexture("Enemy.png"), new Color(0, 0, 1f), TextureLoader.getDefaultLoader().getTexture("Shield.png"), new Color(0, 1f, 1f)); }

		return null;
	}
}
