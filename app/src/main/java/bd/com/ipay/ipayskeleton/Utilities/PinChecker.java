package bd.com.ipay.ipayskeleton.Utilities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.Arrays;
import java.util.List;

import bd.com.ipay.ipayskeleton.Api.HttpRequestGetAsyncTask;
import bd.com.ipay.ipayskeleton.Api.HttpResponseListener;
import bd.com.ipay.ipayskeleton.Customview.AddPinDialogBuilder;
import bd.com.ipay.ipayskeleton.Model.MMModule.ChangeCredentials.PinInfoResponse;
import bd.com.ipay.ipayskeleton.R;

/**
 * Checks if pin has been added for this user. If not, show pin input dialog. Otherwise,
 * performs desired work passed through the PinCheckerListener.
 */
public class PinChecker implements HttpResponseListener {

    private Context mContext;
    private PinCheckerListener mPinCheckerListener;

    private HttpRequestGetAsyncTask mGetPinInfoTask = null;
    private PinInfoResponse mPinInfoResponse;

    private ProgressDialog mProgressDialog;
    private SharedPreferences pref;

    private boolean cancel;

    public PinChecker(Context context, PinCheckerListener pinCheckerListener) {
        mContext = context;
        mPinCheckerListener = pinCheckerListener;
        mProgressDialog = new ProgressDialog(context);

        pref = mContext.getSharedPreferences(Constants.ApplicationTag, Context.MODE_PRIVATE);
    }

    public void execute() {
        cancel = false;

        if (pref.getBoolean(Constants.IS_PIN_ADDED, false)) {
            if (mPinCheckerListener != null) {
                mPinCheckerListener.ifPinAdded();
            }
        } else {
            getPinInfo();
        }
    }

    private void getPinInfo() {
        if (mGetPinInfoTask != null) {
            return;
        }

        mProgressDialog.setMessage(mContext.getString(R.string.progress_dialog_if_pin_exists));
        mProgressDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                cancel = true;
            }
        });
        mProgressDialog.show();

        mGetPinInfoTask = new HttpRequestGetAsyncTask(Constants.COMMAND_GET_PIN_INFO,
                Constants.BASE_URL + Constants.URL_GET_PIN_INFO, mContext, this);
        mGetPinInfoTask.execute();
    }


    @Override
    public void httpResponseReceiver(String result) {
        mProgressDialog.dismiss();

        if (result == null) {
            mGetPinInfoTask = null;
            if (mContext != null)
                Toast.makeText(mContext, R.string.fetch_info_failed, Toast.LENGTH_LONG).show();

            return;
        }

        List<String> resultList = Arrays.asList(result.split(";"));
        Gson gson = new Gson();

        if (resultList.get(0).equals(Constants.COMMAND_GET_PIN_INFO)) {
            if (!cancel) {
                try {
                    if (resultList.get(1) != null && resultList.get(1).equals(Constants.HTTP_RESPONSE_STATUS_OK)) {
                        mPinInfoResponse = gson.fromJson(resultList.get(2), PinInfoResponse.class);

                        if (mPinInfoResponse.isPinExists()) {
                            if (mPinCheckerListener != null) {
                                mPinCheckerListener.ifPinAdded();
                            }

                            // Save the information so that we don't need to get pin info again and again
                            pref.edit().putBoolean(Constants.IS_PIN_ADDED, true).apply();

                        } else {
                            AddPinDialogBuilder addPinDialogBuilder = new AddPinDialogBuilder(mContext);
                            addPinDialogBuilder.show();
                        }
                    } else {
                        if (mContext != null) {
                            Toast.makeText(mContext, mPinInfoResponse.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    if (mContext != null) {
                        Toast.makeText(mContext, R.string.failed_loading_pin, Toast.LENGTH_LONG).show();
                    }
                }

                mGetPinInfoTask = null;
            }
        }
    }

    /**
     * If pin has been set for this user, PinChecker calls ifPinAdded() function.
     */
    public interface PinCheckerListener {
        void ifPinAdded();
    }
}
