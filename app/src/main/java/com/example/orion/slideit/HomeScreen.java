package com.example.orion.slideit;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;

public class HomeScreen extends Activity implements View.OnClickListener{

    Button playButton;
    Button exitButton;
    SoundPool soundPool;

    MediaPlayer mp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        playButton =(Button)(findViewById(R.id.playButton));
        exitButton =(Button)(findViewById(R.id.exitButton));


        mp = MediaPlayer.create(getApplicationContext(), R.raw.welcomescreen);
        mp.setLooping(true);
        mp.start();


        playButton.setOnClickListener(this);
        exitButton.setOnClickListener(this);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_home_screen, menu);



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

    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.playButton){
            //go to the game activity screen
            Intent myIntent = new Intent(this, GameActivity.class);
            startActivity(myIntent);
            mp.stop();
            finish();
        }else if(v.getId()==R.id.exitButton){
            mp.stop();
            finish(); //close application
        }
    }

    @Override
    public void onDestroy(){
        mp.stop();
        super.onDestroy();
    }
}
