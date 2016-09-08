package com.tuggygs.bringsfdata;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.salesforce.androidsdk.rest.RestClient;
import com.salesforce.androidsdk.rest.RestRequest;
import com.salesforce.androidsdk.rest.RestResponse;
import com.salesforce.androidsdk.ui.SalesforceActivity;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Turgay on 27.7.2016 г..
 */
public class PredictorRegistration extends SalesforceActivity {

    private RestClient clientr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registration_activity);


    }

    public void savePredictor(View v) {

        String baseURL = "https://eu6.salesforce.com/services/apexrest/NewPredictor/", reqURL;

        EditText prName, prUsername, prPassword;
        RestRequest restRequest;

        prName = (EditText) findViewById(R.id.etName);
        prUsername = (EditText) findViewById(R.id.etUsername);
        prPassword = (EditText) findViewById(R.id.etPassword);

        if(!prName.getText().toString().equals("") && !prUsername.getText().toString().equals("") && !prPassword.getText().toString().equals("")) {
            if(isEmailValid(prUsername.getText().toString())) {

                Log.e("USERNAME and PASSWORD-->", prUsername.getText() + " / " + prPassword.getText());
                //String str = result.toString().replaceAll("^\"|\"$", "");
                reqURL = baseURL + prName.getText().toString().replaceAll(" ", "_") + "&" + prUsername.getText().toString() + "&" + prPassword.getText().toString();

                try {
                    restRequest = new RestRequest(RestRequest.RestMethod.POST, reqURL, null);
                } catch (Exception e) {
                    // You might want to log the error or show it to the user
                    return;
                }

                clientr.sendAsync(restRequest, new RestClient.AsyncRequestCallback() {
                    @Override
                    public void onSuccess(RestRequest request, RestResponse result) {
                        try {
                            Log.d("CREATE Responce====>", result.toString());
                            if(Boolean.valueOf(String.valueOf(result))) {
                                Toast.makeText(getApplicationContext(), "Saving Predictor...", Toast.LENGTH_SHORT).show();
                                Intent i = new Intent(PredictorRegistration.this, PredictorLogin.class);
                                startActivity(i);
                            }else {
                                Toast.makeText(getApplicationContext(), "This email is already in use...", Toast.LENGTH_SHORT).show();
                            }

                        } catch (Exception e) {
                            // You might want to log the error or show it to the user
                        }
                    }

                    @Override
                    public void onError(Exception e) {
                        // You might want to log the error or show it to the user
                    }
                });

            }else {
                prUsername.setError("Please enter a valid email!");
            }

        }else {
            Toast.makeText(getApplicationContext(), "All fields are required...", Toast.LENGTH_SHORT).show();
        }

    }

    public static boolean isEmailValid(String email) {
        boolean isValid = false;

        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        CharSequence inputStr = email;

        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(inputStr);
        if (matcher.matches()) {
            isValid = true;
        }
        return isValid;
    }




    @Override
    public void onResume() {
        // Hide everything until we are logged in
        //findViewById(R.id.rootpredict).setVisibility(View.INVISIBLE);

        super.onResume();
    }

    @Override
    public void onResume(RestClient client) {

        // Keeping reference to rest client
        this.clientr = client;
        //this.client2 = client;

        // Show everything
        //findViewById(R.id.rootpredict).setVisibility(View.VISIBLE);
    }
}
