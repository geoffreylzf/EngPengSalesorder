package my.com.engpeng.engpengsalesorder.database;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import android.content.Context;

import my.com.engpeng.engpengsalesorder.database.log.LogDao;
import my.com.engpeng.engpengsalesorder.database.log.LogEntry;
import my.com.engpeng.engpengsalesorder.database.salesorderDetail.SalesorderDetailDao;
import my.com.engpeng.engpengsalesorder.database.salesorderDetail.SalesorderDetailEntry;
import my.com.engpeng.engpengsalesorder.database.branch.BranchDao;
import my.com.engpeng.engpengsalesorder.database.branch.BranchEntry;
import my.com.engpeng.engpengsalesorder.database.customerCompanyAddress.CustomerCompanyAddressDao;
import my.com.engpeng.engpengsalesorder.database.customerCompanyAddress.CustomerCompanyAddressEntry;
import my.com.engpeng.engpengsalesorder.database.itemPacking.ItemPackingDao;
import my.com.engpeng.engpengsalesorder.database.itemPacking.ItemPackingEntry;
import my.com.engpeng.engpengsalesorder.database.customerCompany.CustomerCompanyDao;
import my.com.engpeng.engpengsalesorder.database.customerCompany.CustomerCompanyEntry;
import my.com.engpeng.engpengsalesorder.database.priceSetting.PriceSettingDao;
import my.com.engpeng.engpengsalesorder.database.priceSetting.PriceSettingEntry;
import my.com.engpeng.engpengsalesorder.database.salesorder.SalesorderDao;
import my.com.engpeng.engpengsalesorder.database.salesorder.SalesorderEntry;
import my.com.engpeng.engpengsalesorder.database.tableList.TableInfoDao;
import my.com.engpeng.engpengsalesorder.database.tableList.TableInfoEntry;
import my.com.engpeng.engpengsalesorder.database.tempSalesorderDetail.TempSalesorderDetailDao;
import my.com.engpeng.engpengsalesorder.database.tempSalesorderDetail.TempSalesorderDetailEntry;

@Database(entities = {TableInfoEntry.class,
        BranchEntry.class,
        PriceSettingEntry.class,
        ItemPackingEntry.class,
        CustomerCompanyEntry.class,
        CustomerCompanyAddressEntry.class,
        TempSalesorderDetailEntry.class,
        SalesorderEntry.class,
        SalesorderDetailEntry.class,
        LogEntry.class},
        version = 1,
        exportSchema = false)

public abstract class AppDatabase extends RoomDatabase {

    private static final Object LOCK = new Object();
    private static final String DATABASE_NAME = "engpeng.db";
    private static AppDatabase sInstance;

    public static AppDatabase getInstance(Context context) {
        if (sInstance == null) {
            synchronized (LOCK) {
                sInstance = Room.databaseBuilder(context.getApplicationContext(),
                        AppDatabase.class, AppDatabase.DATABASE_NAME)
                        .build();
            }
        }
        return sInstance;
    }

    public abstract TableInfoDao tableInfoDao();
    public abstract BranchDao branchDao();
    public abstract PriceSettingDao priceSettingDao();
    public abstract ItemPackingDao itemPackingDao();
    public abstract CustomerCompanyDao customerCompanyDao();
    public abstract CustomerCompanyAddressDao customerCompanyAddressDao();
    public abstract TempSalesorderDetailDao tempSalesorderDetailDao();
    public abstract SalesorderDao salesorderDao();
    public abstract SalesorderDetailDao salesorderDetailDao();
    public abstract LogDao logDao();
}
