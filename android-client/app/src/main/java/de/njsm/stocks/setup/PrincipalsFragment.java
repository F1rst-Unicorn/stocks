package de.njsm.stocks.setup;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.fcannizzaro.materialstepper.AbstractStep;

import de.njsm.stocks.R;

public class PrincipalsFragment extends AbstractStep {

    public PrincipalsFragment() {
    }

    @Override
    public String name() {
        return "User";
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_principals, container, false);
    }



}
