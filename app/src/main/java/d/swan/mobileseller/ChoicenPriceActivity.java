package d.swan.mobileseller;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class ChoicenPriceActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    PointAdapter adapter;
    TextView tvSelectedSummary;

    ListView selectedPriceList;
    ArrayList<Point> selectedPoints = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choicen_price);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        selectedPoints.addAll(getIntent().<Point>getParcelableArrayListExtra("Price"));


        adapter = new PointAdapter(this, selectedPoints);

        selectedPriceList = (ListView) findViewById(R.id.selectedPriceList);
        selectedPriceList.setOnItemClickListener(this);

        selectedPriceList.setAdapter(adapter);

        tvSelectedSummary = (TextView) findViewById(R.id.tvSelectedSummary);
        tvSelectedSummary.setText(String.valueOf(adapter.getSummary()));
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home)
            finish();
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        //
    }
}
