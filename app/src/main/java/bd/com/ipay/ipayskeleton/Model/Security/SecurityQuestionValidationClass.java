package bd.com.ipay.ipayskeleton.Model.Security;

public class SecurityQuestionValidationClass {
    private String question;
    private boolean isQuestion_available = true;
    private boolean isAnswer_available = true;

    public SecurityQuestionValidationClass() {
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public boolean isQuestion_available() {
        return isQuestion_available;
    }

    public void setQuestion_available(boolean question_available) {
        this.isQuestion_available = question_available;
    }

    public boolean isAnswer_available() {
        return isAnswer_available;
    }

    public void setAnswer_available(boolean answer_available) {
        this.isAnswer_available = answer_available;
    }
}
