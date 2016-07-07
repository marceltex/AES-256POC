package com.marcel.example.aes_256poc;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.Security;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;

public class AES256Activity extends AppCompatActivity {

    private static final int READ_REQUEST_CODE = 42;
    private static final String TAG = "AES256Activity";

    private String data; // String representation of file

    private static byte[] iv; // Initialisation vector

    private Button selectFileButton;

    private TextView debugInfoTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aes256);

        selectFileButton = (Button) findViewById(R.id.button_select_file);

        debugInfoTextView = (TextView) findViewById(R.id.text_view_debug_info);

        selectFileButton.setOnClickListener(new View.OnClickListener() {
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

        try {
            InputStream inputStream = getResources().getAssets().open("file.txt");

            data = readTextFromInputStream(inputStream);
            debugInfoTextView.setText("Original file: " + data);

            // Generate session key for AES 256 encryption
            SecretKey sessionKey = generateAESSessionKey();
            debugInfoTextView.setText(debugInfoTextView.getText() + "\n\nSecret key generated successfully!");

            // Encrypt file using AES 256 encryption and generated session key
            byte[] encryptedFile = encryptFile(sessionKey, data.getBytes());
            debugInfoTextView.setText(debugInfoTextView.getText() + "\n\nEncrypted file: " + new String(encryptedFile, "UTF-8"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent resultData) {
        if (requestCode == READ_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            Uri uri;
            if (resultData != null) {
                uri = resultData.getData();
                Log.i(TAG, "Uri: " + uri.toString());
                try {
                    InputStream inputStream = getContentResolver().openInputStream(uri);

                    data = readTextFromInputStream(inputStream);
                    debugInfoTextView.setText("Original file: " + data);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Method to return a string representation of a file
     *
     * @param inputStream Input Stream of the file
     * @return String representation of the file
     */
    private String readTextFromInputStream(InputStream inputStream) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        StringBuilder stringBuilder = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            stringBuilder.append(line);
        }
        inputStream.close();
        reader.close();
        return stringBuilder.toString();
    }

    /**
     * Method to generate and return a session key to use for AES encryption.
     *
     * @return SecretKey session key that will be used for AES encryption
     */
    private SecretKey generateAESSessionKey() {
        KeyGenerator keyGen = null;
        try {
            keyGen = KeyGenerator.getInstance("AES");
            keyGen.init(128);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return keyGen.generateKey();
    }

    /**
     * Method used to encrypt file.
     *
     * @param sessionKey Session key to be used for AES encryption
     * @param buffer Binary representation of the file to be encrypted
     * @return Encrypted file as a byte array
     * @throws Exception Throws exception if algorithm for cipher not found
     */
    private byte[] encryptFile(SecretKey sessionKey, byte[] buffer) throws Exception {
        Security.insertProviderAt(new org.spongycastle.jce.provider.BouncyCastleProvider(), 1);
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding", "BC");
        SecureRandom randomSecureRandom = SecureRandom.getInstance("SHA1PRNG");
        iv = new byte[cipher.getBlockSize()];
        randomSecureRandom.nextBytes(iv);
        IvParameterSpec ivParams = new IvParameterSpec(iv);
        cipher.init(Cipher.ENCRYPT_MODE, sessionKey, ivParams);
        byte[] encrypted = cipher.doFinal(buffer);
        return encrypted;
    }
}
