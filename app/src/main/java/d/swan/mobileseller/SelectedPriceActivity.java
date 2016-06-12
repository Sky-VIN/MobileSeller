package d.swan.mobileseller;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;

public class SelectedPriceActivity extends AppCompatActivity implements AdapterView.OnItemClickListener, View.OnClickListener {

    PointAdapter adapter;
    TextView tvSelectedSummary;

    ListView selectedPriceList;
    ArrayList<Point> selectedPoints = new ArrayList<>();

    Button btnSavePrice;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selected_price);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        btnSavePrice = (Button) findViewById(R.id.btnSavePrice);
        btnSavePrice.setOnClickListener(this);

        selectedPoints.addAll(getIntent().<Point>getParcelableArrayListExtra("Price"));


        adapter = new PointAdapter(this, selectedPoints);

        selectedPriceList = (ListView) findViewById(R.id.selectedPriceList);
        selectedPriceList.setOnItemClickListener(this);

        selectedPriceList.setAdapter(adapter);

        tvSelectedSummary = (TextView) findViewById(R.id.tvSelectedSummary);
        tvSelectedSummary.setText(String.valueOf(adapter.getSummary()));
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, final int position, long l) {
        final EditText eText = new EditText(this);
        eText.setText(String.valueOf(adapter.getPoint(position).amount));
        eText.setInputType(InputType.TYPE_CLASS_PHONE);
        eText.selectAll();

        new AlertDialog.Builder(this)
                .setIcon(R.mipmap.ic_launcher)
                .setTitle("Количество")
                .setView(eText)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        if (TextUtils.isDigitsOnly(eText.getText().toString()) && !eText.getText().toString().equals("")) {
                            adapter.getPoint(position).amount = Integer.valueOf(eText.getText().toString());
                            adapter.getPoint(position).priceTotal = new Rounding().round_up(Integer.valueOf(eText.getText().toString()) * adapter.getPoint(position).priceUnit);
                            tvSelectedSummary.setText(String.valueOf(adapter.getSummary()) + " грн");
                            adapter.notifyDataSetChanged();
                        } else
                            Toast.makeText(getApplicationContext(), "Неверный ввод!", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNeutralButton("Удалить", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        adapter.getPoint(position).amount = 0;
                        adapter.getPoint(position).priceTotal = 0;
                        tvSelectedSummary.setText(String.valueOf(adapter.getSummary()) + " грн");
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
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            Intent intent = new Intent();
            intent.putParcelableArrayListExtra("Price", selectedPoints);
            setResult(RESULT_OK, intent);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        //
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if(id == R.id.btnSavePrice) {
            ExcelWorker excelWorker = new ExcelWorker();
            try {
                excelWorker.writeIntoExcel("org", "addr", selectedPoints, adapter.getSummary());
            } catch (IOException e) {
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }
}
