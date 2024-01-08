package me.mp1282.shoppinglist;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import me.mp1282.shoppinglist.db.DBHelper;
import me.mp1282.shoppinglist.adapters.ItemListAdapter;
import me.mp1282.shoppinglist.model.ItemModel;
import me.mp1282.shoppinglist.model.ShoppingListModel;
import me.mp1282.shoppinglist.util.SwipeLeftDeleteHelper;
import com.google.android.material.snackbar.Snackbar;

import java.util.Optional;

public class EditShoppingListActivity extends AppCompatActivity {

    // Fields will always be initialised in the onCreate method
    private ShoppingListModel shoppingListModel;
    private ItemListAdapter recyclerAdapter;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_shopping_list);

        long id = getIntent().getLongExtra("shoppingListModelId", -1);
        this.shoppingListModel = ShoppingListModel.getModelById(id);
        if(shoppingListModel == null) throw new NullPointerException("ShoppingListModel cannot be null");

        updateTitleText();

        Button back = findViewById(R.id.back_button);
        back.setOnClickListener(v -> {
            Intent intent = new Intent(this, HomeActivity.class);
            intent.putExtra("shoppingListModelId", shoppingListModel.getId());
            startActivity(intent);
        });

        Button editText = findViewById(R.id.edit_title_button);
        editText.setOnClickListener(v -> displayEditTitlePopup());

        this.recyclerView = findViewById(R.id.recyclerView);
        this.recyclerAdapter = new ItemListAdapter(shoppingListModel);
        recyclerView.setAdapter(recyclerAdapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, linearLayoutManager.getOrientation()));

        ItemTouchHelper deleteHelper = new ItemTouchHelper(new SwipeLeftDeleteHelper(this,
                this::swipeLeftToDeleteItem));
        deleteHelper.attachToRecyclerView(recyclerView);

        Button addButton = findViewById(R.id.add_item_button);
        addButton.setOnClickListener(v -> {
            // If the item model most recently added to the list (the last item model)
            // is empty, e.g. it does not have a name or any user set properties,
            // then edit that item model instead of creating a duplicate empty model
            Optional<ItemModel> optional = shoppingListModel.getLastItemModel();
            ItemModel model;
            if(optional.isPresent() && optional.get().isEmpty()) {
                model = optional.get();
            } else {
                model = new ItemModel("");
                model.setId(shoppingListModel.getAndIncrementItemsCounter());
                shoppingListModel.getItemList().add(model);
            }

            // Open input dialog for the item model
            displayEditItemPopup(model);
        });

        Button buyButton = findViewById(R.id.purchase_list_button);
        buyButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, PurchaseListActivity.class);
            intent.putExtra("shoppingListModelId", shoppingListModel.getId());
            startActivity(intent);
        });
    }

    @Override
    protected void onPause() {
        // Save shopping list model to the database
        DBHelper db = DBHelper.getInstance(this);
        db.saveShoppingList(shoppingListModel);

        super.onPause();
    }

    /**
     * <p>
     *     This method creates an {@link AlertDialog} from the
     *     edit_title_popup.xml layout file. When the user inputs the text and clicks OK,
     *     the model is updated.
     * </p>
     */
    private void displayEditTitlePopup() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.edit_title_button));

        View inflated = LayoutInflater.from(this)
                .inflate(R.layout.edit_title_popup, null);

        final EditText input = inflated.findViewById(R.id.edit_title_input);
        input.setText(shoppingListModel.getName());

        builder.setView(inflated);

        builder.setPositiveButton(R.string.edit_title_popup_ok, (dialog, which) -> {
            dialog.dismiss();
            shoppingListModel.setName(input.getText().toString());
            updateTitleText();
        });
        builder.setNegativeButton(R.string.edit_title_popup_cancel, (dialog, which) ->
                dialog.cancel());
        builder.show();
    }

    /**
     * <p>
     *     This method creates an {@link AlertDialog} from the
     *     edit_item_model_popup.xml layout file. When the user inputs the text and clicks OK,
     *     the {@link ItemModel} is updated and the recycler view is also updated
     *     to display the changes.
     * </p>
     * @param model - the item model to edit
     */
    @SuppressLint("NotifyDataSetChanged")
    private void displayEditItemPopup(ItemModel model) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.edit_item_model_title));

        View inflated = LayoutInflater.from(this)
                .inflate(R.layout.edit_item_model_popup, null);

        final EditText input = inflated.findViewById(R.id.edit_item_input);
        input.setText(model.getName());

        builder.setView(inflated);

        builder.setPositiveButton(R.string.edit_title_popup_ok, (dialog, which) -> {
            dialog.dismiss();
            model.setName(input.getText().toString());
            recyclerAdapter.notifyDataSetChanged();
        });
        builder.setNegativeButton(R.string.edit_title_popup_cancel, (dialog, which) ->
                dialog.cancel());
        builder.show();
    }

    /**
     * <p>
     *     This method gets the {@link TextView} responsible for displaying
     *     the shopping list name/title to the toolbar/action bar
     *     and updates its text to reflect the {@link ShoppingListModel}'s name.
     * </p>
     */
    private void updateTitleText() {
        TextView titleText = findViewById(R.id.title_text_view);
        String title = getString(R.string.title_text, shoppingListModel.getName());
        titleText.setText(title);
    }

    /**
     * <p>
     *     Method to handle when the delete image is clicked in the recycler view list item.
     *     This method will remove the item from the list, update the display and then display
     *     a {@link Snackbar} giving the user an option to undo that delete action.
     * </p>
     * <p>
     *     NotifyDataSetChanged is suppressed as calling the more 'efficient' method
     *     {@link RecyclerView.Adapter#notifyItemInserted(int)} throws an index out of bounds
     *     exception.
     * </p>
     * @param position - the index of the deleted item
     */
    @SuppressLint("NotifyDataSetChanged")
    private void swipeLeftToDeleteItem(int position) {
        final ItemModel item = shoppingListModel.getItemList().remove(position);
        item.setDeletedFlag(true);
        shoppingListModel.getDeletedItemList().add(item);

        recyclerAdapter.notifyItemRemoved(position);

        // Display SnackBar with option to undo
        Snackbar.make(
                recyclerView,
                getString(R.string.item_deleted_text, item.getName()),
                Snackbar.LENGTH_LONG
        ).setAction(
                R.string.item_deleted_undo_text, v -> {
                    // Add the item model back to the list at the same index
                    item.setDeletedFlag(false);
                    shoppingListModel.getItemList().add(position, item);
                    shoppingListModel.getDeletedItemList().remove(item);
                    recyclerAdapter.notifyDataSetChanged();
                }
        ).show();
    }
}