package org.techtown.ThreeMate;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import rubikstudio.library.LuckyWheelView;
import rubikstudio.library.model.LuckyItem;

public class RouletteFragment2 extends Fragment {
    List<LuckyItem> data = new ArrayList<>();
    private int sequenceNum = 0;
    private ArrayList<String> matchFoods = new ArrayList<String>();
    private ArrayList<String> matchFoods2 = new ArrayList<String>();
    private ArrayList<String> imageurl = new ArrayList<String>();
    private ListView listView;
    private AlertDialog dialog;

    //
    private static final String TAG_TEXT = "text";
    TextView textView;
    List<Map<String,Object>> dialogItemList;

    //listView
    public static RouletteFragment2 newInstance(){
        RouletteFragment2 rouletteFragment2 = new RouletteFragment2();
        return  rouletteFragment2;
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,  Bundle savedInstanceState) {
        return inflater.inflate(R.layout.frag_roulette2,container, false );

    }

    @Override
    public void onStart() {
        super.onStart();


        try {
            JSONObject json = new JSONObject(getJsonString(getActivity()));      // json ????????? ???????????? Object????????? ??????
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


            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

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



        String str=new String(String.valueOf(matchFoods));

        //
        textView=(TextView) getView().findViewById(R.id.main_text);
        Button button=(Button) getView().findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAlertDialog();
            }
        });
        dialogItemList = new ArrayList<>();

        int len = matchFoods.size();
        for(int i = 0; i<len; i++)
        {
            Map<String, Object> itemMap = new HashMap<>();
            itemMap.put(TAG_TEXT, matchFoods.get(i));

            dialogItemList.add(itemMap);
        }
        //listView
    }

    //
    private void showAlertDialog()
    {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.alert_dialog, null);
        builder.setView(view);

        ListView listview = (ListView)view.findViewById(R.id.listview_alterdialog_list);
        dialog = builder.create();

        SimpleAdapter simpleAdapter = new SimpleAdapter(getActivity(), dialogItemList,
                R.layout.alert_dialog_row,
                new String[]{TAG_TEXT},
                new int[]{ R.id.alertDialogItemTextView});

        listview.setAdapter(simpleAdapter);
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                textView.setText(matchFoods.get(position) + "???(???) ??????????????????.");
                dialog.dismiss();

                //

                final LuckyWheelView luckyWheelView = (LuckyWheelView) getView().findViewById(R.id.luckyWheel);
                String str = new String(String.valueOf(matchFoods.get(position))); // text[position]??? ??????????????????!!!????????????

                LuckyItem luckyItem1 = new LuckyItem();

                luckyItem1.topText = matchFoods.get(position);
                luckyItem1.topText2 = matchFoods2.get(position);
                luckyItem1.topText3 = imageurl.get(position);


                /** ??? ?????? ?????? **/
                if (sequenceNum % 2 == 0){
                    luckyItem1.color = 0xffFFD399;
                }else{
                    luckyItem1.color = 0xffFFBB61;
                }
                sequenceNum += 1;
                data.add(luckyItem1);

                luckyWheelView.setData(data);
                luckyWheelView.setRound(5);

                getView().findViewById(R.id.play).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        int index = getRandomIndex();
                        luckyWheelView.startLuckyWheelWithTargetIndex(index);
                    }
                });

                luckyWheelView.setLuckyRoundItemSelectedListener(new LuckyWheelView.LuckyRoundItemSelectedListener() {
                    @Override
                    public void LuckyRoundItemSelected(int index) {
                        Intent myIntent = new Intent(getActivity(), ResultActivity2.class);
                        myIntent.putExtra("name", data.get(index).topText);
                        myIntent.putExtra("name2", data.get(index).topText2);
                        myIntent.putExtra("image", data.get(index).topText3);
                        getActivity().finish();
                        startActivity(myIntent);//???????????? ?????????
                        Toast.makeText(getActivity(), data.get(index).topText + " ??????!", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        dialog.setCancelable(true);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();

        //?????????????????? ?????? ??????
        /* Button rbtn = (Button)getView().findViewById(R.id.rmenu_btn);
        rbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), RouletteActivity.class);
                startActivity(intent);
            }
        }); */
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


    private static String getJsonString(Context context) {
        String json = "";
        try {
            InputStream is = context.getAssets().open("jsons/test.json");
            int fileSize = is.available();

            byte[] buffer = new byte[fileSize];
            is.read(buffer);
            is.close();

            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return json;
    }

}
