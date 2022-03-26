public class Calibration {
    Model ModelInstance;
    ModelParams params;
    CalibrationEngine engine;
    public  enum CalibrationType{Global, Local}
    public Calibration(ModelType modelName){
        ModelFactory factory = new ModelFactory();
        this.ModelInstance = factory.getModel(modelName);
    }
    public void modelCalibrate(MarketData mktdata, CalibrationType type, CalibrationEngine engine){
        switch (type){
            case Local:
                // do local calibration
                break;
            case Global:
                // do global calibration
                break;
            default:
                throw new IllegalArgumentException("No such argument" + type.name());
        }

    }
}
