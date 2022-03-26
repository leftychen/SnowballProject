import java.time.LocalDate;

public class Derivative {
    public enum ProductType{Option};
    public double Units = 0;
    public double Notional = 0;
    public double SpotUsed = 0;
    public LocalDate TradeDate;
    public LocalDate ExpirationDate;
    public ProductType Product;

    public Derivative(){

    }
    public Derivative(double units, double notional, double spotUsed, LocalDate tradeDate, LocalDate expDate, ProductType product){
        this.Units = units;
        this.Notional = notional;
        this.SpotUsed = spotUsed;
        this.TradeDate = tradeDate;
        this.ExpirationDate = expDate;
        this.Product = product;
    }

}
