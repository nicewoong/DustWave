package kr.ac.knu.dustwave;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    public Button mapButton;
//    public static String REQUEST_URL_ADDRESS = "http://www.naver.com"; //된다.
//    public static String REQUEST_URL_ADDRESS = "http://rose.teemo.io/"; // 안 된다 .
//    public static String REQUEST_URL_ADDRESS = "https://raw.github.com/square/okhttp/master/README.md"; //된다 .
    public static String REQUEST_URL_ADDRESS = "http://rose.teemo.io/dataall";
    public String requestResult;
    public String httpCookieData;
    public URL requestUrl;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setViewAction(); // button 등의 view 들을 구성합니다.


        requestServerData();

    }


    /**
     * Button 등의 view 들을 구성합니다.
     *
     */
    public void setViewAction() {
        mapButton = (Button)findViewById(R.id.map_button);
        mapButton.setOnClickListener(this);

    }


    public void requestServerData() {

        new AsyncTask<Void,Void,Void>(){
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
//                REQUEST_URL_ADDRESS = "rose.teemo.io"; //탐색하고 싶은 URL이다. <= static 으로 set 해놓을께용
            }

            @Override
            protected Void doInBackground(Void... voids) {
                try{
                    requestUrl = new URL(REQUEST_URL_ADDRESS);  // URL화 한다.
                    HttpURLConnection conn = (HttpURLConnection) requestUrl.openConnection(); // URL을 연결한 객체 생성.
                    conn.setRequestMethod("GET"); // get 방식 통신
//                    conn.setDoOutput(true);       // 쓰기모드 지정 <= 안 쓸거면 지정하지마 안돼 ㅠㅠ
                    conn.setDoInput(true);        // 읽기모드 지정
                    conn.setUseCaches(false);     // 캐싱데이터를 받을지 안받을지
                    conn.setDefaultUseCaches(false); // 캐싱데이터 디폴트 값 설정
//                    conn.setRequestProperty("Content-Type","application/json");

                    httpCookieData = conn.getHeaderField("Set-Cookie"); //쿠키데이터 보관

                    InputStream is = conn.getInputStream();        //input스트림 개방

                    StringBuilder builder = new StringBuilder();   //문자열을 담기 위한 객체
                    BufferedReader reader = new BufferedReader(new InputStreamReader(is,"UTF-8"));  //문자열 셋 세팅
                    String line;

                    while ((line = reader.readLine()) != null) {
                        builder.append(line+ "\n");
                    }

                    requestResult = builder.toString();

                }catch(MalformedURLException | ProtocolException exception) {
                    exception.printStackTrace();
                }catch(IOException io){
                    io.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);

                if(requestResult!=null)
                    Log.d("HTTP URL REQUEST RESULT",requestResult);
                else
                    Log.d("HTTP URL REQUEST RESULT", "NULL이네요. 아무것도 안 받아와요. ");
            }
        }.execute();
    }


    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.map_button :

                //Open Map Activity
                Intent intent = new Intent(this, DMapActivity.class);
                startActivity(intent);
                Log.d(this.getLocalClassName(), "map_button clicked ! ");


                break;



            default:

                break;
        }
    }
}
