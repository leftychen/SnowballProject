import org.apache.commons.math3.linear.RealMatrix;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class MonteCarloSim extends Model {
    protected RealMatrix[] Paths;
    public List<LocalDate> SimDates;
    protected abstract RealMatrix generatePathHelper(double spot, double vol, double mu, double fwd, int simPathCnt, int simDays,double dt);
    protected void generatePath(LocalDate runDate, double spot, double vol, double mu, double fwd, int simPathCnt, int simDays, GreeksType [] types,
                                double shockRate, double dt, boolean regenerate) throws NoSuchMethodException {
        if(regenerate){
            this.Paths = null;
        }

        if (this.Paths == null){
            var valTypeCnt = GreeksType.values().length;
            // each greek has two paths except price path requires only one
            this.Paths = new RealMatrix[valTypeCnt * 2 - 1];
        }
        else{
            return;
        }
        // initialize
        this.SimDates = Stream.iterate(runDate, date -> date.plusDays(1)).limit(simDays).collect(Collectors.toList());
        for(int t = 0; t < types.length; t++) {
            switch (types[t]) {
                case Price:
                    this.Paths[0] = this.generatePathHelper(spot, vol, mu, fwd, simPathCnt, simDays, dt);
                    break;
                case Delta:
                    this.Paths[1] = this.generatePathHelper(spot * (1 + shockRate), vol, mu, fwd, simPathCnt, simDays, dt);
                    this.Paths[2] = this.generatePathHelper(spot * (1 - shockRate), vol, mu, fwd, simPathCnt, simDays, dt);
                    break;
                case Gamma:
                    if (this.Paths[1].getEntry(0,0) == 0){
                        this.Paths[1] = this.generatePathHelper(spot * (1 + shockRate), vol, mu, fwd, simPathCnt, simDays, dt);
                        this.Paths[2] = this.generatePathHelper(spot * (1 - shockRate), vol, mu, fwd, simPathCnt, simDays, dt);
                }
                    break;
                case Theta:
                    throw new NoSuchMethodException("Theta hasn't been implemented yet");
                case Vega:
                    throw new NoSuchMethodException("Vega hasn't been implemented yet");
                case Rho:
                    throw new NoSuchMethodException("Rho hasn't been implemented yet");
                default:
                    throw new IllegalArgumentException("No such argument" + types[t].name());
            }
        }
    }

    @Override
    public double getPrice(LocalDate runDate, PayoffScripts script,double rf,RealMatrix path,List<LocalDate> resetDates) {
        return script.mcPayoff(runDate, this.SimDates, path,rf, resetDates);

    }

    @Override
    public double getPrice(LocalDate runDate, PayoffScripts script,double rf,List<LocalDate> resetDates) {
        return script.mcPayoff(runDate, this.SimDates, this.Paths[0],rf, resetDates);

    }

    @Override
    public double getGreeks(LocalDate runDate, GreeksType type, PayoffScripts script,
                           double rf, List<LocalDate> resetDates, double shockRate, double spot) throws NoSuchMethodException {
        // Here assume path number 0 is no shock path, path number 1 is index shock up path, path number 2 is
        // index shock dn path, for other paths, will keep clarifying with future implementations
        switch (type) {
            case Delta:
                var priceUp = this.getPrice(runDate,script,rf,this.Paths[1],resetDates);
                var priceDn = this.getPrice(runDate,script,rf,this.Paths[2],resetDates);
                var delta = (priceUp - priceDn) / (2 * shockRate);
                return delta;
            case Gamma:
                priceUp = this.getPrice(runDate,script,rf,this.Paths[1],resetDates);
                priceDn = this.getPrice(runDate,script,rf,this.Paths[2],resetDates);
                var price = this.getPrice(runDate,script,rf,this.Paths[0],resetDates);
                var gamma = (priceUp + priceDn - 2 * price) / Math.pow(shockRate, 2);
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