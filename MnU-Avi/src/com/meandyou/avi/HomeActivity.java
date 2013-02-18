package com.meandyou.avi;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class HomeActivity extends Activity {
	Button advisory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        final Intent myIntent = new Intent();
        advisory = (Button) findViewById(R.id.advisory);
        advisory.setOnClickListener(new OnClickListener(){
			public void onClick(View v){
				myIntent.setClass(HomeActivity.this, Advisory.class);
				HomeActivity.this.startActivity(myIntent);
			}
		});
        
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_home, menu);
        return true;
    }
    
}
