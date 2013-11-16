package com.enezaeducation.mwalimu;

public class LoginActivity extends BaseActivity {
	
	/*
	 * CONSTANTS
	 */
	
	/** tag */
    private static final String TAG = "LoginActivity";
	
	/*
	 * MEMBERS
	 */
	
	/** user */
	private User user = null;
	
	/*
	 * INTERFACE COMPONENTS
	 */
	
	/** input for username */
	private EditText fieldUsername = null;

	/** input for password */
	private EditText fieldPassword = null;
	
	
	/*
	 * INITIALISATION
	 */

	/** initialise members, interface and try to log in */
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        initialiseMembers();
        
        initialiseInterface(R.layout.activity_login);

    	// try to log in if it's not a log out
        Bundle extras = getIntent().getExtras();
    	if((extras == null || !extras.getBoolean("logout")) && Utils.isOnline(this) && user.verify()) {
    		// log in if : wasn't just logged out : device is online : password/username are not empty
    		login();
    	}
    }

	/** initialise valuable members */
	protected void initialiseMembers() {
		// initialise user
		user = User.getInstance(this);

		// initialise session
		session = Session.getInstance();
		
		// initialise popup helper
		popupHelper = new PopupHelper(this);
	}
	
	/** initialise interface components */
	@Override
	protected void initialiseInterface(int layout) {
		super.initialiseInterface(layout);
		
		// initialise progress dialogue
		progressDialog = new ProgressDialog(this);
		progressDialog.setTitle(getString(R.string.login_progress_title));
		progressDialog.setMessage(getString(R.string.login_progress_body));
		
		// initialise sign in button
		Button btnSignIn = (Button)findViewById(R.id.btnSignIn);
    	btnSignIn.setOnClickListener(btnListener);
    	
    	// initialise offline button
    	Button btnOffline = (Button)findViewById(R.id.btnOffline);
    	btnOffline.setOnClickListener(btnOfflineListener);
    	
    	// username field
    	fieldUsername = (EditText)findViewById(R.id.inputUsername);
    	fieldUsername.setText(user.getUsername());
    	
    	// password field
    	fieldPassword = (EditText)findViewById(R.id.inputPassword);
    	fieldPassword.setText(user.getPassword());
	}
	
	/*
	 * PRIVATE METHODS
	 */
	
	/** try to log in, does not check Internet availability */
	private void login() {
		// show progress
		progressDialog.show();
		
		ServerTask.makeTask(this, Constants.LOGIN_URL, new ServerCallback() {
			@Override
			public void run() {
				// hide progress dialogue
				progressDialog.hide();
				
				if(status == ServerTask.REQUEST_SUCCESS) {
					// response available
					if(response != null) {
						boolean loggedIn;
						try {
							loggedIn = response.getBoolean("user");
							if(loggedIn) {
								// logged in
								session.setOfflineMode(false);
								
								if(((CheckBox)findViewById(R.id.remember)).isChecked()) {
									user.save();
								} else {
									user.remove();
								}
								
								// release the progress dialogue
								progressDialog.dismiss();
								
								switchToMainActivity();
							} else {
								int reason = response.getInt("reason");
								popupHelper.makeUserLoginError(reason);
							}
							
							return; // these error (if any) are not 'server' errors
						} catch(JSONException e) {
							if(DEBUG) {
								Log.e(TAG, "Server error", e);
							}
						}
					}
				}
				popupHelper.makeResponseError(response, status);
			}
		});
	}

	/** start MainActivity and closes LoginActivity */
	private void switchToMainActivity() {
		Intent mainActivity = new Intent(this, MainActivity.class);
		this.startActivity(mainActivity);
		finish();
	}
	
	/*
	 * PRIVATE CALLBACKS
	 */
	
	/** when log in button clicked */
	private OnClickListener btnListener = new OnClickListener() {
		@Override
        public void onClick(View v) {
			if(session.isOnline(LoginActivity.this)) {
				
				user.setUsername(fieldUsername.getText().toString());
				user.setPassword(fieldPassword.getText().toString());
	            
				// try to log in
				login();
			} else {
				popupHelper.makeOfflineAlert();
			}
        }
    };
    
    /** when offline button clicked */
    private OnClickListener btnOfflineListener = new OnClickListener() {
		@Override
        public void onClick(View v) {
			session.setOfflineMode(true);
			switchToMainActivity();
        }
    };
}