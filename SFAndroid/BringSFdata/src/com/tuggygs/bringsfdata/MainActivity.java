/*
 * Copyright (c) 2012, salesforce.com, inc.
 * All rights reserved.
 * Redistribution and use of this software in source and binary forms, with or
 * without modification, are permitted provided that the following conditions
 * are met:
 * - Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 * - Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * - Neither the name of salesforce.com, inc. nor the names of its contributors
 * may be used to endorse or promote products derived from this software without
 * specific prior written permission of salesforce.com, inc.
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package com.tuggygs.bringsfdata;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.salesforce.androidsdk.app.SalesforceSDKManager;
import com.salesforce.androidsdk.rest.RestClient;
import com.salesforce.androidsdk.rest.RestClient.AsyncRequestCallback;
import com.salesforce.androidsdk.rest.RestRequest;
import com.salesforce.androidsdk.rest.RestResponse;
import com.salesforce.androidsdk.ui.SalesforceActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

/**
 * Main activity
 */
public class MainActivity extends SalesforceActivity {

	private RestClient client;
	private ArrayAdapter<String> listAdapter;
	private Button addContact;

	JSONArray contacts = null;
	JSONArray places = null;
	private static final String TAG_NAME = "Name";
	public String contactTypeStr = "", cFirstName, cLastName, cEmail, cPhone;
	ListView lv;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
/*
		String consumerKey = this.getResources().getString(R.string.consumerKey).toString();
        String url = this.getResources().getString(R.string.oAuthUrl).toString();
        callbackUrl = this.getResources().getString(R.string.callbackUrl).toString();

        String reqUrl = url + "/services/oauth2/authorize?response_type=token&display=touch&client_id=" + consumerKey + "&redirect_uri=" + callbackUrl; */

		lv = (ListView) findViewById(R.id.contacts_list);
		lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

				if(contactTypeStr.equals("Contact")) {

					String selectedContact = (lv.getItemAtPosition(position)).toString();

					try {
						requestSelectedContact("SELECT Id, Name, FirstName, LastName, Email, Phone FROM Contact WHERE Name='"+selectedContact+"'");
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					}

					//Toast.makeText(getApplicationContext(), "CONTACT - " + selectedContact, Toast.LENGTH_LONG).show();
				}else {

					GlobalState gs = (GlobalState) getApplication();
					gs.setSelectedAccount(gs.getAccounts()[position]);
					launchAccountDetail();
					// When clicked, show a toast with the TextView text
					Toast.makeText(getApplicationContext(), ((TextView) view).getText(), Toast.LENGTH_SHORT).show();

					//Toast.makeText(getApplicationContext(), "This is NOT a Contact", Toast.LENGTH_LONG).show();
				}
			}

		});

		addContact = (Button)findViewById(R.id.add_contact);
		addContact.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent i = new Intent(MainActivity.this, DetailActivity.class);
				startActivity(i);
			}
		});
	}

//===========================================================================================
public void makePredictions(View v) {

	Intent i = new Intent(MainActivity.this, PredictoinMaker.class);
	startActivity(i);

	//Intent i = new Intent(MainActivity.this, PredictorLogin.class);
	//startActivity(i);

	//sendRequest("SELECT Name FROM Contact");
}
//===========================================================================================


	private void requestSelectedContact(String soql) throws UnsupportedEncodingException {

		RestRequest restRequest = RestRequest.getRequestForQuery(getString(R.string.api_version), soql);

		client.sendAsync(restRequest, new AsyncRequestCallback() {
			@Override
			public void onSuccess(RestRequest request, RestResponse result) {
				Log.e("RRRRESPPPooOO====>", String.valueOf(result));

				try {

					JSONArray records = result.asJSONObject().getJSONArray("records");

					for (int i = 0; i < records.length(); i++) {
						//contact.put(records.getJSONObject(i).getString("Id"), records.getJSONObject(i).getString("Name"));
						//listAdapter.add(records.getJSONObject(i).getString("FirstName"));
						cFirstName = String.valueOf(records.getJSONObject(i).getString("FirstName"));
						cLastName = records.getJSONObject(i).getString("LastName");
						cEmail = records.getJSONObject(i).getString("Email");
						cPhone = records.getJSONObject(i).getString("Phone");
					}

					Intent i = new Intent(MainActivity.this, ContactDetails.class);
					i.putExtra("Con_fName", cFirstName);
					i.putExtra("Con_lName", cLastName);
					i.putExtra("Con_Email", cEmail);
					i.putExtra("Con_Phone", cPhone);
					startActivity(i);

				} catch (Exception e) {
					onError(e);
				}
			}

			@Override
			public void onError(Exception exception) {
				Toast.makeText(MainActivity.this,
						MainActivity.this.getString(SalesforceSDKManager.getInstance().getSalesforceR().stringGenericError(), exception.toString()),
						Toast.LENGTH_LONG).show();
			}
		});
	}



	/**
	 * Called when "Fetch Contacts" button is clicked
	 *
	 * @param v
	 * @throws UnsupportedEncodingException
	 */
	public void onFetchContactsClick(View v) throws UnsupportedEncodingException {
		contactTypeStr = "Contact";
		sendRequest("SELECT Name FROM Contact");
	}

	/**
	 * Called when "Fetch Accounts" button is clicked
	 *
	 * @param v
	 * @throws UnsupportedEncodingException
	 */
	public void onFetchAccountsClick(View v) throws UnsupportedEncodingException {
		sendRequest("SELECT Name FROM Account");
	}

	private void sendRequest(String soql) throws UnsupportedEncodingException {

		RestRequest restRequest = RestRequest.getRequestForQuery(getString(R.string.api_version), soql);

		client.sendAsync(restRequest, new AsyncRequestCallback() {
			@Override
			public void onSuccess(RestRequest request, RestResponse result) {
				Log.d("RRRRESPPP2====>", String.valueOf(result));
				try {
					listAdapter.clear();
					JSONArray records = result.asJSONObject().getJSONArray("records");
					for (int i = 0; i < records.length(); i++) {
						listAdapter.add(records.getJSONObject(i).getString("Name"));
					}
				} catch (Exception e) {
					onError(e);
				}
			}

			@Override
			public void onError(Exception exception) {
				Toast.makeText(MainActivity.this,
						MainActivity.this.getString(SalesforceSDKManager.getInstance().getSalesforceR().stringGenericError(), exception.toString()),
						Toast.LENGTH_LONG).show();
			}
		});
	}

	public void dataFromRESTServis(View view) throws UnsupportedEncodingException {

		 RestRequest restRequest = new RestRequest(RestRequest.RestMethod.GET, "https://eu6.salesforce.com/services/apexrest/RestForAndroid", null);
		// restRequest = RestRequest.getRequestForCreate(getString(R.string.api_version), "Contact", fields);
		//RestRequest restRequest = new RestRequest(RestRequest.RestMethod.POST,"https://eu6.salesforce.com/services/apexrest/AddContact",fieldss);

		client.sendAsync(restRequest, new AsyncRequestCallback() {
			@Override
			public void onSuccess(RestRequest request, RestResponse result) {
				Log.d("RRRRESPPP2====>", String.valueOf(result));
				String jsonStr = String.valueOf(result);

				if (jsonStr != null) {
					try {
						places = new JSONArray(jsonStr);

						listAdapter.clear();

						// looping through All Contacts
						for (int i = 0; i < places.length(); i++) {
							JSONObject c = places.getJSONObject(i);

							String name = c.getString(TAG_NAME);

							listAdapter.add(name);
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
				Toast.makeText(MainActivity.this,
						MainActivity.this.getString(SalesforceSDKManager.getInstance().getSalesforceR().stringGenericError(), exception.toString()),
						Toast.LENGTH_LONG).show();
			}
		});
	}

	//********-------********
	public void placesFromSF(View view) throws UnsupportedEncodingException {

		RestRequest restRequest = new RestRequest(RestRequest.RestMethod.GET, "https://eu6.salesforce.com/services/apexrest/Destinations", null);
		// restRequest = RestRequest.getRequestForCreate(getString(R.string.api_version), "Contact", fields);
		//RestRequest restRequest = new RestRequest(RestRequest.RestMethod.POST,"https://eu6.salesforce.com/services/apexrest/AddContact",fieldss);

		client.sendAsync(restRequest, new AsyncRequestCallback() {
			@Override
			public void onSuccess(RestRequest request, RestResponse result) {
				Log.d("PLACES====>", String.valueOf(result));
				String jsonStr = String.valueOf(result);

				if (jsonStr != null) {
					try {
						contacts = new JSONArray(jsonStr);

						listAdapter.clear();

						// looping through All Contacts
						for (int i = 0; i < contacts.length(); i++) {
							JSONObject c = contacts.getJSONObject(i);

							String name = c.getString(TAG_NAME);

							listAdapter.add(name);
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
				Toast.makeText(MainActivity.this,
						MainActivity.this.getString(SalesforceSDKManager.getInstance().getSalesforceR().stringGenericError(), exception.toString()),
						Toast.LENGTH_LONG).show();
			}
		});
	}
	//****-------****


	/**
	 * Called when "Clear" button is clicked.
	 *
	 * @param v
	 */
	public void onClearClick(View v) {
		listAdapter.clear();
	}

	@Override 
	public void onResume() {
		// Hide everything until we are logged in
		findViewById(R.id.root).setVisibility(View.INVISIBLE);

		// Create list adapter
		listAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, new ArrayList<String>());
		((ListView) findViewById(R.id.contacts_list)).setAdapter(listAdapter);				
		
		super.onResume();
	}		
	
	@Override
	public void onResume(RestClient client) {
        // Keeping reference to rest client
        this.client = client; 

		// Show everything
		findViewById(R.id.root).setVisibility(View.VISIBLE);
	}

	/**
	 * Called when "Logout" button is clicked. 
	 * 
	 * @param v
	 */
	public void onLogoutClick(View v) {
		SalesforceSDKManager.getInstance().logout(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();

		Bundle dataBundle = new Bundle();
		//noinspection SimplifiableIfStatement
       /* if(id == R.id.addContact) {
            dataBundle.putInt("id", 0);

            Intent intent = new Intent(getApplicationContext(), tuggy.com.greeter.DisplayBirthdays.class);
            intent.putExtras(dataBundle);

            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item); */

		switch(id) {  //item.getItemId()
			case R.id.addContact:

/*
				Intent intentAC = new Intent(getApplicationContext(), tuggy.com.greeter.DisplayBirthdays.class);
				intentAC.putExtras(dataBundle);

				startActivity(intentAC); */

				return true;
			case R.id.setAlarmTime:
			/*	Intent intentST = new Intent(getApplicationContext(), tuggy.com.greeter.AlarmTimeSetting.class);
				startActivity(intentST);*/

				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}

	protected void launchAccountDetail() {
		Intent i = new Intent(this, AccountDetails.class);
		startActivity(i);
	}

}
