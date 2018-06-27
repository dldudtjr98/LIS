package com.dldud.riceapp;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapView;

import java.util.concurrent.ExecutionException;

import static com.dldud.riceapp.MainActivity.navigation;


/**
 * A simple {@link Fragment} subclass.
 */
public class PageMapFragment extends Fragment implements MapView.POIItemEventListener{

    String myString;
    String PageIdx;
    int linearNum;

    String[] idx, locationLat, locationLong;
    String strPointLat,strPointLong;
    double pointLat,pointLong;


    public PageMapFragment() {
        // Required empty public constructor
    }

    public static PageMapFragment newInstance(){
        Bundle args = new Bundle();

        PageMapFragment fragment = new PageMapFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_page_map, container, false);
        //Feed Idx
        Intent intent = getActivity().getIntent();
        PageIdx = intent.getStringExtra("position");

        try {
            Task task = new Task();
            myString = task.execute("http://52.78.18.156/public/ping_db.php").get();
            task.jsonParser(myString);

            //list to array
            idx = task.idx.toArray(new String[task.idx.size()]);
            locationLat = task.locationlat.toArray(new String[task.locationlat.size()]);
            locationLong = task.locationlong.toArray(new String[task.locationlong.size()]);

            linearNum = idx.length;

            for(int i = 0 ; i < linearNum ; i++){
                if(PageIdx.equals(idx[i])){
                    strPointLat = locationLat[i];
                    strPointLong = locationLong[i];

                    break;
                }
            }

        }catch(ExecutionException e){
            e.printStackTrace();
        }catch(InterruptedException e){
            e.printStackTrace();
        }
        pointLat = Double.parseDouble(strPointLat);
        pointLong = Double.parseDouble(strPointLong);
        MapView mapView = new MapView(getActivity());

        ViewGroup mapViewContainer = (ViewGroup) v.findViewById(R.id.map_view);
        mapViewContainer.addView(mapView);
        mapView.setPOIItemEventListener(this);
        mapView.removeAllPOIItems();
        mapView.setMapCenterPoint(MapPoint.mapPointWithGeoCoord(pointLat,pointLong), true);
        mapView.setZoomLevel(1, true);

        MapPOIItem customMarker = new MapPOIItem();
        customMarker.setItemName("Custom Marker");
        customMarker.setTag(1);
        customMarker.setMapPoint(MapPoint.mapPointWithGeoCoord(pointLat,pointLong));
        customMarker.setMarkerType(MapPOIItem.MarkerType.CustomImage); // 마커타입을 커스텀 마커로 지정.
        customMarker.setCustomImageResourceId(R.drawable.marker_red); // 마커 이미지.
        customMarker.setCustomImageAutoscale(false); // hdpi, xhdpi 등 안드로이드 플랫폼의 스케일을 사용할 경우 지도 라이브러리의 스케일 기능을 꺼줌.
        customMarker.setCustomImageAnchor(0.5f, 1.0f); // 마커 이미지중 기준이 되는 위치(앵커포인트) 지정 - 마커 이미지 좌측 상단 기준 x(0.0f ~ 1.0f), y(0.0f ~ 1.0f) 값.

        mapView.addPOIItem(customMarker);

        return v;
    }

    @Override
    public void onPOIItemSelected(net.daum.mf.map.api.MapView mapView, MapPOIItem mapPOIItem) {

    }

    @Override
    public void onCalloutBalloonOfPOIItemTouched(net.daum.mf.map.api.MapView mapView, MapPOIItem mapPOIItem) {

    }

    @Override
    public void onCalloutBalloonOfPOIItemTouched(net.daum.mf.map.api.MapView mapView, MapPOIItem mapPOIItem, MapPOIItem.CalloutBalloonButtonType calloutBalloonButtonType) {

    }

    @Override
    public void onDraggablePOIItemMoved(net.daum.mf.map.api.MapView mapView, MapPOIItem mapPOIItem, MapPoint mapPoint) {

    }

    @Override
    public void onDestroy(){
        super.onDestroy();

    }
}
