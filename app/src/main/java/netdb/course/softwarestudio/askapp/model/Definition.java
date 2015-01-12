package netdb.course.softwarestudio.askapp.model;

import java.util.List;

import netdb.course.softwarestudio.askapp.service.rest.model.Resource;

public class Definition extends Resource{

    private String title;
    private String description;
    private List<String> comment;

    public static String getCollectionName() {
        return "definitions";
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setComment(String com) {
        this.comment.add(com);
    }

    public String getDescription() {
        return description;
    }

    public List<String> getComment() {
        return comment;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
