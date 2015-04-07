/**
 * Copyright (C) 2009 - 2013 SC 4ViewSoft SRL
 *  
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *  
 *      http://www.apache.org/licenses/LICENSE-2.0
 *  
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.fieldworker1;

import org.achartengine.chart.BarChart.Type;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import org.achartengine.ChartFactory;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import com.example.dao.ObservationDao;
import com.example.domain.Observation;
import com.example.service.DataChartService;
import com.example.validator.MyApplication;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint.Align;

/**
 * Multiple temperature demo chart.
 */
public class MultipleTemperatureChart extends AbstractDemoChart {
  /**
   * Returns the chart name.
   * 
   * @return the chart name
   */
	private ArrayList<String> traits;
	private ArrayList<Integer> observations;
	private DataChartService dataChartService;
	private String chartType;
	public MultipleTemperatureChart(ArrayList<String> traits,ArrayList<Integer> observations,String chartType,Context context)
	{
		this.traits=traits;
		
		this.observations=observations;
		this.chartType=chartType;
		dataChartService=new DataChartService(context, this.traits, this.observations);
	}
    public String getName() {
        return "Temperature and sunshine";
    }

  /**
   * Returns the chart description.
   * 
   * @return the chart description
   */
  public String getDesc() {
    return "The average temperature and hours of sunshine in Crete (line chart with multiple Y scales and axis)";
  }

  /**
   * Executes the chart demo.
   * 
   * @param context the context
   * @return the built intent
 * @throws ParseException 
   */
  public Intent execute(Context context)  {
    String[] titles = new String[traits.size()];
    for (int i = 0; i < traits.size(); i++) {
		titles[i]=traits.get(i);
	}
   
    List<Date[]> x = new ArrayList<Date[]>();
    
    try {
		x=dataChartService.getX();
		
	} catch (ParseException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
    //for (int i = 0; i < titles.length; i++) {
   //   x.add(new double[] { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12 });
   // }
    List<double[]> values = new ArrayList<double[]>();
    //values=dataChartService.getValues();
    //values.add(new double[] { 12.3, 12.5 });
    int[] colors = new int[] { Color.BLUE, Color.YELLOW };
    PointStyle[] styles = new PointStyle[] { PointStyle.POINT, PointStyle.POINT };
  
    
   
    values=dataChartService.getValues();
    List<double[]> yValues=new ArrayList<double[]>();
    yValues.add(values.get(0));
    
    List<double[]> xAxis=new ArrayList<double[]>();
    double[] lables=new double[x.get(0).length];
    for (int i = 0; i < lables.length; i++) {
		lables[i]=i;
	}
//    for (int i = 0; i < x.size(); i++) {
//    	System.out.println("x"+i+": "+x.get(0)[i]);
//    	xAxis.add(lables);
//	}
    xAxis.add(lables);
    
    for (int i = 0; i <values.get(1).length; i++) {
    	System.out.println("values"+i+":"+values.get(1)[i]);
	}
    
    //XYMultipleSeriesDataset dataset = buildDataset(titles, xAxis, values);
  
   List<Double> getMax = new ArrayList<Double>();  
    for (int i = 0; i < values.size(); i++) {  
        for (double d : values.get(i)) {  
            getMax.add(d);  
        }  

    }  
    System.out.println("!!!!"+getMax);
    // 柱形图Y轴高度，在最大值基础上加2000,不会Y轴满屏  
    double ymaxValue = Collections.max(getMax) + 5; 
    XYMultipleSeriesDataset dataset = buildDataset(new String[] { traits.get(0) }, xAxis, yValues);
    yValues.clear();
    yValues.add(values.get(1));
    
    addXYSeries(dataset, new String[] { traits.get(1) }, xAxis, yValues, 1);
    
    XYMultipleSeriesRenderer render=getDemoRenderer(x,"", "Create Time", traits, 0.3, 4.3, 0, ymaxValue, Color.LTGRAY, Color.LTGRAY);
    Intent intent=new Intent();
    if (chartType.equals("Line")) {
    	 intent = ChartFactory.getCubicLineChartIntent(context, dataset, render,0);
	}
    else {
    	intent = ChartFactory.getBarChartIntent(context, dataset, render,Type.DEFAULT);       	
	}   
    return intent;
  }

public ArrayList<String> getTraits() {
	return traits;
}

public void setTraits(ArrayList<String> traits) {
	this.traits = traits;
}

public ArrayList<Integer> getObservations() {
	return observations;
}

public void setObservations(ArrayList<Integer> observations) {
	this.observations = observations;
}
private XYMultipleSeriesRenderer getDemoRenderer(List<Date[]> x,String title, String xTitle,
	      ArrayList<String> traits, double xMin, double xMax, double yMin, double yMax, int axesColor,
	      int labelsColor) {
    XYMultipleSeriesRenderer renderer = new XYMultipleSeriesRenderer(2);
    renderer.setAxisTitleTextSize(28);
    renderer.setChartTitleTextSize(36);
    renderer.setLabelsTextSize(20);
    renderer.setLegendTextSize(23);
    renderer.setPointSize(5f);
    renderer.setMargins(new int[] {20, 30, 15, 15});
    
    XYSeriesRenderer r = new XYSeriesRenderer();
	
	r.setColor(Color.BLUE);
	r.setPointStyle(PointStyle.SQUARE);
  
    r.setFillPoints(true);
    r.setLineWidth(2.5f);
    renderer.addSeriesRenderer(r);
    if (traits.size()==2) {
       XYSeriesRenderer r1 = new XYSeriesRenderer();
    	
    	r1.setColor(Color.YELLOW);
    	r1.setPointStyle(PointStyle.CIRCLE);
     
        r1.setFillPoints(true);
        r1.setLineWidth(2.5f);
        renderer.addSeriesRenderer(r1);
	}
   
    renderer.setXLabels(0);
    DateFormat df = new SimpleDateFormat("MM/dd/yyyy");
    for (int i = 0; i < x.get(0).length; i++) {
    	renderer.addXTextLabel(i, df.format(x.get(0)[i]));
	}
   
    renderer.setChartTitle(title);
    renderer.setXTitle(xTitle);
    renderer.setYTitle(traits.get(0));
    renderer.setXAxisMin(xMin);
    renderer.setXAxisMin(xMin, 1);
    renderer.setXAxisMax(xMax);
    renderer.setXAxisMax(xMax, 1);
    renderer.setYAxisMin(yMin);
    renderer.setYAxisMin(yMin, 1);
    renderer.setYAxisMax(yMax);
    renderer.setYAxisMax(yMax, 1);
    renderer.setAxesColor(axesColor);
    renderer.setLabelsColor(labelsColor);
    
    renderer.setYTitle(traits.get(1), 1); 
    renderer.setZoomButtonsVisible(true);
    renderer.setYLabelsPadding(-25);  
    renderer.setYAxisAlign(Align.RIGHT, 1);  
    renderer.setYLabelsAlign(Align.LEFT, 1);
    renderer.setShowGrid(true);  
    renderer.setZoomButtonsVisible(true); 
    return renderer;
  }

}
