package de.njsm.stocks.android.frontend.setup;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import androidx.annotation.NonNull;
import androidx.navigation.Navigation;
import de.njsm.stocks.R;
import de.njsm.stocks.android.frontend.BaseFragment;
import de.njsm.stocks.android.frontend.util.NonEmptyValidator;
import de.njsm.stocks.android.util.Logger;

public class PrincipalsFragment extends BaseFragment {

    private static final Logger LOG = new Logger(PrincipalsFragment.class);

    private PrincipalsFragmentArgs input;

    private EditText userName;

    private EditText userId;

    private EditText deviceName;

    private EditText deviceId;

    private EditText fingerprint;

    private EditText ticket;

    private Button next;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_principals, container, false);
        assert getArguments() != null;
        input = PrincipalsFragmentArgs.fromBundle(getArguments());

        userName = root.findViewById(R.id.user_name);
        userId = root.findViewById(R.id.user_id);
        deviceName = root.findViewById(R.id.device_name);
        deviceId = root.findViewById(R.id.device_id);
        fingerprint = root.findViewById(R.id.fingerprint);
        ticket = root.findViewById(R.id.ticket);
        next = root.findViewById(R.id.principals_button);

        if (input.getUsername() != null) {
            LOG.d("Got input from qr fragment");
            userName.setText(input.getUsername());
            deviceName.setText(input.getDeviceName());
            fingerprint.setText(input.getFingerprint());
            ticket.setText(input.getTicket());
            userId.setText(String.valueOf(input.getUserId()));
            deviceId.setText(String.valueOf(input.getDeviceId()));
        } else {
            LOG.d("Requiring user input");
            next.setEnabled(false);
        }

        userName.addTextChangedListener(new NonEmptyValidator(this::invalidateButton, userName));
        userId.addTextChangedListener(new NonEmptyValidator(this::invalidateButton, userId));
        deviceName.addTextChangedListener(new NonEmptyValidator(this::invalidateButton, deviceName));
        deviceId.addTextChangedListener(new NonEmptyValidator(this::invalidateButton, deviceId));
        fingerprint.addTextChangedListener(new NonEmptyValidator(this::invalidateButton, fingerprint));
        ticket.addTextChangedListener(new NonEmptyValidator(this::invalidateButton, ticket));

        next.setOnClickListener(this::next);
        requireActivity().setTitle(R.string.title_principals);

        return root;
    }

    private void invalidateButton(EditText view, Boolean isEmpty) {
        next.setEnabled(!isEmpty);
        if (isEmpty) {
            String error = requireActivity().getResources().getString(R.string.error_may_not_be_empty);
            view.setError(error);
        } else {
            view.setError(null);
        }
    }

    private void next(View view) {
        PrincipalsFragmentDirections.ActionNavFragmentPrincipalsToNavFragmentStartup args =
                PrincipalsFragmentDirections.actionNavFragmentPrincipalsToNavFragmentStartup()
                .setServerUrl(input.getServerUrl())
                .setCaPort(input.getCaPort())
                .setSentryPort(input.getSentryPort())
                .setServerPort(input.getServerPort())
                .setUsername(userName.getText().toString())
                .setUserId(Integer.parseInt(userId.getText().toString()))
                .setDeviceName(deviceName.getText().toString())
                .setDeviceId(Integer.parseInt(deviceId.getText().toString()))
                .setFingerprint(fingerprint.getText().toString())
                .setTicket(ticket.getText().toString());
        Navigation.findNavController(requireActivity(), R.id.main_nav_host_fragment).navigate(args);
    }
}
