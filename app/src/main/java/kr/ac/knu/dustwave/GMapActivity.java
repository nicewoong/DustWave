package kr.ac.knu.dustwave;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class GMapActivity  extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener, ClusterManager.OnClusterItemClickListener<BusStopClusterItem> {


    public static final String LOG_TAG = "GMapActivity_LOG";

    ClusterManager<BusStopClusterItem> mClusterManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goole_map);

        MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    /**
     * 맵이 준비 되었을 때
     * @param googleMap
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {


        Log.d(LOG_TAG, " onMap Ready() 가 호출되었음 ");

        // 카메라 중심 이동
        googleMap.moveCamera(CameraUpdateFactory.
                newLatLngZoom(new LatLng(MainActivity.latestLatitude, MainActivity.latestLongitude), 15)); // 숫자 높을 수록 확대

        // 현재 위치 (중점) 마커 표시
        addCurrentLocationMarker(new LatLng(MainActivity.latestLatitude, MainActivity.latestLongitude), googleMap);

        mClusterManager = new ClusterManager<>(this, googleMap);
        mClusterManager.setOnClusterItemClickListener(this);
        googleMap.setOnCameraIdleListener(mClusterManager);
        googleMap.setOnMarkerClickListener(mClusterManager);




        // 받아온 모든 정류장 미세먼지 데이터 (커스텀 마커만 not Clustering)
//        addAllBusStopMarker(MainActivity.allBusStopDustInfoList, googleMap);

        // 받아온 모든 정류장 미세먼지 데이터 (클러스터 마커로)
         addAllBusStopClusterMarker(MainActivity.allBusStopDustInfoList, googleMap);

        // 받아온 모든 정류장 미세먼지 데이터를 Circle overlay 로 표시
        addAllBusStopCircle(MainActivity.allBusStopDustInfoList, googleMap);

    }




    /**
     * 지도 위에 지정된 위치에 maker 를 표시합니다.
     *
     */
    public void addCurrentLocationMarker(LatLng centerLatLng, GoogleMap googleMap ) {

        googleMap.addMarker(new MarkerOptions()
                .position(new LatLng(MainActivity.latestLatitude, MainActivity.latestLongitude))
                .title("현재 위치")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_current_pink)));

    }


    public void addAllBusStopMarker(JSONArray busStopDustInfoList, GoogleMap googleMap) {

        Log.d("ADD_ALL_MARKER", " 모든 마커 반복문 시작! ");
        if(busStopDustInfoList == null)
            return;

        for(int i = 0; i< busStopDustInfoList.length(); i=i+2) {
            try {
                addCustomDustInfoMarker(busStopDustInfoList.getJSONObject(i), googleMap); // 마커그리기
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        Log.d("ADD_ALL_MARKER", " 모든 마커 반복문 끝! ");

    }


    /**
     * 지도 위에 지정된 위치에 maker 를 표시합니다.
     *
     */
    public void addCustomDustInfoMarker(JSONObject dustInfoObject, GoogleMap googleMap) {

        double dustInfoPm10; //미세먼지
        double dustInfoPm25; //초미세먼지
        double latitude;
        double longitude;
        String fineDustLevel; //좋은지 나쁜지 text
        int markerResourceId;

        try {
            dustInfoPm10 = Math.random()*200+1;
//            dustInfoPm10 = dustInfoObject.getDouble(LocalDatabaseKey.dust_info_pm10);

            dustInfoPm25 = dustInfoObject.getDouble(LocalDatabaseKey.dust_info_pm25);
            latitude = dustInfoObject.getDouble(LocalDatabaseKey.bus_stop_latitude);
            longitude = dustInfoObject.getDouble(LocalDatabaseKey.bus_stop_longitude);


            if (dustInfoPm10 <= 30) {
                fineDustLevel = "좋음"; //파랑색
                markerResourceId = R.drawable.marker_green;
            } else if (dustInfoPm10 > 30 && dustInfoPm10 <= 80) {
                fineDustLevel = "보통"; //초록
                markerResourceId = R.drawable.marker_blue;

            }else if (dustInfoPm10 > 80 && dustInfoPm10 <= 150) {
                fineDustLevel = "나쁨"; //오린지
                markerResourceId = R.drawable.marker_orange;

            } else{
                fineDustLevel = "매우나쁨"; //빨강
                markerResourceId = R.drawable.marker_red;

            }

            // 마커 추가
            googleMap.addMarker(new MarkerOptions()
                    .position(new LatLng(latitude, longitude))
                    .title(fineDustLevel)
                    .alpha(0.6f) // 0 이 완전 투명
                    .icon(BitmapDescriptorFactory.fromResource(markerResourceId)));



        } catch (JSONException e) {
            e.printStackTrace();
        }



    }


    /**
     * addAllBusStopMarker 와 같은 기능을 하는 클러스터용 메서드 구현
     *
     * @param busStopDustInfoList
     * @param googleMap
     */
    public void addAllBusStopClusterMarker(JSONArray busStopDustInfoList, GoogleMap googleMap) {

        Log.d("ADD_ALL_MARKER", " 모든 마커 반복문 시작! ");
        if(busStopDustInfoList == null)
            return;

        for(int i = 0; i< busStopDustInfoList.length(); i=i+2) {
            try {
                addDustInfoClusterMarker(busStopDustInfoList.getJSONObject(i), googleMap); // 마커그리기
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        Log.d("ADD_ALL_MARKER", " 모든 마커 반복문 끝! ");

    }


    /**
     * 지도 위에 지정된 위치에 maker 를 표시합니다.
     *
     */
    public void addDustInfoClusterMarker(JSONObject dustInfoObject, GoogleMap googleMap) {

        double dustInfoPm10; //미세먼지
        double dustInfoPm25; //초미세먼지
        double latitude;
        double longitude;
        String fineDustLevel; //좋은지 나쁜지 text
        int markerResourceId;

        try {
            dustInfoPm10 = dustInfoObject.getDouble(LocalDatabaseKey.dust_info_pm10);
            dustInfoPm25 = dustInfoObject.getDouble(LocalDatabaseKey.dust_info_pm25);
            latitude = dustInfoObject.getDouble(LocalDatabaseKey.bus_stop_latitude);
            longitude = dustInfoObject.getDouble(LocalDatabaseKey.bus_stop_longitude);


            BusStopClusterItem busStopClusterItem = new BusStopClusterItem(new LatLng(latitude, longitude));
            mClusterManager.addItem(busStopClusterItem);



        } catch (JSONException e) {
            e.printStackTrace();
        }


    }


    /**
     * addAllBusStopMarker 와 같은 기능을 하는 클러스터용 메서드 구현
     *
     * @param busStopDustInfoList
     * @param googleMap
     */
    public void addAllBusStopCircle(JSONArray busStopDustInfoList, GoogleMap googleMap) {

        Log.d("ADD_ALL_Circle", " 모든 마커 반복문 시작! ");
        if(busStopDustInfoList == null)
            return;

        for(int i = 0; i< busStopDustInfoList.length(); i=i+2) {
            try {
                addDustInfoCircleOverlay(busStopDustInfoList.getJSONObject(i), googleMap); // 마커그리기
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        Log.d("ADD_ALL_Circle", " 모든 마커 반복문 끝! ");

    }




    /**
     * 해당 버스스탑의 미세먼지 수치에 따른 색상으로 circle overlay를 그려줍니다.
     *
     * @param dustInfoObject
     * @param googleMap
     */
    public void addDustInfoCircleOverlay(JSONObject dustInfoObject, GoogleMap googleMap) {

        double dustInfoPm10; //미세먼지
        double dustInfoPm25; //초미세먼지
        double latitude;
        double longitude;
        String fineDustLevel; //좋은지 나쁜지 text
        int markerResourceId;
        int circleColor;

        try {
//            dustInfoPm10 = Math.random()*200+1; // 다양한 색상을 위한 랜덤값!
            dustInfoPm10 = dustInfoObject.getDouble(LocalDatabaseKey.dust_info_pm10); // 진짜 값
            dustInfoPm25 = dustInfoObject.getDouble(LocalDatabaseKey.dust_info_pm25);
            latitude = dustInfoObject.getDouble(LocalDatabaseKey.bus_stop_latitude);
            longitude = dustInfoObject.getDouble(LocalDatabaseKey.bus_stop_longitude);


            if (dustInfoPm10 <= 30) { //파랑
                circleColor = Color.argb(150, 0, 0, 255);

            } else if (dustInfoPm10 > 30 && dustInfoPm10 <= 80) { //초롱
                circleColor = Color.argb(150, 0, 255, 0);


            }else if (dustInfoPm10 > 80 && dustInfoPm10 <= 150) { //오렌지
                circleColor = Color.argb(150, 255,165,0);


            } else{ //빨강
                circleColor = Color.argb(150, 255, 0, 0);

            }

            // 우선 서클옵션을 만들고
            CircleOptions circleOptions = new CircleOptions()
                    .center(new LatLng(latitude, longitude))
                    .radius(150)
                    .strokeWidth(0)
                    .fillColor(circleColor); // 수치에 따른 색상

            //옵션을 활용해 써클을 추가합니다
            Circle circle = googleMap.addCircle(circleOptions); // return 되는 circle 은 나중에 변경가능 (Mutable)



        } catch (JSONException e) {
            e.printStackTrace();
        }


    }


    @Override
    public boolean onMarkerClick(Marker marker) {
        Log.d("onClusterClick", "마커를 클릭하였습니다 . " + marker.toString());

        return false;
    }

    @Override
    public boolean onClusterItemClick(BusStopClusterItem busStopClusterItem) {

        Log.d("onClusterClick", "클러스터마커를 클릭하였습니다 . " + busStopClusterItem.toString());
        // TODO: 2017. 6. 23. 다이얼로그 하나 띄우고 busStopClusterItem 에서 정보 뽑아서 보여주면 될 듯!

        return false;
    }
    
}
