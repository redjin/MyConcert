package app.filanninogiovanni.sms16.ivu.di.uniba.it.myconcert.Artista;

/**
 * Created by Michele on 01/07/2016.
 */

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import app.filanninogiovanni.sms16.ivu.di.uniba.it.myconcert.BigScreenUtility.TwitterList;
import app.filanninogiovanni.sms16.ivu.di.uniba.it.myconcert.Entities.Concert;
import app.filanninogiovanni.sms16.ivu.di.uniba.it.myconcert.Entities.Setlist;
import app.filanninogiovanni.sms16.ivu.di.uniba.it.myconcert.MainActivity;
import app.filanninogiovanni.sms16.ivu.di.uniba.it.myconcert.R;
import io.fabric.sdk.android.Fabric;


import app.filanninogiovanni.sms16.ivu.di.uniba.it.myconcert.Adapter.AdapterItemDrawer;


/**
 * Created by Giovanni on 15/06/2016.
 */
public class ArtistaHome extends AppCompatActivity {

    private String urlImmagine;
    private String nomeArtistaString;
    private String cognomeArtistaString;
    private String aliasArtistaString;
    private RecyclerView recyclerView;
    private DrawerLayout drawerLayout;
    public String data;
    private String home;
    private String attivi;
    private String schermiGrandi;
    private String favorite;

    public String postoConcerto;
    public String cittaConcerto;
    public String idConcerto;
    RequestQueue requestQueue;
    ProgressDialog dialog;
    RecyclerView.LayoutManager mLayoutManager;
    public String pseArtista;
    ArrayList<Setlist> concerti=new ArrayList<Setlist>();
    FragmentManager fragmentManager;
    String urlPHPpart = "http://mymusiclive.altervista.org/concertiAttiviArtista.php?username=";
String stato;
    Context context;
    Toolbar toolbar;
    static final boolean fatto=false;
    private static final String TWITTER_KEY = "9R1qMlXL3qRX4wwkKasPn6yvE";
    private static final String TWITTER_SECRET = "kTZ7Z9aU0b04igbUAp12AjgR0tcXXnHvPVc90E0t6aRUx5bh24";


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.artistahome);
        if(savedInstanceState!=null)
        {
            stato=savedInstanceState.getString("stato");
        }
        fragmentManager =getFragmentManager();
        urlImmagine=getIntent().getStringExtra("url");
        nomeArtistaString=getIntent().getStringExtra("nome");
        cognomeArtistaString=getIntent().getStringExtra("cognome");
        aliasArtistaString=getIntent().getStringExtra("alias");
        context = this;
        requestQueue = Volley.newRequestQueue(this);
        toolbar=(Toolbar)findViewById(R.id.tool_bar_artista) ;
        setSupportActionBar(toolbar);
        home=getResources().getString(R.string.home);
        attivi=getResources().getString(R.string.concertiAttivi);
        schermiGrandi=getResources().getString(R.string.schermiGrandi);
        favorite=getResources().getString(R.string.preferiteDaiFan);
        final String[]  optionDrawer= {home,attivi,schermiGrandi,favorite };
        int ICONS[] = {R.drawable.ic_home_black_24dp,R.drawable.ic_library_music_black_24dp,R.drawable.ic_tv_black_24dp,R.drawable.ic_playlist_add_check_black_24dp};
        TwitterAuthConfig authConfig = new TwitterAuthConfig(TWITTER_KEY, TWITTER_SECRET);
        Fabric.with(this, new Twitter(authConfig));
        recyclerView = (RecyclerView) findViewById(R.id.left_drawer);
        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.account);
        final AdapterItemDrawer adapterItemDrawer =new AdapterItemDrawer(optionDrawer,ICONS,nomeArtistaString+" "+cognomeArtistaString,aliasArtistaString,bitmap,this);


        recyclerView.setAdapter(adapterItemDrawer);
        mLayoutManager=new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout); //information per la comparsa del menu laterale
        ActionBarDrawerToggle mDrowerToggle =new ActionBarDrawerToggle(this,drawerLayout,
                toolbar,R.string.navigation_drawer_open,R.string.navigation_drawer_close){

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                if(ArtistaHomeFragment.immagine!=null&&!fatto){
                    adapterItemDrawer.setImage(ArtistaHomeFragment.immagine);
                }
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }
        };
        drawerLayout.setDrawerListener(mDrowerToggle);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        mDrowerToggle.syncState();

        AdapterItemDrawer.OnItemClickListener onItemClickListener= new AdapterItemDrawer.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position, String scelta) {
                Fragment current=fragmentManager.findFragmentById(R.id.content_frame);
                if(optionDrawer[position].compareToIgnoreCase(attivi)==0) {
                    if (current != null && !(current instanceof ResultFragmentArtisti)) {
                        drawerLayout.closeDrawers();
                        dialog = new ProgressDialog(context);
                        dialog.setMessage(getResources().getString(R.string.loading));
                        dialog.show();
                        ResultFragmentArtisti resultFragmentArtisti = new ResultFragmentArtisti();
                        goToConcert(resultFragmentArtisti);
                        concerti.clear();
                    }
                }
                else{
                    if(optionDrawer[position].compareToIgnoreCase(home)==0){
                        if(current!=null && !(current instanceof ArtistaHomeFragment)){
                            drawerLayout.closeDrawers();
                            ArtistaHomeFragment artistaHome=new ArtistaHomeFragment();
                            artistaHome.setNomeArtistaString(nomeArtistaString);
                            artistaHome.setCognomeArtitaString(cognomeArtistaString);
                            artistaHome.setAliasArtistaString(aliasArtistaString);
                            artistaHome.setUrlImmagine(urlImmagine);
                            startTransiction(artistaHome);

                        }
                    }
                    else {
                        if(optionDrawer[position].compareToIgnoreCase(schermiGrandi)==0){
                            if(current!=null && !(current instanceof TwitterList)) {
                                drawerLayout.closeDrawers();
                                TwitterList twitterList = new TwitterList();
                                startTransiction(twitterList);
                            }
                        }
                        else {
                            if(optionDrawer[position].compareToIgnoreCase(favorite)==0){
                                drawerLayout.closeDrawers();
                                dialog = new ProgressDialog(context);
                                dialog.setMessage(getResources().getString(R.string.loading));
                                dialog.show();
                                if(current!=null && !(current instanceof Concerti)) {
                                    Concerti conc=new Concerti();
                                    goToConcert(conc);
                                    concerti.clear();
                                }
                            }
                        }
                    }
                }
                drawerLayout.closeDrawers();
            }
        };
        adapterItemDrawer.setOnItemClickListener(onItemClickListener);
        ArtistaHomeFragment artistaHome=new ArtistaHomeFragment();
        artistaHome.setNomeArtistaString(nomeArtistaString);
        artistaHome.setCognomeArtitaString(cognomeArtistaString);
        artistaHome.setAliasArtistaString(aliasArtistaString);
        artistaHome.setUrlImmagine(urlImmagine);
        startTransiction(artistaHome);
    }

    public void goToConcert(final ResultFragmentArtisti fragment){
        String artista=aliasArtistaString.replaceAll("\\s+","%20");
        String url=urlPHPpart+'"'+artista + '"';
        JsonArrayRequest arrayRequest =new JsonArrayRequest(url, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                JSONArray jsonArray = response;
                JSONObject jsonObject;
                try{
                    for(int i=0;i< response.length();i++){
                        Setlist conc=new Setlist();
                        jsonObject = response.getJSONObject(i);
                        data = jsonObject.getString("Data");
                        conc.setDate(data);
                        postoConcerto = jsonObject.getString("PostoConcerto");
                        conc.setVenueName(postoConcerto);
                        cittaConcerto = jsonObject.getString("CittaConcerto");
                        conc.setCity(cittaConcerto);
                        pseArtista=jsonObject.getString("PseArtista");
                        conc.setArtistName(pseArtista);
                        idConcerto=jsonObject.getString("IdConcerto");
                        conc.setId(idConcerto);
                        conc.setCover(ArtistaHomeFragment.immagine);
                        concerti.add(conc);
                    }
                    fragment.riempiArray(concerti,aliasArtistaString,ArtistaHomeFragment.immagine);
                    dialog.hide();
                    startTransiction(fragment);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        requestQueue.add(arrayRequest);
    }

    public void goToConcert(final Concerti fragment){
        String artista=aliasArtistaString.replaceAll("\\s+","%20");
        String url=urlPHPpart+'"'+artista + '"';
        JsonArrayRequest arrayRequest =new JsonArrayRequest(url, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                JSONArray jsonArray = response;
                JSONObject jsonObject;
                try{
                    for(int i=0;i< response.length();i++){
                        Setlist conc=new Setlist();
                        jsonObject = response.getJSONObject(i);
                        data = jsonObject.getString("Data");
                        conc.setDate(data);
                        postoConcerto = jsonObject.getString("PostoConcerto");
                        conc.setVenueName(postoConcerto);
                        cittaConcerto = jsonObject.getString("CittaConcerto");
                        conc.setCity(cittaConcerto);
                        pseArtista=jsonObject.getString("PseArtista");
                        conc.setArtistName(pseArtista);
                        idConcerto=jsonObject.getString("IdConcerto");
                        conc.setId(idConcerto);
                        conc.setCover(ArtistaHomeFragment.immagine);
                        concerti.add(conc);
                    }
                    fragment.riempiArray(concerti,aliasArtistaString,ArtistaHomeFragment.immagine);
                    dialog.hide();
                    startTransiction(fragment);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        requestQueue.add(arrayRequest);
    }

    public void startTransiction(Fragment fragment){

        FragmentTransaction fragmentTransaction=fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.content_frame, fragment);
        fragmentTransaction.addToBackStack("").commit();
    }


    @Override
    public void onBackPressed() {

        Fragment current=fragmentManager.findFragmentById(R.id.content_frame);
        boolean check=current instanceof ArtistaHomeFragment;
        final Context context=this;
        if(check){
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setMessage(R.string.exit)
                    .setCancelable(false)
                    .setPositiveButton(R.string.si, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            moveTaskToBack(true);
                            Intent intent=new Intent(context, MainActivity.class);
                            startActivity(intent);
                        }
                    })
                    .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });
            AlertDialog alert = builder.create();
            alert.show();
        }
        else {
            super.onBackPressed();
        }
    }

}
