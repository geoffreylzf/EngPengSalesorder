package my.com.engpeng.engpengsalesorder.model;

public class Status {
    private boolean success;
    private String message;

    public Status(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }
}