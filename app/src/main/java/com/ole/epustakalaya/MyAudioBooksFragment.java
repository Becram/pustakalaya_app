package com.ole.epustakalaya;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.ole.epustakalaya.interfacesAndAdaptors.AudioBookAdapter;
import com.ole.epustakalaya.interfacesAndAdaptors.ItemClickSupport;
import com.ole.epustakalaya.models.AllAudioBooks;
import com.ole.epustakalaya.models.RecycleItem;
import com.ole.epustakalaya.retrofit_api.PustakalayaApiInterface;

import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.ArrayList;
import java.util.List;

import retrofit.Call;
import retrofit.Callback;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;

/**
 * Created by bikram on 2/12/16.
 */
public class MyAudioBooksFragment extends Fragment implements Callback<AllAudioBooks> {

    private RecyclerView mRecyclerView;
    private ProgressBar progress;
    private LinearLayoutManager mLayoutManager;
    private PustakalayaApiInterface APIInterface;
    private AudioBookAdapter mAdapter;
    private List<RecycleItem> result;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootview= inflater.inflate(R.layout.audio_books, container, false);


        mRecyclerView = (RecyclerView) rootview.findViewById(R.id.activity_main_recycler_view);
        progress= (ProgressBar)rootview.findViewById(R.id.progress);
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);
        Retrofit_Reloader("date");
        progress.setVisibility(View.VISIBLE);



        return rootview;
    }


    public void Retrofit_Reloader(String s){

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://www.pustakalaya.org")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        APIInterface = retrofit.create(PustakalayaApiInterface.class);
//        Log.d("sort",sort);
        Call<AllAudioBooks> call = APIInterface.getAllAudioBooks(s);
        //asynchronous call
        call.enqueue(this);


    }

    @Override
    public void onResponse(final Response<AllAudioBooks> response, Retrofit retrofit) {

        progress.setVisibility(View.GONE);

//        Log.d("size of book list", String.valueOf(response.body().getContent().size()));
//        Log.d("size of book list", String.valueOf(response.body().getContent().get(109).getTitle()));




        createList(response);
        mAdapter = new AudioBookAdapter(result,mRecyclerView,getContext());
        mRecyclerView.clearFocus();
        mRecyclerView.setAdapter(mAdapter);


//        RecyclerAdaper adapter = (RecyclerAdaper) mRecyclerView.getAdapter();
        Log.d("snkbdj", ToStringBuilder.reflectionToString(response));
         mAdapter.notifyDataSetChanged();

        ItemClickSupport.addTo(mRecyclerView).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {


            @Override
            public void onItemClicked(RecyclerView recyclerView, int position, View v) {


                Log.d("Cool", response.body().getContent().get(position).getTitle());

                Intent i = new Intent(getActivity(), AudioDetails.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                i.putExtra("bookID", response.body().getContent().get(position).getId());
                Log.d("book id", response.body().getContent().get(position).getId());
                startActivity(i);


            }

        });

    }

    @Override
    public void onFailure(Throwable t) {

    }

    private void createList(Response<AllAudioBooks> t) {

        result = new ArrayList<>();
        for (int i = 0; i < t.body().getContent().size(); i++) {
            RecycleItem ci = new RecycleItem();
            ci.title = t.body().getContent().get(i).getTitle();
            ci.image=t.body().getContent().get(i).getCover();
            ci.Author=t.body().getContent().get(i).getAuthor();
            ci.id=t.body().getContent().get(i).getId();
            Log.d("get count", String.valueOf(t.body().getCount())) ;


            result.add(ci);

        }

//        return result;


    }
}
