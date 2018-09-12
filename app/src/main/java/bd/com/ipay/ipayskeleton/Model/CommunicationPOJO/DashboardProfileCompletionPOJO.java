package bd.com.ipay.ipayskeleton.Model.CommunicationPOJO;

public class DashboardProfileCompletionPOJO {
    private String property;
    private String title;
    private String subTitle;
    private int imgDrawable;
    private int tag;

    public DashboardProfileCompletionPOJO(String property, String title, String subTitle, int imgDrawable, int tag) {
        this.property = property;
        this.title = title;
        this.subTitle = subTitle;
        this.imgDrawable = imgDrawable;
        this.tag = tag;
    }

    public String getProperty() {
        return property;
    }

    public void setProperty(String property) {
        this.property = property;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubTitle() {
        return subTitle;
    }

    public void setSubTitle(String subTitle) {
        this.subTitle = subTitle;
    }

    public int getImgDrawable() {
        return imgDrawable;
    }

    public void setImgDrawable(int imgDrawable) {
        this.imgDrawable = imgDrawable;
    }

    public int getTag() {
        return tag;
    }

    public void setTag(int tag) {
        this.tag = tag;
    }

    @Override
    public String toString() {
        return "DashboardProfileCompletionPOJO{" +
                "property='" + property + '\'' +
                ", title='" + title + '\'' +
                ", subTitle='" + subTitle + '\'' +
                ", imgDrawable=" + imgDrawable +
                ", tag=" + tag +
                '}';
    }
}

