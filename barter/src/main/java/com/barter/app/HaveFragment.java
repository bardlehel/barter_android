package com.barter.app;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import com.barter.app.BarterApplication;
import com.barter.app.BarterServer;
import com.barter.app.HavesListViewAdapter;
import com.barter.app.R;

public class HaveFragment extends Fragment
{
    private BarterApplication app = null;

    public class HavesListener implements BarterServer.IGetHavesListener {

        @Override
        public void callback(BarterServer.GetHavesTask task) {
            LayoutInflater inflater = LayoutInflater.from(getActivity().getBaseContext());
            adapter = new HavesListViewAdapter(
                    inflater.getContext(),
                    task.haves);

            ListView lv = (ListView) getView().findViewById(R.id.have_listview);
            lv.setAdapter(adapter);
        }
    }

    private HavesListViewAdapter adapter;


    //@Override
    // public void onListItemClick(ListView l, View v, int position, long id) {
    //new CustomToast(getActivity(), numbers_digits[(int) id]);
    //}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        View rootView = inflater.inflate(R.layout.have_layout, container, false);
        app = (BarterApplication)getActivity().getApplication();
        return rootView;
    }

    @Override
    public void onViewCreated (View view, Bundle savedInstanceState) {
        Button b = (Button) getView().findViewById(R.id.addhave);
        b.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                //numbers_text.add("blah");
                adapter.notifyDataSetChanged();
            }
        });

    }

    public void barterGetHaves() {
        BarterServer.IGetHavesListener listener = new HavesListener();
        BarterApplication app = ((BarterApplication)getActivity().getApplication());
        String accessToken = app.accessToken;
        app.barterServer.getHaves(accessToken, listener);
    }


}
