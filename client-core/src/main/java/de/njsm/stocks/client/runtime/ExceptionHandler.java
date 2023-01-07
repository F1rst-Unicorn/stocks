/*
 * stocks is client-server program to manage a household's food stock
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
 *
 */

package de.njsm.stocks.client.runtime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.io.File;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class ExceptionHandler implements Thread.UncaughtExceptionHandler {

    private static final Logger LOG = LoggerFactory.getLogger(ExceptionHandler.class);

    public static final DateTimeFormatter TECHNICAL_DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS").withZone(ZoneId.of("UTC"));

    private final Thread.UncaughtExceptionHandler defaultHandler;

    private final FileInteractor fileInteractor;

    @Inject
    ExceptionHandler(FileInteractor fileInteractor) {
        this.defaultHandler = (a,b) -> {};
        this.fileInteractor = fileInteractor;
    }

    public ExceptionHandler(Thread.UncaughtExceptionHandler defaultHandler, FileInteractor fileInteractor) {
        this.defaultHandler = defaultHandler;
        this.fileInteractor = fileInteractor;
    }

    @Override
    public void uncaughtException(Thread faultyThread, Throwable occuredException) {
        LOG.info("Caught runtime exception", occuredException);

        PrintWriter pw;
        String timestamp = TECHNICAL_DATE_FORMAT.format(Instant.now());
        String fileName = "crashlog_" + timestamp + ".txt";
        try (OutputStream os = fileInteractor.getFileOutputStream(new File(fileName))) {
            pw = new PrintWriter(os);
            pw.println(occuredException.getClass().getSimpleName());
            pw.println(timestamp);
            occuredException.printStackTrace(pw);
            pw.flush();

        } catch(Exception e) {
            LOG.error("Exception during crash logging", e);
        } finally {
            defaultHandler.uncaughtException(faultyThread, occuredException);
        }
    }
}
