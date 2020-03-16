package my.com.engpeng.engpengsalesorder.database.store;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import org.json.JSONObject;

@Entity(tableName = StoreEntry.TABLE_NAME)
public class StoreEntry {
    @Ignore
    public final static String TABLE_NAME = "store";
    @Ignore
    public final static String TABLE_DISPLAY_NAME = "Store";

    @PrimaryKey()
    private long id;
    @ColumnInfo(name = "store_code")
    private String storeCode;
    @ColumnInfo(name = "store_name")
    private String storeName;
    @ColumnInfo(name = "is_delete")
    private int isDelete;

    public StoreEntry(long id, String storeCode, String storeName, int isDelete) {
        this.id = id;
        this.storeCode = storeCode;
        this.storeName = storeName;
        this.isDelete = isDelete;
    }

    public StoreEntry(JSONObject jsonObject) {
        try {
            setId(jsonObject.getLong("id"));
            setStoreCode(jsonObject.isNull("sc") ? null : jsonObject.getString("sc"));
            setStoreName(jsonObject.isNull("sn") ? null : jsonObject.getString("sn"));
            setIsDelete(jsonObject.isNull("i_d") ? 0 : jsonObject.getInt("i_d"));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getStoreCode() {
        return storeCode;
    }

    public void setStoreCode(String storeCode) {
        this.storeCode = storeCode;
    }

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    public int getIsDelete() {
        return isDelete;
    }

    public void setIsDelete(int isDelete) {
        this.isDelete = isDelete;
    }

    @NonNull
    @Override
    public String toString() {
        return this.storeCode + " - " + this.storeName;
    }
}
