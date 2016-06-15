package bd.com.ipay.ipayskeleton.Custom_View;

public interface OnOverScrollByListener {
    boolean overScrollBy(int deltaX, int deltaY, int scrollX,
                         int scrollY, int scrollRangeX, int scrollRangeY,
                         int maxOverScrollX, int maxOverScrollY, boolean isTouchEvent);
}