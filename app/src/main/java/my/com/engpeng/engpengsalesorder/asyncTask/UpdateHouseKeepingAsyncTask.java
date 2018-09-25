package my.com.engpeng.engpengsalesorder.asyncTask;

import android.content.Context;
import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URL;
import java.util.List;

import my.com.engpeng.engpengsalesorder.Global;
import my.com.engpeng.engpengsalesorder.database.AppDatabase;
import my.com.engpeng.engpengsalesorder.database.branch.BranchEntry;
import my.com.engpeng.engpengsalesorder.database.customerCompany.CustomerCompanyEntry;
import my.com.engpeng.engpengsalesorder.database.customerCompanyAddress.CustomerCompanyAddressEntry;
import my.com.engpeng.engpengsalesorder.database.itemPacking.ItemPackingEntry;
import my.com.engpeng.engpengsalesorder.database.priceSetting.PriceSettingEntry;
import my.com.engpeng.engpengsalesorder.database.tableList.TableInfoEntry;
import my.com.engpeng.engpengsalesorder.utilities.JsonUtils;
import my.com.engpeng.engpengsalesorder.utilities.NetworkUtils;
import my.com.engpeng.engpengsalesorder.utilities.StringUtils;

import static my.com.engpeng.engpengsalesorder.Global.ACTION_REFRESH;
import static my.com.engpeng.engpengsalesorder.Global.ACTION_UPDATE;
import static my.com.engpeng.engpengsalesorder.Global.sPassword;
import static my.com.engpeng.engpengsalesorder.Global.sUniqueId;
import static my.com.engpeng.engpengsalesorder.Global.sUsername;

public class UpdateHouseKeepingAsyncTask extends AsyncTask<String, Void, String> {

    private String last_sync_date;

    private List<TableInfoEntry> tableInfoList;
    private Context context;
    private AppDatabase mDb;
    private boolean isLocal;
    private String action;
    private UpdateHouseKeepingAsyncTaskListener uhkatListener;

    public interface UpdateHouseKeepingAsyncTaskListener {

        void initialProgress(String table);

        void updateProgress(String type, double row, double total);

        void completeProgress();
    }

    public UpdateHouseKeepingAsyncTask(Context context,
                                       AppDatabase mDb,
                                       List<TableInfoEntry> tableInfoList,
                                       String action,
                                       boolean isLocal,
                                       UpdateHouseKeepingAsyncTaskListener uhkatListener) {
        this.context = context;
        this.mDb = mDb;
        this.tableInfoList = tableInfoList;
        this.action = action;
        this.isLocal = isLocal;
        this.uhkatListener = uhkatListener;
    }

    @Override
    protected String doInBackground(String... strings) {
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
            return null;
        }

        data += NetworkUtils.buildParam(NetworkUtils.PARAM_UNIQUE_ID, uniqueId);
        data += NetworkUtils.buildParam(NetworkUtils.PARAM_TYPE, type);
        data += NetworkUtils.buildParam(NetworkUtils.PARAM_ACTION, action);

        uhkatListener.initialProgress(type);

        last_sync_date = StringUtils.getCurrentDateTime();
        URL url = NetworkUtils.buildUrl(NetworkUtils.MODULE_GET_HOUSE_KEEPING, isLocal);
        try {
            String json = NetworkUtils.sendPostToHttpUrl(url, username, password, data);
            if (json != null && !json.equals("")) {
                if (JsonUtils.getAuthentication(json).isSuccess()) {
                    String table_info_type = JsonUtils.getString(json, JsonUtils.TYPE);
                    String action = JsonUtils.getString(json, JsonUtils.ACTION);

                    if (table_info_type.equals(CustomerCompanyEntry.TABLE_NAME)) {
                        if (action.equals(ACTION_REFRESH)) {
                            mDb.customerCompanyDao().deleteAll();
                        }
                        return InsertCustomerCompany(json);
                    }
                    if (table_info_type.equals(CustomerCompanyAddressEntry.TABLE_NAME)) {
                        if (action.equals(ACTION_REFRESH)) {
                            mDb.customerCompanyAddressDao().deleteAll();
                        }
                        return InsertCustomerCompanyAddress(json);
                    }
                    if (table_info_type.equals(ItemPackingEntry.TABLE_NAME)) {
                        if (action.equals(ACTION_REFRESH)) {
                            mDb.itemPackingDao().deleteAll();
                        }
                        return InsertItemPacking(json);
                    }
                    if (table_info_type.equals(PriceSettingEntry.TABLE_NAME)) {
                        if (action.equals(ACTION_REFRESH)) {
                            mDb.priceSettingDao().deleteAll();
                        }
                        return InsertPriceSetting(json);
                    }
                    if (table_info_type.equals(BranchEntry.TABLE_NAME)) {
                        if (action.equals(ACTION_REFRESH)) {
                            mDb.branchDao().deleteAll();
                        }
                        return InsertBranch(json);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private String InsertPriceSetting(String jsonStr) {
        try {
            JSONObject json = new JSONObject(jsonStr);
            JSONArray jsonArray = json.getJSONArray(PriceSettingEntry.TABLE_NAME);

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);

                PriceSettingEntry priceSetting = new PriceSettingEntry(jsonObject);

                mDb.priceSettingDao().insertPriceSetting(priceSetting);

                TableInfoEntry tableInfo = new TableInfoEntry(PriceSettingEntry.TABLE_NAME, last_sync_date, (i + 1), jsonArray.length());
                mDb.tableInfoDao().insertTableInfo(tableInfo);

                double row = i + 1;
                double total = (double) jsonArray.length();
                uhkatListener.updateProgress(PriceSettingEntry.TABLE_NAME, row, total);
            }
            return PriceSettingEntry.TABLE_NAME;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private String InsertItemPacking(String jsonStr) {
        try {
            JSONObject json = new JSONObject(jsonStr);
            JSONArray jsonArray = json.getJSONArray(ItemPackingEntry.TABLE_NAME);

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);

                ItemPackingEntry itemPacking = new ItemPackingEntry(jsonObject);

                mDb.itemPackingDao().insertItemPacking(itemPacking);

                TableInfoEntry tableInfo = new TableInfoEntry(ItemPackingEntry.TABLE_NAME, last_sync_date, (i + 1), jsonArray.length());
                mDb.tableInfoDao().insertTableInfo(tableInfo);

                double row = i + 1;
                double total = (double) jsonArray.length();

                uhkatListener.updateProgress(ItemPackingEntry.TABLE_NAME, row, total);
            }
            return ItemPackingEntry.TABLE_NAME;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private String InsertCustomerCompany(String jsonStr) {
        try {
            JSONObject json = new JSONObject(jsonStr);
            JSONArray jsonArray = json.getJSONArray(CustomerCompanyEntry.TABLE_NAME);

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);

                CustomerCompanyEntry customerCompanyEntry = new CustomerCompanyEntry(jsonObject);

                mDb.customerCompanyDao().insertCustomerCompany(customerCompanyEntry);

                TableInfoEntry tableInfo = new TableInfoEntry(CustomerCompanyEntry.TABLE_NAME, last_sync_date, (i + 1), jsonArray.length());
                mDb.tableInfoDao().insertTableInfo(tableInfo);

                double row = i + 1;
                double total = (double) jsonArray.length();

                uhkatListener.updateProgress(CustomerCompanyEntry.TABLE_NAME, row, total);
            }
            return CustomerCompanyEntry.TABLE_NAME;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private String InsertCustomerCompanyAddress(String jsonStr) {
        try {
            JSONObject json = new JSONObject(jsonStr);
            JSONArray jsonArray = json.getJSONArray(CustomerCompanyAddressEntry.TABLE_NAME);

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);

                CustomerCompanyAddressEntry customerCompanyAddressEntry = new CustomerCompanyAddressEntry(jsonObject);

                mDb.customerCompanyAddressDao().insertCustomerCompanyAddress(customerCompanyAddressEntry);

                TableInfoEntry tableInfo = new TableInfoEntry(CustomerCompanyAddressEntry.TABLE_NAME, last_sync_date, (i + 1), jsonArray.length());
                mDb.tableInfoDao().insertTableInfo(tableInfo);

                double row = i + 1;
                double total = (double) jsonArray.length();

                uhkatListener.updateProgress(CustomerCompanyAddressEntry.TABLE_NAME, row, total);
            }
            return CustomerCompanyAddressEntry.TABLE_NAME;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private String InsertBranch(String jsonStr) {
        try {
            JSONObject json = new JSONObject(jsonStr);
            JSONArray jsonArray = json.getJSONArray(BranchEntry.TABLE_NAME);

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);

                BranchEntry branchEntry = new BranchEntry(jsonObject);

                mDb.branchDao().insertBranch(branchEntry);

                TableInfoEntry tableInfo = new TableInfoEntry(BranchEntry.TABLE_NAME, last_sync_date, (i + 1), jsonArray.length());
                mDb.tableInfoDao().insertTableInfo(tableInfo);

                double row = i + 1;
                double total = (double) jsonArray.length();

                uhkatListener.updateProgress(BranchEntry.TABLE_NAME, row, total);
            }
            return BranchEntry.TABLE_NAME;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(String table_info_type) {
        if (table_info_type != null && !table_info_type.equals("")) {
            setTableListUpdate(table_info_type);

            UpdateHouseKeepingAsyncTask updateHouseKeepingAsyncTask = new UpdateHouseKeepingAsyncTask(context, mDb, tableInfoList, action, isLocal, uhkatListener);
            updateHouseKeepingAsyncTask.execute();
        } else {
            uhkatListener.completeProgress();
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
}
