package com.enezaeducation.mwalimu;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.enezaeducation.mwalimu.server.ServerCallback;
import com.enezaeducation.mwalimu.server.ServerTask;

import android.os.Bundle;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

public class ClassActivity extends BaseActivity {
	
	private ListView listView = null;
	
	private Button btnAddClass = null;

	private final static String TAG = "ClassActivity";
	
	private ArrayList<Integer> classIds = null;
	private ArrayList<String> classNames = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initialiseInterface(R.layout.activity_class);
		
		initialiseMembers();
	}
	
	protected void initialiseMembers() {
		// list ivew
		listView = (ListView)findViewById(R.id.classesListView);
		
		String[] content = { "No classes" };		
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, content);
		listView.setAdapter(adapter);
		
		loadClasses();
		
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				Intent intent = new Intent(ClassActivity.this, ClassStudentsActivity.class);
				intent.putExtra("id", classIds.get(arg2));
		   		ClassActivity.this.startActivity(intent);
			}
		});
		
		// + button
		btnAddClass = (Button)findViewById(R.id.btnAddClass);
		btnAddClass.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				self = ClassActivity.this;
				Intent intent = new Intent(ClassActivity.this, AddClassActivity.class);
		   		ClassActivity.this.startActivity(intent);
			}
		});
	}
	
	public static ClassActivity self = null; 
	
	public void loadClasses() {
		final User user = User.getInstance(this);
		Log.i(TAG, Constants.BASE_URL + "users/" + user.getId() + "/classes");
		ServerTask task = new ServerTask(ClassActivity.this, Constants.BASE_URL + "users/" + user.getId() + "/classes", new ServerCallback() {
			@Override
			public void run() {
				// hide progress dialogue
				
				if(status == ServerTask.REQUEST_SUCCESS) {
					// response available
					if(response != null) {
						Log.i(TAG, response.toString());
						try {
							JSONArray classes = response.getJSONArray("classes");
							classIds = new ArrayList<Integer>();
							classNames = new ArrayList<String>();
							for(int i = 0; i < classes.length(); ++i) {
								JSONObject row = classes.getJSONObject(i);
								int id = row.getInt("id");
								String name = row.getString("subject");
								classIds.add(id);
								classNames.add(name);
							}
							ArrayAdapter<String> adapter = new ArrayAdapter<String>(ClassActivity.this, android.R.layout.simple_list_item_1, classNames);
							listView.setAdapter(adapter);
							return; // these error (if any) are not 'server' errors
						} catch(JSONException e) {
							if(Constants.DEBUG) {
								Log.e(TAG, "Server error", e);
							}
						}
					}
				}
				Utils.makeOkAlert(ClassActivity.this, "Server Error", "Sorry, Technical issues");
			}
		});
		//
		task.run();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.class_menu, menu);
		return true;
	}

}
