package test;


public class StatLib {
	// simple average
	public static float avg(float[] x){
		float sum = 0;
		int len = x.length;
		for(int i = 0; i < x.length; i++)
		{
			sum += x[i];
		}
		return sum / len;
	}
	// Variance is sum of squared differences from the mean divided by number of elements.
	// returns the variance of X and Y
	public static float var(float[] x){
		float mean = avg(x);
		float sum = 0;
		int n = x.length;
		for (int i = 0; i < n; i++) {
			sum += Math.pow((x[i] - mean), 2);
		}
		return sum / n;
	}
	//cov(X, Y) = sum [(xi - E(X))(yi - E(Y))] / (n)
	// returns the covariance of X and Y
	public static float cov(float[] x, float[] y){
		float meanX = avg(x);
		float meanY = avg(y);
		float sum = 0;
		int n = x.length;
		for (int i = 0; i < n; i++) {
			sum += (x[i] - meanX) * (y[i] - meanY);
		}
		return sum / n;
	}

	//Correlation coefficient is an equation that is used to determine the strength of relation between two variables.
	// Correlation coefficient always lies between -1 to +1 where -1 represents X and Y are negatively correlated
	// and +1 represents X and Y are positively correlated.
	// returns the Pearson correlation coefficient of X and Y
	public static float pearson(float[] x, float[] y){
		float covXY = cov(x, y);
		double rootVarXY = (Math.sqrt(var(x)) * Math.sqrt(var(y)));

		return (float) (covXY / rootVarXY);
	}

	// performs a linear regression and returns the line equation
	public static Line linear_reg(Point[] points){
		float a;
		float b;
		float[] x = new float[points.length];
		float[] y = new float[points.length];

		for (int i = 0; i < points.length; i++) {
			x[i]=  points[i].x;
			y[i]=  points[i].y;
		}
		a=cov(x,y)/var(x);
		b=avg(y)-a*avg(x);
		return new Line(a,b);
	}

	//Standard deviation = square root of ∑(Xi - ų)2 / N
	//The standard deviation is the measure of how spread out numbers are.
	// returns the deviation between point p and the line equation of the points
	public static float dev(Point p,Point[] points){
		float dist;
		Line line = linear_reg(points);
		float y2 = line.f(p.x);
		dist = (float)Math.abs(y2-p.y);
		return dist;
	}
	// returns the deviation between point p and the line
	public static float dev(Point p,Line l){
		float dist;
		float y2 = l.f(p.x);
		dist = (float)Math.abs(y2-p.y);
		return dist;
	}
}