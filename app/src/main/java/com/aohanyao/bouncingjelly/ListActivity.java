package com.aohanyao.bouncingjelly;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import com.aohanyao.bouncingjelly.adapter.RecyclerAdapter;

public class ListActivity extends AppCompatActivity {

    private RecyclerView rl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        rl = (RecyclerView) findViewById(R.id.rl);
        initRecyclerView();
    }

    private void initRecyclerView() {
        RecyclerAdapter mAdapter = new RecyclerAdapter(this);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rl.setLayoutManager(layoutManager);
        rl.setAdapter(mAdapter);
    }

}
