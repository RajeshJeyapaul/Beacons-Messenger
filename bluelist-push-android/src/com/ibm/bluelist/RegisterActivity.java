package com.ibm.bluelist;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.ActionMode;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;

import com.ibm.mobile.services.cloudcode.IBMCloudCode;
import com.ibm.mobile.services.core.http.IBMHttpResponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import bolts.Continuation;
import bolts.Task;

public class RegisterActivity extends Activity {

    List<Item> itemList;
    BlueListApplication blApplication;
    ArrayAdapter<Item> lvArrayAdapter;
    ActionMode mActionMode = null;
    int listItemPosition;
    public static final String CLASS_NAME = "REgisterActivity";
    public static Button check;

    @Override
    /**
     * onCreate called when main activity is created.
     *
     * Sets up the itemList, application, and sets listeners.
     *
     * @param savedInstanceState
     */
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
		
		/* Use application class to maintain global state. */
        blApplication = (BlueListApplication) getApplication();
        //itemList = blApplication.getItemList();
		
		/* Set up the array adapter for items list view. */
        //ListView itemsLV = (ListView)findViewById(R.id.itemsList);
        //lvArrayAdapter = new ArrayAdapter<Item>(this, R.layout.list_item_1, itemList);
        //itemsLV.setAdapter(lvArrayAdapter);
		
		/* Refresh the list. */
        //listItems();


        check = (Button) findViewById(R.id.btnRegister);
        check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText name = (EditText)findViewById(R.id.name);
                EditText email = (EditText)findViewById(R.id.email);
                EditText pwd = (EditText)findViewById(R.id.password);
                String n = name.getText().toString();
                String e = email.getText().toString();
                String p = pwd.getText().toString();
                final String url = "register?name="+n+"&email="+e+"&pwd="+p;
                System.out.println(url);
                IBMCloudCode.initializeService();
                IBMCloudCode myCloudCodeService = IBMCloudCode.getService();
                myCloudCodeService.get(url).continueWith(new Continuation<IBMHttpResponse, Void>() {

                    @Override
                    public Void then(Task<IBMHttpResponse> task) throws Exception {
                        if (task.isCancelled()) {
                            Log.e(CLASS_NAME, "Exception : Task" + task.isCancelled() + "was cancelled.");
                        } else if (task.isFaulted()) {
                            Log.e(CLASS_NAME, "Exception : " + task.getError().getMessage());
                        } else {
                            InputStream is = task.getResult().getInputStream();
                            try {
                                BufferedReader in = new BufferedReader(new InputStreamReader(is));
                                String responseString = "";
                                String myString = "";
                                while ((myString = in.readLine()) != null)
                                    responseString += myString;

                                in.close();
                                Log.i(CLASS_NAME, "Response Body: " + responseString);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }


                            Log.i(CLASS_NAME, "Response Status from register: " + task.getResult().getHttpResponseCode());
                        }

                        return null;
                    }

                });
                finish();
            }
        });

    }
}