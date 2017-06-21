package kr.ac.knu.dustwave;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

/**
 * Created by nicewoong on 2017. 6. 21..
 * 서버에서 받아온 미세먼지정보를 변환하고 처리하고 Local database 의 DustInfo Table 에 접근해서 입출력을 총 담당하는 클래스
 */

public class CurrentLocationDustInfo {


//    "http://rose.teemo.io/LOCATION?LON=35.8714354&LAT=128.601445";
    public static String REQUEST_URL_LOCATION_DUST_INFO_BASE = "http://rose.teemo.io/LOCATION?";

    public static final String LOG_TAG = "CurrentLocationDustInfo";


    //Async Task HTTP 통신
    public static URL requestUrl;
    public static String requestResult;
    public static String httpCookieData;



    /**
     -> request1 에 대해서
     - request1 요청하는 함수
     - 이름 : requestCurrentDataByLocation()
     - 현재 위도 경도를 변수에 담아서 HTTP AsyncTask 로 요청
     - 요청 도착하면 JSONobject String으로 날라올꺼잖아
     */
    public void requestCurrentDataByLocation(final CurrentLocationDustInfoHttpRequestListener dustInfoHttpRequestListener,
                                             final double latitude,
                                             final double longitude) {

        new AsyncTask<Void,Void,Void>(){

            //작업 준비
            @Override
            protected void onPreExecute() {
                super.onPreExecute();

            }

            //작업 진행
            @Override
            protected Void doInBackground(Void... voids) {
                try{
                    Log.d(LOG_TAG, "현재 위치에 해당하는 미세먼지 정보를 http 요청합니다. ");
                    requestUrl = new URL(REQUEST_URL_LOCATION_DUST_INFO_BASE + "LON=" + longitude + "&LAT=" + latitude);  // URL화 한다.
                    HttpURLConnection conn = (HttpURLConnection) requestUrl.openConnection(); // URL을 연결한 객체 생성.
                    conn.setRequestMethod("GET"); // get 방식 통신
                    conn.setDoInput(true);        // 읽기모드 지정
                    conn.setUseCaches(false);     // 캐싱데이터를 받을지 안받을지
                    conn.setDefaultUseCaches(false); // 캐싱데이터 디폴트 값 설정

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

            //작업 끝 마무리
            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                // 데이터 도착했다고 콜백 메서드 날려줍니다.
                dustInfoHttpRequestListener.onCurrentLocationDataStringArrived(requestResult);

            }

        }.execute();

    }// end of


    /**
     - 현재 위치에 맞는 먼지 데이터를 담고 있는 string 결과를 ->  json object로 변경해서 리턴해주는 함수
     - 이름 : convertCurrentLocationDustDataToJsonObject()
     - 인자는 string
     - return Json object 형식 변환해서 .

     * @param jsonDataStream
     * @return null 이 반환될 수 있음 !
     */
    public JSONObject convertCurrentLocationDustDataToObject(String jsonDataStream) {

        if(jsonDataStream == null )
            return null;

        JSONObject currentLocationDustInfo = null;

        try {
            currentLocationDustInfo = new JSONObject(jsonDataStream);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return currentLocationDustInfo; // null 이 반환될 수 있음 !
    }






}
