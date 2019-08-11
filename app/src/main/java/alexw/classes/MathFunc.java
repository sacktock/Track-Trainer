package alexw.classes; /**
 * Created by alexw on 5/5/2017.
 */
import java.util.ArrayList;
import java.util.Random;

import alexw.classes.RegressionLine;

public class MathFunc {

    public static int getRandomNumber(int LowerBound, int UpperBound){
        int n;
        Random rand = new Random();
        n = rand.nextInt(UpperBound-LowerBound +1) + LowerBound;
        return n;
    }

    public static boolean isNotZero(int x){
        if (x == 0) {
            return false;
        }else{
            return true;
            }
    }

    public static boolean isInteger(String s) {
        //Returns true if a given string can be parsed into an integer
        try {
            Integer.parseInt(s);
        } catch(NumberFormatException | NullPointerException e) {
            return false;
        }
        return true;
    }

    public static double getMean(double[] numberarray){
        return getSx(numberarray) / numberarray.length;
    }

    private static double getSx(double[] numberarray){
        //Used for regression line instantiation
        double Sx = 0;
        for(int x = 0; x < numberarray.length; x = x+1){
            Sx = Sx + numberarray[x];
        }
        return Sx;
    }

    private static double getSxSquare(double[] numberarray){
        //Used for regression line instantiation
        double SxSquare = 0;
        double num;
        for(int x = 0; x < numberarray.length; x = x+ 1){
            num = numberarray[x];
            SxSquare = SxSquare  + (num * num);
        }
        return SxSquare;
    }

    private static double getSxx(double[] numberarray){
        //Used for regression line instantiation
        double num = getSx(numberarray);
        return getSxSquare(numberarray) - (num * num) / numberarray.length;
    }

    private static double getSigmaXY(double[] numberXarray, double[] numberYarray){
        //Used for regression line instantiation
        double SigmaXY = 0;
        for(int x = 0; x < numberXarray.length; x = x+1){
            SigmaXY = SigmaXY + (numberXarray[x] * numberYarray[x]);
        }
        return SigmaXY;
    }

    public static double calculateCorrelationCoefficient(double[] numberXarray, double[] numberYarray){
        //Used to calculate the correlation between 2 arrays of doubles
        return getSxy(numberXarray, numberYarray) / Math.sqrt(getSxx(numberXarray) * getSxx(numberYarray));
    }

    private static double getSxy(double[] numberXarray, double[] numberYarray){
        //Used for regression line instantiation
        return getSigmaXY(numberXarray, numberYarray) -
                ((getSx(numberXarray) * getSx(numberYarray)) / numberXarray.length);
    }

    public static RegressionLine createRegressionLine(double[] numberXarray, double[] numberYarray) {
        //Creates a regression line object from 2 arrays of doubles
       return new RegressionLine(getSxx(numberXarray),
                getSxy(numberXarray, numberYarray), numberXarray.length, getSx(numberYarray), getSx(numberXarray));
    }

    public static double[] redefineArray(double[] array, int length){
        return java.util.Arrays.copyOf(array, length);
    }

    public static double arrayTotal(double[] array){
        double d = 0;
        for (double anArray : array) {
            d = d + anArray;
        }
        return d;
    }
}

