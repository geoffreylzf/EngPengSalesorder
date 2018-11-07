package my.com.engpeng.engpengsalesorder.asyncTask;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URL;
import java.util.List;

import my.com.engpeng.engpengsalesorder.database.AppDatabase;
import my.com.engpeng.engpengsalesorder.database.salesorder.SalesorderEntry;
import my.com.engpeng.engpengsalesorder.database.salesorderDetail.SalesorderDetailEntry;
import my.com.engpeng.engpengsalesorder.database.tableList.TableInfoEntry;
import my.com.engpeng.engpengsalesorder.model.ConnectionInfo;
import my.com.engpeng.engpengsalesorder.utilities.JsonUtils;
import my.com.engpeng.engpengsalesorder.utilities.NetworkUtils;
import my.com.engpeng.engpengsalesorder.utilities.StringUtils;

import static my.com.engpeng.engpengsalesorder.Global.SO_STATUS_CONFIRM;
import static my.com.engpeng.engpengsalesorder.Global.sPassword;
import static my.com.engpeng.engpengsalesorder.Global.sUsername;

public class DownloadHistoryAsyncTask extends AsyncTask<Void, Void, ConnectionInfo> {

    private String lastSyncDate;
    private boolean isLocal;
    private AppDatabase mDb;
    private DownloadHistoryAsyncTaskListener dhatListener;

    public DownloadHistoryAsyncTask(boolean isLocal, AppDatabase mDb, DownloadHistoryAsyncTaskListener dhatListener) {
        this.isLocal = isLocal;
        this.mDb = mDb;
        this.dhatListener = dhatListener;
    }

    public interface DownloadHistoryAsyncTaskListener {
        void initialProgress();

        void updateProgress(String table, double row, double total);

        void completeProgress();

        void errorProgress();
    }


    @Override
    protected ConnectionInfo doInBackground(Void... voids) {
        String username = sUsername;
        String password = sPassword;
        String data = "";

        dhatListener.initialProgress();

        Log.e("username", username);
        Log.e("password", password);

        lastSyncDate = StringUtils.getCurrentDateTime();
        URL url = NetworkUtils.buildUrl(NetworkUtils.MODULE_GET_HISTORY, isLocal);
        try {

            String json = NetworkUtils.sendPostToHttpUrl(url, username, password, data);
            Log.e("json", json);
            if (json != null && !json.equals("")) {
                if (JsonUtils.getAuthentication(json).isSuccess()) {
                    JSONArray salesorderArray = JsonUtils.getJsonArray(json, SalesorderEntry.TABLE_NAME);
                    if (salesorderArray != null) {
                        ClearConfirmedUploadedSalesorder();
                        return InsertSalesorder(salesorderArray);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return new ConnectionInfo(false);
    }

    private void ClearConfirmedUploadedSalesorder() {
        List<SalesorderEntry> salesorderEntryList = mDb.salesorderDao().loadAllSalesorderByStatusUpload(SO_STATUS_CONFIRM, 1);
        for (SalesorderEntry salesorderEntry : salesorderEntryList) {
            mDb.salesorderDetailDao().deleteAllBySalesorderId(salesorderEntry.getId());
            mDb.salesorderDao().deleteById(salesorderEntry.getId());
        }
    }

    private ConnectionInfo InsertSalesorder(JSONArray headArray) {
        try {
            if (headArray.length() != 0) {
                for (int i = 0; i < headArray.length(); i++) {
                    JSONObject headObject = headArray.getJSONObject(i);

                    SalesorderEntry salesorderEntry = new SalesorderEntry(headObject);
                    salesorderEntry.setStatus(SO_STATUS_CONFIRM);
                    salesorderEntry.setIsUpload(1);
                    Long salesorderId = mDb.salesorderDao().insertSalesorder(salesorderEntry);

                    JSONArray detailArray = headObject.getJSONArray(SalesorderDetailEntry.TABLE_NAME);
                    if (detailArray != null) {
                        for (int x = 0; x < detailArray.length(); x++) {
                            JSONObject detailObject = detailArray.getJSONObject(x);
                            SalesorderDetailEntry salesorderDetailEntry = new SalesorderDetailEntry(detailObject);
                            salesorderDetailEntry.setSalesorderId(salesorderId);
                            mDb.salesorderDetailDao().insertSalesorderDetail(salesorderDetailEntry);
                        }
                    }

                    TableInfoEntry tableInfo = new TableInfoEntry(SalesorderEntry.TABLE_NAME, lastSyncDate, (i + 1), headArray.length());
                    mDb.tableInfoDao().insertTableInfo(tableInfo);

                    double row = i + 1;
                    double total = (double) headArray.length();

                    dhatListener.updateProgress(SalesorderEntry.TABLE_NAME, row, total);
                }
            } else {
                TableInfoEntry tableInfo = new TableInfoEntry(SalesorderEntry.TABLE_NAME, lastSyncDate, 0, headArray.length());
                mDb.tableInfoDao().insertTableInfo(tableInfo);
            }
            return new ConnectionInfo(true);
        } catch (Exception e) {
            e.printStackTrace();
            return new ConnectionInfo(false);
        }
    }

    @Override
    protected void onPostExecute(ConnectionInfo connectionInfo) {

        if(connectionInfo.isSuccess()){
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            dhatListener.completeProgress();
        }else{
            dhatListener.errorProgress();
        }
    }
}
