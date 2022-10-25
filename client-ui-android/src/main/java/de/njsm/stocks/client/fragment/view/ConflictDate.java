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

package de.njsm.stocks.client.fragment.view;

import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import de.njsm.stocks.client.ui.R;

import java.time.LocalDate;

public class ConflictDate extends ConflictField {

    private final View root;

    private final DatePicker datePicker;

    private final Button today;

    private final Button predict;

    public ConflictDate(View root) {
        super(root.findViewById(R.id.date_conflict_conflict));
        this.root = root;
        datePicker = root.findViewById(R.id.date_conflict_date_picker);
        today = root.findViewById(R.id.date_conflict_today);
        predict = root.findViewById(R.id.date_conflict_predict);
    }

    public void setSelection(LocalDate date) {
        setSelectedDate(date);
    }

    public void setToday(LocalDate date) {
        today.setOnClickListener(__ -> setSelectedDate(date));
    }

    public void setPredict(LocalDate date) {
        predict.setOnClickListener(__ -> setSelectedDate(date));
    }

    private void setSelectedDate(LocalDate date) {
        datePicker.init(date.getYear(), date.getMonthValue() - 1, date.getDayOfMonth(), null);
    }

    public LocalDate get() {
        return LocalDate.of(
                datePicker.getYear(),
                datePicker.getMonth()+1,
                datePicker.getDayOfMonth());
    }

    @Override
    public void hide() {
        super.hide();
        root.setVisibility(View.GONE);
        datePicker.setVisibility(View.GONE);
    }
}
