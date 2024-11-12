package com.example.tools_rental.View;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.tools_rental.R;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class login_page extends AppCompatActivity {

    private static final String TAG = "LoginPage";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_page);

        EditText emailField = findViewById(R.id.email);  // Ensure these IDs match your XML layout
        EditText passwordField = findViewById(R.id.password);
        Button loginButton = findViewById(R.id.login_btn);
        Button registerButton = findViewById(R.id.registerButton);

        // Start Register Activity on button click
        registerButton.setOnClickListener(v -> {
            startActivity(new Intent(this, register_page.class));
        });

        // Login button click listener
        loginButton.setOnClickListener(v -> {
            String email = emailField.getText().toString();
            String password = passwordField.getText().toString();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please enter email and password", Toast.LENGTH_SHORT).show();
            } else {
                loginUser(email, password);
            }
        });
    }

    private void loginUser(String email, String password) {
        new AsyncTask<String, Void, String>() {
            @Override
            protected String doInBackground(String... params) {
                String email = params[0];
                String password = params[1];
                HttpURLConnection conn = null;
                try {
                    // Update URL with the correct server URL (check your local network address)
                    URL url = new URL("http://10.74.220.48/tools_rental/login.php");
                    conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Content-Type", "application/json; utf-8");
                    conn.setRequestProperty("Accept", "application/json");
                    conn.setDoOutput(true);

                    // Create JSON object for login data
                    JSONObject jsonParam = new JSONObject();
                    jsonParam.put("email", email);
                    jsonParam.put("password", password);

                    // Write JSON to output stream
                    try (OutputStream os = conn.getOutputStream()) {
                        byte[] input = jsonParam.toString().getBytes("utf-8");
                        os.write(input, 0, input.length);
                    }

                    // Get response code
                    int responseCode = conn.getResponseCode();
                    Log.d(TAG, "Response Code: " + responseCode);

                    // Read the response body
                    StringBuilder response = new StringBuilder();
                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                        String inputLine;
                        while ((inputLine = in.readLine()) != null) {
                            response.append(inputLine);
                        }
                        in.close();
                        return response.toString(); // Returning server response for logging
                    } else {
                        return "Login failed: " + responseCode;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    return "Error logging in: " + e.getMessage();
                } finally {
                    if (conn != null) {
                        conn.disconnect();
                    }
                }
            }

            @Override
            protected void onPostExecute(String result) {
                // Handle the result on the main thread
                Log.d(TAG, "Response: " + result);
                if (result.contains("Login successful")) {
                    Toast.makeText(login_page.this, result, Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(login_page.this, userdashboard.class);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(login_page.this, result, Toast.LENGTH_SHORT).show();
                }
            }
        }.execute(email, password);
    }

}
