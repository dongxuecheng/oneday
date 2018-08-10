package com.myday.dong.myday;

import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;

import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private List<Info> list=new ArrayList<>();
    private RecyclerView myRecyclerView;
    private RecyclerAdapter adapter;
    private SQLiteDatabase db;
    private DBHelper dbHelper;
    private Info info;
    private Toolbar toolbar;
    private NavigationView navigationView;
    private int menuItem=2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setNavigationBarColor(Color.parseColor("#2c7180"));
        }
        myRecyclerView=(RecyclerView)findViewById(R.id.recycler_view);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        SimpleDateFormat sf=new SimpleDateFormat("M月d日");
        getSupportActionBar().setTitle(sf.format(new Date().getTime()).toString());
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        BindData(menuItem);


        LinearLayoutManager linearLayout= new LinearLayoutManager(this);
        linearLayout.setOrientation(LinearLayoutManager.VERTICAL);
        myRecyclerView.setLayoutManager(linearLayout);
        adapter= new RecyclerAdapter(list);
        myRecyclerView.setAdapter(adapter);
        myRecyclerView.setItemAnimator(new DefaultItemAnimator());//动画
        myRecyclerView.addOnItemTouchListener(new RecyclerItemClickListener(MainActivity.this, myRecyclerView, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
            }

            @Override
            public void onItemLongClick(View view, int position) {
                list.clear();
                BindData(menuItem);
                Info info=list.get(position);
                AlertDialog.Builder dialog=new AlertDialog.Builder(view.getContext());
                dialog.setTitle("删除目标");
                if(info.getStatus()==0){
                    dialog.setMessage("你的目标还没完成呢，你真的要删除吗！");
                }else{
                    dialog.setMessage("目标已经完成，确认删除吗？");
                }
                dialog.setPositiveButton("确认删除", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        db=new DBHelper(view.getContext(),"InfoDB.db",null,2).getReadableDatabase();
                        db.execSQL("delete from Info where todo=? and memo=? and hour=? and minute=? and status=? and alarm=?",
                                new String[]{info.getInfo(),info.getMemo(),""+info.getHour(),""+info.getMinute(),""+info.getStatus(),""+info.getAlarm()});
                        list.clear();
                        BindData(menuItem);
                        adapter.notifyItemRemoved(position);

                        Snackbar.make(view,"删除成功",Snackbar.LENGTH_LONG)
                                .setAction("撤销", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        db=new DBHelper(view.getContext(),"InfoDB.db",null,2).getReadableDatabase();
                                        db.execSQL("insert into Info(todo,memo,hour,minute,status,alarm) values(?,?,?,?,?,?)"
                                                ,new String[]{info.getInfo(),info.getMemo(),""+info.getHour(),""+info.getMinute(),""+info.getStatus(),""+info.getAlarm()});

                                        list.clear();
                                        BindData(menuItem);
                                        adapter.notifyItemInserted(0);
                                    }
                                }).show();
                    }
                });
                dialog.setNegativeButton("我再想想", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                });
                dialog.show();
            }
        }));
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(MainActivity.this,AddActivity.class),1);
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.my_day_goal);
    }

    public void BindData(int n) {
        Cursor cursor;
        dbHelper=new DBHelper(this,"InfoDB.db",null,2);
        db=dbHelper.getReadableDatabase();
        //Cursor cursor=db.query("Info",null,null,null,null,null,null);
        if(n==1||n==0){
            cursor=db.rawQuery("select * from Info where status=?",new String[]{""+n});
        }else{
            cursor=db.rawQuery("select * from Info",new String[]{});
        }
        if(cursor.moveToNext()){
            do {
                info=new Info(cursor.getString(cursor.getColumnIndex("todo")),
                        cursor.getString(cursor.getColumnIndex("memo")),
                        cursor.getInt(cursor.getColumnIndex("hour")),
                        cursor.getInt(cursor.getColumnIndex("minute")),
                        cursor.getInt(cursor.getColumnIndex("status")),
                        cursor.getInt(cursor.getColumnIndex("alarm")));
                list.add(info);
            }while (cursor.moveToNext());
        }
        cursor.close();
        Collections.reverse(list);//反转
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        switch (requestCode){
            case 1:
                if(resultCode==RESULT_OK){
                    list.clear();
                    BindData(2);
                    adapter.notifyItemInserted(0);
                    navigationView.setCheckedItem(R.id.my_day_goal);
                }
                break;
                default:
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    public static void copyToClipboard(Context context, String text) {
        ClipboardManager systemService = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        systemService.setPrimaryClip(ClipData.newPlainText("text", text));
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.my_day_goal) {
            // Handle the camera action
            menuItem=2;
            list.clear();
            BindData(menuItem);
            adapter.notifyDataSetChanged();
            if(list.isEmpty()){
                Toast.makeText(MainActivity.this,"空空如也呢，快去新建一些目标吧",Toast.LENGTH_SHORT).show();
            }
        } else if (id == R.id.todo_goal) {
            menuItem=0;
            list.clear();
            BindData(menuItem);
            adapter.notifyDataSetChanged();
            if(list.isEmpty()){
                Toast.makeText(MainActivity.this,"空空如也呢，快去新建一些目标吧",Toast.LENGTH_SHORT).show();
            }
        } else if (id == R.id.done_goal) {
            menuItem=1;
            list.clear();
            BindData(menuItem);
            adapter.notifyDataSetChanged();
            if(list.isEmpty()){
                Toast.makeText(MainActivity.this,"一个目标都没完成呢，快去完成你的小目标吧",Toast.LENGTH_SHORT).show();
            }
        } else if (id == R.id.about) {
            AlertDialog.Builder dialog=new AlertDialog.Builder(MainActivity.this);
            dialog.setTitle("软件使用说明");
            dialog.setMessage("进入页面，点击主界面的增加按钮就可以新增你的小目标了，你可以设置完成目标的时间和增加备注" +
                        "点击小目标前面的复选框，打勾就代表你完成了这个小目标，点击完成后，该目标会自动转到完成的界面去，你可以在" +
                        "右侧侧滑框中查看你当前的完成情况,长按小目标可以删除了。\n点击闹钟按钮，当到达指定的时间后会以通知的形式进行提醒，" +
                    "为了能保证您准时收到通知提醒，请在系统的设置界面的通知管理处将OneDay通知功能打开，并且设置为重要" +
                    "允许震动和通知铃声。\n因为本软件没有进行联网，所以都是推送本地通知，所以清理后台时请保留该软件防止被清理，" +
                    "否则无法及时收到通知！");
            dialog.setNegativeButton("ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                }
            });
            dialog.show();
        }else if(id==R.id.donate){
            AlertDialog.Builder dialog=new AlertDialog.Builder(MainActivity.this);
            dialog.setTitle("关于作者");
            dialog.setMessage("这个便签小应用是我的第一个安卓APP，是我自学安卓一个多月的小成果，很开心你能使用它，" +
                    "关于本软件还有很多不足，这也只是它的第一个版本，以后我也会慢慢更新，如果你在使用途中遇到了什么bug，欢迎" +
                    "提出您宝贵的意见，您也可以与我联系！");
            dialog.setNegativeButton("联系作者", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    copyToClipboard(MainActivity.this,"DXC19970920");
                    Toast.makeText(MainActivity.this,"作者微信号复制成功",Toast.LENGTH_SHORT).show();
                }
            });
            dialog.setPositiveButton("支付宝捐赠", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    copyToClipboard(MainActivity.this,"#吱口令#长按复制此条消息，打开支付宝给我转账lHolu086hi");
                    Toast.makeText(MainActivity.this,"吱口令已经复制，打开支付宝就可以打赏作者了",Toast.LENGTH_SHORT).show();
                }
            });
            dialog.show();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
