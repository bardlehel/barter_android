package com.barter.app;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.barter.app.BarterApplication;
import com.barter.app.BarterServer;
import com.barter.app.R;

import org.json.JSONException;
import android.app.Activity;

import java.lang.reflect.Field;


public class MainFragment extends Fragment
{

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);


        return rootView;
    }

    @Override
    public void onViewCreated (View view, Bundle savedInstanceState) {

    }


    public void barterGetMainNumbers(BarterApplication a) {
        BarterServer.IGetNumbersListener listener = new GetNumbersListener();
        String accessToken = a.accessToken;
        a.barterServer.getNumbers(accessToken, listener);
    }


    public class GetNumbersListener implements BarterServer.IGetNumbersListener {

        @Override
        public void callback(BarterServer.GetNumbersTask task) {
            TextView tvHaves = (TextView) getActivity().findViewById(R.id.tvHaves);
            TextView tvWants = (TextView) getActivity().findViewById(R.id.tvWants);
            try {
                if(task.numbers != null) {
                    tvHaves.setText(task.numbers.getString("haves") + " Haves");
                    tvWants.setText(task.numbers.getString("wants") + " Wants");
                }
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }

    }

    @Override
    public void onDetach() {
        super.onDetach();

        try {
            Field childFragmentManager = Fragment.class.getDeclaredField("mChildFragmentManager");
            childFragmentManager.setAccessible(true);
            childFragmentManager.set(this, null);

        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
