package com.cosw.go2u;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import javax.net.ssl.HttpsURLConnection;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link UniversityFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link UniversityFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UniversityFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String univID;

    private OnFragmentInteractionListener mListener;

    private ArrayList<String> universityData = new ArrayList<>();
    private View uniView;

    public UniversityFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @return A new instance of fragment UniversityFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static UniversityFragment newInstance(String param1) {
        UniversityFragment fragment = new UniversityFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            univID = getArguments().getString(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        uniView = inflater.inflate(R.layout.fragment_university, container, false);
        new loadUniversity().execute(univID);

        FloatingActionButton fab = (FloatingActionButton) uniView.findViewById(R.id.university_send_email);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendEmail();
            }
        });

        Button save =(Button) uniView.findViewById(R.id.university_take_test_button);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = (Fragment) TestFragment.newInstance(universityData.get(0));
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.content_welcome, fragment).addToBackStack("").commit();
            }
        });

        return uniView;
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

    public void sendEmail(){
        Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto", universityData.get(3), null));

        Calendar c= Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String formattedDate = df.format(c.getTime());

        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Contact FAQ");
        emailIntent.putExtra(Intent.EXTRA_TEXT, ""+formattedDate+"\n\nThanks!");
        startActivity(Intent.createChooser(emailIntent, "Contact University..."));
    }

    public class loadUniversity extends AsyncTask<String, Integer, ArrayList<String>> {

        @Override
        protected ArrayList<String> doInBackground(String... args) {
            ArrayList<String> resp = new ArrayList<>();
            try {
                URL url = new URL("https://go2u.herokuapp.com/api/uni/"+args[0]);
                HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();

                urlConnection.setRequestMethod("GET");
                urlConnection.setRequestProperty("Content-Type", "application/json");
                urlConnection.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
                String userpass = LoginActivity.user+ ":" + LoginActivity.password;
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
                resp.add(jsonObject.getString("city"));
                resp.add(jsonObject.getString("email"));
                resp.add(jsonObject.getString("url"));
                resp.add(jsonObject.getString("address"));
                resp.add(jsonObject.getString("number"));
                resp.add(jsonObject.getString("descp"));

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
            EditText name = (EditText) getView().findViewById(R.id.university_name);
            name.setText(p.get(1));
            name.setKeyListener(null);
            EditText email = (EditText) getView().findViewById(R.id.university_email);
            email.setText(p.get(3));
            email.setKeyListener(null);
            EditText address = (EditText) getView().findViewById(R.id.university_address);
            address.setText(p.get(5));
            address.setKeyListener(null);
            EditText url = (EditText) getView().findViewById(R.id.university_url);
            url.setText(p.get(4));
            url.setKeyListener(null);
            url.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String url = ((EditText) getView().findViewById(R.id.university_url)).getText().toString();
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(url));
                    startActivity(i);
                }
            });
            EditText phone = (EditText) getView().findViewById(R.id.university_phone);
            phone.setText(p.get(6));
            phone.setKeyListener(null);
            EditText desc = (EditText) getView().findViewById(R.id.university_desc);
            desc.setText(p.get(7));
            desc.setKeyListener(null);
            getActivity().setTitle(p.get(1));
            universityData.clear();
            universityData.addAll(p);
        }
    }
}
