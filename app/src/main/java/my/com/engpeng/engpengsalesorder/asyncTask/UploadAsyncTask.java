package my.com.engpeng.engpengsalesorder.asyncTask;

import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;

import java.net.URL;
import java.util.List;

import my.com.engpeng.engpengsalesorder.database.AppDatabase;
import my.com.engpeng.engpengsalesorder.database.log.LogEntry;
import my.com.engpeng.engpengsalesorder.database.salesorder.SalesorderEntry;
import my.com.engpeng.engpengsalesorder.database.salesorderDetail.SalesorderDetailEntry;
import my.com.engpeng.engpengsalesorder.executor.AppExecutors;
import my.com.engpeng.engpengsalesorder.model.UploadInfo;
import my.com.engpeng.engpengsalesorder.utilities.JsonUtils;
import my.com.engpeng.engpengsalesorder.utilities.NetworkUtils;
import my.com.engpeng.engpengsalesorder.utilities.StringUtils;

import static my.com.engpeng.engpengsalesorder.Global.LOG_TASK_UPLOAD;
import static my.com.engpeng.engpengsalesorder.Global.SO_STATUS_CONFIRM;
import static my.com.engpeng.engpengsalesorder.Global.sPassword;
import static my.com.engpeng.engpengsalesorder.Global.sUniqueId;
import static my.com.engpeng.engpengsalesorder.Global.sUsername;
import static my.com.engpeng.engpengsalesorder.utilities.JsonUtils.IMPORTED_MOBILE_IDS;

public class UploadAsyncTask extends AsyncTask<Void, Void, UploadInfo> {

    private AppDatabase mDb;
    private boolean isLocal;
    private UploadAsyncTaskListener uatListener;
    private boolean isAuto;

    public interface UploadAsyncTaskListener {
        void initialProgress();

        void completeProgress();

        void errorProgress();
    }

    public UploadAsyncTask(AppDatabase mDb, boolean isLocal, UploadAsyncTaskListener uatListener, boolean isAuto) {
        this.mDb = mDb;
        this.isLocal = isLocal;
        this.uatListener = uatListener;
        this.isAuto = isAuto;
    }

    @Override
    protected UploadInfo doInBackground(Void... voids) {
        String username = sUsername;
        String password = sPassword;
        String uniqueId = sUniqueId;
        String data = "";

        data += NetworkUtils.buildParam(NetworkUtils.PARAM_UNIQUE_ID, uniqueId);

        List<SalesorderEntry> salesorderEntryList = mDb.salesorderDao().loadAllSalesorderByStatusUpload(SO_STATUS_CONFIRM, 0);
        for (int i = 0; i < salesorderEntryList.size(); i++) {
            List<SalesorderDetailEntry> salesorderDetails = mDb.salesorderDetailDao().loadAllSalesorderDetailsBySalesorderId(salesorderEntryList.get(i).getId());
            salesorderEntryList.get(i).setSalesorderDetails(salesorderDetails);
        }
        Gson gson = new Gson();
        String salesorders = gson.toJson(salesorderEntryList);
        data += NetworkUtils.buildParam(SalesorderEntry.TABLE_NAME, salesorders);

        uatListener.initialProgress();

        URL url = NetworkUtils.buildUrl(NetworkUtils.MODULE_UPLOAD, isLocal);
        try {
            String json = NetworkUtils.sendPostToHttpUrl(url, username, password, data);
            if (json != null && !json.equals("")) {
                if (JsonUtils.getAuthentication(json).isSuccess()) {
                    return new UploadInfo(true, json);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new UploadInfo(false);
    }

    @Override
    protected void onPostExecute(UploadInfo uploadInfo) {
        if (uploadInfo.isSuccess()) {
            try {
                final JSONArray jsonArray = JsonUtils.getJsonArray(uploadInfo.getJsonStr(), IMPORTED_MOBILE_IDS);
                if (jsonArray != null) {
                    for (int i = 0; i < jsonArray.length(); i++) {
                        final long id = jsonArray.getLong(i);
                        AppExecutors.getInstance().diskIO().execute(new Runnable() {
                            @Override
                            public void run() {
                                SalesorderEntry salesorderEntry = mDb.salesorderDao().loadSalesorder(id);
                                salesorderEntry.setIsUpload(1);
                                mDb.salesorderDao().insertSalesorder(salesorderEntry);
                            }
                        });
                    }

                    AppExecutors.getInstance().diskIO().execute(new Runnable() {
                        @Override
                        public void run() {
                            String remark;
                            if (isAuto) {
                                remark = jsonArray.length() + " salesorder(s) auto uploaded";
                            } else {
                                remark = jsonArray.length() + " salesorder(s) uploaded";
                            }
                            LogEntry logEntry = new LogEntry(LOG_TASK_UPLOAD, StringUtils.getCurrentDateTime(), remark);
                            mDb.logDao().insertLog(logEntry);
                        }
                    });
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            uatListener.completeProgress();
        } else {
            uatListener.errorProgress();
            AppExecutors.getInstance().diskIO().execute(new Runnable() {
                @Override
                public void run() {
                    String remark;
                    if (isAuto) {
                        remark = "Auto upload error";
                    } else {
                        remark = "Upload error";
                    }
                    LogEntry logEntry = new LogEntry(LOG_TASK_UPLOAD, StringUtils.getCurrentDateTime(), remark);
                    mDb.logDao().insertLog(logEntry);
                }
            });
        }

    }
}
