package com.philips.research.bombar.core.domain.licenses;

public class LicenseObligation {

  private final String key;
  private final String message;

  public LicenseObligation(String key, String message) {
	this.key = key;
	this.message = message;
  }

  public String getKey() {
	return key;
  }

  public String getMessage() {
	return message;
  }

  @Override
  public String toString() {
	return "License Obligations " + key + " " + message;
  }
}
