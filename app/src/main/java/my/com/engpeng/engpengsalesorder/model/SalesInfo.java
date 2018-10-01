package my.com.engpeng.engpengsalesorder.model;

public class SalesInfo {
    private int draft, confirm, unupload;
    private double draftAmt, confirmAmt;

    public SalesInfo(int draft, int confirm, int unupload, double draftAmt, double confirmAmt) {
        this.draft = draft;
        this.confirm = confirm;
        this.unupload = unupload;
        this.draftAmt = draftAmt;
        this.confirmAmt = confirmAmt;
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

    public double getDraftAmt() {
        return draftAmt;
    }

    public void setDraftAmt(double draftAmt) {
        this.draftAmt = draftAmt;
    }

    public double getConfirmAmt() {
        return confirmAmt;
    }

    public void setConfirmAmt(double confirmAmt) {
        this.confirmAmt = confirmAmt;
    }
}
