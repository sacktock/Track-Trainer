package alexw.classes;

import alexw.classes.MathFunc;

/**
 * Created by alexw on 5/5/2017.
 */

public class DiscreteDistribution {

    //Class defines a discrete probability distribution
    private int[] valueX;
    private double[] PXisx;

    public double[] getPXisx() {
        return PXisx;
    }

    public DiscreteDistribution(int[] valueArray, double[] distributionArray){
        //Constructor
        valueX = valueArray;
        PXisx = distributionArray;
    }

    public DiscreteDistribution(int[] valueArray){
        //Constructor makes a uniform probability distribution
        //all values of X or values in the valueArray have equal
        //probability of being returned from getX() function
        PXisx = new double[valueArray.length];
        for (int i = 0; i < valueArray.length; i++) {
            double d = valueArray.length;
            double x = 1;
            PXisx[i] = x/d;
        }
        this.valueX = valueArray;
    }

    public int getX(){
        //getX() function returns a random value x for X
        //Values are defined in the array valueX[]
        //Probability of each value being randomly returned is defined in the array PXisx[]
        int randomNumber = MathFunc.getRandomNumber(0,100);
        double probability = 0;
        for (int i = 0; i < PXisx.length; i++) {
            probability = probability + PXisx[i];
            if ((probability * 100) >= randomNumber){
                return valueX[i];
            }
        }
        return valueX[valueX.length-1];
    }

    public double probabilityTotal(){
        //Returns the probability total
        //This should always return 1 if the PXisx[] array is defined correctly
        double d = 0;
        for (int i = 0; i < PXisx.length; i++) {
            d = d + PXisx[i];
        }
        return d;
    }

    public double getExpectedValue(){
        //Returns the mean of the discrete probability distribution function
        double EX = 0;
        for(int x = 0; x <= valueX.length; x = x+1){
            EX = EX + valueX[x] * PXisx[x];
        }
        return EX;
    }
}
