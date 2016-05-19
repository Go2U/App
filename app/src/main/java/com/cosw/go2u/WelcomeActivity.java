package com.cosw.go2u;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
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

        Button save =(Button) findViewById(R.id.user_welcome_save_button);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username= ((EditText) findViewById(R.id.welcome_username)).getText().toString();
                String name= ((EditText) findViewById(R.id.welcome_name)).getText().toString();
                String lastname= ((EditText) findViewById(R.id.welcome_lastname)).getText().toString();
                String address= ((EditText) findViewById(R.id.welcome_address)).getText().toString();
                String email= ((EditText) findViewById(R.id.welcome_email)).getText().toString();
                String phone= ((EditText) findViewById(R.id.welcome_phone)).getText().toString();
                new SaveStudent().execute(LoginActivity.user,LoginActivity.password,username,name,lastname,address,email,phone);
            }
        });
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

        if (id == R.id.nav_find_univ) {
            // Handle the camera action
        } else if (id == R.id.nav_check_result) {

        } else if (id == R.id.nav_payment) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

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
        return true;
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
            EditText username = (EditText) findViewById(R.id.welcome_username);
            username.setText(p.get(0));
            // not editable
            username.setKeyListener(null);
            EditText name = (EditText) findViewById(R.id.welcome_name);
            name.setText(p.get(1));
            EditText lastname = (EditText) findViewById(R.id.welcome_lastname);
            lastname.setText(p.get(2));
            EditText address = (EditText) findViewById(R.id.welcome_address);
            address.setText(p.get(3));
            EditText email = (EditText) findViewById(R.id.welcome_email);
            email.setText(p.get(4));
            EditText phone = (EditText) findViewById(R.id.welcome_phone);
            phone.setText(p.get(5));
            TextView nav_username = (TextView) findViewById(R.id.nav__welcome_username);
            nav_username.setText(p.get(1));
            TextView nav_email = (TextView) findViewById(R.id.nav__welcome_email);
            nav_email.setText(p.get(4));
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
                AlertDialog.Builder alertBuilder=new AlertDialog.Builder(WelcomeActivity.this);
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
                AlertDialog.Builder alertBuilder=new AlertDialog.Builder(WelcomeActivity.this);
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
