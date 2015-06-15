package smarthouse.smartronic2;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Handler;
import android.widget.Toast;

/**
 * Created by burak on 11/06/15.
 */
public class CheckConnection extends BroadcastReceiver {
    Context context;
    String type = "";

    public CheckConnection(Context context) {
        super();
        this.context = context;
        IntentFilter intentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        context.registerReceiver(CheckConnection.this, intentFilter);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
        if (isConnected) {
            System.out.println(activeNetwork.getType());
            System.out.println(activeNetwork.getTypeName());
            System.out.println(activeNetwork.getState());
            type = activeNetwork.getTypeName();
            sendBroadcastMessage(type);
        } else {
            sendBroadcastMessage("");
        }
    }

    private void sendBroadcastMessage(String type) {
        // function which broadcasts to all activities the connection type
        if (type == "") {
            Toast.makeText(context, "No Network",
                    Toast.LENGTH_LONG).show();
            AlertDialog alertDialog = new AlertDialog.Builder(context, 4).create();
            alertDialog.setTitle("Error");
            alertDialog.setMessage("Please make sure you are connected to Internet");
            alertDialog.setCancelable(false);
            alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });

            // send broadcast message
            Intent myIntent = new Intent("network_connection_type");
            myIntent.putExtra("key", type);
            context.sendBroadcast(myIntent);
        }
    }
}
