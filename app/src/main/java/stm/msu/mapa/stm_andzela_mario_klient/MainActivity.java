package stm.msu.mapa.stm_andzela_mario_klient;

import android.os.Bundle;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.ksoap2.serialization.PropertyInfo;

import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import java.io.FileNotFoundException;
import java.math.BigDecimal;

public class MainActivity extends Activity {
    private String URL = "http://127.0.0.1:8080/MapaWebService/MapaWebService?WSDL";
    private String NAMESPACE = "http://stm_andzela_mario_serwer/";
    private String METHOD_NAME = "get_small_map";
    private String SOAP_ACTION =  "http://stm_andzela_mario/get_small_map";
    private String MAP_METHOD_NAME = "map";
    private String MAP_SOAP_ACTION =  "http://stm_andzela_mario/map";
    ImageView imageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button go_button = findViewById(R.id.go_button);
        go_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {

                EditText lat1_txt = findViewById(R.id.lat1_box);
                double lat1 = Double.parseDouble(lat1_txt.getText().toString());
                EditText lat2_txt = findViewById(R.id.lat2_box);
                double lat2 = Double.parseDouble(lat2_txt.getText().toString());
                EditText lon1_txt = findViewById(R.id.lon1_box);
                double lon1 = Double.parseDouble(lon1_txt.getText().toString());
                EditText lon2_txt = findViewById(R.id.lon2_box);
                double lon2 = Double.parseDouble(lon2_txt.getText().toString());
                new CallGetMapWebService().execute(lat1, lat2, lon1, lon2);
            }
        });
        Button reset_button = findViewById(R.id.reset_button);
        reset_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                new CallResetWebService().execute("");
            }
        });
        imageView = findViewById(R.id.mapView);
//        java.io.FileInputStream in = null;
//        try {
//            in = openFileInput("C:\\eti\\stm\\map.png");
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        }
//        imageView.setImageBitmap(BitmapFactory.decodeStream(in));

    }
    public class CallGetMapWebService extends AsyncTask<Double, Void, String> {
        @Override
        protected void onPostExecute(String s) {
            byte[] decodedString = Base64.decode(s, Base64.DEFAULT);
            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            imageView.setImageBitmap(decodedByte);
        }

        @Override
        protected String doInBackground(Double... params) {
            SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
            PropertyInfo propertyInfo_lat1 = new PropertyInfo();
            propertyInfo_lat1.setName("lat1");
            propertyInfo_lat1.setValue(params[0]);
            propertyInfo_lat1.setType(double.class);
            PropertyInfo propertyInfo_lat2 = new PropertyInfo();
            propertyInfo_lat2.setName("lat2");
            propertyInfo_lat2.setValue(params[1]);
            propertyInfo_lat2.setType(double.class);
            PropertyInfo propertyInfo_lon1 = new PropertyInfo();
            propertyInfo_lon1.setName("lat2");
            propertyInfo_lon1.setValue(params[1]);
            propertyInfo_lon1.setType(double.class);
            PropertyInfo propertyInfo_lon2 = new PropertyInfo();
            propertyInfo_lon2.setName("lat2");
            propertyInfo_lon2.setValue(params[1]);
            propertyInfo_lon2.setType(double.class);

            request.addProperty(propertyInfo_lat1);
            request.addProperty(propertyInfo_lat2);
            request.addProperty(propertyInfo_lon1);
            request.addProperty(propertyInfo_lon2);

            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.dotNet = false;
            envelope.setOutputSoapObject(request);
            HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);

            try {
                androidHttpTransport.call(SOAP_ACTION, envelope);
                SoapPrimitive  resultsRequestSOAP = (SoapPrimitive) envelope.getResponse();
                return resultsRequestSOAP.toString();
            } catch (Exception e) {

                Log.e("Activity",e.toString());
            }
            return "something`s wrong?";
        }
    }

    public class CallResetWebService extends AsyncTask<String, Void, String> {
        @Override
        protected void onPostExecute(String s) {
            byte[] decodedString = Base64.decode(s, Base64.DEFAULT);
            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            imageView.setImageBitmap(decodedByte);
        }

        @Override
        protected String doInBackground(String... params) {
            SoapObject request = new SoapObject(NAMESPACE, MAP_METHOD_NAME);
            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.dotNet = false;
            envelope.setOutputSoapObject(request);
            HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);

            try {
                androidHttpTransport.call(MAP_SOAP_ACTION, envelope);
                SoapPrimitive  resultsRequestSOAP = (SoapPrimitive) envelope.getResponse();
                return resultsRequestSOAP.toString();
            } catch (Exception e) {
                Log.e("Activity",e.toString());
            }
            return "something`s wrong?";
        }
    }
}