package my.com.engpeng.engpengsalesorder.database.salesorder;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;

@Entity(tableName = SalesorderEntry.TABLE_NAME)
public class SalesorderEntry {
    @Ignore
    public final static String TABLE_NAME = "salesorder";
    @Ignore
    public final static String TABLE_DISPLAY_NAME = "Salesorder";

    @PrimaryKey(autoGenerate = true)
    private long id;
    @ColumnInfo(name = "company_id")
    private long companyId;
    @ColumnInfo(name = "customer_company_id")
    private long customerCompanyId;
    @ColumnInfo(name = "customer_address_id")
    private long customerAddressId;
    @ColumnInfo(name = "document_date")
    private String documentDate;
    @ColumnInfo(name = "delivery_date")
    private String deliveryDate;
    private String lpo;
    private String remark;
    private String status;
    @ColumnInfo(name = "running_no")
    private String runningNo;
    @ColumnInfo(name = "create_datetime")
    private String createDatetime;
    @ColumnInfo(name = "modify_datetime")
    private String modifyDatetime;

    public SalesorderEntry(long companyId, long customerCompanyId,
                           long customerAddressId, String documentDate, String deliveryDate,
                           String lpo, String remark, String status,
                           String runningNo, String createDatetime, String modifyDatetime) {
        this.companyId = companyId;
        this.customerCompanyId = customerCompanyId;
        this.customerAddressId = customerAddressId;
        this.documentDate = documentDate;
        this.deliveryDate = deliveryDate;
        this.lpo = lpo;
        this.remark = remark;
        this.status = status;
        this.runningNo = runningNo;
        this.createDatetime = createDatetime;
        this.modifyDatetime = modifyDatetime;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getCompanyId() {
        return companyId;
    }

    public void setCompanyId(long companyId) {
        this.companyId = companyId;
    }

    public long getCustomerCompanyId() {
        return customerCompanyId;
    }

    public void setCustomerCompanyId(long customerCompanyId) {
        this.customerCompanyId = customerCompanyId;
    }

    public long getCustomerAddressId() {
        return customerAddressId;
    }

    public void setCustomerAddressId(long customerAddressId) {
        this.customerAddressId = customerAddressId;
    }

    public String getDocumentDate() {
        return documentDate;
    }

    public void setDocumentDate(String documentDate) {
        this.documentDate = documentDate;
    }

    public String getDeliveryDate() {
        return deliveryDate;
    }

    public void setDeliveryDate(String deliveryDate) {
        this.deliveryDate = deliveryDate;
    }

    public String getLpo() {
        return lpo;
    }

    public void setLpo(String lpo) {
        this.lpo = lpo;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getRunningNo() {
        return runningNo;
    }

    public void setRunningNo(String runningNo) {
        this.runningNo = runningNo;
    }

    public String getCreateDatetime() {
        return createDatetime;
    }

    public void setCreateDatetime(String createDatetime) {
        this.createDatetime = createDatetime;
    }

    public String getModifyDatetime() {
        return modifyDatetime;
    }

    public void setModifyDatetime(String modifyDatetime) {
        this.modifyDatetime = modifyDatetime;
    }
}
