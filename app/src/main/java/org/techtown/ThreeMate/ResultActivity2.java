package org.techtown.ThreeMate;

        import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.fonts.Font;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

public class ResultActivity2 extends AppCompatActivity {
    private ImageView ivImage;
    private TextView Answer;
    private TextView hidden;
    private TextView tv;
    private String key = "RPT4s8Z6M%2Fx8mQW4CYG1A0l3SEU5EVg818RAioq2uvIy6B05Dn3fJvWjthwkztWzuq75u6Xuqh1rtk27ZM2n7w%3D%3D";
    private String data;
    private String kcal;
    private URL Url;
    private String strUrl, strCookie, result;
    private Font font;


    private ArrayList<String> matchFoods = new ArrayList<String>();
    private ArrayList<String> matchFoods2 = new ArrayList<String>();
    private ArrayList<String> foodkcal = new ArrayList<String>();
    private ArrayList<String> foodtan = new ArrayList<String>();
    private ArrayList<String> fooddan = new ArrayList<String>();
    private ArrayList<String> foodji = new ArrayList<String>();
    private ArrayList<String> imageurl = new ArrayList<String>();

    @SuppressLint("StaticFieldLeak")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result2);

        ivImage = findViewById(R.id.iv_image);
        Answer = findViewById(R.id.Answer);
        tv = findViewById(R.id.tv);
        final Button button = findViewById(R.id.button);
        final Button foodsave = findViewById(R.id.foodsave);
        final Button main = findViewById(R.id.main);
        hidden = findViewById(R.id.hidden);



        main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AnswerSetGetter.setClear();
                finish();



            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AnswerSetGetter.setClear();
                Intent intent = new Intent(ResultActivity2.this, RouletteActivity.class);
                finish();
                startActivity(intent);


            }
        });
        foodsave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                AlertDialog.Builder builder = new AlertDialog.Builder(ResultActivity2.this);
                builder.setPositiveButton("???", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Double parsekcal= 0.0;
                        Double parsetan= 0.0;
                        Double parsedan= 0.0;
                        Double parseji= 0.0;
                        if (TextUtils.isEmpty(tv.getText().toString()) || tv.getText().toString().equals("???????????? ?????????..")){
                            Toast.makeText(ResultActivity2.this, "??????????????? ????????? ??? ?????? ?????????????????????.", Toast.LENGTH_SHORT).show();

                        }
                        else {

                            Intent myIntent = new Intent(ResultActivity2.this, Diary.class);
                            myIntent.putExtra("name", matchFoods.get(0));
                            myIntent.putExtra("kcal", foodkcal.get(0));
                            myIntent.putExtra("carbs", foodtan.get(0));
                            myIntent.putExtra("protein", fooddan.get(0));
                            myIntent.putExtra("fat", foodji.get(0));
                            myIntent.putExtra("url", imageurl.get(0));
                            myIntent.putExtra("num", 1);
                            Toast.makeText(getApplicationContext(),"?????? ???????????? ??????! '+' ????????? ?????? ??????????????????!",Toast.LENGTH_LONG).show();
                            startActivity(myIntent);
                            finish();
                        }
                    }
                });
                builder.setCancelable(false);
                builder.setNegativeButton("?????????",null);
                builder.setTitle("?????? ??????");
                builder.setMessage("?????? ????????? ???????????? ?????? ??????????????? ?????????????????????????");
                builder.show();






            }
        });

        main.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        main.setBackground(getDrawable(R.drawable.main_menu_btn_pressed));
                        break;
                    case MotionEvent.ACTION_UP:
                        main.setBackground(getDrawable(R.drawable.main_menu_btn));
                        break;
                }
                return false;
            }
        });

        button.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        button.setBackground(getDrawable(R.drawable.replay_btn_pressed));
                        break;
                    case MotionEvent.ACTION_UP:
                        button.setBackground(getDrawable(R.drawable.replay_btn));
                        break;
                }
                return false;
            }
        });

        foodsave.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        foodsave.setBackground(getDrawable(R.drawable.food_save_btn_pressed));
                        break;
                    case MotionEvent.ACTION_UP:
                        foodsave.setBackground(getDrawable(R.drawable.food_save_btn));
                        break;
                }
                return false;
            }
        });


        Intent secondIntent = getIntent();
        String name = secondIntent.getStringExtra("name");
        String name2 = secondIntent.getStringExtra("name2");
        String image = secondIntent.getStringExtra("image");

        matchFoods.add(name);
        matchFoods2.add(name2);
        imageurl.add(image);
        Log.d("Lee", matchFoods.get(0) + matchFoods2.get(0) + imageurl.get(0));
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        // ????????? ????????? ???????????? ?????????
        if (matchFoods.size() > 0) {
           /*      System.out.println("Start..." + new Date());
                // delay 5 seconds
                Thread.sleep(1300);

            */

            Glide.with(this).load(imageurl.get(0)).into(ivImage);
            Answer.setText(matchFoods.get(0));
            hidden.setText(matchFoods2.get(0));
            setTv();

        }
        // ????????? ????????? ?????????
        else {
            builder.setCancelable(false);
            builder.setPositiveButton("??????", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    AnswerSetGetter.setClear();
                    Intent intent = new Intent(ResultActivity2.this, menuQuiz.class);
                    startActivity(intent);
                }
            });
            builder.setMessage("???????????? ????????? ????????????.");
            builder.show();
        }


    }


    public void setTv() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                data = getXmlData();


                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        tv.setText(data);

                        Log.d("lee", data);

                    }
                });
            }
        }).start();
    }

    String getXmlData() {
        StringBuffer buffer = new StringBuffer();
        String str = hidden.getText().toString();//EditText??? ????????? Text????????????
        String location = URLEncoder.encode(str);
        String query = "%EC%A0%84%EB%A0%A5%EB%A1%9C";

        String queryUrl = "http://apis.data.go.kr/1470000/FoodNtrIrdntInfoService/getFoodNtrItdntList?serviceKey="
                + key + "&desc_kor="
                + location
                + "&pageNo=1&numOfRows=1&bgn_year=&animal_plant=&";
        try {
            URL url = new URL(queryUrl);//???????????? ??? ?????? url??? URL ????????? ??????.
            InputStream is = url.openStream(); //url????????? ??????????????? ??????

            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();//xml????????? ??????
            XmlPullParser xpp = factory.newPullParser();
            xpp.setInput(new InputStreamReader(is, "UTF-8")); //inputstream ???????????? xml ????????????

            String tag;

            xpp.next();
            int eventType = xpp.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                switch (eventType) {
                    case XmlPullParser.START_DOCUMENT:
                        buffer.append("?????? ??????...\n\n");
                        break;

                    case XmlPullParser.START_TAG:
                        tag = xpp.getName();//?????? ?????? ????????????
                        if (tag.equals("item")) ;// ????????? ????????????

                        else if (tag.equals("SERVING_WT")) {
                            buffer.append(" - 1???????????? : ");
                            xpp.next();
                            buffer.append(xpp.getText());//category ????????? TEXT ???????????? ?????????????????? ??????
                            buffer.append("g" + "\n");//????????? ?????? ??????
                        } else if (tag.equals("NUTR_CONT1")) {
                            buffer.append(" - ?????? : ");
                            xpp.next();
                            buffer.append(xpp.getText());//description ????????? TEXT ???????????? ?????????????????? ??????
                            foodkcal.add(xpp.getText());
                            buffer.append("kcal" + "\n");//????????? ?????? ??????
                            kcal = xpp.getText();
                            Log.d("lee", "????????? : " + kcal + "kcal");

                        } else if (tag.equals("NUTR_CONT2")) {
                            buffer.append(" - ???????????? : ");
                            xpp.next();
                            foodtan.add(xpp.getText());
                            buffer.append(xpp.getText());//telephone ????????? TEXT ???????????? ?????????????????? ??????
                            buffer.append("g" + "\n");//????????? ?????? ??????
                        } else if (tag.equals("NUTR_CONT3")) {
                            buffer.append(" - ????????? : ");
                            xpp.next();
                            fooddan.add(xpp.getText());
                            buffer.append(xpp.getText());//address ????????? TEXT ???????????? ?????????????????? ??????
                            buffer.append("g" + "\n");//????????? ?????? ??????
                        } else if (tag.equals("NUTR_CONT4")) {
                            buffer.append(" - ?????? : ");
                            xpp.next();
                            foodji.add(xpp.getText());
                            buffer.append(xpp.getText());//mapx ????????? TEXT ???????????? ?????????????????? ??????
                            buffer.append("g"); //????????? ?????? ??????
                        }
                        break;

                    case XmlPullParser.TEXT:
                        break;

                    case XmlPullParser.END_TAG:
                        tag = xpp.getName(); //?????? ?????? ????????????

                        if (tag.equals("item")) buffer.append("\n");// ????????? ??????????????????..?????????
                        break;
                }

                eventType = xpp.next();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return buffer.toString();//StringBuffer ????????? ?????? ??????

    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(ResultActivity2.this, RouletteActivity.class);
        finish();
        startActivity(intent);
    }
}