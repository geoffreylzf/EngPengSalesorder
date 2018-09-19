package my.com.engpeng.engpengsalesorder.database.salesorder;

import android.arch.persistence.db.SimpleSQLiteQuery;
import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;

import org.parceler.Parcel;

import my.com.engpeng.engpengsalesorder.Global;

import static my.com.engpeng.engpengsalesorder.Global.DATE_TYPE_MONTH;
import static my.com.engpeng.engpengsalesorder.Global.DATE_TYPE_YEAR;

@Parcel
@Entity(tableName = SalesorderEntry.TABLE_NAME)
public class SalesorderEntry {
    @Ignore
    public final static String TABLE_NAME = "salesorder";
    @Ignore
    public final static String TABLE_DISPLAY_NAME = "Salesorder";

    @PrimaryKey(autoGenerate = true)
    public long id;
    @ColumnInfo(name = "company_id")
    public long companyId;
    @ColumnInfo(name = "customer_company_id")
    public long customerCompanyId;
    @ColumnInfo(name = "customer_address_id")
    public long customerAddressId;
    @ColumnInfo(name = "document_date")
    public String documentDate;
    @ColumnInfo(name = "delivery_date")
    public String deliveryDate;
    public String lpo;
    public String remark;
    public String status;
    @ColumnInfo(name = "running_no")
    public String runningNo;
    @ColumnInfo(name = "is_upload")
    public int isUpload;
    @ColumnInfo(name = "create_datetime")
    public String createDatetime;
    @ColumnInfo(name = "modify_datetime")
    private String modifyDatetime;

    public SalesorderEntry(long id, long companyId, long customerCompanyId, long customerAddressId,
                           String documentDate, String deliveryDate,
                           String lpo, String remark,
                           String status, String runningNo, int isUpload,
                           String createDatetime, String modifyDatetime) {
        this.id = id;
        this.companyId = companyId;
        this.customerCompanyId = customerCompanyId;
        this.customerAddressId = customerAddressId;
        this.documentDate = documentDate;
        this.deliveryDate = deliveryDate;
        this.lpo = lpo;
        this.remark = remark;
        this.status = status;
        this.runningNo = runningNo;
        this.isUpload = isUpload;
        this.createDatetime = createDatetime;
        this.modifyDatetime = modifyDatetime;
    }

    @Ignore
    public SalesorderEntry() {

    }

    @Ignore
    //for new insert
    public SalesorderEntry(long companyId, long customerCompanyId,
                           long customerAddressId, String documentDate, String deliveryDate,
                           String lpo, String remark, String status,
                           String runningNo, int isUpload,
                           String createDatetime, String modifyDatetime) {
        this.companyId = companyId;
        this.customerCompanyId = customerCompanyId;
        this.customerAddressId = customerAddressId;
        this.documentDate = documentDate;
        this.deliveryDate = deliveryDate;
        this.lpo = lpo;
        this.remark = remark;
        this.status = status;
        this.runningNo = runningNo;
        this.isUpload = isUpload;
        this.createDatetime = createDatetime;
        this.modifyDatetime = modifyDatetime;
    }

    @Ignore
    //for update
    public SalesorderEntry(long id, long companyId, long customerCompanyId, long customerAddressId,
                           String documentDate, String deliveryDate,
                           String lpo, String remark, String status,
                           String runningNo, int isUpload,
                           String modifyDatetime) {
        this.id = id;
        this.companyId = companyId;
        this.customerCompanyId = customerCompanyId;
        this.customerAddressId = customerAddressId;
        this.documentDate = documentDate;
        this.deliveryDate = deliveryDate;
        this.lpo = lpo;
        this.remark = remark;
        this.status = status;
        this.runningNo = runningNo;
        this.isUpload = isUpload;
        this.modifyDatetime = modifyDatetime;
    }

    public static SimpleSQLiteQuery constructSoGroupQuery(String dateType, String dateFilter, String status) {
        String sql;
        Object[] o;

        if (dateType == null || dateType.equals("")) {

            sql = "SELECT 'YEAR' AS dateType, strftime('%Y', document_date) AS documentDate, COUNT(*) AS count" +
                    " FROM salesorder" +
                    " WHERE 1 = 1";

            if ((status.equals(Global.SO_STATUS_ALL) || status.equals(""))) {
                o = new Object[]{};
            } else {
                sql += " AND salesorder.status = ?";
                o = new Object[]{status};
            }

            sql += " GROUP BY strftime('%Y', document_date)" +
                    " ORDER BY document_date DESC";

            return new SimpleSQLiteQuery(sql, o);

        } else if (dateType.equals(DATE_TYPE_YEAR)) {

            sql = "SELECT 'MONTH' AS dateType, strftime('%Y-%m', document_date) AS documentDate, COUNT(*) AS count" +
                    " FROM salesorder" +
                    " WHERE strftime('%Y', document_date) = ?";

            if ((status.equals(Global.SO_STATUS_ALL) || status.equals(""))) {
                o = new Object[]{dateFilter};
            } else {
                sql += " AND salesorder.status = ?";
                o = new Object[]{dateFilter, status};
            }

            sql += " GROUP BY strftime('%Y-%m', document_date)" +
                    " ORDER BY document_date DESC";

            return new SimpleSQLiteQuery(sql, o);

        } else if (dateType.equals(DATE_TYPE_MONTH)) {

            sql = "SELECT 'DAY' AS dateType, document_date AS documentDate, COUNT(*) AS count" +
                    " FROM salesorder" +
                    " WHERE strftime('%Y-%m', document_date) = ?";

            if ((status.equals(Global.SO_STATUS_ALL) || status.equals(""))) {
                o = new Object[]{dateFilter};
            } else {
                sql += " AND salesorder.status = ?";
                o = new Object[]{dateFilter, status};
            }

            sql += " GROUP BY document_date" +
                    " ORDER BY document_date DESC";

            return new SimpleSQLiteQuery(sql, o);
        }
        return null;
    }

    public static SimpleSQLiteQuery constructSoDisplayQuery(String document, String status) {
        String sql;
        Object[] o;

        sql = "SELECT salesorder.*, " +
                " branch_name AS companyName," +
                " pcc.person_customer_company_name AS customerCompanyName," +
                " pcca.person_customer_address_name AS customerAddressName," +
                " COUNT(salesorder_detail.id) AS count" +
                " FROM salesorder" +
                " LEFT JOIN branch ON salesorder.company_id = branch.id" +
                " LEFT JOIN person_customer_company pcc ON salesorder.customer_company_id = pcc.id" +
                " LEFT JOIN person_customer_company_address pcca ON salesorder.customer_address_id = pcca.id" +
                " LEFT JOIN salesorder_detail ON salesorder.id = salesorder_detail.salesorder_id" +
                " WHERE salesorder.document_date = ?";

        if (status.equals(Global.SO_STATUS_ALL) || status.equals("")) {
            o = new Object[]{document};
        } else {
            sql += " AND salesorder.status = ?";
            o = new Object[]{document, status};
        }

        sql += " GROUP BY salesorder.id" +
                " ORDER BY salesorder.id DESC";

        return new SimpleSQLiteQuery(sql, o);
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

    public int getIsUpload() {
        return isUpload;
    }

    public void setIsUpload(int isUpload) {
        this.isUpload = isUpload;
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
