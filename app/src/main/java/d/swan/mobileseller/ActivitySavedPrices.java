package d.swan.mobileseller;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ActivitySavedPrices extends AppCompatActivity implements View.OnClickListener {

    ListView savedPricesList;
    Button btnSendPrice;

    List<String> files = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_prices);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        btnSendPrice = (Button) findViewById(R.id.btnSendPrice);
        btnSendPrice.setOnClickListener(this);

        savedPricesList = (ListView) findViewById(R.id.savedPricesList);

        File dir = new File("/sdcard/Mobile Seller");
        if (dir.exists() && dir.isDirectory()) {
            for (File f : dir.listFiles())
                files.add(f.getName());
        }

        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_checked, files);
        savedPricesList.setAdapter(adapter);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home)
            finish();
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {

    }
}
