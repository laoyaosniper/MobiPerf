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
package com.mobiperf;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ListView;
import android.widget.ToggleButton;

import java.util.List;

import com.mobilyzer.api.API;
import com.mobiperf.R;
import com.mobiperf.util.Logger;

/**
 * The activity that provides a console and progress bar of the ongoing measurement
 */
public class ResultsConsoleActivity extends Activity {
  
  public static final String TAB_TAG = "MY_MEASUREMENTS";
  
  private ListView consoleView;
  private ArrayAdapter<String> results;
  BroadcastReceiver receiver;
  ToggleButton showUserResultButton;
  ToggleButton showSystemResultButton;
  private Console console;
  boolean userResultsActive = false;
  
  private API api;
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    Logger.d("ResultsConsoleActivity.onCreate called");
    super.onCreate(savedInstanceState);
    setContentView(R.layout.results);
    
    this.consoleView = (ListView) this.findViewById(R.id.resultConsole);
    this.results = new ArrayAdapter<String>(getApplicationContext(), R.layout.list_item);
    this.consoleView.setAdapter(this.results);
    
    showUserResultButton = (ToggleButton) findViewById(R.id.showUserResults);
    showSystemResultButton = (ToggleButton) findViewById(R.id.showSystemResults);
    showUserResultButton.setChecked(true);
    showSystemResultButton.setChecked(false);
    userResultsActive = true;
    
    // We enforce a either-or behavior between the two ToggleButtons
    OnCheckedChangeListener buttonClickListener = new OnCheckedChangeListener() {
      @Override
      public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        Logger.d("onCheckedChanged");
        switchBetweenResults(buttonView == showUserResultButton ? isChecked : !isChecked);
      }
    };
    showUserResultButton.setOnCheckedChangeListener(buttonClickListener);
    showSystemResultButton.setOnCheckedChangeListener(buttonClickListener);
    
    // Hongyi: get API singleton object
    this.api = API.getAPI(this, MobiperfConfig.CLIENT_KEY);

    // Hongyi: get console singleton
    this.console = ((SpeedometerApp)this.getParent()).getConsole();
    
    IntentFilter filter = new IntentFilter();
    filter.addAction(MobiperfIntent.SCHEDULER_CONNECTED_ACTION);
    filter.addAction(api.userResultAction);
    filter.addAction(API.SERVER_RESULT_ACTION);
    this.receiver = new BroadcastReceiver() {
      @Override
      // All onXyz() callbacks are single threaded
      public void onReceive(Context context, Intent intent) {
        if ( intent.getAction().equals(api.userResultAction) ) {
          Logger.d("receive user results");
          switchBetweenResults(true);
          console.updateStatus(null);
          console.persistState();
        }
        else if ( intent.getAction().equals(API.SERVER_RESULT_ACTION) ) {
          getConsoleContentFromScheduler();
          console.updateStatus(null);
          console.persistState();
        }
        else if (intent.getAction().equals(MobiperfIntent.SCHEDULER_CONNECTED_ACTION)) {
          Logger.d("scheduler connected");
          switchBetweenResults(userResultsActive);
        }
      }
    };
    this.registerReceiver(this.receiver, filter);

    getConsoleContentFromScheduler();
  }
  
  /**
   * Change the underlying adapter for the ListView.
   * 
   * @param showUserResults If true, show user results; otherwise, show system results.
   */
  private synchronized void switchBetweenResults(boolean showUserResults) {
    userResultsActive = showUserResults;
    getConsoleContentFromScheduler();
    showUserResultButton.setChecked(showUserResults);
    showSystemResultButton.setChecked(!showUserResults);
    Logger.d("switchBetweenResults: showing " + results.getCount() + " " +
             (showUserResults ? "user" : "system") + " results");
  }
  
  @Override
  protected void onDestroy() {
    Logger.d("ResultsConsoleActivity.onDestroy called");
    super.onDestroy();
    this.unregisterReceiver(this.receiver);
  }

  private synchronized void getConsoleContentFromScheduler() {
    Logger.d("ResultsConsoleActivity.getConsoleContentFromScheduler called");
    // Scheduler may have not had time to start yet. When it does, the intent above will call this
    // again.
    if (console != null) {
      Logger.d("Updating measurement results from thread " + Thread.currentThread().getName());
      results.clear();
      final List<String> scheduler_results =
          (userResultsActive ? console.getUserResults() : console.getSystemResults());
      for (String result : scheduler_results) {
        results.add(result);
      }
      runOnUiThread(new Runnable() {
        public void run() { results.notifyDataSetChanged(); }
      });
    }
    else {
      Logger.e("Console is not instantialized!");
    }
  }
}
