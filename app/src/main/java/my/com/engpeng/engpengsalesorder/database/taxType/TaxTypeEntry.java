package my.com.engpeng.engpengsalesorder.database.taxType;

import org.json.JSONObject;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = TaxTypeEntry.TABLE_NAME)
public class TaxTypeEntry {
    @Ignore
    public final static String TABLE_NAME = "tax_type";
    @Ignore
    public final static String TABLE_DISPLAY_NAME = "Tax Type";

    @PrimaryKey()
    @NonNull
    private long id;
    @ColumnInfo(name = "effective_date_from")
    private String effectiveDateFrom;
    @ColumnInfo(name = "effective_date_to")
    private String effectiveDateTo;
    @ColumnInfo(name = "is_delete")
    private int isDelete;

    public TaxTypeEntry(@NonNull long id, String effectiveDateFrom, String effectiveDateTo, int isDelete) {
        this.id = id;
        this.effectiveDateFrom = effectiveDateFrom;
        this.effectiveDateTo = effectiveDateTo;
        this.isDelete = isDelete;
    }

    @Ignore
    public TaxTypeEntry(JSONObject jsonObject) {
        try {
            setId(jsonObject.getLong("id"));
            setEffectiveDateFrom(jsonObject.isNull("e_d_f") ? null : jsonObject.getString("e_d_f"));
            setEffectiveDateTo(jsonObject.isNull("e_d_t") ? null : jsonObject.getString("e_d_t"));
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

    public String getEffectiveDateFrom() {
        return effectiveDateFrom;
    }

    public void setEffectiveDateFrom(String effectiveDateFrom) {
        this.effectiveDateFrom = effectiveDateFrom;
    }

    public String getEffectiveDateTo() {
        return effectiveDateTo;
    }

    public void setEffectiveDateTo(String effectiveDateTo) {
        this.effectiveDateTo = effectiveDateTo;
    }

    public int getIsDelete() {
        return isDelete;
    }

    public void setIsDelete(int isDelete) {
        this.isDelete = isDelete;
    }
}
