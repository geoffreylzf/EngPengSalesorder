package my.com.engpeng.engpengsalesorder.database.taxCode;

import org.json.JSONObject;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = TaxCodeEntry.TABLE_NAME)
public class TaxCodeEntry {
    @Ignore
    public final static String TABLE_NAME = "tax_code";
    @Ignore
    public final static String TABLE_DISPLAY_NAME = "Tax Code";

    @PrimaryKey()
    @NonNull
    private long id;
    @ColumnInfo(name = "tax_code")
    private String taxCode;
    @ColumnInfo(name = "tax_rate")
    private double taxRate;
    @ColumnInfo(name = "is_delete")
    private int isDelete;

    public TaxCodeEntry(@NonNull long id, String taxCode, double taxRate, int isDelete) {
        this.id = id;
        this.taxCode = taxCode;
        this.taxRate = taxRate;
        this.isDelete = isDelete;
    }

    @Ignore
    public TaxCodeEntry(JSONObject jsonObject) {
        try {
            setId(jsonObject.getLong("id"));
            setTaxCode(jsonObject.isNull("tc") ? null : jsonObject.getString("tc"));
            setTaxRate(jsonObject.isNull("tr") ? 0 : jsonObject.getDouble("tr"));
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

    public String getTaxCode() {
        return taxCode;
    }

    public void setTaxCode(String taxCode) {
        this.taxCode = taxCode;
    }

    public double getTaxRate() {
        return taxRate;
    }

    public void setTaxRate(double taxRate) {
        this.taxRate = taxRate;
    }

    public int getIsDelete() {
        return isDelete;
    }

    public void setIsDelete(int isDelete) {
        this.isDelete = isDelete;
    }
}
