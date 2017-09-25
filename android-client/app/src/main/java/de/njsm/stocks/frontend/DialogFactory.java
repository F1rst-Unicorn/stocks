package de.njsm.stocks.frontend;

import android.app.Activity;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.widget.EditText;
import de.njsm.stocks.R;
import de.njsm.stocks.backend.network.NetworkManager;
import de.njsm.stocks.common.data.Food;
import de.njsm.stocks.common.data.Location;
import de.njsm.stocks.common.data.User;

public class DialogFactory {

    public static void showFoddAddingDialog(Activity activity,
                                            NetworkManager networkManager) {
        final EditText textField = (EditText) activity.getLayoutInflater().inflate(R.layout.text_field, null);
        textField.setHint(activity.getResources().getString(R.string.hint_food));
        new AlertDialog.Builder(activity)
                .setTitle(activity.getResources().getString(R.string.dialog_new_food))
                .setView(textField)
                .setPositiveButton(activity.getResources().getString(R.string.dialog_ok), (DialogInterface dialog, int whichButton) -> {
                    String name = textField.getText().toString().trim();
                    networkManager.addFood(new Food(0, name));
                })
                .setNegativeButton(activity.getResources().getString(R.string.dialog_cancel), (DialogInterface dialog, int whichButton) -> {})
                .show();
    }

    public static void showUserAddingDialog(Activity activity,
                                                NetworkManager networkManager) {
        final EditText textField = (EditText) activity.getLayoutInflater().inflate(R.layout.text_field, null);
        textField.setHint(activity.getResources().getString(R.string.hint_username));
        new AlertDialog.Builder(activity)
                .setTitle(activity.getResources().getString(R.string.dialog_new_user))
                .setView(textField)
                .setPositiveButton(activity.getResources().getString(R.string.dialog_ok), (DialogInterface dialog, int whichButton) -> {
                    String name = textField.getText().toString().trim();
                    networkManager.addUser(new User(0, name));
                })
                .setNegativeButton(activity.getResources().getString(R.string.dialog_cancel), (DialogInterface dialog, int whichButton) -> {})
                .show();
    }

    public static void showLocationAddingDialog(Activity activity,
                                                NetworkManager networkManager) {
        final EditText textField = (EditText) activity.getLayoutInflater().inflate(R.layout.text_field, null);
        textField.setHint(activity.getResources().getString(R.string.hint_location));
        new AlertDialog.Builder(activity)
                .setTitle(activity.getResources().getString(R.string.dialog_new_location))
                .setView(textField)
                .setPositiveButton(activity.getResources().getString(R.string.dialog_ok), (DialogInterface dialog, int whichButton) -> {
                    String name = textField.getText().toString().trim();
                    networkManager.addLocation(new Location(0, name));
                })
                .setNegativeButton(activity.getResources().getString(R.string.dialog_cancel), (DialogInterface dialog, int whichButton) -> {})
                .show();
    }
}
