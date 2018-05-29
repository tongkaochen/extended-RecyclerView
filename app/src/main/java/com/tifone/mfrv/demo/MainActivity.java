package com.tifone.mfrv.demo;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tifone.mfrv.pullload.PullLoadRecyclerView;
import com.tifone.mfrv.pullload.adapter.WrapHeaderFooterAdapter;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private PullLoadRecyclerView mPullLoadRecyclerView;
    private List<String> mTestDataSet;
    private LayoutInflater mInflater;
    private List<String> mTestDataSet2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mTestDataSet = new ArrayList<>();
        mInflater = LayoutInflater.from(this);
        for (int i = 0; i < 10; i++) {
            mTestDataSet.add("Index " + i);
        }

        mTestDataSet2 = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            mTestDataSet2.add("Index : " + i);
        }
        //setupHeaderFooterRecyclerView();
        setupPullLoadRecyclerView();
        //setupOriginRecyclerView();
    }

    private void setupHeaderFooterRecyclerView() {
        setContentView(R.layout.activity_main);
        mRecyclerView = findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        final MyMultiItemAdapter adapter = new MyMultiItemAdapter(this);
        adapter.setDataSet(mTestDataSet);
        final WrapHeaderFooterAdapter wrapAdapter = new WrapHeaderFooterAdapter(adapter);
        final View header = mInflater.inflate(R.layout.recycler_view_header_sample_layout, null);
        final View footer = mInflater.inflate(R.layout.recycler_view_footer_sample_layout, null);
        wrapAdapter.addHeaderView(header);
        wrapAdapter.addFooterView(footer);
        mRecyclerView.setAdapter(wrapAdapter);
    }

    private void setupPullLoadRecyclerView() {
        setContentView(R.layout.activity_main_pull_load);

        mPullLoadRecyclerView = findViewById(R.id.pull_load_recycler_view);
        mPullLoadRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        final MyMultiItemAdapter adapter = new MyMultiItemAdapter(this);
        //adapter.setDataSet(mTestDataSet);
        mPullLoadRecyclerView.setAdapter(adapter);
//        mPullLoadRecyclerView.setLoadMoreBehavior(PullLoadRecyclerView.LOAD_BEHAVIOR_STYLE_NORMAL);
        mPullLoadRecyclerView.setOnLoadListener(new PullLoadRecyclerView.OnLoadListener() {
            @Override
            public void onLoadStarted() {
                mPullLoadRecyclerView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mPullLoadRecyclerView.notifyLoadComplete();
                        adapter.addDataSet(mTestDataSet2);
                    }
                }, 2000);
            }
        });
        mPullLoadRecyclerView.setOnRefreshListener(new PullLoadRecyclerView.OnRefreshListener() {
            @Override
            public void onRefreshStarted() {
                mPullLoadRecyclerView.postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        mPullLoadRecyclerView.notifyRefreshCompleted();
                        adapter.addDataSet(mTestDataSet2);
                    }
                }, 1000);
            }
        });
    }

    private void setupOriginRecyclerView() {
        setContentView(R.layout.activity_main_origin);
        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        MyAdapter adapter = new MyAdapter(mTestDataSet);
        //MyMultiItemAdapter adapter = new MyMultiItemAdapter(this);
        //adapter.setDataSet(mTestDataSet);
        recyclerView.setAdapter(adapter);
    }

    class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {
        List<String> mDataSet;

        private MyAdapter(List<String> dataSet) {
            mDataSet = dataSet;
        }

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_view_item, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
            Log.e("tifone", "onBindViewHolder = " + position);
            holder.textView.setText(mDataSet.get(position));
        }

        @Override
        public int getItemCount() {
            return mDataSet.size();
        }

        @Override
        public int getItemViewType(int position) {
            Log.e("tifone", "position = " + position);
            return super.getItemViewType(position);
        }

        class MyViewHolder extends RecyclerView.ViewHolder {
            private TextView textView;

            public MyViewHolder(View itemView) {
                super(itemView);
                textView = itemView.findViewById(R.id.title);
            }
        }
    }
}
