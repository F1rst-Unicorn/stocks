package de.njsm.stocks.frontend.crashlog;

import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.ListAdapter;
import android.widget.SimpleAdapter;
import de.njsm.stocks.Config;
import de.njsm.stocks.R;
import org.apache.commons.io.IOUtils;

import java.io.*;
import java.util.*;

public class ListCrashLogsTask extends AsyncTask<Void, Void, Integer> {

    private File crashLogDirectory;

    private CrashLogListFragment fragment;

    private ListAdapter adapter;

    public ListCrashLogsTask(File crashLogDirectory, CrashLogListFragment fragment) {
        this.crashLogDirectory = crashLogDirectory;
        this.fragment = fragment;
    }

    @Override
    protected Integer doInBackground(Void... params) {

        if (android.os.Debug.isDebuggerConnected()) {
            android.os.Debug.waitForDebugger();
        }

        File[] crashlogs = crashLogDirectory.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.startsWith("crashlog_");
            }
        });

        List<Map<String, String>> entries = computeView(crashlogs);
        adapter = new SimpleAdapter(fragment.getActivity(), entries,
                R.layout.item_crash_log,
                new String[] {"name", "date"},
                new int[] {R.id.item_crash_log_name, R.id.item_crash_log_date});

        fragment.setCrashlogs(crashlogs);
        return crashlogs.length;
    }


    @NonNull
    private List<Map<String, String>> computeView(File[] crashlogs) {
        List<Map<String, String>> entries = new LinkedList<>();
        for (File report : crashlogs) {
            entries.add(getMetaInformationOfReport(report));
        }
        return entries;
    }

    private Map<String, String> getMetaInformationOfReport(File report) {

        HashMap<String, String> result = new HashMap<>();
        result.put("name", "<?>");
        result.put("date", Config.TECHNICAL_DATE_FORMAT.format(new Date(0L)));

        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream(report);
            String reportContent = IOUtils.toString(inputStream);
            String[] lines = reportContent.split("\n");

            if (lines.length >= 2) {
                result.put("name", lines[0]);
                result.put("date", lines[1]);
            }
        } catch (IOException e) {
            Log.w(Config.log, "Crash report has been removed", e);
        } finally {
            IOUtils.closeQuietly(inputStream);
        }
        return result;
    }

    @Override
    protected void onPreExecute() {
        fragment.setListShown(false);
    }

    @Override
    protected void onPostExecute(Integer numberOfCrashlogs) {
        fragment.setEmptyText(fragment.getResources().getString(R.string.text_no_crash_logs));
        fragment.getListView().setAdapter(adapter);
        fragment.setListShown(true);
    }
}

