package smarthouse.smartronic2;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.NetworkInterface;
import java.net.URL;
import java.net.URLConnection;

import android.os.Handler;
import android.widget.Toast;

/**
 * Created by smartronic on 27.5.2015. adb
 */
public class Polling extends AsyncTask<String, String, String> {

    String response = "";
    String item = "";
    String lu_data = "id=lu_sdata";
    String text = "";
    Context context;
    int LoadTime = 0;
    int DataVersion = 0;
    int DefaultTimeout = 60;
    int DefaultMinimumDelay = 2000;
    int CurrentMinimumDelay = 0;
    int CurrentSleep = 2000;
    int EngineState = -2;
    int NumFailures = 0;
    private Polling poll;
    MainActivity urrr = new MainActivity();

    public Polling(Context c) {
        this.context = c;
    }

    @Override
    protected void onPostExecute(String s) {

        Toast.makeText(context, s, Toast.LENGTH_SHORT).show();
        Intent myintent = new Intent("polling guncellemesi");
        myintent.putExtra("melih", s);
        context.sendBroadcast(myintent);
        System.out.println(urrr.isstoped);
        if (!urrr.isstoped) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {

                    poll = new Polling(context);
                    poll.execute();
                }
            }, 3000);

        }
    }

    @Override
    protected String doInBackground(String... params) {


        try {
            URL url = new URL(urrr.CommandURL);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);


            conn.addRequestProperty("MMS,auth", "Identity");
            conn.addRequestProperty("MMSAutSig", "IdentitySignature");

            OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream());


            //postData.put("PK_Oem", "");
            //postData.put("AppKey", "");


            //writer.write(getPostDataString(postData));
            //System.out.println(json.toString());
            writer.write("naber");
            writer.flush();

            String line;


            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            conn.getResponseCode();
               /* if((line=reader.readLine())== null){
                    EngineState=-2;
                    //not connected durumunu bildirmek gerekir
                    NumFailures=NumFailures+1;
                    if(NumFailures >20){
                        //checkconnection()
                    }
                    //The request failed, so sleep a couple seconds before trying again
                    //Sleep(CurrentSleep)

                    CurrentMinimumDelay=0;
                }*/

            //If we have data
            //Parse(data)


            while ((line = reader.readLine()) != null) {
                CurrentMinimumDelay = DefaultMinimumDelay;
                //Parse data
                System.out.println(line);
                text += line + "/n";

            }
            writer.close();
            reader.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
        try {

            JSONObject jsonObject = new JSONObject(text);
            System.out.println(jsonObject.isNull("key"));
            //JSONArray jsonArray = new JSONArray(text);
            item = jsonObject.getString("key");
            System.out.println(item);


        } catch (JSONException e) {
            e.printStackTrace();
        }

        return text;

    }

}