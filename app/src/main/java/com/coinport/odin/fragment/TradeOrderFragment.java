package com.coinport.odin.fragment;

import android.app.Activity;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.format.DateUtils;
import android.text.format.Time;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.coinport.odin.R;
import com.coinport.odin.layout.RefreshableView;
import com.coinport.odin.library.ptr.PullToRefreshBase;
import com.coinport.odin.library.ptr.PullToRefreshListView;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link TradeOrderFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link TradeOrderFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TradeOrderFragment extends Fragment {
    private PullToRefreshListView refreshableView;
    private PullToRefreshBase<ListView> headerRefreshView;
    private PullToRefreshBase<ListView> footerRefreshView;

    private ListView listView;
    private ArrayAdapter<String> adapter;
    private String[] items = { "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L" };

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private Time now = new Time();
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
                new GetDataTask("header").execute();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                self.footerRefreshView = refreshView;
                new GetDataTask("footer").execute();
            }
        });
//        listView = (ListView) view.findViewById(R.id.list_view);
//        adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, items);
//        listView.setAdapter(adapter);
//        refreshableView.setOnRefreshListener(new RefreshableView.PullToRefreshListener() {
//            @Override
//            public void onRefresh() {
//                try {
//                    Thread.sleep(3000);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//                refreshableView.finishRefreshing();
//            }
//        }, 0);
        return view;
    }

    private class GetDataTask extends AsyncTask<Void, Void, String[]> {
        private String direction;
        public GetDataTask(String direction) {
            this.direction = direction;
        }
        @Override
        protected String[] doInBackground(Void... params) {
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return new String[0];
        }

        @Override
        protected void onPostExecute(String[] result) {
            // Call onRefreshComplete when the list has been refreshed.
            now.setToNow();
            if (direction == "header") {
                String label = String.format(getString(R.string.last_updated_at), now.format("%Y-%m-%d %k:%M:%S"));
                headerRefreshView.getLoadingLayoutProxy(true, false).setLastUpdatedLabel(label);
            } else {
                String label = String.format(getString(R.string.last_loaded_at), now.format("%Y-%m-%d %k:%M:%S"));
                footerRefreshView.getLoadingLayoutProxy(false, true).setLastUpdatedLabel(label);
            }
            refreshableView.onRefreshComplete();
            super.onPostExecute(result);
        }
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
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }

}
