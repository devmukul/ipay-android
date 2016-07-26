package bd.com.ipay.ipayskeleton.Model.MMModule.Education;

import bd.com.ipay.ipayskeleton.Model.MMModule.Resource.Resource;

public class SemesterOrSession implements Resource{

    private int sessionId;
    private String description;
    private String sessionName;
    private Institution institute;

    public int getId() {
        return sessionId;
    }

    public String getDescription() {
        return description;
    }

    public String getName() {
        return sessionName;
    }

    public Institution getInstitute() {
        return institute;
    }

    public void setSessionId(int sessionId) {
        this.sessionId = sessionId;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setSessionName(String sessionName) {
        this.sessionName = sessionName;
    }

    public void setInstitute(Institution institute) {
        this.institute = institute;
    }
}

