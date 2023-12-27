package com.example.myapplication;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.window.OnBackInvokedDispatcher;

import com.example.myapplication.db.DBHelper;
import com.example.myapplication.adapters.ShoppingListAdapter;
import com.example.myapplication.model.ShoppingListModel;
import com.example.myapplication.util.SwipeLeftDeleteHelper;
import com.google.android.material.snackbar.Snackbar;

public class HomeActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ShoppingListAdapter recyclerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        System.out.println("HOME CREATE");
        setContentView(R.layout.activity_home);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerAdapter = new ShoppingListAdapter(
                ShoppingListModel.getShoppingListModels(),
                this::openShoppingListEditActivity
        );
        recyclerView.setAdapter(recyclerAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        ItemTouchHelper touchHelper = new ItemTouchHelper(new SwipeLeftDeleteHelper(
                this, this::onSwipeLeftDeleteList));
        touchHelper.attachToRecyclerView(recyclerView);

        Button createButton = findViewById(R.id.create_button);
        createButton.setOnClickListener(view -> createNewShoppingList());

        // Disable the on back button functionality from this activity
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            getOnBackInvokedDispatcher().registerOnBackInvokedCallback(
                    OnBackInvokedDispatcher.PRIORITY_DEFAULT, () -> {});
        } else {
            getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
                @Override
                public void handleOnBackPressed() {}
            });
        }
    }

    @Override
    protected void onResume() {
        Intent intent = getIntent();
        long id = intent.getLongExtra("shoppingListModelId", -1);
        if(id != -1) {
            int index = ShoppingListModel.getIndexById(id);
            if(index != -1) {
                recyclerAdapter.notifyItemChanged(index);
            }
        }

        super.onResume();
    }

    /**
     * <p>
     *     Opens the {@link EditShoppingListActivity} activity, passing the ID
     *     of the shopping list the user wants to edit.
     * </p>
     * <p>
     *     The ID is passed so that the other activity can retrieve the same object instance
     *     as this home activity, so that any changes are reflected.
     * </p>
     * @param model To edit
     */
    private void openShoppingListEditActivity(ShoppingListModel model) {
        Intent intent = new Intent(this, EditShoppingListActivity.class);
        intent.putExtra("shoppingListModelId", model.getId());
        startActivity(intent);
    }

    private void createNewShoppingList() {
        ShoppingListModel model = new ShoppingListModel(
                "List #" + (ShoppingListModel.getShoppingListModels().size() + 1)
        );
        model.setId(ShoppingListModel.getAndIncrementListCounter());
        ShoppingListModel.getShoppingListModels().add(model);
        openShoppingListEditActivity(model);
    }

    /**
     * <p>
     *     When the user swipes from the right side of their screen to the left side and releases
     *     this method is called.
     *     This method deletes the {@link ShoppingListModel} at the provided position.
     * </p>
     * <p>
     *     Displays a {@link Snackbar}, giving the user the option to undo the delete action.
     * </p>
     * @param position the index of the model in the list
     */
    private void onSwipeLeftDeleteList(final int position) {
        final ShoppingListModel model = ShoppingListModel.getShoppingListModels().remove(position);
        model.setDeletedFlag(true);

        DBHelper.getInstance(this).saveShoppingList(model); // Save the delete flag to db

        // Update the recycler adapter
        recyclerAdapter.notifyItemRemoved(position);

        // Display SnackBar with option to undo
        Snackbar.make(
                recyclerView,
                getString(R.string.model_deleted_text, model.getName()),
                Snackbar.LENGTH_LONG
        ).setAction(
                R.string.model_deleted_undo_text, v -> {
                    // Add the item model back to the list at the same index
                    ShoppingListModel.getShoppingListModels().add(position, model);
                    model.setDeletedFlag(false);
                    // Update db
                    DBHelper.getInstance(this).saveShoppingList(model);
                    // Update adapter
                    recyclerAdapter.notifyItemInserted(position);
                }
        ).show();
    }
}