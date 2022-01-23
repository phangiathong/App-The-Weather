package com.academy.me9_appwhatstheweather;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

public class MainActivity extends AppCompatActivity {

    EditText editText;
    TextView txtResult;
    String message="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editText=findViewById(R.id.editText);
        txtResult=findViewById(R.id.txtResult);

    }

    public void getWeather(View view) {

        try {
            DownloadTask task = new DownloadTask();
            String encodedCityName = URLEncoder.encode(editText.getText().toString(), "UTF-8");

            if (!encodedCityName.equals(""))
            {
                task.execute("https://openweathermap.org/data/2.5/weather?q="+encodedCityName+"&appid=439d4b804bc8187953eb36d2a8c26a02");
            }else {
                Toast.makeText(getApplicationContext(),"Could not find weather !",Toast.LENGTH_LONG).show();
            }

            editText.setText("");
        }catch (Exception e){
            e.printStackTrace();
            e.getMessage();
            Toast.makeText(getApplicationContext(),"Could not find weather !",Toast.LENGTH_LONG).show();
        }
    }

    public class DownloadTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... urls) {

            String result="";
            URL url;
            HttpURLConnection urlConnection=null;

            try {
                url=new URL(urls[0]);
                urlConnection=(HttpURLConnection) url.openConnection();
                InputStream in=urlConnection.getInputStream();
                InputStreamReader reader=new InputStreamReader(in);
                int data=reader.read();

                while(data!=-1) {

                    char current=(char)data;
                    result+=current;
                    data=reader.read();
                }

                return result;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            
            if (s!=null){
                try {
                    JSONObject jsonObject=new JSONObject(s);
                    String weatherInfor=jsonObject.getString("weather");
                    Log.i("weather",weatherInfor);

                    JSONArray array=new JSONArray(weatherInfor);

                    String main="";
                    String description="";

                    for (int i=0;i<array.length();i++){

                        JSONObject object=array.getJSONObject(i);
                        main=object.getString("main");
                        description=object.getString("description");

                        if (!main.equals("") && !description.equals("")){
                            message+=main+":"+description+"\r\n";
                        }
                    }

                    if (!message.equals("")){
                        txtResult.setText(message);
                    }else {
                        Toast.makeText(getApplicationContext(), "Could not find weather !", Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Could not find weather !", Toast.LENGTH_SHORT).show();
                }

            }else {// khi nhập bậy bạn cũng toast ra.
                Toast.makeText(getApplicationContext(), "Could not find weather !", Toast.LENGTH_SHORT).show();
            }
        }
    }
}