package com.tuggygs.bringsfdata;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.salesforce.androidsdk.app.SalesforceSDKManager;
import com.salesforce.androidsdk.rest.RestClient;
import com.salesforce.androidsdk.rest.RestRequest;
import com.salesforce.androidsdk.rest.RestResponse;
import com.salesforce.androidsdk.ui.SalesforceActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Turgay on 7/1/2016.
 */
public class PredictoinMaker extends SalesforceActivity {

    private RestClient client, client2;
    private ArrayAdapter<String> listAdapter;
    private Spinner spinnerFood;
    private ArrayList<String> predictorList;
    JSONArray predictors = null, matches = null;
    private static final String TAG_NAME = "Name", TAG_HOME_TEAM = "Home__c", TAG_AWAY_TEAM = "Away__c", TAG_TOTAL_POINTS = "TotalPoints__c";
    private String selectedPredictor;
    private Integer totalPoints = 1;
    Map<String, Object> fields = new HashMap<String, Object>();
    private Button saveButton;
    private TextView tpoints;
    private LinearLayout ll_outer;

    ProgressDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.prediction_activity);

        Intent intent = getIntent();
      /*  if (intent != null) {
            selectedPredictor = intent.getStringExtra("LOGEDIN_PREDICTOR");
            Log.e("LOGEDIN_PREDICTOR_1", selectedPredictor);
        } */

        spinnerFood = (Spinner) findViewById(R.id.spinPredictors);
        predictorList = new ArrayList<String>();

        ll_outer = (LinearLayout)findViewById(R.id.predictionLayout);
        ll_outer.setGravity(Gravity.CENTER_HORIZONTAL);

        saveButton = (Button) findViewById(R.id.savePrediction);
        tpoints = (TextView) findViewById(R.id.totalPoints);

        spinnerFood.setVisibility(View.INVISIBLE);
        saveButton.setVisibility(View.INVISIBLE);
        tpoints.setVisibility(View.INVISIBLE);

        spinnerFood.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {

                String URL = "https://eu6.salesforce.com/services/apexrest/Predictions/";
                selectedPredictor = spinnerFood.getSelectedItem().toString();

                ll_outer.removeAllViewsInLayout();

                RestRequest restRequest2 = new RestRequest(RestRequest.RestMethod.GET, URL+selectedPredictor.replaceAll(" ", "_"), null);
                Log.e("restRequest2", restRequest2.toString());
                client2.sendAsync(restRequest2, new RestClient.AsyncRequestCallback() {
                    @Override
                    public void onSuccess(RestRequest request, RestResponse result) {
                        Log.d("MATCHES and PREDICTIONS", "-->" + String.valueOf(result));
                        String jsonStr = String.valueOf(result);

                        if(jsonStr != null) {
                            try {
                                matches = new JSONArray(jsonStr);

                                int count = 0;

                                // looping through All Matches
                                for(int i = 0; i < matches.length(); i++) {

                                    String goalsHome = "", goalsAway = "", matchPoints = "";
                                    LinearLayout row = new LinearLayout(getApplicationContext());
                                    row.setOrientation(LinearLayout.HORIZONTAL);
                                    row.setGravity(Gravity.CENTER_HORIZONTAL);

                                    TextView home_tv = new TextView(getApplicationContext());
                                    EditText hgoals_et = new EditText(getApplicationContext());
                                    TextView away_tv = new TextView(getApplicationContext());
                                    EditText agoals_et = new EditText(getApplicationContext());
                                    TextView mpoints_tv = new TextView(getApplicationContext());

                                    JSONObject c = matches.getJSONObject(i);

                                    //String homeGoals = c.getJSONObject("Match__r").get("Away__c").toString();
                                    //String awayGoals = c.getJSONObject("Match__r").get("Home__c").toString();
                                    String homeTeam = c.getString(TAG_HOME_TEAM);
                                    String awayTeam = c.getString(TAG_AWAY_TEAM);

                                    if(c.has("Predictions__r")) {
                                        JSONArray matchPrediction = c.getJSONObject("Predictions__r").getJSONArray("records");
                                        JSONObject predObj = (JSONObject) matchPrediction.get(0);

                                        goalsHome = predObj.optString("Goals_Home__c").toString();
                                        goalsAway = predObj.optString("Goals_Away__c").toString();
                                        matchPoints = predObj.optString("MatchPoints__c").toString();
                                        totalPoints = predObj.getJSONObject("MatchPredictor__r").optInt(TAG_TOTAL_POINTS);

                                        Log.e("MATCH POINTS ====> ", matchPoints);
                                    }else {
                                        Log.e("No value for ", "Predictions__r...");
                                    }

                                    tpoints.setText("Total points: " + totalPoints.toString());

                                    home_tv.setId(count);
                                    hgoals_et.setId(++count);
                                    agoals_et.setId(++count);
                                    away_tv.setId(++count);
                                    mpoints_tv.setId(++count);
                                    count++;

                                    row.addView(home_tv);
                                    home_tv.setText(homeTeam);
                                    row.addView(hgoals_et);
                                    hgoals_et.setText(goalsHome);
                                    row.addView(agoals_et);
                                    agoals_et.setText(goalsAway);
                                    row.addView(away_tv);
                                    away_tv.setText(awayTeam);

                                    row.addView(mpoints_tv);
                                    mpoints_tv.setText(" / " + matchPoints + "p");

                                    ll_outer.addView(row);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        } else {
                            Log.e("ServiceHandler", "Couldn't get any data from the url");
                        }
                    }

                    @Override
                    public void onError(Exception exception) {
                        Toast.makeText(PredictoinMaker.this,
                                PredictoinMaker.this.getString(SalesforceSDKManager.getInstance().getSalesforceR().stringGenericError(), exception.toString()),
                                Toast.LENGTH_LONG).show();
                    }
                });



            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });
    }

//--------------------SAVE---------------------------------------------------------------------------
    public void savePredictions(View v) {

        String reqURL = "https://eu6.salesforce.com/services/apexrest/NewPredictions/", wholeURL;
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

                wholeURL = reqURL + selectedPredictor + "&" + gh.getText().toString() + "&" + ga.getText().toString() + "&" + ht.getText().toString() + "-" + at.getText().toString();

                try {
                    restRequest = new RestRequest(RestRequest.RestMethod.POST, wholeURL, null);
                }catch(Exception e) {
                    // You might want to log the error or show it to the user
                    return;
                }
                client.sendAsync(restRequest, new RestClient.AsyncRequestCallback() {
                    @Override
                    public void onSuccess(RestRequest request, RestResponse result) {
                        try {

                            Log.d("CREATE Responce====>", result.toString());
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
//--------------------------------------------------------------------------------------------------------------------------

//------------------------------------------------------------
    private void loadPredictors() {

        ArrayList<String> lables = new ArrayList<String>();

		for(int i = 0; i < predictorList.size(); i++) {
			if(predictorList.get(i).equals(selectedPredictor)) {
                lables.add(0, predictorList.get(i));
            }else {
                lables.add(i, predictorList.get(i));
            }
		}

        // Creating adapter for spinner
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, lables);

        // Drop down layout style - list view with radio button
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        spinnerFood.setAdapter(spinnerAdapter);
    }
//--------------------------------------------------------------------------------------------------------------------

//---------------------------------POPULATE PREDICTIONS------------------------------------------------------------------------------------------
    public void onFetchPredictorsClick(final View view) throws UnsupportedEncodingException {

        spinnerFood.setVisibility(View.VISIBLE);
        saveButton.setVisibility(View.VISIBLE);

        tpoints.setVisibility(View.VISIBLE);

        RestRequest restRequest = new RestRequest(RestRequest.RestMethod.GET, "https://eu6.salesforce.com/services/apexrest/Predictors", null);

        client.sendAsync(restRequest, new RestClient.AsyncRequestCallback() {
            @Override
            public void onSuccess(RestRequest request, RestResponse result) {
                Log.d("PREDICTORS====>", String.valueOf(result));
                String jsonStr = String.valueOf(result);

                if(jsonStr != null) {
                    try {
                        predictors = new JSONArray(jsonStr);

                        predictorList.clear();

                        // looping through All Contacts
                        for (int i = 0; i < predictors.length(); i++) {
                            JSONObject c = predictors.getJSONObject(i);

                            String name = c.getString(TAG_NAME);

                            predictorList.add(name);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    Log.e("ServiceHandler", "Couldn't get any data from the url");
                }
                loadPredictors();
            }

            @Override
            public void onError(Exception exception) {
                Toast.makeText(PredictoinMaker.this,
                        PredictoinMaker.this.getString(SalesforceSDKManager.getInstance().getSalesforceR().stringGenericError(), exception.toString()),
                        Toast.LENGTH_LONG).show();
            }
        });
    }








    @Override
    public void onResume() {
        // Hide everything until we are logged in
        findViewById(R.id.rootpredict).setVisibility(View.INVISIBLE);

        // Create list adapter
        // listAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, new ArrayList<String>());
        // ((ListView) findViewById(R.id.prediction_list)).setAdapter(listAdapter);

        super.onResume();
    }

    @Override
    public void onResume(RestClient client) {

        // Keeping reference to rest client
        this.client = client;
        this.client2 = client;

        // Show everything
        findViewById(R.id.rootpredict).setVisibility(View.VISIBLE);
    }


/////////////////////////////////
@Override
public boolean onCreateOptionsMenu(Menu menu) {

    // Inflate the menu; this adds items to the action bar if it is present.
    Bundle extras = getIntent().getExtras();
   // if(extras !=null) {
        //int Value = extras.getInt("id");
        //if(Value > 0) {
            getMenuInflater().inflate(R.menu.edit_scores, menu);
       // }else{
        //    getMenuInflater().inflate(R.menu.menu_main, menu);
      //  }
    //}

    return true;
}

    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);

        switch(item.getItemId()) {
            case R.id.Edit_Score:

                //name.setEnabled(true);
                //name.setFocusableInTouchMode(true);
                //name.setClickable(true);

                Intent i = new Intent(PredictoinMaker.this, ScoreEditor.class);
                startActivity(i);

                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
