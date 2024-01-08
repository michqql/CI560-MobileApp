package me.mp1282.shoppinglist.util;

import android.content.Context;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class SwipeLeftDeleteHelperInstrumentedTest {

    private Context context;
    private SwipeLeftDeleteHelper helper;

    @Before
    public void setup() {
        context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        helper = new SwipeLeftDeleteHelper(context, (index) -> {});
    }

    @Test
    public void onSwipe() {
        RecyclerView.ViewHolder viewHolder = new RecyclerView.ViewHolder(new View(context)) {
            @Override
            public String toString() {
                return super.toString();
            }
        };

        helper.onSwiped(viewHolder, -1);
    }
}
