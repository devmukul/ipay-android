package bd.com.ipay.ipayskeleton.Customview;

public interface OnOverScrollByListener {
    boolean overScrollBy(int deltaX, int deltaY, int scrollX,
                         int scrollY, int scrollRangeX, int scrollRangeY,
                         int maxOverScrollX, int maxOverScrollY, boolean isTouchEvent);
}