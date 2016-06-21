package d.swan.mobileseller;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.menu.ActionMenuItem;
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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class SelectedPriceActivity extends AppCompatActivity implements AdapterView.OnItemClickListener, View.OnClickListener {

    PointAdapter pointAdapter;
    TextView tvSelectedSummary, tvSelectedOrganization, tvSelectedAddress, tvSelectedPrice;

    ListView selectedPriceList;
    ArrayList<Point> selectedPriceArray = new ArrayList<>();

    Button btnSavePrice;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selected_price);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        btnSavePrice = (Button) findViewById(R.id.btnSavePrice);
        btnSavePrice.setOnClickListener(this);

        tvSelectedSummary = (TextView) findViewById(R.id.tvSelectedSummary);
        tvSelectedSummary.setText(getIntent().getStringExtra("summary"));

        tvSelectedOrganization = (TextView) findViewById(R.id.tvSelectedOrganization);
        tvSelectedOrganization.setText(getIntent().getStringExtra("org"));

        tvSelectedAddress = (TextView) findViewById(R.id.tvSelectedAddress);
        tvSelectedAddress.setText(getIntent().getStringExtra("addr"));

        tvSelectedPrice = (TextView) findViewById(R.id.tvSelectedPrice);
        tvSelectedPrice.setText(getIntent().getStringExtra("price"));

        selectedPriceArray.addAll(getIntent().<Point>getParcelableArrayListExtra("PriceList"));
        pointAdapter = new PointAdapter(this, selectedPriceArray);

        selectedPriceList = (ListView) findViewById(R.id.selectedPriceList);
        selectedPriceList.setOnItemClickListener(this);
        selectedPriceList.setAdapter(pointAdapter);

    }

    @Override
    public void onItemClick(final AdapterView<?> adapterView, View view, final int position, long l) {
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

        tvSelectedSummary.setText(String.valueOf(pointAdapter.getSummary() + " грн"));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home)
            goHome();
        return super.onOptionsItemSelected(item);
    }

    private void goHome() {
        Intent intent = new Intent();
        intent.putParcelableArrayListExtra("Price", selectedPriceArray);
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        // nothing
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.btnSavePrice) {
            new ExcelWorker();
            try {
                final String filename = ExcelWorker.writeIntoExcel(getIntent().getStringExtra("org"), getIntent().getStringExtra("addr"), selectedPriceArray, pointAdapter.getSummary());
                new AlertDialog.Builder(this)
                        .setIcon(R.mipmap.ic_launcher)
                        .setTitle(R.string.app_name)
                        .setMessage("Прайс успешно сохранен по адресу\n" + filename + "\n\n Отправить заказ на почту?")
                        .setPositiveButton("Да", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int i) {
                                sendEmail(new File(filename));
                                goHome();
                            }
                        })
                        .setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int i) {
                                dialog.cancel();
                                goHome();
                            }
                        }).show();
            } catch (IOException e) {
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void sendEmail(File file) {

        Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);

        emailIntent.setType("file/*");
        emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{"asdf"});
        emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, R.string.app_name);

        Uri uri = Uri.fromFile(file);
        emailIntent.putExtra(Intent.EXTRA_STREAM, uri);

        startActivity(Intent.createChooser(emailIntent, "Pick an Email provider"));
    }
}
