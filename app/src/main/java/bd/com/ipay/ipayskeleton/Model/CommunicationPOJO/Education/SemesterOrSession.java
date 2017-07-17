package bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Education;

import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Resource.Resource;

public class SemesterOrSession implements Resource{

    private int id;
    private String description;
    private String sessionName;
    private int instituteId;

    @Override
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getName() {
        return sessionName;
    }

    @Override
    public String getStringId() {
        return null;
    }

    public void setSessionName(String sessionName) {
        this.sessionName = sessionName;
    }

    public int getInstituteId() {
        return instituteId;
    }

    public void setInstituteId(int instituteId) {
        this.instituteId = instituteId;
    }
}

