package com.tuggygs.bringsfdata;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.salesforce.androidsdk.app.SalesforceSDKManager;
import com.salesforce.androidsdk.rest.RestClient;
import com.salesforce.androidsdk.rest.RestRequest;
import com.salesforce.androidsdk.rest.RestResponse;
import com.salesforce.androidsdk.ui.SalesforceActivity;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Turgay on 3/9/2016.
 */
public class DetailActivity extends SalesforceActivity {

    private RestClient client;

    private EditText fnameField, lnameField, emailField, phoneField;
    private Button saveContactt;

    Map<String, Object> fields = new HashMap<String, Object>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail);

        fnameField = (EditText) findViewById(R.id.fname_field);
        lnameField = (EditText) findViewById(R.id.lname_field);
        emailField = (EditText) findViewById(R.id.email_field);
        phoneField = (EditText) findViewById(R.id.phone_field);

        saveContactt = (Button)findViewById(R.id.saveContact);
        saveContactt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                fields.put("FirstName", fnameField.getText().toString());
                fields.put("LastName", lnameField.getText().toString());
                fields.put("Email", emailField.getText().toString());
                fields.put("Phone", phoneField.getText().toString());
                Log.d("LNAME***** ELSE =====> ", lnameField.getText().toString());

                if(lnameField.getText().toString() != null && lnameField.getText().toString() != "") {
                    Log.d("LNAME***** IF =====> ", lnameField.getText().toString());
                    addContact();
                }else {
                    Log.d("LNAME***** ELSE =====> ", lnameField.getText().toString());

                    Toast.makeText(DetailActivity.this,
                            DetailActivity.this.getString(SalesforceSDKManager.getInstance().getSalesforceR().stringGenericError(),"Please fill Last Name!"),
                            Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    public void addContact() { //View v

        //Map<String, Object> fields = new HashMap<String, Object>();
        //fields.put("FirstName", "First");
        //fields.put("LastName", "Last");

        RestRequest restRequest;
        try {
            //restRequest = RestRequest.getRequestForUpdate(getString(R.string.api_version), "Contact", id, fields);
            restRequest = RestRequest.getRequestForCreate(getString(R.string.api_version), "Contact", fields);
            //restRequest = RestRequest.getRequestForDelete(getString(R.string.api_version),"Contact","00358000002rH0SAAU");
        } catch (Exception e) {
            // You might want to log the error or show it to the user
            return;
        }
        client.sendAsync(restRequest, new RestClient.AsyncRequestCallback() {
            @Override
            public void onSuccess(RestRequest request, RestResponse result) {
                try {
                    //DetailActivity.this.finish();
                    Log.d("CREATE Responce====>", result.toString());
                    Toast.makeText(DetailActivity.this,DetailActivity.this.getString(SalesforceSDKManager.getInstance().getSalesforceR().stringGenericError(),"The contact is saving..."),
                            Toast.LENGTH_LONG).show();
                    Intent i = new Intent(DetailActivity.this, MainActivity.class);
                    startActivity(i);
                } catch (Exception e) {
                    // You might want to log the error or show it to the user
                }
            }

            @Override
            public void onError(Exception e) {
                // You might want to log the error or show it to the user
            }
        });

    }

    @Override
    public void onResume(RestClient client) {
        // Keeping reference to rest client
        this.client = client;

        // Show everything
        findViewById(R.id.root).setVisibility(View.VISIBLE);
    }

}
