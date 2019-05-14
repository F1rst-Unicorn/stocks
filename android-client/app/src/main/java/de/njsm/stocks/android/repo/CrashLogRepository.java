package de.njsm.stocks.android.repo;

import android.content.Context;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import de.njsm.stocks.android.frontend.crashlog.CrashLog;
import de.njsm.stocks.android.util.Logger;
import org.apache.commons.io.IOUtils;

import javax.inject.Inject;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;

public class CrashLogRepository {

    private static final Logger LOG = new Logger(CrashLogRepository.class);

    private Executor executor;

    private Context context;

    private MutableLiveData<List<CrashLog>> data;

    @Inject
    public CrashLogRepository(Context context, Executor executor) {
        this.executor = executor;
        this.context = context;
    }

    public LiveData<List<CrashLog>> getCrashLogs() {
        LOG.d("getting crash logs");
        if (data == null) {
            LOG.d("Creating new live data for that");
            data = new MutableLiveData<>();
        }
        executor.execute(() -> createCrashLogList(data));
        return data;
    }

    public void delete(CrashLog t) {
        boolean result = t.getFile().delete();
        if (result) {
            LOG.i("Removed crash log " + t.getName());
        } else {
            LOG.i("Crash log " + t.getName() + " was already gone");
        }
        getCrashLogs();
    }

    private void createCrashLogList(MutableLiveData<List<CrashLog>> result) {
        File crashLogDirectory = context.getFilesDir();
        File[] rawData = crashLogDirectory.listFiles((dir, name) -> name.startsWith("crashlog_"));
        List<CrashLog> logs = new ArrayList<>();
        for (File data : rawData) {
            CrashLog item = createReport(data);
            logs.add(item);
        }
        result.postValue(logs);
    }

    private CrashLog createReport(File data) {
        try (InputStream stream = new FileInputStream(data)) {
            String content = IOUtils.toString(stream, StandardCharsets.UTF_8);
            return parseContent(data, content);
        } catch (IOException e) {
            LOG.w("Could not read crashlog " + data.getPath(), e);
            return new CrashLog(data);
        }
    }

    private CrashLog parseContent(File file, String content) {
        String[] lines = content.split("\n");
        if (lines.length >= 3)
            return new CrashLog(lines[0], lines[1], content, file);
        else
            return new CrashLog(file);
    }
}
