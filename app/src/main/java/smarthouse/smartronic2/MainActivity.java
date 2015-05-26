package smarthouse.smartronic2;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;


public class MainActivity extends ActionBarActivity {

    EditText ed1;
    EditText ed2;
    Button loginButton;
    HashMap<String, String> user = new HashMap<>();
    String username, password;
    String salt = "oZ7QE6LcLJp6fiWzdqZc";
    String errorFormat = "";
    Boolean error = false;
    String selectedOption;
    //String[] Logout = {"Yes", "No"};
    //String[] Home = {};

    private static final String URL = "http://ragip.info/Controller/index.php";

    JSONObject json = new JSONObject();
    JSONObject json2 = new JSONObject();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        loginButton = (Button) findViewById(R.id.LoginButton);
        ed1 = (EditText) findViewById(R.id.UsernameEditText);
        ed2 = (EditText) findViewById(R.id.PasswordEditText);

        /*try {
            json.put("PK_Device", 1234);
            json.put("MacAddress", "00:26:b8:71:ac:5e");
            json.put("PK_DeviceType", 1);
            json.put("PK_DeviceSubType", 2);
            json.put("Server_Device", "us-device11.mios.com");
            json.put("Server_Event", "us-event11.mios.com");
            json.put("Server_Account", "us-account11.mios.com");
            json.put("InternalIP", "192.168.8.60");
            json.put("LastAliveReported", "date time format");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            json2.accumulate("Devices", String.valueOf(json));
        } catch (JSONException e) {
            e.printStackTrace();
        }*/

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

        @Override
        protected Void doInBackground(Void... params) {

            //String response = "";

            HashMap<String, String> postData = new HashMap<>();

            try {
                URL url = new URL(URL);

                URLConnection conn = url.openConnection();
                conn.setDoOutput(true);

                OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream());

                // SHA-1 Hashed Password to go to vera
                String hashedPassword = hashThePassword(password);

                // encoded username will be posted to mios vera
                String encodedUsername = URLEncoder.encode(username, "utf-8");

                System.out.println(hashedPassword);
                System.out.println(encodedUsername);

                postData.put("username", encodedUsername);
                postData.put("Password", hashedPassword);
                //postData.put("PK_Oem", "");
                //postData.put("AppKey", "");


                //writer.write(getPostDataString(postData));
                //System.out.println(json.toString());
                writer.write("json2=" + json2.toString());
                writer.flush();

                String line;
                String text = "";

                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));

                while ((line = reader.readLine()) != null) {
                    text += line + "/n";
                    printResponse(text);
                }
                writer.close();
                reader.close();

            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        private String getPostDataString(HashMap<String, String> params) throws UnsupportedEncodingException {
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
                System.out.println(result);
            }

            return result.toString();
        }

        public void printResponse(String line) {
            try {
                JSONObject jsonArray = new JSONObject(line);
                System.out.println(jsonArray.get("key"));
                System.out.println(jsonArray);
            } catch (JSONException e) {
                e.printStackTrace();
            }

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


    }

}
