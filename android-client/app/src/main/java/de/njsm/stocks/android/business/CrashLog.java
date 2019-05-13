package de.njsm.stocks.android.business;

import java.io.File;

public class CrashLog {

    private String name;

    private String date;

    private String content;

    private File file;

    public CrashLog(String name, String date, String content, File file) {
        this.name = name;
        this.date = date;
        this.content = content;
        this.file = file;
    }

    public CrashLog(File f) {
        this("<?>", "-", "", f);
    }

    public String getName() {
        return name;
    }

    public String getDate() {
        return date;
    }

    public String getContent() {
        return content;
    }

    public File getFile() {
        return file;
    }
}
