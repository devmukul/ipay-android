package bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Security;

import android.os.Parcel;
import android.os.Parcelable;

public class PreviousSecurityQuestionClass implements Parcelable {

    private int id;
    private String question;

    protected PreviousSecurityQuestionClass(Parcel in) {
        id = in.readInt();
        question = in.readString();
    }

    public static final Creator<PreviousSecurityQuestionClass> CREATOR = new Creator<PreviousSecurityQuestionClass>() {
        @Override
        public PreviousSecurityQuestionClass createFromParcel(Parcel in) {
            return new PreviousSecurityQuestionClass(in);
        }

        @Override
        public PreviousSecurityQuestionClass[] newArray(int size) {
            return new PreviousSecurityQuestionClass[size];
        }
    };

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }



    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(id);
        parcel.writeString(question);
    }
}

