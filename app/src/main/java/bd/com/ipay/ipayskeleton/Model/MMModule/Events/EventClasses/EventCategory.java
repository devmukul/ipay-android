package bd.com.ipay.ipayskeleton.Model.MMModule.Events.EventClasses;

public class EventCategory {

    private long id;
    private String description;
    private String name;
    private long creationDateTime;
    private long updateDateTime;

    public EventCategory(long id, String description, String name) {
        super();
        this.id = id;
        this.description = description;
        this.name = name;
    }

    public long getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public String getName() {
        return name;
    }

    public long getCreationDateTime() {
        return creationDateTime;
    }

    public long getUpdateDateTime() {
        return updateDateTime;
    }
}