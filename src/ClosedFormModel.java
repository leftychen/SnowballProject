public abstract class ClosedFormModel extends Model {
/* Abstract class to hold all of closed form models, such as BS model or TurnBull Wakeman model
Other greeks will be implemented as requested
 */

    @Override
    public double getGreeks(GreeksType type, double spot, double strike, double ttm, double vol,
                            double rf, double div, double shockRate, Option.OptionType callput) throws Exception {
        switch (type){
            case Delta:
                var priceUp = this.getPrice(spot * (1 + shockRate), strike, ttm, vol, rf, div, callput);
                var priceDn = this.getPrice(spot * (1 - shockRate), strike, ttm, vol, rf, div,callput);
                var delta = (priceUp - priceDn) / (2 * shockRate * spot);
                return delta;
            case Gamma:
                priceUp = this.getPrice(spot * (1 + shockRate), strike, ttm, vol, rf, div,callput);
                priceDn = this.getPrice(spot * (1 - shockRate), strike, ttm, vol, rf, div, callput);
                var price = this.getPrice(spot, strike, ttm, vol, rf, div,callput);
                var gamma = (priceUp + priceDn - 2 * price) / Math.pow(spot * shockRate, 2);
                return gamma;
            case Theta:
                throw new NoSuchMethodException("Theta hasn't been implemented yet");
            case Vega:
                throw new NoSuchMethodException("Vega hasn't been implemented yet");
            case Rho:
                throw new NoSuchMethodException("Rho hasn't been implemented yet");
            default:
                throw new IllegalArgumentException("No such argument" + type.name());
        }
    }
}

