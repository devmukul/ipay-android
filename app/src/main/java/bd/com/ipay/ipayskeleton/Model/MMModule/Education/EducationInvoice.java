package bd.com.ipay.ipayskeleton.Model.MMModule.Education;

import java.math.BigDecimal;

public class EducationInvoice {
    private Integer educationalInvoiceId;
    private BigDecimal totalFee;
    private BigDecimal vat;
    private BigDecimal discount;
    private Integer status;
    private String creatorIpayAccountID;
    private String description;
    private Institution institute;
    private Student eventParticipant;
    private SemesterOrSession session;
    private Long creationTime;
    private Long updateTime;
    private InvoicePayableAccountRelation[] invoicePayableAccountRelations;

    public EducationInvoice(Integer educationalInvoiceId, BigDecimal totalFee, BigDecimal vat, BigDecimal discount, Integer status, String creatorIpayAccountID,
                            String description, Institution institute, Student eventParticipant, SemesterOrSession session, Long creationTime, Long updateTime,
                            InvoicePayableAccountRelation[] invoicePayableAccountRelations) {
        this.educationalInvoiceId = educationalInvoiceId;
        this.totalFee = totalFee;
        this.vat = vat;
        this.discount = discount;
        this.status = status;
        this.creatorIpayAccountID = creatorIpayAccountID;
        this.description = description;
        this.institute = institute;
        this.eventParticipant = eventParticipant;
        this.session = session;
        this.creationTime = creationTime;
        this.updateTime = updateTime;
        this.invoicePayableAccountRelations = invoicePayableAccountRelations;
    }

    public EducationInvoice() {
    }

    public Integer getEducationalInvoiceId() {
        return educationalInvoiceId;
    }

    public void setEducationalInvoiceId(Integer educationalInvoiceId) {
        this.educationalInvoiceId = educationalInvoiceId;
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

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getCreatorIpayAccountID() {
        return creatorIpayAccountID;
    }

    public void setCreatorIpayAccountID(String creatorIpayAccountID) {
        this.creatorIpayAccountID = creatorIpayAccountID;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Institution getInstitute() {
        return institute;
    }

    public void setInstitute(Institution institute) {
        this.institute = institute;
    }

    public Student getEventParticipant() {
        return eventParticipant;
    }

    public void setEventParticipant(Student eventParticipant) {
        this.eventParticipant = eventParticipant;
    }

    public SemesterOrSession getSession() {
        return session;
    }

    public void setSession(SemesterOrSession session) {
        this.session = session;
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

    public InvoicePayableAccountRelation[] getInvoicePayableAccountRelations() {
        return invoicePayableAccountRelations;
    }

    public void setInvoicePayableAccountRelations(InvoicePayableAccountRelation[] invoicePayableAccountRelations) {
        this.invoicePayableAccountRelations = invoicePayableAccountRelations;
    }
}
