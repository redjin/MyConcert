package app.filanninogiovanni.sms16.ivu.di.uniba.it.myconcert;


import android.app.Dialog;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONArray;
import org.json.JSONObject;
import app.filanninogiovanni.sms16.ivu.di.uniba.it.myconcert.Artista.ArtistaHome;
import app.filanninogiovanni.sms16.ivu.di.uniba.it.myconcert.Utility.ErrorClass;


public class loginFragment extends Fragment {

    private EditText username;
    private EditText password;
    private Button login;
    ErrorClass errorClass=new ErrorClass();
    private Dialog dialog;
    private OnLoginConfirmed mLogin;
    private String UserURL = "http://mymusiclive.altervista.org/user.php?username=";
    private String OutPut = "http://mymusiclive.altervista.org/output.json";
    private String PasswordURL = "&password=";
    private String formatJson = "&format=json";
    public static String actualUsername;

    private String nome;
    private String cognome;
    private int artista;
    private String urlImmagine;
    private String alias = "";
    private TextView regartista;
    private static String usernameShare;


    String franco;

    private ImageButton loginButton;

    RequestQueue requestQueue;


    @Override
    public void onAttach(Context context) {

        super.onAttach(context);
        try{
            mLogin = (OnLoginConfirmed) context;
        } catch (Exception e){

        }
    }

    public interface OnLoginConfirmed{
        public void goToSearchFragment(String nome,String cognome);
        public void loginTwitter();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        return inflater.inflate(R.layout.login_fragment,container,false);
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        username = (EditText) getActivity().findViewById(R.id.username);
        password = (EditText) getActivity().findViewById(R.id.password);
        regartista=(TextView) getActivity().findViewById(R.id.regartista) ;

        loginButton = (ImageButton) getActivity().findViewById(R.id.login_button_tweet);

       regartista.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://mymusiclive.altervista.org"));
               startActivity(browserIntent);
           }
       });
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLogin.loginTwitter();
            }
        });


        requestQueue = Volley.newRequestQueue(getActivity());



        login = (Button) getActivity().findViewById(R.id.buttonLogin);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isNetworkAvailable()){
                    dialog=ErrorClass.onCreateDialog(ErrorClass.PROGRESS_DIALOG_ID,getActivity());
                    //dialog.show();

                    String url = UserURL + "\"" + username.getText().toString() + "\"" + PasswordURL + "\"" + password.getText().toString() + "\"" + formatJson;
                    JsonArrayRequest arrayRequest = new JsonArrayRequest(url, new Response.Listener<JSONArray>() {
                        @Override
                        public void onResponse(JSONArray response) {
                                JSONArray jsonArray = response;
                                franco = jsonArray.toString();

                            if (checkUtente(franco)) {
                                actualUsername = username.getText().toString();


                                try {
                                    JSONObject jsonObject = getJson(jsonArray);
                                    nome = jsonObject.getString("Nome");
                                    cognome = jsonObject.getString("Cognome");
                                    artista = jsonObject.getInt("artista");

                                    if(artista==1){
                                        urlImmagine = jsonObject.getString("Immagine");
                                        alias = jsonObject.getString("Pseudonimo");
                                        Intent artistaHome = new Intent(getContext(), ArtistaHome.class);
                                        artistaHome.putExtra("nome", nome);
                                        artistaHome.putExtra("cognome", cognome);
                                        artistaHome.putExtra("alias", alias);
                                        artistaHome.putExtra("url", urlImmagine);
                                        startActivity(artistaHome);
                                    } else {


                                        mLogin.goToSearchFragment(nome,cognome);
                                    }
                                } catch (Exception e){
                                    e.printStackTrace();
                                }
                                usernameShare = username.getText().toString();

                                requestQueue.stop();

                            } else {
                                Toast.makeText(getActivity(), getResources().getText(R.string.no_user_avabile), Toast.LENGTH_LONG);
                                ErrorClass.onCreateDialog(ErrorClass.DIALOG_FAILACCESS_ID,getActivity());
                            }



                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {

                        }
                    });
                    requestQueue.add(arrayRequest);

                }
                else {
                    dialog = ErrorClass.onCreateDialog(ErrorClass.DIALOG_NOCONNECTION_ID,getActivity());
                }
            }
        });

        super.onActivityCreated(savedInstanceState);
    }

    private boolean checkUtente(String query){
        if(query.compareTo("[]")==0){
            return false;
        }
        return true;
    }

    private boolean isNetworkAvailable() {

        ConnectivityManager connectivityManager
                = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();

    }

    private JSONObject getJson(JSONArray jsonArray){
        JSONObject result = null;
        try{
            result = jsonArray.getJSONObject(0);
        } catch (Exception e){

        }
        return result;
    }

    public String getActualUsername(){
        return usernameShare;
    }


    public static void setActualUsername(String actualUsername) {
        loginFragment.actualUsername = actualUsername;
    }


}