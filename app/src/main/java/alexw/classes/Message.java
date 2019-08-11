package alexw.classes;

/**
 * Created by alexw on 8/5/2017.
 */

public class Message {

    private String title;
    private String body;

    public String getBody() {
        return body;
    }
    public String getTitle() {
        return title;
    }

    public Message(String title, String body ){
        //Constructor
        this.body = body;
        this.title = title;
    }

    public Message(String title){
        //Constructor with only a title
        this.title = title;
        this.body = "";
    }
}
