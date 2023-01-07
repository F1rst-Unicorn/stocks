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

package de.njsm.stocks.client.business;

import de.njsm.stocks.client.business.entities.CrashLog;
import de.njsm.stocks.client.execution.Scheduler;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

import static de.njsm.stocks.client.execution.SchedulerImplTest.runJobOnMocked;
import static de.njsm.stocks.client.runtime.ExceptionHandler.TECHNICAL_DATE_FORMAT;
import static org.junit.jupiter.api.Assertions.assertFalse;

@ExtendWith(MockitoExtension.class)
class CrashListInteractorImplTest {

    private CrashListInteractorImpl uut;

    @Mock
    private Scheduler scheduler;

    @BeforeEach
    void setUp() throws IOException {
        uut = new CrashListInteractorImpl(
                new TestFileInteractor(new File(".")),
                scheduler,
                new Localiser(Instant::now));
        placeTestCrashLog();
    }

    @AfterEach
    void tearDown() {
        new File(getInput().fileName()).delete();
    }

    private static void placeTestCrashLog() throws IOException {
        String content = getInput().exceptionName() + "\n" +
                TECHNICAL_DATE_FORMAT.format(Instant.EPOCH) + "\n" +
                getInput().stackTrace();
        OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(getInput().fileName()));
        writer.append(content);
        writer.close();
    }

    private static CrashLog getInput() {
        return CrashLog.create("crashlog_123.txt", LocalDateTime.ofInstant(Instant.EPOCH, ZoneId.systemDefault()), SubsystemException.class.getName(), "fake stacktrace");
    }

    @Test
    void deletingCrashLogWorks() {
        CrashLog input = getInput();

        uut.delete(input);
        runJobOnMocked(scheduler);

        assertFalse(new File(input.fileName()).exists());
    }

    @Test
    void readingCrashLogsWorks() {
        var input = getInput();

        var actual = uut.get();
        runJobOnMocked(scheduler);

        actual.test().assertValue(List.of(input));
    }
}