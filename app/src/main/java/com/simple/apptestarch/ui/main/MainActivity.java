package com.simple.apptestarch.ui.main;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import com.simple.apptestarch.R;
import com.simple.apptestarch.data.local.NewsDbSource;
import com.simple.apptestarch.data.remote.NewsRemoteSource;
import com.simple.apptestarch.domain.News;
import com.simple.apptestarch.ui.detail.DetailActivity;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements MainView {

    private MainPresenter mMainPresenter = new MainPresenter(new NewsDbSource(), new NewsRemoteSource(), new RefreshMonitor());
    RecyclerView mRecyclerView ;
    RecyclerViewDemoAdapter mAdapter = new RecyclerViewDemoAdapter() ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mMainPresenter.attach(getApplicationContext(), this);

        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView) ;
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                startActivity(new Intent(MainActivity.this, DetailActivity.class));
            }
        });

        mMainPresenter.fetchNews();
    }


    @Override
    public void onFetchNews(List<News> newsList) {
        mAdapter.addDatas(newsList);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMainPresenter.detach();
    }


    /**
     *
     */
    public final static class NewsViewHolder extends RecyclerView.ViewHolder {
        TextView titleTv;
        TextView pubTimeTv;

        public NewsViewHolder(View itemView) {
            super(itemView);
            titleTv = (TextView) itemView.findViewById(R.id.title_tv);
            pubTimeTv = (TextView) itemView.findViewById(R.id.time_tv);
        }
    }


    /**
     *
     */
    public static class RecyclerViewDemoAdapter extends RecyclerView.Adapter<NewsViewHolder> {

        private final List<News> mDataSet = new ArrayList<>();

        RecyclerViewDemoAdapter() {
        }

        public void addDatas(List<News> datas) {
            mDataSet.addAll(0, datas) ;
            notifyDataSetChanged();
        }

        @Override
        public NewsViewHolder onCreateViewHolder(
                ViewGroup viewGroup, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext()) ;
            return new NewsViewHolder(inflater.inflate(R.layout.news_item_layout, viewGroup, false));
        }

        @Override
        public void onBindViewHolder(final NewsViewHolder viewHolder, final int position) {
            viewHolder.titleTv.setText(mDataSet.get(position).title);
            viewHolder.pubTimeTv.setText(mDataSet.get(position).publishTime);
            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mOnItemClickListener != null ) {
                        mOnItemClickListener.onItemClick(null,viewHolder.itemView,  position, position);
                    }
                }
            });
        }

        AdapterView.OnItemClickListener mOnItemClickListener;

        public RecyclerViewDemoAdapter setOnItemClickListener(AdapterView.OnItemClickListener mOnItemClickListener) {
            this.mOnItemClickListener = mOnItemClickListener;
            return this;
        }

        @Override
        public int getItemCount() {
            return mDataSet.size();
        }
    }

}
