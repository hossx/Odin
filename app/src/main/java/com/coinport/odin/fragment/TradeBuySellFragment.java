package com.coinport.odin.fragment;

import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.coinport.odin.R;
import com.coinport.odin.activity.TradeActivity;
import com.coinport.odin.adapter.DepthAdapter;
import com.coinport.odin.adapter.TickerViewAdapter;
import com.coinport.odin.obj.DepthItem;
import com.coinport.odin.obj.TickerItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link TradeBuySellFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link TradeBuySellFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TradeBuySellFragment extends Fragment {
    private JSONObject depthResult;
    private ListView buyListView;
    private ListView sellListView;
    private DepthAdapter buyAdapter;
    private DepthAdapter sellAdapter;
    private String inCurrency, outCurrency;

    private Timer timer = new Timer();
    private TimerTask fetchDepthTask = null;
    ArrayList<DepthItem> buyItems = new ArrayList<DepthItem>();
    ArrayList<DepthItem> sellItems = new ArrayList<DepthItem>();
    private final Handler depthHandler = new Handler();

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment TradeBuyFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static TradeBuySellFragment newInstance(String param1, String param2) {
        TradeBuySellFragment fragment = new TradeBuySellFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public TradeBuySellFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        inCurrency = ((TradeActivity)getActivity()).getInCurrency();
        outCurrency = ((TradeActivity)getActivity()).getOutCurrency();
    }

    @Override
    public void onStop() {
        super.onStop();
        stopFetchData();
    }

    @Override
    public void onStart() {
        super.onStart();
        startFetchData();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View buySellView = inflater.inflate(R.layout.trade_buy_sell_fragment, container, false);
        buyListView = (ListView) buySellView.findViewById(R.id.buy_depth);
        sellListView = (ListView) buySellView.findViewById(R.id.sell_depth);
        buyAdapter = new DepthAdapter(getActivity());
        buyListView.setAdapter(buyAdapter);
        sellAdapter = new DepthAdapter(getActivity());
        sellListView.setAdapter(sellAdapter);
        return buySellView;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

//    @Override
//    public void onAttach(Activity activity) {
//        super.onAttach(activity);
//        try {
//            mListener = (OnFragmentInteractionListener) activity;
//        } catch (ClassCastException e) {
//            throw new ClassCastException(activity.toString()
//                    + " must implement OnFragmentInteractionListener");
//        }
//    }

//    @Override
//    public void onDetach() {
//        super.onDetach();
//        mListener = null;
//    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }

    private void startFetchData() {
        timer.cancel();
        if (fetchDepthTask != null)
            fetchDepthTask.cancel();
        fetchDepthTask = new FetchDepthTask();
        timer = new Timer();
        timer.schedule(fetchDepthTask, 0, 5000);
    }

    private void stopFetchData() {
        if (timer != null)
            timer.cancel();
        if (fetchDepthTask != null)
            fetchDepthTask.cancel();
    }

    private class FetchDepthTask extends TimerTask {

        @Override
        public void run() {
            buyItems.clear();
            sellItems.clear();
            try {
                String file;
                file = "btc_cny_depth.json";
                InputStream is = getActivity().getAssets().open(file);
                int size = is.available();
                byte[] buffer = new byte[size];
                is.read(buffer);
                is.close();
                String bufferString = new String(buffer);
                depthResult = new JSONObject(bufferString);

                JSONArray buyJsonList = depthResult.getJSONObject("data").getJSONArray("b");
                for (int i = 0; i < buyJsonList.length(); ++i) {
                    JSONObject jsonObj = buyJsonList.getJSONObject(i);
                    buyItems.add(DepthItem.DepthItemBuilder.generateFromJson(jsonObj, true));
                }
                JSONArray sellJsonList = depthResult.getJSONObject("data").getJSONArray("a");
                for (int i = 0; i < sellJsonList.length(); ++i) {
                    JSONObject jsonObj = sellJsonList.getJSONObject(i);
                    sellItems.add(0, DepthItem.DepthItemBuilder.generateFromJson(jsonObj, false));
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            depthHandler.post(new Runnable() {
                @Override
                public void run() {
                    buyAdapter.setDepthItems(buyItems);
                    sellAdapter.setDepthItems(sellItems);
                    buyAdapter.notifyDataSetChanged();
                    sellAdapter.notifyDataSetChanged();
                }
            });
        }
    }
}
