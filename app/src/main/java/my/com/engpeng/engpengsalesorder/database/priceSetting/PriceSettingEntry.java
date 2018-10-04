package my.com.engpeng.engpengsalesorder.database.priceSetting;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import androidx.annotation.NonNull;

import org.json.JSONObject;

@Entity(tableName = PriceSettingEntry.TABLE_NAME)
public class PriceSettingEntry {

    @Ignore
    public final static String TABLE_NAME = "price_setting";
    @Ignore
    public final static String TABLE_DISPLAY_NAME = "Price Setting";

    @PrimaryKey
    @NonNull
    private long id;
    @ColumnInfo(name = "company_id")
    private long companyId;
    @ColumnInfo(name = "person_customer_company_id")
    private long personCustomerCompanyId;
    @ColumnInfo(name = "doc_type_id")
    private long docTypeId;
    @ColumnInfo(name = "price_group_id")
    private long priceGroupId;
    @ColumnInfo(name = "item_packing_id")
    private long itemPackingId;
    @ColumnInfo(name = "selling_price")
    private double sellingPrice;
    @ColumnInfo(name = "starting_date")
    private String startingDate;
    @ColumnInfo(name = "end_date")
    private String endDate;
    @ColumnInfo(name = "create_date")
    private String createDate;
    @ColumnInfo(name = "is_delete")
    private int isDelete;

    public PriceSettingEntry(long id, long companyId, long personCustomerCompanyId, long docTypeId, long priceGroupId, long itemPackingId, double sellingPrice, String startingDate, String endDate, String createDate, int isDelete) {
        this.id = id;
        this.companyId = companyId;
        this.personCustomerCompanyId = personCustomerCompanyId;
        this.docTypeId = docTypeId;
        this.priceGroupId = priceGroupId;
        this.itemPackingId = itemPackingId;
        this.sellingPrice = sellingPrice;
        this.startingDate = startingDate;
        this.endDate = endDate;
        this.createDate = createDate;
        this.isDelete = isDelete;
    }

    @Ignore
    public PriceSettingEntry(JSONObject jsonObject) {
        try{
            setId(jsonObject.getLong("id"));
            setCompanyId(jsonObject.isNull("c_i") ? 0 : jsonObject.getLong("c_i"));
            setPersonCustomerCompanyId(jsonObject.isNull("pcc_i") ? 0 : jsonObject.getLong("pcc_i"));
            setDocTypeId(jsonObject.isNull("dt_i") ? 0 : jsonObject.getLong("dt_i"));
            setPriceGroupId(jsonObject.isNull("pg_i") ? 0 : jsonObject.getLong("pg_i"));
            setItemPackingId(jsonObject.isNull("ip_i") ? 0 : jsonObject.getLong("ip_i"));
            setSellingPrice(jsonObject.isNull("sp") ? 0 : jsonObject.getDouble("sp"));
            setStartingDate(jsonObject.isNull("s_d") ? null : jsonObject.getString("s_d"));
            setEndDate(jsonObject.isNull("e_d") ? null : jsonObject.getString("e_d"));
            setCreateDate(jsonObject.isNull("c_d") ? null : jsonObject.getString("c_d"));
            setIsDelete(jsonObject.isNull("i_d") ? 0 : jsonObject.getInt("i_d"));
        }catch (Exception e){
            e.printStackTrace();
        }
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

    public long getPersonCustomerCompanyId() {
        return personCustomerCompanyId;
    }

    public void setPersonCustomerCompanyId(long personCustomerCompanyId) {
        this.personCustomerCompanyId = personCustomerCompanyId;
    }

    public long getDocTypeId() {
        return docTypeId;
    }

    public void setDocTypeId(long docTypeId) {
        this.docTypeId = docTypeId;
    }

    public long getPriceGroupId() {
        return priceGroupId;
    }

    public void setPriceGroupId(long priceGroupId) {
        this.priceGroupId = priceGroupId;
    }

    public long getItemPackingId() {
        return itemPackingId;
    }

    public void setItemPackingId(long itemPackingId) {
        this.itemPackingId = itemPackingId;
    }

    public double getSellingPrice() {
        return sellingPrice;
    }

    public void setSellingPrice(double sellingPrice) {
        this.sellingPrice = sellingPrice;
    }

    public String getStartingDate() {
        return startingDate;
    }

    public void setStartingDate(String startingDate) {
        this.startingDate = startingDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public int getIsDelete() {
        return isDelete;
    }

    public void setIsDelete(int isDelete) {
        this.isDelete = isDelete;
    }
}
