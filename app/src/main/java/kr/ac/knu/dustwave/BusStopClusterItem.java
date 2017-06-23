package kr.ac.knu.dustwave;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

/**
 * Created by nicewoong on 2017. 6. 23..
 */

public class BusStopClusterItem implements ClusterItem {
    private LatLng location;
    private String address;


    public BusStopClusterItem(LatLng location) {
        this.location = location;
    }


    public BusStopClusterItem(LatLng location, String address) {
        this.location = location;
        this.address = address;
    }

    public LatLng getLocation() {
        return location;
    }

    public void setLocation(LatLng location) {
        this.location = location;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @Override
    public LatLng getPosition() {
        return location;
    }

}
