package org.techtown.ThreeMate;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import rubikstudio.library.LuckyWheelView;
import rubikstudio.library.model.LuckyItem;

public class Roulette2 extends AppCompatActivity {
    List<LuckyItem> data = new ArrayList<>();

    private int sequenceNum = 0;

    //
    private static final String TAG_TEXT = "text";
    TextView textView;
    List<Map<String,Object>> dialogItemList;
    private String[] text = {"밀크티","파르페", "푸딩",
            "제주말차라떼","고구마파이", "귤타르트" ,
            "피칸파이","도넛", "불닭크림파스타",
            "탕수육","마라탕","훠궈"};
    //listView


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.roulette2);


        String str=new String(String.valueOf(text));

        //
        textView=(TextView)findViewById(R.id.main_text);
        Button button=(Button)findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAlertDialog();
            }
        });
        dialogItemList = new ArrayList<>();

        int len = text.length;
        for(int i = 0; i<len; i++)
        {
            Map<String, Object> itemMap = new HashMap<>();
            itemMap.put(TAG_TEXT, text[i]);

            dialogItemList.add(itemMap);
        }
        //listView
    }

    //
    private void showAlertDialog()
    {

        AlertDialog.Builder builder = new AlertDialog.Builder(Roulette2.this);
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.alert_dialog, null);
        builder.setView(view);

        final ListView listview = (ListView)view.findViewById(R.id.listview_alterdialog_list);
        final AlertDialog dialog = builder.create();

        SimpleAdapter simpleAdapter = new SimpleAdapter(Roulette2.this, dialogItemList,
                R.layout.alert_dialog_row,
                new String[]{TAG_TEXT},
                new int[]{ R.id.alertDialogItemTextView});

        listview.setAdapter(simpleAdapter);
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                textView.setText(text[position] + "를(을) 선택했습니다.");
                dialog.dismiss();

                //

                final LuckyWheelView luckyWheelView = (LuckyWheelView) findViewById(R.id.luckyWheel);
                String str = new String(String.valueOf(text[position])); // text[position]를 가져오셔야죠!!!ㅋㅋㅋㅋ

                LuckyItem luckyItem1 = new LuckyItem();
                luckyItem1.topText = str;

                /** 색 교차 구현 **/
                /*if (sequenceNum % 2 == 0&&sequenceNum % 3 == 1){
                    luckyItem1.color = 0xffFFD399;
                }else if(sequenceNum % 3 == 0){
                    luckyItem1.color = 0xffFFBB61;
                }else if(sequenceNum % 2 == 1||sequenceNum % 5 == 0||sequenceNum % 7 == 0){
                    luckyItem1.color = 0xffFF8B61;
                }
                sequenceNum += 1;*/

                switch (sequenceNum+=1){
                    case 1
                            :luckyItem1.color = 0xffFFD399; break;
                    case 2
                            :luckyItem1.color = 0xffFFBB61; break;
                    case 3
                            :luckyItem1.color = 0xffFF8B61; break;
                    case 4
                            :luckyItem1.color = 0xffFFD399; break;
                    case 5
                            :luckyItem1.color = 0xffFFBB61; break;
                    case 6
                            :luckyItem1.color = 0xffFF8B61; break;
                    case 7
                            :luckyItem1.color = 0xffFFD399; break;
                    case 8
                            :luckyItem1.color = 0xffFFBB61; break;
                    case 9
                            :luckyItem1.color = 0xffFF8B61; break;
                    case 10
                            :luckyItem1.color = 0xffFFD399; break;
                    case 11
                            :luckyItem1.color = 0xffFFBB61; break;
                    case 12
                            :luckyItem1.color = 0xffFF8B61; break;
                    case 13
                            :luckyItem1.color = 0xffFFD399; break;
                    case 14
                            :luckyItem1.color = 0xffFFBB61; break;
                    case 15
                            :luckyItem1.color = 0xffFF8B61; break;
                    case 16
                            :luckyItem1.color = 0xffFFD399; break;
                    case 17
                            :luckyItem1.color = 0xffFFBB61; break;
                    case 18
                            :luckyItem1.color = 0xffFF8B61; break;
                    case 19
                            :luckyItem1.color = 0xffFFD399; break;
                    case 20
                            :luckyItem1.color = 0xffFFBB61; break;
                    case 21
                            :luckyItem1.color = 0xffFF8B61; break;
                    case 22
                            :luckyItem1.color = 0xffFFD399; break;
                    case 23
                            :luckyItem1.color = 0xffFFBB61; break;
                    case 24
                            :luckyItem1.color = 0xffFF8B61; break;
                    case 25
                            :luckyItem1.color = 0xffFFD399; break;
                    case 26
                            :luckyItem1.color = 0xffFFBB61; break;
                    case 27
                            :luckyItem1.color = 0xffFF8B61; break;
                    case 28
                            :luckyItem1.color = 0xffFFD399; break;
                    case 29
                            :luckyItem1.color = 0xffFFBB61; break;
                    case 30
                            :luckyItem1.color = 0xffFF8B61; break;
                    case 31
                            :luckyItem1.color = 0xffFFD399; break;
                    case 32
                            :luckyItem1.color = 0xffFFBB61; break;
                    case 33
                            :luckyItem1.color = 0xffFF8B61; break;


                }





                data.add(luckyItem1);

                luckyWheelView.setData(data);
                luckyWheelView.setRound(5);

                findViewById(R.id.play).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        int index = getRandomIndex();
                        luckyWheelView.startLuckyWheelWithTargetIndex(index);
                    }
                });

                luckyWheelView.setLuckyRoundItemSelectedListener(new LuckyWheelView.LuckyRoundItemSelectedListener() {
                    @Override
                    public void LuckyRoundItemSelected(int index) {
                        Toast.makeText(getApplicationContext(), data.get(index).topText, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        dialog.setCancelable(false);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();

        //랜덤메뉴룰렛 화면 전환
        Button rbtn = (Button)findViewById(R.id.rmenu_btn);
        rbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Roulette2.this, Roulette.class);
                startActivity(intent);
            }
        });
    }
    //listView

    private int getRandomIndex() {
        Random rand = new Random();
        return rand.nextInt(data.size() - 1) + 0;
    }

    private int getRandomRound() {
        Random rand = new Random();
        return rand.nextInt(10) + 15;
    }
}
