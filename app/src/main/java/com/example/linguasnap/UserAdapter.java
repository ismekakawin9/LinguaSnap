package com.example.linguasnap;


import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

    class UserAdapter extends BaseAdapter {

    final ArrayList<User> userList;

        UserAdapter(ArrayList<User> userList){
    this.userList= userList;
    }
    @Override
    public int getCount() {
        return userList.size();
    }
    @Override
    public Object getItem(int position) {
        return userList.get(position);
    }
    @Override
    public long getItemId(int i) {
        return 0;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View viewlist;
        if (convertView == null) {
            viewlist = View.inflate(parent.getContext(), R.layout.itemuser, null);
        }
        else viewlist = convertView;
        //Bind sữ liệu phần tử vào View
        User user = (User) getItem(position);
        ((TextView) viewlist.findViewById(R.id.txt_from)).setText(String.format( user.from));
        ((TextView) viewlist.findViewById(R.id.txt_to)).setText(String.format( user.to));
        ((TextView) viewlist.findViewById(R.id.txt_input)).setText(String.format( user.inputtext));
        ((TextView) viewlist.findViewById(R.id.txt_translate)).setText(String.format( user.translatetext));
        return viewlist;
    }
}