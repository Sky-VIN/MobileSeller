package d.swan.mobileseller;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by daniel on 6/30/16.
 */
public class Email {
    private Activity activity;

    Email(Activity activity) {
        this.activity = activity;
    }

    public void Send(List<String> files) {
        String[] mail = new DataBaseHelper(activity.getApplicationContext()).loadMailSettings();
        ArrayList<Uri> uris = new ArrayList<>();
        uris.clear();
        for (String s : files)
            uris.add(Uri.parse("file:" + Environment.getExternalStorageDirectory().getAbsolutePath() + "/Mobile Seller/" + s));

        Intent emailIntent = new Intent(Intent.ACTION_SEND_MULTIPLE);

        emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{mail[5]});
        emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, mail[0]);
        emailIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris);

        emailIntent.setType("application/octet-stream");

        activity.startActivityForResult(Intent.createChooser(emailIntent, null), 1);
    }
}
