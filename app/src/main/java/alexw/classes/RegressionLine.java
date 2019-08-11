package alexw.classes;

/**
 * Created by alexw on 5/5/2017.
 */

public class RegressionLine {

    //Class that defines a regression line
    private double constantA;
    private double gradientB;
    private double standardDeviation;

    public double getConstantA(){return constantA;}
    public double getGradientB() {return gradientB;}

    RegressionLine(double Sxx, double Sxy, int n, double SY, double SX){
        //Constructor
        standardDeviation = Math.sqrt(Sxy/n);
        gradientB = Sxy / Sxx;
        constantA =( SY / n) - (SX / n) * gradientB;
    }

    public double getY(double x){
        return constantA + (gradientB * x);
    };
    public double getX(double y){
        //Inverse function
        return (y - constantA) / gradientB;
    }
    public double getStandardDeviation(){return standardDeviation;}
}
