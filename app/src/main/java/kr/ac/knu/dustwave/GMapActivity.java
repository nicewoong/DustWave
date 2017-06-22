package kr.ac.knu.dustwave;

import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class GMapActivity  extends AppCompatActivity implements OnMapReadyCallback {


    public static final String LOG_TAG = "GMapActivity_LOG";

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
                newLatLngZoom(new LatLng(MainActivity.latestLatitude, MainActivity.latestLongitude), 17)); // 숫자 높을 수록 확대

        // 현재 위치 (중점) 마커 표시
        addCurrentLocationMarker(new LatLng(MainActivity.latestLatitude, MainActivity.latestLongitude), googleMap);

        // 받아온 모든 정류장 미세먼지 데이터
        addAllBusStopMarker(MainActivity.allBusStopDustInfoList, googleMap);

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
                    .icon(BitmapDescriptorFactory.fromResource(markerResourceId)));



        } catch (JSONException e) {
            e.printStackTrace();
        }



    }




}
