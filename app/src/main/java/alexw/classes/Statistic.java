package alexw.classes;

/**
 * Created by alexw on 8/4/2017.
 */

public class Statistic {

    private int value;
    private String valueName;
    private String units;

    public int getValue() {
        return value;
    }
    public String getUnits() {
        return units;
    }
    public String getValueName() {
        return valueName;
    }

    public Statistic(String valueName, int value, String units){
        //Constructor
        this.valueName = valueName;
        this.value = value;
        this.units = units;
    }
}
