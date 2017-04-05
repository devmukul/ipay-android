package bd.com.ipay.ipayskeleton.Utilities;

import java.util.List;

import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Ticket.Comment;

public class CustomerSupportUtilities {

    public static int getIndexOfComment(long commentId, List<Comment> mComments) {
        for (Comment comment : mComments) {
            if (comment.getComment_Id() == commentId)
                return mComments.indexOf(comment);
        }
        return -1;
    }

    public static Comment getUpdatedCommentWithDocuments(Comment comment, List<String> newUploadedDocuments) {
        List<String> documentList = comment.getDocuments();
        documentList.addAll(newUploadedDocuments);
        comment.setDocuments(documentList);
        return comment;
    }
}
