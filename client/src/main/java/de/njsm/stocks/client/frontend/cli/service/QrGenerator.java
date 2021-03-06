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

package de.njsm.stocks.client.frontend.cli.service;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Hashtable;

public class QrGenerator {

    private static final Logger LOG = LogManager.getLogger(QrGenerator.class);

    private static final String WHITE_WHITE = " ";

    private static final String WHITE_BLACK = "▄";

    private static final String BLACK_WHITE = "▀";

    private static final String BLACK_BLACK = "█";

    public String generateQrCode(String text) {
        try {
            boolean[][] bitmask = mapToBitmask(text);
            return mapToText(bitmask);
        } catch (WriterException e) {
            LOG.error("QR code could not be generated", e);
            return "";
        }
    }

    private boolean[][] mapToBitmask(String text) throws WriterException {
        Hashtable<EncodeHintType, Object> hintMap = new Hashtable<>();
        hintMap.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L);
        hintMap.put(EncodeHintType.MARGIN, 0);

        QRCodeWriter qrCodeWriter = new QRCodeWriter();

        int size = 60;

        BitMatrix bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, size, size, hintMap);
        boolean[][] result = new boolean[size][size];

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                result[i][j] = bitMatrix.get(i,j);
            }
        }
        return result;
    }

    private String mapToText(boolean[][] bitmask) {
        StringBuilder buffer = new StringBuilder();

        for (int i = 0; i < bitmask.length - 1; i += 2) {
            for (int j = 0; j < bitmask[i].length; j++) {
                buffer.append(mapSingleLetter(bitmask[i][j], bitmask[i+1][j]));
            }
            buffer.append("\n");
        }

        if (bitmask.length % 2 == 1) {
            for (int j = 0; j < bitmask[bitmask.length - 1].length; j++) {
                buffer.append(mapSingleLetter(bitmask[bitmask.length - 1][j], false));
            }
        }
        buffer.append("\n");

        return buffer.toString();
    }

    private String mapSingleLetter(boolean up, boolean down) {
        if (up && down) {
            return WHITE_WHITE;
        } else if (up) {
            return WHITE_BLACK;
        } else if (down) {
            return BLACK_WHITE;
        } else {
            return BLACK_BLACK;
        }
    }


}
