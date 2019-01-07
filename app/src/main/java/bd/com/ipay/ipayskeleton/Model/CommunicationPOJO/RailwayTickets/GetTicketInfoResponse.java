
package bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.RailwayTickets;

import java.io.Serializable;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class GetTicketInfoResponse implements Serializable
{

    @SerializedName("className")
    @Expose
    private String className;
    @SerializedName("fare")
    @Expose
    private Double fare;
    @SerializedName("journeyDate")
    @Expose
    private String journeyDate;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("messageId")
    @Expose
    private String messageId;
    @SerializedName("stationFrom")
    @Expose
    private String stationFrom;
    @SerializedName("stationTo")
    @Expose
    private String stationTo;
    @SerializedName("ticketId")
    @Expose
    private String ticketId;
    @SerializedName("totalFare")
    @Expose
    private Double totalFare;
    @SerializedName("trainNumber")
    @Expose
    private Long trainNumber;
    @SerializedName("vat")
    @Expose
    private Double vat;
    private final static long serialVersionUID = 5679884630731318303L;

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public Double getFare() {
        return fare;
    }

    public void setFare(Double fare) {
        this.fare = fare;
    }

    public String getJourneyDate() {
        return journeyDate;
    }

    public void setJourneyDate(String journeyDate) {
        this.journeyDate = journeyDate;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
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

    public String getTicketId() {
        return ticketId;
    }

    public void setTicketId(String ticketId) {
        this.ticketId = ticketId;
    }

    public Double getTotalFare() {
        return totalFare;
    }

    public void setTotalFare(Double totalFare) {
        this.totalFare = totalFare;
    }

    public Long getTrainNumber() {
        return trainNumber;
    }

    public void setTrainNumber(Long trainNumber) {
        this.trainNumber = trainNumber;
    }

    public Double getVat() {
        return vat;
    }

    public void setVat(Double vat) {
        this.vat = vat;
    }

}
