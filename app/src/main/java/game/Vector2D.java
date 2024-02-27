package game;

import java.awt.Point;

public class Vector2D
{

	public double x;
	public double y;

	public Vector2D()
	{
	}

	public Vector2D(double x, double y)
	{
		this.x = x;
		this.y = y;
	}

	public Vector2D(Point startPoint, Point endPoint)
	{
		x = endPoint.x - startPoint.x;
		y = endPoint.y - startPoint.y;
	}

	public Vector2D mirrorAlongVector(Vector2D vector)
	{
		double scale = (x * vector.x + y * vector.y) / (vector.x * vector.x + vector.y * vector.y);
		double projX = scale * vector.x;
		double projY = scale * vector.y;

		return new Vector2D(2 * projX - x, 2 * projY - y);
	}

	@Override
	public String toString()
	{

		return "LTVector(" + x + ", " + y + ")";
	}
}
