package my.com.engpeng.engpengsalesorder.asyncTask;

import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;

import java.net.URL;
import java.util.List;

import my.com.engpeng.engpengsalesorder.database.AppDatabase;
import my.com.engpeng.engpengsalesorder.database.salesorder.SalesorderEntry;
import my.com.engpeng.engpengsalesorder.database.salesorderDetail.SalesorderDetailEntry;
import my.com.engpeng.engpengsalesorder.executor.AppExecutors;
import my.com.engpeng.engpengsalesorder.utilities.JsonUtils;
import my.com.engpeng.engpengsalesorder.utilities.NetworkUtils;

import static my.com.engpeng.engpengsalesorder.Global.SO_STATUS_CONFIRM;
import static my.com.engpeng.engpengsalesorder.Global.sPassword;
import static my.com.engpeng.engpengsalesorder.Global.sUniqueId;
import static my.com.engpeng.engpengsalesorder.Global.sUsername;
import static my.com.engpeng.engpengsalesorder.utilities.JsonUtils.IMPORTED_MOBILE_IDS;

public class UploadAsyncTask extends AsyncTask<Void, Void, String> {

    private AppDatabase mDb;
    private boolean isLocal;
    private UploadAsyncTaskListener uatListener;

    public interface UploadAsyncTaskListener {
        void completeProgress();
    }

    public UploadAsyncTask(AppDatabase mDb, boolean isLocal, UploadAsyncTaskListener uatListener) {
        this.mDb = mDb;
        this.isLocal = isLocal;
        this.uatListener = uatListener;
    }

    @Override
    protected String doInBackground(Void... voids) {
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

        URL url = NetworkUtils.buildUrl(NetworkUtils.MODULE_UPLOAD, isLocal);
        try {
            String json = NetworkUtils.sendPostToHttpUrl(url, username, password, data);
            if (json != null && !json.equals("")) {
                if (JsonUtils.getAuthentication(json).isSuccess()) {
                    return json;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(String json) {
        try {
            JSONArray jsonArray = JsonUtils.getJsonArray(json, IMPORTED_MOBILE_IDS);
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
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        uatListener.completeProgress();
    }
}
