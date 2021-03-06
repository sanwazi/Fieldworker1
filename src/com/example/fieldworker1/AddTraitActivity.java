package com.example.fieldworker1;

import com.example.dao.PredefineValueDao;
import com.example.dao.TraitDao;
import com.example.domain.PredefineValue;
import com.example.domain.Trait;
import com.mobsandgeeks.saripaar.Rule;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.Validator.ValidationListener;
import com.mobsandgeeks.saripaar.annotation.Required;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TableLayout;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TableRow;

public class AddTraitActivity extends Activity {
	private String widgetName;
	private Validator validator;
	private TraitDao traitDao;
	private PredefineValueDao predefineValueDao;
	private RadioGroup group;
	private TableLayout tableLayout;
	private Button addButton;
	private Button deleteButton;
	private Button submitButton;
	private int editTextCount;
	@Required(order = 1)
	private EditText traitNamEditText;
	private EditText unitEditText;
	// @NotEmpty(messageId=R.string.minValueNotEmpty, order=2)
	// @Custom(value=MyVeryOwnValidator.class,messageId=R.string.notANumber,order=3)
	private EditText editText1;
	// @MinNumberValue(value="0",messageId=R.string.minNumberValue,order=4)
	// @NotEmpty(messageId=R.string.maxValueNotEmpty, order=4)
	// @Custom(value=MyVeryOwnValidator.class,messageId=R.string.notANumber,order=4)
	private EditText editText2;
	private EditText preValue1;
	private EditText preValue2;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		widgetName = "Spinner";
		super.onCreate(savedInstanceState);
		setContentView(R.layout.add_trait);
		validator = new Validator(this);
		validator.setValidationListener(new InputValidatorListener());
		Rule<EditText> uniqueTraitName = new Rule<EditText>(
				"Trait name already exists") {

			@Override
			public boolean isValid(EditText arg0) {
				// TODO Auto-generated method stub
				return traitDao.checkTraitName(traitNamEditText.getText()
						.toString());
			}

		};
		/*
		 * Rule<EditText> minMax=new
		 * Rule<EditText>("Maximum value should be greater than minimum value"){
		 * 
		 * @Override public boolean isValid(EditText arg0) { // TODO
		 * Auto-generated method stub return
		 * Integer.parseInt(editText2.getText()
		 * .toString())>Integer.parseInt(editText1.getText().toString()); } };
		 * 
		 * validator.put(editText2,minMax);
		 */
		validator.put(traitNamEditText, uniqueTraitName);
		traitDao = new TraitDao(AddTraitActivity.this);
		predefineValueDao = new PredefineValueDao(AddTraitActivity.this);
		traitNamEditText = (EditText) findViewById(R.id.traitName);
		unitEditText = (EditText) findViewById(R.id.unit);
		editText1 = (EditText) findViewById(R.id.min);
		editText2 = (EditText) findViewById(R.id.max);
		preValue1 = (EditText) findViewById(R.id.traitName1);
		preValue2 = (EditText) findViewById(R.id.traitName3);
		group = (RadioGroup) findViewById(R.id.radioGroup);
		tableLayout = (TableLayout) findViewById(R.id.table1);
		TableRow row1 = (TableRow) tableLayout.getChildAt(3);
		row1.setVisibility(8);
		TableRow row2 = (TableRow) tableLayout.getChildAt(4);
		row2.setVisibility(8);
		editTextCount = 1;
		addButton = (Button) findViewById(R.id.add);
		addButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				TableRow tableRow = new TableRow(AddTraitActivity.this);
				tableRow.setId(tableLayout.getChildCount());
				EditText editText = new EditText(AddTraitActivity.this);
				TableRow.LayoutParams p = new TableRow.LayoutParams(1);
				editText.setLayoutParams(p);
				tableRow.addView(editText);
				tableLayout.addView(tableRow);
				editTextCount++;
			}
		});
		deleteButton = (Button) findViewById(R.id.deleteButton);
		deleteButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub

				TableRow tableRow = (TableRow) findViewById(tableLayout
						.getChildCount() - 1);
				// System.out.println(tableLayout.getChildCount());
				tableLayout.removeView(tableRow);
			}
		});
		group.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				// TODO Auto-generated method stub
				int radioButtonId = group.getCheckedRadioButtonId();
				RadioButton rb = (RadioButton) AddTraitActivity.this
						.findViewById(radioButtonId);
				widgetName = rb.getText().toString();
				if (rb.getText().equals("EditText")) {
					for (int i = 3; i < tableLayout.getChildCount(); i++) {
						TableRow tableRow = (TableRow) tableLayout
								.getChildAt(i);
						tableRow.setVisibility(8);
					}
				} else if (rb.getText().equals("Slider")) {
					TableRow row1 = (TableRow) tableLayout.getChildAt(3);
					row1.setVisibility(0);
					TableRow row2 = (TableRow) tableLayout.getChildAt(4);
					row2.setVisibility(0);
					for (int i = 5; i < tableLayout.getChildCount(); i++) {
						TableRow tableRow = (TableRow) tableLayout
								.getChildAt(i);
						tableRow.setVisibility(8);
					}
				} else {
					TableRow row1 = (TableRow) tableLayout.getChildAt(3);
					row1.setVisibility(8);
					TableRow row2 = (TableRow) tableLayout.getChildAt(4);
					row2.setVisibility(8);
					for (int i = 5; i < tableLayout.getChildCount(); i++) {
						TableRow tableRow = (TableRow) tableLayout
								.getChildAt(i);
						tableRow.setVisibility(0);
					}
				}
			}
		});
		submitButton = (Button) findViewById(R.id.submitButton);
		submitButton.setOnClickListener(new submitButtonListener());
	}

	private class submitButtonListener implements OnClickListener {
		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub

			if (widgetName.equals("Slider")) {
				validateSlider();
			}
			if (widgetName.equals("Spinner") || widgetName.equals("CheckBox")
					|| widgetName.equals("RadioButton")) {
				validateOther();
			}
			validator.validate();

		}

	}

	class InputValidatorListener implements ValidationListener {
		@Override
		public void onValidationFailed(View failedView, Rule<?> failedRule) {
			// TODO Auto-generated method stub
			String message = failedRule.getFailureMessage();

			if (failedView instanceof EditText) {
				failedView.requestFocus();
				((EditText) failedView).setError(message);
			} else {
				Crouton.makeText(AddTraitActivity.this, message, Style.ALERT)
						.show();
			}
		}

		@Override
		public void onValidationSucceeded() {
			// TODO Auto-generated method stub
			String traitName = traitNamEditText.getText().toString();
			String unit = unitEditText.getText().toString();
			RadioButton radioButton = (RadioButton) findViewById(group
					.getCheckedRadioButtonId());
			widgetName = radioButton.getText().toString();
			if (widgetName.equals("DropDownMenu")) {
				widgetName = "Spinner";
			}
			Trait t = null;
			if (!unit.equals("")) {
				t = new Trait(traitName, widgetName, unit);
			} else {
				t = new Trait(traitName, widgetName, null);
			}
			traitDao.insert(t);
			if (widgetName.equals("CheckBox") || widgetName.equals("Spinner")
					|| widgetName.equals("RadioButton")) {
				TableRow tableRow2 = (TableRow) tableLayout.getChildAt(5);
				EditText editText1 = (EditText) tableRow2.getVirtualChildAt(1);
				String preVal1 = editText1.getText().toString();
				PredefineValue predefineValue = new PredefineValue(
						t.getTraitID(), preVal1);
				predefineValueDao.insert(predefineValue);
				for (int i = 6; i < tableLayout.getChildCount(); i++) {
					TableRow tableRow3 = (TableRow) tableLayout.getChildAt(i);
					EditText eText = (EditText) tableRow3.getVirtualChildAt(1);
					String preVal2 = eText.getText().toString();
					PredefineValue predefineValue2 = new PredefineValue(
							t.getTraitID(), preVal2);
					predefineValueDao.insert(predefineValue2);
				}
			}
			if (widgetName.equals("Slider")) {
				TableRow tableRow1 = (TableRow) tableLayout.getChildAt(3);
				String preVal1 = editText1.getText().toString();
				TableRow tableRow2 = (TableRow) tableLayout.getChildAt(4);
				String preVal2 = editText2.getText().toString();
				predefineValueDao.insert(new PredefineValue(t.getTraitID(),
						preVal1));
				predefineValueDao.insert(new PredefineValue(t.getTraitID(),
						preVal2));
			}
			Intent intent = new Intent();
			intent.setClass(AddTraitActivity.this, TraitActivity.class);
			AddTraitActivity.this.startActivity(intent);
			finish();
		}

	}

	public void validateOther() {
		Rule<EditText> notEmpty3 = new Rule<EditText>(
				"Please input the predefined value") {

			@Override
			public boolean isValid(EditText arg0) {
				// TODO Auto-generated method stub
				if (preValue1.getText().toString().equals(""))
					return false;
				else
					return true;
			}
		};
		validator.put(preValue1, notEmpty3);
		Rule<EditText> notEmpty4 = new Rule<EditText>(
				"Please input the predefined value") {
			@Override
			public boolean isValid(EditText arg0) {
				// TODO Auto-generated method stub
				if (preValue2.getText().toString().equals(""))
					return false;
				else
					return true;
			}
		};
		validator.put(preValue2, notEmpty4);
	}

	public void validateSlider() {
		Rule<EditText> notEmpty = new Rule<EditText>(
				"Minimum value can not be empty") {

			@Override
			public boolean isValid(EditText arg0) {
				// TODO Auto-generated method stub
				if (editText1.getText().toString().equals(""))
					return false;
				else
					return true;
			}
		};
		validator.put(editText1, notEmpty);
		Rule<EditText> notEmpty1 = new Rule<EditText>(
				"Maximum value can not be empty") {

			@Override
			public boolean isValid(EditText arg0) {
				// TODO Auto-generated method stub
				if (editText2.getText().toString().equals(""))
					return false;
				else
					return true;
			}
		};
		validator.put(editText2, notEmpty1);
		if ((!editText1.getText().toString().equals(""))
				&& (!editText2.getText().toString().equals(""))) {
			Rule<EditText> minMax = new Rule<EditText>(
					"Maximum value should be greater than minimum value") {
				@Override
				public boolean isValid(EditText arg0) {
					// TODO Auto-generated method stub
					return Integer.parseInt(editText2.getText().toString()) > Integer
							.parseInt(editText1.getText().toString());
				}
			};
			validator.put(editText2, minMax);
		}
	}

}
