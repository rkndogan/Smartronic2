package smarthouse.smartronic2;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.LinkedHashMap;
import java.util.Map;

public class Methods {

    int responseCode = 0;
    JSONObject identityToken;

    String filetoWrite = "response.txt";

    /*public void writeToFile(String line) throws IOException {

        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(openFileOutput(filetoWrite, Context.MODE_PRIVATE));
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

    }*/

    public String handleURLConnections(String inputUrl, LinkedHashMap linkedHashMap,
                                       String headers, String type, Context context) {

        String line;
        String text = "";
        switch (headers) {
            case "1":
                try {

                    URL url = new URL(inputUrl);

                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setDoOutput(true);
                    conn.setRequestMethod(type);
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
                    conn.setRequestMethod(type);
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

            case "3":
                try {

                    // this will be used when header is necessary.
                    URL url = new URL(inputUrl);

                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setDoOutput(true);
                    conn.setRequestMethod(type);

                    JSONObject keyArg = new JSONObject();
                    keyArg.put("MacAddress", getMacAddress(context));
                    keyArg.put("PK_DeviceType", "3");
                    keyArg.put("PK_DeviceSubType", "301");
                    keyArg.put("EK_Oem", "0");
                    String putFields = keyArg.toString();

                    OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream());

                    writer.write(putFields);
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


    public void setResponseCode(int responseCode) {
        this.responseCode = responseCode;
    }

    public String getSPreferences(String name, String key, Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(name, context.MODE_PRIVATE);
        System.out.println(sharedPreferences.getString("Username", null));

        return sharedPreferences.getString(key, null);
    }

    public void createSPreferences(String name, Context context) {

        SharedPreferences sharedpreferences = context.getSharedPreferences(name, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
    }

    public void updateSPreferences(String key, String value, String name, Context context) {
        SharedPreferences sharedpreferences = context.getSharedPreferences(name, context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public String getMacAddress(Context context) {
        WifiManager wimanager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        String macAddress = wimanager.getConnectionInfo().getMacAddress();
        if (macAddress == null) {
            macAddress = "Device don't have mac address or wi-fi is disabled";
        }
        return macAddress;
    }
}

