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
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, AdapterView.OnItemSelectedListener, View.OnClickListener {

    ArrayList<String> org = new ArrayList<>();
    ArrayList<String> addr = new ArrayList<>();

    ArrayAdapter<String> orgAdapter;
    ArrayAdapter<String> addrAdapter;



    Spinner spinnerOrgName, spinnerAddrName;
    Button btnNewPrice, btnOrgAdd, btnAddrAdd, btnOrgDel, btnAddrDel;

    DataBaseHelper dbh = new DataBaseHelper(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // startActivity(new Intent(this, PriceActivity.class));

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarMain);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, 0, 0);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

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
        refreshAddrSpinner();

    }

    private void refreshOrgSpinner() {
        org.clear();
        org.addAll(dbh.getAllValues("Organization"));

        orgAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, org);
        orgAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        orgAdapter.notifyDataSetChanged();
        spinnerOrgName.setAdapter(orgAdapter);
    }

    private void refreshAddrSpinner() {
        addr.clear();
        addr.addAll(dbh.getAllValues("Address", spinnerOrgName.getSelectedItemPosition() + 1));

        addrAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, addr);
        addrAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        addrAdapter.notifyDataSetChanged();
        spinnerAddrName.setAdapter(addrAdapter);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            // Nothing
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.

        int id = item.getItemId();

        if (id == R.id.nav_update) {
            Toast.makeText(this, "Кнопка пока не работает...", Toast.LENGTH_SHORT).show();

        } else if (id == R.id.nav_settings) {
            startActivity(new Intent(this, SettingsActivity.class));

        } else if (id == R.id.nav_exit) {
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
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) throws NullPointerException {

        if(adapterView.getId() == R.id.spinnerOrgName)
            refreshAddrSpinner();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.btnNewPrice) {
            if (spinnerOrgName.getSelectedItemPosition() == -1 || spinnerAddrName.getSelectedItemPosition() == -1)
                Toast.makeText(this, "Не выбран один из пунктов!", Toast.LENGTH_SHORT).show();
            else
                startActivity(new Intent(this, PriceActivity.class));
        } else

        if (id == R.id.btnOrgAdd) {

            final EditText eText = new EditText(this);
            new AlertDialog.Builder(this)
                    .setTitle("Добавление организации")
                    .setIcon(R.mipmap.ic_launcher)
                    .setView(eText)
                    .setPositiveButton("Добавить", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int i) {
                            if (!eText.getText().toString().equals("")) {
                                if (!dbh.identityVerification("Organization", eText.getText().toString())) {
                                    dbh.addField("Organization", eText.getText().toString());
                                    refreshOrgSpinner();
                                    refreshAddrSpinner();
                                    if (spinnerOrgName.getCount() > 0)
                                        spinnerOrgName.setSelection(spinnerOrgName.getCount() - 1);
                                }
                                else
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

        if (id == R.id.btnAddrAdd) {
            if (spinnerOrgName.getSelectedItemPosition() == -1)
                Toast.makeText(this, "Не выбрана организация!", Toast.LENGTH_SHORT).show();
            else {
                final EditText eText = new EditText(this);
                new AlertDialog.Builder(this)
                        .setTitle("Добавление адреса")
                        .setIcon(R.mipmap.ic_launcher)
                        .setView(eText)
                        .setPositiveButton("Добавить", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int i) {
                                if (!eText.getText().toString().equals("")) {
                                    if (!dbh.identityVerification("Address", eText.getText().toString())) {
                                        dbh.addField("Address", eText.getText().toString(), spinnerOrgName.getSelectedItemPosition() + 1);

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
                                dbh.deleteField("Organization", spinnerOrgName.getSelectedItemPosition() + 1);
                                dbh.deleteField("Address", spinnerOrgName.getSelectedItemPosition() + 1);

                                refreshOrgSpinner();
                                refreshAddrSpinner();
                                if (spinnerOrgName.getCount() > 0)
                                    spinnerOrgName.setSelection(spinnerOrgName.getCount() - 1);
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
}
