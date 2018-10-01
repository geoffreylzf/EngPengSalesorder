package my.com.engpeng.engpengsalesorder.model;

public class SalesInfo {
    private int draft, confirm, unupload;

    public SalesInfo(int draft, int confirm, int unupload) {
        this.draft = draft;
        this.confirm = confirm;
        this.unupload = unupload;
    }

    public int getDraft() {
        return draft;
    }

    public void setDraft(int draft) {
        this.draft = draft;
    }

    public int getConfirm() {
        return confirm;
    }

    public void setConfirm(int confirm) {
        this.confirm = confirm;
    }

    public int getUnupload() {
        return unupload;
    }

    public void setUnupload(int unupload) {
        this.unupload = unupload;
    }
}
