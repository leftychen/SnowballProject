import org.apache.commons.math3.distribution.NormalDistribution;

public class BlackScholesModel extends ClosedFormModel{
    // pricing option with vannilla black scholes model
    // reference: https://en.wikipedia.org/wiki/Black–Scholes_model
    @Override
    public double getPrice(double spot, double strike, double ttm, double vol, double rf, double div,
                           Option.OptionType type) throws IllegalArgumentException{
        double forwardSpot = spot * Math.exp((rf - div) * ttm);
        double d_1 = 1/(Math.sqrt(ttm) * vol) * (Math.log(spot / strike) + (rf - div + 0.5 * Math.pow(vol,2)) * ttm);
        double d_2 = d_1 - vol * Math.sqrt(ttm);
        var normDist = new NormalDistribution();

        switch (type){
            case Call:
                var Nd1 = normDist.cumulativeProbability(d_1);
                var Nd2 = normDist.cumulativeProbability(d_2);
                var price = Math.exp(-rf * ttm) * ((forwardSpot * Nd1) - strike * Nd2);
                return price;
            case Put:
                Nd1 = normDist.cumulativeProbability(-d_1);
                Nd2 = normDist.cumulativeProbability(-d_2);
                price = Math.exp(-rf * ttm) * (-(forwardSpot * Nd1) + strike * Nd2);
                return price;
            default:
                throw new IllegalArgumentException("type" + type.name() + " hasn't implemented");
        }

    }
}
