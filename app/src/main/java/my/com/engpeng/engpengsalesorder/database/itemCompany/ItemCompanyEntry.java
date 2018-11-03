package my.com.engpeng.engpengsalesorder.database.itemCompany;

import org.json.JSONObject;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = ItemCompanyEntry.TABLE_NAME)
public class ItemCompanyEntry {
    @Ignore
    public final static String TABLE_NAME = "item_company";
    @Ignore
    public final static String TABLE_DISPLAY_NAME = "Item Company";

    @PrimaryKey()
    @NonNull
    private long id;
    @ColumnInfo(name = "item_master_id")
    private long itemMasterId;
    @ColumnInfo(name = "company_id")
    private long companyId;
    @ColumnInfo(name = "is_delete")
    private int isDelete;

    public ItemCompanyEntry(@NonNull long id, long itemMasterId, long companyId, int isDelete) {
        this.id = id;
        this.itemMasterId = itemMasterId;
        this.companyId = companyId;
        this.isDelete = isDelete;
    }

    @Ignore
    public ItemCompanyEntry(JSONObject jsonObject) {
        try {
            setId(jsonObject.getLong("id"));
            setItemMasterId(jsonObject.isNull("im_i") ? 0 : jsonObject.getLong("im_i"));
            setCompanyId(jsonObject.isNull("c_i") ? 0 : jsonObject.getLong("c_i"));
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

    public long getItemMasterId() {
        return itemMasterId;
    }

    public void setItemMasterId(long itemMasterId) {
        this.itemMasterId = itemMasterId;
    }

    public long getCompanyId() {
        return companyId;
    }

    public void setCompanyId(long companyId) {
        this.companyId = companyId;
    }

    public int getIsDelete() {
        return isDelete;
    }

    public void setIsDelete(int isDelete) {
        this.isDelete = isDelete;
    }
}
