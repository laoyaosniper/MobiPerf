
/* Copyright 2012 Google Inc.
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*     http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/


//TODO: remove duplicate/unnecessary static variables
package com.mobiperf;

/**
* The system defaults.
*/
public interface MobiperfConfig {
  public static final String CLIENT_KEY = "Mobiperf";
  // Hongyi: add default context interval
  public static final int DEFAULT_CONTEXT_INTERVAL = 1;
  public static final int DEFAULT_USER_MEASUREMENT_COUNT = 1;
  // Default interval in seconds between user measurements of a given measurement type
  public static final double DEFAULT_USER_MEASUREMENT_INTERVAL_SEC = 5;

  public static final boolean DEFAULT_START_ON_BOOT = false;

  /** Constants used in MeasurementMonitorActivity.java */
  public static final int MAX_LIST_ITEMS = 50;

  public static final String PREF_KEY_SYSTEM_RESULTS = "PREF_KEY_SYSTEM_RESULTS";
  public static final String PREF_KEY_USER_RESULTS = "PREF_KEY_USER_RESULTS";
  public static final String PREF_KEY_COMPLETED_MEASUREMENTS = "PREF_KEY_COMPLETED_MEASUREMENTS";
  public static final String PREF_KEY_FAILED_MEASUREMENTS = "PREF_KEY_FAILED_MEASUREMENTS";
  public static final String PREF_KEY_CONSENTED = "PREF_KEY_CONSENTED";
  public static final String PREF_KEY_SELECTED_ACCOUNT = "PREF_KEY_SELECTED_ACCOUNT";
  public static final String PREF_KEY_USER_TASKS = "PREF_KEY_USER_TASKS";
  public static final String PREF_KEY_USER_PAUSED_TASKS = "PREF_KEY_USER_PAUSED_TASKS";


  /** Constants for the splash screen */
  public static final long SPLASH_SCREEN_DURATION_MSEC = 1500;
}
