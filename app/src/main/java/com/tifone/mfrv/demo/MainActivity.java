package com.tifone.mfrv.demo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;

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
        mTestDataSet.add("abc");
        mTestDataSet.add("111");
        mTestDataSet.add("abc");
        mTestDataSet.add("111");
        mTestDataSet.add("abc");
        mTestDataSet.add("222");

        mTestDataSet2 = new ArrayList<>();
        mTestDataSet2.add("123");
        mTestDataSet2.add("fg");
        mTestDataSet2.add("abc");
        mTestDataSet2.add("357");
        mTestDataSet2.add("abc");
        mTestDataSet2.add("867");
        //setupHeaderFooterRecyclerView();
        setupPullLoadRecyclerView();
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
        adapter.setDataSet(mTestDataSet);
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
                },2000);
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
}
