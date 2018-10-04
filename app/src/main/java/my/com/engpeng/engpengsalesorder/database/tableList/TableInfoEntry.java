package my.com.engpeng.engpengsalesorder.database.tableList;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import androidx.annotation.NonNull;

@Entity(tableName = TableInfoEntry.TABLE_NAME)
public class TableInfoEntry {

    @Ignore
    public final static String TABLE_NAME = "table_info";

    @PrimaryKey
    @NonNull
    private String type;
    @ColumnInfo(name = "last_sync_date")
    private String lastSyncDate;
    private int insert;
    private int total;
    @Ignore
    private boolean isUpdated;

    public TableInfoEntry(String type, String lastSyncDate, int insert, int total) {
        this.type = type;
        this.lastSyncDate = lastSyncDate;
        this.insert = insert;
        this.total = total;
    }

    @Ignore
    public TableInfoEntry(@NonNull String type, boolean isUpdated) {
        this.type = type;
        this.isUpdated = isUpdated;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getLastSyncDate() {
        return lastSyncDate;
    }

    public void setLastSyncDate(String lastSyncDate) {
        this.lastSyncDate = lastSyncDate;
    }

    public int getInsert() {
        return insert;
    }

    public void setInsert(int insert) {
        this.insert = insert;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public boolean isUpdated() {
        return isUpdated;
    }

    public void setUpdated(boolean updated) {
        isUpdated = updated;
    }
}
