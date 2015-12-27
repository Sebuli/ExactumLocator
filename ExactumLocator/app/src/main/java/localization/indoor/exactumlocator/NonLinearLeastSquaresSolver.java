package localization.indoor.exactumlocator;

import org.apache.commons.math3.fitting.leastsquares.LeastSquaresFactory;
import org.apache.commons.math3.fitting.leastsquares.LeastSquaresOptimizer;
import org.apache.commons.math3.fitting.leastsquares.LeastSquaresOptimizer.Optimum;
import org.apache.commons.math3.fitting.leastsquares.LeastSquaresProblem;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.DiagonalMatrix;

public class NonLinearLeastSquaresSolver {

	protected final TrilaterationFunction function;
	protected final LeastSquaresOptimizer leastSquaresOptimizer;

	protected final static int MAXNUMBEROFITERATIONS = 1000;

	public NonLinearLeastSquaresSolver(TrilaterationFunction function, LeastSquaresOptimizer leastSquaresOptimizer) {
		this.function = function;
		this.leastSquaresOptimizer = leastSquaresOptimizer;
	}

	public Optimum solve(double[] target, double[] weights, double[] initialPoint, boolean debugInfo) {
		if (debugInfo) {
			System.out.println("Max Number of Iterations : " + MAXNUMBEROFITERATIONS);
		}

		LeastSquaresProblem leastSquaresProblem = LeastSquaresFactory.create(
				function,
				new ArrayRealVector(target, false), new ArrayRealVector(initialPoint, false), new DiagonalMatrix(weights), null, MAXNUMBEROFITERATIONS, MAXNUMBEROFITERATIONS);

		return leastSquaresOptimizer.optimize(leastSquaresProblem);
	}

	public Optimum solve(double[] target, double[] weights, double[] initialPoint) {
		return solve(target, weights, initialPoint, false);
	}

	public Optimum solve(boolean debugInfo) {
		int numberOfPositions = function.getPositions().length;
		int positionDimension = function.getPositions()[0].length;

		double[] initialPoint = new double[positionDimension];
		for (int i = 0; i < function.getPositions().length; i++) {
			double[] vertex = function.getPositions()[i];
			for (int j = 0; j < vertex.length; j++) {
				initialPoint[j] += vertex[j];
			}
		}
		for (int j = 0; j < initialPoint.length; j++) {
			initialPoint[j] /= numberOfPositions;
		}

		if (debugInfo) {
			StringBuilder output = new StringBuilder("initialPoint: ");
			for (int i = 0; i < initialPoint.length; i++) {
				output.append(initialPoint[i]).append(" ");
			}
			System.out.println(output.toString());
		}

		double[] target = new double[numberOfPositions];
		double[] distances = function.getDistances();
		double[] weights = new double[target.length];
		for (int i = 0; i < target.length; i++) {
			target[i] = 0.0;
			weights[i] = (distances[i] * distances[i]);
		}

		return solve(target, weights, initialPoint, debugInfo);
	}

	public Optimum solve() {
		return solve(false);
	}
}