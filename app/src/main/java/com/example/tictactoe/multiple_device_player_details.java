package com.example.tictactoe;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.material.textfield.TextInputLayout;



public class multiple_device_player_details extends AppCompatActivity {
    TextInputLayout t1;
    String player1 = "", player2 = "";
    private final int MIN_LENGTH = 3, MAX_LENGTH = 12;
    private int player;
    LottieAnimationView togglebtn, homebtn;
    Boolean ischecked = false;
    Button continuebuttn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.multiple_device_player_details);
        t1 = findViewById(R.id.name);
        togglebtn = findViewById(R.id.choose_color);
        homebtn = findViewById(R.id.home);
        homebtn.setSpeed(2.5f);
        continuebuttn = findViewById(R.id.multi_continue_game);
        //lottiebuttn.setOnClickListener(v -> next_multi());
        continuebuttn.setOnClickListener(v -> {
           next_multi();

        });
        togglebtn.setOnClickListener(v -> {
            if(ischecked){
                togglebtn.setMinAndMaxProgress(0.5f,1.0f);
                togglebtn.playAnimation();
                ischecked = false;
            }
            else{
                togglebtn.setMinAndMaxProgress(0.0f,0.5f);
                togglebtn.playAnimation();
                ischecked = true;
            }
        });
        homebtn.setOnClickListener(v -> {
            Intent i = new Intent(multiple_device_player_details.this,MainActivity.class);
            startActivity(i);
            finish();
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent i = new Intent(this,Menu.class);
        startActivity(i);
        finish();

    }

    public void next_multi() {
        // deciding the players' name
        if (!ischecked) {
            player1 = t1.getEditText().getText().toString();
            if (!player1.equals("")) {
                if (player1.length() < MIN_LENGTH)
                    t1.setError("Too short : Minimum length must be " + MIN_LENGTH);
                else if (player1.length() > MAX_LENGTH)
                    t1.setError("Too large : Maximum length must be " + MAX_LENGTH);
                else {
                    player = 1;         // player 1 -> GREEN
                    validate();
                }
            } else t1.setError("Enter a name ");
        }
        else {
            player2 = t1.getEditText().getText().toString();
            if (!player2.equals("")) {
                if (player2.length() < MIN_LENGTH)
                    t1.setError("Too short : Minimum length must be " + MIN_LENGTH);
                else if (player2.length() > MAX_LENGTH)
                    t1.setError("Too large : Maximum length must be " + MAX_LENGTH);
                else {
                    player = 2;         // player 2 -> RED
                    validate();
                }
            } else t1.setError("Enter a name ");

        }
    }


    private void validate() {
        t1.setError(null);
        Intent i = new Intent(this, WifiModeselect.class);
        i.putExtra("me", player);
        if (player == 1) {
            i.putExtra("name", player1);
            startActivity(i);
            finish();
        } else {
            i.putExtra("name", player2);
            startActivity(i);
            finish();
        }
    }
}



