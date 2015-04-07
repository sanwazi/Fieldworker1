package com.example.domain;

import java.util.UUID;

public class Trait {

	private int traitID;
	public Trait(int traitID, String traitName, String widgetName, String unit,
			int accessible) {
		super();
		this.traitID = traitID;
		this.traitName = traitName;
		this.widgetName = widgetName;
		this.unit = unit;
		this.accessible = accessible;
	}

	private String traitName;
	private String widgetName;
	private String unit;
	private int accessible;

	public int getAccessible() {
		return accessible;
	}

	public void setAccessible(int accessible) {
		this.accessible = accessible;
	}

	public Trait(int traitID, String tn, String wn, String unit) {
		this.traitID = traitID;
		traitName = tn;
		widgetName = wn;
		this.unit = unit;
	}

	public Trait(String traitName, String widgetName, String unit) {
		super();
		Integer traitID = UUID.randomUUID().hashCode();
		this.traitID = traitID;
		this.traitName = traitName;
		this.widgetName = widgetName;
		this.unit = unit;
	}

	public Trait() {
		super();
	}

	public int getTraitID() {
		return traitID;
	}

	public void setTraitID(int traitID) {
		this.traitID = traitID;
	}

	public String getTraitName() {
		return traitName;
	}

	public void setTraitName(String traitName) {
		this.traitName = traitName;
	}

	public String getWidgetName() {
		return widgetName;
	}

	public void setWidgetName(String widgetName) {
		this.widgetName = widgetName;
	}

	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}

}
