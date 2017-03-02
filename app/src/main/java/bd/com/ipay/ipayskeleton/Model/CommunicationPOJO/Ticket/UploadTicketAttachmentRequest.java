package bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Ticket;

import java.io.File;

public class UploadTicketAttachmentRequest {
    private long commentId;
    private File[] file;

    public UploadTicketAttachmentRequest(long commentId, File[] file) {
        this.commentId = commentId;
        this.file = file;
    }

    public long getCommentId() {
        return commentId;
    }

    public File[] getFile() {
        return file;
    }
}

