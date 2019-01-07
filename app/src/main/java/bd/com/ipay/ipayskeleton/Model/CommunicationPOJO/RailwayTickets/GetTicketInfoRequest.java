
package bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.RailwayTickets;

import java.io.Serializable;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class GetTicketInfoRequest implements Serializable
{

    @SerializedName("age")
    @Expose
    private Long age;
    @SerializedName("className")
    @Expose
    private String className;
    @SerializedName("gender")
    @Expose
    private String gender;
    @SerializedName("journeyDate")
    @Expose
    private Long journeyDate;
    @SerializedName("numberOfAdults")
    @Expose
    private Long numberOfAdults;
    @SerializedName("numberOfChildren")
    @Expose
    private Long numberOfChildren;
    @SerializedName("stationFrom")
    @Expose
    private String stationFrom;
    @SerializedName("stationTo")
    @Expose
    private String stationTo;
    @SerializedName("trainNumber")
    @Expose
    private Long trainNumber;

    public GetTicketInfoRequest(Long age, String className, String gender, Long journeyDate, Long numberOfAdults, Long numberOfChildren, String stationFrom, String stationTo, Long trainNumber) {
        this.age = age;
        this.className = className;
        this.gender = gender;
        this.journeyDate = journeyDate;
        this.numberOfAdults = numberOfAdults;
        this.numberOfChildren = numberOfChildren;
        this.stationFrom = stationFrom;
        this.stationTo = stationTo;
        this.trainNumber = trainNumber;
    }

    public Long getAge() {
        return age;
    }

    public void setAge(Long age) {
        this.age = age;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public Long getJourneyDate() {
        return journeyDate;
    }

    public void setJourneyDate(Long journeyDate) {
        this.journeyDate = journeyDate;
    }

    public Long getNumberOfAdults() {
        return numberOfAdults;
    }

    public void setNumberOfAdults(Long numberOfAdults) {
        this.numberOfAdults = numberOfAdults;
    }

    public Long getNumberOfChildren() {
        return numberOfChildren;
    }

    public void setNumberOfChildren(Long numberOfChildren) {
        this.numberOfChildren = numberOfChildren;
    }

    public String getStationFrom() {
        return stationFrom;
    }

    public void setStationFrom(String stationFrom) {
        this.stationFrom = stationFrom;
    }

    public String getStationTo() {
        return stationTo;
    }

    public void setStationTo(String stationTo) {
        this.stationTo = stationTo;
    }

    public Long getTrainNumber() {
        return trainNumber;
    }

    public void setTrainNumber(Long trainNumber) {
        this.trainNumber = trainNumber;
    }

}
