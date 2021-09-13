package com.ecomindo.onboarding.springjpawebf.data.jpa.dto;

public class OptionsDTO {
	private Object label;
	private Object value;

	public OptionsDTO() {

	}

	public OptionsDTO(Object label, Object value) {
		this.label = label;
		this.value = value;

	}

	public Object getLabel() {
		return label;
	}

	public void setLabel(Object label) {
		this.label = label;
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}

}
