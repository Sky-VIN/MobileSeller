package d.swan.mobileseller;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class ActivitySelectedPrice extends AppCompatActivity
        implements AdapterView.OnItemClickListener, View.OnClickListener {

    PointAdapter pointAdapter;
    TextView tvSelectedSummary, tvSelectedOrganization, tvSelectedAddress, tvSelectedPrice, tvSelectedPriceComment;

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

        tvSelectedPriceComment = (TextView) findViewById(R.id.tvSelectedPriceComment);
        tvSelectedPriceComment.setText(getIntent().getStringExtra("comment"));

        selectedPriceArray.clear();
        selectedPriceArray.addAll(getIntent().<Point>getParcelableArrayListExtra("priceList"));
        pointAdapter = new PointAdapter(this, selectedPriceArray);

        selectedPriceList = (ListView) findViewById(R.id.selectedPriceList);
        selectedPriceList.setOnItemClickListener(this);
        selectedPriceList.setAdapter(pointAdapter);
    }

    @Override
    public void onItemClick(final AdapterView<?> adapterView, View view, final int position, long l) {

        final EditText eText = new EditText(this);
        eText.setSingleLine();
        eText.setBackgroundResource(android.R.drawable.edit_text);
        eText.setInputType(InputType.TYPE_CLASS_PHONE);

        new AlertDialog.Builder(this)
                .setIcon(R.mipmap.ic_launcher)
                .setTitle("Количество")
                .setView(eText)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String amountText = eText.getText().toString();
                        if (TextUtils.isDigitsOnly(amountText) && !amountText.equals("")) {
                            int amount = Integer.valueOf(amountText);
                            float priceUnit = pointAdapter.getPoint(position).priceUnit;
                            refreshPriceList(position, amount, priceUnit);
                        } else
                            Toast.makeText(ActivitySelectedPrice.this, "Неверный ввод!", Toast.LENGTH_SHORT).show();
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
                })
                .show();
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
            goHome(false);
        return super.onOptionsItemSelected(item);
    }

    private void goHome(boolean isHome) {
        if (isHome)
            startActivity(new Intent(this, ActivityMain.class));
        else {
            Intent intent = new Intent();
            intent.putParcelableArrayListExtra("priceList", selectedPriceArray);
            setResult(RESULT_OK, intent);
        }
        finish();
    }

    @Override
    public void onBackPressed() {
        // nothing
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btnSavePrice) {

            ExcelWorker excelWorker = new ExcelWorker();

            final String datetime = new SimpleDateFormat("dd.MM.yyyy HH-mm-ss").format(System.currentTimeMillis());

            excelWorker.setDateTime(datetime);
            excelWorker.setOrganization(getIntent().getStringExtra("org"));
            excelWorker.setAddress(getIntent().getStringExtra("addr"));
            excelWorker.setComment(getIntent().getStringExtra("comment"));
            excelWorker.setSummary(pointAdapter.getSummary());
            excelWorker.setPriceArray(selectedPriceArray);

            try {
                excelWorker.writeIntoExcel();
            } catch (IOException e) {
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            } finally {
                new AlertDialog.Builder(this)
                        .setIcon(R.mipmap.ic_launcher)
                        .setTitle(R.string.app_name)
                        .setMessage("Прайс успешно сохранен по адресу\n"
                                + Environment.getExternalStorageDirectory().toString()
                                + "/Mobile Seller/"
                                + datetime
                                + ".xls\n\n Отправить прайс?")
                        .setPositiveButton("Да", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int i) {
                                sendEmail(new File(Environment.getExternalStorageDirectory().getAbsolutePath()
                                        + "/Mobile Seller/"
                                        + datetime
                                        + ".xls"));
                            }
                        })
                        .setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int i) {
                                goHome(true);
                                dialog.cancel();

                            }
                        })
                        .show();
            }
        }
    }

    private void sendEmail(File file) {

        String[] mail = new DataBaseHelper(this).loadMailSettings();
        Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);

        emailIntent.setType("file/*");
        emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{mail[5]});
        emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, mail[0]);

        Uri uri = Uri.fromFile(file);
        emailIntent.putExtra(Intent.EXTRA_STREAM, uri);

        startActivityForResult(Intent.createChooser(emailIntent, null), 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        goHome(true);
        super.onActivityResult(requestCode, resultCode, data);
    }
}
