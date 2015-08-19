/*
 * Copyright 2014 IBM Corp. All Rights Reserved
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0 
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.ibm.bluelist;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.ibm.mobile.services.cloudcode.IBMCloudCode;
import com.ibm.mobile.services.core.http.IBMHttpResponse;
import com.ibm.mobile.services.data.IBMDataException;
import com.ibm.mobile.services.data.IBMDataObject;
import com.ibm.mobile.services.data.IBMQuery;
import com.sensoro.beacon.kit.Beacon;
import com.sensoro.beacon.kit.BeaconManagerListener;
import com.sensoro.cloud.SensoroManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import bolts.Continuation;
import bolts.Task;

public class MainActivity extends Activity {

	List<Item> itemList;
	BlueListApplication blApplication;
	ArrayAdapter<Item> lvArrayAdapter;
	ActionMode mActionMode = null;
	int listItemPosition;
	public static final String CLASS_NAME = "MainActivity";
	public static Button check;
    private static Button register;
    private static Button notify;
    public Intent myIntent;
    private final static int REQUEST_ENABLE_BT = 1;
    public String notiMessage;
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
        setContentView(R.layout.activity_main);
        SensoroManager sensoroManager = SensoroManager.getInstance(getApplicationContext());
		/* Use application class to maintain global state. */
        blApplication = (BlueListApplication) getApplication();
        //itemList = blApplication.getItemList();
		
		/* Set up the array adapter for items list view. */
        //ListView itemsLV = (ListView)findViewById(R.id.itemsList);
        //lvArrayAdapter = new ArrayAdapter<Item>(this, R.layout.list_item_1, itemList);
        //itemsLV.setAdapter(lvArrayAdapter);
		
		/* Refresh the list. */
        //listItems();
        if (getIntent().getBooleanExtra("EXIT", false)) {
            finish();
        }
        //check = (Button) findViewById(R.id.btnLogin);
        //register = (Button)findViewById(R.id.btnLinkToRegisterScreen);
        //notify = (Button)findViewById(R.id.notifyMessage);
        myIntent = new Intent(getApplicationContext(), DoSomething.class);
        //myIntent.putExtra("ID", "5");
        myIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);

        /*PendingIntent pendingIntent = PendingIntent.getActivity(
                getApplicationContext(),
                0,
                myIntent,
                Intent.FLAG_ACTIVITY_SINGLE_TOP);
        */
        /*
        notify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                /*Notification noti = new Notification.Builder(getApplicationContext())
                        .setContentTitle("New mail from " + "test")
                        .setContentText("Hey Welcome to EGL")
                        .setSmallIcon(R.drawable.common_signin_btn_icon_dark)
                        .build();

                //myIntent.putExtra("ID","5");
                NotificationManager notificationManager = (NotificationManager) getSystemService(
                        Context.NOTIFICATION_SERVICE);
                notificationManager.notify(5, builder.build());
            }
        });*/
        /*check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                IBMCloudCode.initializeService();
                IBMCloudCode myCloudCodeService = IBMCloudCode.getService();
                EditText email = (EditText)findViewById(R.id.email);
                EditText pwd = (EditText)findViewById(R.id.password);
                String e = email.getText().toString();
                String p = pwd.getText().toString();
                final String url = "login?email="+e+"&pwd="+p;
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
                                JSONObject obj = new JSONObject(responseString);
                                JSONArray arr = obj.getJSONArray("user");
                                if(arr.length()==0)
                                {
                                    System.out.println("Wrong Credentials");
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(getApplicationContext(), "Wrong Credentials", Toast.LENGTH_LONG).show();
                                        }
                                    });
                                    //Toast.makeText(getApplicationContext(),"Wrong Credentials", Toast.LENGTH_LONG).show();

                                }
                                else
                                {
                                    System.out.println(arr.getJSONObject(0).getString("Name"));
                                    final String name = arr.getJSONObject(0).getString("Name");
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(getApplicationContext(),"Welcome "+name, Toast.LENGTH_LONG).show();
                                        }
                                    });
                                    //Toast.makeText(getApplicationContext(),"Welcome "+arr.getJSONObject(0).getString("Name"), Toast.LENGTH_LONG).show();
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }


                            Log.i(CLASS_NAME, "Response Status from login: " + task.getResult().getHttpResponseCode());
                        }

                        return null;
                    }

                });
            }
        });
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),RegisterActivity.class);
                startActivity(intent);
            }
        });*/


        /*Beacon sensor*/
        if (sensoroManager.isBluetoothEnabled()) {
            /**
             * Enable cloud service (upload sensor data, including battery status, UMM, etc.)。Without setup, it keeps in closed status as default.
             **/
            sensoroManager.setCloudServiceEnable(true);
            /**
             * Enable SDK service
             **/
            try {
                sensoroManager.startService();
            } catch (Exception e) {
                e.printStackTrace(); // Fetch abnormal info
            }
        }
        else
        {
            Intent bluetoothIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(bluetoothIntent, REQUEST_ENABLE_BT);
        }
        BeaconManagerListener beaconManagerListener = new BeaconManagerListener() {
            ArrayList<String> foundSensors = new ArrayList<String>();

            @Override
            public void onUpdateBeacon(ArrayList<Beacon> beacons) {
                // Refresh sensor info
                for (Beacon beacon : beacons) {
                    if (true) {
                        if (beacon.getMovingState() == Beacon.MovingState.DISABLED) {
                            // Disable accelerometer
                            System.out.println(beacon.getSerialNumber() + "Disabled");
                            Log.d("Main", beacon.getSerialNumber() + "Disabled");


                        } else if (beacon.getMovingState() == Beacon.MovingState.STILL) {
                            // Device is at static
                            Log.d("Main", beacon.getSerialNumber() + "static");
                            Log.d("Main", beacon.getProximityUUID());
                            if (!foundSensors.contains(beacon.getProximityUUID())) {
                                //String messageDisplay = "";
                                Log.d("Main", "getting Beacon Info");
                                foundSensors.add(beacon.getProximityUUID());
                                IBMCloudCode.initializeService();
                                IBMCloudCode myCloudCodeService = IBMCloudCode.getService();
                                String id = beacon.getProximityUUID();
                                String url = "/getNotification?id="+id;
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
                                                JSONObject obj = new JSONObject(responseString);
                                                final String name = obj.getString("message");
                                                final String meets = obj.getString("meetups");
                                                //myIntent.putExtra("messaging", name);
                                                //messageDisplay = name;
                                                /*Notification*/

                                                myIntent.putExtra("response", responseString);
                                                //myIntent.putExtra("meets",meets);
                                                PendingIntent pendingNotificationIntent = PendingIntent.getActivity(getApplicationContext(),5,myIntent,PendingIntent.FLAG_UPDATE_CURRENT);
                                                //pendingNotificationIntent.flags |= Notification.FLAG_AUTO_CANCEL;
                                                //notification.setLatestEventInfo(getApplicationContext(), "Hi", notificationMessage, pendingNotificationIntent);
                                                final NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext());
                                                builder.setSmallIcon(R.drawable.logo);
                                                builder.setContentTitle("IBM Beacons Messenger");
                                                builder.setContentText(name);
                                                builder.setContentIntent(pendingNotificationIntent);
                                                NotificationManager notificationManager = (NotificationManager) getSystemService(
                                                        Context.NOTIFICATION_SERVICE);
                                                notificationManager.notify(5, builder.build());



                                                /*runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        Toast.makeText(getApplicationContext(),name, Toast.LENGTH_LONG).show();
                                                    }
                                                });*/
                                                //Toast.makeText(getApplicationContext(),"Welcome "+arr.getJSONObject(0).getString("Name"), Toast.LENGTH_LONG).show();

                                            } catch (IOException e) {
                                                e.printStackTrace();
                                            }


                                            Log.i(CLASS_NAME, "Response Status from login: " + task.getResult().getHttpResponseCode());
                                        }

                                        return null;
                                    }

                                });
                                //System.out.println("And the notification is "+notiMessage);
                                /*myIntent.putExtra("messaging", notiMessage);
                                PendingIntent pendingNotificationIntent = PendingIntent.getActivity(getApplicationContext(),5,myIntent,PendingIntent.FLAG_UPDATE_CURRENT);
                                //pendingNotificationIntent.flags |= Notification.FLAG_AUTO_CANCEL;
                                //notification.setLatestEventInfo(getApplicationContext(), "Hi", notificationMessage, pendingNotificationIntent);
                                final NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext());
                                builder.setSmallIcon(R.drawable.common_signin_btn_icon_dark);
                                builder.setContentTitle("BasicNotifications Sample");
                                builder.setContentText("Time to learn about notifications!");
                                builder.setContentIntent(pendingNotificationIntent);
                                NotificationManager notificationManager = (NotificationManager) getSystemService(
                                        Context.NOTIFICATION_SERVICE);
                                notificationManager.notify(5, builder.build());*/
                                //callPresenceInsights(beacon.getProximityUUID(),beacon.getMajor().toString(),beacon.getMinor().toString());
                            }
                            Log.d("Main", beacon.getMajor().toString());
                            Log.d("Main", beacon.getMinor().toString());
                            //Log.d("Main",beacon.getRssi().toString());
                            System.out.println(beacon.getSerialNumber() + "static");

                        } else if (beacon.getMovingState() == Beacon.MovingState.MOVING) {
                            // Device is moving
                            Log.d("Main", beacon.getSerialNumber() + "moving");
                            Log.d("Main", beacon.getProximityUUID());
                            System.out.println(beacon.getSerialNumber() + "moving");
                        }
                    }
                }
            }

            @Override
            public void onNewBeacon(Beacon beacon) {
                // New sensor found
                /*if (beacon.getSerialNumber().equals("0117C5456A36")){
                    // Yunzi with SN "0117C5456A36" enters the range
                }*/
                System.out.println(beacon.getSerialNumber());
                Log.d("Main", beacon.getSerialNumber() + "Got New");
            }

            @Override
            public void onGoneBeacon(Beacon beacon) {
                // A sensor disappears from the range
                System.out.println(beacon.getSerialNumber());

            }
        };
        sensoroManager.setBeaconManagerListener(beaconManagerListener);
    }
		/* Set long click listener. */
		/*itemsLV.setOnItemLongClickListener(new OnItemLongClickListener() {
		    public boolean onItemLongClick(AdapterView<?> adapter, View view, int position,
	                long rowId) {
		    	listItemPosition = position;
				if (mActionMode != null) {
		            return false;
		        }
		        mActionMode = MainActivity.this.startActionMode(mActionModeCallback);
		        return true;
		    }
		});
		EditText itemToAdd = (EditText) findViewById(R.id.itemToAdd);
		itemToAdd.setOnEditorActionListener(new OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId==EditorInfo.IME_ACTION_DONE){
                    createItem(v);
                    return true;
                }
                return false;
            }
        });
	}

	/**
	 * Removes text on click of x button.
	 *
	 * @param  v the edittext view.
	 */
	public void clearText(View v) {
		EditText itemToAdd = (EditText) findViewById(R.id.itemToAdd);
		itemToAdd.setText("");
	}
	
	/**
	 * Refreshes itemList from data service.
	 * 
	 * An IBMQuery is used to find all the list items.
	 */
	public void listItems() {
		try {
			IBMQuery<Item> query = IBMQuery.queryForClass(Item.class);
			/**
			 * IBMQueryResult is used to receive array of objects from server.
			 * 
			 * onResult is called when it successfully retrieves the objects associated with the 
			 * query, and will reorder these items based on creation time.
			 * 
			 * onError is called when an error occurs during the query.
			 */
			query.find().continueWith(new Continuation<List<Item>, Void>() {

                @Override
                public Void then(Task<List<Item>> task) throws Exception {
                    // Log error message, if the save task is cancelled.
                    if (task.isCancelled()) {
                        Log.e(CLASS_NAME, "Exception : Task " + task.toString() + " was cancelled.");
                    }
                    // Log error message, if the save task fails.
                    if (task.isFaulted()) {
                        Log.e(CLASS_NAME, "Exception : " + task.getError().getMessage());
                    }

                    // If the result succeeds, load the list.
                    else {
                        final List<Item> objects = task.getResult();
                        // Clear local itemList, as we'll be reordering & repopulating from DataService.
                        itemList.clear();
                        for (IBMDataObject item : objects) {
                            itemList.add((Item) item);
                        }
                        sortItems(itemList);
                        lvArrayAdapter.notifyDataSetChanged();
                    }
                    return null;
                }
            },Task.UI_THREAD_EXECUTOR);
			
		}  catch (IBMDataException error) {
			Log.e(CLASS_NAME, "Exception : " + error.getMessage());
		}
	}

	/**
	 * Send a notification to all devices whenever the BlueList is modified (create, update, or delete).
	 */
	private void updateOtherDevices() {

		// Initialize and retrieve an instance of the IBM CloudCode service.
		IBMCloudCode.initializeService();
		IBMCloudCode myCloudCodeService = IBMCloudCode.getService();
		JSONObject jsonObj = new JSONObject();
		try {
			jsonObj.put("key1", "value1");
		} catch (JSONException e) {
			e.printStackTrace();
		}

		/*
		 * Call the node.js application hosted in the IBM Cloud Code service
		 * with a POST call, passing in a non-essential JSONObject.
		 * The URI is relative to/appended to the BlueMix context root.
		 */
		
		myCloudCodeService.post("notifyOtherDevices", jsonObj).continueWith(new Continuation<IBMHttpResponse, Void>() {

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


                    Log.i(CLASS_NAME, "Response Status from notifyOtherDevices: " + task.getResult().getHttpResponseCode());
                }

                return null;
            }

        });

	}
	/**
	 * On return from other activity, check result code to determine behavior.
	 */
	@Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
		switch (resultCode)
		{
		/* If an edit has been made, notify that the data set has changed. */
		case BlueListApplication.EDIT_ACTIVITY_RC:
			updateOtherDevices();
			sortItems(itemList);
			lvArrayAdapter.notifyDataSetChanged();
    		break;
		}
    }
	
	/**
	 * Called on done and will add item to list.
	 *
	 * @param  v edittext View to get item from.
	 * @throws IBMDataException 
	 */
	public void createItem(View v) {
		EditText itemToAdd = (EditText) findViewById(R.id.itemToAdd);
		String toAdd = itemToAdd.getText().toString();
		Item item = new Item();
		if (!toAdd.equals("")) {
			item.setName(toAdd);
			/**
			 * IBMObjectResult is used to handle the response from the server after 
			 * either creating or saving an object.
			 * 
			 * onResult is called if the object was successfully saved.
			 * onError is called if an error occurred saving the object. 
			 */
			item.save().continueWith(new Continuation<IBMDataObject, Void>() {

				@Override
				public Void then(Task<IBMDataObject> task) throws Exception {
                     // Log error message, if the save task is cancelled.
                    if (task.isCancelled()) {
                        Log.e(CLASS_NAME, "Exception : Task " + task.toString() + " was cancelled.");
                    }
					 // Log error message, if the save task fails.
					else if (task.isFaulted()) {
						Log.e(CLASS_NAME, "Exception : " + task.getError().getMessage());
					}

					 // If the result succeeds, load the list.
					else {
						listItems();
						updateOtherDevices();
					}
					return null;
				}

			});

			// Set text field back to empty after item added.
			itemToAdd.setText("");
		}
	}
	
	/**
	 * Will delete an item from the list.
	 *
	 * @param  Item item to be deleted.
	 */
	public void deleteItem(Item item) {
		itemList.remove(listItemPosition);
		// This will attempt to delete the item on the server.
		item.delete().continueWith(new Continuation<IBMDataObject, Void>() {
			
			// Called if the object was successfully deleted.
			@Override
			public Void then(Task<IBMDataObject> task) throws Exception {
                 // Log error message, if the delete task is cancelled.
                if (task.isCancelled()) {
                    Log.e(CLASS_NAME, "Exception : Task " + task.toString() + " was cancelled.");
                }
				 // Log error message, if the delete task fail.
				else if (task.isFaulted()) {
					Log.e(CLASS_NAME, "Exception : " + task.getError().getMessage());
				}

				 // If the result succeeds, reload the list.
				else {
                    updateOtherDevices();
                    lvArrayAdapter.notifyDataSetChanged();
				}
				return null;
			}
		},Task.UI_THREAD_EXECUTOR);
		
		lvArrayAdapter.notifyDataSetChanged();
	}
	
	/**
	 * Will call new activity for editing item on list.
	 * @parm String name - name of the item.
	 */
	public void updateItem(String name) {
		Intent editIntent = new Intent(getBaseContext(), EditActivity.class);
    	editIntent.putExtra("ItemText", name);
    	editIntent.putExtra("ItemLocation", listItemPosition);
    	startActivityForResult(editIntent, BlueListApplication.EDIT_ACTIVITY_RC);
	}
	
	/**
	 * Sort a list of Items.
	 * @param List<Item> theList
	 */
	private void sortItems(List<Item> theList) {
		// Sort collection by case insensitive alphabetical order.
		Collections.sort(theList, new Comparator<Item>() {
			public int compare(Item lhs,
					Item rhs) {
				String lhsName = lhs.getName();
				String rhsName = rhs.getName();
				return lhsName.compareToIgnoreCase(rhsName);
			}
		});
	}
	
	private ActionMode.Callback mActionModeCallback = new ActionMode.Callback() {
	    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
	        /* Inflate a menu resource with context menu items. */
	        MenuInflater inflater = mode.getMenuInflater();
	        inflater.inflate(R.menu.editaction, menu);
	        return true;
	    }

	    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
	        return false;
	    }

		/**
		 * Called when user clicks on contextual action bar menu item.
		 * 
		 * Determined which item was clicked, and then determine behavior appropriately.
		 *
		 * @param  item menu item clicked
		 */
	    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
	    	Item lItem = itemList.get(listItemPosition);
	    	/* Switch dependent on which action item was clicked. */
	    	switch (item.getItemId()) {
	    		/* On edit, get all info needed & send to new, edit activity. */
	            case R.id.action_edit:
	            	updateItem(lItem.getName());
	                mode.finish(); /* Action picked, so close the CAB. */
	                return true;
	            /* On delete, remove list item & update. */
	            case R.id.action_delete:
	            	deleteItem(lItem);
	                mode.finish(); /* Action picked, so close the CAB. */
	            default:
	                return false;
	        }
	    }

	    /* Called on exit of action mode. */
	    public void onDestroyActionMode(ActionMode mode) {
	        mActionMode = null;
	    }
	};
}
