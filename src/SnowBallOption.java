import org.apache.commons.math3.linear.RealMatrix;

import java.time.Duration;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SnowBallOption extends Option implements PayoffScripts{
    public double KnockOut;
    public double KnockIn;
    public double Dividend;
    public int[] KnockInOutStat;
    List<LocalDate> ResetDates;

    public SnowBallOption(double notional, double spotUsed, LocalDate tradeDate, LocalDate expDate,
                          double knockOutPct, double knockInPct, double div){
        super(notional, spotUsed, tradeDate,expDate);
        this.Dividend = div;
        this.KnockIn = knockInPct * this.SpotUsed;
        this.KnockOut = knockOutPct * this.SpotUsed;
        this.generateResetDates(this.TradeDate);


    }
    private void generateResetDates(LocalDate tradeDate){
        LocalDate startReset = tradeDate.plusMonths(4);
        this.ResetDates = Stream.iterate(startReset, date -> date.plusMonths(1)).limit((long)this.Term * 12-3).collect(Collectors.toList());
    }
    @Override
    public double mcPayoff(LocalDate runDate, List<LocalDate> PathDates, RealMatrix path, double rf, List<LocalDate> resetDates) {
        int rowCnt = path.getRowDimension();
        int colCnt = path.getColumnDimension();
        double[] payoff = new double[rowCnt];
        this.KnockInOutStat = new int[rowCnt];
        for(int d = 0; d < colCnt; d++){
            for(int sim = 0; sim < rowCnt; sim++){
                LocalDate curDate = PathDates.get(d);
                double indexLevel = path.getEntry(sim, d);
                // Knock out
                if (indexLevel >= this.KnockOut && resetDates.contains(curDate)){
                    this.KnockInOutStat[sim] = 1;
                    long timeDuration = Duration.between(this.TradeDate.atStartOfDay(), curDate.atStartOfDay()).toDays();
                    payoff[sim] = timeDuration/365.0 * this.Notional * this.Dividend * Math.exp(-rf * timeDuration/365.0);
                }
                //Knock in
                if(indexLevel <= this.KnockIn && this.KnockInOutStat[sim] != 1){
                    this.KnockInOutStat[sim] = -1;
                }
            }
        }
        double total = 0;
        double ttm = Duration.between(runDate.atStartOfDay(), this.ExpirationDate.atStartOfDay()).toDays()/365.0;
        for(int i = 0; i < rowCnt; i++){
            if(this.KnockInOutStat[i] == 0){
                total += this.Dividend * 360/365.0 * Math.exp(-rf * ttm);
            }
            else if(this.KnockInOutStat[i] == -1){
                double endIndexLevel = path.getEntry(i,colCnt-1);
                total += (endIndexLevel/this.SpotUsed - 1) * Math.exp(-rf * ttm);
            }
            else{
                total += payoff[i];
            }
        }

        return total/payoff.length;

    }
}
