package com.example.phpServer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import com.example.dao.AddLogDao;
import com.example.dao.DeleteLogDao;
import com.example.dao.TraitDao;
import com.example.dao.TraitListDao;
import com.example.domain.AddLog;
import com.example.domain.DeleteLog;
import com.example.domain.TraitList;
import com.example.domain.TraitListContent;
import com.example.fieldworker1.ListViewSubClass;
import com.example.fieldworker1.MyAdapter;
import com.example.fieldworker1.R;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import android.content.Context;
import android.os.AsyncTask;
import android.telephony.TelephonyManager;
import android.widget.Toast;

public class TraitListPhpService {
	private Context context;
	private String username;
	private static final String UR_STRING = "http://172.31.201.109:8888";

	public TraitListPhpService(Context context, String username) {
		super();
		this.context = context;
		this.username = username;
	}

	public void addTraitList(TraitList traitList, List<String> traits) {
		// insert into TraitList
		// insert traits length entries into TraitListContent
		String url = "http://localhost:8888/server.php/";
		new AddTraitListAsyncTask(traitList, traits).execute(url);
	}

	public void synTraitList(List<AddLog> addLogs, List<AddLog> addLogs2,
			List<DeleteLog> deleteLogs) {
		final AddLogDao addLogDao = new AddLogDao(context);
		final DeleteLogDao deleteLogDao = new DeleteLogDao(context);
		TraitListDao traitListDao = new TraitListDao(context);

		ArrayList<TraitList> traitLists = new ArrayList<TraitList>();
		ArrayList<TraitListContent> traits = new ArrayList<TraitListContent>();
		Integer[] deletedTraitList = new Integer[deleteLogs.size()];
		for (int i = 0; i < addLogs.size(); i++) {
			Integer traitListID = addLogs.get(i).getFirstID();

			traitLists.add(traitListDao.findById(traitListID));
		}
		for (int i = 0; i < addLogs2.size(); i++) {
			Integer traitListID = addLogs2.get(i).getFirstID();
			Integer traitID = addLogs2.get(i).getSecondID();
			traits.add(new TraitListContent(traitListID, traitID));
		}
		for (int i = 0; i < deleteLogs.size(); i++) {
			deletedTraitList[i] = deleteLogs.get(i).getFirstID();
		}
		AsyncHttpClient client = new AsyncHttpClient();
		RequestParams params = new RequestParams();

		Gson gson = new GsonBuilder().create();
		String strJSON = gson.toJson(traitLists);
		String strJSON2 = gson.toJson(traits);
		String strJSON3 = gson.toJson(deletedTraitList);
		params.put("TraitLists", strJSON);
		params.put("traitContents", strJSON2);
		params.put("deletedTraitList", strJSON3);
		params.put("deviceId", getDeviceID());
		System.out.println(getDeviceID());
		System.out.println(strJSON3);
		client.post(UR_STRING + "/workspace/test/SynTraitList.php", params,
				new AsyncHttpResponseHandler() {
					@Override
					public void onSuccess(String response) {
						System.out.println(response);
						addLogDao.deleteByTableName("TraitList");
						addLogDao.deleteByTableName("TraitListContent");
						deleteLogDao.deleteByTableName("TraitList");
						Toast.makeText(context, "TraitList Sync completed!",
								Toast.LENGTH_LONG).show();

					}

					public void onFailure(int statusCode, Throwable error,
							String content) {
						// TODO Auto-generated method stub
						/*
						 * prgDialog.hide(); if (statusCode == 404) {
						 * Toast.makeText(context,
						 * "Requested resource not found",
						 * Toast.LENGTH_LONG).show(); } else if (statusCode ==
						 * 500) { Toast.makeText(context,
						 * "Something went wrong at server end",
						 * Toast.LENGTH_LONG).show(); } else { Toast.makeText(
						 * context,
						 * "Unexpected Error occcured! [Most common Error: Device might not be connected to Internet]"
						 * , Toast.LENGTH_LONG).show(); }
						 */
					}
				});
	}

	public void findAll(ListViewSubClass mListView, Context context,
			List<HashMap<String, String>> list) throws InterruptedException {
		String url = UR_STRING + "/server.php/";
		FindAllAsyncTask findAllAsyncTask = new FindAllAsyncTask(mListView,
				context, list);
		findAllAsyncTask.execute(url);
	}

	class AddTraitListAsyncTask extends AsyncTask<String, Integer, String> {

		private TraitList traitList;
		private List<String> traits;

		public AddTraitListAsyncTask(TraitList traitList, List<String> traits) {
			super();
			this.traitList = traitList;
			this.traits = traits;
		}

		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub

			HttpPost httpRequest = new HttpPost(UR_STRING
					+ "/TraitListService.php");
			TraitDao traitDao = new TraitDao(context);
			ArrayList<TraitListContent> contents = new ArrayList<TraitListContent>();
			List<NameValuePair> param = new ArrayList<NameValuePair>();
			param.add(new BasicNameValuePair("traitListID", traitList
					.getTraitListID() + ""));
			param.add(new BasicNameValuePair("traitListName", traitList
					.getTraitListName()));
			param.add(new BasicNameValuePair("username", traitList
					.getUsername()));
			param.add(new BasicNameValuePair("deviceId", getDeviceID()));
			for (String s : traits) {
				Integer traitID = traitDao.findIdbyName(s);
				contents.add(new TraitListContent(traitList.getTraitListID(),
						traitID));
			}

			Gson gson = new GsonBuilder().create();
			String strJSON = gson.toJson(contents);

			param.add(new BasicNameValuePair("Traits", strJSON));
			InputStream is = null;

			try {

				HttpEntity httpEntity = new UrlEncodedFormEntity(param, "utf-8");
				httpRequest.setEntity(httpEntity);

				HttpClient httpClient = new DefaultHttpClient();
				HttpResponse httpResponse = httpClient.execute(httpRequest);

				if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
					HttpEntity entity = httpResponse.getEntity();

					// is=entity.getContent();
					System.out.println("***" + EntityUtils.toString(entity));

				} else {
					// tv.setText("request error");
				}
			} catch (UnsupportedEncodingException e) {
				// TODO: handle exception
				e.printStackTrace();
			} catch (ClientProtocolException e) {
				// TODO: handle exception
				e.printStackTrace();
			} catch (IOException e) {
				// TODO: handle exception
				e.printStackTrace();
			}

			return "";
		}

	}

	class FindAllAsyncTask extends AsyncTask<String, Integer, String> {
		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			List<TraitList> traitLists = new ArrayList<TraitList>();
			try {

				JSONArray jArray = new JSONArray(result);

				if (jArray.length() > 0) {
					for (int i = 0; i < jArray.length(); i++) {
						JSONObject json_data = jArray.getJSONObject(i);

						Integer traitListID = json_data.getInt("traitListID");
						String traitListName = json_data
								.getString("traitListName");
						String username = json_data.getString("username");
						traitLists.add(new TraitList(traitListID,
								traitListName, username));
					}

				} else {
					result = "Can't find Your name!";
				}
			} catch (Exception e) {
				// TODO: handle exception
				// Log.e("log_tag", "Error parsing data "+e.toString());
			}
			for (java.util.Iterator<TraitList> iterator = traitLists.iterator(); iterator
					.hasNext();) {
				TraitList t = (TraitList) iterator.next();

				HashMap<String, String> map = new HashMap<String, String>();
				map.put("traitList_name", t.getTraitListName());
				// map.put("traitList_id", t.getTraitListID()+"");
				list.add(map);
			}

		}

		private ListViewSubClass mListView;
		private Context context;
		private List<HashMap<String, String>> list;

		public FindAllAsyncTask(ListViewSubClass mListView, Context context,
				List<HashMap<String, String>> list) {
			this.mListView = mListView;
			this.context = context;
			this.list = list;

		}

		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub

			HttpPost httpRequest = new HttpPost(UR_STRING
					+ "/FindAllTraitLists.php");
			List<NameValuePair> param = new ArrayList<NameValuePair>();
			param.add(new BasicNameValuePair("username", username));	
			InputStream is = null;
			String result = "";

			try {
				HttpEntity httpEntity = new UrlEncodedFormEntity(param, "utf-8");
				httpRequest.setEntity(httpEntity);
				HttpClient httpClient = new DefaultHttpClient();
				HttpResponse httpResponse = httpClient.execute(httpRequest);

				if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
					HttpEntity entity = httpResponse.getEntity();

					is = entity.getContent();
					// System.out.println("***"+EntityUtils.toString(entity));

				} else {
					// tv.setText("request error");
				}
			} catch (UnsupportedEncodingException e) {
				// TODO: handle exception
				e.printStackTrace();
			} catch (ClientProtocolException e) {
				// TODO: handle exception
				e.printStackTrace();
			} catch (IOException e) {
				// TODO: handle exception
				e.printStackTrace();
			}

			try {
				BufferedReader br = new BufferedReader(new InputStreamReader(
						is, "iso-8859-1"), 8);
				StringBuilder sb = new StringBuilder();
				String line = null;
				while ((line = br.readLine()) != null) {
					sb.append(line + "\n");
				}
				is.close();
				result = sb.toString();
				System.out.println(result);
			} catch (Exception e) {
				// TODO: handle exception
			}

			return result;
		}

	}

	public void deleteTraitList(String traitListName) {

		new DeleteTraitListAsyncTask().execute(traitListName);
	}

	class DeleteTraitListAsyncTask extends AsyncTask<String, Integer, String> {

		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub
			String traitListName = params[0];
			// System.out.println("^^^"+traitListName);
			HttpPost httpRequest = new HttpPost(UR_STRING
					+ "/workspace/test/DeleteTraitList.php");
			List<NameValuePair> param = new ArrayList<NameValuePair>();
			param.add(new BasicNameValuePair("traitListName", traitListName));
			try {
				HttpEntity httpEntity = new UrlEncodedFormEntity(param, "utf-8");
				httpRequest.setEntity(httpEntity);
				HttpClient httpClient = new DefaultHttpClient();
				HttpResponse httpResponse = httpClient.execute(httpRequest);

				if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
					HttpEntity entity = httpResponse.getEntity();
					System.out.println("***" + EntityUtils.toString(entity));
				} else {
					System.out.println("request error");
				}
			} catch (UnsupportedEncodingException e) {
				// TODO: handle exception
				e.printStackTrace();
			} catch (ClientProtocolException e) {
				// TODO: handle exception
				e.printStackTrace();
			} catch (IOException e) {
				// TODO: handle exception
				e.printStackTrace();
			}
			return null;
		}

	}

	public String getDeviceID() {
		TelephonyManager tm = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
		final String tmDevice, tmSerial, androidId;
		tmDevice = "" + tm.getDeviceId();
		tmSerial = "" + tm.getSimSerialNumber();
		androidId = ""
				+ android.provider.Settings.Secure.getString(
						context.getContentResolver(),
						android.provider.Settings.Secure.ANDROID_ID);

		UUID deviceUuid = new UUID(androidId.hashCode(),
				((long) tmDevice.hashCode() << 32) | tmSerial.hashCode());
		return (deviceUuid.toString());

	}
}
