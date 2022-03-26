import java.util.Locale;

public class ModelFactory {
    public Model getModel(ModelType modelName){
        switch (modelName.Name.toLowerCase()){
            case "gbmmc":
                return new BrownianMotionMC();
            case "bsmodel":
                return new BlackScholesModel();
            default:
                throw new IllegalArgumentException("No such argument" + modelName.Name);
        }
    }
}
