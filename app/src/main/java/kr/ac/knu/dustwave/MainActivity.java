package kr.ac.knu.dustwave;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapReverseGeoCoder;
import net.daum.mf.map.api.MapView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, MapView.MapViewEventListener,  MapView.POIItemEventListener, MapReverseGeoCoder.ReverseGeoCodingResultListener {


    //View
    public Button mapButton;
    public TextView currentLocation; // 현재 위치에 대한 한글 주소 표시

    public static String REQUEST_URL_CURRENT_LOCATION = "http://rose.teemo.io/dataall";
    public String requestResult;
    public String httpCookieData;
    public URL requestUrl;


    //map view 를 위한 variables
    MapView mapView;
    ViewGroup mapViewContainer;
    MapPOIItem marker ; //중심점 마커

    //가장 최신 위도경도 => default 는 대구광역시 중심입니다.
    public static double latestLatitude = 35.8714354;
    public static double latestLongitude = 128.601445;


    // Acquire a reference to the system Location Manager
    LocationManager locationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setViewAction(); // button 등의 view 들을 구성합니다.

        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE); // 위도 경도를 받아올 준비
        addLocationChangedListener(this);
        requestServerData(); // 모든 bus stop data 요청하는  request2  를 여기에 두자.



    }

    /**
     * 가려졌던 View 가 다시 보이게 될 때
     */
    @Override
    protected void onResume() {
        super.onResume();

        createSmallMapView(); // 가려진 뷰가 다시 생성될 때 다음 지도를 생성해서 view 를 채운다.
    }

    /**
     * 뷰가 가려지거나 없어지기 전 가장 기초단계
     */
    @Override
    protected void onPause() {
        super.onPause();
        mapViewContainer.removeAllViews();

    }

    /**
     * Button 등의 view 들을 구성합니다.
     *
     */
    public void setViewAction() {
//        mapButton = (Button) findViewById(R.id.map_button);
//        mapButton.setOnClickListener(this);

        currentLocation = (TextView) findViewById(R.id.textview_current_location);

    }

    /**
     * 다음지도API를 활용해서 맵뷰를 생성해서 activity 를 채운다.
     *
     */
    public void createSmallMapView() {

        mapView = new MapView(this);
        mapView.setDaumMapApiKey(this.getResources().getString(R.string.daum_map_view_api_key)); // 다음 api key 를 인자로 넘겨준다.

        mapViewContainer = (ViewGroup) findViewById(R.id.small_map_view);
        mapViewContainer.addView(mapView);

        //이벤트리스너 등록
        mapView.setMapViewEventListener(this); // this에 MapView.MapViewEventListener 구현.
        mapView.setPOIItemEventListener(this);


    }


    /**
     * Location 이 변경될 때 호출되는 Listener를 등록합니다
     *
     */
    public void addLocationChangedListener(final MapReverseGeoCoder.ReverseGeoCodingResultListener geoCodingResultListener) {
        // Define a listener that responds to location updates

        LocationListener locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                // Called when a new location is found by the network location provider.
                Log.d("LOCATION UPDATED : ", location.toString());
                // TODO: 2017. 6. 20. 위치를 받아왔을 때 갱신할 작업을 여기서 하면 됩니다

                // 최신 위도경도 값 최신화
                latestLatitude = location.getLatitude();
                latestLongitude = location.getLongitude();

                // small map view 의  중심점 변경 + 줌 레벨 변경
                setMapCenter(location.getLatitude(), location.getLongitude());

                //위도경도 정보로 해당 주소지명 가져오기 => call back method 에서 결과 처리합시다.
                MapReverseGeoCoder reverseGeoCoder =
                        new MapReverseGeoCoder(getApplicationContext().getResources().getString(R.string.daum_map_view_api_key),
                        MapPoint.mapPointWithGeoCoord(location.getLatitude(), location.getLongitude()),
                                geoCodingResultListener,
                        MainActivity.this);

                reverseGeoCoder.startFindingAddress();

                // 해당 location 에 맞는 bus stop 1개의 미세먼지 데이터 요청 : request1 을 여기서 해주자


            }

            public void onStatusChanged(String provider, int status, Bundle extras) {
            }

            public void onProviderEnabled(String provider) {
            }

            public void onProviderDisabled(String provider) {
            }

        };

        // Register the listener with the Location Manager to receive location updates
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }else {
            // location 받아오기를 요청합니다 ( 두 번째 parameter 가 interval 밀리세컨즈 )
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
        }

    }


    /**
     * 지도의 중심을 어디로 해서 보여줄지 설정
     */
    public void setMapCenter(double latitude, double longitude) {
        // 중심점 변경
        mapView.setMapCenterPoint(MapPoint.mapPointWithGeoCoord(latitude, longitude), true); // 대구광역시
        // 줌 레벨 변경
        mapView.setZoomLevel(1, true); // level 낮을 수록 확대

//        // 중심점 변경 + 줌 레벨 변경
//        mapView.setMapCenterPointAndZoomLevel(MapPoint.mapPointWithGeoCoord(33.41, 126.52), 9, true);

        // 줌 인
        mapView.zoomIn(false);

        // 줌 아웃
        mapView.zoomOut(false);

        // 중심점에 Marker로 표시해줍니다
        addCenterMarker(latitude, longitude);
    }


    /**
     * small map view 에 지정된 위치에  중심 maker 를 표시합니다.
     *
     */
    public void addCenterMarker(double latitude, double longitude) {
        marker = new MapPOIItem();
        marker.setItemName("현재 위치");
        marker.setTag(0);
        marker.setMapPoint(MapPoint.mapPointWithGeoCoord(latitude, longitude)); //대구 광역시 중심
        marker.setMarkerType(MapPOIItem.MarkerType.BluePin); // 기본으로 제공하는 BluePin 마커 모양.
        marker.setSelectedMarkerType(MapPOIItem.MarkerType.BluePin); // 마커를 클릭했을때, 기본으로 제공하는 RedPin 마커 모양.
        mapView.addPOIItem(marker);

    }


    /**
     * OnClick Listener
     * @param v
     */
    @Override
    public void onClick(View v) {

        switch (v.getId()) {
//            case R.id.map_button : //지도 보기 버튼 눌렀을 때
//                //Open Map Activity
//                Intent intent = new Intent(this, DMapActivity.class);
//                startActivity(intent);
//                Log.d(this.getLocalClassName(), "map_button clicked ! ");
//                break;



            default:

                break;
        }
    }


    /**
     *  HTTP통신으로 서버에 데이터 요청하기!
     *
     */
    public void requestServerData() {

        new AsyncTask<Void,Void,Void>(){
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
//                REQUEST_URL_CURRENT_LOCATION = "rose.teemo.io"; //탐색하고 싶은 URL이다. <= static 으로 set 해놓을께용
            }

            @Override
            protected Void doInBackground(Void... voids) {
                try{
                    requestUrl = new URL(REQUEST_URL_CURRENT_LOCATION);  // URL화 한다.
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



    // ============ Daum MapView override 메서드 ============ //

    @Override
    public void onMapViewInitialized(MapView mapView) {
        setMapCenter(latestLatitude, latestLongitude); //대구광역시를 중심

    }

    @Override
    public void onMapViewCenterPointMoved(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewZoomLevelChanged(MapView mapView, int i) {

    }

    /**
     * 맵이 한 번 눌렸을 때
     * @param mapView
     * @param mapPoint
     */
    @Override
    public void onMapViewSingleTapped(MapView mapView, MapPoint mapPoint) {
        // DMapActivity 를 호출합니다.
        Intent intent = new Intent(this, DMapActivity.class);
        startActivity(intent);
        Log.d(this.getLocalClassName(), "map clicked ! ");

    }

    @Override
    public void onMapViewDoubleTapped(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewLongPressed(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewDragStarted(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewDragEnded(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewMoveFinished(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onPOIItemSelected(MapView mapView, MapPOIItem mapPOIItem) {

    }

    @Override
    public void onCalloutBalloonOfPOIItemTouched(MapView mapView, MapPOIItem mapPOIItem) {

    }

    @Override
    public void onCalloutBalloonOfPOIItemTouched(MapView mapView, MapPOIItem mapPOIItem, MapPOIItem.CalloutBalloonButtonType calloutBalloonButtonType) {

    }

    @Override
    public void onDraggablePOIItemMoved(MapView mapView, MapPOIItem mapPOIItem, MapPoint mapPoint) {

    }

    // ============ ReverseGeoCodingResultListener override 메서드 ============ //
    @Override
    public void onReverseGeoCoderFoundAddress(MapReverseGeoCoder mapReverseGeoCoder, String s) {
        //GeoCoding => 위도 경도 Location 정보를 통해 주소를 찾은 경우
        Log.d("REVERSE GEO CODER", s);
        currentLocation.setText(s);

    }

    @Override
    public void onReverseGeoCoderFailedToFindAddress(MapReverseGeoCoder mapReverseGeoCoder) {
        //GeoCoding => 위도 경도 Location 정보를 통해 주소를 찾은 경우
        Log.d("REVERSE GEO CODER", "주소를 못 찾았습니다 ");
        currentLocation.setText("대구광역시 중구"); //default location 를 대구광역시 중구로 설정합니다
    }
}
