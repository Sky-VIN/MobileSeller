package d.swan.mobileseller;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, AdapterView.OnItemSelectedListener, View.OnClickListener {

    List<String> orgArray = new ArrayList<>();
    List<String> addrArray = new ArrayList<>();

    ArrayAdapter<String> orgAdapter;
    ArrayAdapter<String> addrAdapter;

    RadioButton radioRetail, radioWholesale;
    Spinner spinnerOrgName, spinnerAddrName;
    Button btnNewPrice, btnOrgAdd, btnAddrAdd, btnOrgDel, btnAddrDel;

    DataBaseHelper dbHelper = new DataBaseHelper(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarMain);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, 0, 0);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        radioRetail = (RadioButton) findViewById(R.id.radioRetail);
        radioWholesale = (RadioButton) findViewById(R.id.radioWholesale);

        btnNewPrice = (Button) findViewById(R.id.btnNewPrice);
        btnNewPrice.setOnClickListener(this);

        btnOrgAdd = (Button) findViewById(R.id.btnOrgAdd);
        btnOrgAdd.setOnClickListener(this);

        btnAddrAdd = (Button) findViewById(R.id.btnAddrAdd);
        btnAddrAdd.setOnClickListener(this);

        btnOrgDel = (Button) findViewById(R.id.btnOrgDel);
        btnOrgDel.setOnClickListener(this);

        btnAddrDel = (Button) findViewById(R.id.btnAddrDel);
        btnAddrDel.setOnClickListener(this);

        spinnerOrgName = (Spinner) findViewById(R.id.spinnerOrgName);
        spinnerOrgName.setOnItemSelectedListener(this);

        spinnerAddrName = (Spinner) findViewById(R.id.spinnerAddrName);
        spinnerAddrName.setOnItemSelectedListener(this);

        refreshOrgSpinner();
    }

    private void refreshOrgSpinner() {
        orgArray.clear();
        orgArray.addAll(dbHelper.getAllValues("Organization"));

        orgAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, orgArray);
        orgAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerOrgName.setAdapter(orgAdapter);
        orgAdapter.notifyDataSetChanged();
    }

    private void refreshAddrSpinner() {
        addrArray.clear();
        if (spinnerOrgName.getCount() > 0) {
            int linked_id = dbHelper.getLinkedIdByName("Organization", spinnerOrgName.getSelectedItem().toString());
            addrArray.addAll(dbHelper.getAllValues("Address", linked_id));
        }

        addrAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, addrArray);
        addrAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerAddrName.setAdapter(addrAdapter);
        addrAdapter.notifyDataSetChanged();

    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) throws NullPointerException {
        if (adapterView.getId() == R.id.spinnerOrgName)
            refreshAddrSpinner();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
        // Nothing
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START))
            drawer.closeDrawer(GravityCompat.START);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        int id = item.getItemId();

        // Кнопка "Обновить"
        if (id == R.id.nav_update) {
/*
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("file*/
/*");
            startActivityForResult(intent, 1);
*/
            Toast.makeText(this, "Кнопка пока не работает...", Toast.LENGTH_SHORT).show();
        } else
            // Кнопка "Настройки"
            if (id == R.id.nav_settings) {
                startActivity(new Intent(this, SettingsActivity.class));
            } else
                // Кнопка "Выход"
                if (id == R.id.nav_exit) {
                    new AlertDialog.Builder(this)
                            .setIcon(R.mipmap.ic_launcher)
                            .setTitle(R.string.app_name)
                            .setMessage("Подтвердите выход")
                            .setPositiveButton("Да", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    finish();
                                }
                            })
                            .setNegativeButton("Нет", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            }).show();
                }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onClick(View view) {

        int id = view.getId();

        // Нажатие на кнопку Создать (Price)
        if (id == R.id.btnNewPrice) {
            if (spinnerOrgName.getSelectedItemPosition() == -1 || spinnerAddrName.getSelectedItemPosition() == -1)
                Toast.makeText(this, "Не выбран один из пунктов!", Toast.LENGTH_SHORT).show();
            else {
                Intent intent = new Intent(this, PriceActivity.class);
                intent.putExtra("org", spinnerOrgName.getSelectedItem().toString());
                intent.putExtra("addr", spinnerAddrName.getSelectedItem().toString());

                if (radioRetail.isChecked()) {
                    intent.putExtra("price", "Розничная");

                    intent.putParcelableArrayListExtra("PriceList", new PriceListFiller(getResources()).getRetailPrice());
                } else {
                    intent.putExtra("price", "Оптовая");
                    intent.putParcelableArrayListExtra("PriceList", new PriceListFiller(getResources()).getWholesalePrice());
                }

                startActivity(intent);
            }
        } else
            // Нажатие на кнопку "Добавть организацию"
            if (id == R.id.btnOrgAdd) {
                final EditText eText = new EditText(this);
                eText.setFocusable(true);
                new AlertDialog.Builder(this)
                        .setTitle("Добавление организации")
                        .setIcon(R.mipmap.ic_launcher)
                        .setView(eText)
                        .setPositiveButton("Добавить", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int i) {
                                if (!eText.getText().toString().equals("")) {
                                    // Проверка на идентичность имени
                                    if (!dbHelper.identityVerification("Organization", eText.getText().toString())) {
                                        dbHelper.addField("Organization", eText.getText().toString(), dbHelper.findFreeLinkedId());
                                        refreshOrgSpinner();
                                        refreshAddrSpinner();
                                        if (spinnerOrgName.getCount() > 0)
                                            spinnerOrgName.setSelection(spinnerOrgName.getCount() - 1);
                                    } else
                                        Toast.makeText(getApplicationContext(), "Такая организайия уже есть!", Toast.LENGTH_SHORT).show();
                                }
                            }
                        })
                        .setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int i) {
                                dialog.cancel();
                            }
                        }).show();
            } else
                // Нажатие на кнопку "Добавть адрес"
                if (id == R.id.btnAddrAdd) {
                    if (spinnerOrgName.getSelectedItemPosition() == -1)
                        Toast.makeText(this, "Не выбрана организация!", Toast.LENGTH_SHORT).show();
                    else {
                        final EditText eText = new EditText(this);
                        eText.setFocusable(true);
                        new AlertDialog.Builder(this)
                                .setTitle("Добавление адреса")
                                .setIcon(R.mipmap.ic_launcher)
                                .setView(eText)
                                .setPositiveButton("Добавить", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int i) {
                                        if (!eText.getText().toString().equals("")) {
                                            // Проверка на идентичность имени
                                            if (!dbHelper.identityVerification("Address", eText.getText().toString())) {
                                                int linked_id = dbHelper.getLinkedIdByName("Organization", spinnerOrgName.getSelectedItem().toString());
                                                dbHelper.addField("Address", eText.getText().toString(), linked_id);
                                                refreshAddrSpinner();
                                                if (spinnerAddrName.getCount() > 0)
                                                    spinnerAddrName.setSelection(spinnerAddrName.getCount() - 1);
                                            } else
                                                Toast.makeText(getApplicationContext(), "Такой адрес уже есть!", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                })
                                .setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int i) {
                                        dialog.cancel();
                                    }
                                }).show();
                    }
                } else
                    // Нажатие на кнопку "Удалить организацию"
                    if (id == R.id.btnOrgDel) {
                        if (spinnerOrgName.getSelectedItemPosition() == -1)
                            Toast.makeText(this, "Удалять нечего!", Toast.LENGTH_SHORT).show();
                        else {
                            new AlertDialog.Builder(this)
                                    .setTitle("Удаление организации")
                                    .setIcon(R.mipmap.ic_launcher)
                                    .setMessage("Вы точно хотите удалить \"" + spinnerOrgName.getSelectedItem().toString() + "\"?")
                                    .setPositiveButton("Да", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int i) {
                                            int linked_id = dbHelper.getLinkedIdByName("Organization", spinnerOrgName.getSelectedItem().toString());
                                            dbHelper.deleteField("Organization", linked_id);
                                            dbHelper.deleteField("Address", linked_id);
                                            refreshOrgSpinner();
                                            refreshAddrSpinner();
                                            if (spinnerOrgName.getCount() > 0)
                                                spinnerOrgName.setSelection(spinnerOrgName.getSelectedItemPosition() - 1);
                                        }
                                    })
                                    .setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int i) {
                                            dialog.cancel();
                                        }
                                    }).show();
                        }
                    } else
                        // Нажатие на кнопку "Удалить адрес"
                        if (id == R.id.btnAddrDel) {
                            if (spinnerAddrName.getSelectedItemPosition() == -1)
                                Toast.makeText(this, "Удалять нечего!", Toast.LENGTH_SHORT).show();
                            else {
                                new AlertDialog.Builder(this)
                                        .setTitle("Удаление адреса")
                                        .setIcon(R.mipmap.ic_launcher)
                                        .setMessage("Вы точно хотите удалить \"" + spinnerAddrName.getSelectedItem().toString() + "\"?")
                                        .setPositiveButton("Да", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int i) {
                                                dbHelper.deleteField("Address", spinnerAddrName.getSelectedItem().toString());
                                                refreshAddrSpinner();
                                                if (spinnerAddrName.getCount() > 0)
                                                    spinnerAddrName.setSelection(spinnerAddrName.getSelectedItemPosition() - 1);
                                            }
                                        })
                                        .setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int i) {
                                                dialog.cancel();
                                            }
                                        }).show();
                            }
                        }
    }


/*
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data == null)
            return;

        File file = new File(data.getData().toString());
        String sub = file.getPath().substring(5, file.getPath().length());
        ExcelWorker worker = new ExcelWorker();
        try {
            worker.readFromExcel(this, sub);
        } catch (IOException e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
*/

}
