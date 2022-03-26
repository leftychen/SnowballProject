import java.time.Duration;
import java.time.LocalDate;

public class Option extends Derivative{
    public enum OptionType{Call, Put}
    public double StrikeLevel;
    public double Term;
    public Option(){
        this.Product = ProductType.Option;
    }
    public Option(double units, double notional, double spotUsed, LocalDate tradeDate, LocalDate expDate,
                  double strikeLevel){
        super(units, notional, spotUsed, tradeDate,expDate,ProductType.Option);
        this.StrikeLevel = strikeLevel;
    }
    public Option(double notional, double spotUsed, LocalDate tradeDate, LocalDate expDate){
        super(0, notional, spotUsed, tradeDate,expDate,ProductType.Option);
        this.Term = Duration.between(tradeDate.atStartOfDay(), expDate.atStartOfDay()).toDays() /365;
    }
}


