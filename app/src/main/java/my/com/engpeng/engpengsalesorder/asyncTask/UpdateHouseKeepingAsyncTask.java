package my.com.engpeng.engpengsalesorder.asyncTask;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URL;
import java.util.List;

import my.com.engpeng.engpengsalesorder.database.AppDatabase;
import my.com.engpeng.engpengsalesorder.database.branch.BranchEntry;
import my.com.engpeng.engpengsalesorder.database.customerCompany.CustomerCompanyEntry;
import my.com.engpeng.engpengsalesorder.database.customerCompanyAddress.CustomerCompanyAddressEntry;
import my.com.engpeng.engpengsalesorder.database.itemCompany.ItemCompanyEntry;
import my.com.engpeng.engpengsalesorder.database.itemPacking.ItemPackingEntry;
import my.com.engpeng.engpengsalesorder.database.log.LogEntry;
import my.com.engpeng.engpengsalesorder.database.priceSetting.PriceSettingEntry;
import my.com.engpeng.engpengsalesorder.database.store.StoreEntry;
import my.com.engpeng.engpengsalesorder.database.tableList.TableInfoEntry;
import my.com.engpeng.engpengsalesorder.database.taxCode.TaxCodeEntry;
import my.com.engpeng.engpengsalesorder.database.taxItemMatching.TaxItemMatchingEntry;
import my.com.engpeng.engpengsalesorder.database.taxType.TaxTypeEntry;
import my.com.engpeng.engpengsalesorder.executor.AppExecutors;
import my.com.engpeng.engpengsalesorder.model.UpdateHkInfo;
import my.com.engpeng.engpengsalesorder.utilities.JsonUtils;
import my.com.engpeng.engpengsalesorder.utilities.NetworkUtils;
import my.com.engpeng.engpengsalesorder.utilities.StringUtils;

import static my.com.engpeng.engpengsalesorder.Global.ACTION_REFRESH;
import static my.com.engpeng.engpengsalesorder.Global.ACTION_UPDATE;
import static my.com.engpeng.engpengsalesorder.Global.LOG_TASK_UPDATE_HK;
import static my.com.engpeng.engpengsalesorder.Global.sPassword;
import static my.com.engpeng.engpengsalesorder.Global.sUniqueId;
import static my.com.engpeng.engpengsalesorder.Global.sUsername;

public class UpdateHouseKeepingAsyncTask extends AsyncTask<String, Void, UpdateHkInfo> {

    private int RECORD_PER_TRANSFER = 200;

    private String lastSyncDate;

    private List<TableInfoEntry> tableInfoList;
    private AppDatabase mDb;
    private boolean isLocal;
    private String action;
    private UpdateHouseKeepingAsyncTaskListener uhkatListener;
    private int updateCount;
    private UpdateHkInfo updateHkInfo;
    private boolean isAuto;

    public interface UpdateHouseKeepingAsyncTaskListener {

        void initialProgress(String table);

        void updateProgress(String type, double row, double total);

        void completeProgress();

        void errorProgress();
    }

    public UpdateHouseKeepingAsyncTask(
            AppDatabase mDb,
            List<TableInfoEntry> tableInfoList,
            String action,
            boolean isLocal,
            UpdateHouseKeepingAsyncTaskListener uhkatListener,
            int updateCount,
            UpdateHkInfo updateHkInfo,
            boolean isAuto) {

        this.mDb = mDb;
        this.tableInfoList = tableInfoList;
        this.action = action;
        this.isLocal = isLocal;
        this.uhkatListener = uhkatListener;
        this.updateCount = updateCount;
        this.updateHkInfo = updateHkInfo;
        this.isAuto = isAuto;
    }

    @Override
    protected UpdateHkInfo doInBackground(String... strings) {
        String username = sUsername;
        String password = sPassword;
        String uniqueId = sUniqueId;
        String data = "";

        String type = null;
        for (TableInfoEntry tableInfoEntry : tableInfoList) {
            if (!tableInfoEntry.isUpdated()) {
                type = tableInfoEntry.getType();
                break;
            }
        }
        if (type == null) {
            return new UpdateHkInfo(true, true);
        }

        data += NetworkUtils.buildParam(NetworkUtils.PARAM_UNIQUE_ID, uniqueId);
        data += NetworkUtils.buildParam(NetworkUtils.PARAM_TYPE, type);
        data += NetworkUtils.buildParam(NetworkUtils.PARAM_ACTION, action);

        if (updateHkInfo != null) {
            data += NetworkUtils.buildParam(NetworkUtils.PARAM_LIMIT_START, String.valueOf(updateHkInfo.getLimitStart()));
            data += NetworkUtils.buildParam(NetworkUtils.PARAM_LIMIT_LENGTH, String.valueOf(updateHkInfo.getLimitLength()));
            data += NetworkUtils.buildParam(NetworkUtils.PARAM_LOG_ID, String.valueOf(updateHkInfo.getLogId()));
        } else {
            uhkatListener.initialProgress(type);
        }

        lastSyncDate = StringUtils.getCurrentDateTime();
        URL url = NetworkUtils.buildUrl(NetworkUtils.MODULE_GET_HOUSE_KEEPING, isLocal);
        try {
            String json = NetworkUtils.sendPostToHttpUrl(url, username, password, data);
            if (json != null && !json.equals("")) {
                if (JsonUtils.getAuthentication(json).isSuccess()) {

                    String tableName = JsonUtils.getString(json, JsonUtils.TYPE);
                    String action = JsonUtils.getString(json, JsonUtils.ACTION);

                    long logId = JsonUtils.getLong(json, JsonUtils.LOG_ID);
                    int count = JsonUtils.getInt(json, JsonUtils.COUNT);


                    if (action.equals(ACTION_REFRESH)) {
                        if (logId > 0 && count > 0) {

                            switch (tableName){
                                case BranchEntry.TABLE_NAME:
                                    mDb.branchDao().deleteAll();
                                    break;
                                case CustomerCompanyAddressEntry.TABLE_NAME:
                                    mDb.customerCompanyAddressDao().deleteAll();
                                    break;
                                case CustomerCompanyEntry.TABLE_NAME:
                                    mDb.customerCompanyDao().deleteAll();
                                    break;
                                case ItemCompanyEntry.TABLE_NAME:
                                    mDb.itemCompanyDao().deleteAll();
                                    break;
                                case ItemPackingEntry.TABLE_NAME:
                                    mDb.itemPackingDao().deleteAll();
                                    break;
                                case PriceSettingEntry.TABLE_NAME:
                                    mDb.priceSettingDao().deleteAll();
                                    break;
                                case StoreEntry.TABLE_NAME:
                                    mDb.storeDao().deleteAll();
                                    break;
                                case TaxCodeEntry.TABLE_NAME:
                                    mDb.taxCodeDao().deleteAll();
                                    break;
                                case TaxItemMatchingEntry.TABLE_NAME:
                                    mDb.taxItemMatchingDao().deleteAll();
                                    break;
                                case TaxTypeEntry.TABLE_NAME:
                                    mDb.taxTypeDao().deleteAll();
                                    break;
                            }

                            UpdateHkInfo updateHkInfo = new UpdateHkInfo(true, tableName, count, logId);
                            updateHkInfo.setCount(count);
                            updateHkInfo.setLogId(logId);
                            updateHkInfo.setPartial(true);
                            updateHkInfo.setLimitStart(0);
                            updateHkInfo.setLimitLength(RECORD_PER_TRANSFER);
                            return updateHkInfo;
                        } else {
                            return InsertPartial(json, tableName, updateHkInfo);
                        }
                    } else if (action.equals(ACTION_UPDATE)) {
                        return Insert(json, tableName);
                    }
                } else {
                    return new UpdateHkInfo(false);
                }
            } else {
                return new UpdateHkInfo(false);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new UpdateHkInfo(false);
        }
        return new UpdateHkInfo(false);
    }

    private UpdateHkInfo Insert(String jsonStr, String tableName) {
        try {
            JSONObject json = new JSONObject(jsonStr);
            JSONArray jsonArray = json.getJSONArray(tableName);

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);

                insertTable(tableName, jsonObject);

                TableInfoEntry tableInfo = new TableInfoEntry(tableName, lastSyncDate, (i + 1), jsonArray.length());
                mDb.tableInfoDao().insertTableInfo(tableInfo);

                double row = i + 1;
                double total = (double) jsonArray.length();

                uhkatListener.updateProgress(tableName, row, total);
            }

            if (jsonArray.length() == 0) {
                TableInfoEntry tableInfo = new TableInfoEntry(tableName, lastSyncDate, 0, 0);
                mDb.tableInfoDao().insertTableInfo(tableInfo);
            }
            updateCount += jsonArray.length();
            return new UpdateHkInfo(true, tableName);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new UpdateHkInfo(false);
    }

    private UpdateHkInfo InsertPartial(String jsonStr, String tableName, UpdateHkInfo updateHkInfo) {
        try {
            JSONObject json = new JSONObject(jsonStr);
            JSONArray jsonArray = json.getJSONArray(tableName);

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);

                insertTable(tableName, jsonObject);

                TableInfoEntry tableInfo = new TableInfoEntry(tableName, lastSyncDate, (updateHkInfo.getLimitStart() + i + 1), updateHkInfo.getCount());
                mDb.tableInfoDao().insertTableInfo(tableInfo);

            }
            updateCount += jsonArray.length();

            if (jsonArray.length() != 0) {
                int newLimitStart = updateHkInfo.getLimitStart() + updateHkInfo.getLimitLength();
                updateHkInfo.setLimitStart(newLimitStart);
                uhkatListener.updateProgress(tableName, (double) updateHkInfo.getLimitStart(), (double) updateHkInfo.getCount());
                return updateHkInfo;
            } else {
                return new UpdateHkInfo(true, tableName);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return new UpdateHkInfo(false);
    }

    @Override
    protected void onPostExecute(UpdateHkInfo updateHkInfo) {

        if (updateHkInfo.isSuccess()) {
            if (updateHkInfo.isEnd()) {
                delayThread();
                uhkatListener.completeProgress();
                AppExecutors.getInstance().diskIO().execute(new Runnable() {
                    @Override
                    public void run() {
                        String remark;
                        if (isAuto) {
                            remark = updateCount + " data auto received";
                        } else {
                            remark = updateCount + " data received";
                        }
                        LogEntry logEntry = new LogEntry(LOG_TASK_UPDATE_HK, StringUtils.getCurrentDateTime(), remark);
                        mDb.logDao().insertLog(logEntry);
                    }
                });
            } else if (updateHkInfo.isPartial()) {
                UpdateHouseKeepingAsyncTask updateHouseKeepingAsyncTask = new UpdateHouseKeepingAsyncTask(mDb, tableInfoList, action, isLocal, uhkatListener, updateCount, updateHkInfo, isAuto);
                updateHouseKeepingAsyncTask.execute();

            } else {
                //continue next table
                setTableListUpdate(updateHkInfo.getTableName());
                UpdateHouseKeepingAsyncTask updateHouseKeepingAsyncTask = new UpdateHouseKeepingAsyncTask(mDb, tableInfoList, action, isLocal, uhkatListener, updateCount, null, isAuto);
                updateHouseKeepingAsyncTask.execute();
            }
        } else {
            delayThread();
            uhkatListener.errorProgress();
            AppExecutors.getInstance().diskIO().execute(new Runnable() {
                @Override
                public void run() {
                    String remark;
                    if (isAuto) {
                        remark = updateCount + " data auto received with error";
                    } else {
                        remark = updateCount + " data received with error";
                    }
                    LogEntry logEntry = new LogEntry(LOG_TASK_UPDATE_HK, StringUtils.getCurrentDateTime(), remark);
                    mDb.logDao().insertLog(logEntry);
                }
            });
        }
    }

    private void insertTable(String tableName, JSONObject jsonObject){
        switch (tableName){
            case BranchEntry.TABLE_NAME:
                mDb.branchDao().insertBranch(new BranchEntry(jsonObject));
                break;
            case CustomerCompanyAddressEntry.TABLE_NAME:
                mDb.customerCompanyAddressDao().insertCustomerCompanyAddress(new CustomerCompanyAddressEntry(jsonObject));
                break;
            case CustomerCompanyEntry.TABLE_NAME:
                mDb.customerCompanyDao().insertCustomerCompany(new CustomerCompanyEntry(jsonObject));
                break;
            case ItemCompanyEntry.TABLE_NAME:
                mDb.itemCompanyDao().insertItemCompany(new ItemCompanyEntry(jsonObject));
                break;
            case ItemPackingEntry.TABLE_NAME:
                mDb.itemPackingDao().insertItemPacking(new ItemPackingEntry(jsonObject));
                break;
            case PriceSettingEntry.TABLE_NAME:
                mDb.priceSettingDao().insertPriceSetting(new PriceSettingEntry(jsonObject));
                break;
            case StoreEntry.TABLE_NAME:
                mDb.storeDao().insertStore(new StoreEntry(jsonObject));
                break;
            case TaxCodeEntry.TABLE_NAME:
                mDb.taxCodeDao().insertTaxCode(new TaxCodeEntry(jsonObject));
                break;
            case TaxItemMatchingEntry.TABLE_NAME:
                mDb.taxItemMatchingDao().insertTaxItemMatching(new TaxItemMatchingEntry(jsonObject));
                break;
            case TaxTypeEntry.TABLE_NAME:
                mDb.taxTypeDao().insertTaxType(new TaxTypeEntry(jsonObject));
                break;
        }
    }

    private void setTableListUpdate(String table_info_type) {
        for (TableInfoEntry tableInfo : tableInfoList) {
            if (tableInfo.getType().equals(table_info_type)) {
                tableInfo.setUpdated(true);
                break;
            }
        }
    }

    private void delayThread() {
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
