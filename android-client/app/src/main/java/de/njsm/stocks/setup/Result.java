package de.njsm.stocks.setup;

public class Result {

    protected String title;
    protected String message;
    protected boolean success;

    public static Result SUCCESS = new Result("Success", "Registration finished", true);

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
}
