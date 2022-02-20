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

package de.njsm.stocks.client.view;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import de.njsm.stocks.client.ui.R;

public interface CameraPermissionProber {

    default boolean probeForCameraPermission() {
        return probeForCameraPermission(true);
    }

    default boolean probeForCameraPermission(boolean showRationale) {
        if (ContextCompat.checkSelfPermission(requireActivity(),
                Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {

            if (showRationale && ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(),
                    Manifest.permission.CAMERA)) {
                String message = getString(R.string.text_camera_explanation);
                showErrorDialog(R.string.title_camera_permission,
                        message,
                        (d, w) -> this.probeForCameraPermission(false));
                return false;
            } else {
                ActivityCompat.requestPermissions(requireActivity(),
                        new String[]{Manifest.permission.CAMERA},
                        0);
            }
            return false;
        } else {
            return true;
        }
    }

    default void showErrorDialog(int titleId, String message, DialogInterface.OnClickListener doer) {
        new AlertDialog.Builder(requireActivity())
                .setTitle(requireActivity().getString(titleId))
                .setMessage(message)
                .setIcon(R.drawable.ic_error_black_24dp)
                .setPositiveButton(android.R.string.ok, doer)
                .setNegativeButton(getString(android.R.string.cancel), this::doNothing)
                .show();
    }

    Activity requireActivity();

    String getString(@StringRes int id);

    default void doNothing(DialogInterface dialogInterface, int i) {}
}
