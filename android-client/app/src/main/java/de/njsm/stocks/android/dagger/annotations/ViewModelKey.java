package de.njsm.stocks.android.dagger.annotations;

import androidx.lifecycle.ViewModel;
import dagger.MapKey;

import java.lang.annotation.*;

@Documented
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@MapKey
public @interface ViewModelKey {
    Class<? extends ViewModel> value();
}