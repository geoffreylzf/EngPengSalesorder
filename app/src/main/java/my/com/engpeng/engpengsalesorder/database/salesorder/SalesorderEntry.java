package my.com.engpeng.engpengsalesorder.database.salesorder;

import androidx.sqlite.db.SimpleSQLiteQuery;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import org.json.JSONObject;
import org.parceler.Parcel;
import org.parceler.Transient;

import java.util.List;

import my.com.engpeng.engpengsalesorder.Global;
import my.com.engpeng.engpengsalesorder.database.salesorderDetail.SalesorderDetailEntry;

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
    @ColumnInfo(name = "round_adj")
    public double roundAdj;
    @ColumnInfo(name = "running_no")
    public String runningNo;
    public String latitude;
    public String longitude;
    @ColumnInfo(name = "is_upload")
    public int isUpload;
    @ColumnInfo(name = "create_datetime")
    public String createDatetime;
    @ColumnInfo(name = "modify_datetime")
    public String modifyDatetime;

    @Ignore
    @Transient
    public List<SalesorderDetailEntry> salesorderDetails;

    public SalesorderEntry(long id, long companyId, long customerCompanyId, long customerAddressId, String documentDate, String deliveryDate, String lpo, String remark, String status, double roundAdj, String runningNo, String latitude, String longitude, int isUpload, String createDatetime, String modifyDatetime) {
        this.id = id;
        this.companyId = companyId;
        this.customerCompanyId = customerCompanyId;
        this.customerAddressId = customerAddressId;
        this.documentDate = documentDate;
        this.deliveryDate = deliveryDate;
        this.lpo = lpo;
        this.remark = remark;
        this.status = status;
        this.roundAdj = roundAdj;
        this.runningNo = runningNo;
        this.latitude = latitude;
        this.longitude = longitude;
        this.isUpload = isUpload;
        this.createDatetime = createDatetime;
        this.modifyDatetime = modifyDatetime;
    }

    @Ignore
    public SalesorderEntry() {

    }

    @Ignore
    public SalesorderEntry(JSONObject jsonObject) {
        try {
            setCompanyId(jsonObject.isNull("c_i") ? 0 : jsonObject.getLong("c_i"));
            setCustomerCompanyId(jsonObject.isNull("pcc_id") ? 0 : jsonObject.getLong("pcc_id"));
            setCustomerAddressId(jsonObject.isNull("pcca_id") ? 0 : jsonObject.getLong("pcca_id"));
            setDocumentDate(jsonObject.isNull("doc_d") ? null : jsonObject.getString("doc_d"));
            setDeliveryDate(jsonObject.isNull("del_d") ? null : jsonObject.getString("del_d"));
            setLpo(jsonObject.isNull("lpo") ? null : jsonObject.getString("lpo"));
            setRemark(jsonObject.isNull("r") ? null : jsonObject.getString("r"));
            setRoundAdj(jsonObject.isNull("ra") ? 0 : jsonObject.getDouble("ra"));
            setLatitude(jsonObject.isNull("lat") ? null : jsonObject.getString("lat"));
            setLongitude(jsonObject.isNull("lon") ? null : jsonObject.getString("lon"));
            setRunningNo(jsonObject.isNull("rn") ? null : jsonObject.getString("rn"));
            setCreateDatetime(jsonObject.isNull("c_dt") ? null : jsonObject.getString("c_dt"));
            setModifyDatetime(jsonObject.isNull("m_dt") ? null : jsonObject.getString("m_dt"));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static SimpleSQLiteQuery constructSoGroupQuery(String dateType, long companyId, String dateFilter, String status) {
        String sql;
        Object[] o;

        if (dateType == null || dateType.equals("")) {

            sql = "SELECT 'YEAR' AS dateType, strftime('%Y', document_date) AS documentDate, COUNT(*) AS count" +
                    " FROM salesorder" +
                    " WHERE company_id = ?";

            if ((status.equals(Global.SO_STATUS_ALL) || status.equals(""))) {
                o = new Object[]{companyId};
            } else {
                sql += " AND salesorder.status = ?";
                o = new Object[]{companyId, status};
            }

            sql += " GROUP BY strftime('%Y', document_date)" +
                    " ORDER BY document_date DESC";

            return new SimpleSQLiteQuery(sql, o);

        } else if (dateType.equals(DATE_TYPE_YEAR)) {

            sql = "SELECT 'MONTH' AS dateType, strftime('%Y-%m', document_date) AS documentDate, COUNT(*) AS count" +
                    " FROM salesorder" +
                    " WHERE company_id = ?" +
                    " AND strftime('%Y', document_date) = ?";

            if ((status.equals(Global.SO_STATUS_ALL) || status.equals(""))) {
                o = new Object[]{companyId, dateFilter};
            } else {
                sql += " AND salesorder.status = ?";
                o = new Object[]{companyId, dateFilter, status};
            }

            sql += " GROUP BY strftime('%Y-%m', document_date)" +
                    " ORDER BY document_date DESC";

            return new SimpleSQLiteQuery(sql, o);

        } else if (dateType.equals(DATE_TYPE_MONTH)) {

            sql = "SELECT 'DAY' AS dateType, document_date AS documentDate, COUNT(*) AS count" +
                    " FROM salesorder" +
                    " WHERE company_id = ?" +
                    " AND strftime('%Y-%m', document_date) = ?";

            if ((status.equals(Global.SO_STATUS_ALL) || status.equals(""))) {
                o = new Object[]{companyId, dateFilter};
            } else {
                sql += " AND salesorder.status = ?";
                o = new Object[]{companyId, dateFilter, status};
            }

            sql += " GROUP BY document_date" +
                    " ORDER BY document_date DESC";

            return new SimpleSQLiteQuery(sql, o);
        }
        return null;
    }

    public static SimpleSQLiteQuery constructSoDisplayQuery(String documentDate, long companyId, String status) {
        String sql;
        Object[] o;

        sql = "SELECT salesorder.*, " +
                " branch_name AS companyName," +
                " IFNULL(pcc.person_customer_company_name, 'ID: '||salesorder.customer_company_id) AS customerCompanyName," +
                " IFNULL(pcca.person_customer_address_name, 'ID: '||salesorder.customer_address_id) AS customerAddressName," +
                " COUNT(salesorder_detail.id) AS count" +
                " FROM salesorder" +
                " LEFT JOIN branch ON salesorder.company_id = branch.id" +
                " LEFT JOIN person_customer_company pcc ON salesorder.customer_company_id = pcc.id" +
                " LEFT JOIN person_customer_company_address pcca ON salesorder.customer_address_id = pcca.id" +
                " LEFT JOIN salesorder_detail ON salesorder.id = salesorder_detail.salesorder_id" +
                " WHERE salesorder.company_id = ?" +
                " AND salesorder.document_date = ?";

        if (status.equals(Global.SO_STATUS_ALL) || status.equals("")) {
            o = new Object[]{companyId, documentDate};
        } else {
            sql += " AND salesorder.status = ?";
            o = new Object[]{companyId, documentDate, status};
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

    public double getRoundAdj() {
        return roundAdj;
    }

    public void setRoundAdj(double roundAdj) {
        this.roundAdj = roundAdj;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
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

    public List<SalesorderDetailEntry> getSalesorderDetails() {
        return salesorderDetails;
    }

    public void setSalesorderDetails(List<SalesorderDetailEntry> salesorderDetails) {
        this.salesorderDetails = salesorderDetails;
    }
}
