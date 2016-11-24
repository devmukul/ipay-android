package bd.com.ipay.ipayskeleton.Model.Security;

import bd.com.ipay.ipayskeleton.Model.MMModule.Resource.Resource;

public class UpdateSecurityAnswerClass {

   private String question;
    private int id;
    private String answer;

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

}

