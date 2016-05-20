package com.cosw.go2u;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;

import javax.net.ssl.HttpsURLConnection;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ListUniFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ListUniFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ListUniFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private ArrayAdapter<String> mAdapter;
    private ArrayList<String> universities = new ArrayList<>();
    private ArrayList<String> idUniversities = new ArrayList<>();

    private View listUView;

    public ListUniFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ListUniFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ListUniFragment newInstance(String param1, String param2) {
        ListUniFragment fragment = new ListUniFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        listUView = inflater.inflate(R.layout.fragment_list_uni, container, false);
        new loadUniversities().execute();

        ProgressBar progressBar = new ProgressBar(getContext());
        progressBar.setLayoutParams(new Toolbar.LayoutParams(Toolbar.LayoutParams.WRAP_CONTENT,
                Toolbar.LayoutParams.WRAP_CONTENT, Gravity.CENTER));
        progressBar.setIndeterminate(true);
        ListView listView = (ListView) listUView.findViewById(R.id.universities_list);
        listView.setEmptyView(progressBar);

        // Must add the progress bar to the root of the layout
        //ViewGroup root = (ViewGroup) listUView.findViewById(android.R.id.content);
        ViewGroup root = (ViewGroup) listUView.getRootView();
        root.addView(progressBar);

        // For the cursor adapter, specify which columns go into which views
        String[] fromColumns = {"ID","Name","Price"};
        int[] toViews = {android.R.id.text1}; // The TextView in simple_list_item_1

        // Create an empty adapter we will use to display the loaded data.
        // We pass null for the cursor, then update it in onLoadFinished()
        mAdapter = new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_list_item_1, universities);
        listView.setAdapter(mAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                System.out.println(position+" -- "+idUniversities.get(position));
                Fragment fragment = (Fragment) UniversityFragment.newInstance(idUniversities.get(position));
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                //FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                fragmentManager.beginTransaction().replace(R.id.content_welcome, fragment).addToBackStack("").commit();
                //fragmentTransaction.replace(R.id.content_welcome, fragment);
                //fragmentTransaction.addToBackStack("University List");
                //fragmentTransaction.commit();
            }
        });

        return listUView;
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

    public class loadUniversities extends AsyncTask<Void, Integer, ArrayList<String>> {

        @Override
        protected ArrayList<String> doInBackground(Void... args) {
            ArrayList<String> resp = new ArrayList<>();
            try {
                URL url = new URL("https://go2u.herokuapp.com/api/uni");
                HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();

                urlConnection.setRequestMethod("GET");
                urlConnection.setRequestProperty("Content-Type", "application/json");
                urlConnection.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
                String userpass = LoginActivity.user + ":" + LoginActivity.password;
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

                System.out.println(response.toString());

                //JSONObject jsonObject = new JSONObject(response.toString());
                JSONArray jsonArray = new JSONArray(response.toString());
                JSONObject jsonObject;
                String s;
                idUniversities = new ArrayList<>();
                for (int i=0;i<jsonArray.length();i++) {
                    //System.out.println(jsonArray.getJSONObject(i).toString());
                    jsonObject=jsonArray.getJSONObject(i);
                    s="Name:\t\t\t\t\t"+jsonObject.getString("name")+"\n"
                            +"Email:\t\t"+jsonObject.getString("email")+"\n"
                            +"Address:\t\t"+jsonObject.getString("address")+"\n"
                            +"Number:\t\t"+jsonObject.getString("number");
                    //System.out.println(s);
                    idUniversities.add(jsonObject.getString("username"));
                    resp.add(s);
                }

            } catch (Exception e){
                e.printStackTrace();
            }
            System.out.println("Load");
            return resp;
        }

        @Override
        protected void onPostExecute(ArrayList<String> p) {
            super.onPostExecute(p);
            if(p!=null){
                universities.clear();
                universities.addAll(p);
            }
            mAdapter.notifyDataSetChanged();
            //System.out.println(products.toString());
        }
    }
}
