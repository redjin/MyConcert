package app.filanninogiovanni.sms16.ivu.di.uniba.it.myconcert;


import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.graphics.Palette;
import android.transition.Fade;
import android.transition.Transition;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import app.filanninogiovanni.sms16.ivu.di.uniba.it.myconcert.Adapter.ItemSongPlayAdapter;
import app.filanninogiovanni.sms16.ivu.di.uniba.it.myconcert.Artista.ArtistaHomeFragment;
import app.filanninogiovanni.sms16.ivu.di.uniba.it.myconcert.Entities.Song;
import app.filanninogiovanni.sms16.ivu.di.uniba.it.myconcert.Utility.DeezerPlayTrack;

public class DetailActivity extends Activity implements View.OnClickListener {

    public static final String EXTRA_PARAM_ID = "place_id";
    private ListView mList;
    private ImageView mImageView;
    private TextView nomeArtista;
    private TextView citta;
    private TextView luogo;
    private TextView dataTXT;
    private FloatingActionButton sendplaylist;
    private LinearLayout mTitleHolder;
    private ArrayList<String> setlist;
    private RelativeLayout mRevealView;
    private String nome;
    private String data;
    private String cit;
    private String lu;

    private boolean isEditTextVisible;
    private ImageButton partecipero;
    private ImageButton editSongList;
    private InputMethodManager mInputManager;
    private ArrayList<String> mTodoList;
    private ArrayAdapter mToDoAdapter;
    private ArrayList<Song> songArrayList;
    int defaultColor;
    int color;
    private static final String SUCCESS_TAG = "success";
    private RequestQueue requestQueue;
    private ItemSongPlayAdapter itemSongPlayAdapter;
    private boolean visible=false;
    private String idConcerto;
    private boolean partecipo;
    private SharedPreferences sharedPreferences;
    private String PREFERENCES = "";
    private int success = 0;

    private View.OnClickListener setPartecipation = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
                String URL = "http://mymusiclive.altervista.org/setPartecipation.php?username=" + '"' + loginFragment.actualUsername + '"' + "&idConcerto=" +
                        "'"+ idConcerto+ "'";
            JSONObject jsonObject = new JSONObject();
            Log.d("url" , URL);
                JsonObjectRequest arrayRequest = new JsonObjectRequest(URL, jsonObject, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        JSONObject test = response;
                        try {
                            success = test.getInt(SUCCESS_TAG);
                            Log.d("mi fido","" + success);
                        } catch (JSONException e1) {
                            e1.printStackTrace();
                        }
                            if (success == 1) {
                                partecipero.setImageResource(R.drawable.thumbsup_selected);
                                editSongList.setVisibility(View.VISIBLE);
                                editor.putBoolean(idConcerto + loginFragment.actualUsername, true).commit();
                            }
                        else {
                            editor.putBoolean(idConcerto + loginFragment.actualUsername, false).commit();
                            partecipero.setImageResource(R.drawable.thumbsup);
                            sendplaylist.setVisibility(View.GONE);
                            editSongList.setVisibility(View.GONE);
                            itemSongPlayAdapter.setGone();
                        }
                    }}, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }


                });


                requestQueue.add(arrayRequest);

            }
    };




    private View.OnClickListener editSong = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(!visible) {
                itemSongPlayAdapter.setVisible();
                sendplaylist.setVisibility(View.VISIBLE);
                visible=true;
            }else {
                itemSongPlayAdapter.setGone();
                sendplaylist.setVisibility(View.GONE);
                visible=false;
            }
            Log.d("Wanna","Chi sei goku non lo sai");
        }
    };

    private ListView.OnItemLongClickListener editSongs = new AdapterView.OnItemLongClickListener() {
        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.card_detail);
        PREFERENCES = getResources().getString(R.string.partecipero_preferences);
        idConcerto = getIntent().getStringExtra("id");
        Log.d("ID",""+ idConcerto);
        //idConcerto="10";
        //Log.d("ID",""+idConcerto);

        sharedPreferences = getSharedPreferences(PREFERENCES,MODE_PRIVATE);
        partecipo = sharedPreferences.getBoolean(idConcerto+loginFragment.actualUsername,false);
        Log.d("ILvalosr","" + partecipo);
        setlist = getIntent().getStringArrayListExtra("canzoni");
        cit=getIntent().getStringExtra("citta");
        lu=getIntent().getStringExtra("luogo");
        nome=getIntent().getStringExtra("cantante");
        data=getIntent().getStringExtra("data");
        mList = (ListView) findViewById(R.id.list);
        mImageView = (ImageView) findViewById(R.id.placeImage);
        nomeArtista = (TextView) findViewById(R.id.artistaDett);
        dataTXT=(TextView) findViewById(R.id.dataDett);
        sendplaylist=(FloatingActionButton) findViewById(R.id.playlist);
        partecipero = (ImageButton) findViewById(R.id.partecipero);
        editSongList = (ImageButton) findViewById(R.id.editSongList);
        mTitleHolder = (LinearLayout) findViewById(R.id.placeNameHolder);
        mRevealView = (RelativeLayout) findViewById(R.id.llEditTextHolder);
        citta=(TextView)findViewById(R.id.cittaDettagli);
        luogo=(TextView)findViewById(R.id.luogoDettagli);
        defaultColor = getResources().getColor(R.color.colorPrimaryDark);
        mTitleHolder.setBackgroundColor(color);
        final Context context=this;
        Transition fade = new Fade();
        songArrayList = getSongArray(nome,setlist);
        setUpAdapter();
        requestQueue = Volley.newRequestQueue(this);
        partecipero.setOnClickListener(setPartecipation);
        editSongList.setOnClickListener(editSong);

        if(partecipo){
            partecipero.setImageResource(R.drawable.thumbsup_selected);
            sendplaylist.setVisibility(View.VISIBLE);
            editSongList.setVisibility(View.VISIBLE);
        } else {
            partecipero.setImageResource(R.drawable.thumbsup);
        }

        mInputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        mRevealView.setVisibility(View.INVISIBLE);
        if(ResultFragment.bitmap!=null) {

            Palette palette = Palette.generate(ResultFragment.bitmap);
            int standard=getResources().getColor(R.color.colorPrimary);
            int vibrant = palette.getVibrantColor(standard);
            mTitleHolder.setBackgroundColor(vibrant);
            mImageView.setImageBitmap(ResultFragment.bitmap);
        }
        else {
            Bitmap bit= BitmapFactory.decodeResource(getResources(),R.drawable.concertimilano);
            mImageView.setImageBitmap(bit);
            Palette palette = Palette.generate(bit);
            int standard=getResources().getColor(R.color.colorPrimary);
            int vibrant = palette.getVibrantColor(standard);
            mTitleHolder.setBackgroundColor(vibrant);

        }
        isEditTextVisible = false;fade.excludeTarget(android.R.id.statusBarBackground, true);
        fade.excludeTarget(android.R.id.navigationBarBackground, true);
        getWindow().setExitTransition(fade);
        getWindow().setEnterTransition(fade);
        citta.setVisibility(View.GONE);
        luogo.setVisibility(View.GONE);
        loadPlace();
    }

    private void setUpAdapter() {
        itemSongPlayAdapter = new ItemSongPlayAdapter(this,R.layout.itemplaysong,songArrayList);
        mList.setAdapter(itemSongPlayAdapter);

    }



    private void loadPlace() {

        dataTXT.setText(data);
       citta.setText(cit);
       luogo.setText(lu);
        citta.setVisibility(View.VISIBLE);
        luogo.setVisibility(View.VISIBLE);

        nomeArtista.setText(nome);

    }

    private void windowTransition() {

    }

    private void addToDo(String todo) {
        mTodoList.add(todo);
    }

    private void getPhoto() {


    }

    private void colorize(Bitmap photo) {
    }

    private void applyPalette() {

    }

    @Override
    public void onClick(View v) {
    }

    private void revealEditText(LinearLayout view) {

    }

    private void hideEditText(final LinearLayout view) {

    }

    private ArrayList<Song> getSongArray(String artist, ArrayList<String> songs){
        ArrayList<Song> songArray = new ArrayList<Song>();
        for(String s: songs){
            songArray.add(new Song(s,artist));
        }

        return songArray;
    }
    @Override
    public void onBackPressed() {
        citta.setVisibility(View.GONE);
        luogo.setVisibility(View.GONE);
        super.onBackPressed();
    }

}