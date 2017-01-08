package bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Security;

public class SecurityQuestionValidationClass {
    private int id;
    private String question;
    private String answer;
    private boolean isQuestionAvailable = true;
    private boolean isAnswerAvailable = true;

    public SecurityQuestionValidationClass() {
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public boolean isQuestionAvailable() {
        return isQuestionAvailable;
    }

    public void setQuestionAvailable(boolean questionAvailable) {
        this.isQuestionAvailable = questionAvailable;
    }

    public boolean isAnswerAvailable() {
        return isAnswerAvailable;
    }

    public void setAnswerAvailable(boolean answerAvailable) {
        this.isAnswerAvailable = answerAvailable;
    }
}
