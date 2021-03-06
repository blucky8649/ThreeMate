package org.techtown.ThreeMate;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;

import com.bumptech.glide.Glide;
import com.google.firebase.FirebaseApp;
import com.google.firebase.appcheck.FirebaseAppCheck;
import com.google.firebase.appcheck.safetynet.SafetyNetAppCheckProviderFactory;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import net.daum.mf.map.api.MapPOIItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

import static java.lang.String.valueOf;

public class MainActivity extends Activity implements TextWatcher {
    private int dlgCode = 0;
    private long backKeyPressedTime = 0;
    private Toast toast;
    private Button quizbtn, diarybtn, mapbtn, roulbtn;
    //????????????, ????????????, ??????, ?????? ??????
    //private AutoCompleteTextView autoComplete;
    private TextView hidden;
    private ArrayList<String> foodOneTime = new ArrayList<String>();
    private CardView cv_userInfo;
    private TextView tv;
    private Button search;
    private GpsTracker gpsTracker;
    private TextView tv_age;
    private TextView tv_bmi;
    private TextView tv_bmr;
    private TextView tv_exer;
    String url = "https://place.map.kakao.com/18992342";

    MapPOIItem marker = new MapPOIItem();
    private ArrayList<String> matchFoods = new ArrayList<String>();
    private ArrayList<String> matchFoods2 = new ArrayList<String>();
    private ArrayList<String> imageurl = new ArrayList<String>();
    private ArrayList<String> placeName = new ArrayList<String>();
    private ArrayList<String> address_name = new ArrayList<String>();
    private ArrayList<String> categoryName = new ArrayList<String>();
    private ArrayList<String> phone = new ArrayList<String>();
    private ArrayList<String> place_url = new ArrayList<String>();
    private ArrayList<String> road_address_name = new ArrayList<String>();
    private ArrayList<String> x = new ArrayList<String>();
    private ArrayList<String> y = new ArrayList<String>();
    private ArrayList<String> matchScore = new ArrayList<String>();
    String data;
    private String name2;
    private String image;

    /**
     * ????????? ??????
     */
    private FirebaseAuth auth; // ????????? ????????? ?????? ??????
    private FirebaseUser user;
    private FirebaseDatabase database;
    private DatabaseReference databaseReference;
    private FirebaseStorage storage;
    private StorageReference storageRef;
    private Uri filePath;
    private String userUID;
    private String userName;
    private String userProfile;
    private String userEmail;
    private String bornDate;
    private String gender;
    private String bodyLength;
    private String bodyWeight;
    private SQLiteManager sqLiteManager;
    private ArrayList<String> spy = new ArrayList<String>();
    private TextView tv_userInfo;
    private Button btn_logout;
    private CircleImageView iv_profile;
    private TextView tv_nickname;
    private int age;
    private String bmr;
    private String exer;
    private String dailyKcal;

    String msg = "0";
    String url2 = "https://search.naver.com/search.naver?where=nexearch&sm=top_hty&fbm=1&ie=utf8&query=%EC%BD%94%EB%A1%9C%EB%82%98+19";
    String ii = ".message_area";
    final Bundle bundle = new Bundle();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FirebaseApp.initializeApp(/*context=*/ this);
        FirebaseAppCheck firebaseAppCheck = FirebaseAppCheck.getInstance();
        firebaseAppCheck.installAppCheckProviderFactory(
                SafetyNetAppCheckProviderFactory.getInstance());



        sqLiteManager = new SQLiteManager(getApplicationContext(), "ThreeMate.db", null, 1);
        auth = FirebaseAuth.getInstance(); // ?????????????????? ?????? ?????? ?????????.
        user = auth.getCurrentUser();
        userUID = user.getUid();
        userProfile = user.getPhotoUrl().toString();
        userName = user.getDisplayName();
        userEmail = user.getEmail();
        gpsTracker = new GpsTracker(MainActivity.this);





        firebaseUpdate();


        final double latitude = gpsTracker.getLatitude();
        final double longitude = gpsTracker.getLongitude();

        String address = getCurrentAddress(latitude, longitude);

        cv_userInfo = findViewById(R.id.cv_userInfo);
        tv_age = findViewById(R.id.tv_age);
        tv_bmi = findViewById(R.id.tv_bmi);
        tv_bmr = findViewById(R.id.tv_bmr);
        tv_exer = findViewById(R.id.tv_exer);
        tv_userInfo = findViewById(R.id.tv_userInfo);
        tv_nickname = findViewById(R.id.tv_nickname);
        iv_profile = findViewById(R.id.iv_profile);
        btn_logout = findViewById(R.id.btn_logout);
        updateUserInfo();


        hidden = findViewById(R.id.hidden);
        search = findViewById(R.id.search);

        btn_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                auth.signOut();
                sqLiteManager.deleteAll();
                Toast.makeText(getApplicationContext(),"???????????? ???????????????.",Toast.LENGTH_SHORT).show();
                Intent intent1 = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent1);
                finish();
            }
        });
        cv_userInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CustomDialog customDialog = new CustomDialog(MainActivity.this);
                customDialog.show();
            }
        });



        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(getApplicationContext(), SearchActivity.class);
                myIntent.putExtra("matchFoods", matchFoods);
                myIntent.putExtra("matchFoods2", matchFoods2);
                myIntent.putExtra("imageurl", imageurl);
                myIntent.putExtra("foodOneTime", foodOneTime);

                startActivity(myIntent);
            }
        });
        search.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                Animation anim = AnimationUtils.loadAnimation
                        (getApplicationContext(), // ??????????????? ????????????
                                R.anim.scale_anim2);
                anim.setFillAfter(true);
                Animation anim2 = AnimationUtils.loadAnimation
                        (getApplicationContext(), // ??????????????? ????????????
                                R.anim.scale_anim);
                anim2.setFillAfter(true);
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:

                        search.startAnimation(anim);
                        break;
                    case MotionEvent.ACTION_UP:
                        search.startAnimation(anim2);
                        break;
                }
                return false;
            }
        });

        quizbtn = findViewById(R.id.button1);
        quizbtn.setOnClickListener(new View.OnClickListener() {
            //???????????? ??????
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, menuQuiz.class);
                startActivity(intent);

            }
        });
        quizbtn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                Animation anim = AnimationUtils.loadAnimation
                        (getApplicationContext(), // ??????????????? ????????????
                                R.anim.scale_anim2);
                anim.setFillAfter(true);
                Animation anim2 = AnimationUtils.loadAnimation
                        (getApplicationContext(), // ??????????????? ????????????
                                R.anim.scale_anim);
                anim2.setFillAfter(true);
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:

                        quizbtn.startAnimation(anim);
                        break;
                    case MotionEvent.ACTION_UP:
                        quizbtn.startAnimation(anim2);
                        break;
                }
                return false;
            }
        });

        diarybtn = findViewById(R.id.button2);
        diarybtn.setOnClickListener(new View.OnClickListener() {
            //???????????? ??????
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, Diary.class);

                startActivity(intent);
            }
        });
        diarybtn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                Animation anim = AnimationUtils.loadAnimation
                        (getApplicationContext(), // ??????????????? ????????????
                                R.anim.scale_anim2);
                anim.setFillAfter(true);
                Animation anim2 = AnimationUtils.loadAnimation
                        (getApplicationContext(), // ??????????????? ????????????
                                R.anim.scale_anim);
                anim2.setFillAfter(true);
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:

                        diarybtn.startAnimation(anim);
                        break;
                    case MotionEvent.ACTION_UP:
                        diarybtn.startAnimation(anim2);
                        break;
                }
                return false;
            }
        });

        new Thread(new Runnable() {
            @Override
            public void run() {


                data = request("FD6", 1, latitude, longitude);


                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        try {

                            placeName.clear();
                            categoryName.clear();
                            address_name.clear();
                            phone.clear();
                            place_url.clear();
                            road_address_name.clear();
                            y.clear();
                            x.clear();
                            matchScore.clear();
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
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });

            }

        }).start();

        mapbtn = findViewById(R.id.button3);
        mapbtn.setOnClickListener(new View.OnClickListener() {
            //?????? ??????
            @Override
            public void onClick(View view) {



                Intent myIntent = new Intent(MainActivity.this, MenuMap.class);
                myIntent.putExtra("placeName", placeName);
                myIntent.putExtra("address_name", address_name);
                myIntent.putExtra("phone", phone);
                myIntent.putExtra("place_url", place_url);
                myIntent.putExtra("road_address_name", road_address_name);
                myIntent.putExtra("categoryName", categoryName);
                myIntent.putExtra("x", x);
                myIntent.putExtra("y", y);
                myIntent.putExtra("matchScore", matchScore);
                startActivity(myIntent);
            }


        });
        mapbtn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                Animation anim = AnimationUtils.loadAnimation
                        (getApplicationContext(), // ??????????????? ????????????
                                R.anim.scale_anim2);
                anim.setFillAfter(true);
                Animation anim2 = AnimationUtils.loadAnimation
                        (getApplicationContext(), // ??????????????? ????????????
                                R.anim.scale_anim);
                anim2.setFillAfter(true);
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:

                        mapbtn.startAnimation(anim);
                        break;
                    case MotionEvent.ACTION_UP:
                        mapbtn.startAnimation(anim2);
                        break;
                }
                return false;
            }
        });

        roulbtn = findViewById(R.id.button4);
        roulbtn.setOnClickListener(new View.OnClickListener() {
            //?????? ??????
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, RouletteActivity.class);
                startActivity(intent);

            }
        });
        roulbtn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                Animation anim = AnimationUtils.loadAnimation
                        (getApplicationContext(), // ??????????????? ????????????
                                R.anim.scale_anim2);
                anim.setFillAfter(true);
                Animation anim2 = AnimationUtils.loadAnimation
                        (getApplicationContext(), // ??????????????? ????????????
                                R.anim.scale_anim);
                anim2.setFillAfter(true);
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:

                        roulbtn.startAnimation(anim);
                        break;
                    case MotionEvent.ACTION_UP:
                        roulbtn.startAnimation(anim2);
                        break;
                }
                return false;
            }
        });


        try {
            JSONObject json = new JSONObject(getJsonString(this));      // json ????????? ???????????? Object????????? ??????
            JSONArray array = new JSONArray(json.getString("??????"));      // json ????????? "??????"??? ???????????? object??? JsonArray ????????? ??????

            // json "??????"??? ?????? ????????? ????????? ??????.
            // ?????? ????????? ????????? ???????????? hot, sugar, solt, food ?????? ?????? ????????? ????????????, ????????? matchFoods??? ??????.
            for (int idx = 0; idx < array.length(); idx++) {
                JSONObject object = new JSONObject(array.get(idx).toString());
                String name = object.getString("name");
                String image = object.getString("image");
                String name2 = object.getString("name2");


                if (name.length() >= 1) {
                    matchFoods.add(name);
                    matchFoods2.add(name2);
                    imageurl.add(image);
                }


            }


            AlertDialog.Builder builder = new AlertDialog.Builder(this);

            // ????????? ????????? ???????????? ?????????
            if (matchFoods.size() > 0) {

                for (int i = 0; i < matchFoods.size(); i++) {
                    int j = (int) (Math.random() * matchFoods.size());
                    String tmp = "";

                    tmp = matchFoods.get(i);
                    matchFoods.set(i, matchFoods.get(j));
                    matchFoods.set(j, tmp);

                    tmp = matchFoods2.get(i);
                    matchFoods2.set(i, matchFoods2.get(j));
                    matchFoods2.set(j, tmp);

                    tmp = imageurl.get(i);
                    imageurl.set(i, imageurl.get(j));
                    imageurl.set(j, tmp);

                }
           /*      System.out.println("Start..." + new Date());
                // delay 5 seconds
                Thread.sleep(1300);

            */



            }
            // ????????? ????????? ?????????

        } catch (JSONException e) {
            Log.i("lee", "error2: " + e);
        }



    }

    @Override
    public void onBackPressed() {
        // ?????? ???????????? ????????? ????????? ???????????? ???????????? ?????? ??????
        // super.onBackPressed();

        // ??????????????? ???????????? ????????? ????????? ????????? 2?????? ?????? ??????????????? ?????? ???
        // ??????????????? ???????????? ????????? ????????? ????????? 2?????? ???????????? Toast Show
        // 2000 milliseconds = 2 seconds
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

    public void afterTextChanged(Editable arg0) {
        // TODO Auto-generated method stub

    }

    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        // TODO Auto-generated method stub

    }

    public void onTextChanged(CharSequence s, int start, int before, int count) {
        // TODO Auto-generated method stub

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

    private static String getJsonString(Context context){
        String json = "";
        try {
            InputStream is = context.getAssets().open("jsons/test.json");
            int fileSize = is.available();

            byte[] buffer = new byte[fileSize];
            is.read(buffer);
            is.close();

            json = new String(buffer, "UTF-8");
        }
        catch (IOException ex)
        {
            ex.printStackTrace();
        }
        return json;
    }


    private static String request(String category_group_code, int page, double latitude, double longitude) {
        StringBuilder output = new StringBuilder();
        String line = null;
        try {
            URL url = new URL("http://dapi.kakao.com/v2/local/search/category.json?category_group_code="+category_group_code+
                    "&page=" + page + "&size=15&sort=distance" +
                    "&x=" + longitude + "&y=" + latitude);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            if (conn != null) {
                conn.setConnectTimeout(3 * 1000);
                conn.setReadTimeout(3 * 1000);
                conn.setRequestMethod("GET");
                conn.setDoInput(true);
                conn.addRequestProperty("Authorization", "KakaoAK " + MapApiConst.DAUM_MAPS_ANDROID_APP_API_KEY); //key??? ??????
                int resCode = conn.getResponseCode();
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



    /*
     * ActivityCompat.requestPermissions??? ????????? ????????? ????????? ????????? ???????????? ??????????????????.
     */

    private void updateUserInfo() {
        spy.clear();
        ArrayList<JSONObject> array = sqLiteManager.getResultUser(); // DB??? ????????? ??????????????? ?????? ????????????
        try {
            Calendar current = Calendar.getInstance();
            int currentYear = current.get(Calendar.YEAR);
            int currentMonth = current.get(Calendar.MONTH)+1;
            int currentDay = current.get(Calendar.DAY_OF_MONTH);
            database = FirebaseDatabase.getInstance(); // ?????????????????? ?????????????????? ??????
            databaseReference = database.getReference(userUID); // DB ????????? ??????
            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    // ?????????????????? ????????????????????? ???????????? ???????????? ???
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) { // ??????????????? ????????? List??? ????????????
                        User user = snapshot.getValue(User.class); // ??????????????? User ????????? ???????????? ?????????.
                        if (user.getBodyLength() != null){
                            userUID = user.getUserUID();
                            userName = user.getUserName();
                            userProfile = user.getUserProfile();;
                            userEmail = user.getUserEmail();
                            bornDate = user.getBornDate();
                            gender = user.getGender();
                            bodyLength= user.getBodyLength();
                            bodyWeight = user.getBodyWeight();
                            exer = user.getExercise();

                            sqLiteManager.insertUser(
                                    userName,
                                    userProfile,
                                    bornDate,
                                    gender,
                                    bodyLength,
                                    bodyWeight,
                                    exer);

                            spy.add(userName);
                            int birthYear = Integer.valueOf(bornDate.substring(0,4));
                            int birthMonth = Integer.valueOf(bornDate.substring(5,7));
                            int birthDay = Integer.valueOf(bornDate.substring(8,10));
                            age = currentYear-birthYear;
                            if (birthMonth * 100 + birthDay >= currentMonth * 100 + currentDay){
                                age = currentYear-birthYear-1;
                            }
                            Log.d("Lee", String.valueOf(birthMonth * 100 + birthDay) + String.valueOf(currentMonth * 100 + currentDay));
                            Double bmi =  Double.valueOf(bodyWeight) / ((Double.valueOf(bodyLength)/100) *  (Double.valueOf(bodyLength)/100)) ;
                             bmr = gender.equals("???")?String.format("%.2f",(66.47+(13.75*Double.valueOf(bodyWeight) )+(5*Double.valueOf(bodyLength)) - (6.76 * Double.valueOf(age))))
                                    :String.format("%.2f",(665.1+(9.56*Double.valueOf(bodyWeight) )+(1.85*Double.valueOf(bodyLength)) - (4.68 * Double.valueOf(age))));
                            Log.d("Lee", bodyWeight+ String.valueOf((Double.valueOf(bodyLength)/10) *  (Double.valueOf(bodyLength)/10)) );
                            if (bmi > 26.35){
                                tv_bmi.setText("BMI : " + String.format("%.2f",bmi) + "(??????)");
                                tv_bmi.setTextColor(Color.parseColor("#BA78D9"));
                            }else if (bmi >23.32){
                                tv_bmi.setText("BMI : " + String.format("%.2f",bmi) + "(?????????)");
                                tv_bmi.setTextColor(Color.parseColor("#787AD6"));
                            }else if (bmi > 15.35){
                                tv_bmi.setText("BMI : " + String.format("%.2f",bmi) + "(??????)");
                                tv_bmi.setTextColor(Color.parseColor("#557CD5"));
                            }else{
                                tv_bmi.setText("BMI : " + String.format("%.2f",bmi) + "(?????????)");
                                tv_bmi.setTextColor(Color.parseColor("#7DA4BD"));
                            }
                            switch (Integer.parseInt(exer)){
                                case 1:
                                    dailyKcal =String.format("%.2f",Double.parseDouble(bmr) * 1.3) ;
                                    break;
                                case 2:
                                    dailyKcal =String.format("%.2f",Double.parseDouble(bmr) * 1.55) ;
                                    break;
                                case 3:
                                    dailyKcal =String.format("%.2f",Double.parseDouble(bmr) * 1.7) ;
                                    break;
                                case 4:
                                    dailyKcal =String.format("%.2f",Double.parseDouble(bmr) * 1.9) ;
                                    break;
                            }
                            tv_userInfo.setText("   Age : " + age + " / BMI : " + bmi + " / BMR : " + bmr + "kcal" );
                            tv_age.setText("Age : " + age + "???");

                            tv_bmr.setText("BMR : " + bmr + "kcal");


                            tv_exer.setText( "??? "+ userName + "?????? ?????? ?????? ???????????? " + dailyKcal + "kcal ?????????.");

                            tv_nickname.setText(userName);
                            Glide.with(getApplicationContext()).load(valueOf(userProfile)).into(iv_profile);


                        }else{



                        }



                    }
                    if (spy.size()<1){
                        CustomDialog customDialog = new CustomDialog(MainActivity.this);
                        customDialog.setCancelable(false);
                        customDialog.show();
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // ????????? ??????????????? ?????? ?????? ???
                    Log.e("MainActivity", String.valueOf(databaseError.toException())); // ????????? ??????
                }
            });



            int length = array.size(); // ????????? ??????
            for (int idx = 0; idx < length; idx++) {  // ????????? ???????????? ??????
                JSONObject object = array.get(idx);// json??? idx?????? object??? ????????????,
                String id = object.getString("id");
                String userName = object.getString("userName");
                String userProfile = object.getString("userProfile");
                String bornDate = object.getString("bornDate");
                String gender = object.getString("gender");
                String bodyLength = object.getString("bodyLength");
                String bodyWeight = object.getString("bodyWeight");

                // ????????? ????????? ????????? ListView??? ?????? ?????????.


            }
        } catch (Exception e) {
            Log.i("seo", "error : " + e);

        }
    }

    private void firebaseUpdate(){
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("????????? ????????????...");
        progressDialog.setCancelable(false);
        progressDialog.show();


        sqLiteManager = new SQLiteManager(this, "ThreeMate.db", null, 1);




        database = FirebaseDatabase.getInstance(); // ?????????????????? ?????????????????? ??????
        databaseReference = database.getReference(userUID); // DB ????????? ??????
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // ?????????????????? ????????????????????? ???????????? ???????????? ???
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) { // ??????????????? ????????? List??? ????????????
                    FD fd = snapshot.getValue(FD.class); // ??????????????? User ????????? ???????????? ?????????.
                    if (fd.getName() != null){
                        String name = fd.getName();
                        String kcal = fd.getKcal();
                        String carbs = fd.getCarbs();
                        String protein = fd.getProtein();
                        String fat = fd.getFat();
                        String date = fd.getDate();
                        String url = fd.getIcon();
                        String time = fd.getTime();



                        sqLiteManager.insert2(name,
                                kcal,
                                carbs,
                                protein,
                                fat,
                                date, url,time);



                    }


                }
                /*
                new Thread(){
                    @Override
                    public void run() {
                        Document doc = null;

                        try {
                            for (int i = 0 ; i < placeName.size();i++){
                                doc = Jsoup.connect("https://search.naver.com/search.naver?where=nexearch&sm=top_hty&fbm=1&ie=utf8&query=" + placeName.get(i)).get();
                                Elements elements = doc.select(".score");
                                if (elements.size()>0 ){
                                    msg = elements.first().text();
                                    matchScore.add(msg.substring(0,4));
                                    //bundle.putString("message",msg);
                                    //Message msg = handler.obtainMessage();
                                    //msg.setData(bundle);
                                    //handler.sendMessage(msg);
                                    Log.d("Lee",placeName.get(i)+ ": ??? ?????? : "+msg);
                                }else{
                                    matchScore.add("0.00".substring(0,4));
                                    Log.d("Lee",placeName.get(i)+ ": ??? ?????? : "+"0.00");
                                }
                            }


                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }.start();

                 */

                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // ????????? ??????????????? ?????? ?????? ???
                Log.e("MainActivity", String.valueOf(databaseError.toException())); // ????????? ??????
            }
        });

    }
    Handler handler = new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            Bundle bundle = msg.getData();
           //matchScore.add(bundle.getString("message").substring(0,4));
            Log.d("Lee",bundle.getString("message").substring(0,4)+ ": ?????????");
        }
    };
}