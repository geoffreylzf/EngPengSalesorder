package my.com.engpeng.engpengsalesorder.database.customerCompanyAddress;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import org.json.JSONObject;

@Entity(tableName = CustomerCompanyAddressEntry.TABLE_NAME)
public class CustomerCompanyAddressEntry {
    @Ignore
    public final static String TABLE_NAME = "person_customer_company_address";
    @Ignore
    public final static String TABLE_DISPLAY_NAME = "Customer Company Address";

    @PrimaryKey()
    @NonNull
    private long id;
    @ColumnInfo(name = "person_customer_address_code")
    private String personCustomerAddressCode;
    @ColumnInfo(name = "person_customer_address_name")
    private String personCustomerAddressName;
    @ColumnInfo(name = "person_customer_company_id")
    private long personCustomerCompanyId;
    @ColumnInfo(name = "is_delete")
    private int isDelete;

    public CustomerCompanyAddressEntry(@NonNull long id, String personCustomerAddressCode, String personCustomerAddressName, long personCustomerCompanyId, int isDelete) {
        this.id = id;
        this.personCustomerAddressCode = personCustomerAddressCode;
        this.personCustomerAddressName = personCustomerAddressName;
        this.personCustomerCompanyId = personCustomerCompanyId;
        this.isDelete = isDelete;
    }

    public CustomerCompanyAddressEntry(JSONObject jsonObject) {
        try {
            setId(jsonObject.getLong("id"));
            setPersonCustomerAddressCode(jsonObject.isNull("pcac") ? null : jsonObject.getString("pcac"));
            setPersonCustomerAddressName(jsonObject.isNull("pcan") ? null : jsonObject.getString("pcan"));
            setPersonCustomerCompanyId(jsonObject.isNull("pcc_i") ? 0 : jsonObject.getLong("pcc_i"));
            setIsDelete(jsonObject.isNull("i_d") ? 0 : jsonObject.getInt("i_d"));

        } catch (Exception e) {
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

    public String getPersonCustomerAddressCode() {
        return personCustomerAddressCode;
    }

    public void setPersonCustomerAddressCode(String personCustomerAddressCode) {
        this.personCustomerAddressCode = personCustomerAddressCode;
    }

    public String getPersonCustomerAddressName() {
        return personCustomerAddressName;
    }

    public void setPersonCustomerAddressName(String personCustomerAddressName) {
        this.personCustomerAddressName = personCustomerAddressName;
    }

    public long getPersonCustomerCompanyId() {
        return personCustomerCompanyId;
    }

    public void setPersonCustomerCompanyId(long personCustomerCompanyId) {
        this.personCustomerCompanyId = personCustomerCompanyId;
    }

    public int getIsDelete() {
        return isDelete;
    }

    public void setIsDelete(int isDelete) {
        this.isDelete = isDelete;
    }
}
