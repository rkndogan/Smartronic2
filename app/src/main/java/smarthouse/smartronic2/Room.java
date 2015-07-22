package smarthouse.smartronic2;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.ContextThemeWrapper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

public class Room extends Activity {

    String dataURL = "/data_request?id=lu_sdata&loadtime=0&dataversion=0";

    HashMap<String, Integer> user = new HashMap<>();

    //public SharedPreferences mPrefs;
    int say = 1;
    int generatedButtonId = 0;
    MainActivity ma = new MainActivity();
    Context context;
    Database database;
    Methods methods;
    Button saloonButton, bedRoomButton, kidRoomButton;
    int numberOfRooms = 0;
    ArrayList<Button> roomArrayList = new ArrayList<>();
    ArrayList<Button> deviceArrayList = new ArrayList<>();
    ArrayList plugNamesArray = new ArrayList();
    ArrayList heaterNamesArray = new ArrayList();
    ArrayList lampNamesArray = new ArrayList();

    //finish();
    //startActivity(getIntent()); THIS CODE REFRESHES THE ACTIVITY



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room);

        database = new Database(getApplicationContext());
        methods = new Methods(getApplicationContext());

        SharedPreferences mPrefs = getSharedPreferences("rooms", MODE_PRIVATE);
        Button button = (Button) findViewById(R.id.kitchenTextView);
        button.setText(mPrefs.getString("room1", ""));
        button.setBackgroundResource(R.drawable.circle);
        registerBroadcastReceiver();
        //dynamically update xml
        context = getApplicationContext();

        // Linear Layout

        /*final LinearLayout ly = (LinearLayout) findViewById(R.id.linearLayout);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        params.leftMargin = 50;*/

        numberOfRooms = database.getRowCount(getString(R.string.room_table));

        if (numberOfRooms == 0) {
            AlertDialog.Builder builder = new AlertDialog.Builder(Room.this);
            builder.setMessage(getString(R.string.no_room) + getString(R.string.wish_exit))
                    .setTitle(getString(R.string.exit))
                    .setCancelable(false);
            builder.setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    moveTaskToBack(true);
                    android.os.Process.killProcess(android.os.Process.myPid());
                    System.exit(1);
                }
            });

            builder.setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Toast.makeText(context, getString(R.string.returning), Toast.LENGTH_SHORT).show();
                }
            });
            AlertDialog alertdialog = builder.create();
            alertdialog.show();
        } else {
            for (int i = 0; i < numberOfRooms; i++) {
                // in this place, buttons will be added
                createButton("erfv", R.id.linearLayout, "rferv", true, i);
            }

            // How it will be implemented to my previous algorithm?
            kidRoomButton = (Button) findViewById(R.id.kidRoomTextView);

            String internalIp = ma.InternalIp;
            dataURL = internalIp + dataURL;
            dataURL = "http://" + dataURL;

            buttonHandler(roomArrayList);

        }
    }



    @Override
    protected void onPause() {
        //methods.createSPreferences(getString(R.string.room_status), this.context);
        //methods.updateSPreferences("button1", saloonButton.getText().toString(), getString(R.string.room_status), this.context);
        super.onPause();
        unregisterReceiver(myBroadcastReceiver);
    }

    @Override
    protected void onResume() {
        // sharedPreferences Data will be restored here
        super.onResume();
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

    public void buttonHandler(final ArrayList<Button> arrayList) {

        for (int i = 0; i < arrayList.size(); i++) {
            arrayList.get(i).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // got the room's name from the database
                    String name = database.getRoomText(String.valueOf(v.getId()));

                    // in this section: plug, heater and lamp status for room will be get.
                    database.getDeviceName(v.getId(), 1); // plug
                    Iterator entries = plugNamesArray.listIterator();
                    int j = 0;
                    while (entries.hasNext()) {
                        String plugName = plugNamesArray.get(j).toString();
                        createButton("", R.id.sample, plugName, false, 31);
                        j++;
                    }
                    int k = 0;
                    database.getDeviceName(v.getId(), 2); // heater
                    Iterator entries2 = heaterNamesArray.listIterator();
                    while (entries2.hasNext()) {
                        String plugName = heaterNamesArray.get(k).toString();
                        createButton("", R.id.sample, plugName, false, 31);
                        k++;
                    }

                    int l = 0;
                    database.getDeviceName(v.getId(), 3); // lamp
                    Iterator entries3 = lampNamesArray.listIterator();
                    while (entries3.hasNext()) {
                        String plugName = lampNamesArray.get(l).toString();
                        createButton("", R.id.sample, plugName, false, 31);
                        l++;
                    }
                    //checkViewStubArray(name, v.getId(), v);
                }
            });
        }
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
        View view1 = findViewById(R.id.sample);

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

                Toast.makeText(getApplicationContext(), getString(R.string.already) + key, Toast.LENGTH_SHORT).show();
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

    public void createButton(String key, int linearLayoutId, String text, Boolean room, int index) {
        LinearLayout linearLayout = (LinearLayout) findViewById(linearLayoutId);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        params.leftMargin = 50;
        generatedButtonId = View.generateViewId();
        Button button = new Button(new ContextThemeWrapper(context, R.style.circle_texts), null, 0);
        button.setBackgroundResource(R.drawable.circle);
        button.setId(View.generateViewId());
        if (room) {
            //text = database.getRoomText(String.valueOf(j));
        }
        button.setText(text);
        button.setGravity(17);
        button.setLayoutParams(params);
        linearLayout.addView(button);
        //database.setLayoutId(generatedButtonId, name);
        if (room) {
            roomArrayList.add((Button) findViewById(generatedButtonId));
        } else {
            deviceArrayList.add((Button) findViewById(generatedButtonId));
        }
    }

    private BroadcastReceiver myBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            say++;
            String action;
            action = intent.getAction();

            switch (0) {
                default:
                    if (action.equals("polling_update")) {
                        try {
                            JSONObject jsonObject;
                            jsonObject = new JSONObject(intent.getStringExtra("data"));
                            Toast.makeText(getApplication(), "polling data", Toast.LENGTH_SHORT).show();
                            Button button = (Button) findViewById(R.id.kitchenTextView);
                            SharedPreferences mPrefs = getSharedPreferences("rooms", MODE_PRIVATE);
                            SharedPreferences.Editor ed = mPrefs.edit();
                            button.setText(jsonObject.getString("key"));
                            ed.putString("room1", jsonObject.getString("key"));
                            ed.apply();

                            /*final LinearLayout ly = (LinearLayout) findViewById(R.id.linearLayout);
                            Button btn = new Button (getApplicationContext());
                            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams
                                    (LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
                            btn.setText(jsonObject.getString("key"));
                            btn.setId(R.id.checkbox);
                            btn.setBackgroundResource(R.drawable.circle);
                            btn.setLayoutParams(params);
                            ly.addView(btn);*/
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

    public void getPolllingUpdate(int type, LinkedHashMap linkedHashMap) {
        // this function will get the data from the mios and operate according to it.
    }
}