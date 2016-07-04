package com.marcel.example.aes_256poc;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class AES256Activity extends AppCompatActivity {

    private static final int READ_REQUEST_CODE = 42;

    Button selectFileBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aes256);

        selectFileBtn = (Button) findViewById(R.id.button_select_file);

        selectFileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // ACTION_OPEN_DOCUMENT is the intent to choose a file via the system's file
                // browser.
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);

                // Filter to only show results that can be "opened", such as a
                // file (as opposed to a list of contacts or timezones)
                intent.addCategory(Intent.CATEGORY_OPENABLE);

                // Search for all documents available via installed service providers
                intent.setType("*/*");

                startActivityForResult(intent, READ_REQUEST_CODE);
            }
        });
    }
}
