package kr.ac.knu.dustwave;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ViewGroup;

import net.daum.mf.map.api.CameraUpdateFactory;
import net.daum.mf.map.api.MapCircle;
import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapPointBounds;
import net.daum.mf.map.api.MapView;


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


        createMapView(); // 다음 지도를 생성해서 view 를 채운다.


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
     * todo 사용자의 현재위치를 받아서 중심점을 선택하는 기능으로 변경하기
     *
     */
    public void setMapCenter() {
        // 중심점 변경
        mapView.setMapCenterPoint(MapPoint.mapPointWithGeoCoord(35.8714354, 128.601445), true); // 대구광역시

        // 줌 레벨 변경
        mapView.setZoomLevel(7, true);

//        // 중심점 변경 + 줌 레벨 변경
//        mapView.setMapCenterPointAndZoomLevel(MapPoint.mapPointWithGeoCoord(33.41, 126.52), 9, true);

        // 줌 인
        mapView.zoomIn(true);

        // 줌 아웃
        mapView.zoomOut(true);
    }


    /**
     * 지도 위에 지정된 위치에 maker 를 표시합니다.
     *
     */
    public void addMarker() {
        MapPOIItem marker = new MapPOIItem();
        marker.setItemName("Default Marker");
        marker.setTag(0);
        marker.setMapPoint(MapPoint.mapPointWithGeoCoord(35.8714354, 128.601445)); //대구 광역시 중심
        marker.setMarkerType(MapPOIItem.MarkerType.BluePin); // 기본으로 제공하는 BluePin 마커 모양.
        marker.setSelectedMarkerType(MapPOIItem.MarkerType.RedPin); // 마커를 클릭했을때, 기본으로 제공하는 RedPin 마커 모양.

        mapView.addPOIItem(marker);

        //custom marker

//        MapPOIItem customMarker = new MapPOIItem();
//        customMarker.setItemName("Custom Marker");
//        customMarker.setTag(1);
//        customMarker.setMapPoint(MapPoint.mapPointWithGeoCoord(35.8900521, 128.6113282)); // 경북대학교
//        customMarker.setMarkerType(MapPOIItem.MarkerType.CustomImage); // 마커타입을 커스텀 마커로 지정.
//        customMarker.setCustomImageResourceId(R.drawable.map_marker_blue); // 마커 이미지.
//        customMarker.setCustomImageAutoscale(false); // hdpi, xhdpi 등 안드로이드 플랫폼의 스케일을 사용할 경우 지도 라이브러리의 스케일 기능을 꺼줌.
//        customMarker.setCustomImageAnchor(0.5f, 1.0f); // 마커 이미지중 기준이 되는 위치(앵커포인트) 지정 - 마커 이미지 좌측 상단 기준 x(0.0f ~ 1.0f), y(0.0f ~ 1.0f) 값.
//
//        mapView.addPOIItem(customMarker);

    }


    /**
     * 지도 위에 Circle 을 그립니다
     *
     */
    public void addCircleOverLay() {
        MapCircle circle1 = new MapCircle(
                MapPoint.mapPointWithGeoCoord(35.8900521, 128.6113282), // center
                500, // radius
                Color.argb(128, 255, 0, 0), // strokeColor
                Color.argb(128, 0, 255, 0) // fillColor
        );
        circle1.setTag(1234);
        mapView.addCircle(circle1);

//        MapCircle circle2 = new MapCircle(
//                MapPoint.mapPointWithGeoCoord(35.8900521, 128.6113282), // circle 중심 :경북대
//                1000, // radius
//                Color.argb(128, 255, 0, 0), // strokeColor
//                Color.argb(128, 255, 255, 0) // fillColor
//        );
//        circle2.setTag(5678);
//        mapView.addCircle(circle2);

//
//        // 지도뷰의 중심좌표와 줌레벨을 Circle이 모두 나오도록 조정.
//        MapPointBounds[] mapPointBoundsArray = { circle1.getBound(), circle2.getBound() };
//        MapPointBounds mapPointBounds = new MapPointBounds(mapPointBoundsArray);
//        int padding = 50; // px
//        mapView.moveCamera(CameraUpdateFactory.newMapPointBounds(mapPointBounds, padding));
    }




    // ============ MapView.MapViewEventListener 구현해야하는 메서드 ============ //

    @Override
    public void onMapViewInitialized(MapView mapView) {
        setMapCenter(); //지도의 중심점 설정
        addMarker(); // marker 표시하기
        addCircleOverLay(); // circle  그리기
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
