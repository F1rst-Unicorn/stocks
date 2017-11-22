package de.njsm.stocks.frontend.crashlog;


import android.app.ListFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import de.njsm.stocks.backend.util.Config;
import de.njsm.stocks.R;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class CrashLogListFragment extends ListFragment
        implements AdapterView.OnItemLongClickListener {

    private ListView mList;

    private File[] crashlogs;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
        mList = getListView();
        mList.setOnItemLongClickListener(this);
        ListCrashLogsTask asyncTask = new ListCrashLogsTask(getActivity(), getActivity().getFilesDir(), this);
        asyncTask.execute();
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
            Log.e(Config.LOG_TAG, "", e);
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
                .setPositiveButton(getResources().getString(android.R.string.yes), (DialogInterface dialog, int whichButton) -> {
                    crashlogs[i].delete();
                    ListCrashLogsTask asyncTask = new ListCrashLogsTask(getActivity(),
                            getActivity().getFilesDir(), CrashLogListFragment.this);
                    asyncTask.execute();
                })
                .setNegativeButton(getResources().getString(android.R.string.no), (DialogInterface dialog, int whichButton) -> {})
                .show();
        return true;
    }

    public void setCrashlogs(File[] crashlogs) {
        this.crashlogs = crashlogs;
    }
}
