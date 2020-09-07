package de.njsm.stocks.android.business.data.activity;

import de.njsm.stocks.R;

public interface EanNumberIconResourceProvider extends EntityIconResourceProvider {

    default int getEntityIconResource() {
        return R.drawable.ic_photo_camera_white_24dp;
    }
}
