import org.apache.commons.math3.linear.RealMatrix;

import java.time.LocalDate;
import java.util.List;

public interface PayoffScripts {
    public double mcPayoff(LocalDate runDate, List<LocalDate> PathDates, RealMatrix path, double rf, List<LocalDate> resetDates);
}
