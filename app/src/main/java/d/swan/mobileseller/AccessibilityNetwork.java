package d.swan.mobileseller;

import android.content.Context;
import android.net.ConnectivityManager;

public final class AccessibilityNetwork {
    private Context context;

    AccessibilityNetwork(Context context) {
        this.context = context;
    }

    public boolean check() {
        ConnectivityManager cManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return cManager.getActiveNetworkInfo() != null;
    }
}
