package com.example.swimmingwearable;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;


public class PoolRecyclerViewAdapter extends RecyclerView.Adapter<PoolRecyclerViewAdapter.ViewHolder> {


    private ArrayList<Tuple<String,Integer>> poolList;
    private Context mContext;
    private Integer userID;

    public PoolRecyclerViewAdapter(Context context, ArrayList<Tuple<String,Integer>> pList, Integer uID) {
        poolList = pList;
        mContext = context;
        userID = uID;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_poollistitem, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        //Set text of name
        holder.poolName.setText(poolList.get(position).first());

        //Set onClick
        holder.poolListLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //FIXME: Move to next page with current userID stored.
                Intent i = new Intent(mContext, WorkoutCreationActivity.class);


                //String message = SELECTED USER;
                i.putExtra("UserID", userID);
                i.putExtra("PoolID", poolList.get(position).second());
                //FIXME: need pool length?
                //i.putExtra("PoolLength", 7);
                mContext.startActivity(i);


            }
        });
    }

    @Override
    public int getItemCount() {
        return poolList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView poolName;
        RelativeLayout poolListLayout;

        public ViewHolder(View itemView) {
            super(itemView);
            poolName = itemView.findViewById(R.id.poolListItem);
            poolListLayout = itemView.findViewById(R.id.poollist_layout);
        }
    }
}

