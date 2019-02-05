
package bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.TransactionHistory;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class TransactionMetaData implements Parcelable
{

    @SerializedName("visibility")
    @Expose
    private List<Visibility> visibility = null;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeList(visibility);
    }

    public final static Creator<TransactionMetaData> CREATOR = new Creator<TransactionMetaData>() {

        @Override
        public TransactionMetaData createFromParcel(Parcel in) {
            return new TransactionMetaData(in);
        }

        @Override
        public TransactionMetaData[] newArray(int size) {
            return (new TransactionMetaData[size]);
        }

    }
    ;

    protected TransactionMetaData(Parcel in) {
        visibility = in.readArrayList(Visibility.class.getClassLoader());
    }


    public TransactionMetaData() {
    }

    public TransactionMetaData(ArrayList<Visibility> visibility) {
        super();
        this.visibility = visibility;
    }

    public List<Visibility> getVisibility() {
        return visibility;
    }

    public void setVisibility(List<Visibility> visibility) {
        this.visibility = visibility;
    }


    @Override
    public String toString() {
        return "TransactionMetaData{" +
                "visibility=" + visibility +
                '}';
    }
}
