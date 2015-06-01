package smarthouse.smartronic2;

/**
 * Created by burak on 27/05/15.
 */
public class Methods {

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

    public void theGatewaysAssignedToAccount (String url) {

    }


}
