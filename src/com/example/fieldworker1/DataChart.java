package com.example.fieldworker1;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Collections;

import com.example.dao.ObservationDao;
import com.example.domain.Observation;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class DataChart extends Activity {
	private ArrayList<String> traitName;
	private ArrayList<String> observationNames;
	private ArrayList<Integer> observationIDs;
	private MultipleTemperatureChart mChart;
	private ObservationDao oDao;
	private String chartType;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		oDao=new ObservationDao(DataChart.this);
		Intent intent=getIntent();
		traitName=intent.getStringArrayListExtra("traitName");
		observationNames=intent.getStringArrayListExtra("observationName");
		System.out.println("DataChart observationNames size:"+observationNames.size());
		observationIDs=intent.getIntegerArrayListExtra("observationID");
		System.out.println(observationIDs+"$$$$");
//		observationIDs=new ArrayList<Integer>();
//		for (int i = 0; i < observationNames.size(); i++) {
//			observationIDs.add(oDao.findIdByName(observationNames.get(i)));
//		}
		chartType=intent.getStringExtra("chartType");
		ArrayList<Observation> observations=new ArrayList<Observation>();
		
		for(Integer i:observationIDs)
		{
			observations.add(oDao.findObervationById(i));
		}
		Collections.sort(observations);
		for (int i=0;i<observationIDs.size();i++) {
			observationIDs.set(i, observations.get(i).getObservationID());
		}
		
		mChart=new MultipleTemperatureChart(traitName, observationIDs, chartType,DataChart.this);
		Intent intent2=new Intent();
		intent2=mChart.execute(DataChart.this);
		startActivity(intent2);
		this.finish();
	}
	public void generateChart()
	{
		
	}
	

}
