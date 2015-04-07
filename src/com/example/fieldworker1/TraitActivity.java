package com.example.fieldworker1;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.example.dao.TraitDao;
import com.example.domain.Trait;
import com.example.fieldworker1.ListViewSubClass.OnDeleteListener;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

public class TraitActivity extends ListActivity {
private static final int AddTrait=1;
	private TraitDao traitDao;
	private ListViewSubClass mListView;
	private List<HashMap<String, String>> list;
	private MyAdapter listAdapter;
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		traitDao=new TraitDao(TraitActivity.this);
		mListView=(ListViewSubClass) findViewById(android.R.id.list);
		showTraits();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		menu.add(0,AddTrait,1,R.string.addTrait);
		//menu.add(0,obserList,2,R.string.obserList);
		
		return super.onCreateOptionsMenu(menu);
	}
    
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		if (item.getItemId()==AddTrait) {
			Intent intent=new Intent();
			intent.setClass(TraitActivity.this,AddTraitActivity.class);
			TraitActivity.this.startActivity(intent);
			finish();
			//showTraitList();
		}
		
		return super.onOptionsItemSelected(item);
	}

	private void showTraits() {
		System.out.println("start showTraits");
		// TODO Auto-generated method stub
		list = new ArrayList<HashMap<String, String>>();
		List<Trait> traits=traitDao.findAll();
		for (java.util.Iterator<Trait> iterator = traits.iterator(); iterator.hasNext();)
		{
			Trait t = (Trait) iterator.next();
			System.out.println(t.toString());
			HashMap<String, String> map = new HashMap<String, String>();
			map.put("trait_name", t.getTraitName());
	//		map.put("traitList_id", t.getTraitListID()+"");
			list.add(map);
		}
		listAdapter=new MyAdapter(this, list,
				             R.layout.trait_item, new String[]{"trait_name"}, 
				             new int[]{R.id.trait_name});
		
		//setListAdapter(listAdapter);
		mListView.setAdapter(listAdapter);
		mListView.setOnDeleteListener(new OnDeleteListener(){

			@Override
			public void onDelete(int index) {
				// TODO Auto-generated method stub
		
			
			 traitDao.delete(list.get(index).get("trait_name"));
			 System.out.println(list.get(index).get("trait_name"));
			 list.remove(index);
			 listAdapter.notifyDataSetChanged();
			}
			
		});
		
	}

	protected void onListItemClick(ListView l, View v, int position, long id) {
		// TODO Auto-generated method stub
		super.onListItemClick(l, v, position, id);
		System.out.println("click"+position);
		Intent intent=new Intent();
		intent.putExtra("traitName", list.get(position).get("trait_name"));
		intent.setClass(TraitActivity.this, ShowTrait.class);
		TraitActivity.this.startActivity(intent);
		//finish();
		
	}
	
	
}
