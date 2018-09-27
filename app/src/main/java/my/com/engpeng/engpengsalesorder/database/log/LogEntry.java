package my.com.engpeng.engpengsalesorder.database.log;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

@Entity(tableName = LogEntry.TABLE_NAME)
public class LogEntry {
    @Ignore
    public final static String TABLE_NAME = "log";

    @PrimaryKey()
    @NonNull
    private long id;
    private String task;
    private String datetime;
    private String remark;

    public LogEntry(@NonNull long id, String task, String datetime, String remark) {
        this.id = id;
        this.task = task;
        this.datetime = datetime;
        this.remark = remark;
    }

    @Ignore
    public LogEntry(String task, String datetime, String remark) {
        this.task = task;
        this.datetime = datetime;
        this.remark = remark;
    }

    @NonNull
    public long getId() {
        return id;
    }

    public void setId(@NonNull long id) {
        this.id = id;
    }

    public String getTask() {
        return task;
    }

    public void setTask(String task) {
        this.task = task;
    }

    public String getDatetime() {
        return datetime;
    }

    public void setDatetime(String datetime) {
        this.datetime = datetime;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
