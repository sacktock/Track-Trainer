package alexw.classes;

/**
 * Created by alexw on 10/4/2017.
 */

public class Account_Stats {

    private String ID;
    private double multiplier;
    private double V4Speed;
    private double lactateThreshold;
    private double VO2Max;
    private double recoveryRate;
    //Private attributes of account stats entity

    public String getID() {
        return ID;
    }
    public double getMultiplier() {
        return multiplier;
    }
    public double getLactateThreshold() {
        return lactateThreshold;
    }
    public double getRecoveryRate() {
        return recoveryRate;
    }
    public double getV4Speed() {
        return V4Speed;
    }
    public double getVO2Max() {
        return VO2Max;
    }

    public void setMultiplier(double multiplier){
        this.multiplier = multiplier;
    }

    public Account_Stats(String ID,double multiplier, double V4Speed, double lactateThreshold,
                         double VO2Max, double recoveryRate){
        //Constructor
        this.ID = ID;
        this.multiplier = multiplier;
        this.V4Speed = V4Speed;
        this.lactateThreshold = lactateThreshold;
        this.VO2Max = VO2Max;
        this.recoveryRate = recoveryRate;
    }
}
