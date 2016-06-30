package d.swan.mobileseller;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.SparseBooleanArray;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ActivitySavedPrices extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemLongClickListener {

    ListView savedPricesList;
    Button btnSendPrice, btnDeletePrice;

    List<String> files = new ArrayList<>();
    List<String> selectedFiles = new ArrayList<>();

    ArrayAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_prices);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        btnSendPrice = (Button) findViewById(R.id.btnSendPrice);
        btnSendPrice.setOnClickListener(this);

        btnDeletePrice = (Button) findViewById(R.id.btnDeletePrice);
        btnDeletePrice.setOnClickListener(this);

        savedPricesList = (ListView) findViewById(R.id.savedPricesList);
        savedPricesList.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        savedPricesList.setOnItemLongClickListener(this);

        listFill();
    }

    private void listFill() {
        File dir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Mobile Seller");
        files.clear();
        if (dir.exists() && dir.isDirectory()) {
            for (File f : dir.listFiles())
                files.add(f.getName());
        }

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_checked, files);
        savedPricesList.setAdapter(adapter);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home)
            finish();
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        // nothing
    }

    @Override
    public void onClick(View view) {

        int id = view.getId();

        selectedFiles.clear();
        getSelectedFiles();

        if (id == R.id.btnDeletePrice) {
            if (selectedFiles.size() > 0) {
                new AlertDialog.Builder(this)
                        .setIcon(R.mipmap.ic_launcher)
                        .setTitle(R.string.app_name)
                        .setMessage("Вы точно хотите удалить выделеные элементы?")
                        .setPositiveButton("Да", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                deleteSelectedFiles();
                                listFill();
                            }
                        })
                        .setNegativeButton("Нет", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int i) {
                                dialog.dismiss();
                            }
                        })
                        .show();
            } else Toast.makeText(this, "Удалять нечего!", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.btnSendPrice) {
            if (selectedFiles.size() > 0) {
                new AlertDialog.Builder(this)
                        .setIcon(R.mipmap.ic_launcher)
                        .setTitle(R.string.app_name)
                        .setMessage("Вы точно хотите отправить выделеные элементы?")
                        .setPositiveButton("Да", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                new Email(ActivitySavedPrices.this).Send(selectedFiles);
                            }
                        })
                        .setNegativeButton("Нет", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int i) {
                                dialog.dismiss();
                            }
                        })
                        .show();
            } else Toast.makeText(this, "Отправлять нечего!", Toast.LENGTH_SHORT).show();
        }

    }

    private void deleteSelectedFiles() {
        for (int i = 0; i < selectedFiles.size(); i++) {
            File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath()
                    + "/Mobile Seller/"
                    + selectedFiles.get(i));
            if (!file.delete())
                Toast.makeText(this, "Файл " + file.getName() + " не удален!", Toast.LENGTH_SHORT).show();
        }
    }

    private void getSelectedFiles() {
        SparseBooleanArray sbArray = savedPricesList.getCheckedItemPositions();
        selectedFiles.clear();
        for (int i = 0; i < sbArray.size(); i++) {
            int key = sbArray.keyAt(i);
            if (sbArray.get(key))
                selectedFiles.add(files.get(i));
        }
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long l) {

        final String app = "com.google.android.apps.docs.editors.sheets";
        PackageManager pManager = getPackageManager();
        try {
            PackageInfo pInfo = pManager.getPackageInfo(app, 0);
            openXLS(new File(Environment.getExternalStorageDirectory() + "/Mobile Seller/" + files.get(position)));
        } catch (PackageManager.NameNotFoundException e) {
            new AlertDialog.Builder(this)
                    .setIcon(R.mipmap.ic_launcher)
                    .setTitle(R.string.app_name)
                    .setMessage("Не установлено приложение для чтения XLS файлов.\nХотите его установить?")
                    .setPositiveButton("Да", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            openMarket(app);
                        }
                    })
                    .setNegativeButton("Нет", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .show();
        }
        return false;
    }

    private void openMarket(String application) {
        try {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + application)));
        } catch (android.content.ActivityNotFoundException anfe) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + application)));
        }
    }

    private void openXLS(File file) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(file), "application/vnd.ms-excel");
        startActivity(intent);
    }
}
