package com.cosw.go2u;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
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
        implements NavigationView.OnNavigationItemSelectedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        new loadStudent().execute(LoginActivity.user,LoginActivity.password);
        setContentView(R.layout.activity_welcome);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

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
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public class loadStudent extends AsyncTask<String, Integer, ArrayList<String>> {

        @Override
        protected ArrayList<String> doInBackground(String... args) {
            ArrayList<String> resp = new ArrayList<>();
            try {
                URL url = new URL("http://go2u.herokuapp.com/api/stu/?id="+args[0]);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

                urlConnection.setRequestMethod("GET");
                urlConnection.setRequestProperty("Content-Type", "application/json");
                urlConnection.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
                String userpass = args[0]+ ":" + args[1];
                String basicAuth = "Basic " + new String(Base64.encodeToString(userpass.getBytes(), Base64.NO_WRAP));
                urlConnection.setRequestProperty ("Authorization", basicAuth);

//                JSONObject jso=new JSONObject();
//                jso.put("id",args[0]);
//                String params = jso.toString();
//
//                urlConnection.setRequestMethod(params);
//                byte[] data = message.getBytes("UTF-8");
//
//                OutputStream os = urlConnection.getOutputStream();
//                os.write(jso.toString().getBytes());
//                os.flush();
//                os.close();

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
                for (int i=0;i<jsonArray.length();i++) {
                    //System.out.println(jsonArray.getJSONObject(i).toString());
                    jsonObject=jsonArray.getJSONObject(i);
                    s="Id:\t\t\t\t\t"+jsonArray.getJSONObject(i).getInt("idproducto")+"\n"
                            +"Name:\t\t"+jsonObject.getString("nombre")+"\n"
                            +"Price:\t\t"+jsonObject.getInt("precio");
                    //System.out.println(s);
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
                System.out.println("NULL");
            }
            System.out.println(p.toString());
            //mAdapter.notifyDataSetChanged();
            //System.out.println(products.toString());
        }
    }
}
