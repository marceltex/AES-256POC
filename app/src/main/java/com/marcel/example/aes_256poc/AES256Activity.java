package com.marcel.example.aes_256poc;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class AES256Activity extends AppCompatActivity {

    Button selectFileBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aes256);

        selectFileBtn = (Button) findViewById(R.id.button_select_file);

        selectFileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT, null);
                startActivityForResult(intent, 100);
            }
        });
    }
}
