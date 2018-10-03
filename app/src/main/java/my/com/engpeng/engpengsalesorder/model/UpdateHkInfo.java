package my.com.engpeng.engpengsalesorder.model;

public class UpdateHkInfo {
    private boolean isSuccess = true;
    private boolean isEnd = false;
    private boolean isPartial = false;
    private String tableName = null;
    private int count;
    private long logId;
    private int limitStart;
    private int limitLength;

    public UpdateHkInfo(boolean isSuccess) {
        this.isSuccess = isSuccess;
    }

    public UpdateHkInfo(boolean isSuccess, boolean isEnd) {
        this.isSuccess = isSuccess;
        this.isEnd = isEnd;
    }

    public UpdateHkInfo(boolean isSuccess, String tableName) {
        this.isSuccess = isSuccess;
        this.tableName = tableName;
    }

    public UpdateHkInfo(boolean isSuccess, String tableName, int count, long logId) {
        this.isSuccess = isSuccess;
        this.tableName = tableName;
        this.count = count;
        this.logId = logId;
    }


    public boolean isPartial() {
        return isPartial;
    }

    public void setPartial(boolean partial) {
        isPartial = partial;
    }

    public boolean isEnd() {
        return isEnd;
    }

    public void setEnd(boolean end) {
        isEnd = end;
    }

    public boolean isSuccess() {
        return isSuccess;
    }

    public void setSuccess(boolean success) {
        isSuccess = success;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public long getLogId() {
        return logId;
    }

    public void setLogId(long logId) {
        this.logId = logId;
    }

    public int getLimitStart() {
        return limitStart;
    }

    public void setLimitStart(int limitStart) {
        this.limitStart = limitStart;
    }

    public int getLimitLength() {
        return limitLength;
    }

    public void setLimitLength(int limitLength) {
        this.limitLength = limitLength;
    }
}
