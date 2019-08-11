package alexw.classes;

import java.util.ArrayList;

/**
 * Created by alexw on 10/17/2017.
 */

public class AthleteLactateModel {

    private static final double k = 40;
    private static final double baselineLactate = 1;
    private static final double lactateTime = 15;
    private static final double z = 20;
    private static final double f = 6;
    //Constants

    private double totalTime;
    private double avgLactate;
    private ExponentialModel lactateCurve;
    private double recoveryRate;
    private double lactate;
    private double lactateThreshold;
    //Variables

    public AthleteLactateModel(double V4Speed, double lactateThreshold, double VO2MAX, double recoveryRate){
        this.recoveryRate = recoveryRate;
        //Recovery rate is the rate of lactate dissipation when resting (mol/l/s)
        this.lactateThreshold = lactateThreshold;
        //lactateThreshold is the y value (mol/l) at which the rate of lactate generated
        //is greater than the rate of lactate dissipation

        //V4Speed determines the shift of the graph along the x axis

        //VO2Max is the value that determines the anaerobic systems relevance in generating power (ml/min/kg)
        //VO2Max determines the gradient of the graph
        double b = k / VO2MAX;
        double a = V4Speed;
        double d = baselineLactate - Math.pow(Math.E, -(b*a));
        //y intercept must always equal the baseline lactate
        this.lactateCurve = new ExponentialModel(a,b,1,d);
        //Where f(x) = c.e^(b(x-a)) + d
    }

    private void runDistance(int distance, double time){
        final double lactateLevel = lactateCurve.getY(distance / time);
        //lactateLevel is the level of lactate expected to be reached running at a particular speed
        if (lactateLevel > lactateThreshold) {
            //If lactateLevel is above the lactateThreshold, lactate could increase above the lactateLevel
            double LTime = (baselineLactate / lactate)* lactateTime;
            //LTime is calculated using current lactate and constants: baseline lactate and lactate time
            //LTime describes the time taken while running for the expected lactateLevel to be reached
            LinearModel LM1 = new LinearModel(new Point(0, lactate), new Point(LTime, lactateLevel));
            if (LTime > time){
                //If running for a shorter time than LTime then lactateLevel will not be reached
                //Lactate increases over time: (lactateLevel - lactate) / LTime
                for (int i = 0; i < time ; i++) {
                    lactate = LM1.getY(i);
                    avgLactate = avgLactate + (lactate/totalTime);
                }
            } else {
                //If running for longer than LTime then lactateLevel will be reached
                //and lactate will increase above the expected lactateLevel
                for (int i = 0; i < LTime ; i++) {
                    lactate = LM1.getY(i);
                    avgLactate = avgLactate + (lactate/totalTime);
                    //lactateLevel is reached when time = LTime
                    //before this lactate increases over time: (lactateLevel - lactate) / LTime
                }
                for (int i = 0; i < time - LTime ; i++) {
                    LinearModel LM2 = new LinearModel(((lactate - lactateThreshold) / z), lactateLevel);
                    lactate = LM2.getY(i);
                    avgLactate = avgLactate + (lactate/totalTime);
                    //Lactate increases above the expected lactateLevel and increases proportional to: (lactate - lactateThreshold)
                    //Given that lactate is increasing we have to re-define the linear model each iteration
                    //In effect this increase in lactate is not linear
                    //Constant z determines the extent of the rate of increase of lactate
                }
            }
        } else {
            //If lactateLevel is less than lactateThreshold lactate will never exceed the lactateThreshold
            double LTime = (baselineLactate / lactate)* lactateTime;
            LinearModel LM1 = new LinearModel(new Point(0, lactate), new Point(LTime, lactateLevel));

            if (LTime > time){
                //If running for a shorter time than LTime then lactateLevel will not be reached
                //Lactate increases over time exactly to: (lactateLevel - lactate) / LTime
                for (int i = 0; i < time ; i++) {
                    lactate = LM1.getY(i);
                    avgLactate = avgLactate + (lactate/totalTime);
                }
            } else {
                //If running for longer than LTime then lactateLevel will be reached
                for (int i = 0; i < LTime ; i++) {
                    lactate = LM1.getY(i);
                    avgLactate = avgLactate + (lactate/totalTime);
                }
                //When lactateLevel is reached lactate remains constant as lactateLevel is below lactateThreshold
                for (int i = 0; i < time - LTime ; i++) {
                    lactate = lactateLevel;
                    avgLactate = avgLactate + (lactate/totalTime);
                    //Lactate remains constant
                }
            }
        }
    }

    private void rest(double time){
        //Code to calculate the lactate level at end of rest
        double rise = (lactate - lactateThreshold) / f;
        //f used as a rate constant
        //If lactate is above lactateThreshold lactate rises slightly when resting
        double riseRate = rise / lactateTime;
        if (rise > 0) {
            //If rise is above 0 lactate increases over time, where rise defines the total rise in lactate
            for (int i = 0; i < time; i++) {
                if (i <= lactateTime) {
                    //Until time resting exceeds lactateTime lactate changes with a rate: (riseRate - recoveryRate)
                    lactate = lactate - recoveryRate + riseRate;
                } else {
                    //After lactateTime has been reached lactate decreases normally with a rate: recoveryRate
                    lactate = lactate - recoveryRate;
                }
                if (lactate < baselineLactate){
                    //Lactate can never decrease below the baseline level
                    lactate = baselineLactate;
                }
                avgLactate = avgLactate + (lactate/totalTime);
            }
        } else {
            //If rise is equal to 0 lactate simply decreases over time: recoveryRate
            for (int i = 0; i < time ; i++) {
                lactate = lactate - recoveryRate;
                if (lactate < baselineLactate){
                    //Lactate can never decrease below the baseline level
                    lactate = baselineLactate;
                }
                avgLactate = avgLactate + (lactate/totalTime);
            }
        }
    }

    public Account_Stats optimiseAccountStats(ArrayList<Object> trainingSets, Account_Stats accountStats, double estimatedLactate){
        double V4Speed;
        double LT;
        double VO2Max;
        double RR;

        ArrayList<double[]> solutions = new ArrayList<>();
        double[] solution = new double[4];

        //Set V4Speed and Lactate Threshold to their lower bound
        V4Speed = 2.85;
        LT = 3.00;
        RR = accountStats.getRecoveryRate();
        VO2Max = accountStats.getVO2Max();

        //Vary V4Speed and Lactate Threshold to find a solution
        for (int i = 0; i < 60; i++) {
            V4Speed = V4Speed + 0.05;
            LT = LT + 0.05;
            AthleteLactateModel varyModel = new AthleteLactateModel(V4Speed,LT, VO2Max, RR);
            double checkDifference = estimatedLactate - varyModel.avgLactateForTraining(trainingSets);
            //Solution is found if avg lactate for training is less than estimated lactate
            //but not greater than 1 unit less
            //This is used to find the most reasonable solution(s)
            if ((checkDifference > 0)&&(checkDifference < 1)){
                //Save solution
                solution[0] = V4Speed;
                solution[1] = LT;
                solution[2] = VO2Max;
                solution[3] = RR;
                solutions.add(solution);
            }
        }

        //Set V4Speed to its lower bound
        V4Speed = 2.85;
        LT = accountStats.getLactateThreshold();
        RR = accountStats.getRecoveryRate();
        VO2Max = accountStats.getVO2Max();

        //Vary V4Speed to find a solution
        for (int i = 0; i < 60; i++) {
            V4Speed = V4Speed + 0.05;
            AthleteLactateModel varyModel = new AthleteLactateModel(V4Speed,LT, VO2Max, RR);
            double checkDifference = estimatedLactate - varyModel.avgLactateForTraining(trainingSets);
            if ((checkDifference > 0)&&(checkDifference < 1)){
                //Save solution
                solution[0] = V4Speed;
                solution[1] = LT;
                solution[2] = VO2Max;
                solution[3] = RR;
                solutions.add(solution);
            }
        }

        //Set Lactate Threshold to its lower bound
        V4Speed = accountStats.getV4Speed();
        LT = 3.00;
        RR = accountStats.getRecoveryRate();
        VO2Max = accountStats.getVO2Max();

        //Vary Lactate Threshold to find a solution
        for (int i = 0; i < 60; i++) {
            LT = LT + 0.05;
            AthleteLactateModel varyModel = new AthleteLactateModel(V4Speed,LT, VO2Max, RR);
            double checkDifference = estimatedLactate - varyModel.avgLactateForTraining(trainingSets);
            if ((checkDifference > 0)&&(checkDifference < 1)){
                //Save solution
                solution[0] = V4Speed;
                solution[1] = LT;
                solution[2] = VO2Max;
                solution[3] = RR;
                solutions.add(solution);
            }
        }

        //Set V4Speed, Lactate Threshold and VO2Max to their lower bound
        V4Speed = 2.85;
        LT = 3.00;
        VO2Max = 20;
        RR = accountStats.getRecoveryRate();
        for (int i = 0; i < 60; i++) {
            //Vary V4Speed, Lactate Threshold and VO2Max
            V4Speed = V4Speed + 0.05;
            LT = LT + 0.05;
            VO2Max = VO2Max + 1.16;
            AthleteLactateModel varyModel = new AthleteLactateModel(V4Speed,LT, VO2Max, RR);
            double checkDifference = estimatedLactate - varyModel.avgLactateForTraining(trainingSets);
            if ((checkDifference > 0)&&(checkDifference < 1)){
                solution[0] = V4Speed;
                solution[1] = LT;
                solution[2] = VO2Max;
                solution[3] = RR;
                solutions.add(solution);
            }
        }

        //Set recovery rate to its lower bound
        RR = 0.05;
        V4Speed = accountStats.getV4Speed();
        LT = accountStats.getLactateThreshold();
        VO2Max = accountStats.getVO2Max();
        for (int i = 0; i < 15; i++) {
            RR = RR + 0.01;
            AthleteLactateModel varyModel = new AthleteLactateModel(V4Speed,LT, VO2Max, RR);
            double checkDifference = estimatedLactate - varyModel.avgLactateForTraining(trainingSets);
            if ((checkDifference > 0)&&(checkDifference < 1)){
                solution[0] = V4Speed;
                solution[1] = LT;
                solution[2] = VO2Max;
                solution[3] = RR;
                solutions.add(solution);
            }
        }

        //Set VO2Max to its lower bound
        VO2Max = 20;
        V4Speed = accountStats.getV4Speed();
        RR = accountStats.getRecoveryRate();
        LT = accountStats.getLactateThreshold();
        for (int i = 0; i < 60; i++) {
            VO2Max = VO2Max + 1.16;
            AthleteLactateModel varyModel = new AthleteLactateModel(V4Speed,LT, VO2Max, RR);
            double checkDifference = estimatedLactate - varyModel.avgLactateForTraining(trainingSets);
            if ((checkDifference > 0)&&(checkDifference < 1)){
                solution[0] = V4Speed;
                solution[1] = LT;
                solution[2] = VO2Max;
                solution[3] = RR;
                solutions.add(solution);
            }
        }

        if(solutions.size() > 0) {
            VO2Max = accountStats.getVO2Max();
            V4Speed = accountStats.getV4Speed();
            RR = accountStats.getRecoveryRate();
            LT = accountStats.getLactateThreshold();
            //Reset values to their original for comparison

            int solutionIndex = 0;

            double solutionValidity = Math.abs((solutions.get(0)[0]  - V4Speed) / V4Speed) + Math.abs((solutions.get(0)[1] - LT) / LT)
                    + Math.abs((solutions.get(0)[2] - VO2Max) / VO2Max) + Math.abs((solutions.get(0)[3] - RR) / RR);

            //Check which solution is most realistic for the athlete
            for (int i = 1; i < solutions.size() ; i++) {
                double thisSolutionValidity = Math.abs((solutions.get(i)[0]  - V4Speed) / V4Speed) + Math.abs((solutions.get(i)[1] - LT) / LT)
                        + Math.abs((solutions.get(i)[2] - VO2Max) / VO2Max) + Math.abs((solutions.get(i)[3] - RR) / RR);

                if (thisSolutionValidity < solutionValidity){
                    solutionValidity = thisSolutionValidity;
                    solutionIndex =i;
                }
            }

            return new Account_Stats(accountStats.getID(), accountStats.getMultiplier()
                    , solutions.get(solutionIndex)[0], solutions.get(solutionIndex)[1],
                    solutions.get(solutionIndex)[2], solutions.get(solutionIndex)[3]);
        } else {
            //In unlikely event no solutions are found account stats are unchanged
            return accountStats;
        }
    }

    public double avgLactateForTraining(ArrayList<Object> trainingSets) {
        avgLactate = 0;
        lactate = baselineLactate;
        //lactate = baselineLactate before exercise has started
        //Find total time for training to be completed so average can be calculated
        for (int i = 0; i < trainingSets.size() ; i++) {
            if (trainingSets.get(i) instanceof Training_Set) {
                Training_Set set = (Training_Set) trainingSets.get(i);
                totalTime = totalTime + set.getTotalTime();
                if (i < (trainingSets.size() -1)) {
                    totalTime = totalTime + 300;
                }
            }
        }

        for (int i = 0; i < trainingSets.size(); i++) {
            if (trainingSets.get(i) instanceof Training_Set) {
                Training_Set set = (Training_Set) trainingSets.get(i);
                for (int j = 0; j < set.getReps() ; j++) {
                    runDistance(set.getDistance(), set.getDistanceTime());
                    //Distance is run
                    if (j < (set.getReps() -1)) {
                        rest(set.getRestTime());
                        //Rest is done
                    }
                }
                if (i < (trainingSets.size() -1)) {
                    rest(300); // 5 minutes rest in between each training set
                }
            }
        }
        return avgLactate;
    }

    private class LinearModel {
        double m;
        double c;
        //Where y = mx+c

        LinearModel(double m, double c){
            //Instantiates a linear model using a gradient and a constant c
            this.m = m;
            this.c = c;
        }

        LinearModel(Point p1, Point p2){
            //Instantiates a linear model using 2 points
            m = (p2.y - p1.y) / (p2.x - p1.x);
            c = p1.y - ((p1.x)*m);
        }

        LinearModel(Point p, double m){
            //Instantiates a linear model using a point and a gradient
            this.m = m;
            c = p.y - ((p.x)*m);
        }

        double getX(double y){
            //Inverse function
            return ((y-c)/m);
        }
        double getY(double x){
            return ((m*x) + c);
        }
    }

    private class Point{
        double x;
        double y;

        Point(double x, double y){
            //Simple structure that defines a point
            this.x = x;
            this.y = y;
        }
    }

    private class ExponentialModel {
        private double a;
        private double b;
        private double c;
        private double d;
        //Where y = (c)e^b(x-a) + d

        ExponentialModel(double a, double b, double c, double d){
            this.a = a;
            this.b = b;
            this.c = c;
            this.d = d;
        }

        double getY(double x){
            return (c) * Math.pow(Math.E, b*(x-a)) + d;
        }

        public double getX(double y){
            //Inverse function
            return a + (Math.log(y-d/c)/b);
        }
    }
}
