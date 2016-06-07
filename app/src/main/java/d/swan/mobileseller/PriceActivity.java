package d.swan.mobileseller;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Random;

public class PriceActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    String[] names = {"One", "Two", "Three", "Four", "Five", "Six", "Seven", "Eight", "Nine", "Ten",
            "Eleven", "Twelve", "Thirteen", "Fourteen", "Fifteen", "Sixteen", "Seventeen", "Eighteen", "Nineteen", "Twenty"};
    int amount = 0;
    PointAdapter adapter;
/*
    int hatch, article, amount;
    float priceUnit, priceTotal;
*/

    ArrayList<Point> price = new ArrayList<>();

    ListView priceList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_price);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        priceList = (ListView) findViewById(R.id.priceList);
        priceList.setOnItemClickListener(this);


        for (int i = 0; i<names.length; i++) {
            price.add(new Point(names[i],
                    new Random().nextInt(), new Random().nextInt(), new Random().nextFloat(), new Random().nextFloat(), 0));
        }

        adapter = new PointAdapter(this, price);
        priceList.setAdapter(adapter);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home)
            finish();
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
        final EditText editText = new EditText(this);
        editText.setText(String.valueOf(adapter.getPoint(position).amount));
        editText.selectAll();
        new AlertDialog.Builder(this)
                .setIcon(R.mipmap.ic_launcher)
                .setTitle("Количество")
                .setView(editText)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        if (TextUtils.isDigitsOnly(editText.getText().toString()))
                            adapter.getPoint(position).amount = Integer.valueOf(editText.getText().toString());
                        else
                            Toast.makeText(getApplicationContext(), "Error input!", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                }).show();
    }

    @Override
    public void onBackPressed() {
        // Nothing
    }
}
