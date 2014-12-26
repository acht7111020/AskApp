package netdb.course.softwarestudio.askapp.model;

import netdb.course.softwarestudio.askapp.service.rest.model.Resource;

public class Definition extends Resource{

    private String title;
    private String description;

    public static String getCollectionName() {
        return "definition";
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
