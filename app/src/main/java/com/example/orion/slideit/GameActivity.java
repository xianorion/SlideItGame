package com.example.orion.slideit;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.os.*;
import android.provider.MediaStore;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Orion on 5/31/2016.
 */
public class GameActivity extends Activity implements Runnable,View.OnClickListener,View.OnTouchListener,SensorEventListener {
    //variables
    private int answer;
    private static int scoreNum;
    private boolean success;
    private static int level ; // the level the user is on
    private static int intensity; //represents how fast the timer will go
    private boolean playing; //if false, run stops
    private Button tap;
    private SeekBar slider;
    TextView theScore;
    private ProgressBar timer;
    TextView commandMsg;
    private static int timerStatus;
    private final int MAX=100;
    Handler handler;

    private boolean hasLost=false;


    // The following are used for the shake detection
    private ShakeDetector mShakeDetector;



    //sensor
    private boolean init;
    private Sensor mAccelerometer;
    private SensorManager mSensorManager;
    private float x1, x2, x3;
    private static final float ERROR = (float) 7.0;


    //booleans for each command
    private boolean tapped,swiped,shook,flipped;



    //music section:
    MediaPlayer slideMusic, tapMusic, shakeMusic, gameMusic,loseMusic;

    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_activity);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        //set up variables
        answer=-1;
        scoreNum=0;
        success=false;
        level =1; // the level the user is on
        intensity=1; //represents how fast the timer will go
        playing= true; //if false, run stops
        timerStatus=0;
        tapped=swiped=shook=flipped=false;

        //set up all things on screen
        tap= (Button)(findViewById(R.id.tapIt));
        theScore= (TextView)(findViewById(R.id.score));
        timer= (ProgressBar)(findViewById(R.id.timerProg));
        commandMsg = (TextView)(findViewById(R.id.commandMsg));
        slider = (SeekBar)(findViewById(R.id.slideIt));

        //set up music
        slideMusic = MediaPlayer.create(getApplicationContext(), R.raw.slideit);
        tapMusic = MediaPlayer.create(getApplicationContext(), R.raw.tapit);
        shakeMusic = MediaPlayer.create(getApplicationContext(), R.raw.flipit);
        loseMusic = MediaPlayer.create(getApplicationContext(), R.raw.youlose);
        gameMusic = MediaPlayer.create(getApplicationContext(), R.raw.ingame);


        gameMusic.setLooping(true);
        gameMusic.start();

        //set up events
        timer.setMax(MAX);
        timer.setProgress(timerStatus);
        timer.setVisibility(View.VISIBLE); //make sure we can see progress bar

        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        List listOfSensorsOnDevice = mSensorManager.getSensorList(Sensor.TYPE_ALL);

        tap.setOnClickListener(this);

        slider.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (progress == slider.getMax()) {
                    swiped = true;
                    shook = tapped = flipped = false;
                    slider.setProgress(0);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                //do nothing
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                //do nothing
            }
        });

         handler = new Handler();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //start running main thread
        new Thread(this).start();
//set up runnable timer
       new Thread (new Runnable() {
            @Override
            public void run() {

                while ((timerStatus < 100)&&(playing)) {
                    // add timer

                    timerStatus = updateProgressBar(1);
                    // your computer is too fast, sleep 1 second
                    try {
                        Thread.sleep(1000/intensity);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    // Update the progress bar
                            if (success==false){
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                       // commandMsg.setText("WAIT "+ timerStatus);
                                        timer.setProgress(timerStatus);
                                    }
                                });



                            }
                        }
                timerStatus=MAX; //set timer to max to inticate that game should end
            }
        }).start();


        // ShakeDetector initialization
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mAccelerometer = mSensorManager
                .getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mShakeDetector = new ShakeDetector();
        mShakeDetector.setOnShakeListener(new ShakeDetector.OnShakeListener() {

            @Override
            public void onShake(int count) {
				/*
				 * The following method, "handleShakeEvent(count):" is a stub //
				 * method you would use to setup whatever you want done once the
				 * device has been shook.
				 */
                shook=true;
            }
        });

    }



    /*
    function updates the progress bar by adding the previous
    progress by some neww value
     */
    private int updateProgressBar(int i){
        int updateValue=0;
        if(i!=-1) {
            updateValue=i+timerStatus;

        }

        return updateValue;
    }
    //random class
    private int randomCommand(){
        int command=0;
        //get random number between 1-4
        Random rand = new Random();
        command=rand.nextInt(3)+1;

        return command;
    }


    public boolean commandHit(){
        boolean hit=false;

        int whatToHit=randomCommand();
        //switch statment seeing which object was hit
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //whatToHit = 4;
        switch (whatToHit) {
            case 1:
                //use must tapIt
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        commandMsg.setText("TAP IT");
                    }
                });

                //if user tapsIt, set success to true
                while((tapped==false)&&(timerStatus!=MAX)) {
                   //wait till tapit is hit
                }
                if(tapped==true){
                    success = hit = true;
                    tapped=false;

                    tapMusic.start();

                }else{
                    success=playing=false;
                }
                break;
            case 2:
                //user must slideIt
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        commandMsg.setText("SLIDE IT");
                    }
                });

                while((swiped==false)&&(timerStatus!=MAX)) {
                    //wait till slideit is hit
                }
                if(swiped==true){
                    success = hit = true;
                    swiped=false;

                    slideMusic.start();

                }else{
                    success=playing=false;
                }
                break;
            case 3:
                //user must shake it
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        commandMsg.setText("SHAKE IT");
                    }
                });

                while((shook==false)&&(timerStatus!=MAX)) {
                    //wait till phone is shook
                }
                if(shook==true){
                    success = hit = true;
                    shook=false;

                    shakeMusic.start();

                }else{
                    success=playing=false;
                }

                break;
            case 4:
                //user must flip it
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        commandMsg.setText("FLIP IT");
                    }
                });

                while((flipped==false)&&(timerStatus!=MAX)) {
                    //wait till screen is flipped
                }
                if(flipped==true){
                    success = hit = true;
                    flipped=false;
                }else{
                    success=playing=false;
                }

                break;

        }

        handler.post(new Runnable() {
            @Override
            public void run() {
                commandMsg.setText("...");
            }
        });

        return hit;
    }

    public void updateScore(int level){
        //switch statment to update score based on level
            //add 2 points times the level the user is on
            scoreNum+=getLevel()*2;
            handler.post(new Runnable() {
                @Override
                public void run() {
                    theScore.setText("Score: " + scoreNum);
                }
            });



    }

    public void lose(){
        //display final score
        handler.post(new Runnable() {
            @Override
            public void run() {
                commandMsg.setText("YOU LOSE\n FINAL SCORE: " + scoreNum);
                tap.setText("Back to Home");
                gameMusic.stop();
                loseMusic.start();

            }
        });

        //print you lost

        //now move to title screen:
        //go to the game activity screen if user taps
        hasLost=true;

    }



    public void updateLevel(boolean update){
        if(update){
            //go up a level
            intensity++;
            //if level if 5 stop game (This is for demo)
            /*if(intensity==5){
                //stop game
                playing=false;
            }*/
        }else{
            // restart game over all (Move to start screen)
        }
    }


    public int getLevel(){
        return level;
    }

    /*
    Function: runs the game by making sure the time doesnt run out after each command is given
        if the command is satified the next function is called to update the score and level
     */

    public void setCommandMsg(String message){
        commandMsg.setText(message);
    }
    @Override
    public void run(){
            //restart run timer
        while(playing) {
            //call for command
            if(commandHit()) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        setCommandMsg("...");
                    }
                });

                //update score
                //check level
                timerStatus=0;
                timer.setProgress(0);
                level++;
                playing=true ;
                success=false; //change success back to default
                updateScore(getLevel());
                updateLevel(true);
            }else{
                playing=false;
                lose();
                //wait for a bit
                //
                updateLevel(false);
            }

        }
        //go to lose screen

    }




    /* these are the events that the game will look out for when running
 -------------------------------------------------------------------------------------------
     */

    /*
    Function: handle when the user clicks on the tapit button only
     */
    @Override
    public void onClick(View v) {
        if(v.getId()==tap.getId()) {
            tapped = true;
            shook=swiped=flipped=false;
            if(hasLost) {
                Intent myIntent = new Intent(this, HomeScreen.class);
                startActivity(myIntent);
                finish();
            }
        }

    }

    /*
    Function: handles when the user decides to do a swipe on the phone

     */

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return false;
    }

    /*
    Function: checks for changes in the screen orientation and for if the user shakes the screen
    to satisfy a command
     */
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        // Checks the orientation of the screen
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            flipped = true;
            shook=swiped=tapped=false;
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT){
            flipped=false;
        }
    }

    @Override
    public void onSensorChanged(SensorEvent e) {
    //ignore
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        //do nothing
    }




    @Override
    public void onResume() {
        super.onResume();
        // Add the following line to register the Session Manager Listener onResume
        mSensorManager.registerListener(mShakeDetector, mAccelerometer,	SensorManager.SENSOR_DELAY_UI);
    }

    @Override
    public void onPause() {
        // Add the following line to unregister the Sensor Manager onPause
        mSensorManager.unregisterListener(mShakeDetector);
        super.onPause();
    }

    @Override
    public void onDestroy(){
        gameMusic.stop();
        super.onDestroy();

    }


}


