package de.njsm.stocks.android.frontend.util;

import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import androidx.annotation.DrawableRes;
import androidx.core.content.res.ResourcesCompat;

public interface ResourceProvider {

    class ResourceProviderImpl implements ResourceProvider {

        private final Resources resources;

        private final Resources.Theme theme;

        public ResourceProviderImpl(Resources resources, Resources.Theme theme) {
            this.resources = resources;
            this.theme = theme;
        }

        @Override
        public Drawable getDrawable(@DrawableRes int resourceId) {
            return ResourcesCompat.getDrawable(resources, resourceId, theme);
        }
    }

    static ResourceProvider build(Resources resources, Resources.Theme theme) {
        return new ResourceProviderImpl(resources, theme);
    }

    Drawable getDrawable(@DrawableRes int resourceId);
}
