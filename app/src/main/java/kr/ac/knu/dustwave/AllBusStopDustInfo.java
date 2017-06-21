package kr.ac.knu.dustwave;

import android.os.AsyncTask;

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

                // 데이터 도착했다고 콜백 메서드 날려줍니다.
                allDustInfoRequestListener.onAllBusStopDustInfoDataStringArrived(requestResult);

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
     - Json object 를 인자로 받아서 UI에 표시하는 함수
     - 이름 : updateCurrentLocationDustDataView()
     - 인자는 현재 위치에 대한 미세먼지 값 담고 있는 json Object
     => 이것은 MainAcitvity 에 있어야 한다.

     */
    public void updateCurrentLocationDustDataView(JSONObject currentLocationDustInfo) {


    }


    /* =====================================================================
     필요한 메서드










     -------------------------------

     -> request 2 에 대해서

         - request 요청하는 함수
            - 이름 : requestAllRecentDustData()
            - 모든 버스 정보를 요청하기만 함 HTTP 통신을 통해 AsyncTask 로 요청.
            - 콜백 메서드를 안에서 포함하겠군
            - 콜백 메서드 안에서 DATA 가 날라오면 string 으로 오는 모든 정류장 미세먼지 정보를 -> jason object  array 로 바꾸는 메서드 필요하네

         - string 의 모든 정류장 미세먼지 정보를 -> jason object array 로 바꿔주는 메서드
            - 이름 : convertAllRecentDustDataToJasonArray
            - 인자를 String 으로 받는다
            - 내부에서 Jason object로 바꿔서 array로 만든다음
            - return 을 Jason array 형식으로 한다

        - json object array 로 입력된 모든 정류장 미세먼지 정보를 -> Local DB Dust info table 에 저장하는 메서드
            - 이름 : saveAllRecentDustDataToLocalDB
            - Json Array 를 인자로 받는다
            - 반복문을 돌려서 object 하나씩 뽑은 다음 object 에서 원소 하나씩 뽑은 다음 기존 데이터 밀고 새로 집어넣는다 <= 우선은 이렇게 합시다:)
            -> 이거 호출한 다음에는 바로 view 갱신하는 메서드 호출해줘야겠네


        - Local DB 에서 모든 정류장 미세먼지 정보를 버스 stop 정보와 join 해서 Json object array 형태로 반환하는 메서드


        - json object array 를 인풋으로 받아서 지도에 띄워주는 메서드? ㅇㅇ 여기에서 map 을 인자로 받아서 .
            - 이름 : addAllDustInfoMarkerOnMap()
            - 인자 : 모든 버스 stop 미세먼지 정보를 담고 있는 jsonArray()
            -




     */
}
