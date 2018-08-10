package com.myday.dong.myday;



import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Build;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Calendar;


public class AddActivity extends AppCompatActivity {

    private Button confimBtn;
    private EditText inputText,inputMemo;
    private TimePicker timePicker;
    private int inputHour,inputMinete;
    private String inputString,inputMemoStr="";
    private DBHelper dbHelper;
    private SQLiteDatabase db;
    private RecyclerAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setNavigationBarColor(Color.parseColor("#2c7180"));
        }
        Calendar calendar=Calendar.getInstance();
        Toolbar toolbar = (Toolbar) findViewById(R.id.add_toolbar);
        confimBtn=(Button)findViewById(R.id.submit);
        inputText=(EditText)findViewById(R.id.input_text);
        inputMemo=(EditText)findViewById(R.id.memo);
        timePicker=(TimePicker)findViewById(R.id.timer);
        timePicker.setIs24HourView(true);
        timePicker.clearFocus();
        inputHour=timePicker.getCurrentHour();
        inputMinete=timePicker.getCurrentMinute();
        timePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker timePicker, int i, int i1) {
                inputHour=i;
                inputMinete=i1;
            }
        });
        setSupportActionBar(toolbar);
        ActionBar actionBar=getSupportActionBar();
        if(actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        dbHelper=new DBHelper(this,"InfoDB.db",null,2);
        db= dbHelper.getReadableDatabase();
        confimBtn.setOnClickListener(view -> {
            inputString= inputText.getText().toString();
            inputMemoStr=inputMemo.getText().toString();
            if(inputString.isEmpty()){
                Toast.makeText(this,"请输入您的目标",Toast.LENGTH_SHORT).show();
            }else{
                db.execSQL("insert into Info(todo,memo,hour,minute,status,alarm) values(?,?,?,?,0,1)", new String[]{inputString, inputMemoStr, "" + inputHour, "" + inputMinete});
                setResult(RESULT_OK,new Intent());

                finish();
                Toast.makeText(this, "成功新增", Toast.LENGTH_LONG).show();

            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:finish();return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
