package de.njsm.stocks.frontend.setup;

import de.njsm.stocks.R;

public class Result {

    private final int title;

    private final int message;

    private final boolean success;

    public Result(int title, int message, boolean success) {
        this.title = title;
        this.message = message;
        this.success = success;
    }

    public int getTitle() {
        return title;
    }

    public int getMessage() {
        return message;
    }

    public boolean isSuccess() {
        return success;
    }

    public static Result getSuccess() {
        return new Result(R.string.dialog_success,
                R.string.dialog_finished,
                true);
    }
}
