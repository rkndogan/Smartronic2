package smarthouse.smartronic2;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

/**
 * Created by burak on 16/06/15.
 */
public class ReceiveBroadcastMessage extends BroadcastReceiver {
    String message = "";
    String action = "";

    @Override
    public IBinder peekService(Context myContext, Intent service) {
        return super.peekService(myContext, service);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        action = intent.getAction();
        message = intent.getStringExtra("key");
        System.out.println(message);
        setMessage(message);
    }

    public ReceiveBroadcastMessage() {
        super();
    }

    public String setMessage(String message) {
        this.message = message;
        return message;
    }
}
