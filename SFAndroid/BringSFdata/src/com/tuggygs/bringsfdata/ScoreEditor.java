package com.tuggygs.bringsfdata;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.salesforce.androidsdk.rest.RestClient;
import com.salesforce.androidsdk.rest.RestRequest;
import com.salesforce.androidsdk.rest.RestResponse;
import com.salesforce.androidsdk.ui.SalesforceActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

/**
 * Created by Turgay on 2.8.2016 г..
 */
public class ScoreEditor extends SalesforceActivity {

    private RestClient clientt;
    JSONArray matches = null;
    private static final String TAG_NAME = "Name", TAG_HOME_TEAM = "Home__c", TAG_AWAY_TEAM = "Away__c", TAG_HGOALS_FT = "GoalsHomeFT__c", TAG_AGOALS_FT = "GoalsAwayFT__c";
    Button matchList_btn, saveScores_btn;
    String soql = "SELECT Name, Home__c, Away__c, GoalsHomeFT__c, GoalsAwayFT__c FROM Matches__c";
    private LinearLayout ll_outer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_score_activity);

        ll_outer = (LinearLayout)findViewById(R.id.matchLayout);
        ll_outer.setGravity(Gravity.CENTER_HORIZONTAL);

        matchList_btn = (Button) findViewById(R.id.tempButton);
        matchList_btn.setOnClickListener( new View.OnClickListener() {
            public void onClick(View v) {
                try {
                    sendRequest();
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        });

        saveScores_btn = (Button) findViewById(R.id.saveScores);
        saveScores_btn.setVisibility(View.INVISIBLE);
    }

    private void sendRequest() throws UnsupportedEncodingException {

        saveScores_btn.setVisibility(View.VISIBLE);
        //ll_outer.removeAllViewsInLayout();

        RestRequest restRequest = RestRequest.getRequestForQuery(getString(R.string.api_version), soql);

        clientt.sendAsync(restRequest, new RestClient.AsyncRequestCallback() {
            @Override
            public void onSuccess(RestRequest request, RestResponse result) {

                Log.d("RRRRESPPP2====>", String.valueOf(result));

                try {

                    JSONArray records = result.asJSONObject().getJSONArray("records");

                    int count = 0;
                    String hGoalsFT, aGoalsFT, homeTeam, awayTeam;
                    // looping through All Matches
                    for(int i = 0; i < records.length(); i++) {

                        LinearLayout row = new LinearLayout(getApplicationContext());
                        row.setOrientation(LinearLayout.HORIZONTAL);
                        row.setGravity(Gravity.CENTER_HORIZONTAL);

                        TextView home_tv = new TextView(getApplicationContext());
                        EditText hgoalsFT_et = new EditText(getApplicationContext());
                        TextView away_tv = new TextView(getApplicationContext());
                        EditText agoalsFT_et = new EditText(getApplicationContext());

                        homeTeam = records.getJSONObject(i).getString(TAG_HOME_TEAM);
                        awayTeam = records.getJSONObject(i).getString(TAG_AWAY_TEAM);
                        //hGoalsFT = records.getJSONObject(i).getString(TAG_HGOALS_FT);
                        //aGoalsFT = records.getJSONObject(i).getString(TAG_AGOALS_FT);

                        if(!String.valueOf(records.getJSONObject(i).getString(TAG_HGOALS_FT)).equals("null")) {
                            Log.e("TAG_HGOALS_FT ", records.getJSONObject(i).getString(TAG_HGOALS_FT));
                            hGoalsFT = records.getJSONObject(i).getString(TAG_HGOALS_FT);
                        }else {
                            hGoalsFT = "";
                        }
                        if(!String.valueOf(records.getJSONObject(i).getString(TAG_AGOALS_FT)).equals("null")) {
                            aGoalsFT = records.getJSONObject(i).getString(TAG_AGOALS_FT);
                        }else {
                            aGoalsFT = "";
                        }

                        Log.e("JSON DATA ===>", homeTeam + " - " + awayTeam );

                        home_tv.setId(count);
                        hgoalsFT_et.setId(++count);
                        agoalsFT_et.setId(++count);
                        away_tv.setId(++count);
                        count++;

                        row.addView(home_tv);
                        home_tv.setText(homeTeam);
                        row.addView(hgoalsFT_et);
                        hgoalsFT_et.setText(hGoalsFT);
                        row.addView(agoalsFT_et);
                        agoalsFT_et.setText(aGoalsFT);
                        row.addView(away_tv);
                        away_tv.setText(awayTeam);

                        ll_outer.addView(row);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Exception exception) {
                //Toast.makeText(PredictorLogin.this, PredictorLogin.this.getString(SalesforceSDKManager.getInstance().getSalesforceR().stringGenericError(), exception.toString()), Toast.LENGTH_LONG).show();
            }
        });
    }

//================================================================
    public void saveScores(View v) {

        String reqURL = "https://eu6.salesforce.com/services/apexrest/SaveScores/", wholeURL;
        int id1 = 1, id2 = 2, nextET = 0;
        EditText gh, ga;
        TextView ht, at;
        RestRequest restRequest;

        do {
            gh = (EditText) ll_outer.findViewById(id1 + nextET);
            ga = (EditText) ll_outer.findViewById(id2 + nextET);
            ht = (TextView) ll_outer.findViewById(id1 + nextET - 1);
            at = (TextView) ll_outer.findViewById(id2 + nextET + 1);

            if(gh != null || ga != null) {

                wholeURL = reqURL + gh.getText().toString() + "&" + ga.getText().toString() + "&" + ht.getText().toString() + "-" + at.getText().toString();

                try {
                    restRequest = new RestRequest(RestRequest.RestMethod.POST, wholeURL, null);
                }catch(Exception e) {
                    // You might want to log the error or show it to the user
                    return;
                }
                clientt.sendAsync(restRequest, new RestClient.AsyncRequestCallback() {
                    @Override
                    public void onSuccess(RestRequest request, RestResponse result) {
                        try {

                            Log.d("SAVE Responce====>", result.toString());
                            //Toast.makeText(getApplicationContext(), "Saving Predictions...", Toast.LENGTH_SHORT).show();
                        }catch(Exception e) {
                            // You might want to log the error or show it to the user
                        }
                    }

                    @Override
                    public void onError(Exception e) {
                        // You might want to log the error or show it to the user
                    }
                });
            }

            nextET = nextET + 4;

        }while(gh != null || ga != null);

    }
//================================================================








    @Override
    public void onResume() {
        // Hide everything until we are logged in
       //findViewById(R.id.rootpredict).setVisibility(View.INVISIBLE);

        super.onResume();
    }

    @Override
    public void onResume(RestClient client) {

        // Keeping reference to rest client
        this.clientt = client;

        // Show everything
        //findViewById(R.id.rootpredict).setVisibility(View.VISIBLE);
    }
}
