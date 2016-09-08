package com.tuggygs.bringsfdata;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.salesforce.androidsdk.app.SalesforceSDKManager;
import com.salesforce.androidsdk.rest.RestClient;
import com.salesforce.androidsdk.rest.RestRequest;
import com.salesforce.androidsdk.rest.RestResponse;
import com.salesforce.androidsdk.ui.SalesforceActivity;
import com.tuggygs.bringsfdata.R;

import org.json.JSONArray;

import java.io.UnsupportedEncodingException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Turgay on 26.7.2016 г..
 */
public class PredictorLogin extends SalesforceActivity {

    private RestClient client;

    Button login_btn;
    EditText gmUsername;
    EditText gmPassword;
    String gmUsernameStr = "", gmPasswordStr = "", baseURL = "https://eu6.salesforce.com/services/apexrest/CheckLogin/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.predictor_login_activity);

        login_btn = (Button)findViewById(R.id.loginCredentials);
        login_btn.setOnClickListener( new View.OnClickListener() {
            public void onClick(View v) {

                gmUsername = (EditText)findViewById(R.id.etUsername);
                gmPassword = (EditText)findViewById(R.id.etPassword);

                if(gmUsername.getText().toString().trim().equals("")) {
                    gmUsername.setError("User name is required!");
                }else if(!isEmailValid(gmUsername.getText().toString().trim())){
                    gmUsername.setError("Please enter a valid email!");
                }else if(gmPassword.getText().toString().trim().equals("")) {
                    gmPassword.setError("Password is required!");
                }else {

                    gmUsernameStr = gmUsername.getText().toString().trim();
                    gmPasswordStr = gmPassword.getText().toString().trim();

                    try {
                        sendRequest(gmUsernameStr, gmPasswordStr);
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        TextView registrationLink = (TextView) findViewById(R.id.registerLink);
        registrationLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(PredictorLogin.this, PredictorRegistration.class);
                startActivity(i);
            }
        });
    }
//-----------------------------------------------------------------------
    private void sendRequest(String prEmail, String prPass) throws UnsupportedEncodingException {

        String wholeURL = baseURL + prEmail + "&" + prPass;

        RestRequest restRequest = new RestRequest(RestRequest.RestMethod.GET, wholeURL, null);

       // RestRequest restRequest = RestRequest.getRequestForQuery(getString(R.string.api_version), soql);

        Log.e("GMAIL USERNAME", prEmail);
        Log.e("GMAIL PASSWORD", prPass);

        client.sendAsync(restRequest, new RestClient.AsyncRequestCallback() {
            @Override
            public void onSuccess(RestRequest request, RestResponse result) {
                Log.d("RRRRESPPP2====>", String.valueOf(result));
//String str = result.toString().replaceAll("^\"|\"$", "");

                if(!result.toString().replaceAll("^\"|\"$", "").equals("false")) { //Boolean.valueOf(String.valueOf(result))  /  !str.equals("false")
                    Log.e("CREDENTIALS","MATCHES!!!");
                    //Toast.makeText(getApplicationContext(), "MATCHES!!!", Toast.LENGTH_SHORT).show();
                    Intent i = new Intent(PredictorLogin.this, PredictoinMaker.class);
                    i.putExtra("LOGEDIN_PREDICTOR", result.toString().replaceAll("^\"|\"$", ""));
                    startActivity(i);
                }else {
                    Log.e("CREDENTIALS","DOESN'T MATCH!!!");
                    Toast.makeText(getApplicationContext(), "Wrong username or password...", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(Exception exception) {
                //Toast.makeText(PredictorLogin.this, PredictorLogin.this.getString(SalesforceSDKManager.getInstance().getSalesforceR().stringGenericError(), exception.toString()), Toast.LENGTH_LONG).show();
            }
        });
    }
//-----------------------------------------------------------------------





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
        this.client = client;
        //this.client2 = client;

        // Show everything
        //findViewById(R.id.rootpredict).setVisibility(View.VISIBLE);
    }
}
