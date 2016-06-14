package de.njsm.stocks.setup;

import android.content.Context;

import de.njsm.stocks.R;

public class Result {

    protected String title;
    protected String message;
    protected boolean success;

    public Result(String title, String message, boolean success) {
        this.title = title;
        this.message = message;
        this.success = success;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public static Result getSuccess(Context c) {
        return  new Result(c.getResources().getString(R.string.dialog_success),
                c.getResources().getString(R.string.dialog_finished),
                true);
    }
}
