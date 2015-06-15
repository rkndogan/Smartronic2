package smarthouse.smartronic2;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Handler;

public class MainActivity extends ActionBarActivity {

    EditText ed1;
    EditText ed2;
    Button loginButton;
    TextView txt;
    LinkedHashMap<String, String> URLMap = new LinkedHashMap<>();
    String username, password, encodedUsername;
    String salt = "oZ7QE6LcLJp6fiWzdqZc";
    String errorFormat = "";
    Boolean error = false;
    String selectedOption;
    String MyPREFERENCES = "cachedMemory";
    String InternalIp = "";
    String serverDevice = "";
    String serverRelay = "";
    JSONObject identityToken;
    int responseCode = 0;
    Context context;
    Boolean isstoped = false;
    private Handler handler;

    String URLLogin = "https://vera-us-oem-autha.mios.com/autha/auth/username/";
    String getTheDevices = "https://vera-us-oem-authd.mios.com/locator/locator/";
    String requestToGateway = "https://vera-us-oem-device11.mios.com/device/device/device/";
    String CommandURL = "http://ragip.info/Controller/index_melih.php";
    String usingRelayServer = "/device/device/device/";
    String sessionKeyServer = "/relay/relay/relay/device/";
    String mmsSession = "/info/session/token";
    String verifyURL = "/authd/auth/provision";

    BroadcastReceiver myBroadcastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        registerBroadcastReceiver();
        new CheckConnection(getApplicationContext());
        System.out.println(new ReceiveBroadcastMessage());

        //PK_Device and HWKey will be checked here whether they have been stored or not.
        /*if ((methods.getSPreferences(MyPREFERENCES, "PK_Device", getApplicationContext()) == null ||
                methods.getSPreferences(MyPREFERENCES, "PK_Device", getApplicationContext()) == "") &&
                (methods.getSPreferences(MyPREFERENCES, "HWKey", getApplicationContext()) == null ||
                        methods.getSPreferences(MyPREFERENCES, "PK_Device", getApplicationContext()) == "")) {

            // call request to get PK_Device and HWKey
            // And store them on sharedPreferences

            String response = methods.handleURLConnections("us-authd11.mios.com" + verifyURL, null, "3", "PUT", getApplicationContext());

            String PK_Device = "";
            String HWKey = "";

            try {
                JSONObject jsonArray = new JSONObject(response);
                PK_Device = (String) jsonArray.get("PK_Device");
                HWKey = (String) jsonArray.get("HWKey");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            methods.updateSPreferences("PK_Device", PK_Device, MyPREFERENCES, getApplicationContext());
            methods.updateSPreferences("HWKey", HWKey, MyPREFERENCES, getApplicationContext());
        }*/
        context = getApplicationContext();

        String fontPath = "fonts/Walkway_Oblique_SemiBold.ttf";

        URLMap.put("1", URLLogin);
        URLMap.put("2", getTheDevices);
        URLMap.put("3", requestToGateway);
        URLMap.put("4", CommandURL);
        URLMap.put("5", usingRelayServer);
        URLMap.put("6", sessionKeyServer);
        URLMap.put("7", mmsSession);

        loginButton = (Button) findViewById(R.id.LoginButton);
        ed1 = (EditText) findViewById(R.id.UsernameEditText);
        ed2 = (EditText) findViewById(R.id.PasswordEditText);
        txt = (TextView) findViewById(R.id.forgot_text);

        Typeface tf = Typeface.createFromAsset(getAssets(), fontPath);
        ed1.setTypeface(tf);
        ed2.setTypeface(tf);
        loginButton.setTypeface(tf);
        txt.setTypeface(tf);

        loginButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                cachedMemoryVariables("x", "y", "z", "t", "u", "i", "m");

                username = ed1.getText().toString();
                password = ed2.getText().toString();

                if ((username.matches("") && username == null) || (password.matches("") && password == null)) {
                    errorFormat += "username is wrong";
                    error = true;
                }

                /*if(!Functions.isEmailValid(username)){
                    errorFormat += "Wrong username format\n";
                    error=true;
                }*/

                if (error) {
                    AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
                    alertDialog.setTitle("Error");
                    alertDialog.setMessage(errorFormat);
                    alertDialog.setCancelable(false);
                    alertDialog.setButton(RESULT_OK, "OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            ed2.setText("");
                            errorFormat = "";
                            error = false;
                        }
                    });
                    alertDialog.show();
                } else {
                    new LoginCheck().execute();
                    Intent intnt = new Intent(getApplicationContext(), Index.class);
                    startActivity(intnt);
                    Toast.makeText(getApplicationContext(), "oldu ya la", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        super.onCreateOptionsMenu(menu);
        menu.add(R.string.sign_in);
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        CharSequence signInOption = item.getTitle();
        String signInString = getResources().getString(R.string.sign_in);

        if (item.hasSubMenu()) {
            if (signInOption == signInString) {
                Intent intent = new Intent(getApplicationContext(), SignIn.class);
                startActivity(intent);
            } else {
                Context context = getApplicationContext();
                Toast toast = Toast.makeText(context, "an error occurred!", Toast.LENGTH_SHORT);
                toast.show();
            }
            selectedOption = item.getTitle().toString();
        }
        return true;
    }


    public void ForgotPass(View view) {
        Intent intent = new Intent(MainActivity.this, Forgot.class);
        startActivity(intent);
    }

    class LoginCheck extends AsyncTask<Void, Void, Void> {
        String itemValue;
        ListView listView;

        /*@Override
        protected void onPreExecute() {
            super.onPreExecute();
            ProgressDialog dialog = ProgressDialog.show(MainActivity.this,"Loading,","Please wait..",true,true,null);
        }*/

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            //new Polling(getApplicationContext()).execute();
        }

        @Override
        protected Void doInBackground(Void... params) {

            //String response = "";

            LinkedHashMap<String, String> postData = new LinkedHashMap<>();

            try {
                // SHA-1 Hashed Password to go to vera
                String hashedPassword = hashThePassword(password);

                // encoded username will be posted to mios vera
                encodedUsername = URLEncoder.encode(username, "utf-8");

                URLMap.put("1", URLMap.get("1") + encodedUsername);
                //URLLogin = URLLogin + encodedUsername;
                System.out.println(URLLogin);

                //URL url = new URL(URLLogin);

                //URLConnection conn = url.openConnection();
                //conn.setDoOutput(true);

                //OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream());

                postData.put("SHA1Password", hashedPassword);
                postData.put("PK_Oem", "1");
                postData.put("AppKey", "2");

                String pk_account = handleURLConnections(URLMap.get("1"), postData, "1");
                processTheData("1", pk_account);

                postData.clear();

                postData.put("PK_Account", pk_account);
                String devices = handleURLConnections(URLMap.get("2"), postData, "1");
                String PK_Device = processTheData("2", devices);

                SharedPreferences sharedPreferences = getSharedPreferences(MyPREFERENCES, MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("LAST_PK_Device", PK_Device);
                editor.apply();

                postData.clear();

                if (InternalIp.equals("") || InternalIp == null) {
                    URLMap.put("5", "https://" + serverDevice + URLMap.get("5") + PK_Device);
                    postData.put("", "");
                    String response = handleURLConnections(URLMap.get("5"), postData, "1");
                    if (responseCode == 200) {
                        serverRelay = processTheData("3", response);
                    } else {
                        URLMap.put("4", URLMap.get("4") + PK_Device + "/port_3480");
                    }
                } else {
                    //Connect Locally
                    URLMap.put("4", URLMap.get("4") + PK_Device + "/port_3480");
                }

                postData.clear();

                URLMap.put("7", "https://" + serverRelay + URLMap.get("7"));
                postData.put("", "");
                String response = handleURLConnections(URLMap.get("7"), postData, "2");
                String server_relay_mmsSession = processTheData("4", response);

                postData.clear();

                URLMap.put("6", "https://" + serverRelay + URLMap.get("6") + PK_Device +
                        "/session/" + server_relay_mmsSession + "port_3480/");
                postData.put("id", "sdata");
                String dataResponse = handleURLConnections(URLMap.get("6"), postData, "1");
                String processedDataResponse = processTheData("4", dataResponse);

            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        public String hashThePassword(String password) throws NoSuchAlgorithmException {

            MessageDigest mDigest = MessageDigest.getInstance("SHA1");
            byte[] result = mDigest.digest((password + username + salt).getBytes());
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < result.length; i++) {
                sb.append(Integer.toString((result[i] & 0xff) + 0x100, 16).substring(1));
            }
            return sb.toString();
        }

        public String handleURLConnections(String inputUrl, LinkedHashMap linkedHashMap, String headers) {

            String line;
            String text = "";
            switch (headers) {
                case "1":
                    try {

                        URL url = new URL(inputUrl);

                        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                        conn.setDoOutput(true);
                        conn.connect();
                        int responseCode = conn.getResponseCode();
                        System.out.println(responseCode);
                        setResponseCode(responseCode);

                        OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream());

                        writer.write(postDataString(linkedHashMap));
                        writer.flush();

                        BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));

                        while ((line = reader.readLine()) != null) {
                            text += line + "/n";
                            //printResponse(text);
                            //getEncodedStringAndDecode(text);
                        }
                        writer.close();
                        reader.close();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;

                case "2":
                    try {

                        // this will be used when header is necessary.
                        URL url = new URL(inputUrl);

                        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                        conn.setDoOutput(true);
                        conn.addRequestProperty("MMSAuth", identityToken.getString("MMSAuth"));
                        conn.addRequestProperty("MMSAuthSig", identityToken.getString("MMSAuthSig"));

                        OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream());

                        writer.write(postDataString(linkedHashMap));
                        writer.flush();

                        BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));

                        while ((line = reader.readLine()) != null) {
                            text += line + "/n";
                            //printResponse(text);
                            //getEncodedStringAndDecode(text);
                        }
                        writer.close();
                        reader.close();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

            }
            return text;
        }

        public String processTheData(String type, String text) {

            String result = "";
            switch (type) {
                case "1":
                    try {
                        byte[] decodedBytes = Base64.decode(text, Base64.DEFAULT);
                        String decodedString = new String(decodedBytes);
                        JSONObject jsonArray = new JSONObject(decodedString);
                        setIdentityToken(jsonArray);
                        return (String) jsonArray.get("PK_Account");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
                case "2":
                    try {
                        return selectGateway(text);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
                case "3":
                    try {
                        JSONObject jsonArray = new JSONObject(text);
                        return (String) jsonArray.get("Server_Relay");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
                default:
                    break;
            }
            return result;
        }

        private String postDataString(LinkedHashMap<String, String> params) throws UnsupportedEncodingException {
            StringBuilder result = new StringBuilder();
            boolean first = true;
            for (Map.Entry<String, String> entry : params.entrySet()) {
                if (first)
                    first = false;
                else
                    result.append("&");

                result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
                result.append("=");
                result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
            }
            //System.out.println(result);
            return result.toString();
        }

        private String selectGateway(String json) throws JSONException {

            listView = (ListView) findViewById(R.id.gatewaysListView);
            JSONObject jsonObject = new JSONObject(json);
            JSONArray jsonArray = jsonObject.getJSONArray("Devices");
            String[] PK_Devices = new String[0];
            final String[] server_devices = new String[0];
            final String[] internal_ips = new String[0];
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject1 = (JSONObject) jsonArray.get(i);
                String pk_device = (String) jsonObject1.get("PK_Device");
                String internal_ip = (String) jsonObject1.get("InternalIp");
                String server_device = (String) jsonObject1.get("Server_Device");
                PK_Devices[i] = pk_device;
                internal_ips[i] = internal_ip;
                server_devices[i] = server_device;
            }

            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(getApplicationContext(), R.layout.gateways, R.id.textView, PK_Devices);
            listView.setAdapter(arrayAdapter);

            Dialog di = new Dialog(getApplicationContext());
            di.setContentView(R.layout.gateways);
            di.setCancelable(false);

            di.show();
            listView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int item = v.getVerticalScrollbarPosition();
                    itemValue = (String) listView.getItemAtPosition(item);
                    setInternalIp(internal_ips[item]);
                    setServerDevice(server_devices[item]);
                }
            });
            return itemValue;
        }
    }

    @Override
    protected void onDestroy() {
        this.isstoped = true;
        super.onDestroy();

    }


    public void cachedMemoryVariables(String pk_device, String encodedUsername, String hwkey, String last_ip,
                                      String last_wifi, String last_engine_connect, String last_pk_device) {

        SharedPreferences sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString("Username", encodedUsername);
        editor.putString("PK_Device", pk_device);
        editor.putString("HWKey", hwkey);
        editor.putString("LAST_IP", last_ip);
        editor.putString("LAST_WIFI", last_wifi);
        editor.putString("LAST_ENGINE_CONNECT", last_engine_connect);
        editor.putString("LAST_PK_Device", last_pk_device);
        editor.apply();

    }

    public void setInternalIp(String InternalIp) {
        this.InternalIp = InternalIp;
    }

    public void setServerDevice(String serverDevice) {
        this.serverDevice = serverDevice;
    }

    public void setIdentityToken(JSONObject identityToken) {
        this.identityToken = identityToken;
    }

    public void setResponseCode(int responseCode) {
        this.responseCode = responseCode;
    }

    /*public void setRunChoice(int runChoice) {
        this.runChoice = runChoice;
    }

    public int getRunChoice() {
        return this.runChoice;
    }*/

    public void registerBroadcastReceiver() {
        IntentFilter myIntentFilter = new IntentFilter();
        myIntentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(myBroadcastReceiver, myIntentFilter);
    }
}