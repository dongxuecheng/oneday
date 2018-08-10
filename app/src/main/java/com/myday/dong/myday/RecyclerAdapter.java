package com.myday.dong.myday;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;
import java.util.List;


public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {

    private SQLiteDatabase db;
    private List<Info> myList;
    private View view;
    private Calendar calendar;

    public RecyclerAdapter(List<Info> list) {
        myList = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recycler_view_item, viewGroup, false);
        final ViewHolder holder = new ViewHolder(view);
        holder.InfoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int pos = holder.getAdapterPosition();
                Info info = myList.get(pos);
                AlertDialog.Builder dialog = new AlertDialog.Builder(view.getContext());
                dialog.setTitle(info.getInfo());
                if (info.getMemo().isEmpty()) {
                    dialog.setMessage("你还没有写备注呢！");
                } else {
                    dialog.setMessage(info.getMemo());
                }
                dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                dialog.show();
            }
        });
        holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                Info info = myList.get(holder.getAdapterPosition());
                if (b) {
                    if(holder.alarm.isChecked()){
                        AlertDialog.Builder dialog=new AlertDialog.Builder(view.getContext());
                        dialog.setTitle("完成目标");
                        dialog.setMessage("还没有到完成目标的时间呢，你确定完成目标吗？");
                        dialog.setPositiveButton("好像没有", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                holder.checkBox.setChecked(false);
                            }
                        });
                        dialog.setNegativeButton("我已完成", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                holder.alarm.setChecked(false);
                                holder.textView1.setTextColor(Color.parseColor("#CFCFCF"));
                                holder.textView1.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
                                db = new DBHelper(view.getContext(), "InfoDB.db", null, 2).getReadableDatabase();
                                db.execSQL("update Info set status=1,alarm=0 where todo=? and memo=? and hour=? and minute=?",
                                        new String[]{info.getInfo(), info.getMemo(), "" + info.getHour(), "" + info.getMinute()});
                            }
                        });
                        dialog.show();
                    }else {
                        holder.textView1.setTextColor(Color.parseColor("#CFCFCF"));
                        holder.textView1.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
                        db = new DBHelper(view.getContext(), "InfoDB.db", null, 2).getReadableDatabase();
                        db.execSQL("update Info set status=1,alarm=0 where todo=? and memo=? and hour=? and minute=?",
                                new String[]{info.getInfo(), info.getMemo(), "" + info.getHour(), "" + info.getMinute()});
                    }
                } else {
                    holder.textView1.setTextColor(view.getResources().getColor(R.color.colorPrimary));
                    holder.textView1.getPaint().setFlags(0);
                    db.execSQL("update Info set status=0 where todo=? and memo=? and hour=? and minute=? and alarm=?",
                            new String[]{info.getInfo(), info.getMemo(), "" + info.getHour(), "" + info.getMinute(), "" + info.getAlarm()});
                }
            }
        });
        holder.alarm.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                Info info = myList.get(holder.getAdapterPosition());
                calendar = Calendar.getInstance();
                if (b) {
                    if (holder.checkBox.isChecked()) {
                        Toast.makeText(view.getContext(),"该任务已经完成，不需要提醒了",Toast.LENGTH_SHORT).show();
                        holder.alarm.setChecked(false);
                    } else {
                        Intent intent = new Intent(view.getContext(), AlarmService.class);
                        info.setAlarm(1);
                        intent.putExtra("info_data", info);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        view.getContext().startService(intent);
                        db = new DBHelper(view.getContext(), "InfoDB.db", null, 2).getReadableDatabase();
                        db.execSQL("update Info set alarm=1 where todo=? and memo=? and hour=? and minute=? and status=?",
                                new String[]{info.getInfo(), info.getMemo(), "" + info.getHour(), "" + info.getMinute(), "" + info.getStatus()});
                    }
                } else {
                    Intent intent = new Intent(view.getContext(), AlarmService.class);
                    info.setAlarm(0);
                    intent.putExtra("info_data", info);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    view.getContext().startService(intent);
                    db.execSQL("update Info set alarm=0 where todo=? and memo=? and hour=? and minute=? and status=?",
                            new String[]{info.getInfo(), info.getMemo(), "" + info.getHour(), "" + info.getMinute(), "" + info.getStatus()});
                }
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        calendar = Calendar.getInstance();
        Info info = myList.get(i);
        viewHolder.textView1.setText(info.getInfo());
        String hour, minute;
        hour = String.valueOf(info.getHour());
        minute = String.valueOf(info.getMinute());
        if (info.getHour() < 10) {
            hour = "0" + info.getHour();
        }
        if (info.getMinute() < 10) {
            minute = "0" + info.getMinute();
        }
        viewHolder.textView2.setText(hour + ":" + minute);


        if (info.getStatus() == 0) {
            viewHolder.checkBox.setChecked(false);
        } else {
            viewHolder.checkBox.setChecked(true);
        }
        if (info.getAlarm() == 0) {
            viewHolder.alarm.setChecked(false);
        } else {
            if ((info.getHour() * 60 + info.getMinute()) > (calendar.get(Calendar.HOUR_OF_DAY) * 60 + calendar.get(Calendar.MINUTE))) {
                viewHolder.alarm.setChecked(true);
            } else {
                viewHolder.alarm.setChecked(false);
            }

        }
    }

    @Override
    public int getItemCount() {
        return myList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textView1, textView2;
        CheckBox checkBox, alarm;
        View InfoView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            InfoView = itemView;
            textView1 = (TextView) itemView.findViewById(R.id.item_text);
            textView2 = (TextView) itemView.findViewById(R.id.input_time);
            checkBox = (CheckBox) itemView.findViewById(R.id.checkBox);
            alarm = (CheckBox) itemView.findViewById(R.id.alarmtimer);
        }
    }
}
