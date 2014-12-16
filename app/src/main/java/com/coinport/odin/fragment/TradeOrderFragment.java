package com.coinport.odin.fragment;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.format.Time;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.coinport.odin.App;
import com.coinport.odin.R;
import com.coinport.odin.activity.TradeActivity;
import com.coinport.odin.adapter.OrderAdapter;
import com.coinport.odin.library.ptr.PullToRefreshBase;
import com.coinport.odin.library.ptr.PullToRefreshListView;
import com.coinport.odin.network.NetworkAsyncTask;
import com.coinport.odin.network.NetworkRequest;
import com.coinport.odin.network.OnApiResponseListener;
import com.coinport.odin.obj.AccountInfo;
import com.coinport.odin.obj.OrderItem;
import com.coinport.odin.util.Constants;
import com.coinport.odin.util.Util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link TradeOrderFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link TradeOrderFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TradeOrderFragment extends Fragment {
    protected PullToRefreshListView refreshableView;
    protected PullToRefreshBase<ListView> headerRefreshView;
    protected PullToRefreshBase<ListView> footerRefreshView;
    private String inCurrency, outCurrency;

    protected OrderAdapter orderAdapter;
    private ArrayList<OrderItem> orderItems = new ArrayList<OrderItem>();

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private Time now = new Time();

    private final int LIMIT = 10;
    private int page = 1;
    private boolean loadAll = false;
    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment TradeOrderFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static TradeOrderFragment newInstance(String param1, String param2) {
        TradeOrderFragment fragment = new TradeOrderFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public TradeOrderFragment() {
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.trade_order_fragment, container, false);

        final TradeOrderFragment self = this;
        refreshableView = (PullToRefreshListView) view.findViewById(R.id.refreshable_view);
        refreshableView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                self.headerRefreshView = refreshView;
                fetchOrder(true, "header");
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                self.footerRefreshView = refreshView;
                fetchOrder(false, "footer");
            }
        });
        orderAdapter = new OrderAdapter(getActivity());
        refreshableView.getRefreshableView().setAdapter(orderAdapter);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        fetchOrder(true, "");
    }

    protected String getStatus() {
        return "1";
    }

    protected void fetchOrder(final boolean isRefresh, final String direction) {
        if (isRefresh) {
            page = 1;
            loadAll = false;
        }
//        else if (loadAll) {
//            if (!direction.equals(""))
//                refreshableView.onRefreshComplete();
//            return;
//        }
        AccountInfo ai = App.getAccount();
        String url = String.format(Constants.ORDER_URL, ai.uid, inCurrency, outCurrency);
        Map<String, String> params = new HashMap<>();
        params.put("limit", String.valueOf(LIMIT));
        params.put("page", String.valueOf(page));
        params.put("status", getStatus());
        NetworkAsyncTask task = new NetworkAsyncTask(url, Constants.HttpMethod.GET)
                .setOnSucceedListener(new OnApiResponseListener())
                .setOnFailedListener(new OnApiResponseListener())
                .setRenderListener(new NetworkAsyncTask.OnPostRenderListener() {
                    @Override
                    public void onRender(NetworkRequest s) {
                        if (s.getApiStatus() != NetworkRequest.ApiStatus.SUCCEED || loadAll) {
                            refreshableView.onRefreshComplete();
                            return;
                        }
                        JSONArray orderJsonList = Util.getJsonArrayByPath(s.getApiResult(), "data.items");
                        if (isRefresh)
                            orderItems.clear();
                        try {
                            for (int i = 0; i < orderJsonList.length(); ++i) {
                                orderItems.add(OrderItem.OrderItemBuilder.generateFromJson(
                                    orderJsonList.getJSONObject(i)));
                            }
                            if (orderJsonList.length() < LIMIT)
                                loadAll = true;
                            else
                                page += 1;
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        orderAdapter.setOrderItems(orderItems);
                        orderAdapter.notifyDataSetChanged();
                        if (!direction.equals("")) {
                            now.setToNow();
                            if (direction.equals("header")) {
                                String label = String.format(getString(R.string.last_updated_at), now.format("%Y-%m-%d %k:%M:%S"));
                                headerRefreshView.getLoadingLayoutProxy(true, false).setLastUpdatedLabel(label);
                            } else {
                                String label = String.format(getString(R.string.last_loaded_at), now.format("%Y-%m-%d %k:%M:%S"));
                                footerRefreshView.getLoadingLayoutProxy(false, true).setLastUpdatedLabel(label);
                            }
                            refreshableView.onRefreshComplete();
                        }
                    }
                });
        task.execute(params);
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
//
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
    }

}
