import java.util.Date;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class ModelUtil {
    public enum RandomNumberType{Gaussian}
    public static double[] genRandomNumber(int size, RandomNumberType t){
        switch (t) {
            case Gaussian:
                Random r = new Random();
                double[] randomVec = new double[size];
                for (int i = 0; i < size; i++) {
                    randomVec[i] = r.nextGaussian();
                }
            return randomVec;
            default:
                throw new IllegalArgumentException("No such argument" + t.name());

        }
    }

    public static long calDateDuration(Date startDate, Date endDate){
        return TimeUnit.DAYS.convert(endDate.getTime() - startDate.getTime(), TimeUnit.MILLISECONDS);
    }
}
