package de.njsm.stocks;


import android.app.ListFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import org.apache.commons.io.IOUtils;

import java.io.*;
import java.util.*;

public class CrashLogListFragment extends ListFragment
        implements AdapterView.OnItemLongClickListener {

    protected ListView mList;

    private File[] crashlogs;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setListContent();
    }

    private void setListContent() {
        setListShown(false);
        crashlogs = getActivity().getFilesDir().listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.startsWith("crashlog_");
            }
        });

        List<Map<String, String>> entries = new LinkedList<>();

        for (File report : crashlogs) {
            entries.add(getMetaInformation(report));
        }

        SimpleAdapter adapter = new SimpleAdapter(getActivity(), entries,
                R.layout.item_crash_log,
                new String[] {"name", "date"},
                new int[] {R.id.item_crash_log_name, R.id.item_crash_log_date});
        getListView().setAdapter(adapter);
        setListShown(true);
    }

    private Map<String, String> getMetaInformation(File report) {

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
    public void onStart() {
        super.onStart();
        mList = getListView();
        mList.setOnItemLongClickListener(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        mList.setOnItemLongClickListener(null);
        mList = null;
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        FileInputStream stream = null;
        try {
            stream = new FileInputStream(crashlogs[position]);
            String content = IOUtils.toString(stream);
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT, content);
            sendIntent.setType("text/plain");
            startActivity(sendIntent);
        } catch (IOException e) {
            Log.e(Config.log, "", e);
        } finally {
            IOUtils.closeQuietly(stream);
        }
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> adapterView, View view, final int i, long l) {
        String message = getResources().getString(R.string.dialog_remove_crash_log);
        new AlertDialog.Builder(getActivity())
                .setTitle(getResources().getString(R.string.title_remove))
                .setMessage(message)
                .setPositiveButton(getResources().getString(android.R.string.yes), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                    crashlogs[i].delete();
                    CrashLogListFragment.this.setListContent();
                    }
                })
                .setNegativeButton(getResources().getString(android.R.string.no), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {}
                })
                .show();
        return true;
    }
}
