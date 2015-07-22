package smarthouse.smartronic2;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.wifi.WifiManager;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

public class Methods {

    int responseCode = 0;
    JSONObject identityToken;
    String filetoWrite = "response.txt";
    Context context;

    public Methods(Context context) {
        this.context = context;
    }

    Database database = new Database(this.context);

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
                                       int headers, String type, Context context, int action) {

        String line;
        String text = "";
        switch (headers) {
            case 1:
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

            case 2:
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

            case 3:
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
        SharedPreferences sharedPreferences = context.getSharedPreferences(name, Context.MODE_PRIVATE);
        return sharedPreferences.getString(key, null);
    }

    public void createSPreferences(String name, Context context) {

        SharedPreferences sharedpreferences = context.getSharedPreferences(name, Context.MODE_PRIVATE);
        sharedpreferences.edit();
    }

    public void updateSPreferences(String key, String value, String name, Context context) {
        SharedPreferences sharedpreferences = context.getSharedPreferences(name, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public String getMacAddress(Context context) {
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        String macAddress = wifiManager.getConnectionInfo().getMacAddress();
        if (macAddress == null) {
            macAddress = "Device don't have mac address or wi-fi is disabled";
        }
        return macAddress;
    }

    public boolean changeLightStatus(int id, String connectionType, int operation) {
        String postUrl = "";
        if (connectionType.equals("mobile")) {

        } else if (connectionType.equals("WIFI")) {
            postUrl = "http://ip_address:3480/data_request?id=action&output_format=xml&"
                    + "DeviceNum=" + id + "serviceId=" + context.getString(R.string.switch_power) +
                    "&action=SetTarget&newTargetValue=" + operation;
        } else {
            System.exit(0);
        }
        handleURLConnections(postUrl, null, 1, "POST", context, 0);
        return responseCode == 200;
    }

    public boolean setDimmableLight(int id, String connectionType, int level) {
        String postUrl = "";
        if (connectionType.equals("mobile")) {

        } else if (connectionType.equals("WIFI")) {
            postUrl = "http://ip_address:3480/data_request?id=action&output_format=xml&"
                    + "DeviceNum=" + id + "serviceId=" + context.getString(R.string.dimming_service) +
                    "&action=SetLoadLevelTarget&newLoadlevelTarget=" + level;
        } else {
            System.exit(0);
        }
        handleURLConnections(postUrl, null, 1, "POST", context, 0);
        return responseCode == 200;
    }

    /*public boolean runTheScene(int sceneNum, String connectionType) {
        String postUrl = "";
        if (connectionType.equals("mobile")) {

        } else if (connectionType.equals("WIFI")) {
            postUrl = "http://ip_address:3480/data_request?id=action&" + "serviceId="
                    + context.getString(R.string.run_scene_service) +
                    "&action=RunScene&SceneNum=" + sceneNum;
        } else {
            System.exit(0);
        }
        handleURLConnections(postUrl, null, 1, "POST", context, 0);
        return responseCode == 200;
    }*/

    public boolean turnOffAllLights(String connectionType, int value) {
        String postUrl = "";
        if (connectionType.equals("mobile")) {

        } else if (connectionType.equals("WIFI")) {
            postUrl = "http://ip_address:3480/data_request?id=action&output_format=xml&"
                    + "Category=999&serviceId=" + context.getString(R.string.switch_power) +
                    "&action=SetTarget&newTargetValue=" + value;
        } else {
            System.exit(0);
        }
        handleURLConnections(postUrl, null, 1, "POST", context, 0);
        return responseCode == 200;
    }

    public boolean armAllSensors(String connectionType) {
        String postUrl = "";
        if (connectionType.equals("mobile")) {

        } else if (connectionType.equals("WIFI")) {
            postUrl = "http://ip_address:3480/data_request?id=action&output_format=xml&"
                    + "Category=4&serviceId=" + context.getString(R.string.arm_sensor) +
                    "&action=SetArmed&newArmedValue=1";
        } else {
            System.exit(0);
        }
        handleURLConnections(postUrl, null, 1, "POST", context, 0);
        return responseCode == 200;
    }

    public boolean variableSet(String connectionType, int value, int deviceNum, String service) {
        String postUrl = "";
        if (connectionType.equals("mobile")) {

        } else if (connectionType.equals("WIFI")) {
            postUrl = "http://ip_address:3480/data_request?id=variableset&DeviceNum="
                    + deviceNum + "&serviceId=" + service + "&Variable=Status&Value=" + value;
        } else {
            System.exit(0);
        }
        handleURLConnections(postUrl, null, 1, "POST", context, 0);
        return responseCode == 200;
    }

    public String variableGet(String connectionType, int deviceNum, String service) {
        String postUrl = "";
        if (connectionType.equals("mobile")) {

        } else if (connectionType.equals("WIFI")) {
            postUrl = "http://ip_address:3480/data_request?id=variableget&DeviceNum="
                    + deviceNum + "&serviceId=" + service + "&Variable=Status";
        } else {
            System.exit(0);
        }
        return handleURLConnections(postUrl, null, 1, "POST", context, 0);
    }

    public String findTheDevice(String connectionType, int deviceId) {
        String postUrl = "";
        if (connectionType.equals("mobile")) {

        } else if (connectionType.equals("WIFI")) {
            postUrl = "http://ip_address:3480/data_request?id=finddevice&devid=" + deviceId;
        } else {
            System.exit(0);
        }
        return handleURLConnections(postUrl, null, 1, "POST", context, 0);
    }

    public String jobStatus(String connectionType, int jobId) {
        String postUrl = "";
        if (connectionType.equals("mobile")) {

        } else if (connectionType.equals("WIFI")) {
            postUrl = "http://ip_address:3480/data_request?id=jobstatus&job=" + jobId;
        } else {
            System.exit(0);
        }
        return handleURLConnections(postUrl, null, 1, "POST", context, 0);
    }

    public boolean sensorStatus(String connectionType, int armedValue, int deviceNum) {
        String postUrl = "";
        if (connectionType.equals("mobile")) {

        } else if (connectionType.equals("WIFI")) {
            postUrl = "http://ip_address:3480/data_request?id=action&output_format=xml&"
                    + "DeviceNum=" + deviceNum + "serviceId=" + context.getString(R.string.arm_sensor) +
                    "&action=SetTarget&newTargetValue=" + armedValue;
        } else {
            System.exit(0);
        }
        handleURLConnections(postUrl, null, 1, "POST", context, 0);
        return responseCode == 200;
    }

    // This section is all about parameters that are passed to vera gateway in order to change
    // cultivation system settings.

    public boolean setModeHvac(String connectionType, String mode, int deviceNum) {
        String postUrl = "";
        if (connectionType.equals("mobile")) {

        } else if (connectionType.equals("WIFI")) {
            postUrl = "http://ip_address:3480/data_request?id=lu_action&"
                    + "DeviceNum=" + deviceNum + "serviceId=" + context.getString(R.string.hvac_fan_operating) +
                    "&action=SetMode&NewMode=" + mode;
        } else {
            System.exit(0);
        }
        handleURLConnections(postUrl, null, 1, "POST", context, 0);
        return responseCode == 200;
    }

    public boolean setModeTargetHvac(String connectionType, String newModeTarget, int deviceNum) {
        String postUrl = "";
        if (connectionType.equals("mobile")) {

        } else if (connectionType.equals("WIFI")) {
            postUrl = "http://ip_address:3480/data_request?id=lu_action&" + "DeviceNum=" + deviceNum +
                    "serviceId=" + context.getString(R.string.hvac_user_operating) + "&action=SetModeTarget&newModeTarget=" + newModeTarget;
        } else {
            System.exit(0);
        }
        handleURLConnections(postUrl, null, 1, "POST", context, 0);
        return responseCode == 200;
    }

    public boolean setCurrentSetPointHvacCool(String connectionType, int newCurrentSetpoint, int deviceNum) {
        String postUrl = "";
        if (connectionType.equals("mobile")) {

        } else if (connectionType.equals("WIFI")) {
            postUrl = "http://ip_address:3480/data_request?id=lu_action&" + "DeviceNum="
                    + deviceNum + "serviceId=" + context.getString(R.string.hvac_temp_set_point_cool)
                    + "&action=SetCurrentSetpoint&newCurrentSetpoint=" + newCurrentSetpoint;
        } else {
            System.exit(0);
        }
        handleURLConnections(postUrl, null, 1, "POST", context, 0);
        return responseCode == 200;
    }

    public boolean setCurrentSetPointHvacHeat(String connectionType, int newCurrentSetPoint, int deviceNum) {
        String postUrl = "";
        if (connectionType.equals("mobile")) {

        } else if (connectionType.equals("WIFI")) {
            postUrl = "http://ip_address:3480/data_request?id=lu_action&" + "DeviceNum=" +
                    deviceNum + "serviceId=" + context.getString(R.string.hvac_temp_set_point_heat) +
                    "&action=SetCurrentSetpoint&newTargetValue=" + newCurrentSetPoint;
        } else {
            System.exit(0);
        }
        handleURLConnections(postUrl, null, 1, "POST", context, 0);
        return responseCode == 200;
    }

    // Door lock or unlock. That is the real problem.

    public boolean doorLock(String connectionType, int newTargetValue, int deviceNum) {
        String postUrl = "";
        if (connectionType.equals("mobile")) {

        } else if (connectionType.equals("WIFI")) {
            postUrl = "http://ip_address:3480/data_request?id=lu_action&" + "DeviceNum=" +
                    deviceNum + "serviceId=" + context.getString(R.string.door_lock_service) +
                    "&action=SetTarget&newTargetValue=" + newTargetValue;
        } else {
            System.exit(0);
        }
        handleURLConnections(postUrl, null, 1, "POST", context, 0);
        return responseCode == 200;
    }


}