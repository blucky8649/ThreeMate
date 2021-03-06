package org.techtown.ThreeMate;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.view.animation.TranslateAnimation;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import org.json.JSONObject;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class Diary extends AppCompatActivity {
    private Drawable drawable;
    private static final int REQUEST_CODE = 0;
    private String TAG = "Lee";
    final AnimationSet set = new AnimationSet(true);
    private TextView resultText;
    private Button btn_upload;// ???????????????
    private EditText edit_name, edit_kcal, edit_carbs, edit_protein, edit_fat;       // ???????????? ??? 3???(????????????, ???????????????, ??????)
    // SQLite Class ????????? ??????
    private EditText textView;
    private EditText edit_attach;
    private String  imageUrl = "";
    boolean isPhotoCaptured;
    boolean isPhotoFileSaved;
    boolean isPhotoCanceled;
    private ImageView pictureImageView;
    int selectedPhotoMenu;


    File file;
    Bitmap resultPhotoBitmap;
    private String myFormat = "yyyy-MM-dd";    // ????????????   2018/11/28
    private SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.KOREA);
    private SimpleDateFormat sdf2 = new SimpleDateFormat("HH:mm:ss", Locale.KOREA);

    private ListView listView;// DB??? ????????? ????????? ?????????????????? ????????????
    int figures;
    private ArrayAdapter<String> adapter;
    public SQLiteManager sqLiteManager;
    private TextView calories_remaining_number;
    private TextView calories_remaining_number2;
    private Button main_btn;
    private Button btn_upload2;
    private Button DateUp;
    private Button DateDown;
    private double parsekcal = 0.0;
    private ArrayList<String> foodkcal = new ArrayList<String>();
    private ArrayList<String> idIndicator2 = new ArrayList<String>();
    private ArrayList<String> matchfood = new ArrayList<String>();
    private ArrayList<String> matchdate = new ArrayList<String>();
    private ArrayList<String> matchtime = new ArrayList<String>();
    private String idIndicator ="";
    private Context context;
    private int sum=0;
    private SwipeRefreshLayout refreshLayout = null;
    private String date;
    private int down;
    private int up;


    /**
     * FIREBASE ??????
     */
    private FirebaseAuth auth; // ????????? ????????? ?????? ??????
    private FirebaseUser user;
    private FirebaseDatabase database;
    private DatabaseReference databaseReference;
    private String time;
    RecyclerView recyclerView;
    FoodAdapter adapter2;
    private FirebaseStorage storage;
    private StorageReference storageRef;
    private Uri filePath;
    private String stringUri;
    private String userUID;
    private String userName;
    private String userProfile;
    private String userEmail;
    private String bornDate;
    private String gender;
    private String bodyLength;
    private String bodyWeight;
    private String exer;
    private int age;
    private String bmr;
    private String dailyKcal;



    SearchActivity MA = (SearchActivity) SearchActivity.activity;
    Calendar myCalendar = Calendar.getInstance();

    DatePickerDialog.OnDateSetListener myDatePicker = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, month);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            date = year+"/"+month+"/"+dayOfMonth;
            updateLabel();
            updateList();
            LayoutAnimationController controller= new LayoutAnimationController(set, 0.17f);
            recyclerView.setLayoutAnimation(controller);

        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.diary);

        auth = FirebaseAuth.getInstance(); // ?????????????????? ?????? ?????? ?????????.
        user = auth.getCurrentUser();
        userUID = user.getUid();
        database = FirebaseDatabase.getInstance(); // ?????????????????? ?????????????????? ??????
        databaseReference = database.getReference(userUID); // DB ????????? ??????
        pictureImageView = findViewById(R.id.pictureImageView);
        new Thread(r).start();


        Intent secondIntent = getIntent();
        String name = secondIntent.getStringExtra("name");
        String kcal = secondIntent.getStringExtra("kcal");
        String carbs = secondIntent.getStringExtra("carbs");
        String protein = secondIntent.getStringExtra("protein");
        String fat = secondIntent.getStringExtra("fat");
        final int num = secondIntent.getIntExtra("num", 0);
        final int ACode = secondIntent.getIntExtra("ACode", 0);
        final String[] url = {secondIntent.getStringExtra("url")};




        // ??? ???????????? ????????? ?????? ??????????????? (EditText, Button)
        btn_upload = findViewById(R.id.btn_upload);
        btn_upload2 = findViewById(R.id.btn_upload2);
        main_btn = findViewById(R.id.main_button); //'??????????????????' ??????
        DateUp = findViewById(R.id.DateUp);
        DateDown = findViewById(R.id.DateDown);
        edit_name = findViewById(R.id.edit_name);
        edit_kcal = findViewById(R.id.edit_kcal);
        edit_carbs = findViewById(R.id.edit_carbs);
        edit_protein = findViewById(R.id.edit_protein);
        edit_fat = findViewById(R.id.edit_fat);
        textView = (EditText) findViewById(R.id.date);
        refreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);
        if(num == 1){

            edit_name.setText(name);
            edit_kcal.setText(kcal);
            edit_carbs.setText(carbs);
            edit_protein.setText(protein);
            edit_fat.setText(fat);






        }



        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(Diary.this, myDatePicker, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        updateLabel();


        recyclerView = findViewById(R.id.recyclerView);

        calories_remaining_number = findViewById(R.id.calories_remaining_number);
        calories_remaining_number2 = findViewById(R.id.calories_remaining_number2);

        main_btn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                final Animation anim = AnimationUtils.loadAnimation
                        (getApplicationContext(), // ??????????????? ????????????
                                R.anim.scale_anim2);
                anim.setFillAfter(true);
                final Animation anim2 = AnimationUtils.loadAnimation
                        (getApplicationContext(), // ??????????????? ????????????
                                R.anim.scale_anim);
                anim2.setFillAfter(true);
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        main_btn.setBackground(getDrawable(R.drawable.mainbtn_pressed));
                        main_btn.startAnimation(anim);
                        break;
                    case MotionEvent.ACTION_UP:
                        main_btn.setBackground(getDrawable(R.drawable.mainbtn));
                        main_btn.startAnimation(anim2);
                        break;
                }
                return false;
            }
        });
        main_btn.setOnClickListener(new View.OnClickListener() {
            //??????????????? ????????????
            @Override
            public void onClick(View view) {
                if (MA != null){
                    MA.finish();
                    finish();
                }else{
                    finish();
                }



            }
        });

        DateDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String dayDown=sdf.format(myCalendar.getTime()).replace("-","");
                int dayDownint =Integer.parseInt(dayDown);
                dayDownint = dayDownint -1;
                dayDown = String.valueOf(dayDownint);

                SimpleDateFormat sdfmt = new SimpleDateFormat("yyyyMMdd");
                try {
                    Date date = sdfmt.parse(dayDown);
                    dayDown = new java.text.SimpleDateFormat("yyyy-MM-dd").format(date);
                    myCalendar.setTime(date);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                textView.setText(dayDown);
                updateList();
                LayoutAnimationController controller= new LayoutAnimationController(set, 0.17f);
                recyclerView.setLayoutAnimation(controller);
            }
        });

        DateUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String dayUp=sdf.format(myCalendar.getTime()).replace("-","");
                int dayUpint =Integer.parseInt(dayUp);
                dayUpint = dayUpint +1;
                dayUp = String.valueOf(dayUpint);

                SimpleDateFormat sdfmt = new SimpleDateFormat("yyyyMMdd");
                try {
                    Date date = sdfmt.parse(dayUp);
                    dayUp = new java.text.SimpleDateFormat("yyyy-MM-dd").format(date);
                    myCalendar.setTime(date);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                textView.setText(dayUp);
                updateList();
                LayoutAnimationController controller= new LayoutAnimationController(set, 0.17f);
                recyclerView.setLayoutAnimation(controller);
            }
        });
        DateUp.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                final Animation anim = AnimationUtils.loadAnimation
                        (getApplicationContext(), // ??????????????? ????????????
                                R.anim.scale_anim2);
                anim.setFillAfter(true);
                final Animation anim2 = AnimationUtils.loadAnimation
                        (getApplicationContext(), // ??????????????? ????????????
                                R.anim.scale_anim);
                anim2.setFillAfter(true);
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        DateUp.startAnimation(anim);
                        break;
                    case MotionEvent.ACTION_UP:
                        DateUp.startAnimation(anim2);
                        break;
                }
                return false;
            }
        });
        DateDown.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                final Animation anim = AnimationUtils.loadAnimation
                        (getApplicationContext(), // ??????????????? ????????????
                                R.anim.scale_anim2);
                anim.setFillAfter(true);
                final Animation anim2 = AnimationUtils.loadAnimation
                        (getApplicationContext(), // ??????????????? ????????????
                                R.anim.scale_anim);
                anim2.setFillAfter(true);
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        DateDown.startAnimation(anim);
                        break;
                    case MotionEvent.ACTION_UP:
                        DateDown.startAnimation(anim2);
                        break;
                }
                return false;
            }
        });



        recyclerView = findViewById(R.id.recyclerView);
        sqLiteManager = new SQLiteManager(getApplicationContext(), "ThreeMate.db", null, 1);
        GridLayoutManager layoutManager = new GridLayoutManager(this, 1);
        recyclerView.setLayoutManager(layoutManager);

        adapter2 = new FoodAdapter();
        adapter2.setOnItemClickListener(new OnFoodItemClickListener() {
            @Override
            public void onItemClick(FoodAdapter.ViewHolder holder, View view, final int position) {
                AlertDialog.Builder builder = new AlertDialog.Builder(Diary.this);
                builder.setPositiveButton("???", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        delete(position);

                        sqLiteManager.delete(idIndicator2.get(position));
                        Toast.makeText(getApplicationContext(),"["+matchdate.get(position)+"]"+matchfood.get(position)+" ????????????!",Toast.LENGTH_LONG).show();
                        updateList();

                        edit_name.setText(null);
                        edit_kcal.setText(null);
                        edit_carbs.setText(null);
                        edit_protein.setText(null);
                        edit_fat.setText(null);
                        url[0] = null;
                        edit_attach.setText(null);
                        imageUrl = null;
                        drawable = getResources().getDrawable(R.mipmap.ic_launcher_round);
                        pictureImageView.setImageDrawable(drawable);
                        LayoutAnimationController controller= new LayoutAnimationController(set, 0.17f);
                        recyclerView.setLayoutAnimation(controller);



                    }
                });
                builder.setCancelable(true);
                builder.setNegativeButton("?????????",null);
                builder.setTitle("????????? ??????");
                builder.setMessage("["+matchdate.get(position)+"]"+matchfood.get(position)+ " ???????????? ?????????????????????????");
                builder.show();



            }
        });

        edit_attach = findViewById(R.id.edit_attach);
        edit_attach.setFocusableInTouchMode(true);
        edit_attach.setFocusable(false);
        edit_attach.setClickable(true);
        edit_attach.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN){
                    tedPermission();
                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(intent, REQUEST_CODE);

                }
                return false;
            }
        });



        Animation rtl = new TranslateAnimation(
                Animation.RELATIVE_TO_SELF,-1,
                Animation.RELATIVE_TO_SELF,0,
                Animation.RELATIVE_TO_SELF,-1,
                Animation.RELATIVE_TO_SELF,0         );
        rtl.setDuration(500);
        set.addAnimation(rtl);

        Animation alpha = new AlphaAnimation(0,1);
        alpha.setDuration(700);
        set.addAnimation(alpha);

        final LayoutAnimationController[] controller = {new LayoutAnimationController(set, 0.17f)};
        recyclerView.setLayoutAnimation(controller[0]);


        /**
         * ???????????? ??????
         */
        // ??? ???????????? ????????? ?????? ??????????????? (ListVeiw)
        listView = (ListView)findViewById(R.id.listview);

        //???????????? ???????????? ?????? ?????????
        List<String> list = new ArrayList<>();

        //??????????????? ???????????? ???????????? ?????? ???????????? ?????????
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, list);

        listView.setAdapter(adapter);


        /**
         * SQLite ?????? ??????
         */
        // SQLite ?????? ?????????









        edit_name.setText(name);
        edit_kcal.setText(kcal);
        edit_carbs.setText(carbs);
        edit_protein.setText(protein);
        edit_fat.setText(fat);


        resultText = (TextView) findViewById(R.id.calories_remaining_number);
        firebaseUpdate();


        // ????????? ???????????? ????????? ????????? ??????
        btn_upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (edit_kcal.getText().toString().equals("")||edit_name.getText().toString().equals("")||edit_carbs.getText().toString().equals("")||edit_protein.getText().toString().equals("")||edit_fat.getText().toString().equals("")){
                    Toast.makeText(getApplicationContext(),"?????? ????????? ??????????????????.",Toast.LENGTH_LONG).show();
                }else{
                    if (imageUrl.length() >0){
                        url[0] = imageUrl;
                    }
                    // EditText??? ????????? ????????? DB??? Insert.
                    sqLiteManager.insert(
                            edit_name.getText().toString(),
                            edit_kcal.getText().toString(),
                            edit_carbs.getText().toString(),
                            edit_protein.getText().toString(),
                            edit_fat.getText().toString(),
                            textView.getText().toString(),
                            stringUri != null?stringUri:url[0], time);

                    // ??????????????? ????????????. (DB??? ????????? ???????????????)

                    writeNewFood( stringUri != null?stringUri:url[0],
                            edit_name.getText().toString(),
                            edit_kcal.getText().toString(),
                            edit_carbs.getText().toString(),
                            edit_protein.getText().toString(),
                            edit_fat.getText().toString(),
                            textView.getText().toString(),time);


                    updateList();

                    Toast.makeText(getApplicationContext(),"????????????!",Toast.LENGTH_SHORT).show();
                    edit_name.setText(null);
                    edit_kcal.setText(null);
                    edit_carbs.setText(null);
                    edit_protein.setText(null);
                    edit_fat.setText(null);
                    url[0] = null;
                    edit_attach.setText(null);
                    imageUrl = null;
                    drawable = getResources().getDrawable(R.mipmap.ic_launcher_round);
                    pictureImageView.setImageDrawable(drawable);
                    LayoutAnimationController controller= new LayoutAnimationController(set, 0.17f);
                    recyclerView.setLayoutAnimation(controller);
                }

            }
        });
        btn_upload.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                final Animation anim = AnimationUtils.loadAnimation
                        (getApplicationContext(), // ??????????????? ????????????
                                R.anim.scale_anim2);
                anim.setFillAfter(true);
                final Animation anim2 = AnimationUtils.loadAnimation
                        (getApplicationContext(), // ??????????????? ????????????
                                R.anim.scale_anim);
                anim2.setFillAfter(true);
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        btn_upload.setBackground(getDrawable(R.drawable.plus_pressed));
                        btn_upload.startAnimation(anim);
                        break;
                    case MotionEvent.ACTION_UP:
                        btn_upload.setBackground(getDrawable(R.drawable.plus));
                        btn_upload.startAnimation(anim2);
                        break;
                }
                return false;
            }
        });
        findViewById(R.id.btn_upload2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(Diary.this);
                builder.setPositiveButton("???", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {


                        sqLiteManager.clear(date);
                        updateList();
                        edit_name.setText(null);
                        edit_kcal.setText(null);
                        edit_carbs.setText(null);
                        edit_protein.setText(null);
                        edit_fat.setText(null);
                        url[0] = null;
                        edit_attach.setText(null);
                        imageUrl = null;
                        drawable = getResources().getDrawable(R.mipmap.ic_launcher_round);
                        pictureImageView.setImageDrawable(drawable);



                        LayoutAnimationController controller= new LayoutAnimationController(set, 0.17f);
                        recyclerView.setLayoutAnimation(controller);
                        Toast.makeText(getApplicationContext(),"????????????!",Toast.LENGTH_LONG).show();
                    }
                });
                builder.setCancelable(true);
                builder.setNegativeButton("?????????",null);
                builder.setTitle("????????? ??????");
                builder.setMessage("???????????? ?????? ?????????????????????????");
                builder.show();
            }
        });
        findViewById(R.id.btn_upload2).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                final Animation anim = AnimationUtils.loadAnimation
                        (getApplicationContext(), // ??????????????? ????????????
                                R.anim.scale_anim2);
                anim.setFillAfter(true);
                final Animation anim2 = AnimationUtils.loadAnimation
                        (getApplicationContext(), // ??????????????? ????????????
                                R.anim.scale_anim);
                anim2.setFillAfter(true);
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        findViewById(R.id.btn_upload2).setBackground(getDrawable(R.drawable.minus_pressed));
                        findViewById(R.id.btn_upload2).startAnimation(anim);
                        break;
                    case MotionEvent.ACTION_UP:
                        findViewById(R.id.btn_upload2).setBackground(getDrawable(R.drawable.minus));
                        findViewById(R.id.btn_upload2).startAnimation(anim2);
                        break;
                }
                return false;
            }
        });
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Intent intent = new Intent(Diary.this, Diary.class);
                finish();
                startActivity(intent);
            }
        });
    }

    private void tedPermission() {
        PermissionListener permissionListener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                //??????????????????
            }

            @Override
            public void onPermissionDenied(ArrayList<String> deniedPermissions) {
                //?????? ?????? ??????
            }
        };
        TedPermission.with(this)
                .setPermissionListener(permissionListener)
                .setRationaleMessage(getResources().getString(R.string.permission_2))
                .setDeniedMessage(getResources().getString(R.string.permission_1))
                .setPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA)
                .check();
    }

    private void updateList(){
        adapter2.removeItem();
        matchdate.clear();
        matchfood.clear();
        idIndicator2.clear();
        matchtime.clear();
        sum=0;
        resultText.setText(Double.toString(0));
        ArrayList<JSONObject> array = sqLiteManager.getResult(textView.getText().toString()); // DB??? ????????? ??????????????? ?????? ????????????
        try{



            int length =  array.size(); // ????????? ??????
            for(int idx = 0; idx < length; idx++){  // ????????? ???????????? ??????

                JSONObject object = array.get(idx);                // json??? idx?????? object??? ????????????,
                String id = object.getString("id");         // object ????????? id??? ????????? ??????.
                String name = object.getString("name");     // object ????????? name??? ????????? ??????.
                String kcal = object.getString("kcal");     // object ????????? kcal??? ????????? ??????.
                String carbs = object.getString("carbs");     // object ????????? carbs??? ????????? ??????.
                String protein = object.getString("protein");     // object ????????? protein??? ????????? ??????.
                String fat = object.getString("fat");     // object ????????? fat??? ????????? ??????.
                String date = object.getString("date");     // object ????????? date??? ????????? ??????.
                String Url = object.getString("url");     // object ????????? date??? ????????? ??????.
                String time = object.getString("time");     // object ????????? date??? ????????? ??????.
                // ????????? ????????? ????????? ListView??? ?????? ?????????.

                adapter2.addItem(new FD(Url, "????????? : " + name  , "?????? : " + kcal + "kcal","???????????? : " + carbs+ "g", "????????? : " + protein + "g","?????? : " +  fat+ "g",date, time));
                recyclerView.setAdapter(adapter2);


                foodkcal.add(kcal);
                idIndicator2.add(id);
                matchfood.add(name);
                matchdate.add(date);
                matchtime.add(time);


                idIndicator = id;
                sum += Double.parseDouble(kcal);
                resultText.setText(Double.toString(sum));
                if (Double.valueOf(sum) > Double.parseDouble(dailyKcal)){
                    resultText.setTextColor(Color.parseColor("#FF5A5A"));
                } else {
                    resultText.setTextColor(getResources().getColor(R.color.my_green));
                }
            }
            Log.d("Lee", String.valueOf(sum + "kcal"));

        }
        catch (Exception e){
            Log.i("seo","error : " + e);

        }

        adapter2.notifyDataSetChanged();
    }
    @Override
    public void onBackPressed() {
        finish();
    }
    private void updateLabel() {


        date = sdf.format(myCalendar.getTime());
        textView.setText(sdf.format(myCalendar.getTime()));

    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                try {
                    filePath = data.getData();
                    //Glide.with(getActivity().getApplicationContext()).load(String.valueOf(filePath)).into(imageView);
                    uploadFile();




                } catch (Exception e) {

                }
            } else if (resultCode == RESULT_CANCELED) {

            }
        }
    }


    public void writeNewFood(String icon, String name , String kcal, String carbs,String protein,String fat,String date,String time) {
        FD fd = new FD(icon, name, kcal, carbs, protein, fat,date, time);

        databaseReference.child(textView.getText().toString() + "(" +time + ")" ).setValue(fd);
    }


    Runnable r = new Runnable() {
        @Override
        public void run() {

            while (true) {
                try {
                    Thread.sleep(1000);

                } catch (Exception e) {

                }
                if (this != null){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            time = sdf2.format(new Date());
                        }
                    });
                }

            }
        }

    };

    private void delete(int position){
        if (matchdate.size()>0){
            database = FirebaseDatabase.getInstance(); // ?????????????????? ?????????????????? ??????
            databaseReference = database.getReference(userUID);// DB ????????? ??????
            databaseReference.child(matchdate.get(position) + "(" +matchtime.get(position) + ")").setValue(null);

        }
    }

    private void uploadFile() {
        //???????????? ????????? ????????? ??????
        if (filePath != null) {
            //????????? ?????? Dialog ?????????
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("????????????...");
            progressDialog.setCancelable(false);
            progressDialog.show();

            //storage
            storage = FirebaseStorage.getInstance();

            //Unique??? ???????????? ?????????.
            SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMHH_mmss");
            Date now = new Date();
            String filename = formatter.format(now) + ".png";
            //storage ????????? ?????? ???????????? ????????? ??????.
            storageRef = storage.getReferenceFromUrl("gs://threemate-3c56c.appspot.com/").child("images/" +userUID+"/"+ filename);
            //???????????????...
            storageRef.putFile(filePath)
                    //?????????
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            clickLoad();
                            progressDialog.dismiss(); //????????? ?????? Dialog ?????? ??????
                            edit_attach.setText(String.valueOf("?????? ?????? ??????!"));
                            Toast.makeText(getApplicationContext(), "????????? ??????!", Toast.LENGTH_SHORT).show();
                        }
                    })
                    //?????????
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(getApplicationContext(), "????????? ??????!", Toast.LENGTH_SHORT).show();
                        }
                    })
                    //?????????
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            @SuppressWarnings("VisibleForTests") //?????? ?????? ?????? ???????????? ????????? ????????????. ??? ??????????
                            double progress = (100 * taskSnapshot.getBytesTransferred()) /  taskSnapshot.getTotalByteCount();
                            //dialog??? ???????????? ???????????? ????????? ??????
                            progressDialog.setMessage("Uploaded " + ((int) progress) + "% ...");
                        }
                    });
        } else {
            Toast.makeText(getApplicationContext(), "????????? ?????? ???????????????.", Toast.LENGTH_SHORT).show();
        }
    }
    public void clickLoad() {

        //Firebase Storage??? ???????????? ?????? ????????? ?????? ????????????

        //1. Firebase Storeage?????? ?????? ????????????
        FirebaseStorage firebaseStorage= FirebaseStorage.getInstance();

        //2. ??????????????? ?????? ?????? ????????????
        StorageReference rootRef= firebaseStorage.getReference();

        //???????????? ????????? ????????? ???????????? ????????????
        //??????????????? ???????????? ????????? monkey.png


        //?????? ????????? ????????? ??????????????? ????????????
        if(storageRef!=null){
            //??????????????? ?????? ???????????? ???????????? URL??? ????????????
            storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    //???????????? URL??? ??????????????? ???????????? ???.
                    Glide.with(getApplicationContext()).load(String.valueOf(uri)).into(pictureImageView);
                    stringUri = String.valueOf(uri);
                }
            });

        }

    }
    private void firebaseUpdate(){
        Calendar current = Calendar.getInstance();
        int currentYear = current.get(Calendar.YEAR);
        int currentMonth = current.get(Calendar.MONTH);
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




                        int birthYear = Integer.valueOf(bornDate.substring(0,4));
                        int birthMonth = Integer.valueOf(bornDate.substring(5,7));
                        int birthDay = Integer.valueOf(bornDate.substring(8,9));
                        age = currentYear-birthYear;
                        if (birthMonth * 100 + birthDay > currentMonth * 100 + currentDay){
                            age--;
                        }

                        Double bmi =  Double.valueOf(bodyWeight) / ((Double.valueOf(bodyLength)/100) *  (Double.valueOf(bodyLength)/100)) ;
                        bmr = gender.equals("???")?String.format("%.2f",(66.47+(13.75*Double.valueOf(bodyWeight) )+(5*Double.valueOf(bodyLength)) - (6.76 * Double.valueOf(age))))
                                :String.format("%.2f",(665.1+(9.56*Double.valueOf(bodyWeight) )+(1.85*Double.valueOf(bodyLength)) - (4.68 * Double.valueOf(age))));
                        Log.d("Lee", bodyWeight+ String.valueOf((Double.valueOf(bodyLength)/10) *  (Double.valueOf(bodyLength)/10)) );
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

                        calories_remaining_number2.setText(" / " + String.format("%.1f",Double.parseDouble(dailyKcal)) + "kcal");

                        updateList();
                        LayoutAnimationController controller = new LayoutAnimationController(set, 0.17f);
                        recyclerView.setLayoutAnimation(controller);
                    }else{



                    }



                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // ????????? ??????????????? ?????? ?????? ???
                Log.e("MainActivity", String.valueOf(databaseError.toException())); // ????????? ??????
            }
        });


    }
}

