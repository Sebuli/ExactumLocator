package localization.indoor.exactumlocator;

import org.apache.commons.math3.fitting.leastsquares.MultivariateJacobianFunction;
import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;
import org.apache.commons.math3.util.Pair;


public class TrilaterationFunction implements MultivariateJacobianFunction {

	protected static final double epsilon = 1E-7;

	protected final double positions[][];

	protected final double distances[];

	public TrilaterationFunction(double positions[][], double distances[]) {
		
		if(positions.length < 2) {
			throw new IllegalArgumentException("Need at least two positions.");
		}
		
		if(positions.length != distances.length) {
			throw new IllegalArgumentException("The number of positions you provided, " + positions.length + ", does not match the number of distances, " + distances.length + ".");
		}

		for (int i = 0; i < distances.length; i++) {
			distances[i] = Math.max(distances[i], epsilon);
		}
		
		int positionDimension = positions[0].length;
		for (int i = 1; i < positions.length; i++) {
			if(positionDimension != positions[i].length) {
				throw new IllegalArgumentException("The dimension of all positions should be the same.");
			}
		}
		
		this.positions = positions;
		this.distances = distances;
	}

	public final double[] getDistances() {
		return distances;
	}

	public final double[][] getPositions() {
		return positions;
	}

	public RealMatrix jacobian(RealVector point) {
		double[] pointArray = point.toArray();

		double[][] jacobian = new double[distances.length][pointArray.length];
		for (int i = 0; i < jacobian.length; i++) {
			for (int j = 0; j < pointArray.length; j++) {
				jacobian[i][j] = 2 * pointArray[j] - 2 * positions[i][j];
			}
		}

		return new Array2DRowRealMatrix(jacobian);
	}

	@Override
	public Pair<RealVector, RealMatrix> value(RealVector point) {

		double[] pointArray = point.toArray();
		double[] resultPoint = new double[this.distances.length];

		for (int i = 0; i < resultPoint.length; i++) {
			resultPoint[i] = 0.0;
			for (int j = 0; j < pointArray.length; j++) {
				resultPoint[i] += (pointArray[j] - this.getPositions()[i][j]) * (pointArray[j] - this.getPositions()[i][j]);
			}
			resultPoint[i] -= (this.getDistances()[i]) * (this.getDistances()[i]);
		}

		RealMatrix jacobian = jacobian(point);
		return new Pair<RealVector, RealMatrix>(new ArrayRealVector(resultPoint), jacobian);
	}
}
