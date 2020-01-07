package com.example.android.tflitecamerademo;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;


public class Start extends AppCompatActivity {

    // Constants:
    final String WEATHER_URL = "http://api.openweathermap.org/data/2.5/weather";
    // App ID to use OpenWeather data
    final String APP_ID = "7315464228cf3d70f03bf3b23737a74d";
    // Time between location updates (5000 milliseconds or 5 seconds)
    final long MIN_TIME = 5000;
    // Distance between location updates (1000m or 1km)
    final float MIN_DISTANCE = 1000;

    final int REQUEST_CODE=123;
    // TODO: Set LOCATION_PROVIDER here:
    String LOCATION_PROVIDER = LocationManager.GPS_PROVIDER;


    // Member Variables:
    TextView mCityLabel,mLatitudeLabel,mLongitudeLabel;
    ImageView mWeatherImage;
    TextView mTemperatureLabel;

    // TODO: Declare a LocationManager and a LocationListener here:
    LocationManager mLocationManager;
    LocationListener mLocationListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start);
        Intent intent = getIntent();
        Bitmap image = (Bitmap) intent.getParcelableExtra("classified_image");
        String landmark = intent.getStringExtra("landmark_name");
        String latitude = intent.getStringExtra("latitude");
        String longitude = intent.getStringExtra("longitude");

        // Linking the elements in the layout to Java code
        mCityLabel = (TextView) findViewById(R.id.locationTV);
        mWeatherImage = (ImageView) findViewById(R.id.weatherSymbolIV);
        mTemperatureLabel = (TextView) findViewById(R.id.tempTV);
        ImageButton changeToCameraButton = (ImageButton) findViewById(R.id.toCameraButton);
        mLatitudeLabel= (TextView) findViewById(R.id.latitudeTextView);
        mLongitudeLabel= (TextView) findViewById(R.id.longitudeTextView);

        BitmapDrawable ob = new BitmapDrawable(getResources(), image);
        mWeatherImage.setImageDrawable(ob);
        mCityLabel.setText(landmark);
        mLatitudeLabel.setText("Latitude: "+latitude);
        mLongitudeLabel.setText("Longitude: "+longitude);

        // TODO: Add an OnClickListener to the changeToCameraButton here:
        changeToCameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent= new Intent(Start.this, CameraActivity.class);
                startActivity(myIntent);
            }
        });
    }


    // TODO: Add onResume() here:

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("Clima", "onResume() called");
        Log.d("Clima", "Getting weather for current location");
        getWeatherForCurrentLocation();

    }


    // TODO: Add getWeatherForNewCity(String city) here:


    // TODO: Add getWeatherForCurrentLocation() here:

    private void getWeatherForCurrentLocation() {
        mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        mLocationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                Log.d("Clima", "onLocaitonChanged() callback received");
                String longitude=String.valueOf(location.getLongitude());
                String latitude=String.valueOf(location.getLatitude());

                Log.d("Clima","longitude is:"+longitude);
                Log.d("Clima","latitude is:"+latitude);
                RequestParams params= new RequestParams();
                params.put("lat",latitude);
                params.put("lon", longitude);
                params.put("appid",APP_ID);
                mLatitudeLabel.setText ("Latitude: "+latitude.substring(0,5));
                mLongitudeLabel.setText("Longitude" +longitude.substring(0,5));
                letsDoSomeNetworking(params);
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {
                Log.d("Clima", "onProvideDisabled() callback received");

            }
        };
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            ActivityCompat.requestPermissions(this,
                    new String[] {Manifest.permission.ACCESS_FINE_LOCATION},REQUEST_CODE);
            return;
        }
        mLocationManager.requestLocationUpdates(LOCATION_PROVIDER, MIN_TIME, MIN_DISTANCE, mLocationListener);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
                    super.onRequestPermissionsResult(requestCode, permissions, grantResults);
                    if(requestCode== REQUEST_CODE){
                        if(grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                            Log.d("Clima","onRequestPermisionResult(): Permision granted!");
                            getWeatherForCurrentLocation();
                        }else{
                            Log.d("Clima","Permision denied=(" );
            }
        }
    }
// TODO: Add letsDoSomeNetworking(RequestParams params) here:
    private void letsDoSomeNetworking(RequestParams params){
        AsyncHttpClient client= new AsyncHttpClient();
        client.get(WEATHER_URL, params, new JsonHttpResponseHandler(){
           @Override
           public  void onSuccess(int statusCode, Header[] headers, JSONObject response){
                Log.d("Clima","Success! JSON: "+response.toString());
                WeatherDataModel weatherData= WeatherDataModel.fromJson(response);
                updateUI(weatherData);
           }

           @Override
           public  void onFailure(int statusCode, Header[] headers, Throwable e, JSONObject response){
                Log.e("Clima","Fail "+ e.toString());
                Log.e("Clima","Status code" + statusCode);
               Toast.makeText(Start.this, "Request Failed", Toast.LENGTH_SHORT).show();
           }
        });
    }


    // TODO: Add updateUI() here:

    private void updateUI(WeatherDataModel weather){
        mTemperatureLabel.setText(weather.getTemperature());
        mCityLabel.setText(weather.getCity());

        int resourceID= getResources().getIdentifier(weather.getIconName(),"drawable",getPackageName());
        mWeatherImage.setImageResource(resourceID);

    }
    // TODO: Add onPause() here:



}
