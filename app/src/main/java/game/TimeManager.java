package game;

public class TimeManager
{

	public double timeSpeed = 1;

	public double timeDistortion;

	public void updateDistortion(double factor)
	{
		if (timeSpeed != 1)
		{
			timeDistortion += 0.002 * factor;
		}
		else
		{
			timeDistortion -= 0.0008;
		}

		if (timeDistortion > 1) timeDistortion = 1;
		else if (timeDistortion < 0) timeDistortion = 0;
	}

	public boolean isActiveFrame(int playtime)
	{
		if (playtime % (int) (1 / timeSpeed) == 0) { return true; }

		return false;
	}

	public boolean totalDistortion()
	{
		if (timeDistortion >= 1) { return true; }

		return false;
	}
}
