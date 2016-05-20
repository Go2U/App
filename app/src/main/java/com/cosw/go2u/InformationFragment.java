package com.cosw.go2u;

import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.cosw.go2u.R;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.util.ArrayList;

import javax.net.ssl.HttpsURLConnection;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link InformationFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link InformationFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class InformationFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String ARG_PARAM3 = "param3";
    private static final String ARG_PARAM4 = "param4";
    private static final String ARG_PARAM5 = "param5";
    private static final String ARG_PARAM6 = "param6";

    // TODO: Rename and change types of parameters
    private ArrayList<String> userData;

    private View infView;

    private OnFragmentInteractionListener mListener;

    public InformationFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment InformationFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static InformationFragment newInstance(String param1, String param2, String param3, String param4, String param5, String param6) {
        InformationFragment fragment = new InformationFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        args.putString(ARG_PARAM3, param3);
        args.putString(ARG_PARAM4, param4);
        args.putString(ARG_PARAM5, param5);
        args.putString(ARG_PARAM6, param6);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            userData = new ArrayList<>();
            userData.add(getArguments().getString(ARG_PARAM1));
            userData.add(getArguments().getString(ARG_PARAM2));
            userData.add(getArguments().getString(ARG_PARAM3));
            userData.add(getArguments().getString(ARG_PARAM4));
            userData.add(getArguments().getString(ARG_PARAM5));
            userData.add(getArguments().getString(ARG_PARAM6));
        }
        //new loadStudent().execute(LoginActivity.user,LoginActivity.password);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        infView = inflater.inflate(R.layout.fragment_information, container, false);

        ((EditText) infView.findViewById(R.id.welcome_username)).setText(userData.get(0));
        ((EditText) infView.findViewById(R.id.welcome_name)).setText(userData.get(1));
        ((EditText) infView.findViewById(R.id.welcome_lastname)).setText(userData.get(2));
        ((EditText) infView.findViewById(R.id.welcome_address)).setText(userData.get(3));
        ((EditText) infView.findViewById(R.id.welcome_email)).setText(userData.get(4));
        ((EditText) infView.findViewById(R.id.welcome_phone)).setText(userData.get(5));

        Button save =(Button) infView.findViewById(R.id.user_welcome_save_button);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username= ((EditText) infView.findViewById(R.id.welcome_username)).getText().toString();
                String name= ((EditText) infView.findViewById(R.id.welcome_name)).getText().toString();
                String lastname= ((EditText) infView.findViewById(R.id.welcome_lastname)).getText().toString();
                String address= ((EditText) infView.findViewById(R.id.welcome_address)).getText().toString();
                String email= ((EditText) infView.findViewById(R.id.welcome_email)).getText().toString();
                String phone= ((EditText) infView.findViewById(R.id.welcome_phone)).getText().toString();
                new SaveStudent().execute(LoginActivity.user,LoginActivity.password,username,name,lastname,address,email,phone);
            }
        });
        return infView;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    public class loadStudent extends AsyncTask<String, Integer, ArrayList<String>> {

        @Override
        protected ArrayList<String> doInBackground(String... args) {
            ArrayList<String> resp = new ArrayList<>();
            try {
                URL url = new URL("https://go2u.herokuapp.com/api/stu/"+args[0]);
                HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();

                urlConnection.setRequestMethod("GET");
                urlConnection.setRequestProperty("Content-Type", "application/json");
                urlConnection.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
                String userpass = args[0]+ ":" + args[1];
                String basicAuth = "Basic " + new String(Base64.encodeToString(userpass.getBytes(), Base64.NO_WRAP));
                urlConnection.setRequestProperty ("Authorization", basicAuth);

                int rc=urlConnection.getResponseCode();
                System.out.println(rc+"");

                BufferedReader in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                String inputLine;
                StringBuffer response = new StringBuffer();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                JSONObject jsonObject=new JSONObject(response.toString());
                resp.add(jsonObject.getString("username"));
                resp.add(jsonObject.getString("name"));
                resp.add(jsonObject.getString("lastName"));
                resp.add(jsonObject.getString("address"));
                resp.add(jsonObject.getString("email"));
                resp.add(jsonObject.getString("cellPhone"));

            } catch (Exception e){
                e.printStackTrace();
            }
            System.out.println("Load");
            return resp;
        }

        @Override
        protected void onPostExecute(ArrayList<String> p) {
            super.onPostExecute(p);
            System.out.println(p.toString());
            EditText username = (EditText) getView().findViewById(R.id.welcome_username);
            username.setText(p.get(0));
            // not editable
            username.setKeyListener(null);
            EditText name = (EditText) getView().findViewById(R.id.welcome_name);
            name.setText(p.get(1));
            EditText lastname = (EditText) getView().findViewById(R.id.welcome_lastname);
            lastname.setText(p.get(2));
            EditText address = (EditText) getView().findViewById(R.id.welcome_address);
            address.setText(p.get(3));
            EditText email = (EditText) getView().findViewById(R.id.welcome_email);
            email.setText(p.get(4));
            EditText phone = (EditText) getView().findViewById(R.id.welcome_phone);
            phone.setText(p.get(5));
        }
    }

    public class SaveStudent extends AsyncTask<String, Void, Boolean> {

        @Override
        protected Boolean doInBackground(String... args) {
            try {
                URL url = new URL("https://go2u.herokuapp.com/api/stu/upd/"+args[0]);
                HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();

                urlConnection.setRequestMethod("POST");
                urlConnection.setRequestProperty("Content-Type", "application/json");
                String userpass = LoginActivity.user + ":" + LoginActivity.password;
                String basicAuth = "Basic " + new String(Base64.encodeToString(userpass.getBytes(), Base64.NO_WRAP));
                urlConnection.setRequestProperty ("Authorization", basicAuth);

                JSONObject jso=new JSONObject();
                jso.put("username",args[2]);
                jso.put("name",args[3]);
                jso.put("lastName",args[4]);
                jso.put("address",args[5]);
                jso.put("email",args[6]);
                jso.put("cellPhone",args[7]);
                String message = jso.toString();
                byte[] data = message.getBytes("UTF-8");

                OutputStream os = ((HttpsURLConnection)urlConnection).getOutputStream();
                os.write(data);

                os.flush();
                os.close();

                String respmsg = ((HttpsURLConnection)urlConnection).getResponseMessage();
                System.out.println(respmsg);
                //Código HTTP de respuesta
                int restcode=urlConnection.getResponseCode();
                System.out.println(restcode+"");
                if(restcode!=200){
                    return false;
                }

            } catch (Exception e){
                e.printStackTrace();
                return false;
            }
            System.out.println("Save");
            return true;
        }

        @Override
        protected void onPostExecute(Boolean success) {
            super.onPostExecute(success);
            if (!success){
                AlertDialog.Builder alertBuilder=new AlertDialog.Builder(getActivity());
                alertBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                    }
                });
                AlertDialog alertDialog = alertBuilder.create();
                alertDialog.setTitle("Student Information");
                alertDialog.setMessage("It has been a problem, please try again later.\n\nThanks!");

                alertDialog.show();

                new loadStudent().execute(LoginActivity.user,LoginActivity.password);
            } else {
                AlertDialog.Builder alertBuilder=new AlertDialog.Builder(getActivity());
                alertBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                    }
                });
                AlertDialog alertDialog = alertBuilder.create();
                alertDialog.setTitle("Student Information");
                alertDialog.setMessage("Your information has been saved.\n\nThanks!");

                alertDialog.show();
            }
        }
    }
}
