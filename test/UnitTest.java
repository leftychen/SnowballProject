import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.CountDownLatch;
import java.time.LocalDate;


public class UnitTest {
    @Test
    void testBSModel()throws Exception{
        ClosedFormModel m = new BlackScholesModel();
        var price = m.getPrice(100, 100, 1, 0.3, 0.01, 0.015, Option.OptionType.Call);
        var delta = m.getGreeks(Model.GreeksType.Delta, 100.0, 100.0, 1.0, 0.3, 0.01, 0.015, 0.01, Option.OptionType.Call);
        Assertions.assertEquals(Math.round(price*10)/10.0, 11.5);
        Assertions.assertEquals(Math.round(delta * 100)/100.0, 0.54);
    }
    @Test
    void testGenerateResetDates() throws Exception{
        double notional = 1;
        double spot = 6226.42;
        LocalDate tradeDate = LocalDate.parse("2021-11-11");
        LocalDate expDate = LocalDate.parse("2022-11-11");
        double knockoutPct = 1;
        double knockinPct = 0.8;
        double snowballRt = 0.166;
        SnowBallOption snowball = new SnowBallOption(notional, spot, tradeDate, expDate,knockoutPct, knockinPct,snowballRt);
        Assertions.assertEquals(snowball.ResetDates.size(), 9);
    }

    @Test
    void testPathGenerate() throws Exception{
        double notional = 1;
        double spot = 6226.42;
        LocalDate tradeDate = LocalDate.parse("2021-11-11");
        LocalDate expDate = LocalDate.parse("2022-11-11");
        double knockoutPct = 1;
        double knockinPct = 0.8;
        double snowballRt = 0.166;
        double rf = 0.01;
        // Assume average return of ZhongZheng 500 is 24.5%
        double mu = 0.245;
        double q = 0.089;
        double vol = 0.151;
        int simCnt = 1000;
        int simDays = 365;
        double dt = 1/365.0;
        Model.GreeksType[] types = new Model.GreeksType[]{Model.GreeksType.Price};
        SnowBallOption snowball = new SnowBallOption(notional, spot, tradeDate, expDate,knockoutPct, knockinPct,snowballRt);
        MonteCarloSim mc = new BrownianMotionMC();
        mc.generatePath(tradeDate, spot, vol, mu-q, rf,simCnt, simDays, types, 0.01, dt, true);
        Assertions.assertNotEquals(mc.Paths, null);
        Assertions.assertNotEquals(mc.Paths[0].getEntry(500,185),0);
        Assertions.assertNotEquals(mc.Paths[0].getEntry(500,185),null);
    }

    @Test
    void testSnowBallBSModel() throws Exception{
        double notional = 1;
        double spot = 6226.42;
        LocalDate tradeDate = LocalDate.parse("2021-11-11");
        LocalDate expDate = LocalDate.parse("2022-11-11");
        double knockoutPct = 1;
        double knockinPct = 0.8;
        double snowballRt = 0.166;
        double rf = 0.01;
        // Assume average return of ZhongZheng 500 is 24.5%
        double mu = 0.245;
        double q = 0.089;
        double vol = 0.151;
        int simCnt = 1000;
        int simDays = 365;
        double dt = 1/365.0;
        Model.GreeksType[] types = new Model.GreeksType[]{Model.GreeksType.Price};
        SnowBallOption snowball = new SnowBallOption(notional, spot, tradeDate, expDate,knockoutPct, knockinPct,snowballRt);
        ClosedFormModel m = new BlackScholesModel();
        var price = m.getPrice(spot, snowball.KnockOut, 1, vol, rf, q, Option.OptionType.Put);
        System.out.println("Snow Ball BS Model Price is " + price/snowball.SpotUsed);
    }

    @Test
    void testSnowballMCPrice() throws Exception{
        final ExecutorService excutor = Executors.newFixedThreadPool(5);
        CountDownLatch cdl = new CountDownLatch(2000);
        long startTime = System.nanoTime();
        double notional = 1;
        double spot = 6226.42;
        LocalDate tradeDate = LocalDate.parse("2021-11-11");
        LocalDate expDate = LocalDate.parse("2022-11-11");
        double knockoutPct = 1;
        double knockinPct = 0.8;
        double snowballRt = 0.166;
        double rf = 0.01;
        // Assume average return of ZhongZheng 500 is 24.5%
        double mu = 0.245;
        double q = 0.089;
        double vol = 0.151;
        int simCnt = 10000;
        int simDays = 365;
        double dt = 1/365.0;
        Model.GreeksType[] types = new Model.GreeksType[]{Model.GreeksType.Price};
        SnowBallOption snowball = new SnowBallOption(notional, spot, tradeDate, expDate,knockoutPct, knockinPct,snowballRt);
        MonteCarloSim mc = new BrownianMotionMC();
        mc.generatePath(tradeDate, spot, vol, mu-q, rf,simCnt, simDays, types, 0.01, dt, true);
        double price = 0;

        for(int i = 1;i<= 2000;i++) {
            excutor.submit(new Runnable() {
                @Override
                public void run() {
                    Thread t = Thread.currentThread();
                    mc.getPrice(tradeDate, (PayoffScripts) snowball, rf, snowball.ResetDates);
                    cdl.countDown();
                }
            });

        }
        cdl.await();
        excutor.shutdown();
        //System.out.println("SnowBall MC price is " + price);
        long endTime = System.nanoTime();
        System.out.println("total running time" +  (endTime - startTime)/1000000000);
        // Analysis
        int knockOut = 0;
        int knockIn = 0;
        int others = 0;
        for (int i = 0; i < simCnt; i++){
            if(snowball.KnockInOutStat[i] == 1){
                knockOut += 1;
            }
            else if(snowball.KnockInOutStat[i] == -1){
                knockIn += 1;
            }
            else{
                others += 1;
            }
        }
        System.out.println(knockOut / (double)simCnt + " Pct of Simulations are knockout");
        System.out.println(knockIn / (double)simCnt + " Pct of Simulations are knockin");
        System.out.println(others / (double)simCnt + " Pct of Simulations are within knock in and knock out");
    }
    @Test
    void testSnowballMCDelta() throws Exception{
        double notional = 1;
        double spot = 6226.42;
        LocalDate tradeDate = LocalDate.parse("2021-11-11");
        LocalDate expDate = LocalDate.parse("2022-11-11");
        double knockoutPct = 1;
        double knockinPct = 0.8;
        double snowballRt = 0.166;
        double rf = 0.01;
        // Assume average return of ZhongZheng 500 is 24.5%
        double mu = 0.245;
        double q = 0.089;
        double vol = 0.151;
        int simCnt = 10000;
        int simDays = 365;
        double dt = 1/365.0;
        Model.GreeksType[] types = new Model.GreeksType[]{Model.GreeksType.Price, Model.GreeksType.Delta};
        SnowBallOption snowball = new SnowBallOption(notional, spot, tradeDate, expDate,knockoutPct, knockinPct,snowballRt);
        MonteCarloSim mc = new BrownianMotionMC();
        mc.generatePath(tradeDate, spot, vol, mu-q, rf,simCnt, simDays, types, 0.01, dt, true);
        Assertions.assertNotEquals(mc.Paths[1].getEntry(500,185),0);
        Assertions.assertNotEquals(mc.Paths[2].getEntry(500,185),0);
        double delta = mc.getGreeks(tradeDate, Model.GreeksType.Delta, (PayoffScripts) snowball, rf,snowball.ResetDates,
                0.01, spot);
        System.out.println("SnowBall MC delta is " + delta);
    }
}
