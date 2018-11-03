package my.com.engpeng.engpengsalesorder.database.taxItemMatching;

import org.json.JSONObject;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = TaxItemMatchingEntry.TABLE_NAME)
public class TaxItemMatchingEntry {
    @Ignore
    public final static String TABLE_NAME = "tax_item_matching";
    @Ignore
    public final static String TABLE_DISPLAY_NAME = "Tax Item Matching";

    @PrimaryKey()
    @NonNull
    private long id;
    @ColumnInfo(name = "tax_item_type_id")
    private long taxItemTypeId;
    @ColumnInfo(name = "tax_zone_id")
    private long taxZoneId;
    @ColumnInfo(name = "tax_type_id")
    private long taxTypeId;
    @ColumnInfo(name = "tax_code_id")
    private long taxCodeId;
    @ColumnInfo(name = "is_delete")
    private int isDelete;

    public TaxItemMatchingEntry(@NonNull long id, long taxItemTypeId, long taxZoneId, long taxTypeId, long taxCodeId, int isDelete) {
        this.id = id;
        this.taxItemTypeId = taxItemTypeId;
        this.taxZoneId = taxZoneId;
        this.taxTypeId = taxTypeId;
        this.taxCodeId = taxCodeId;
        this.isDelete = isDelete;
    }

    @Ignore
    public TaxItemMatchingEntry(JSONObject jsonObject) {
        try {
            setId(jsonObject.getLong("id"));
            setTaxItemTypeId(jsonObject.isNull("tit_i") ? 0 : jsonObject.getLong("tit_i"));
            setTaxZoneId(jsonObject.isNull("tz_i") ? 0 : jsonObject.getLong("tz_i"));
            setTaxTypeId(jsonObject.isNull("tt_i") ? 0 : jsonObject.getLong("tt_i"));
            setTaxCodeId(jsonObject.isNull("tc_i") ? 0 : jsonObject.getLong("tc_i"));
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

    public long getTaxItemTypeId() {
        return taxItemTypeId;
    }

    public void setTaxItemTypeId(long taxItemTypeId) {
        this.taxItemTypeId = taxItemTypeId;
    }

    public long getTaxZoneId() {
        return taxZoneId;
    }

    public void setTaxZoneId(long taxZoneId) {
        this.taxZoneId = taxZoneId;
    }

    public long getTaxTypeId() {
        return taxTypeId;
    }

    public void setTaxTypeId(long taxTypeId) {
        this.taxTypeId = taxTypeId;
    }

    public long getTaxCodeId() {
        return taxCodeId;
    }

    public void setTaxCodeId(long taxCodeId) {
        this.taxCodeId = taxCodeId;
    }

    public int getIsDelete() {
        return isDelete;
    }

    public void setIsDelete(int isDelete) {
        this.isDelete = isDelete;
    }
}
