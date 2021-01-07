package daemonThread;

import java.util.Date;

public class Event {
    Date date;
    String content;
    // overload contructor
    Event(){

    }
    Event(Date date, String event){
        this.date = date;
        this.content = event;
    }

    public Date getDate() {
        return date;
    }

    public String getContent() {
        return content;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
