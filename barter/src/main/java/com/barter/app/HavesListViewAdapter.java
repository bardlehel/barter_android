package com.barter.app;

import java.util.List;

import com.barter.app.BarterServer.GetHavesTask.Have;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;


public class HavesListViewAdapter extends ArrayAdapter<String> {
  private final Context context;
  private final List<Have> haves;

  static class ViewHolder {
    public TextView text;
    public ImageView image;
  }

  public HavesListViewAdapter(Context context, List<Have> haves) {
    super(context, R.layout.have_item);
    this.context = context;
    this.haves = haves;
  }

  @Override
  public View getView(int position, View convertView, ViewGroup parent) {
    View rowView = convertView;
    // reuse views
    if (rowView == null) {
      LayoutInflater inflater = ((Activity) context).getLayoutInflater();
      rowView = inflater.inflate(R.layout.have_item, null);
      // configure view holder
      ViewHolder viewHolder = new ViewHolder();
      viewHolder.text = (TextView) rowView.findViewById(R.id.tvHaveItem);
      viewHolder.image = (ImageView) rowView
          .findViewById(R.id.imgHaveItem);
      rowView.setTag(viewHolder);
    }

    // fill data
    ViewHolder holder = (ViewHolder) rowView.getTag();
    String s = haves.get(position).title;
    holder.text.setText(s);
    holder.image.setImageResource(R.drawable.ic_launcher);

    return rowView;
  }
} 
