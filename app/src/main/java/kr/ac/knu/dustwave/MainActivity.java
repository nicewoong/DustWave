package kr.ac.knu.dustwave;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapReverseGeoCoder;
import net.daum.mf.map.api.MapView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity implements MapView.MapViewEventListener, MapView.POIItemEventListener, MapReverseGeoCoder.ReverseGeoCodingResultListener, LocationListener, CurrentLocationDustInfoHttpRequestListener, AllBusStopDustInfoHttpRequestListener {


    //View
    public TextView currentLocation; // 현재 위치에 대한 한글 주소 표시
    public TextView fineDustCount; // 미세먼지 수치
    public TextView ultraFineDustCount; // 초 미세먼지 수치
    public TextView textDegreeExpress; // 미세먼지 레벨 표시 (좋음 보통 나쁨 매우나쁨)

    public ImageView fine_dust_level_icon;


    //map view 를 위한 variables
    MapView mapView;
    ViewGroup mapViewContainer;
    MapPOIItem marker; //중심점 마커

    //가장 최신 위도경도 => default 는 대구광역시 중심입니다.
    public static double latestLatitude = 35.8714354;
    public static double latestLongitude = 128.601445;


    // Acquire a reference to the system Location Manager
    LocationManager locationManager;

    //현재 위치에 맞는 미세먼지 데이터
    public CurrentLocationDustInfo currentLocationDustInfo = new CurrentLocationDustInfo();
    //모든 버정의 미세먼지 데이터
    public AllBusStopDustInfo allBusStopDustInfo = new AllBusStopDustInfo(MainActivity.this);


    //모든 버스 정류장 미세먼지 최신 데이터 array
    public static JSONArray allBusStopDustInfoList;

    // 현재 위치에 해당하는 미세먼지 데이터 object
    public static JSONObject currentLocationDustInfoObject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setViewAction(); // button 등의 view 들을 구성합니다.

        allBusStopDustInfo.requestAllData(this);
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE); // 위도 경도를 받아올 준비
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
        }
        Log.d("MainActivity:onCreate()","위치정보 업데이트를 요청합니다. ");
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);

        createSmallMapView(); // 가려진 뷰가 다시 생성될 때 다음 지도를 생성해서 view 를 채운다.
    }

    /**
     * 가려졌던 View 가 다시 보이게 될 때
     */
    @Override
    protected void onResume() {
        super.onResume();



        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
        }
        Log.d("MainActivity:onResume()","위치정보 업데이트를 요청합니다. ");
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);


    }

    /**
     * 뷰가 가려지거나 없어지기 전 가장 기초단계
     */
    @Override
    protected void onPause() {
        super.onPause();
//        mapViewContainer.removeAllViews();

    }

    /**
     * Button 등의 view 들을 구성합니다.
     *
     */
    public void setViewAction() {
        currentLocation = (TextView) findViewById(R.id.textview_current_location);
        fineDustCount = (TextView) findViewById(R.id.fine_dust_count);
        ultraFineDustCount = (TextView) findViewById(R.id.ultra_fine_dust_count);
        textDegreeExpress = (TextView) findViewById(R.id.text_degree_express);
        fine_dust_level_icon = (ImageView) findViewById(R.id.fine_dust_level_icon);

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
     * 지도의 중심을 어디로 해서 보여줄지 설정
     */
    public void setMapCenter(double latitude, double longitude) {
        // 중심점 변경
        mapView.setMapCenterPoint(MapPoint.mapPointWithGeoCoord(latitude, longitude), true); // 대구광역시
        // 줌 레벨 변경
        mapView.setZoomLevel(1, true); // level 낮을 수록 확대

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
        MapPOIItem customMarker = new MapPOIItem();
        customMarker.setItemName("현재위치");
        customMarker.setTag(1);
        customMarker.setMapPoint(MapPoint.mapPointWithGeoCoord(latitude, longitude));
        customMarker.setMarkerType(MapPOIItem.MarkerType.CustomImage); // 마커타입을 커스텀 마커로 지정.
        customMarker.setCustomImageResourceId(R.drawable.marker_current_pink); // 마커 이미지.
        customMarker.setCustomImageAutoscale(true); // hdpi, xhdpi 등 안드로이드 플랫폼의 스케일을 사용할 경우 지도 라이브러리의 스케일 기능을 꺼줌.
        customMarker.setCustomImageAnchor(0.5f, 1.0f); // 마커 이미지중 기준이 되는 위치(앵커포인트) 지정 - 마커 이미지 좌측 상단 기준 x(0.0f ~ 1.0f), y(0.0f ~ 1.0f) 값.
        customMarker.setMoveToCenterOnSelect(true);
        mapView.addPOIItem(customMarker);

    }



    /**
     - Json object 를 인자로 받아서 UI에 표시하는 함수
     - 이름 : updateCurrentLocationDustDataView()
     - 인자는 현재 위치에 대한 미세먼지 값 담고 있는 json Object
     => 이것은 MainActivity 에 있어야 한다.

     */
    public void updateCurrentLocationDustDataView(JSONObject currentLocationDustInfo) {

        if(currentLocationDustInfo == null)
            return;

        double dustInfoPm10; //미세먼지
        double dustInfoPm25; //초미세먼지



        try {
            dustInfoPm10 = currentLocationDustInfo.getDouble(LocalDatabaseKey.dust_info_pm10);
            dustInfoPm25 = currentLocationDustInfo.getDouble(LocalDatabaseKey.dust_info_pm25);

            fineDustCount.setText(dustInfoPm10+""); // 미세먼지
            ultraFineDustCount.setText(dustInfoPm25+""); // 초미세먼지
            setFineDustLevelText(dustInfoPm10); // 좋음인지 나쁨인지 텍스트, 텍스트 컬러
            setFineDustLevelImage(dustInfoPm10); // 아이콘


        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    /**
     * 미세먼지 10 수치에 따라서
     * 좋음 보통 나쁨 매우나쁨 을 판단해서
     * String으로 return 합니다.
     *
     * @param fineDustPm10
     * @return
     */
    public void setFineDustLevelText(double fineDustPm10) {

        if (fineDustPm10 <= 30) {
            textDegreeExpress.setText("좋음"); //파랑색
            textDegreeExpress.setTextColor(getResources().getColor(R.color.pretty_blue));
        } else if (fineDustPm10 > 30 && fineDustPm10 <= 80) {
            textDegreeExpress.setText("보통"); //파랑색
            textDegreeExpress.setTextColor(getResources().getColor(R.color.pretty_green));

        }else if (fineDustPm10 > 80 && fineDustPm10 <= 150) {
            textDegreeExpress.setText("나쁨"); //파랑색
            textDegreeExpress.setTextColor(getResources().getColor(R.color.pretty_orange));
        } else{
            textDegreeExpress.setText("매우나쁨"); //파랑색
            textDegreeExpress.setTextColor(getResources().getColor(R.color.red));
        }

    }


    public void setFineDustLevelImage(double fineDustPm10) {

        if (fineDustPm10 <= 30) {
//            return "좋음"; //파랑색
            fine_dust_level_icon.setImageResource(R.drawable.face_icon_good);
        }else if (fineDustPm10 > 30 && fineDustPm10 <= 80) {
//            return "보통"; //초록색
            fine_dust_level_icon.setImageResource(R.drawable.face_icon_normal);

        }else if (fineDustPm10 > 80 && fineDustPm10 <= 150) {
//            return "나쁨"; //주황색
            fine_dust_level_icon.setImageResource(R.drawable.face_icon_bad);

        }else {
//            return "매우나쁨"; //빨강색
            fine_dust_level_icon.setImageResource(R.drawable.face_icon_too_bad);

        }

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
        Intent intent = new Intent(this, GMapActivity.class);
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
        Log.d("REVERSE GEO CODER", "주소 text 를 못 찾았습니다 ");
        currentLocation.setText("대구광역시"); //default location 를 대구광역시 중구로 설정합니다
    }

    // ============ LocationListener override 메서드 ============ //

    /**
     * 위치 정보가 갱신 됐을 때 !
     * @param location
     */
    @Override
    public void onLocationChanged(Location location) {

        // Called when a new location is found by the network location provider.
        Log.d("LOCATION UPDATED : ", location.toString());

        // 최신 위도경도 값 최신화
        latestLatitude = location.getLatitude();
        latestLongitude = location.getLongitude();

        // 위도 경도에 따른 미세먼지 데이터 하나 요청
        currentLocationDustInfo.requestCurrentDataByLocation(this, latestLatitude, latestLongitude);

        // small map view 의  중심점 변경 + 줌 레벨 변경
        setMapCenter(location.getLatitude(), location.getLongitude());

        //위도경도 정보로 해당 주소지명 가져오기 => call back method 에서 결과 처리합시다.
        MapReverseGeoCoder reverseGeoCoder =
                new MapReverseGeoCoder(getApplicationContext().getResources().getString(R.string.daum_map_view_api_key),
                        MapPoint.mapPointWithGeoCoord(location.getLatitude(), location.getLongitude()),
                        this,
                        MainActivity.this);

        reverseGeoCoder.startFindingAddress();



        /*
        한 번 받아왔으니 중지 !
         */
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            return;
        }
        locationManager.removeUpdates(this);



    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }


    // ============ HTTP 로 현재 위도,경도에 맞는 미세먼지 데이터 요청 override 메서드 ============ //
    /**
     *
     * @param requestStream
     */
    @Override
    public void onCurrentLocationDataStringArrived(String requestStream) {
        if(requestStream!=null) {
            Log.d("MAIN:onCurrentArrived", "현재 위치에 해당하는 미세먼지 정보 http 요청 결과가 도착했습니다. \n -> " + requestStream);
            // json object로 변경
            currentLocationDustInfoObject = currentLocationDustInfo.convertCurrentLocationDustDataToObject(requestStream);
            // 뷰 갱신 !!
            updateCurrentLocationDustDataView(currentLocationDustInfoObject);
        }else {
            Log.e("MAIN:onArrived", " NULL 이네요. 아무것도 안 받아와요. ");

        }



    }

    // ============ 모든 정류장의 미세먼지 데이터 요청 override 메서드 ============ //
    @Override
    public void onAllBusStopDustInfoDataStringArrived(String requestStream) {
        if(requestStream!=null) {
            Log.d("MAIN:onAllBusArrived", requestStream);
            // 일단 JsonArray를 MainActivity(here)의 static 변수에 저장하고, 써먹자.
            // TODO: 2017. 6. 22. 지금은 바로 스태틱 변수에 저장하는데 이것을 db에 저장하는 형태로 변경하면 됌 여기가 바로 그 타이밍임
            allBusStopDustInfoList = allBusStopDustInfo.convertAllRecentDustDataToJasonArray(requestStream);
            Log.d("MAIN:onAllBusArrived", "전체 버정 데이터 셋 배열 갯수 : "+allBusStopDustInfoList.length()+"");

        }else {
            Log.e("MAIN:onAllBusArrived", "모든 정류장 미세먼지 데이터가 NULL 이네요. 아무것도 안 받아와요. ");

        }

    }
}
