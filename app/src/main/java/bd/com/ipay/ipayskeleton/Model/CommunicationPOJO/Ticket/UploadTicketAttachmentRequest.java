package bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Ticket;

import java.io.File;

public class UploadTicketAttachmentRequest {
    private int commentId;
    private File[] file;

    public UploadTicketAttachmentRequest(int commentId, File[] file) {
        this.commentId = commentId;
        this.file = file;
    }

    public UploadTicketAttachmentRequest(int commentId) {
        this.commentId = commentId;
    }

    public long getCommentId() {
        return commentId;
    }

    public File[] getFile() {
        return file;
    }
}

