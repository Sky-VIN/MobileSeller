package d.swan.mobileseller;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by VIN on 21.06.2016.
 */
public class Properties {
    private Context context;

    private static final String PREF_NAME = "Properties";

    protected String user, sender, pass, smtp, port, receiver;
    protected int orgPos, addrPos;
    protected boolean isRetail;

    Properties(Context context) {
        this.context = context;
    }

    public void loadPositions() {
        SharedPreferences sPref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);

        orgPos = sPref.getInt("orgPos", 0);
        addrPos = sPref.getInt("addrPos", 0);

        isRetail = sPref.getBoolean("isRetail", true);
    }

    public void loadMailSettings() {
        SharedPreferences sPref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);

        user = sPref.getString("user", "");
        sender = sPref.getString("sender", "");
        pass = sPref.getString("pass", "");
        smtp = sPref.getString("smtp", "");
        port = sPref.getString("port", "");
        receiver = sPref.getString("receiver", "");
    }

    public void savePositions(int orgPos, int addrPos, boolean isRetail) {
        SharedPreferences sPref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sPref.edit();
        editor.putInt("orgPos", orgPos);
        editor.putInt("addrPos", addrPos);

        editor.putBoolean("isRetail", isRetail);

        editor.apply();
    }

    public void saveMailSettings(String[] mail) {
        SharedPreferences sPref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sPref.edit();

        editor.putString("user", mail[0]);
        editor.putString("sender", mail[1]);
        editor.putString("pass", mail[2]);
        editor.putString("smtp", mail[3]);
        editor.putString("port", mail[4]);
        editor.putString("receiver", mail[5]);

        editor.apply();
    }

}
