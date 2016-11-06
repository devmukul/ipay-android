package bd.com.ipay.ipayskeleton.Model.Security;

import bd.com.ipay.ipayskeleton.Model.MMModule.Resource.Resource;

public class SecurityAnswerClass implements Resource {

    private String qid;
    private String answer;

    public String getQid() {
        return qid;
    }

    public void setQid(String qid) {
        this.qid = qid;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    @Override
    public int getId() {
        return 0;
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public String getStringId() {
        return qid;
    }
}
