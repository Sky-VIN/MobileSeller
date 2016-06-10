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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Random;

public class PriceActivity extends AppCompatActivity implements AdapterView.OnItemClickListener, View.OnClickListener {

    String[] names = {"One", "Two", "Three", "Four", "Five", "Six", "Seven", "Eight", "Nine", "Ten",
            "Eleven", "Twelve", "Thirteen", "Fourteen", "Fifteen", "Sixteen", "Seventeen", "Eighteen", "Nineteen", "Twenty"};

    PointAdapter adapter;
    TextView tvSummary;

    ArrayList<Point> price = new ArrayList<>();

    ListView priceList;

    Button btnCreateList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_price);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        tvSummary = (TextView) findViewById(R.id.tvSummary);
        btnCreateList = (Button) findViewById(R.id.btnCreatePrice);
        btnCreateList.setOnClickListener(this);

        priceList = (ListView) findViewById(R.id.priceList);
        priceList.setOnItemClickListener(this);


        for (int i = 0; i<names.length; i++) {
            price.add(new Point(names[i],
                    new Random().nextInt(1000), new Random().nextInt(1000), new Rounding().round_up(new Random().nextFloat() * 1000), 0, 0));
        }

        adapter = new PointAdapter(this, price);
        priceList.setAdapter(adapter);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            new AlertDialog.Builder(this)
                    .setIcon(R.mipmap.ic_launcher)
                    .setTitle(R.string.app_name)
                    .setMessage("Если Вы вернетесь назад, то потеряете данные.\n\nПродолжить?")
                    .setNegativeButton("Нет", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int i) {
                            dialog.cancel();
                        }
                    })
                    .setPositiveButton("Да", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            finish();
                        }
                    }).show();
        }
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

                        if (TextUtils.isDigitsOnly(editText.getText().toString()) && !editText.getText().toString().equals("")) {
                            adapter.getPoint(position).amount = Integer.valueOf(editText.getText().toString());
                            adapter.getPoint(position).priceTotal = new Rounding().round_up(Integer.valueOf(editText.getText().toString()) * adapter.getPoint(position).priceUnit);
                            tvSummary.setText(String.valueOf(adapter.getSummary()) + " грн");
                            adapter.notifyDataSetChanged();
                        }
                        else
                            Toast.makeText(getApplicationContext(), "Error input!", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNeutralButton("Удалить", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        adapter.getPoint(position).amount = 0;
                        adapter.getPoint(position).priceTotal = 0;
                        tvSummary.setText(String.valueOf(adapter.getSummary()) + " грн");
                        adapter.notifyDataSetChanged();
                    }
                })
                .setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
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

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.btnCreatePrice) {
            if (adapter.getSummary() == 0)
                Toast.makeText(this, "Список пуст!", Toast.LENGTH_SHORT).show();
            else {
                Intent intent = new Intent(this, SelectedPriceActivity.class);
                intent.putParcelableArrayListExtra("Price", adapter.getSelectedPoints());
                startActivity(intent);
            }
        }
    }
}
