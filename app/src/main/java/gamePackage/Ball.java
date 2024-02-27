package gamePackage;

import java.awt.Dimension;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Random;

import org.lwjgl.opengl.GL11;
import org.newdawn.slick.Color;


public class Ball
{

	public static int idle = -1;
	public static int momentum = 0;
	public static int linearTarget = 1;
	public static int random = 2;
	public static int linearDirectional = 3;
	public static int linearDirectionalBouncing = 4;
	public static int antiDistanceNear = 5;
	public static int instant = 6;

	public static int targetModificationNormal = 0;
	public static int targetModificationRotate = 1;
	public double rotationDegrees;
	public double rotationDistance;
	public double rotationDegreeIncrement;

	Dimension fieldSize;

	public boolean playerBall;

	public double generalSpeed_x;
	public double generalSpeed_y;
	public double inertia;
	public int moveMethod;
	public int targetModification;
	public boolean physical;

	public int faction;
	public ArrayList<Integer> killsFaction;
	public Color color;

	public double x;
	public double y;
	public int radius = 15;
	public double speed_x;
	public double speed_y;
	public Target target;

	public double disappearingFactor;
	public double existence;

	AnimatedTexture texture;
	public boolean showsSpecialEffects;

	public int explosionRadius;

	public int drawLayer;

	public Ball(double speedX, double speedY, int movementMethod, int faction, Point startPos, int bRadius, Dimension gameSize)
	{
		playerBall = false;

		generalSpeed_x = speedX;
		generalSpeed_y = speedY;
		inertia = 0;
		moveMethod = movementMethod;
		physical = false;

		this.faction = faction;
		this.killsFaction = new ArrayList<Integer>();

		x = startPos.x;
		y = startPos.y;

		radius = bRadius;

		showsSpecialEffects = true;

		if (faction == 0)
		{
			color = new Color(0.0f, 0.0f, 1.0f);
		}
		else if (faction == 1)
		{
			color = new Color(1.0f, 0.0f, 0.0f);
		}
		else if (faction == 2)
		{
			color = new Color(0.0f, 1.f, 0.0f);
		}
		else if (faction == 3)
		{
			color = new Color(1.0f, 1.0f, 0.0f);
		}
		else if (faction == 4)
		{
			color = new Color(1.0f, 0.0f, 1.0f);
		}
		else if (faction == 5)
		{
			color = new Color(0.0f, 1.0f, 1.0f);
		}

		fieldSize = gameSize;

		if (moveMethod == linearDirectional || moveMethod == linearDirectionalBouncing)
		{
			this.speed_x = speedX;
			this.speed_y = speedY;
		}

		existence = 1;

		rotationDistance = 1;
		rotationDegreeIncrement = 1;

		target = new Target(this, Target.first);
		
		this.texture = TextureLoader.getDefaultLoader().getAnimatedTexture("EnemyBall.png");
	}
	
	public void update(Point targetPoint, double speedFactor)
	{
		Point target = null;

		if (targetModification == targetModificationNormal)
		{
			target = new Point(targetPoint.x, targetPoint.y);
		}
		else if (targetModification == targetModificationRotate)
		{
			target = new Point(targetPoint.x, targetPoint.y);

			target.x += Math.sin((double) rotationDegrees / 180 * Math.PI) * rotationDistance;
			target.y += Math.cos((double) rotationDegrees / 180 * Math.PI) * rotationDistance;

			rotationDegrees += 1 * rotationDegreeIncrement;
			if (rotationDegrees >= 360)
			{
				rotationDegrees = 0;
			}
		}

		if (moveMethod == momentum) //Momentum & Inertia
		{
			speed_x = ((target.x - x) * generalSpeed_x + (speed_x * inertia));
			speed_y = ((target.y - y) * generalSpeed_y + (speed_y * inertia));

			x = x + speed_x * speedFactor;
			y = y + speed_y * speedFactor;
		}
		else if (moveMethod == linearTarget) //Linear (Target Depending)
		{
			double distanceX = target.x - x;
			double distanceY = target.y - y;

			double xPlus = distanceX;
			double yPlus = distanceY;
			if (xPlus < 0) xPlus = x - target.x;
			if (yPlus < 0) yPlus = y - target.y;

			if (xPlus == 0)
			{
				speed_x = 0;
			}
			else
			{
				speed_x = distanceX / (xPlus + yPlus) * generalSpeed_x;
			}
			if (yPlus == 0)
			{
				speed_y = 0;
			}
			else
			{
				speed_y = distanceY / (xPlus + yPlus) * generalSpeed_y;
			}

			x += (speed_x) * speedFactor;
			y += (speed_y) * speedFactor;
		}
		else if (moveMethod == random) //Random Movement
		{
			Random gen = new Random();

			speed_x = (gen.nextDouble() * 8.0 - 4.0) * 0.03 + speed_x;
			speed_y = (gen.nextDouble() * 8.0 - 4.0) * 0.03 + speed_y;

			if (speed_x > generalSpeed_x)
			{
				speed_x = generalSpeed_x;
			}
			else if (speed_x < -generalSpeed_x)
			{
				speed_x = -generalSpeed_x;
			}
			if (speed_y > generalSpeed_y)
			{
				speed_y = generalSpeed_y;
			}
			else if (speed_y < -generalSpeed_y)
			{
				speed_y = -generalSpeed_y;
			}

			if (x < 0 || x > fieldSize.width)
			{
				speed_x = -speed_x;
			}
			if (y < 0 || y > fieldSize.width)
			{
				speed_y = -speed_y;
			}

			x = x + speed_x * speedFactor;
			y = y + speed_y * speedFactor;
		}
		else if (moveMethod == linearDirectional) //Linear (Directional)
		{
			x += speed_x * speedFactor;
			y += speed_y * speedFactor;
		}
		else if (moveMethod == linearDirectionalBouncing) //Linear (Directional, Bouncing)
		{
			x = x + speed_x * speedFactor;
			y = y + speed_y * speedFactor;

			if (x >= fieldSize.width)
			{
				x = fieldSize.width;
				speed_x = -speed_x;
			}
			else if (x <= 0)
			{
				x = 0;
				speed_x = -speed_x;
			}
			if (y >= fieldSize.width)
			{
				y = fieldSize.width;
				speed_y = -speed_y;
			}
			else if (y <= 0)
			{
				y = 0;
				speed_y = -speed_y;
			}
		}
		else if (moveMethod == antiDistanceNear) //Linear (Target Depending, Must be near)
		{
			double distanceX = target.x - x;
			double distanceY = target.y - y;

			double xPlus = distanceX;
			double yPlus = distanceY;
			if (xPlus < 0) xPlus = x - target.x;
			if (yPlus < 0) yPlus = y - target.y;

			if (xPlus + yPlus < 100)
			{
				if (xPlus == 0) xPlus = 0.0001;
				if (yPlus == 0) yPlus = 0.0001;

				speed_x = distanceX / (xPlus + yPlus) * generalSpeed_x;
				speed_y = distanceY / (xPlus + yPlus) * generalSpeed_y;

				x += (speed_x) * speedFactor * (25 / (xPlus + yPlus));
				y += (speed_y) * speedFactor * (25 / (xPlus + yPlus));
			}
		}

		else if (moveMethod == instant) //Instant
		{
			x = target.x;
			y = target.y;
		}

		existence -= disappearingFactor;

		if (existence > 1)
		{
			existence = 1;
			disappearingFactor = 0;
		}
		if (existence < 0)
		{
			existence = 0;
		}
		
		if(texture != null) texture.update();
	}

	public boolean killsFaction(int faction)
	{
		for (int i = 0; i < killsFaction.size(); i++)
		{
			if (killsFaction.get(i) == faction) { return true; }
		}
		return false;
	}

	public boolean checkCollision(Ball oBall)
	{
		// Bestimmen der Verbindungsvektoren
		double x_Dist = oBall.x - x;
		double y_Dist = oBall.y - y;

		// Berechnen der Distanz
		double distance = Math.sqrt((x_Dist * x_Dist) + (y_Dist * y_Dist));

		if (distance < radius + oBall.radius)
		{
			return true;
		}
		else return false;
	}

	public void draw()
	{
		if (existence == 1)
		{
			color.bind();
			
			RenderHelper.drawRect((int) x - radius, (int) y - radius, x + radius, y + radius, texture.getCurrentTexture());
		}
		else
		{
			GL11.glColor4f(color.r, color.g, color.b, (float)existence);

			RenderHelper.drawRect((int) x - radius, (int) y - radius, x + radius, y + radius, texture.getCurrentTexture());
		}
	}

	public boolean checkDead()
	{
		if (existence <= 0)
		{
			return true;
		}
		else if ((moveMethod == linearDirectional || moveMethod == idle || moveMethod == random || moveMethod == linearDirectionalBouncing) && (x < -radius || y < -radius || x > fieldSize.width + radius || y > fieldSize.height + radius)) { return true; }

		return false;
	}

	public static void moveBallWithMagnet(Ball ball, Point magnetPoint, double speed)
	{
		double distanceX = magnetPoint.x - ball.x;
		double distanceY = magnetPoint.y - ball.y;

		double xPlus = distanceX;
		double yPlus = distanceY;
		if (xPlus < 0) xPlus = ball.x - magnetPoint.x;
		if (yPlus < 0) yPlus = ball.y - magnetPoint.y;

		if (xPlus == 0)
		{
			ball.speed_x = 0;
		}
		else
		{
			ball.speed_x = distanceX / (xPlus + yPlus) * speed;
		}
		if (yPlus == 0)
		{
			ball.speed_y = 0;
		}
		else
		{
			ball.speed_y = distanceY / (xPlus + yPlus) * speed;
		}

		ball.x = ball.x + (ball.speed_x);
		ball.y = ball.y + (ball.speed_y);
	}
}
