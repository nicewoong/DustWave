package kr.ac.knu.dustwave;

import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
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
 */

public class AllBusStopDustInfo {

    public static String REQUEST_URL_ALL_DUST_INFO = "http://rose.teemo.io/dataall";

    public static final String LOG_TAG = "AllBusStopDustInfo";

    //Async Task HTTP 통신
    public static URL requestUrl;
    public static String requestResult;
    public static String httpCookieData;

    //Context
    public Context context;



    public AllBusStopDustInfo(Context context) {
        this.context  = context;
    }



    /**
     - request 요청하는 함수
     - 이름 : requestAllRecentDustData()
     - 모든 버스 정보를 요청하기만 함 HTTP 통신을 통해 AsyncTask 로 요청.
     - 콜백 메서드를 안에서 포함하겠군
     - 콜백 메서드 안에서 DATA 가 날라오면 string 으로 오는 모든 정류장 미세먼지 정보를 -> jason object  array 로 바꾸는 메서드 필요하네

     */
    public void requestAllData(final AllBusStopDustInfoHttpRequestListener allDustInfoRequestListener) {

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
                    Log.d(LOG_TAG, "모든 버스 정류장의 최신 미세먼지 정보를 http 요청합니다. ");
                    requestUrl = new URL(REQUEST_URL_ALL_DUST_INFO);  // URL화 한다.
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
                Log.d(LOG_TAG, "모든 버스정류장의 미세먼지 정보 http 요청 결과가 도착했습니다. ");
                // 데이터 도착했다고 콜백 메서드 날려줍니다.
                allDustInfoRequestListener.onAllBusStopDustInfoDataStringArrived(requestResult);

            }

        }.execute();

    }// end of


    /**
     - string 의 모든 정류장 미세먼지 정보를 -> jason object array 로 바꿔주는 메서드
     - 이름 : convertAllRecentDustDataToJasonArray
     - 인자를 String 으로 받는다
     - 내부에서 Jason object로 바꿔서 array로 만든다음
     - return 을 Jason array 형식으로 한다


     * @param jsonDataStream
     * @return null 이 반환될 수 있음 !
     */
    public JSONArray convertAllRecentDustDataToJasonArray(String jsonDataStream) {

        if(jsonDataStream == null )
            return null;

        JSONArray allDustDataList = null;

        try {
            allDustDataList = new JSONArray(jsonDataStream);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return allDustDataList; // null 이 반환될 수 있음 !
    }




    /**
     * 새로 받아온 전체 정류장 먼지 정보들을 Local DB에 갱신해서 저장합니다
     *
     * @param allBusStopInfoList
     */
    public void updateAllBusStopDustInfoToLocal(JSONArray allBusStopInfoList) {

        //전체 테이블 새로 갱신할 거니까 그냥 기존의 것 삭제하고
        clearAllBusDustInfoTable();

        //전부 새로 insert
        for(int i=0 ; i<allBusStopInfoList.length(); i++) {
            try {
                insertBusStopDustInfoToLocal(allBusStopInfoList.getJSONObject(i));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * 모든 정류장의 먼지정보 테이블 전체 데이터를 삭제합니다.
     *
     */
    public void clearAllBusDustInfoTable() {
        LocalDatabase mDatabase = LocalDatabase.getInstance(context);

        try {
            mDatabase.execSQL("DELETE FROM " + LocalDatabase.TABLE_DUST_INFO);

        }catch(Exception e){
            Log.e(LOG_TAG, e.getMessage());
        }
    }


    /**
     *
     * @return
     */
    public boolean insertBusStopDustInfoToLocal(JSONObject busStopDustInfoObject) throws JSONException {

        LocalDatabase mDatabase = LocalDatabase.getInstance(context);

        if (mDatabase.writableOpen()) {
            String SQL = "INSERT INTO " + LocalDatabase.TABLE_DUST_INFO +
                    " (" +
                    LocalDatabaseKey.bus_stop_id + ", " +
                    LocalDatabaseKey.dust_info_pm10 + ", " +
                    LocalDatabaseKey.dust_info_pm25 +
                    " ) " +

                    "VALUES (" +
                    busStopDustInfoObject.getString( LocalDatabaseKey.bus_stop_id) + ", " + //Number
                    busStopDustInfoObject.getString( LocalDatabaseKey.dust_info_pm10) + ", " + //Number
                    busStopDustInfoObject.getString( LocalDatabaseKey.dust_info_pm25) +
                    " )";

            return mDatabase.execSQL(SQL);
        } else {
            Log.e(LOG_TAG,"데이터베이스 insert 에러");
            return false;
        }


    }


    /* =====================================================================
     필요한 메서드

        - Local DB 에서 모든 정류장 미세먼지 정보를 버스 stop 정보와 join 해서 Json object array 형태로 반환하는 메서드

     */
}
