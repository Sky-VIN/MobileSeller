package d.swan.mobileseller;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class PriceActivity extends AppCompatActivity implements AdapterView.OnItemClickListener, View.OnClickListener {


    TextView tvSummary, tvOrganization, tvAddress, tvPrice;

    PointAdapter pointAdapter;
    ArrayList<Point> priceArray = new ArrayList<>();

    ListView priceList;

    Button btnCreateList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_price);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        tvSummary = (TextView) findViewById(R.id.tvSummary);

        tvOrganization = (TextView) findViewById(R.id.tvOrganization);
        tvOrganization.setText(getIntent().getStringExtra("org"));

        tvAddress = (TextView) findViewById(R.id.tvAddress);
        tvAddress.setText(getIntent().getStringExtra("addr"));

        tvPrice = (TextView) findViewById(R.id.tvPrice);
        tvPrice.setText(getIntent().getStringExtra("price"));

        btnCreateList = (Button) findViewById(R.id.btnCreatePrice);
        btnCreateList.setOnClickListener(this);

        priceList = (ListView) findViewById(R.id.priceList);
        priceList.setOnItemClickListener(this);

        priceArray.addAll(getIntent().<Point>getParcelableArrayListExtra("PriceList"));

        pointAdapter = new PointAdapter(this, priceArray);
        priceList.setAdapter(pointAdapter);
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

        final EditText eText = new EditText(this);
        eText.setInputType(InputType.TYPE_CLASS_PHONE);

        new AlertDialog.Builder(this)
                .setIcon(R.mipmap.ic_launcher)
                .setTitle("Количество")
                .setView(eText)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (TextUtils.isDigitsOnly(eText.getText().toString()) && !eText.getText().toString().equals("")) {
                            int amount = Integer.valueOf(eText.getText().toString());
                            float priceUnit = pointAdapter.getPoint(position).priceUnit;
                            refreshPriceList(position, amount, priceUnit);
                        } else
                            Toast.makeText(getApplicationContext(), "Неверный ввод!", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNeutralButton("Удалить", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        refreshPriceList(position, 0, 0);
                    }
                })
                .setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                }).show();
    }

    private void refreshPriceList(int position, int amount, float priceUnit) {
        pointAdapter.getPoint(position).amount = amount;
        pointAdapter.getPoint(position).priceTotal = new Rounding().round_up(amount * priceUnit);
        pointAdapter.notifyDataSetChanged();

        tvSummary.setText(String.valueOf(pointAdapter.getSummary() + " грн"));
    }

    @Override
    public void onBackPressed() {
        // Nothing
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.btnCreatePrice) {
            if (pointAdapter.getSummary() == 0)
                Toast.makeText(this, "Список пуст!", Toast.LENGTH_SHORT).show();
            else {
                Intent intent = new Intent(this, SelectedPriceActivity.class);
                intent.putExtra("org", tvOrganization.getText());
                intent.putExtra("addr", tvAddress.getText());
                intent.putExtra("price", tvPrice.getText());
                intent.putExtra("summary", tvSummary.getText());

                intent.putParcelableArrayListExtra("PriceList", pointAdapter.getSelectedPoints());
                startActivityForResult(intent, 1);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data == null)
            return;

        if (!getIntent().getBooleanExtra("GoHome", false)) {
            ArrayList<Point> selectedPoints = data.getParcelableArrayListExtra("Price");

            for (Point sPoint : selectedPoints)
                for (Point point : priceArray)
                    if (point.name.equals(sPoint.name)) {
                        point.amount = sPoint.amount;
                        point.priceTotal = new Rounding().round_up(point.priceUnit * point.amount);
                    }

            tvSummary.setText(String.valueOf(pointAdapter.getSummary() + " грн"));
            pointAdapter.notifyDataSetChanged();
        } else finish();

        super.onActivityResult(requestCode, resultCode, data);
    }
}
