package game;

import java.util.ArrayList;
import java.util.Collection;

public class ParticleSystem
{

	private ArrayList<Particle2D> particles;

	public ParticleSystem()
	{
		particles = new ArrayList<Particle2D>();
	}

	public void moveParticles()
	{
		ArrayList<Particle2D> removeParticles = new ArrayList<Particle2D>();

		for (Particle2D particle : particles)
		{
			particle.move();

			if (particle.checkDead()) removeParticles.add(particle);
		}

		particles.removeAll(removeParticles);
	}

	public void drawParticles()
	{
		for (Particle2D particle : particles)
		{
			particle.draw();
		}
	}

	public void add(Particle2D particle)
	{
		particles.add(particle);
	}

	public void addAll(Collection<Particle2D> particles)
	{
		this.particles.addAll(particles);
	}

	public ArrayList<Particle2D> getParticles()
	{
		return particles;
	}
}
