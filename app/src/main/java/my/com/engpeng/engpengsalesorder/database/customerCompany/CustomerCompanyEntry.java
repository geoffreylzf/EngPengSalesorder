package my.com.engpeng.engpengsalesorder.database.customerCompany;


import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import org.json.JSONObject;

@Entity(tableName = CustomerCompanyEntry.TABLE_NAME)
public class CustomerCompanyEntry {
    @Ignore
    public final static String TABLE_NAME = "person_customer_company";
    @Ignore
    public final static String TABLE_DISPLAY_NAME = "Customer Company";

    @PrimaryKey()
    @NonNull
    private long id;
    @ColumnInfo(name = "person_customer_company_code")
    private String personCustomerCompanyCode;
    @ColumnInfo(name = "person_customer_company_name")
    private String personCustomerCompanyName;
    @ColumnInfo(name = "price_group_id")
    private long priceGroupId;
    @ColumnInfo(name = "is_delete")
    private int isDelete;

    public CustomerCompanyEntry(@NonNull long id, String personCustomerCompanyCode, String personCustomerCompanyName, long priceGroupId, int isDelete) {
        this.id = id;
        this.personCustomerCompanyCode = personCustomerCompanyCode;
        this.personCustomerCompanyName = personCustomerCompanyName;
        this.priceGroupId = priceGroupId;
        this.isDelete = isDelete;
    }

    public CustomerCompanyEntry(JSONObject jsonObject) {
        try {
            setId(jsonObject.getLong("id"));
            setPersonCustomerCompanyCode(jsonObject.isNull("pccc") ? null : jsonObject.getString("pccc"));
            setPersonCustomerCompanyName(jsonObject.isNull("pccn") ? null : jsonObject.getString("pccn"));
            setPriceGroupId(jsonObject.isNull("pg_i") ? null : jsonObject.getInt("pg_i"));
            setIsDelete(jsonObject.isNull("i_d") ? null : jsonObject.getInt("i_d"));

        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    @NonNull
    public long getId() {
        return id;
    }

    public void setId(@NonNull long id) {
        this.id = id;
    }

    public String getPersonCustomerCompanyCode() {
        return personCustomerCompanyCode;
    }

    public void setPersonCustomerCompanyCode(String personCustomerCompanyCode) {
        this.personCustomerCompanyCode = personCustomerCompanyCode;
    }

    public String getPersonCustomerCompanyName() {
        return personCustomerCompanyName;
    }

    public void setPersonCustomerCompanyName(String personCustomerCompanyName) {
        this.personCustomerCompanyName = personCustomerCompanyName;
    }

    public long getPriceGroupId() {
        return priceGroupId;
    }

    public void setPriceGroupId(long priceGroupId) {
        this.priceGroupId = priceGroupId;
    }

    public int getIsDelete() {
        return isDelete;
    }

    public void setIsDelete(int isDelete) {
        this.isDelete = isDelete;
    }
}
