package smarthouse.smartronic2;

import android.content.Context;
import android.os.AsyncTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import android.os.Handler;
import android.widget.Toast;

/**
 * Created by smartronic on 27.5.2015.
 */
public class Polling extends AsyncTask <String,String,String> {

    String response = "";
    String item = "";
    String lu_data = "id=lu_sdata";
    String text = "";
    Context context;
    private Polling poll;
    MainActivity urrr = new MainActivity();
    public  Polling (Context c){
        this.context=c;
    }

    @Override
    protected void onPostExecute(String s) {

        Toast.makeText(context,s,Toast.LENGTH_SHORT).show();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                poll = new Polling(context);
                poll.execute();
            }
        }, 3000);

    }

    @Override
    protected String doInBackground(String ... params) {


        try {
            URL url = new URL(urrr.CommandURL);

            URLConnection conn = url.openConnection();
            conn.setDoOutput(true);

            OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream());


            //postData.put("PK_Oem", "");
            //postData.put("AppKey", "");


            //writer.write(getPostDataString(postData));
            //System.out.println(json.toString());
            writer.write("naber");
            writer.flush();

            String line;


            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));

            while ((line = reader.readLine()) != null) {
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

        return item;

    }
}