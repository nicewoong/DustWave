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
     * todo 사용자의 현재위치를 받아서 중심점을 선택하는 기능으로 변경하기, 흠 지도로보기에서는 굳이 사용자 위치 아니고 대구 중심으로 해도 될 것 같기도 하다.
     *
     */
    public void setMapCenter() {
        // 중심점 변경
        mapView.setMapCenterPoint(MapPoint.mapPointWithGeoCoord(35.8714354, 128.601445), true); // 대구광역시

        // 줌 레벨 변경
        mapView.setZoomLevel(5, true);

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
        marker.setMarkerType(MapPOIItem.MarkerType.RedPin); // 기본으로 제공하는 BluePin 마커 모양.
        marker.setSelectedMarkerType(MapPOIItem.MarkerType.BluePin); // 마커를 클릭했을때, 기본으로 제공하는 RedPin 마커 모양.
        mapView.addPOIItem(marker);

        //남구
        MapPOIItem marker2 = new MapPOIItem();
        marker2.setItemName("남구");
        marker2.setTag(0);
        marker2.setMapPoint(MapPoint.mapPointWithGeoCoord(35.8460224,128.59752909999997)); //대구 광역시 남구
        marker2.setMarkerType(MapPOIItem.MarkerType.YellowPin); // 기본으로 제공하는 BluePin 마커 모양.
        marker2.setSelectedMarkerType(MapPOIItem.MarkerType.RedPin); // 마커를 클릭했을때, 기본으로 제공하는 RedPin 마커 모양.

        mapView.addPOIItem(marker2);

        //북구
        MapPOIItem marker3 = new MapPOIItem();
        marker3.setItemName("북구");
        marker3.setTag(0);
        marker3.setMapPoint(MapPoint.mapPointWithGeoCoord(35.8857114,128.5828073)); //대구 광역시 북구
        marker3.setMarkerType(MapPOIItem.MarkerType.YellowPin); // 기본으로 제공하는 BluePin 마커 모양.
        marker3.setSelectedMarkerType(MapPOIItem.MarkerType.RedPin); // 마커를 클릭했을때, 기본으로 제공하는 RedPin 마커 모양.

        mapView.addPOIItem(marker3);
    }


    /**
     * 지도 위에 Circle 을 그립니다
     *
     */
    public void addCircleOverLay() {


        MapCircle circle1 = new MapCircle(
                MapPoint.mapPointWithGeoCoord(35.8900521, 128.6113282), // center
                500, // radius
                Color.argb(128, 0, 255, 0), // strokeColor
                Color.argb(128, 0, 255, 0) // fillColor
        );
        circle1.setTag(1234);
        mapView.addCircle(circle1);

        //남구
        MapCircle circle2 = new MapCircle(
                MapPoint.mapPointWithGeoCoord(35.8460224,128.59752909999997), // center - 대구광역시 남구
                2500, // radius
                Color.argb(50, 0, 255, 0), // strokeColor : green
                Color.argb(50, 0, 255, 0) // fillColor
        );
        circle2.setTag(5678);
        mapView.addCircle(circle2);

        //북구
        MapCircle circle3 = new MapCircle(
                MapPoint.mapPointWithGeoCoord(35.8857114,128.5828073), // center - 대구광역시 북구
                2500, // radius
                Color.argb(50, 255, 127, 0), // strokeColor : 주황색
                Color.argb(50, 255, 127, 0) // fillColor
        );
        mapView.addCircle(circle3);

        //동구
        MapCircle circle4 = new MapCircle(
                MapPoint.mapPointWithGeoCoord(35.8866012,128.6353024), // center - 대구광역시 동구
                2500, // radius
                Color.argb(50, 255, 0, 0), // strokeColor : red
                Color.argb(50, 255, 0, 0) // fillColor
        );
        mapView.addCircle(circle4);

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
