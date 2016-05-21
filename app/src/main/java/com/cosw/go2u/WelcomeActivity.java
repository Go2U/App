package com.cosw.go2u;

import android.net.Uri;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.util.Base64;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.cosw.go2u.dummy.DummyContent;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import javax.net.ssl.HttpsURLConnection;


public class WelcomeActivity extends AppCompatActivity
        implements  NavigationView.OnNavigationItemSelectedListener,
                    InformationFragment.OnFragmentInteractionListener,
                    WelcomeFragment.OnFragmentInteractionListener,
                    TestResultFragment.OnFragmentInteractionListener,
                    ListUniFragment.OnFragmentInteractionListener,
                    UniversityFragment.OnFragmentInteractionListener,
                    TestFragment.OnListFragmentInteractionListener {

    // User data
    public static final ArrayList<String> userData = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        new loadStudent().execute(LoginActivity.user,LoginActivity.password);
        setContentView(R.layout.activity_welcome);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.welcome, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        String title="";
        int id = item.getItemId();
        Fragment fragment = null;
        if (id == R.id.nav_find_univ) {
            fragment = (Fragment) ListUniFragment.newInstance(userData.get(1),userData.get(2));
            title="Find university";
        } else if (id == R.id.nav_check_result) {
            fragment = (Fragment) TestResultFragment.newInstance(userData.get(1),userData.get(2));
            title="Check test result";
        } else if (id == R.id.nav_payment) {

        } else if (id == R.id.nav_manage) {
            fragment = (Fragment) InformationFragment.newInstance(userData.get(0),userData.get(1),userData.get(2),userData.get(3),userData.get(4),userData.get(5));
            title="Update infomation";
        } else if (id == R.id.nav_home_welcome) {
            fragment = (Fragment) WelcomeFragment.newInstance(userData.get(1),userData.get(2));
            title="Welcome";
        } else if (id == R.id.nav_logout) {
            AlertDialog.Builder alertBuilder=new AlertDialog.Builder(WelcomeActivity.this);
            alertBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    Intent intent = new Intent(WelcomeActivity.this, LoginActivity.class);
                    WelcomeActivity.this.startActivity(intent);
                }
            });
            alertBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {

                }
            });
            AlertDialog alertDialog = alertBuilder.create();
            alertDialog.setTitle("Student");
            alertDialog.setMessage("Do you want to logout?");

            alertDialog.show();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);

        if(fragment!=null){
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.content_welcome, fragment).addToBackStack("").commit();
            this.setTitle(title);
        }
        return true;
    }

    @Override
    public void onFragmentInteraction(Uri uri) {
        System.out.println("HOLA");
    }

    @Override
    public void onListFragmentInteraction(DummyContent.DummyItem item) {
        System.out.println("HOLA");
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
            userData.add(p.get(0));
            userData.add(p.get(1));
            userData.add(p.get(2));
            userData.add(p.get(3));
            userData.add(p.get(4));
            userData.add(p.get(5));
            TextView nav_username = (TextView) findViewById(R.id.nav__welcome_username);
            nav_username.setText(p.get(1));
            TextView nav_email = (TextView) findViewById(R.id.nav__welcome_email);
            nav_email.setText(p.get(4));

            Fragment fragment = (Fragment) WelcomeFragment.newInstance(userData.get(1),userData.get(2));
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.content_welcome, fragment).commit();
        }
    }

}
