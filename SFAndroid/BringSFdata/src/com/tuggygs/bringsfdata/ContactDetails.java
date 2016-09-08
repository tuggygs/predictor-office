package com.tuggygs.bringsfdata;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.salesforce.androidsdk.app.SalesforceSDKManager;
import com.salesforce.androidsdk.rest.RestClient;
import com.salesforce.androidsdk.ui.SalesforceActivity;

/**
 * Created by Turgay on 6/23/2016.
 */
public class ContactDetails extends SalesforceActivity {

    private RestClient clientt;
    private EditText fnameField, lnameField, emailField, phoneField;
    public String tempName, tmpfName, tmplName, tmpEmail, tmpPhone;
    View vv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contact_details);

        vv = (View) findViewById(R.id.rootContact);

        fnameField = (EditText) findViewById(R.id.fname_field);
        lnameField = (EditText) findViewById(R.id.lname_field);
        emailField = (EditText) findViewById(R.id.email_field);
        phoneField = (EditText) findViewById(R.id.phone_field);

        Bundle extras = getIntent().getExtras();
        if(extras != null) {
            tmpfName = extras.getString("Con_fName");
            tmplName = extras.getString("Con_lName");
            tmpEmail = extras.getString("Con_Email");
            tmpPhone = extras.getString("Con_Phone");

            fnameField.setText(tmpfName);
            lnameField.setText(tmplName);
            emailField.setText(tmpEmail);
            phoneField.setText(tmpPhone);

        }else {
            Log.e("ELSE Con_Name ", tmpfName);
        }

        Log.e("Con_DETAILS ", tmpfName+" "+tmplName+" - "+tmpEmail + " / " + tmpPhone);
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.display_contact, menu);

        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);

        Log.d("ID: ", "ON ITEM SELECTED ");

        switch(item.getItemId()) {
            case R.id.Edit_Contact:

                return true;
            case R.id.Delete_Contact:

               // Button b = (Button)findViewById(R.id.button1);
               // b.setVisibility(View.VISIBLE);

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage(R.string.deleteContact)
                        .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                               // mydb.deleteContact(id_To_Update);

                                Toast.makeText(getApplicationContext(), "Deleted Successfully", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(getApplicationContext(),com.tuggygs.bringsfdata.MainActivity.class);
                                startActivity(intent);
                            }
                        })
                        .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // User cancelled the dialog
                            }
                        });
                AlertDialog d = builder.create();
                d.setTitle("Are you sure");
                d.show();

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
        //return super.onOptionsItemSelected(item);
    }

    public void onLogoutClick(View v) {
        SalesforceSDKManager.getInstance().logout(this);
    }

    @Override
    public void onResume() {
        // Hide everything until we are logged in
        findViewById(R.id.rootContact).setVisibility(View.INVISIBLE);

        // Create list adapter
        // listAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, new ArrayList<String>());
        //((ListView) findViewById(R.id.contacts_list)).setAdapter(listAdapter);

        super.onResume();
    }

    @Override
    public void onResume(RestClient clientt) {
        // Keeping reference to rest client
        this.clientt = clientt;

        // Show everything
        findViewById(R.id.rootContact).setVisibility(View.VISIBLE);
    }


}
