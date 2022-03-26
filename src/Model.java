import org.apache.commons.math3.linear.RealMatrix;

import java.time.LocalDate;
import java.util.List;

public class Model {
    public enum GreeksType{Price, Delta, Gamma, Vega, Rho, Theta};
    public  double getPrice(double spot, double strike, double ttm, double vol, double rf, double div, Option.OptionType type) throws Exception{
        return 0;
    }
    public double getGreeks(GreeksType type, double spot, double strike, double ttm, double vol,
                            double rf, double div, double shockRate, Option.OptionType callput) throws Exception{
        return 0;
    }
    public double getGreeks(LocalDate runDate, GreeksType type, PayoffScripts script, double rf, List<LocalDate> resetDates, double shockRate, double spot) throws Exception {
        return 0;
    }
    public double getPrice(LocalDate runDate, PayoffScripts script,double rf,RealMatrix path, List<LocalDate> resetDates) throws Exception {
        return 0;
    }
    public double getPrice(LocalDate runDate, PayoffScripts script,double rf,List<LocalDate> resetDates) throws Exception {
        return 0;
    }

}
