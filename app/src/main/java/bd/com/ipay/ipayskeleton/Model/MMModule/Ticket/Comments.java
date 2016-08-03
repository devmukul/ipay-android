package bd.com.ipay.ipayskeleton.Model.MMModule.Ticket;

import java.util.List;

public class Comments {
    private List<Comment> comments;
    private String nextPage;
    private String prevPage;
    private int count;

    public List<Comment> getComments() {
        return comments;
    }

    public String getNextPage() {
        return nextPage;
    }

    public String getPrevPage() {
        return prevPage;
    }

    public int getCount() {
        return count;
    }

    @Override
    public String toString() {
        return "Comments{" +
                "comments=" + comments +
                ", nextPage='" + nextPage + '\'' +
                ", prevPage='" + prevPage + '\'' +
                ", count=" + count +
                '}';
    }
}
