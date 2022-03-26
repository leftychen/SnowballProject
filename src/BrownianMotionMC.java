import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;


public class BrownianMotionMC extends MonteCarloSim {
    // should add more path for other Greeks
    public BrownianMotionMC() {
    }
        @Override
        protected RealMatrix generatePathHelper(double spot, double vol, double mu,
                                                double fwd, int simPathCnt, int simDays, double dt) {
            double numPath [][] = new double [simPathCnt][simDays];
            RealMatrix path = new Array2DRowRealMatrix(numPath);
            double firstDayIndex[] = new double[simPathCnt];
            for (int i = 0; i < simPathCnt; i++) {
                firstDayIndex[i] = spot;
            }
            path.setColumn(0, firstDayIndex);
            // Generate random numbers

            for (int d = 1; d < simDays; d++) {
                double[] randArr = ModelUtil.genRandomNumber(simPathCnt, ModelUtil.RandomNumberType.Gaussian);
                RealVector randVec = new ArrayRealVector(randArr);
                RealVector col = path.getColumnVector(d - 1);

                // applied Brownian motion
                RealVector newCol = col.add(col.mapMultiply(mu * dt).add(col.mapMultiply(vol).ebeMultiply(randVec)
                        .mapMultiply(Math.sqrt(dt))));
                path.setColumnVector(d, newCol);
            }
           return path;
        }


}





