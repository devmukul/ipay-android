package bd.com.ipay.ipayskeleton.Model.Security;

import bd.com.ipay.ipayskeleton.Model.MMModule.Resource.Resource;

public class SecurityQuestionClass implements Resource {

    private String qid;
    private String question;

    public SecurityQuestionClass() {
    }

    public String getQid() {
        return qid;
    }

    public String getQuestion() {
        return question;
    }

    @Override
    public int getId() {
        return 0;
    }

    @Override
    public String getName() {
        return question;
    }

    @Override
    public String getStringId() {
        return qid;
    }
}
