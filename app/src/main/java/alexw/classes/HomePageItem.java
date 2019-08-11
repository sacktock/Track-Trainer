package alexw.classes;

/**
 * Created by alexw on 7/15/2017.
 */

public class HomePageItem {

    private String text;
    private String description;
    private int imgResource;

    public String getText(){return text;}
    public String getDescription(){ return description;}
    public int  getImgResource() {return imgResource;}

    public HomePageItem(String text, String description, int imgResource) {
        //Constructor
        this.text = text;
        this.description = description;
        this.imgResource = imgResource;
    }
}
