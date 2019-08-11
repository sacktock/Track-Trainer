package alexw.classes;

/**
 * Created by alexw on 5/3/2017.
 */

public class Training_Set {

    //Class used for formatting descriptions into more useful quantifiable information
    private int distance;
    private int reps;
    private int distanceTime;
    private int restTime;
    private String RepDesc;
    private boolean empty;

    public int getTotalDistance(){
        return distance * reps;
    }
    public String getRepDesc() { return RepDesc;}
    public int getTotalTime() {
        return (reps -1)* restTime + reps * distanceTime;
    }
    public boolean isEmpty() {
        return empty;
    }

    public int getDistance() { return distance;}
    public int getReps() { return reps;}
    public int getDistanceTime() { return distanceTime;}
    public int getRestTime() { return restTime;}
    public String getPace(){
        //Calculates pace time/km for a given distance and time
        String string = new RaceTime((distanceTime *1000) / distance).getTime(true,false) + "/km";
        return string;
    }

    public Training_Set(int Distance, int Reps, int Time, int Rest){
        //Constructor
        distance = Distance;
        reps = Reps;
        distanceTime = Time;
        restTime = Rest;
        notifyDescription();
        empty = false;
    }

    public void notifyDescription(){
        //Changes the description for the training set using its own values when its values have been changed
            RepDesc = StringManipulation.convertToString(reps) + "x" + StringManipulation.convertToString(distance) + "m Time: " +
                    new RaceTime(distanceTime).getTime(true, false) + ", Rest: " + new RaceTime(restTime).getTime(true, false);
        empty = false;
    }

    public Training_Set(){
        //Constructor creates an empty training set
        this.distance = 0;
        this.reps = 0;
        this.distanceTime = 0;
        this.restTime = 0;
        this.RepDesc = "";
        empty = true;
    }

    public Training_Set(int distance, RaceTime pace, int distancePace){
        //Constructor
        this.reps = 1;
        this.distance = distance;
        double d = pace.getTimeInSeconds() * (distance /distancePace);
        this.distanceTime = (int) d;
        this.restTime = 0;
        this.RepDesc = "1x" + StringManipulation.convertToString(distance) + "m Time: " +
                new RaceTime(pace.getTimeInSeconds() * (distance / distancePace)).getTime(true,false)
                + ", Rest: - , Pace: " + pace.getTime(true, false) +"/"+ Integer.toString(distancePace) + "m";
    }

    public void setDistance(int distance){
        this.distance = distance;
        notifyDescription();
    }

    public void setReps(int reps){
        this.reps = reps;
        notifyDescription();
    }

    public void setDistanceTime(int distanceTime){
        this.distanceTime = distanceTime;
        notifyDescription();
    }

    public void setRestTime(int restTime){
        this.restTime = restTime;
        notifyDescription();
    }
}
