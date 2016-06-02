package de.njsm.stocks.setup;


import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.github.fcannizzaro.materialstepper.AbstractStep;

import de.njsm.stocks.R;

public class ServerFragment extends AbstractStep {

    public ServerFragment() {
    }

    @Override
    public String name() {
        return "Server";
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_server, container, false);
    }



}
