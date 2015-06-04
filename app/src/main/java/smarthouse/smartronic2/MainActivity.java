package smarthouse.smartronic2;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.Layout;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

public class MainActivity extends ActionBarActivity {

    EditText ed1;
    EditText ed2;
    Button loginButton;
    LinkedHashMap<String, String> URLMap = new LinkedHashMap<>();
    String username, password;
    String salt = "oZ7QE6LcLJp6fiWzdqZc";
    String errorFormat = "";
    Boolean error = false;
    String selectedOption;
    Context context;
    Boolean isstoped=false;
    private Handler handler;

    //String[] Logout = {"Yes", "No"};
    //String[] Home = {};

    //private static String URL = "https://vera-us-oem-autha.mios.com/autha/auth/username/";

    //JSONObject json = new JSONObject();
    //JSONObject json2 = new JSONObject();

    // Followed URL's will be used when posting data to mios vera eben var m??

    private static String URLLogin = "https://vera-us-oem-autha.mios.com/autha/auth/username/";
    private static String getTheDevices = "https://vera-us-oem-authd.mios.com/locator/locator/";
    private static String requestToGateway = "https://vera-us-oem-device11.mios.com/device/device/device/";

    public static String CommandURL = "http://ragip.info/Controller/index_melih.php";

    /*private static final String URLRegister = "http://example_server/autha/auth/username";
    private static final String TokenUrl = "http://example_server/info/session/token";*/

    // "+ x + ? will be appended to URLLogin string."

    // TokenUrl will be used to check if identity tokens are still valid.

    /*JSONObject json = new JSONObject();
    JSONObject json2 = new JSONObject();*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        URLMap.put("1", URLLogin);
        URLMap.put("2", getTheDevices);
        URLMap.put("3", requestToGateway);

        loginButton = (Button) findViewById(R.id.LoginButton);
        ed1 = (EditText) findViewById(R.id.UsernameEditText);
        ed2 = (EditText) findViewById(R.id.PasswordEditText);


        loginButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                username = ed1.getText().toString();
                password = ed2.getText().toString();
                System.out.println(username);

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

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            new Polling(getApplicationContext()).execute();
        }

        @Override
        protected Void doInBackground(Void... params) {

            //String response = "";

            LinkedHashMap<String, String> postData = new LinkedHashMap<>();

            try {
                // SHA-1 Hashed Password to go to vera
                String hashedPassword = hashThePassword(password);

                // encoded username will be posted to mios vera
                String encodedUsername = URLEncoder.encode(username, "utf-8");

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

                String pk_account = handleURLConnections(URLMap.get("1"), postData);
                processTheData("1", pk_account);

                postData.clear();

                postData.put("PK_Account", pk_account);
                String devices = handleURLConnections(URLMap.get("2"), postData);
                String PK_Device = processTheData("2", devices);

                postData.clear();


                //writer.write(getPostDataString(postData));
                //writer.flush();

                //String line;
                //String text = "";

                //BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));

                /*while ((line = reader.readLine()) != null) {
                    text += line + "/n";
                    //printResponse(text);
                    getEncodedStringAndDecode(text);
                }
                writer.close();
                reader.close();*/

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


        /*public void writeToFile(String line) throws IOException {

            try {
                OutputStreamWriter outputStreamWriter = new OutputStreamWriter(openFileOutput("anan.txt", Context.MODE_PRIVATE));
                outputStreamWriter.write(line);
                outputStreamWriter.close();
            } catch (IOException e) {
                Log.e("Exception", "File write failed: " + e.toString());
            }

            String ret = "";

            try {
                InputStream inputStream = openFileInput("config.txt");

                if (inputStream != null) {
                    InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                    BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                    String receiveString = "";
                    StringBuilder stringBuilder = new StringBuilder();

                    while ((receiveString = bufferedReader.readLine()) != null) {
                        stringBuilder.append(receiveString);
                    }

                    inputStream.close();
                    ret = stringBuilder.toString();
                    System.out.println(ret);
                }
            } catch (FileNotFoundException e) {
                Log.e("login activity", "File not found: " + e.toString());
            } catch (IOException e) {
                Log.e("login activity", "Can not read file: " + e.toString());
            }

        }

        public void writeToFileWithSharedPreferences(String line) {
            SharedPreferences sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedpreferences.edit();
            editor.putString("key", line);
            editor.commit();

            SharedPreferences settings = getSharedPreferences(MyPREFERENCES, 0);
            String preferencesString = settings.getString("key", null);
            System.out.println(preferencesString);

        }*/

        public String handleURLConnections(String inputUrl, LinkedHashMap linkedHashMap) {

            String line;
            String text = "";

            try {

                URL url = new URL(inputUrl);

                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setDoOutput(true);

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
                        //System.out.println(jsonArray.get("PK_Account"));
                        //System.out.println(jsonArray);
                        String PK_Account = (String) jsonArray.get("PK_Account");
                        return PK_Account;
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
                case "2":
                    try {
                        String PK_Device = selectGateway(text);
                        return PK_Device;
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
                case "3":
                    break;
                case "4":
                    break;
                case "5":
                    break;
                case "6":
                    break;
                case "7":
                    break;
                case "8":
                    break;
                case "9":
                    break;
                case "10":
                    break;
                case "11":
                    break;
                case "12":
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

        public void getAboutDevicesAndPkDevice(String PK_Account) {

        }

        private String selectGateway(String json) throws JSONException {

            listView = (ListView) findViewById(R.id.gatewaysListView);
            JSONObject jsonObject = new JSONObject(json);
            JSONArray jsonArray = jsonObject.getJSONArray("Devices");
            String[] PK_Devices = new String[0];
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject1 = (JSONObject) jsonArray.get(i);
                String pk_device = (String) jsonObject1.get("PK_Device");
                PK_Devices[i] = pk_device;
            }

            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getBaseContext(), R.layout.gateways, R.id.textView, PK_Devices);
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
                }
            });
            return itemValue;
        }
    }

    @Override
    protected void onDestroy() {
        this.isstoped=true;
        super.onDestroy();

    }

}