/* stocks is client-server program to manage a household's food stock
 * Copyright (C) 2019  The stocks developers
 *
 * This file is part of the stocks program suite.
 *
 * stocks is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * stocks is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package de.njsm.stocks.android.util;

import java.time.Instant;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;

public class ExceptionHandler implements Thread.UncaughtExceptionHandler {

    private static final Logger LOG = new Logger(ExceptionHandler.class);

    private File directory;

    private Thread.UncaughtExceptionHandler androidHandler;

    public ExceptionHandler(File directory, Thread.UncaughtExceptionHandler androidHandler) {
        this.directory = directory;
        this.androidHandler = androidHandler;
    }

    @Override
    public void uncaughtException(Thread faultyThread, Throwable occuredException) {
        LOG.i("Caught runtime exception", occuredException);

        PrintWriter pw;
        String timestamp = Config.TECHNICAL_DATE_FORMAT.format(Instant.now());
        String fileName = "crashlog_" + timestamp + ".txt";
        try (FileOutputStream os = new FileOutputStream(new File(directory, fileName))) {
            pw = new PrintWriter(os);
            pw.println(occuredException.getClass().getSimpleName());
            pw.println(timestamp);
            occuredException.printStackTrace(pw);
            pw.flush();

        } catch(Exception e) {
            LOG.e("Exception during crash logging", e);
        } finally {
            androidHandler.uncaughtException(faultyThread, occuredException);
        }
    }
}
