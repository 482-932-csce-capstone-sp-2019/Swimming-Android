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


public class UserRecyclerViewAdapter extends RecyclerView.Adapter<UserRecyclerViewAdapter.ViewHolder> {


    private ArrayList<Tuple<String,Integer>> userList;
    private Context mContext;

    public UserRecyclerViewAdapter(Context context, ArrayList<Tuple<String,Integer>> uList) {
        userList = uList;
        mContext = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_userlistitem, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        //Set text of name
        holder.userName.setText(userList.get(position).first());

        //Set onClick
        holder.userListLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //FIXME: Move to next page with current userID stored.
                Intent i = new Intent(mContext, MenuActivity.class);
                //FIXME: way to pass db in intent?
                //String message = SELECTED USER;
                i.putExtra("UserID", userList.get(position).second());
                i.putExtra("UserName", userList.get(position).first());
                mContext.startActivity(i);


            }
        });
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView userName;
        RelativeLayout userListLayout;

        public ViewHolder(View itemView) {
            super(itemView);
            userName = itemView.findViewById(R.id.userListItem);
            userListLayout = itemView.findViewById(R.id.userlist_layout);
        }
    }
}

