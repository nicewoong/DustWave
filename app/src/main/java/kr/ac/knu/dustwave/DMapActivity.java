package kr.ac.knu.dustwave;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.ViewGroup;

import net.daum.mf.map.api.CameraUpdateFactory;
import net.daum.mf.map.api.MapCircle;
import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapPointBounds;
import net.daum.mf.map.api.MapView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


/**
 * 다음지도API를 활용해서 지도 화면을 보여주는 Activity.
 */

public class DMapActivity extends AppCompatActivity implements MapView.MapViewEventListener,  MapView.POIItemEventListener  {

    MapView mapView;
    ViewGroup mapViewContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dmap);



    }

    /**
     * 가려진 Activity 가 다시 보이게 될 때
     */
    @Override
    protected void onResume() {
        super.onResume();

        createMapView(); // 다음 지도를 생성해서 view 를 채운다.

    }

    @Override
    protected void onPause() {
        super.onPause();
        mapViewContainer.removeAllViews(); // 다시 MainActivity 에 small map view 를 그려줘야하기 때문에 여기서 다음 맵을 삭제한다.
    }

    /**
     * 다음지도API를 활용해서 맵뷰를 생성해서 activity 를 채운다.
     *
     */
    public void createMapView() {
        mapView = new MapView(this);
        mapView.setDaumMapApiKey(this.getResources().getString(R.string.daum_map_view_api_key)); // 다음 api key 를 인자로 넘겨준다.

        mapViewContainer = (ViewGroup) findViewById(R.id.map_view);
        mapViewContainer.addView(mapView);

        //이벤트리스너 등록
        mapView.setMapViewEventListener(this); // this에 MapView.MapViewEventListener 구현.
        mapView.setPOIItemEventListener(this);


    }


    /**
     * 지도의 중심을 어디로 해서 보여줄지 설정
     *
     */
    public void setMapCenter(double latitude, double longitude) {
        addCurrentLocationMarker(latitude, longitude);

        // 중심점 변경
        mapView.setMapCenterPoint(MapPoint.mapPointWithGeoCoord(latitude, longitude), true);

        // 줌 레벨 변경
        mapView.setZoomLevel(2, true);

        // 줌 인
        mapView.zoomIn(true);

        // 줌 아웃
        mapView.zoomOut(true);
    }

    /**
     * 지도 위에 지정된 위치에 maker 를 표시합니다.
     *
     */
    public void addCurrentLocationMarker(double latitude, double longitude) {
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

    public void addAllBusStopMarker(JSONArray busStopDustInfoList) {

        Log.d("ADD_ALL_MARKER", " 모든 마커 반복문 시작! ");
        if(busStopDustInfoList == null)
            return;

        for(int i = 0; i< busStopDustInfoList.length(); i=i+2) {
            try {
//                addCustomDustInfoMarker(busStopDustInfoList.getJSONObject(i)); // 마커그리기
                addCircleOverLay(busStopDustInfoList.getJSONObject(i)); //동그란 오버레이 그리기
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
    public void addCustomDustInfoMarker(JSONObject dustInfoObject) {

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

            MapPOIItem customMarker = new MapPOIItem();
            customMarker.setItemName(fineDustLevel); // 좋음인지 나쁨인지
//            customMarker.setTag(1);
            customMarker.setMapPoint(MapPoint.mapPointWithGeoCoord(latitude, longitude));
            customMarker.setMarkerType(MapPOIItem.MarkerType.CustomImage); // 마커타입을 커스텀 마커로 지정.
            customMarker.setCustomImageResourceId(markerResourceId); // 마커 이미지.
            customMarker.setCustomImageAutoscale(true); // hdpi, xhdpi 등 안드로이드 플랫폼의 스케일을 사용할 경우 지도 라이브러리의 스케일 기능을 꺼줌.
            customMarker.setCustomImageAnchor(0.5f, 1.0f); // 마커 이미지중 기준이 되는 위치(앵커포인트) 지정 - 마커 이미지 좌측 상단 기준 x(0.0f ~ 1.0f), y(0.0f ~ 1.0f) 값.
            customMarker.setMoveToCenterOnSelect(true);
            mapView.addPOIItem(customMarker);


        } catch (JSONException e) {
            e.printStackTrace();
        }



    }



    /**
     * 지도 위에 Circle 을 그립니다
     *
     */
    public void addCircleOverLay(JSONObject dustInfoObject) {


        double dustInfoPm10; //미세먼지
        double dustInfoPm25; //초미세먼지
        double latitude;
        double longitude;
        String fineDustLevel; //좋은지 나쁜지 text
        int overLayColorId;




        try {
            dustInfoPm10 = Math.random()*200+1; // 랜덤값 !
//            dustInfoPm10 = dustInfoObject.getDouble(LocalDatabaseKey.dust_info_pm10);

            dustInfoPm25 = dustInfoObject.getDouble(LocalDatabaseKey.dust_info_pm25);
            latitude = dustInfoObject.getDouble(LocalDatabaseKey.bus_stop_latitude);
            longitude = dustInfoObject.getDouble(LocalDatabaseKey.bus_stop_longitude);


            if (dustInfoPm10 <= 30) {
                fineDustLevel = "좋음"; //파랑색
                overLayColorId = Color.argb(128,0,0,255);
            } else if (dustInfoPm10 > 30 && dustInfoPm10 <= 80) {
                fineDustLevel = "보통"; //초록
                overLayColorId = Color.argb(128,0,255,0);

            }else if (dustInfoPm10 > 80 && dustInfoPm10 <= 150) {
                fineDustLevel = "나쁨"; //오린지
                overLayColorId = Color.argb(128,255,165,0);

            } else{
                fineDustLevel = "매우나쁨"; //빨강
                overLayColorId = Color.argb(128,255,0,0);

            }

            MapCircle circle1 = new MapCircle(
                    MapPoint.mapPointWithGeoCoord(latitude, longitude), // center
                    150, // radius
                    overLayColorId, // strokeColor
                    overLayColorId // fillColor
            );
            circle1.setTag(1234);
            mapView.addCircle(circle1);

        } catch (JSONException e) {
            e.printStackTrace();
        }








    }




    // ============ MapView.MapViewEventListener 구현해야하는 메서드 ============ //

    @Override
    public void onMapViewInitialized(MapView mapView) {
        setMapCenter(MainActivity.latestLatitude, MainActivity.latestLongitude); //지도의 중심점 설정
//        addMarker(); // marker 표시하기
        //addCircleOverLay(); // circle  그리기
        addAllBusStopMarker(MainActivity.allBusStopDustInfoList);
    }


    @Override
    public void onMapViewCenterPointMoved(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewZoomLevelChanged(MapView mapView, int i) {

    }

    @Override
    public void onMapViewSingleTapped(MapView mapView, MapPoint mapPoint) {

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


    // ============ MapView.POIItemEventListener 구현해야하는 메서드 ============ //

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
}
