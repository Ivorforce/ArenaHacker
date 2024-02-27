package gamePackage;

import java.awt.Point;
import java.util.ArrayList;

public class Target
{
	public static int all = -1;
	public static int first = 0;
	public static int last = 1;
	public static int nearest = 2;
	public static int farthest = 3;

	public static int everyBall = -1;
	public static int onlyPlayer = 0;
	public static int onlyNonPlayer = 1;

	public Object targetDefinition;
	public int targetSelection;
	public int targetFilter;

	public Target(Object definition, int selection)
	{
		targetDefinition = definition;
		targetSelection = selection;

		targetFilter = everyBall;
	}

	public Target(Object definition, int selection, int filter)
	{
		targetDefinition = definition;
		targetSelection = selection;

		targetFilter = filter;
	}

	public Point getPoint(Game game, Point origin)
	{
		ArrayList<Point> allPoints = getAllPoints(game);

		if (allPoints.size() > 0)
		{
			if (targetSelection == Target.first || targetSelection == Target.all) return allPoints.get(0);
			else if (targetSelection == Target.last) return allPoints.get(allPoints.size() - 1);
			else if (targetSelection == Target.nearest)
			{
				double lowestDistance = 999999999;
				Point nearestPoint = null;

				for (int i = 0; i < allPoints.size(); i++)
				{
					double distance = allPoints.get(i).distanceSq(origin);

					if (distance < lowestDistance)
					{
						nearestPoint = allPoints.get(i);
						lowestDistance = distance;
					}
				}

				return nearestPoint;
			}
			else if (targetSelection == Target.farthest)
			{
				double highestDistance = 0;
				Point farthestPoint = null;

				for (int i = 0; i < allPoints.size(); i++)
				{
					double distance = allPoints.get(i).distanceSq(origin);
					if (distance > highestDistance)
					{
						farthestPoint = allPoints.get(i);
						highestDistance = distance;
					}
				}

				return farthestPoint;
			}
		}

		return null;
	}

	public Ball getBall(Game game, Point origin)
	{
		ArrayList<Ball> allBalls = getAllBalls(game);

		if (allBalls.size() > 0)
		{
			if (targetSelection == Target.first || targetSelection == Target.all) return allBalls.get(0);
			else if (targetSelection == Target.last) return allBalls.get(allBalls.size() - 1);
			else if (targetSelection == Target.nearest)
			{
				double lowestDistance = 99999999;
				Ball nearestBall = null;

				for (int i = 0; i < allBalls.size(); i++)
				{
					double distance = origin.distanceSq((int) allBalls.get(i).x, (int) allBalls.get(i).y);
					if (distance < lowestDistance)
					{
						nearestBall = allBalls.get(i);
						lowestDistance = distance;
					}
				}

				return nearestBall;
			}
			else if (targetSelection == Target.farthest)
			{
				double highestDistance = 0;
				Ball farthestPoint = null;

				for (int i = 0; i < allBalls.size(); i++)
				{
					double distance = origin.distanceSq((int) allBalls.get(i).x, (int) allBalls.get(i).y);
					if (distance > highestDistance)
					{
						farthestPoint = allBalls.get(i);
						highestDistance = distance;
					}
				}

				return farthestPoint;
			}
		}

		return null;
	}

	public String getString()
	{
		return targetDefinition.toString();
	}

	public ArrayList<Point> getAllPoints(Game game)
	{
		ArrayList<Point> allPoints = new ArrayList<Point>();

		if (targetDefinition.getClass() == Point.class)
		{
			Point targetPoint = (Point) targetDefinition;
			allPoints.add(targetPoint);
		}
		else if (targetDefinition.getClass() == Ball.class)
		{
			Ball targetBall = (Ball) targetDefinition;

			if (targetFilter == Target.everyBall) allPoints.add(new Point((int) targetBall.x, (int) targetBall.y));
			else if (targetFilter == Target.onlyPlayer && targetBall.playerBall) allPoints.add(new Point((int) targetBall.x, (int) targetBall.y));
			else if (targetFilter == Target.onlyNonPlayer && !targetBall.playerBall) allPoints.add(new Point((int) targetBall.x, (int) targetBall.y));
		}
		else if (targetDefinition.getClass() == ArrayList.class)
		{
			@SuppressWarnings("unchecked")
			ArrayList<Object> targetArrayList = (ArrayList<Object>) targetDefinition;

			for (int i = 0; i < targetArrayList.size(); i++)
			{
				if (targetArrayList.get(0).getClass() == Ball.class)
				{
					Ball targetBall = (Ball) targetArrayList.get(i);

					if (targetFilter == Target.everyBall) allPoints.add(new Point((int) targetBall.x, (int) targetBall.y));
					else if (targetFilter == Target.onlyPlayer && targetBall.playerBall) allPoints.add(new Point((int) targetBall.x, (int) targetBall.y));
					else if (targetFilter == Target.onlyNonPlayer && !targetBall.playerBall) allPoints.add(new Point((int) targetBall.x, (int) targetBall.y));
				}
				else if (targetArrayList.get(0).getClass() == Point.class)
				{
					Point targetPoint = (Point) targetArrayList.get(i);
					allPoints.add(targetPoint);
				}
				else if (targetArrayList.get(0).getClass() == Integer.class)
				{
					Integer targetInt = (Integer) targetArrayList.get(i);

					for (int ballNumber = 0; ballNumber < game.balls.get(targetInt).size(); ballNumber++)
					{
						Ball targetBall = game.balls.get(targetInt).get(i);

						if (targetFilter == Target.everyBall) allPoints.add(new Point((int) targetBall.x, (int) targetBall.y));
						else if (targetFilter == Target.onlyPlayer && targetBall.playerBall) allPoints.add(new Point((int) targetBall.x, (int) targetBall.y));
						else if (targetFilter == Target.onlyNonPlayer && !targetBall.playerBall) allPoints.add(new Point((int) targetBall.x, (int) targetBall.y));
					}
				}
			}
		}
		else if (targetDefinition.getClass() == String.class)
		{
			String targetString = (String) targetDefinition;

			if (targetString.matches("Mouse"))
			{
				allPoints.add(game.getCursorPosition());
			}
			else if (targetString.matches("Keyboard"))
			{
				allPoints.add(game.crossPosition);
			}
		}
		else if (targetDefinition.getClass() == Integer.class)
		{
			Integer targetInt = (Integer) targetDefinition;

			for (int i = 0; i < game.balls.get(targetInt).size(); i++)
			{
				Ball targetBall = game.balls.get(targetInt).get(i);

				if (targetFilter == Target.everyBall) allPoints.add(new Point((int) targetBall.x, (int) targetBall.y));
				else if (targetFilter == Target.onlyPlayer && targetBall.playerBall) allPoints.add(new Point((int) targetBall.x, (int) targetBall.y));
				else if (targetFilter == Target.onlyNonPlayer && !targetBall.playerBall) allPoints.add(new Point((int) targetBall.x, (int) targetBall.y));
			}
		}

		return allPoints;
	}

	public ArrayList<Ball> getAllBalls(Game game)
	{
		ArrayList<Ball> allBalls = new ArrayList<Ball>();

		if (targetDefinition.getClass() == Ball.class)
		{
			Ball targetBall = (Ball) targetDefinition;

			if (targetFilter == Target.everyBall) allBalls.add(targetBall);
			else if (targetFilter == Target.onlyPlayer && targetBall.playerBall) allBalls.add(targetBall);
			else if (targetFilter == Target.onlyNonPlayer && !targetBall.playerBall) allBalls.add(targetBall);
		}
		else if (targetDefinition.getClass() == ArrayList.class)
		{
			@SuppressWarnings("unchecked")
			ArrayList<Object> targetArrayList = (ArrayList<Object>) targetDefinition;

			for (int i = 0; i < targetArrayList.size(); i++)
			{
				if (targetArrayList.get(0).getClass() == Ball.class)
				{
					Ball targetBall = (Ball) targetArrayList.get(i);

					if (targetFilter == Target.everyBall) allBalls.add(targetBall);
					else if (targetFilter == Target.onlyPlayer && targetBall.playerBall) allBalls.add(targetBall);
					else if (targetFilter == Target.onlyNonPlayer && !targetBall.playerBall) allBalls.add(targetBall);
				}
				else if (targetArrayList.get(0).getClass() == Integer.class)
				{
					Integer targetInt = (Integer) targetArrayList.get(i);

					for (int ballNumber = 0; ballNumber < game.balls.get(targetInt).size(); ballNumber++)
					{
						Ball targetBall = game.balls.get(targetInt).get(ballNumber);

						if (targetFilter == Target.everyBall) allBalls.add(targetBall);
						else if (targetFilter == Target.onlyPlayer && targetBall.playerBall) allBalls.add(targetBall);
						else if (targetFilter == Target.onlyNonPlayer && !targetBall.playerBall) allBalls.add(targetBall);
					}
				}
			}
		}
		else if (targetDefinition.getClass() == Integer.class)
		{
			Integer targetInt = (Integer) targetDefinition;

			for (int i = 0; i < game.balls.get(targetInt).size(); i++)
			{
				Ball targetBall = game.balls.get(targetInt).get(i);

				if (targetFilter == Target.everyBall) allBalls.add(targetBall);
				else if (targetFilter == Target.onlyPlayer && targetBall.playerBall) allBalls.add(targetBall);
				else if (targetFilter == Target.onlyNonPlayer && !targetBall.playerBall) allBalls.add(targetBall);
			}
		}

		return allBalls;
	}

	public boolean affectsBall(Ball ball, Game game)
	{
		if (targetSelection == Target.all)
		{
			ArrayList<Ball> balls = getAllBalls(game);

			if (balls.contains(ball)) return true;
		}
		else if (getBall(game, null) == ball) { return true; }

		return false;
	}

	public boolean targetIsDead(Game game)
	{
		ArrayList<Ball> allBalls = getAllBalls(game);
		int numberOfBalls = allBalls.size();

		ArrayList<Point> allPoints = getAllPoints(game);

		for (int i = 0; i < allBalls.size(); i++)
		{
			if (!game.balls.get(allBalls.get(i).faction).contains(allBalls.get(i)))
			{
				allBalls.remove(i);

				i--;
			}
		}

		if (allBalls.size() == 0 && allPoints.size() - numberOfBalls == 0) { return true; }
		if (targetDefinition == null)
		{
			return true;
		}
		else if (targetDefinition.getClass() == ArrayList.class)
		{
			@SuppressWarnings("unchecked")
			ArrayList<Object> targetArrayList = (ArrayList<Object>) targetDefinition;

			if (targetArrayList.size() == 0) { return true; }
		}

		return false;
	}
}
