package my.com.engpeng.engpengsalesorder.model;

public class UploadInfo {
    private boolean isSuccess;
    private String jsonStr;

    public UploadInfo(boolean isSuccess) {
        this.isSuccess = isSuccess;
    }

    public UploadInfo(boolean isSuccess, String jsonStr) {
        this.isSuccess = isSuccess;
        this.jsonStr = jsonStr;
    }

    public boolean isSuccess() {
        return isSuccess;
    }

    public void setSuccess(boolean success) {
        isSuccess = success;
    }

    public String getJsonStr() {
        return jsonStr;
    }

    public void setJsonStr(String jsonStr) {
        this.jsonStr = jsonStr;
    }
}
