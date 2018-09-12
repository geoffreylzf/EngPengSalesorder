package my.com.engpeng.engpengsalesorder.database.branch;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import org.json.JSONObject;

@Entity(tableName = BranchEntry.TABLE_NAME)
public class BranchEntry {
    @Ignore
    public final static String TABLE_NAME = "branch";
    @Ignore
    public final static String TABLE_DISPLAY_NAME = "Branch";

    @PrimaryKey()
    @NonNull
    private long id;
    @ColumnInfo(name = "branch_code")
    private String branchCode;
    @ColumnInfo(name = "branch_name")
    private String branchName;
    @ColumnInfo(name = "branch_short_name")
    private String branchShortName;
    @ColumnInfo(name = "company_id")
    private long companyId;

    public BranchEntry(@NonNull long id, String branchCode, String branchName, String branchShortName, long companyId) {
        this.id = id;
        this.branchCode = branchCode;
        this.branchName = branchName;
        this.branchShortName = branchShortName;
        this.companyId = companyId;
    }

    public BranchEntry(JSONObject jsonObject) {
        try {
            setId(jsonObject.getLong("id"));
            setBranchCode(jsonObject.isNull("bc") ? null : jsonObject.getString("bc"));
            setBranchName(jsonObject.isNull("bn") ? null : jsonObject.getString("bn"));
            setBranchShortName(jsonObject.isNull("bsn") ? null : jsonObject.getString("bsn"));
            setCompanyId(jsonObject.isNull("ci") ? 0 : jsonObject.getLong("ci"));

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

    public String getBranchCode() {
        return branchCode;
    }

    public void setBranchCode(String branchCode) {
        this.branchCode = branchCode;
    }

    public String getBranchName() {
        return branchName;
    }

    public void setBranchName(String branchName) {
        this.branchName = branchName;
    }

    public String getBranchShortName() {
        return branchShortName;
    }

    public void setBranchShortName(String branchShortName) {
        this.branchShortName = branchShortName;
    }

    public long getCompanyId() {
        return companyId;
    }

    public void setCompanyId(long companyId) {
        this.companyId = companyId;
    }
}
