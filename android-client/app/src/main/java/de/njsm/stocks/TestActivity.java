package de.njsm.stocks;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import de.njsm.stocks.backend.db.StocksContentProvider;

public class TestActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    public static final String KEY_ID = "de.njsm.stocks.FoodActivity.id";
    public static final String KEY_NAME = "de.njsm.stocks.FoodActivity.name";

    protected String mName;
    protected int mId;

    RecyclerView mRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        Bundle extras = getIntent().getExtras();
        mName = extras.getString(KEY_NAME);
        mId = extras.getInt(KEY_ID);

        Toolbar toolbar = (Toolbar) findViewById(R.id.activity_test_toolbar);
        setSupportActionBar(toolbar);
        setTitle(mName);

        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        setUpRecyclerView();

        getLoaderManager().initLoader(0, null, this);
    }

    private void setUpRecyclerView() {
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(null);
        mRecyclerView.setHasFixedSize(true);
        setUpItemTouchHelper();
        setUpAnimationDecoratorHelper();
    }

    /**
     * This is the standard support library way of implementing "swipe to delete" feature. You can do custom drawing in onChildDraw method
     * but whatever you draw will disappear once the swipe is over, and while the items are animating to their new position the recycler view
     * background will be visible. That is rarely an desired effect.
     */
    private void setUpItemTouchHelper() {

        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {

            // we want to cache these and not allocate anything repeatedly in the onChildDraw method
            Drawable background;
            Drawable xMark;
            int xMarkMargin;
            boolean initiated;

            private void init() {
                background = new ColorDrawable(getResources().getColor(R.color.colorAccent));
                xMark = ContextCompat.getDrawable(TestActivity.this, R.drawable.ic_local_dining_white_24dp);
                xMark.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
                xMarkMargin = (int) TestActivity.this.getResources().getDimension(R.dimen.list_item_padding);
                initiated = true;
            }

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public int getSwipeDirs(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
                int position = viewHolder.getAdapterPosition();
                TestAdapter testAdapter = (TestAdapter)recyclerView.getAdapter();
                if (testAdapter.isUndoOn() && testAdapter.isPendingRemoval(position)) {
                    return 0;
                }
                return super.getSwipeDirs(recyclerView, viewHolder);
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                int swipedPosition = viewHolder.getAdapterPosition();
                TestAdapter adapter = (TestAdapter)mRecyclerView.getAdapter();
                boolean undoOn = adapter.isUndoOn();
                if (undoOn) {
                    adapter.pendingRemoval(swipedPosition);
                } else {
                    adapter.remove(swipedPosition);
                }
            }

            @Override
            public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                View itemView = viewHolder.itemView;

                // not sure why, but this method get's called for viewholder that are already swiped away
                if (viewHolder.getAdapterPosition() == -1) {
                    // not interested in those
                    return;
                }

                if (!initiated) {
                    init();
                }

                // draw red background
                background.setBounds(itemView.getRight() + (int) dX, itemView.getTop(), itemView.getRight(), itemView.getBottom());
                background.draw(c);

                // draw x mark
                int itemHeight = itemView.getBottom() - itemView.getTop();
                int intrinsicWidth = xMark.getIntrinsicWidth();
                int intrinsicHeight = xMark.getIntrinsicWidth();

                int xMarkLeft = itemView.getRight() - xMarkMargin - intrinsicWidth;
                int xMarkRight = itemView.getRight() - xMarkMargin;
                int xMarkTop = itemView.getTop() + (itemHeight - intrinsicHeight)/2;
                int xMarkBottom = xMarkTop + intrinsicHeight;
                xMark.setBounds(xMarkLeft, xMarkTop, xMarkRight, xMarkBottom);

                xMark.draw(c);

                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }

        };
        ItemTouchHelper mItemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        mItemTouchHelper.attachToRecyclerView(mRecyclerView);
    }

    /**
     * We're gonna setup another ItemDecorator that will draw the red background in the empty space while the items are animating to thier new positions
     * after an item is removed.
     */
    private void setUpAnimationDecoratorHelper() {
        mRecyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {

            // we want to cache this and not allocate anything repeatedly in the onDraw method
            Drawable background;
            boolean initiated;

            private void init() {
                background = new ColorDrawable(getResources().getColor(R.color.colorAccent));
                initiated = true;
            }

            @Override
            public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {

                if (!initiated) {
                    init();
                }

                // only if animation is in progress
                if (parent.getItemAnimator().isRunning()) {

                    // some items might be animating down and some items might be animating up to close the gap left by the removed item
                    // this is not exclusive, both movement can be happening at the same time
                    // to reproduce this leave just enough items so the first one and the last one would be just a little off screen
                    // then remove one from the middle

                    // find first child with translationY > 0
                    // and last one with translationY < 0
                    // we're after a rect that is not covered in recycler-view views at this point in time
                    View lastViewComingDown = null;
                    View firstViewComingUp = null;

                    // this is fixed
                    int left = 0;
                    int right = parent.getWidth();

                    // this we need to find out
                    int top = 0;
                    int bottom = 0;

                    // find relevant translating views
                    int childCount = parent.getLayoutManager().getChildCount();
                    for (int i = 0; i < childCount; i++) {
                        View child = parent.getLayoutManager().getChildAt(i);
                        if (child.getTranslationY() < 0) {
                            // view is coming down
                            lastViewComingDown = child;
                        } else if (child.getTranslationY() > 0) {
                            // view is coming up
                            if (firstViewComingUp == null) {
                                firstViewComingUp = child;
                            }
                        }
                    }

                    if (lastViewComingDown != null && firstViewComingUp != null) {
                        // views are coming down AND going up to fill the void
                        top = lastViewComingDown.getBottom() + (int) lastViewComingDown.getTranslationY();
                        bottom = firstViewComingUp.getTop() + (int) firstViewComingUp.getTranslationY();
                    } else if (lastViewComingDown != null) {
                        // views are going down to fill the void
                        top = lastViewComingDown.getBottom() + (int) lastViewComingDown.getTranslationY();
                        bottom = lastViewComingDown.getBottom();
                    } else if (firstViewComingUp != null) {
                        // views are coming up to fill the void
                        top = firstViewComingUp.getTop();
                        bottom = firstViewComingUp.getTop() + (int) firstViewComingUp.getTranslationY();
                    }

                    background.setBounds(left, top, right, bottom);
                    background.draw(c);

                }
                super.onDraw(c, parent, state);
            }

        });
    }

    public void addItem(View view) {
        Intent i = new Intent(this, AddFoodItemActivity.class);
        Bundle extras = new Bundle();
        extras.putInt(AddFoodItemActivity.KEY_ID, mId);
        extras.putString(AddFoodItemActivity.KEY_FOOD, mName);
        i.putExtras(extras);
        startActivity(i);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Uri uri = Uri.withAppendedPath(
                StocksContentProvider.baseUri,
                StocksContentProvider.foodItemType);

        return new CursorLoader(
                this,
                uri,
                null, null,
                new String[] {String.valueOf(mId)},
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mRecyclerView.setAdapter(new TestAdapter(data));
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mRecyclerView.setAdapter(null);
    }

    class TestAdapter extends RecyclerView.Adapter {

        private static final int PENDING_REMOVAL_TIMEOUT = 3000; // 3sec

        List<String> items;
        List<Integer> itemsPendingRemoval;
        int lastInsertedIndex; // so we can add some more items for testing purposes
        boolean undoOn = true; // is undo on, you can turn it on from the toolbar menu

        private Handler handler = new Handler(); // hanlder for running delayed runnables
        HashMap<String, Runnable> pendingRunnables = new HashMap<>(); // map of items to pending runnables, so we can cancel a removal if need be

        Cursor mCursor;

        public TestAdapter() {
            items = new ArrayList<>();
            itemsPendingRemoval = new ArrayList<>();
            // let's generate some items
            lastInsertedIndex = 15;
            // this should give us a couple of screens worth
            for (int i=1; i<= lastInsertedIndex; i++) {
                items.add("Item " + i);
            }
        }

        public TestAdapter(Cursor c) {
            mCursor = c;
            itemsPendingRemoval = new ArrayList<>();
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new TestViewHolder(parent);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            TestViewHolder viewHolder = (TestViewHolder)holder;
            final int item = 0;
            if (itemsPendingRemoval.contains(item)) {
                // we need to show the "undo" state of the row
                viewHolder.setRemoveState(TestActivity.this, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // user wants to undo the removal, let's cancel the pending task
                        Runnable pendingRemovalRunnable = pendingRunnables.get(item);
                        pendingRunnables.remove(item);
                        if (pendingRemovalRunnable != null) handler.removeCallbacks(pendingRemovalRunnable);
                        itemsPendingRemoval.remove(item);
                        // this will rebind the row in "normal" state
                        notifyItemChanged(items.indexOf(item));
                    }
                });
            } else {
                viewHolder.setNormalState();
                fillViewWithData(viewHolder, position);
            }
        }

        private void fillViewWithData(TestViewHolder viewHolder, int position) {
            SimpleDateFormat targetFormat = new SimpleDateFormat("dd.MM.yyyy", Locale.US);
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.US);
            Date date = null;
            String location;
            String user;
            String device;

            mCursor.moveToPosition(position);
            try {
                date = format.parse(mCursor.getString(mCursor.getColumnIndex("date")));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            assert date != null;
            location = mCursor.getString(mCursor.getColumnIndex("location"));
            user = mCursor.getString(mCursor.getColumnIndex("user"));
            device = mCursor.getString(mCursor.getColumnIndex("device"));

            viewHolder.mBuyer.setText(user);
            viewHolder.mLocation.setText(location);
            viewHolder.mDevice.setText(device);
        }

        @Override
        public int getItemCount() {
            return mCursor.getCount();
        }

        public boolean isUndoOn() {
            return undoOn;
        }

        public void pendingRemoval(int position) {
            final String item = "";
            if (!itemsPendingRemoval.contains(position)) {
                itemsPendingRemoval.add(position);
                // this will redraw row in "undo" state
                notifyItemChanged(position);
                // let's create, store and post a runnable to remove the item
                Runnable pendingRemovalRunnable = new Runnable() {
                    @Override
                    public void run() {
                        remove(items.indexOf(item));
                    }
                };
                handler.postDelayed(pendingRemovalRunnable, PENDING_REMOVAL_TIMEOUT);
                pendingRunnables.put(item, pendingRemovalRunnable);
            }
        }

        public void remove(int position) {
            String item = "";
            if (itemsPendingRemoval.contains(position)) {
                itemsPendingRemoval.remove(position);
            }
            if (items.contains(item)) {
                items.remove(position);
                notifyItemRemoved(position);
            }
        }

        public boolean isPendingRemoval(int position) {
            return itemsPendingRemoval.contains(position);
        }
    }

    static class TestViewHolder extends RecyclerView.ViewHolder {

        TextView mLocation;
        TextView mDate;
        TextView mBuyer;
        TextView mDevice;
        TextView mComma;
        TextView mColon;
        TextView mBuyerLiteral;
        ImageView mLocationIcon;
        ImageView mIcon;

        Button undoButton;

        public TestViewHolder(ViewGroup parent) {
            super(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_food_item, parent, false));
            mLocation = (TextView) itemView.findViewById(R.id.item_food_item_location);
            mBuyer = (TextView) itemView.findViewById(R.id.item_food_item_user);
            mDevice = (TextView) itemView.findViewById(R.id.item_food_item_device);
            mDate = (TextView) itemView.findViewById(R.id.item_food_item_date);
            mComma = (TextView) itemView.findViewById(R.id.item_food_item_comma);
            mColon = (TextView) itemView.findViewById(R.id.item_food_item_colon);
            mBuyerLiteral = (TextView) itemView.findViewById(R.id.item_food_item_buyer);
            mIcon = (ImageView) itemView.findViewById(R.id.item_food_item_icon);
            mLocationIcon = (ImageView) itemView.findViewById(R.id.item_food_item_location_icon);
            undoButton = (Button) itemView.findViewById(R.id.item_food_item_undo);
        }

        public void setNormalState() {
            itemView.setBackgroundColor(Color.WHITE);
            undoButton.setVisibility(View.GONE);
            undoButton.setOnClickListener(null);

            // maybe add text again?
            mLocation.setVisibility(View.VISIBLE);
            mBuyer.setVisibility(View.VISIBLE);
            mDevice.setVisibility(View.VISIBLE);
            mDate.setVisibility(View.VISIBLE);
            mIcon.setVisibility(View.VISIBLE);
            mComma.setVisibility(View.VISIBLE);
            mColon.setVisibility(View.VISIBLE);
            mBuyerLiteral.setVisibility(View.VISIBLE);
            mLocationIcon.setVisibility(View.VISIBLE);
        }

        public void setRemoveState(Activity c, View.OnClickListener listener) {
            itemView.setBackgroundColor(c.getResources().getColor(R.color.colorAccent));
            undoButton.setVisibility(View.VISIBLE);
            undoButton.setOnClickListener(listener);

            // maybe add text again?
            mLocation.setVisibility(View.GONE);
            mBuyer.setVisibility(View.GONE);
            mDevice.setVisibility(View.GONE);
            mDate.setVisibility(View.GONE);
            mIcon.setVisibility(View.GONE);
            mComma.setVisibility(View.GONE);
            mColon.setVisibility(View.GONE);
            mBuyerLiteral.setVisibility(View.GONE);
            mLocationIcon.setVisibility(View.GONE);
        }
    }



}