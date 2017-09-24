package de.njsm.stocks.frontend.barcodes;


import android.app.ListFragment;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

public class BarcodeFragment extends ListFragment
        implements AdapterView.OnItemLongClickListener {

    private ListView mList;

    private int foodId;

    public static BarcodeFragment newInstance(int foodId) {
        BarcodeFragment result = new BarcodeFragment();
        Bundle args = new Bundle();
        args.putInt("id", foodId);
        result.setArguments(args);
        return result;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        foodId = getArguments().getInt("id");
    }

    @Override
    public void onStart() {
        super.onStart();
        mList = getListView();
        mList.setOnItemLongClickListener(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        mList.setOnItemLongClickListener(null);
        mList = null;
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> adapterView, View view, final int i, long l) {
        return true;
    }
}
