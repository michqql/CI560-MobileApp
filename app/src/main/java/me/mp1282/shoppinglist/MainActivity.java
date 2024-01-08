package me.mp1282.shoppinglist;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import me.mp1282.shoppinglist.db.DBHelper;

/**
 * <p>
 * Main activity displays a splash screen before switching to the {@link HomeActivity}
 * </p>
 * <p>
 * This class extends {@link Activity} instead of {@link androidx.appcompat.app.AppCompatActivity}
 * to avoid an error that would occur when displaying the splash screen.
 * </p>
 */
public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Load the data from the SQLite database once
        DBHelper dbHelper = DBHelper.getInstance(this);
        dbHelper.loadShoppingLists();

        // Switch to the home activity
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
    }
}