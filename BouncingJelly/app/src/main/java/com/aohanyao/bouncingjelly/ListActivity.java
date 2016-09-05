package com.aohanyao.bouncingjelly;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;

import com.aohanyao.bouncingjelly.adapter.RecyclerAdapter;
import com.aohanyao.jelly.library.ui.BouncingRecyclerView;

public class ListActivity extends AppCompatActivity {

    private BouncingRecyclerView rl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        rl = (BouncingRecyclerView) findViewById(R.id.rl);
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
