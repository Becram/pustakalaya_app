package com.ole.epustakalaya;

import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.ole.epustakalaya.models.ModelAudioBookDetails;
import com.ole.epustakalaya.retrofit_api.PustakalayaApiInterface;
import com.squareup.picasso.Picasso;

import retrofit.Call;
import retrofit.Callback;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;

/**
 * Created by bikram on 3/10/16.
 */
public class AudioDetailsFragment extends Fragment implements Callback<ModelAudioBookDetails> {

    private PustakalayaApiInterface APIInterface;
    private ImageView cover;
    private TextView text_author;
    private TextView text_language;
    private TextView text_title;
    private TextView text_views;
    private TextView text_reader;
    private TextView text_publisher;
    private TextView text_genre;
    private TextView text_chap_count;
    private TextView text_description;


    protected String BASE_URL="http://www.pustakalaya.org";
    private ProgressBar progress_detail;
    private Typeface face;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v= inflater.inflate(R.layout.tab_audio_details, container, false);
        cover= (ImageView) v.findViewById(R.id.image_cover);
        text_author= (TextView) v.findViewById(R.id.textView_author);
        text_language= (TextView) v.findViewById(R.id.textView_lanuage);
        text_title= (TextView) v.findViewById(R.id.textView_title);
        text_views= (TextView) v.findViewById(R.id.textView_views);
        text_publisher= (TextView) v.findViewById(R.id.textView_publisher);
        text_reader= (TextView) v.findViewById(R.id.textView_reader);
        text_genre= (TextView) v.findViewById(R.id.textView_genre);
        text_chap_count= (TextView) v.findViewById(R.id.textView_Chap_count);
        text_description= (TextView) v.findViewById(R.id.textView_description);
        progress_detail= (ProgressBar) v.findViewById(R.id.progress_details);



//        text_author.setText("radhav");
        progress_detail.setVisibility(View.VISIBLE);






        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://www.pustakalaya.org")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        APIInterface=retrofit.create(PustakalayaApiInterface.class);
//        face=Typeface.createFromAsset(getActivity().getAssets(), "fonts/Kelson Sans Light.otf");

        Log.d("Bookid from details", AudioAllAMainDetails.bookid);
        Call<ModelAudioBookDetails> call = APIInterface.getAudioBooksDetails(AudioAllAMainDetails.bookid);

        call.enqueue(this);
    }

    @Override
    public void onResponse(final Response<ModelAudioBookDetails> response, Retrofit retrofit) {



        new AsyncTask<Void, Void, Response<ModelAudioBookDetails>>() {
            @Override
            protected Response<ModelAudioBookDetails> doInBackground(Void... params) {

                return response;
            }

            @Override
            protected void onPostExecute(Response<ModelAudioBookDetails> modelAudioBookDetailsResponse) {
                super.onPostExecute(modelAudioBookDetailsResponse);
                progress_detail.setVisibility(View.GONE);
                Picasso.with(getContext()).load(BASE_URL + response.body().getContent().getImage())
                        .into(cover);
                text_title.setText("Title:" + response.body().getContent().getTitle());
            //    text_description.setTypeface(face);


                text_author.setText("Author:" + response.body().getContent().getAuthor());
                text_language.setText("Language:" + response.body().getContent().getLang());
                text_views.setText("Views:" + response.body().getContent().getViews());
                text_publisher.setText("Publisher:" + response.body().getContent().getPublisher());
                text_genre.setText("Genre:" + response.body().getContent().getGenre());
                text_reader.setText("Reader:" + response.body().getContent().getReader());
                text_description.setText("Description:" + response.body().getContent().getDesc());
                text_chap_count.setText("Chapter Count:"+String.valueOf(response.body().getContent().getChapters().size()));
                Log.d("Title:" , response.body().getContent().getTitle());
                Log.d("Author:",response.body().getContent().getAuthor());
                Log.d("Language:", response.body().getContent().getLang());
                Log.d("Views:" , response.body().getContent().getViews());
                Log.d("Publisher:" , response.body().getContent().getPublisher());
                Log.d("Reader:" , response.body().getContent().getReader());
                Log.d("Description:" , response.body().getContent().getDesc());
                Log.d("Chapter No:",response.body().getContent().getChapterCount());

            }
        }.execute();




//        text_views.setText("Views:"+response.body().getContent().getViews());
//        text_title.setText(response.body().getContent().getTitle();
//        text_title.setText(response.body().getContent().getTitle();

    }

    @Override
    public void onFailure(Throwable t) {

    }
}
