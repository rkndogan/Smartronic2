package smarthouse.smartronic2;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class Room extends Activity {

    String dataURL = "/data_request?id=lu_sdata&loadtime=0&dataversion=0";

    HashMap<String, Integer> user = new HashMap<>();
    //public SharedPreferences mPrefs;
    int say = 1;
    MainActivity ma = new MainActivity();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room);
        SharedPreferences mPrefs = getSharedPreferences("rooms", MODE_PRIVATE);
        Button button = (Button) findViewById(R.id.kitchenTextView);
        button.setText(mPrefs.getString("room1", ""));
        button.setBackgroundResource(R.drawable.circle);
        registerBroadcastReceiver();
        //dynamically update xml

        String internalIp = ma.InternalIp;
        dataURL = internalIp + dataURL;
        dataURL = "http://" + dataURL;


        // Buttons

        Button saloonButton = (Button) findViewById(R.id.saloonTextView);
        //Button kitchenButton = (Button) findViewById(R.id.kitchenTextView);
        Button bedRoomButton = (Button) findViewById(R.id.bedRoomTextView);
        Button kidRoomButton = (Button) findViewById(R.id.kidRoomTextView);

        buttonHandler(saloonButton, button, bedRoomButton, kidRoomButton);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        super.onCreateOptionsMenu(menu);
        menu.add("Logout");
        menu.add("Home");
        getMenuInflater().inflate(R.menu.menu_room, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void buttonHandler(Button saloonButton, Button kitchenButton, Button bedRoomButton, Button kidRoomButton) {

        saloonButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //ViewStub stub = (ViewStub) findViewById(R.id.saloon_stub);
                Integer saloon = R.id.x;
                View view = findViewById(R.id.x);
                checkViewStubArray("saloon", saloon, view);
            }
        });

        kitchenButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //ViewStub stub = (ViewStub) findViewById(R.id.kitchen1);
                Integer kitchen = R.id.kitchen;
                View view = findViewById(R.id.kitchen);
                checkViewStubArray("kitchen", kitchen, view);
            }
        });

        bedRoomButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Integer bedroom = R.id.bedroom;
                View view = findViewById(R.id.bedroom);
                checkViewStubArray("living_room", bedroom, view);
            }
        });

        kidRoomButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Integer kidroom = R.id.kidRoom;
                View view = findViewById(R.id.kidRoom);
                checkViewStubArray("kidroom", kidroom, view);
            }
        });
    }

    public HashMap<String, Integer> addViewStubArray(String name, Integer i) {

        user.put(name, i);
        //System.out.println(user.entrySet());
        //System.out.println(user.size());
        return user;
    }

    public HashMap<String, Integer> deleteViewStubArray(String name) {

        user.remove(name);
        //System.out.println("delete section entry set:" + user.entrySet());
        return user;
    }

    public HashMap<String, Integer> checkViewStubArray(String name, Integer i, View layout) {

        Iterator entries = user.entrySet().iterator();
        //System.out.println("Clicked layouts are:" + user.size());
        View view = findViewById(i);
        View view1 = findViewById(R.id.kitchen);

        if (!entries.hasNext()) {
            view1.setVisibility(View.GONE);
            addViewStubArray(name, i);
            view.setVisibility(View.VISIBLE);
        }

        while (entries.hasNext()) {
            //System.out.println("layout array has next!!");
            Map.Entry entry = (Map.Entry) entries.next();
            String key = (String) entry.getKey();
            Integer value = (Integer) entry.getValue();

            if (key.equals(name) && value.equals(i)) {

                Toast.makeText(getApplicationContext(), "You are already in : " + key, Toast.LENGTH_SHORT).show();
                //System.out.println("Not First Click");
                System.out.println(user.entrySet());
                view.setVisibility(View.GONE);
                deleteViewStubArray(name);

            } else {

                layout = findViewById(value);
                layout.setVisibility(View.GONE);
                view.setVisibility(View.VISIBLE);
                deleteViewStubArray(key);
                addViewStubArray(name, i);
                //System.out.println("Entry key is:" + key + "entry value is: " + value);
            }
        }
        return user;
    }

    private BroadcastReceiver myBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            say++;
            String action;

            if (say < 4) {
                final LinearLayout ll = (LinearLayout) findViewById(R.id.linearLayout);
                final Button btn = new Button(getApplicationContext());
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
                btn.setText("new_button");
                btn.setId(R.id.melih_butonu);
                btn.setBackgroundResource(R.drawable.circle);
                btn.setLayoutParams(params);
                ll.addView(btn);

                Button x = (Button) findViewById(R.id.melih_butonu);
                x.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        System.out.println("bana tıkladılar abi");
                    }
                });
            }
            action = intent.getAction();
            System.out.println("the ACTION is that:" + action);

            switch (0) {
                default:
                    if (action.equals("polling guncellemesi")) {
                        try {
                            JSONObject jsonObject;
                            jsonObject = new JSONObject(intent.getStringExtra("melih"));
                            Toast.makeText(getApplication(), "masdad", Toast.LENGTH_SHORT).show();
                            Button button = (Button) findViewById(R.id.kitchenTextView);
                            SharedPreferences mPrefs = getSharedPreferences("rooms", MODE_PRIVATE);
                            SharedPreferences.Editor ed = mPrefs.edit();
                            button.setText(jsonObject.getString("key"));
                            ed.putString("room1", jsonObject.getString("key"));
                            ed.apply();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        break;
                    }
                    /*if (action.equals("android.net.conn.CONNECTIVITY_CHANGE")) {
                        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(context.CONNECTIVITY_SERVICE);
                        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
                        //String extra = networkInfo.getExtraInfo();
                        //System.out.println(extra);
                        //System.out.println(networkInfo.getType());
                        String netType = "";
                        if (networkInfo == null) {
                            netType = "";
                        } else {
                            netType = networkInfo.getTypeName();
                        }
                        Toast.makeText(getApplication(), netType, Toast.LENGTH_SHORT).show();
                        break;
                    }*/
            }
        }
    };

    public void registerBroadcastReceiver() {
        IntentFilter myIntentFilter = new IntentFilter();
        myIntentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(myBroadcastReceiver, myIntentFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(myBroadcastReceiver);
    }
}

