package com.meandyou.avi;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import android.view.View.OnClickListener;

//adapted from jim's httpClientDemo

public class Advisory extends Activity{
	//all of the text views that I need to set
	TextView output;
	TextView date;
	TextView highMorn;
	TextView midMorn;
	TextView lowMorn;
	TextView highAft;
	TextView midAft;
	TextView lowAft;
	ArrayList<TextView> hazards2 = new ArrayList<TextView>();
	TextView Temp5;
	TextView maxTemp;
	TextView wDir;
	TextView wAv;
	TextView wGust;
	TextView spRay;
	TextView spRen;
	TextView spJos;
	TextView sdRay;
	TextView sdRen;
	TextView sdJos;
	TextView tsRay;
	TextView tsRen;
	TextView tsJos;
	TextView sky;
	TextView temp;
	TextView wind;
	TextView snow;
	TextView ridge;
	TextView issued;
	Button plus1;
	View view1;
	ImageView hazard;
	
	
	String html;
	String lines[];
	String highMorn1;
	String highAft1;
	String midMorn1;
	String midAft1;
	String lowMorn1;
	String lowAft1;

	/** Called when the activity is first created. */
	@TargetApi(Build.VERSION_CODES.GINGERBREAD) @Override
	public void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.test);
		//instantiates all of the text views
		output = (TextView) findViewById(R.id.output);
		date = (TextView) findViewById(R.id.date);
		Temp5 = (TextView) findViewById(R.id.Temp5);
		maxTemp = (TextView) findViewById(R.id.maxTemp);
		wDir = (TextView) findViewById(R.id.wDir);
		wAv = (TextView) findViewById(R.id.wAv);
		wGust = (TextView) findViewById(R.id.wGust);
		spRay = (TextView) findViewById(R.id.spRay);
		spRen = (TextView) findViewById(R.id.spRen);
		spJos = (TextView) findViewById(R.id.spJos);
		sdRay = (TextView) findViewById(R.id.sdRay);
		sdRen = (TextView) findViewById(R.id.sdRen);
		sdJos = (TextView) findViewById(R.id.sdJos);
		tsRay = (TextView) findViewById(R.id.tsRay);
		tsRen = (TextView) findViewById(R.id.tsRen);
		tsJos = (TextView) findViewById(R.id.tsJos);
		sky = (TextView) findViewById(R.id.sky);
		temp = (TextView) findViewById(R.id.temp);
		wind = (TextView) findViewById(R.id.wind);
		snow = (TextView) findViewById(R.id.snow);
		ridge = (TextView) findViewById(R.id.ridge);
		issued = (TextView) findViewById(R.id.date);
		hazard = (ImageView) findViewById(R.id.image);
		
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy); 
		
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		
		plus1 = (Button) findViewById(R.id.plus1);
		plus1.setOnClickListener(new OnClickListener(){
			public void onClick(View v){
				if(plus1.getText().equals("+"))
				{
					hazard.setVisibility(View.VISIBLE);
					plus1.setText("-");
				}
				else
				{
					hazard.setVisibility(View.GONE);
					plus1.setText("+");
				}
			}
		});

		new Thread(new doNetwork()).start();
	}
	
	//overode the onresume so the app would lock into landscape when this tab was selected
	@Override 
    public void onResume() { 
        super.onResume(); 
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
    }
	
	//handles messages from the http request thread and calls the parse method
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			//output.append(msg.getData().getString("msg"));
			//check to see if the connection was made
			if(msg.getData().getString("msg").contains("Failed to retreive webpage"))
				issued.setText("Failed to get the web page, check your connection");
			//if there isn't a real advisory print out a message and don't parse
			if(msg.getData().getString("msg").contains("Forecast not found"))
				issued.setText("There is no forcast found for today. Please go to the web page tab to find the most recent forecast");
			//if the message is the html, parse it
			else if(msg.getData().getString("msg").contains("html")){
				html = msg.getData().getString("msg");
				parse();
			}

		}

	};
	

	public void parse(){
		lines = html.split("\n");
		//start at the first line
		int i=0;
		String precip = "";

		String htmls2[];

		while(!lines[i].contains("Issued at"))
			i++;
		precip = lines[i];
		
		htmls2 =  precip.split(">|&");
		for(String data : htmls2){
			//System.out.println(data);
			if(data.contains("Issued at")){
				issued.setText(data);
				//output.append("\n" + data);
			}
		}
		//this is a dummy advance
		while(!lines[i].contains("Rendezvous Summit") )
			i++;
		i++;
		htmls2 = lines[i].split("[>&]");
		for(int j = 0; j<htmls2.length; j++){
			if(!htmls2[j].contains("td")){
				//output.append("\n" + htmls2[j]);
				Temp5.append(htmls2[j] + (char) 0x00B0 + 'F');
				break;
			}
		}
		
		i++;
		htmls2 = lines[i].split("[><]");
		for(int j = 0; j<htmls2.length; j++){
			if(htmls2[j].contains("td")){
				j++;
				//output.append("\n" + htmls2[j]);
				maxTemp.append(htmls2[j] + (char) 0x00B0 + 'F');
				break;
			}
		}
		i++;
		htmls2 = lines[i].split("[><]");
		for(int j = 0; j<htmls2.length; j++){
			if(htmls2[j].contains("td")){
				j++;
				//output.append("\n" + htmls2[j]);
				wDir.append(htmls2[j]);
				break;
			}
		}
		i++;
		htmls2 = lines[i].split("[><]");
		for(int j = 0; j<htmls2.length; j++){
			if(htmls2[j].contains("td")){
				j++;
				//output.append("\n" + htmls2[j]);
				wAv.append(htmls2[j]+" MPH");
				break;
			}
		}
		i++;
		htmls2 = lines[i].split("[><]");
		for(int j = 0; j<htmls2.length; j++){
			if(htmls2[j].contains("td")){
				j++;
				//output.append("\n" + htmls2[j]);
				wGust.append(htmls2[j]+" MPH");
				break;
			}
		}
		
//		
		
		while(!lines[i].contains("Raymer Plot"))
			i++;
		i++;

		TextView row[] = {spRay,sdRay,tsRay};
		int set = 0;
		precip = "";
		while(!lines[i].contains("</tr>")){
			precip += lines[i];
			i++;
		}
		htmls2 =  precip.split("<strong>|</strong>");
		for(String data : htmls2){
			//System.out.println(data);
			if(!data.contains("<") && !data.contains(">")){
				row[set].setText(data.replace(" ", ""));
				set++;
				//output.append("\n" + data);
			}
		}
		
		while(!lines[i].contains("Rendezvous Bowl Plot"))
			i++;
		i++;
		TextView row2[] = {spRen,sdRen,tsRen};
		set = 0;
		precip = "";
		while(!lines[i].contains("</tr>")){
			precip += lines[i];
			i++;
		}
		htmls2 =  precip.split("<strong>|</strong>");
		for(String data : htmls2){
			//System.out.println(data);
			if(!data.contains("<") && !data.contains(">")){
				row2[set].setText(data.replace(" ", ""));
				set++;
				System.out.println(""+set);
				//output.append("\n" + data);
			}
		}
		
		while(!lines[i].contains("Chief Joseph Plot"))
			i++;
		i++;
		TextView row3[] = {spJos,sdJos,tsJos};
		set = 0;
		precip = "";
		while(!lines[i].contains("</tr>")){
			precip += lines[i];
			i++;
		}
		
		htmls2 =  precip.split("<strong>|</strong>");
		for(String data : htmls2){
			//System.out.println(data);
			if(!data.contains("<") && !data.contains(">")){
				row3[set].setText(data.replace(" ", ""));
				set++;
				//output.append("\n" + data);
			}
		}		
		
		//fills in the weather forecast table
		while(!lines[i].contains("mtnWeather"))
			i++;
		i++;
		precip = "";
		while(!lines[i].contains("</tr>")){
			precip += lines[i];
			i++;
		}
		htmls2 =  precip.split("<b>|</b>");
		for(String data : htmls2){
			//System.out.println(data);
			if(!data.contains("<") && !data.contains(">")){
				sky.setText(data);
				//output.append("\n" + data);
			}
		}
		
	
		while(!lines[i].contains("<tr>"))
			i++;
		i++;
		precip = "";
		while(!lines[i].contains("</tr>")){
			precip += lines[i];
			i++;
		}
		
		htmls2 =  precip.split("<b>|</b>");
		for(String data : htmls2){
			//System.out.println(data);
			if(!data.contains("<") && !data.contains(">")){
				temp.setText(data);
				//output.append("\n" + data);
			}
		}
		
		while(!lines[i].contains("<tr>"))
			i++;
		i++;
		precip = "";
		while(!lines[i].contains("</tr>")){
			precip += lines[i];
			i++;
		}
		
		htmls2 =  precip.split("<b>|</b>");
		for(String data : htmls2){
			//System.out.println(data);
			if(!data.contains("<") && !data.contains(">")){
				wind.setText(data);
				//output.append("\n" + data);
			}
		}
//		
		while(!lines[i].contains("<tr>"))
			i++;
		i++;
		precip = "";
		while(!lines[i].contains("</tr>")){
			precip += lines[i];
			i++;
		}
		
		htmls2 =  precip.split("<b>|</b>");
		for(String data : htmls2){
			//System.out.println(data);
			if(!data.contains("<") && !data.contains(">")){
				snow.append(data);
				//output.append("\n" + data);
			}
		}
		
		while(!lines[i].contains("img src"))
			i++;
		precip = lines[i];
		while(!lines[i].contains("/>")){
			precip += lines[i];
			i++;
		}
		
		htmls2 =  precip.split("\"");
		for(String data : htmls2){
			//System.out.println(data);
			if(data.contains(".gif")){
				setGeneralAdvisory("http://jhavalanche.org" + data);
				//output.append("\n" + data);
			}
		}

		
		i=0;
		while(!lines[i].contains("GENERAL AVALANCHE ADVISORY"))
			i++;
		precip = "";
		while(!lines[i].contains("</div>")){
			precip += lines[i];
			i++;
		}
		precip +=lines[i];
		htmls2 =  precip.split("</h3>|</div>");
		for(String data : htmls2){
			//System.out.println(data);
			if(!data.contains("<") && !data.contains(">")){
				output.setText(data);
				break;
			}
		}
	}

	public void mkmsg(String str) {
		//handler junk, because thread can't update screen!
		Message msg = new Message();
		Bundle b = new Bundle();
		b.putString("msg", str);
		msg.setData(b);
		handler.sendMessage(msg);
	}
	
	//this sets the colors of the general hazard to match the forecast webpage
	public void setHazardColors(){
		for(TextView hazard: hazards2){
			if(hazard.getText().toString().contains("LOW"))
				hazard.setTextColor(Color.GREEN);
			else if(hazard.getText().toString().contains("MODERATE"))
				hazard.setTextColor(Color.YELLOW);
			else if(hazard.getText().toString().contains("CONSIDERABLE"))
				hazard.setTextColor(Color.rgb(255, 150, 45));
			else if(hazard.getText().toString().contains("HIGH"))
				hazard.setTextColor(Color.RED);
			else if(hazard.getText().toString().contains("EXTREME"))
				hazard.setTextColor(Color.RED);
		}
	}
	//performs the httpClient code, recieves the web page html and sends it to the mkmsg which sends it to the handler
	public void executeHttpGet() throws Exception {
		BufferedReader in = null;
		try {
			HttpClient client = new DefaultHttpClient();
			HttpGet request = new HttpGet();
			//NOTE:
			request.setURI(new URI("http://jhavalanche.org/viewTeton"));
			//request.setURI(new URI("http://www.cs.uwyo.edu/~briotto1/"));
			mkmsg("Requesting web page.\n");
			HttpResponse response = client.execute(request);
			mkmsg("Webpage retreived, processing it.\n");
			in = new BufferedReader	(new InputStreamReader(response.getEntity().getContent()));
			StringBuffer sb = new StringBuffer("");
			String line = "";
			String NL = System.getProperty("line.separator");
			while ((line = in.readLine()) != null) {
				sb.append(line + NL);
			}
			in.close();
			String page = sb.toString();
			mkmsg("Processed page:");
			mkmsg(page);
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

		}
	}
	// the thread that calls the request method
	class doNetwork  implements Runnable {
		public void run() {
			mkmsg("Attempting to retreive webpage ...\n");
			try {
				executeHttpGet();
				mkmsg("Finished\n");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				mkmsg("Failed to retreive webpage ...\n");
				mkmsg(e.getMessage());
				e.printStackTrace();
			}
		}
	}
	public void setGeneralAdvisory(String url){
	try {
		  Bitmap bitmap = BitmapFactory.decodeStream((InputStream)new URL(url).getContent());
		  hazard.setImageBitmap(bitmap); 
		} catch (MalformedURLException e) {
		  e.printStackTrace();
		} catch (IOException e) {
		  e.printStackTrace();
		}
	}
}