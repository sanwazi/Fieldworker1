package com.example.fieldworker1;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.security.auth.PrivateCredentialPermission;

import com.example.asynTask.LogoutAsynTask;
import com.example.dao.AddLogDao;
import com.example.dao.DeleteLogDao;
import com.example.dao.ObservationDao;
import com.example.dao.TraitListContentDao;
import com.example.dao.TraitListDao;
import com.example.domain.AddLog;
import com.example.domain.DeleteLog;
import com.example.domain.Trait;
import com.example.domain.User;
import com.example.domain.TraitList;
import com.example.phpServer.TraitListPhpService;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.EditText;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.Toast;

public class HomeActivity extends Activity {

	private static final String PREFS_NAME = "MyPrefsFile";
    private BroadcastReceiver mReceiver;
	private ListView listView;
	private User user;
	private Intent intentAnalyseObser;
	private ArrayList<String> traitSelected;
	private ArrayList<String> obserSelected;
	private ArrayList<Integer> obserIDsSelected;
	private String chartType;

	private ObservationDao observationDao;
	private TraitListDao traitListDao;
	private TraitListContentDao traitListContentDao;

	private TableLayout obserTable;
	private Spinner traitSpinner;
	private Spinner trait2Spinner;
	private RadioGroup group;
	private RadioButton barButton;
	private RadioButton lineButton;
	private EditText fromDate;
	private EditText toDate;

	private Date from, to;
	private List<String> observations;
	private List<Integer> observationIDs;
	private List<String> observationsForDate;
	private AddLogDao addLogDao;
	private DeleteLogDao deleteLogDao;
	@Override
	protected void onResume() {
		super.onStart();
		IntentFilter intentFilter=new IntentFilter();
		intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
		mReceiver=new BroadcastReceiver() {
			
			@Override
			public void onReceive(Context context, Intent intent) {
				String action=intent.getAction();
				if (action.equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
					ConnectivityManager con=(ConnectivityManager) getSystemService(context.CONNECTIVITY_SERVICE);
					NetworkInfo info=con.getActiveNetworkInfo();
					if (info!=null&&info.isAvailable()) {
						String name = info.getTypeName();
	                    System.out.println("current network name：" + name);
					}
					else {
						System.out.println("no available network");
	                }
				}
				
			}
		};
		registerReceiver(mReceiver, intentFilter);
		
	}
	

	@Override
    public void onPause() {
        super.onPause();
        unregisterReceiver(mReceiver);
      
    }
	@Override
	public void onDestroy()
	{
		super.onDestroy();
		LogoutAsynTask lTask=new LogoutAsynTask();
		lTask.execute( Constant.urlString + "Logout.php",user.getUserName());
		System.out.println("HomeActivity onDestroy");
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);
		SharedPreferences mySharedPreferences = getSharedPreferences(
				PREFS_NAME, 0);
		String username = mySharedPreferences.getString("username", "");
		Intent intent = getIntent();
		user = new User(intent.getStringExtra("username"),
				intent.getStringExtra("password"));
		if (isNetworkOnline()) {
			TraitListPhpService traitListPhpService = new TraitListPhpService(
					HomeActivity.this,user.getUserName());

			// synchonize the data of delete log and add log and updata sqlite
			// traitList table
			// firstly, add log table
			List<AddLog> addLogs = new ArrayList<AddLog>();
			addLogDao = new AddLogDao(this);
			deleteLogDao = new DeleteLogDao(this);
			addLogs = addLogDao.findAllByTableName("TraitList");
			List<AddLog> addLogs2 = new ArrayList<AddLog>();
			addLogs2 = addLogDao.findAllByTableName("TraitListContent");
			List<DeleteLog> deleteLogs = new ArrayList<DeleteLog>();
			List<DeleteLog> deleteLogs2 = new ArrayList<DeleteLog>();
			deleteLogs = deleteLogDao.findAllByTableName("TraitList");
			deleteLogs2=deleteLogDao.findAllByTableName("TraitListContent");
			// JSONArray jsonArray=JSONArray.fromObject(addLogs);
			System.out.println("Add log size:"+addLogs.size());
			if (addLogs.size() > 0 || deleteLogs.size() > 0
					|| addLogs2.size() > 0||deleteLogs2.size()>0) {
                System.out.println("home activity addLogs size not empty");
				traitListPhpService.synTraitList(addLogs, addLogs2, deleteLogs,deleteLogs2);
			}

		}
		

		traitSelected = new ArrayList<String>();
		obserSelected = new ArrayList<String>();
		obserIDsSelected = new ArrayList<Integer>();
		intentAnalyseObser = new Intent();

		observationDao = new ObservationDao(this);
		traitListDao = new TraitListDao(this);
		traitListContentDao = new TraitListContentDao(this);

		List<String> selection = new ArrayList<String>();
		selection.add("Observation Management");
		selection.add("TraitList Management");
		
		selection.add("Trait Management");
		selection.add("Observation Analysis");
		selection.add("Upload/Download Image/Excel File");
        selection.add("More TraitList");
		listView = new ListView(this);
		listView.setAdapter(new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, selection));

		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				if (position == 0) {
					Intent intent = new Intent();
					intent.setClass(HomeActivity.this,
							ObservationListActivity.class);
					intent.putExtra("username", user.getUserName());
					intent.putExtra("password", user.getPassword());
					startActivity(intent);
				} else if (position == 1) {
					Intent intent = new Intent();
					intent.putExtra("username", user.getUserName());
					intent.setClass(HomeActivity.this, TraitListActivity2.class);
					//intent.setClass(HomeActivity.this, DownloadTraitListActivity.class);
					startActivity(intent);
				} else if (position == 2) {
					Intent intent = new Intent();
					intent.putExtra("username", user.getUserName());
					intent.setClass(HomeActivity.this, TraitActivity.class);
					startActivity(intent);

				} else if (position == 3) {
				  
				   
					intentAnalyseObser.putExtra("password", user.getPassword());
					intentAnalyseObser.putExtra("username", user.getUserName());

					obserSelected = new ArrayList<String>();
					obserIDsSelected=new ArrayList<Integer>();
					traitSelected = new ArrayList<String>();
    
					List<TraitList> traitLists = traitListDao.findAll();
					List<String> selections = new ArrayList<String>();

					for (Iterator<TraitList> iterator = traitLists.iterator(); iterator
							.hasNext();) {
						selections.add(((TraitList) iterator.next())
								.getTraitListName());
					}

					AlertDialog.Builder builder = new AlertDialog.Builder(
							HomeActivity.this);
					LayoutInflater factory = LayoutInflater
							.from(HomeActivity.this);
					final View myView = factory.inflate(
							R.layout.dialog_choose_observations, null);

					lineButton = (RadioButton) myView
							.findViewById(R.id.trait1_line_button);
					barButton = (RadioButton) myView
							.findViewById(R.id.trait1_bar_button);
					group = (RadioGroup) myView
							.findViewById(R.id.trait1_radioGroup);

					group.setOnCheckedChangeListener(new OnCheckedChangeListener() {

						@Override
						public void onCheckedChanged(RadioGroup arg0, int arg1) {
							// TODO Auto-generated method stub
							if (lineButton.getId() == arg1) {
								chartType = "";
								chartType += "line";
							} else if (barButton.getId() == arg1) {
								chartType = "";
								chartType += "bar";
							}
						}

					});

					fromDate = (EditText) myView
							.findViewById(R.id.trait1_from_date);
					toDate = (EditText) myView
							.findViewById(R.id.trait1_to_date);

					fromDate.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub
							showDialog(0);
						}

					});

					toDate.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub
							showDialog(1);
						}

					});

					obserTable = (TableLayout) myView
							.findViewById(R.id.observation_choose_table);

					ArrayAdapter traitListAdapter = new ArrayAdapter<String>(
							HomeActivity.this,
							android.R.layout.simple_spinner_item, selections);
					traitListAdapter
							.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
					Spinner traitListSpinner = (Spinner) myView
							.findViewById(R.id.traitListSpinner_dialog);
					traitListSpinner.setAdapter(traitListAdapter);
					traitListSpinner
							.setOnItemSelectedListener(new traitListSpinnerListener());

					traitSpinner = (Spinner) myView
							.findViewById(R.id.trait1Spinner_dialog);
					trait2Spinner = (Spinner) myView
							.findViewById(R.id.trait2Spinner_dialog);

					List<String> selections1 = new ArrayList<String>();
					ArrayAdapter traitAdapter = new ArrayAdapter(
							HomeActivity.this,
							android.R.layout.simple_spinner_item, selections1);
					traitAdapter
							.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
					traitSpinner.setAdapter(traitAdapter);
					trait2Spinner.setAdapter(traitAdapter);
					traitSpinner
							.setOnItemSelectedListener(new traitSpinnerListener());
					trait2Spinner
							.setOnItemSelectedListener(new trait2SpinnerListener());

					TableLayout obserTable = (TableLayout) myView
							.findViewById(R.id.observation_choose_table);

					builder.setTitle("Choose TraitList and Trait");
					builder.setView(myView);
					builder.setPositiveButton("OK",
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									
									if (chartType == "") {
										Toast.makeText(HomeActivity.this,
												"Please select a chart type.",
												Toast.LENGTH_SHORT).show();
									} else {
										intentAnalyseObser.putExtra(
												"chartType", chartType);
										intentAnalyseObser
												.putStringArrayListExtra(
														"traitName",
														traitSelected);
										intentAnalyseObser
												.putStringArrayListExtra(
														"observationName",
														obserSelected);
										intentAnalyseObser
												.putIntegerArrayListExtra(
														"observationID",
														obserIDsSelected);
										intentAnalyseObser.setClass(
												HomeActivity.this,
												DataChart.class);
										HomeActivity.this
												.startActivity(intentAnalyseObser);

									}

								}
							});
					builder.setNegativeButton("Cancel", null);
					builder.create().show();
				} else if (position == 5) {
					Intent intent=new Intent();
					intent.setClass(HomeActivity.this, DownloadTraitListActivity.class);
					startActivity(intent);
				}
				else {
					Intent intent = new Intent();
					intent.setClass(HomeActivity.this, FileTransActivity.class);
					intent.putExtra("username", user.getUserName());
					intent.putExtra("password", user.getPassword());
					startActivity(intent);
				}
			}

		});

		setContentView(listView);

	}

	class traitListSpinnerListener implements OnItemSelectedListener {

		@Override
		public void onItemSelected(AdapterView<?> parent, View view,
				int position, long id) {
			// TODO Auto-generated method stub

			fromDate.setText("");
			from = null;
			toDate.setText("");
			to = null;
			obserTable.removeAllViews();
			List<String> selections2 = traitListContentDao
					.searchTraitNames(traitListDao.findIdByName(parent
							.getItemAtPosition(position).toString()));
			List<Trait> traits = traitListContentDao
					.searchTraitsByTraitListID(traitListDao.findIdByName(parent
							.getItemAtPosition(position).toString()));
			for (int i = 0; i < selections2.size(); i++) {
				if (i == selections2.size())
					break;

				if (traits.get(i).getUnit() == null) {
					traits.remove(i);
					selections2.remove(i);
					i--;
				}
			}
			if (selections2.size() == 0)
				Toast.makeText(HomeActivity.this,
						"There is no analysable trait", Toast.LENGTH_SHORT)
						.show();
			ArrayAdapter traitAdapter = new ArrayAdapter(HomeActivity.this,
					android.R.layout.simple_spinner_item, selections2);
			List<String> selections3 = new ArrayList<String>();
			selections3.add("UnSelected");
			for (int i = 0; i < selections2.size(); i++)
				selections3.add(selections2.get(i));
			ArrayAdapter trait2Adapter = new ArrayAdapter(HomeActivity.this,
					android.R.layout.simple_spinner_item, selections3);
			traitAdapter
					.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			trait2Adapter
					.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

			traitSpinner.setAdapter(traitAdapter);
			traitSpinner.setOnItemSelectedListener(new traitSpinnerListener());

			trait2Spinner.setAdapter(trait2Adapter);
			trait2Spinner
					.setOnItemSelectedListener(new trait2SpinnerListener());

			observations = observationDao
					.searchObservationsWithTraitList(traitListDao
							.findIdByName(parent.getItemAtPosition(position)
									.toString()));
			observationIDs = observationDao
					.searchObservationsWithTraitList1(traitListDao
							.findIdByName(parent.getItemAtPosition(position)
									.toString()));
			observationsForDate = observationDao
					.searchObservationsWithTraitList(traitListDao
							.findIdByName(parent.getItemAtPosition(position)
									.toString()));
			refreshObserTable();
		}

		@Override
		public void onNothingSelected(AdapterView<?> parent) {
			// TODO Auto-generated method stub

		}

	}

	private void refreshObserTable() {
		obserTable.removeAllViews();
		String str = "";
		int index = 0;
		for (int i = 0; i < observations.size(); i++) {
			TableRow row = new TableRow(HomeActivity.this);
			str = "";
			str += observations.get(i);
			index = str.indexOf("---");
			CheckBox select = new CheckBox(HomeActivity.this);
			select.setId(observationIDs.get(i));
			if (index != -1)
				select.append(str.substring(0, index));
			else
				select.append(str);

			select.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

				@Override
				public void onCheckedChanged(CompoundButton buttonView,
						boolean isChecked) {
					// TODO Auto-generated method stub
					if (isChecked) {
						obserSelected.add(buttonView.getText().toString());
						
						obserIDsSelected.add(buttonView.getId());
					} else {
						obserSelected.remove(buttonView.getText().toString());
						obserIDsSelected.remove(buttonView.getId() + "");
					}
				}
			});
			row.addView(select);
			obserTable.addView(row);
		}
	}

	private void refreshObserTableForDate() {
		obserTable.removeAllViews();
		String str = "";
		int index = 0;
		for (int i = 0; i < observationsForDate.size(); i++) {
			TableRow row = new TableRow(HomeActivity.this);
			str = "";
			str += observationsForDate.get(i);
			index = str.indexOf("---");
			CheckBox select = new CheckBox(HomeActivity.this);
			if (index != -1)
				select.append(str.substring(0, index));
			else
				select.append(str);

			select.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

				@Override
				public void onCheckedChanged(CompoundButton buttonView,
						boolean isChecked) {
					// TODO Auto-generated method stub
					if (isChecked) {
						obserSelected.add(buttonView.getText().toString());
					} else {
						obserSelected.remove(buttonView.getText().toString());
					}
				}
			});
			row.addView(select);
			obserTable.addView(row);
		}
	}

	class traitSpinnerListener implements OnItemSelectedListener {

		@Override
		public void onItemSelected(AdapterView<?> parent, View view,
				int position, long id) {
			// TODO Auto-generated method stub

			if (traitSelected.size() < 1) {
				traitSelected
						.add(parent.getItemAtPosition(position).toString());
			} else {
				if (traitSelected.contains(parent.getItemAtPosition(position)
						.toString())) {
					Toast.makeText(
							HomeActivity.this,
							parent.getItemAtPosition(position).toString()
									+ " has been selected,select another one please.",
							Toast.LENGTH_SHORT).show();
					traitSelected.set(0, "");
				} else
					traitSelected.set(0, parent.getItemAtPosition(position)
							.toString());
			}
		}

		@Override
		public void onNothingSelected(AdapterView<?> parent) {
			// TODO Auto-generated method stub

		}

	}

	class trait2SpinnerListener implements OnItemSelectedListener {

		@Override
		public void onItemSelected(AdapterView<?> parent, View view,
				int position, long id) {
			// TODO Auto-generated method stub

			if (traitSelected.size() < 2) {
				if (position != 0)
					if (traitSelected.contains(parent.getItemAtPosition(
							position).toString())) {
						Toast.makeText(
								HomeActivity.this,
								parent.getItemAtPosition(position).toString()
										+ " has been selected,select another one please.",
								Toast.LENGTH_SHORT).show();
						traitSelected.set(1, "");
					} else
						traitSelected.add(parent.getItemAtPosition(position)
								.toString());
			} else {
				if (position != 0)
					if (traitSelected.contains(parent.getItemAtPosition(
							position).toString()))
						Toast.makeText(
								HomeActivity.this,
								parent.getItemAtPosition(position).toString()
										+ " has been selected,select another one please.",
								Toast.LENGTH_SHORT).show();
					else
						traitSelected.set(1, parent.getItemAtPosition(position)
								.toString());
			}
		}

		@Override
		public void onNothingSelected(AdapterView<?> parent) {
			// TODO Auto-generated method stub

		}

	}

	DatePickerDialog.OnDateSetListener fromDatePickerListener = new DatePickerDialog.OnDateSetListener() {

		@Override
		public void onDateSet(DatePicker view, int year, int monthOfYear,
				int dayOfMonth) {
			// TODO Auto-generated method stub

			from = new Date((year - 1900), monthOfYear, dayOfMonth);
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			fromDate.setText(sdf.format(from));

			for (int i = 0; i < observationsForDate.size(); i++) {
				if (i == observationsForDate.size())
					break;
				if (observationDao
						.findObervationById(
								observationDao.findIdByName(observationsForDate
										.get(i))).getCreateTime().before(from)) {
					observationsForDate.remove(i);
					i--;
				}

			}
			refreshObserTableForDate();

			observationsForDate = new ArrayList<String>();
			for (int i = 0; i < observations.size(); i++)
				observationsForDate.add(observations.get(i));
		}

	};

	DatePickerDialog.OnDateSetListener toDatePickerListener = new DatePickerDialog.OnDateSetListener() {

		@Override
		public void onDateSet(DatePicker view, int year, int monthOfYear,
				int dayOfMonth) {
			// TODO Auto-generated method stub

			to = new Date((year - 1900), monthOfYear, dayOfMonth);
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			toDate.setText(sdf.format(to));

			for (int i = 0; i < observationsForDate.size(); i++) {

				if (i == observationsForDate.size())
					break;
				if (observationDao
						.findObervationById(
								observationDao.findIdByName(observationsForDate
										.get(i))).getCreateTime().after(to)) {
					observationsForDate.remove(i);
					i--;
				}

			}
			refreshObserTableForDate();

			observationsForDate = new ArrayList<String>();
			for (int i = 0; i < observations.size(); i++)
				observationsForDate.add(observations.get(i));
		}

	};

	@Override
	@Deprecated
	protected Dialog onCreateDialog(int id) {
		// TODO Auto-generated method stub
		java.util.Date current = new java.util.Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		int year = Integer.parseInt(sdf.format(current).substring(0, 4));
		int month = Integer.parseInt(sdf.format(current).substring(5, 7));
		int day = Integer.parseInt(sdf.format(current).substring(8, 10));
		switch (id) {
		case 0:
			return new DatePickerDialog(this, fromDatePickerListener, 2014, 9,
					1);
		case 1:
			return new DatePickerDialog(this, toDatePickerListener, year,
					(month - 1), day);
		}

		return null;
	}

	public boolean isNetworkOnline() {
		boolean status = false;
		try {
			ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo netInfo = cm.getNetworkInfo(0);
			if (netInfo != null
					&& netInfo.getState() == NetworkInfo.State.CONNECTED) {
				status = true;
			} else {
				netInfo = cm.getNetworkInfo(1);
				if (netInfo != null
						&& netInfo.getState() == NetworkInfo.State.CONNECTED)
					status = true;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return status;

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.home, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_reset_password) {
			Intent intent = new Intent();
			intent.setClass(HomeActivity.this, ResetPasswordActivity.class);
			intent.putExtra("password", user.getPassword());
			intent.putExtra("username", user.getUserName());
			startActivity(intent);
			return true;
		} else {
			LogoutAsynTask lTask=new LogoutAsynTask();
//			String UR_STRING = "http://172.31.201.109:8888//workspace/test/Logout.php";
//			System.out.println("HomeActivity username:"+user.getUserName());
			lTask.execute( Constant.urlString + "Logout.php",user.getUserName());
			Intent intent = new Intent();
			intent.setClass(HomeActivity.this, MainActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			intent.putExtra("flag", 1);
			startActivity(intent);
			finish();
		}

		return super.onOptionsItemSelected(item);
	}
}
