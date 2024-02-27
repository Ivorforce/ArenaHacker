package game;


import java.awt.Point;
import java.util.ArrayList;
import java.util.Random;

import org.newdawn.slick.Color;

public class EnemySpawn
{

	public Point point;

	public EntityEnemy enemy;

	private int duration;

	public boolean canceled;

	public EnemySpawn(EntityEnemy enemy, int duration)
	{
		this.point = new Point((int) enemy.x, (int) enemy.y);

		this.enemy = enemy;

		this.duration = duration;
	}

	public ArrayList<Particle2D> update()
	{
		if(canceled) duration = 0;
		
		ArrayList<Particle2D> returnParticles = new ArrayList<Particle2D>();

		Random r = new Random();

		if (r.nextInt(5) == 0)
		{
			double angle = r.nextDouble() * Math.PI * 2;
			
			double x = Math.sin(angle) * enemy.radius / 14.0;
			double y = Math.cos(angle) * enemy.radius / 14.0;
					
			Particle2D particle = new Particle2D(point.x, point.y, x, y, Particle2D.standard, TextureLoader.getDefaultLoader().getTexture("Particle.png"), Color.blue);
			particle.radius = 2;
			returnParticles.add(particle);
		}

		duration--;

		return returnParticles;
	}

	public boolean readyToSpawn()
	{
		if (duration <= 0) return true;

		return false;
	}
}
