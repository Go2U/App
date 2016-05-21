package com.cosw.go2u;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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

import com.cosw.go2u.dummy.DummyContent;
import com.cosw.go2u.dummy.DummyContent.DummyItem;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class TestFragment extends Fragment {

    // TODO: Customize parameter argument names
    private static final String ARG_UNI_ID = "param1";
    // TODO: Customize parameters

    private String uniID;
    private OnListFragmentInteractionListener mListener;
    private View testView;
    private ArrayList<DummyItem> dummyItems = new ArrayList<>();
    private TestRecyclerViewAdapter testRecyclerViewAdapter;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public TestFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static TestFragment newInstance(String param1) {
        TestFragment fragment = new TestFragment();
        Bundle args = new Bundle();
        args.putString(ARG_UNI_ID, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            uniID = getArguments().getString(ARG_UNI_ID);
        }


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        testView = inflater.inflate(R.layout.fragment_test_list, container, false);
        new loadUniversityTest().execute(uniID);

        // Set the adapter
        RecyclerView recyclerView =(RecyclerView) testView.findViewById(R.id.university_test_list);
        Context context = testView.getContext();
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        testRecyclerViewAdapter = new TestRecyclerViewAdapter(dummyItems, mListener);
        recyclerView.setAdapter(testRecyclerViewAdapter);
        recyclerView.addItemDecoration(
                new HorizontalDividerItemDecoration.Builder(getContext())
                        .color(Color.DKGRAY)
                        .sizeResId(R.dimen.divider)
                        .marginResId(R.dimen.leftmargin, R.dimen.rightmargin)
                        .build());

        Button save =(Button) testView.findViewById(R.id.university_send_test);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alertBuilder=new AlertDialog.Builder(getActivity());
                alertBuilder.setPositiveButton("Send", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        System.out.println("---> Send");
                        AlertDialog.Builder alertBuilder=new AlertDialog.Builder(getActivity());
                        alertBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                getActivity().setTitle("Welcome");
                                Fragment fragment = (Fragment) WelcomeFragment.newInstance(WelcomeActivity.userData.get(1),WelcomeActivity.userData.get(2));
                                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                                fragmentManager.beginTransaction().replace(R.id.content_welcome, fragment).commit();
                            }
                        });
                        AlertDialog alertDialog = alertBuilder.create();
                        alertDialog.setTitle("University Test");
                        alertDialog.setMessage("Your test has been saved successfully!\n\nThanks!");

                        alertDialog.show();
                    }
                });
                alertBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                    }
                });
                AlertDialog alertDialog = alertBuilder.create();
                alertDialog.setTitle("University Test");
                alertDialog.setMessage("Do you want to send the Test?");

                alertDialog.show();
            }
        });

        return testView;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
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
    public interface OnListFragmentInteractionListener {
        // TODO: Update argument type and name
        void onListFragmentInteraction(DummyItem item);
    }

    public class loadUniversityTest extends AsyncTask<String, Integer, ArrayList<DummyItem>> {

        @Override
        protected ArrayList<DummyItem> doInBackground(String... args) {
            ArrayList<DummyItem> resp = new ArrayList<>();
            try {
                URL url = new URL("https://go2u.herokuapp.com/test/getTestUniversity/"+args[0]);
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

                System.out.println(response.toString());
                JSONArray jsonArray = new JSONArray(response.toString());
                JSONObject jsonObject;
                JSONObject jsonObject2;
                ArrayList<String> answer;
                for (int i=0;i<jsonArray.length();i++) {
                    //System.out.println(jsonArray.getJSONObject(i).toString());
                    jsonObject=jsonArray.getJSONObject(i);
                    String idQ = jsonObject.getString("idQuestion");
                    String question = jsonObject.getString("question");
                    JSONArray ansJSON = jsonObject.getJSONArray("answer");
                    answer = new ArrayList<>();
                    for (int j=0;j<ansJSON.length();j++) {
                        jsonObject2=ansJSON.getJSONObject(j);
                        answer.add(jsonObject2.getString("answer"));
                    }
                    //System.out.println(s);
                    resp.add(new DummyItem(idQ,question,answer));
                }

            } catch (Exception e){
                e.printStackTrace();
            }
            System.out.println("Load");
            return resp;
        }

        @Override
        protected void onPostExecute(ArrayList<DummyItem> p) {
            super.onPostExecute(p);
            System.out.println(p.toString());
//            EditText name = (EditText) getView().findViewById(R.id.university_name);
//            name.setText(p.get(1));
//            name.setKeyListener(null);
//            getActivity().setTitle(p.get(1));
            dummyItems.clear();
            dummyItems.addAll(p);
            testRecyclerViewAdapter.notifyDataSetChanged();
        }
    }
}
