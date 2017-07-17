package bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Ticket;

public class AddCommentResponse {
    private String message;
    private int statusCode;
    private CommentIdWithDocumentList response;

    public String getMessage() {
        return message;
    }

    public CommentIdWithDocumentList getResponse() {
        return response;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public void setResponse(CommentIdWithDocumentList response) {
        this.response = response;
    }
}
