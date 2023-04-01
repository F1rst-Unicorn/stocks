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

package de.njsm.stocks.client.fragment.ticketshow;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import de.njsm.stocks.client.business.entities.RegistrationForm;
import de.njsm.stocks.client.ui.R;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Hashtable;
import java.util.function.Function;

public class TicketShowForm {

    private static final Logger LOG = LoggerFactory.getLogger(TicketShowForm.class);

    private final View shareButton;

    private final TextView text;

    private final ImageView image;

    private final Function<Integer, String> dictionary;

    public TicketShowForm(View root, Function<Integer, String> dictionary) {
        shareButton = root.findViewById(R.id.fragment_ticket_show_share);
        text = root.findViewById(R.id.fragment_ticket_show_text);
        image = root.findViewById(R.id.fragment_ticket_show_image);
        this.dictionary = dictionary;
    }

    public void onShare(View.OnClickListener listener) {
        shareButton.setOnClickListener(listener);
    }

    public void showData(RegistrationForm registrationForm, int screenWidth) {
        text.setText(dictionary.apply(R.string.hint_servername) + ": " + registrationForm.serverName() + "\n"
                        + dictionary.apply(R.string.title_caport) + ": " + registrationForm.caPort() + "\n"
                        + dictionary.apply(R.string.title_registration_port) + ": " + registrationForm.registrationPort() + "\n"
                        + dictionary.apply(R.string.title_server_port) + ": " + registrationForm.serverPort() + "\n"
                        + dictionary.apply(R.string.hint_username) + ": " + registrationForm.userName() + "\n"
                        + dictionary.apply(R.string.hint_device_name) + ": " + registrationForm.userDeviceName() + "\n"
                        + dictionary.apply(R.string.hint_user_id) + ": " + registrationForm.userId() + "\n"
                        + dictionary.apply(R.string.hint_device_id) + ": " + registrationForm.userDeviceId() + "\n"
                        + dictionary.apply(R.string.hint_fingerprint) + ": " + registrationForm.fingerprint() + "\n"
                        + dictionary.apply(R.string.hint_ticket) + ": " + registrationForm.ticket());

        Bitmap image = null;
        try {
            image = generateQrCode(registrationForm.toQrString(), screenWidth);
        } catch (WriterException e) {
            LOG.error("Image computation failed", e);
        }

        this.image.setImageBitmap(image);
    }

    private Bitmap generateQrCode(String myCodeText, int screenWidth) throws WriterException {
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
        bmp = Bitmap.createScaledBitmap(bmp, screenWidth, screenWidth, false);
        return bmp;
    }
}
