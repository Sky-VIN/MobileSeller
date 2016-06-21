package d.swan.mobileseller;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.EditText;

public class SettingsActivity extends AppCompatActivity {

    DataBaseHelper dbHelper = new DataBaseHelper(this);

    EditText etUser, etSender, etPass, etSMTP, etPort, etReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        String[] settings = dbHelper.loadMailSettings();

        etUser = (EditText) findViewById(R.id.etUser);
        etUser.setText(settings[0]);
        etSender = (EditText) findViewById(R.id.etSender);
        etSender.setText(settings[1]);
        etPass = (EditText) findViewById(R.id.etPass);
        etPass.setText(settings[2]);
        etSMTP = (EditText) findViewById(R.id.etSMTP);
        etSMTP.setText(settings[3]);
        etPort = (EditText) findViewById(R.id.etPort);
        etPort.setText(settings[4]);
        etReceiver = (EditText) findViewById(R.id.etReceiver);
        etReceiver.setText(settings[5]);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {

            String[] mail = new String[] {
                    etUser.getText().toString(),
                    etSender.getText().toString(),
                    etPass.getText().toString(),
                    etSMTP.getText().toString(),
                    etPort.getText().toString(),
                    etReceiver.getText().toString()
            };

            dbHelper.saveMailSettings(mail);
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        // nothing
    }
}
