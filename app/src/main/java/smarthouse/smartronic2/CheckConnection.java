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
            System.out.println("BURASI ONRECEIVE İÇİ" + activeNetwork.getTypeName());
            System.out.println(activeNetwork.getState());
            type = activeNetwork.getTypeName();
            sendBroadcastMessage(type);
        } else {
            sendBroadcastMessage("");
        }
    }

    private void sendBroadcastMessage(String type) {
        // function which broadcasts to all activities the connection type

        // send broadcast message
        System.out.println("BURAYA BİŞEY GELMİYOR MU ŞİMDİ" + type);
        Intent myIntent = new Intent("network_connection");
        myIntent.putExtra("x", String.valueOf(type));
        context.sendBroadcast(myIntent);
    }
}
