package com.example.tictactoe;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class PlayerDetails extends AppCompatActivity {
    protected String player1,player2;
    protected TextInputLayout t1, t2;
    private static final int LENGTH = 12;
    private static final int MIN_LENGTH = 3;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player_details);
        t1 = findViewById(R.id.firstPlayer);
        t2 = findViewById(R.id.secondPlayer);
    }


    public void back(View view) {
        Intent i = new Intent(this,MainActivity.class);
        startActivity(i);
    }

    public void next(View view) {
        // text input of the players' name
        player1 = t1.getEditText().getText().toString();
        player2 = t2.getEditText().getText().toString();

        validate(player1,player2);  //checking
    }

    private void validate(String player1, String player2) {
        if (!player1.equals("") && !player2.equals(""))
        {
            t1.setError(null);
            t2.setError(null);
            if (player1.length()<MIN_LENGTH) t1.setError("Too short : Minimum length is "+ MIN_LENGTH);
            if (player2.length()<MIN_LENGTH) t2.setError("Too short : Minimum length is "+ MIN_LENGTH);
            if(player1.length()<=LENGTH && player2.length()<=LENGTH && player1.length()>=MIN_LENGTH && player2.length()>=MIN_LENGTH) {
            Intent i = new Intent(this, Game.class);
            i.putExtra("player1", player1);
            i.putExtra("player2", player2);
            startActivity(i);

            }
        }
        else
        {
            if (player1.equals(""))
            {
                if (!player2.equals(""))t2.setError(null);
                t1.setError("Enter a name");
            }
            if(player2.equals(""))
            {
                if (!player1.equals(""))t1.setError(null);
                t2.setError("Enter a name");
            }
        }

    }
}