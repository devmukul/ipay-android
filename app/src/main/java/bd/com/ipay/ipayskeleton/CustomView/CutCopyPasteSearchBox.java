package bd.com.ipay.ipayskeleton.CustomView;


import android.content.Context;
import android.util.AttributeSet;
import android.widget.SearchView;

public class CutCopyPasteSearchBox extends SearchView {

    public interface OnCutCopyPasteListener {
        void onPaste();
    }

    private OnCutCopyPasteListener mOnCutCopyPasteListener;

    public void setOnCutCopyPasteListener(OnCutCopyPasteListener listener) {
        mOnCutCopyPasteListener = listener;
    }

    public CutCopyPasteSearchBox(Context context) {
        super(context);
    }

    public CutCopyPasteSearchBox(Context context, AttributeSet attrs) {
        super(context, attrs);
    }






//    @Override
//    public boolean onTextContextMenuItem(int id) {
//        // Do your thing:
//        boolean consumed = super.onTextContextMenuItem(id);
//        // React:
//        switch (id){
//            case android.R.id.paste:
//                onPaste();
//        }
//        return consumed;
//    }
//
//    public void onPaste(){
//        if(mOnCutCopyPasteListener!=null)
//            mOnCutCopyPasteListener.onPaste();
//    }
}
