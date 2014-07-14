package com.apps40in.rosberry.test.newsapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EdgeEffect;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.apps40in.rosberry.test.newsapp.R;

public class LoginActivity extends ActionBarActivity {

    private final String PREFERENCE_NAME = "Login";
    private final int PREFERENCE_MODE = 0;
    private SharedPreferences sp;
    private SharedPreferences.Editor spEditor;
    private EditText etName, etPassword;
    private Button btn;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        sp = getSharedPreferences(PREFERENCE_NAME, PREFERENCE_MODE);
        //spEditor = sp.edit();

        etName = (EditText) findViewById(R.id.etName);
        etPassword = (EditText) findViewById(R.id.etPass);
        btn = (Button) findViewById(R.id.btnLogin);

        buttonListener();

    }

    private void buttonListener(){
        btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String name = etName.getText().toString();
                String pass = etPassword.getText().toString();

                if(sp.contains(name)){
                    if(isPasswordCorrect(name,pass)){
                        Intent intent = new Intent(getApplicationContext(), NewsListActivity.class);
                        intent.putExtra("name",name);
                        intent.putExtra("newUser", false);
                        startActivity(intent);
                    }else{
                        Toast.makeText(getApplicationContext(), "INVALID PASSWORD", Toast.LENGTH_LONG).show();
                        return;
                    }

                }else{
                    spEditor = sp.edit();
                    spEditor.putString(name,pass);
                    spEditor.commit();

                    Intent intent = new Intent(getApplicationContext(), NewsListActivity.class);
                    intent.putExtra("name",name);
                    intent.putExtra("newUser", true);
                    startActivity(intent);
                }

            }
        });
    }

    private boolean isPasswordCorrect(String u, String p){
       // Log.d("!! TAG !!", "SP getString " + sp.getString(u,null));
       // Log.d("!! TAG !!", "Entered pass " + p);
        if(sp.getString(u,null).equals(p)){
            return true;
        }
        return false;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.login, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
