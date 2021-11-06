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

package de.njsm.stocks.android.frontend.device;


import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import de.njsm.stocks.R;
import de.njsm.stocks.android.frontend.BaseFragment;
import de.njsm.stocks.android.util.Config;
import de.njsm.stocks.android.util.Logger;

import java.util.Hashtable;
import java.util.Locale;

public class QrCodeDisplayFragment extends BaseFragment {

    private static final Logger LOG = new Logger(QrCodeDisplayFragment.class);

    private QrCodeDisplayFragmentArgs input;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View result = inflater.inflate(R.layout.fragment_qr_code_display, container, false);
        assert getArguments() != null;
        input = QrCodeDisplayFragmentArgs.fromBundle(getArguments());
        return result;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        computeQrCode(view);
    }

    private void computeQrCode(View view) {
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences(Config.PREFERENCES_FILE, Context.MODE_PRIVATE);
        String serverName = sharedPreferences.getString(Config.SERVER_NAME_CONFIG, "");
        int caPort = sharedPreferences.getInt(Config.CA_PORT_CONFIG, 10910);
        int sentryPort = sharedPreferences.getInt(Config.SENTRY_PORT_CONFIG, 10911);
        int serverPort = sharedPreferences.getInt(Config.SERVER_PORT_CONFIG, 10912);
        String fingerprint = sharedPreferences.getString(Config.FPR_CONFIG, "");

        String qrContent = String.format(
                Locale.US,
                "%s\n%s\n%d\n%d\n%s\n%s\n%s\n%d\n%d\n%d\n",
                input.getUsername(),
                input.getDeviceName(),
                input.getUserId(),
                input.getDeviceId(),
                fingerprint,
                input.getTicket(),
                serverName,
                caPort,
                sentryPort,
                serverPort);

        Bitmap image = null;
        try {
            image = generateQrCode(qrContent);
        } catch (WriterException e) {
            LOG.e("Image computation failed", e);
        }

        ((ImageView) view.findViewById(R.id.fragment_qr_code_display_image)).setImageBitmap(image);

        ((TextView) view.findViewById(R.id.fragment_qr_code_display_text)).setText(
                getString(R.string.hint_servername) + ": " + serverName + "\n"
                + getString(R.string.title_caport) + ": " + caPort + "\n"
                + getString(R.string.title_registration_port) + ": " + sentryPort + "\n"
                + getString(R.string.title_server_port) + ": " + serverPort + "\n"
                + getString(R.string.hint_username) + ": " + input.getUsername() + "\n"
                + getString(R.string.hint_device_name) + ": " + input.getDeviceName() + "\n"
                + getString(R.string.hint_user_id) + ": " + input.getUserId() + "\n"
                + getString(R.string.hint_device_id) + ": " + input.getDeviceId() + "\n"
                + getString(R.string.hint_fingerprint) + ": " + fingerprint + "\n"
                + getString(R.string.hint_ticket) + ": " + input.getTicket()
        );
    }

    private Bitmap generateQrCode(String myCodeText) throws WriterException {
        Hashtable<EncodeHintType, ErrorCorrectionLevel> hintMap = new Hashtable<>();
        hintMap.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L);

        QRCodeWriter qrCodeWriter = new QRCodeWriter();

        int size = 256;

        BitMatrix bitMatrix = qrCodeWriter.encode(myCodeText, BarcodeFormat.QR_CODE, size, size, hintMap);
        int width = bitMatrix.getWidth();
        Bitmap bmp = Bitmap.createBitmap(width, width, Bitmap.Config.RGB_565);
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < width; y++) {
                bmp.setPixel(y, x, bitMatrix.get(x, y) ? Color.BLACK : Color.WHITE);
            }
        }
        DisplayMetrics display = new DisplayMetrics();
        requireActivity().getWindowManager().getDefaultDisplay().getMetrics(display);
        int screenWidth = display.widthPixels;
        bmp = Bitmap.createScaledBitmap(bmp, screenWidth, screenWidth, false);
        return bmp;
    }
}
