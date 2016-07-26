package bd.com.ipay.ipayskeleton.Model.MMModule.Education;

import android.os.Parcel;
import android.os.Parcelable;

public class Department implements Parcelable {
    private int departmentId;
    private String departmentName;
    private String departmentShortCode;
    private String details;
    private Institution institute;

    public int getDepartmentId() {
        return departmentId;
    }

    public String getDepartmentName() {
        return departmentName;
    }

    public String getDepartmentShortCode() {
        return departmentShortCode;
    }

    public String getDetails() {
        return details;
    }

    public Institution getInstitute() {
        return institute;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.departmentId);
        dest.writeString(this.departmentName);
        dest.writeString(this.departmentShortCode);
        dest.writeString(this.details);
        dest.writeParcelable(this.institute, flags);
    }

    public Department() {
    }

    protected Department(Parcel in) {
        this.departmentId = in.readInt();
        this.departmentName = in.readString();
        this.departmentShortCode = in.readString();
        this.details = in.readString();
        this.institute = in.readParcelable(Institution.class.getClassLoader());
    }

    public static final Parcelable.Creator<Department> CREATOR = new Parcelable.Creator<Department>() {
        @Override
        public Department createFromParcel(Parcel source) {
            return new Department(source);
        }

        @Override
        public Department[] newArray(int size) {
            return new Department[size];
        }
    };
}
