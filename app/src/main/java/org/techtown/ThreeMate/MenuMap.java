package org.techtown.ThreeMate;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.LayoutAnimationController;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import net.daum.mf.map.api.CalloutBalloonAdapter;
import net.daum.mf.map.api.CameraUpdateFactory;
import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapReverseGeoCoder;
import net.daum.mf.map.api.MapView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
public class MenuMap extends AppCompatActivity implements Serializable, MapView.CurrentLocationEventListener, MapReverseGeoCoder.ReverseGeoCodingResultListener, MapView.POIItemEventListener, CalloutBalloonAdapter {
    private static final String LOG_TAG = "MainActivity";
    private long backKeyPressedTime = 0;
    private Toast toast;
    RecyclerView recyclerView;
    PersonAdapter adapter;
    private GpsTracker gpsTracker;
    private String data;
    private TextView textView;
    public MapView mMapView;
    private int rtOn = 1;
    private int cfOn = 0;
    private int csOn = 0;
    ArrayList<String> placeName ;
    ArrayList<String> categoryName ;
    ArrayList<String> phone;
    ArrayList<String> place_url ;
    ArrayList<String> address_name ;
    ArrayList<String> road_address_name;
    ArrayList<String> x ;
    ArrayList<String> y ;
    ArrayList<String> matchScore ;
    final String[] words = new String[] {"??????","????????????"};

    private static final int GPS_ENABLE_REQUEST_CODE = 2001;
    private static final int PERMISSIONS_REQUEST_CODE = 100;
    String[] REQUIRED_PERMISSIONS  = {Manifest.permission.ACCESS_FINE_LOCATION};


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menumap);





        Intent intent = getIntent();
        placeName = (ArrayList<String>) intent.getSerializableExtra("placeName");
       categoryName = (ArrayList<String>) intent.getSerializableExtra("categoryName");
       phone = (ArrayList<String>) intent.getSerializableExtra("phone");
            place_url = (ArrayList<String>) intent.getSerializableExtra("place_url");
        address_name = (ArrayList<String>) intent.getSerializableExtra("address_name");
        road_address_name = (ArrayList<String>) intent.getSerializableExtra("road_address_name");
        x = (ArrayList<String>) intent.getSerializableExtra("x");
        y = (ArrayList<String>) intent.getSerializableExtra("y");
        matchScore = (ArrayList<String>) intent.getSerializableExtra("matchScore");
        mMapView = (MapView) findViewById(R.id.map_view);
        //mMapView.setDaumMapApiKey(MapApiConst.DAUM_MAPS_ANDROID_APP_API_KEY);
        mMapView.setCurrentLocationEventListener(this);
        mMapView.setPOIItemEventListener(poiItemEventListener);
        textView = findViewById(R.id.textView);
        final Button button = findViewById(R.id.button);
        final Button button2 = findViewById(R.id.button2);
        final Button button3 = findViewById(R.id.button3);


        recyclerView = findViewById(R.id.recyclerView);

        GridLayoutManager layoutManager = new GridLayoutManager(this, 1);
        recyclerView.setLayoutManager(layoutManager);

        adapter = new PersonAdapter();
        adapter.setOnItemClickListener(new OnPersonItemClickListener() {
            @Override
            public void onItemClick(PersonAdapter.ViewHolder holder, View view,  int position) {
                MapPOIItem[] mapPOIItems = mMapView.getPOIItems();
                Item item = adapter.getItem(position);
                mMapView.selectPOIItem(mapPOIItems[position],true);


            }
        });

        final AnimationSet set = new AnimationSet(true);

        Animation rtl = new TranslateAnimation(
                Animation.RELATIVE_TO_SELF,0,
                Animation.RELATIVE_TO_SELF,0,
                Animation.RELATIVE_TO_SELF,-1,
                Animation.RELATIVE_TO_SELF,0         );
        rtl.setDuration(500);
        set.addAnimation(rtl);

        Animation alpha = new AlphaAnimation(0,1);
        alpha.setDuration(700);
        set.addAnimation(alpha);

        LayoutAnimationController controller= new LayoutAnimationController(set, 0.17f);
        recyclerView.setLayoutAnimation(controller);






        if (!checkLocationServicesStatus()) {

            showDialogForLocationServiceSetting();
        }else {

            checkRunTimePermission();
        }

        gpsTracker = new GpsTracker(MenuMap.this);

        final double latitude = gpsTracker.getLatitude();
        final double longitude = gpsTracker.getLongitude();

        String address = getCurrentAddress(latitude, longitude);

        final MapPoint mapPoint = MapPoint.mapPointWithGeoCoord(Double.parseDouble(String.valueOf(latitude)), Double.parseDouble(String.valueOf(longitude)));



        button.setBackground(getDrawable(R.drawable.restaurant_btn_pressed));

        button.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        if (rtOn == 0)
                            button.setBackground(getDrawable(R.drawable.rt_toched));

                        break;
                    case MotionEvent.ACTION_UP:
                        if (rtOn ==0)
                        button.setBackground(getDrawable(R.drawable.restaurant_btn));
                        break;
                }
                return false;
            }
        });

        button2.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        if (cfOn == 0)
                        button2.setBackground(getDrawable(R.drawable.cf_touched));
                        break;
                    case MotionEvent.ACTION_UP:
                        if (cfOn == 0)
                        button2.setBackground(getDrawable(R.drawable.caffe_btn));
                        break;
                }
                return false;
            }
        });

        button3.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        if (csOn == 0)
                        button3.setBackground(getDrawable(R.drawable.cs_toiched));
                        break;
                    case MotionEvent.ACTION_UP:
                        if (csOn == 0)
                        button3.setBackground(getDrawable(R.drawable.convenience_btn));
                        break;
                }
                return false;
            }
        });







        new Thread(new Runnable() {
            @Override
            public void run() {
                Intent intent = getIntent();
                final ArrayList<String> placeName = (ArrayList<String>) intent.getSerializableExtra("placeName");
                final ArrayList<String> categoryName = (ArrayList<String>) intent.getSerializableExtra("categoryName");
                final ArrayList<String> phone = (ArrayList<String>) intent.getSerializableExtra("phone");
                final ArrayList<String> place_url = (ArrayList<String>) intent.getSerializableExtra("place_url");
                final ArrayList<String> address_name = (ArrayList<String>) intent.getSerializableExtra("address_name");
                final ArrayList<String> road_address_name = (ArrayList<String>) intent.getSerializableExtra("road_address_name");
                final ArrayList<String> x = (ArrayList<String>) intent.getSerializableExtra("x");
                final ArrayList<String> y = (ArrayList<String>) intent.getSerializableExtra("y");
                final ArrayList<String> matchScore = (ArrayList<String>) intent.getSerializableExtra("matchScore");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mMapView.moveCamera(CameraUpdateFactory.newMapPoint(mapPoint,2f));
                        for (int i = 0; i < placeName.size(); i++) {
                            MapPoint mapPoint = MapPoint.mapPointWithGeoCoord(Double.parseDouble(y.get(i)), Double.parseDouble(x.get(i)));
                            MapPOIItem marker = new MapPOIItem();
                            marker.setItemName(placeName.get(i));
                            marker.setTag(i);
                            marker.setMapPoint(mapPoint);
                            marker.setMarkerType(MapPOIItem.MarkerType.CustomImage);
                            marker.setCustomImageResourceId(R.drawable.custom_marker_fd);
                            marker.setSelectedMarkerType(MapPOIItem.MarkerType.CustomImage);
                            marker.setCustomSelectedImageResourceId(R.drawable.custom_marker_fd);
                            marker.setCustomImageAutoscale(true);
                            marker.setCustomImageAnchor(0.5f, 1.0f);
                            mMapView.addPOIItem(marker);

                            adapter.addItem(new Item(R.drawable.icon_pz_1 , placeName.get(i), categoryName.get(i), road_address_name.get(i), phone.get(i)));
                            recyclerView.setAdapter(adapter);

                        }

                    }
                });
            }
        }).start();
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rtOn=0;
                cfOn=0;
                csOn=0;
                button.setBackground(getDrawable(R.drawable.restaurant_btn));
                button2.setBackground(getDrawable(R.drawable.caffe_btn));
                button3.setBackground(getDrawable(R.drawable.convenience_btn));
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        adapter.removeItem();
                        mMapView.removeAllPOIItems();


                        data = request("??????","FD6", 1, latitude, longitude);



                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                try {
                                    button.setBackground(getDrawable(R.drawable.restaurant_btn_pressed));
                                    rtOn=1;
                                    textView.setText("");
                                    placeName.clear();
                                    categoryName.clear();
                                    address_name.clear();
                                    phone.clear();
                                    place_url.clear();
                                    road_address_name.clear();
                                    y.clear();
                                    x.clear();
                                    JSONObject json = new JSONObject(data);
                                    JSONArray array = new JSONArray(json.getString("documents"));

                                    for (int idx = 0; idx < array.length(); idx++) {
                                        JSONObject object = new JSONObject(array.get(idx).toString());
                                        String name = object.getString("place_name");
                                        String address = object.getString("address_name");
                                        String category_name = object.getString("category_name");
                                        String call = object.getString("phone");
                                        String url = object.getString("place_url");
                                        String roadAddress = object.getString("road_address_name");
                                        String longitude = object.getString("x");
                                        String latitude = object.getString("y");

                                        placeName.add(name);
                                        address_name.add(address);
                                        phone.add(call);
                                        place_url.add(url);
                                        road_address_name.add(roadAddress);
                                        categoryName.add(category_name);
                                        x.add(longitude);
                                        y.add(latitude);

                                  /*      textView.append("- ????????? : " + placeName.get(idx) + "\n" +
                                                "- ?????? ??? : " + address_name.get(idx) + "\n" +
                                                "- ???????????? : " + phone.get(idx) + "\n" +
                                                "- URL : " + place_url.get(idx) + "\n" +
                                                "- ????????? ?????? : " + road_address_name.get(idx) + "\n" +
                                                "- ?????? : " + y.get(idx) + "\n" +
                                                "- ?????? : " + x.get(idx) + "\n" + "\n");

                                   */
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }


                                for (int i = 0; i < placeName.size(); i++) {
                                    MapPoint mapPoint = MapPoint.mapPointWithGeoCoord(Double.parseDouble(y.get(i)), Double.parseDouble(x.get(i)));
                                    MapPOIItem marker = new MapPOIItem();
                                    marker.setItemName(placeName.get(i));
                                    marker.setTag(i);
                                    ;
                                    marker.setMapPoint(mapPoint);
                                    marker.setMarkerType(MapPOIItem.MarkerType.CustomImage);
                                    marker.setCustomImageResourceId(R.drawable.custom_marker_fd);
                                    marker.setSelectedMarkerType(MapPOIItem.MarkerType.CustomImage);
                                    marker.setCustomSelectedImageResourceId(R.drawable.custom_marker_fd);

                                    marker.setCustomImageAutoscale(true);
                                    marker.setCustomImageAnchor(0.5f, 1.0f);

                                    mMapView.addPOIItem(marker);
                                    adapter.addItem(new Item(R.drawable.icon_pz_1 , placeName.get(i), categoryName.get(i), road_address_name.get(i), phone.get(i)));
                                    recyclerView.setAdapter(adapter);

                                }

                            }
                        });
                    }
                }).start();
                LayoutAnimationController controller= new LayoutAnimationController(set, 0.17f);
                recyclerView.setLayoutAnimation(controller);
            }
        });

        findViewById(R.id.button2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                button.setBackground(getDrawable(R.drawable.restaurant_btn));
                button2.setBackground(getDrawable(R.drawable.caffe_btn));
                button3.setBackground(getDrawable(R.drawable.convenience_btn));
                rtOn=0;
                cfOn=0;
                csOn=0;
                mMapView.removeAllPOIItems();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        adapter.removeItem();
                        //for (int i = 1; i < 5; i++) {
                        data = request("??????","CE7", 1, latitude, longitude);


                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                try {
                                    cfOn=1;
                                    button2.setBackground(getDrawable(R.drawable.caffe_btn_pressed));
                                    //?????? ????????????

                                    textView.setText("");
                                    categoryName.clear();
                                    placeName.clear();
                                    address_name.clear();
                                    phone.clear();
                                    place_url.clear();
                                    road_address_name.clear();
                                    y.clear();
                                    x.clear();
                                    JSONObject json = new JSONObject(data);
                                    JSONArray array = new JSONArray(json.getString("documents"));

                                    for (int idx = 0; idx < array.length(); idx++) {
                                        JSONObject object = new JSONObject(array.get(idx).toString());
                                        String name = object.getString("place_name");
                                        String address = object.getString("address_name");
                                        String category_name = object.getString("category_name");
                                        String call = object.getString("phone");
                                        String url = object.getString("place_url");
                                        String roadAddress = object.getString("road_address_name");
                                        String longitude = object.getString("x");
                                        String latitude = object.getString("y");

                                        placeName.add(name);
                                        address_name.add(address);
                                        phone.add(call);
                                        place_url.add(url);
                                        road_address_name.add(roadAddress);
                                        categoryName.add(category_name);
                                        x.add(longitude);
                                        y.add(latitude);
                                        adapter.addItem(new Item(R.drawable.icon_pz_2,placeName.get(idx), categoryName.get(idx), road_address_name.get(idx), phone.get(idx)));
                                        recyclerView.setAdapter(adapter);
                                      /*  textView.append("- ????????? : " + placeName.get(idx) + "\n" +
                                                "- ?????? ??? : " + address_name.get(idx) + "\n" +
                                                "- ???????????? : " + phone.get(idx) + "\n" +
                                                "- URL : " + place_url.get(idx) + "\n" +
                                                "- ????????? ?????? : " + road_address_name.get(idx) + "\n" +
                                                "- ?????? : " + y.get(idx) + "\n" +
                                                "- ?????? : " + x.get(idx) + "\n" + "\n");

                                       */
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }


                                for (int i = 0; i < placeName.size(); i++) {
                                    MapPoint mapPoint = MapPoint.mapPointWithGeoCoord(Double.parseDouble(y.get(i)), Double.parseDouble(x.get(i)));
                                    MapPOIItem marker = new MapPOIItem();
                                    marker.setItemName(placeName.get(i));
                                    marker.setTag(i);
                                    marker.setMapPoint(mapPoint);
                                    marker.setMarkerType(MapPOIItem.MarkerType.CustomImage);
                                    marker.setCustomImageResourceId(R.drawable.custom_marker_cf);
                                    marker.setSelectedMarkerType(MapPOIItem.MarkerType.CustomImage);
                                    marker.setCustomSelectedImageResourceId(R.drawable.custom_marker_cf);

                                    marker.setCustomImageAutoscale(true);
                                    marker.setCustomImageAnchor(0.5f, 1.0f);

                                    mMapView.addPOIItem(marker);

                                }

                            }
                        });
                    }
                    //   }
                }).start();
                LayoutAnimationController controller= new LayoutAnimationController(set, 0.17f);
                recyclerView.setLayoutAnimation(controller);
            }
        });

        findViewById(R.id.button3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMapView.removeAllPOIItems();
                button.setBackground(getDrawable(R.drawable.restaurant_btn));
                button2.setBackground(getDrawable(R.drawable.caffe_btn));
                button3.setBackground(getDrawable(R.drawable.convenience_btn));
                rtOn=0;
                cfOn=0;
                csOn=0;
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        adapter.removeItem();
                        recyclerView.removeAllViewsInLayout();
                        // for (int i = 1; i < 5; i++) { ?????? ?????? 30????????? ???????????? ????????? ?????? ??????????????? ??????.
                        data = request("?????????","CS2", 1, latitude, longitude);


                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                try {
                                    csOn=1;
                                    button3.setBackground(getDrawable(R.drawable.convenience_btn_pressed));
                                    textView.setText("");
                                    placeName.clear();
                                    categoryName.clear();
                                    address_name.clear();
                                    phone.clear();
                                    place_url.clear();
                                    road_address_name.clear();
                                    y.clear();
                                    x.clear();
                                    JSONObject json = new JSONObject(data);
                                    JSONArray array = new JSONArray(json.getString("documents"));

                                    for (int idx = 0; idx < array.length(); idx++) {
                                        JSONObject object = new JSONObject(array.get(idx).toString());
                                        String name = object.getString("place_name");
                                        String address = object.getString("address_name");
                                        String call = object.getString("phone");
                                        String category_name = object.getString("category_name");
                                        String url = object.getString("place_url");
                                        String roadAddress = object.getString("road_address_name");
                                        String longitude = object.getString("x");
                                        String latitude = object.getString("y");

                                        placeName.add(name);
                                        address_name.add(address);
                                        phone.add(call);
                                        place_url.add(url);
                                        road_address_name.add(roadAddress);
                                        categoryName.add(category_name);
                                        x.add(longitude);
                                        y.add(latitude);
                                        adapter.addItem(new Item(R.drawable.icon_pz_3,placeName.get(idx), categoryName.get(idx), road_address_name.get(idx), phone.get(idx)));
                                        recyclerView.setAdapter(adapter);
                                    /**    textView.append("- ????????? : " + placeName.get(idx) + "\n" +
                                      *          "- ?????? ??? : " + address_name.get(idx) + "\n" +
                                      *          "- ???????????? : " + phone.get(idx) + "\n" +
                                      *          "- URL : " + place_url.get(idx) + "\n" +
                                      *          "- ????????? ?????? : " + road_address_name.get(idx) + "\n" +
                                      *          "- ?????? : " + y.get(idx) + "\n" +
                                      *          "- ?????? : " + x.get(idx) + "\n" + "\n");
                                     */
                                    }

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }


                                for (int i = 0; i < placeName.size(); i++) {
                                    MapPoint mapPoint = MapPoint.mapPointWithGeoCoord(Double.parseDouble(y.get(i)), Double.parseDouble(x.get(i)));
                                    MapPOIItem marker = new MapPOIItem();
                                    marker.setItemName(placeName.get(i));
                                    marker.setTag(i);
                                    marker.setMapPoint(mapPoint);
                                    marker.setMarkerType(MapPOIItem.MarkerType.CustomImage);
                                    marker.setCustomImageResourceId(R.drawable.custom_marker_hr);
                                    marker.setSelectedMarkerType(MapPOIItem.MarkerType.CustomImage);
                                    marker.setCustomSelectedImageResourceId(R.drawable.custom_marker_hr);

                                    marker.setCustomImageAutoscale(true);
                                    marker.setCustomImageAnchor(0.5f, 1.0f);

                                    mMapView.addPOIItem(marker);

                                }

                            }
                        });
                    }
                    // }
                }).start();
                LayoutAnimationController controller= new LayoutAnimationController(set, 0.17f);
                recyclerView.setLayoutAnimation(controller);
            }
        });




    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOff);
        mMapView.setShowCurrentLocationMarker(false);
    }

    @Override
    public void onCurrentLocationUpdate(MapView mapView, MapPoint currentLocation, float accuracyInMeters) {
        MapPoint.GeoCoordinate mapPointGeo = currentLocation.getMapPointGeoCoord();
        Log.i(LOG_TAG, String.format("MapView onCurrentLocationUpdate (%f,%f) accuracy (%f)", mapPointGeo.latitude, mapPointGeo.longitude, accuracyInMeters));
    }


    @Override
    public void onCurrentLocationDeviceHeadingUpdate(MapView mapView, float v) {

    }

    @Override
    public void onCurrentLocationUpdateFailed(MapView mapView) {

    }

    @Override
    public void onCurrentLocationUpdateCancelled(MapView mapView) {

    }

    @Override
    public void onReverseGeoCoderFoundAddress(MapReverseGeoCoder mapReverseGeoCoder, String s) {
        mapReverseGeoCoder.toString();
        onFinishReverseGeoCoding(s);
    }

    @Override
    public void onReverseGeoCoderFailedToFindAddress(MapReverseGeoCoder mapReverseGeoCoder) {
        onFinishReverseGeoCoding("Fail");
    }

    private void onFinishReverseGeoCoding(String result) {
//        Toast.makeText(LocationDemoActivity.this, "Reverse Geo-coding : " + result, Toast.LENGTH_SHORT).show();
    }




    /*
     * ActivityCompat.requestPermissions??? ????????? ????????? ????????? ????????? ???????????? ??????????????????.
     */
    @Override
    public void onRequestPermissionsResult(int permsRequestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grandResults) {

        if ( permsRequestCode == PERMISSIONS_REQUEST_CODE && grandResults.length == REQUIRED_PERMISSIONS.length) {

            // ?????? ????????? PERMISSIONS_REQUEST_CODE ??????, ????????? ????????? ???????????? ??????????????????

            boolean check_result = true;


            // ?????? ???????????? ??????????????? ???????????????.

            for (int result : grandResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    check_result = false;
                    break;
                }
            }


            if ( check_result ) {
                Log.d("@@@", "start");
                //?????? ?????? ????????? ??? ??????
                mMapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOnWithHeading);
            }
            else {
                // ????????? ???????????? ????????? ?????? ????????? ??? ?????? ????????? ??????????????? ?????? ???????????????.2 ?????? ????????? ????????????.

                if (ActivityCompat.shouldShowRequestPermissionRationale(this, REQUIRED_PERMISSIONS[0])) {

                    Toast.makeText(MenuMap.this, "???????????? ?????????????????????. ?????? ?????? ???????????? ???????????? ??????????????????.", Toast.LENGTH_LONG).show();
                    finish();


                }else {

                    Toast.makeText(MenuMap.this, "???????????? ?????????????????????. ??????(??? ??????)?????? ???????????? ???????????? ?????????. ", Toast.LENGTH_LONG).show();

                }
            }

        }
    }

    void checkRunTimePermission(){

        //????????? ????????? ??????
        // 1. ?????? ???????????? ????????? ????????? ???????????????.
        int hasFineLocationPermission = ContextCompat.checkSelfPermission(MenuMap.this,
                Manifest.permission.ACCESS_FINE_LOCATION);


        if (hasFineLocationPermission == PackageManager.PERMISSION_GRANTED ) {

            // 2. ?????? ???????????? ????????? ?????????
            // ( ??????????????? 6.0 ?????? ????????? ????????? ???????????? ???????????? ????????? ?????? ????????? ?????? ???????????????.)


            // 3.  ?????? ?????? ????????? ??? ??????
            mMapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOnWithHeading);


        } else {  //2. ????????? ????????? ????????? ?????? ????????? ????????? ????????? ???????????????. 2?????? ??????(3-1, 4-1)??? ????????????.

            // 3-1. ???????????? ????????? ????????? ??? ?????? ?????? ????????????
            if (ActivityCompat.shouldShowRequestPermissionRationale(MenuMap.this, REQUIRED_PERMISSIONS[0])) {

                // 3-2. ????????? ???????????? ?????? ?????????????????? ???????????? ????????? ????????? ???????????? ????????? ????????????.
                Toast.makeText(MenuMap.this, "??? ?????? ??????????????? ?????? ?????? ????????? ???????????????.", Toast.LENGTH_LONG).show();
                // 3-3. ??????????????? ????????? ????????? ?????????. ?????? ????????? onRequestPermissionResult?????? ???????????????.
                ActivityCompat.requestPermissions(MenuMap.this, REQUIRED_PERMISSIONS,
                        PERMISSIONS_REQUEST_CODE);


            } else {
                // 4-1. ???????????? ????????? ????????? ??? ?????? ?????? ???????????? ????????? ????????? ?????? ?????????.
                // ?????? ????????? onRequestPermissionResult?????? ???????????????.
                ActivityCompat.requestPermissions(MenuMap.this, REQUIRED_PERMISSIONS,
                        PERMISSIONS_REQUEST_CODE);
            }

        }

    }
    public String getCurrentAddress( double latitude, double longitude) {

        //????????????... GPS??? ????????? ??????
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());

        List<Address> addresses;

        try {

            addresses = geocoder.getFromLocation(
                    latitude,
                    longitude,
                    7);
        } catch (IOException ioException) {
            //???????????? ??????
            Toast.makeText(this, "???????????? ????????? ????????????", Toast.LENGTH_LONG).show();
            return "???????????? ????????? ????????????";
        } catch (IllegalArgumentException illegalArgumentException) {
            Toast.makeText(this, "????????? GPS ??????", Toast.LENGTH_LONG).show();
            return "????????? GPS ??????";

        }



        if (addresses == null || addresses.size() == 0) {
            Toast.makeText(this, "?????? ?????????", Toast.LENGTH_LONG).show();
            return "?????? ?????????";

        }

        Address address = addresses.get(0);
        return address.getAddressLine(0).toString()+"\n";

    }


    //??????????????? GPS ???????????? ?????? ????????????
    private void showDialogForLocationServiceSetting() {

        AlertDialog.Builder builder = new AlertDialog.Builder(MenuMap.this);
        builder.setTitle("?????? ????????? ????????????");
        builder.setMessage("?????? ???????????? ???????????? ?????? ???????????? ???????????????.\n"
                + "?????? ????????? ???????????????????");
        builder.setCancelable(true);
        builder.setPositiveButton("??????", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                Intent callGPSSettingIntent
                        = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivityForResult(callGPSSettingIntent, GPS_ENABLE_REQUEST_CODE);
            }
        });
        builder.setNegativeButton("??????", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        builder.create().show();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {

            case GPS_ENABLE_REQUEST_CODE:

                //???????????? GPS ?????? ???????????? ??????
                if (checkLocationServicesStatus()) {
                    if (checkLocationServicesStatus()) {

                        Log.d("@@@", "onActivityResult : GPS ????????? ?????????");
                        checkRunTimePermission();
                        return;
                    }
                }

                break;
        }
    }

    public boolean checkLocationServicesStatus() {
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }




    private static String request(String quary, String category_group_code, int page, double latitude, double longitude) {
        StringBuilder output = new StringBuilder();
        String line = null;
        try {
            URL url = new URL("http://dapi.kakao.com/v2/local/search/keyword.json?query=" + quary +
                    "&category_group_code=" + category_group_code +
                    "&page=" + page + "&size=15&sort=distance" +
                    "&x=" + longitude + "&y=" + latitude);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            if (conn != null) {
                conn.setConnectTimeout(3 * 1000);
                conn.setReadTimeout(3 * 1000);
                conn.setRequestMethod("GET");
                conn.setDoInput(true);
                conn.addRequestProperty("Authorization", "KakaoAK " + MapApiConst.DAUM_MAPS_ANDROID_APP_API_KEY); //key??? ??????
                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));

                while (true) {
                    line = reader.readLine();
                    if (line == null) {
                        break;
                    }

                    output.append(line + "\n");
                }
                reader.close();
                conn.disconnect();
            }


        } catch (Exception ex) {
            Log.d("Lee", "?????? ????????? : " + ex.toString());
        }

        Log.d("Lee", "?????? -> " + output.toString());

        return output.toString();

    }


    @Override
    public void onPOIItemSelected(MapView mapView, MapPOIItem mapPOIItem) {
     ;
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

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
    @Override
    public void onBackPressed() {
        if (System.currentTimeMillis() > backKeyPressedTime + 2000) {
            backKeyPressedTime = System.currentTimeMillis();
            toast = Toast.makeText(this, "\'??????\' ????????? ?????? ??? ???????????? ???????????????.", Toast.LENGTH_SHORT);
            toast.show();
            return;
        }
        // ??????????????? ???????????? ????????? ????????? ????????? 2?????? ?????? ??????????????? ?????? ???
        // ??????????????? ???????????? ????????? ????????? ????????? 2?????? ????????? ???????????? ??????
        // ?????? ????????? Toast ??????
        if (System.currentTimeMillis() <= backKeyPressedTime + 2000) {

            finish();
            toast.cancel();
        }

    }

    @Override
    public View getCalloutBalloon(MapPOIItem mapPOIItem) {
        return null;
    }

    @Override
    public View getPressedCalloutBalloon(MapPOIItem mapPOIItem) {
        return null;
    }



    private MapView.POIItemEventListener poiItemEventListener = new MapView.POIItemEventListener() {
        @Override
        public void onPOIItemSelected(MapView mapView, MapPOIItem mapPOIItem) {
        }

        @Override
        public void onCalloutBalloonOfPOIItemTouched(MapView mapView, MapPOIItem mapPOIItem) {


        }

        @Override
        public void onCalloutBalloonOfPOIItemTouched(MapView mapView, MapPOIItem mapPOIItem, MapPOIItem.CalloutBalloonButtonType calloutBalloonButtonType) {
            new AlertDialog.Builder(MenuMap.this).setItems(words, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which) {
                        case 0:
                            Toast.makeText(getApplicationContext(), mapPOIItem.getItemName() + " ??????", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(place_url.get(mapPOIItem.getTag())));
                            startActivity(intent);
                            break;
                        case 1:
                            if (phone.get(mapPOIItem.getTag()).equals(null)||phone.get(mapPOIItem.getTag()).equals("")){
                                Toast.makeText(getApplicationContext(),"'" + mapPOIItem.getItemName()+ "' ????????? ??????????????? ?????? ??? ????????????.", Toast.LENGTH_SHORT).show();
                            }else{
                                Toast.makeText(getApplicationContext(), mapPOIItem.getItemName() + "??? ????????????", Toast.LENGTH_SHORT).show();
                                Intent intent2 = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phone.get(mapPOIItem.getTag())));
                                startActivity(intent2);
                            }


                    }

                }
            }).show();




        }

        @Override
        public void onDraggablePOIItemMoved(MapView mapView, MapPOIItem mapPOIItem, MapPoint mapPoint) {

        }
    };


}


