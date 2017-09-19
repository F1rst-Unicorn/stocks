package de.njsm.stocks.frontend.crashlog;

import android.content.ContextWrapper;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.ListAdapter;
import android.widget.SimpleAdapter;
import de.njsm.stocks.R;
import de.njsm.stocks.backend.util.AbstractAsyncTask;
import de.njsm.stocks.backend.util.Config;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

public class ListCrashLogsTask extends AbstractAsyncTask<Void, Void, Integer> {

    private File crashLogDirectory;

    private CrashLogListFragment fragment;

    private ListAdapter adapter;

    public ListCrashLogsTask(ContextWrapper context,
                             File crashLogDirectory,
                             CrashLogListFragment fragment) {
        super(context.getFilesDir());
        this.crashLogDirectory = crashLogDirectory;
        this.fragment = fragment;
    }

    @Override
    protected Integer doInBackgroundInternally(Void[] params) {

        File[] crashlogs = crashLogDirectory.listFiles((File dir, String name) -> name.startsWith("crashlog_"));

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
            Log.w(Config.LOG_TAG, "Crash report has been removed", e);
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

