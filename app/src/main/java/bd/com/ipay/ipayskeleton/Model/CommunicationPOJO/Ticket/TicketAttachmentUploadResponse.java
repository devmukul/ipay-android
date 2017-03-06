package bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Ticket;

public class TicketAttachmentUploadResponse {
    private String message;
    private int statusCode;
    private CommentIdResponse response;

    public String getMessage() {
        return message;
    }

    public CommentIdResponse getResponse() {
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

    public void setResponse(CommentIdResponse response) {
        this.response = response;
    }
}
