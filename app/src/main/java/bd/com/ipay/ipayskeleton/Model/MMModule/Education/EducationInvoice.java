package bd.com.ipay.ipayskeleton.Model.MMModule.Education;

import java.math.BigDecimal;

public class EducationInvoice {
    private Integer eventParticipantId;
    private Integer instituteId;
    private Integer departmentId;
    private Integer sessionId;
    private BigDecimal totalFee;
    private BigDecimal vat;
    private BigDecimal discount;
    private String description;
    private Long creationTime;
    private Long updateTime;

    public EducationInvoice() {
    }

    public EducationInvoice(Integer eventParticipantId, Integer instituteId, Integer departmentId, Integer sessionId,
                            BigDecimal totalFee, BigDecimal vat, BigDecimal discount, String description, Long creationTime,
                            Long updateTime) {
        this.eventParticipantId = eventParticipantId;
        this.instituteId = instituteId;
        this.departmentId = departmentId;
        this.sessionId = sessionId;
        this.totalFee = totalFee;
        this.vat = vat;
        this.discount = discount;
        this.description = description;
        this.creationTime = creationTime;
        this.updateTime = updateTime;
    }

    public Integer getEventParticipantId() {
        return eventParticipantId;
    }

    public void setEventParticipantId(Integer eventParticipantId) {
        this.eventParticipantId = eventParticipantId;
    }

    public Integer getInstituteId() {
        return instituteId;
    }

    public void setInstituteId(Integer instituteId) {
        this.instituteId = instituteId;
    }

    public Integer getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(Integer departmentId) {
        this.departmentId = departmentId;
    }

    public Integer getSessionId() {
        return sessionId;
    }

    public void setSessionId(Integer sessionId) {
        this.sessionId = sessionId;
    }

    public BigDecimal getTotalFee() {
        return totalFee;
    }

    public void setTotalFee(BigDecimal totalFee) {
        this.totalFee = totalFee;
    }

    public BigDecimal getVat() {
        return vat;
    }

    public void setVat(BigDecimal vat) {
        this.vat = vat;
    }

    public BigDecimal getDiscount() {
        return discount;
    }

    public void setDiscount(BigDecimal discount) {
        this.discount = discount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(Long creationTime) {
        this.creationTime = creationTime;
    }

    public Long getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Long updateTime) {
        this.updateTime = updateTime;
    }
}
