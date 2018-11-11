package stm.msu.mapa.stm_andzela_mario_klient;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.Marshal;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.ksoap2.serialization.PropertyInfo;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlSerializer;

import android.util.AttributeSet;
import android.util.Base64;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.IOException;

import stm.msu.mapa.stm_andzela_mario_klient.MapView;

public class MainActivity extends Activity implements Marshal {
    private String URL = "http://10.0.2.2:8080/ImageEncoder/WebEncoder?WSDL";
    private String NAMESPACE = "http://encoder.angelika.org/";
    private String METHOD_GET_RESIZED_IMAGE = "crop";
    private String SOAP_ACTION_GET_RESIZED_IMAGE =  "http://encoder.angelika.org/crop/";
    private String METHOD_GET_FULL_IMAGE = "getBinaryImage";
    private String SOAP_ACTION_GET_FULL_IMAGE =  "http://encoder.angelika.org/getBinaryImage/";
    MapView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final EditText lat1_txt = findViewById(R.id.lat1_box);
        final EditText lon1_txt = findViewById(R.id.lon1_box);
        final EditText lat2_txt = findViewById(R.id.lat2_box);
        final EditText lon2_txt = findViewById(R.id.lon2_box);

        final Button reset_button = findViewById(R.id.reset_button);
        final Button go_button = findViewById(R.id.go_button);
        imageView = findViewById(R.id.mapView);

        go_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                try {
                    double lat1 = Double.parseDouble(lat1_txt.getText().toString());
                    double lat2 = Double.parseDouble(lat2_txt.getText().toString());
                    double lon1 = Double.parseDouble(lon1_txt.getText().toString());
                    double lon2 = Double.parseDouble(lon2_txt.getText().toString());
                    new CallGetMapWebService().execute(lat1, lat2, lon1, lon2);
                    arg0.setEnabled(false);
                    reset_button.setEnabled(true);
                    lat1_txt.clearFocus();
                    lat2_txt.clearFocus();
                    lon1_txt.clearFocus();
                    lon2_txt.clearFocus();
                    imageView.resetPaths();
                }
                catch (Exception e){
                    Toast.makeText(getApplicationContext(),"Enter proper values",Toast.LENGTH_LONG);
                }
            }
        });

        reset_button.setEnabled(false);
        reset_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                new CallResetWebService().execute("");
               arg0.setEnabled(false);
                go_button.setEnabled(true);
                lat1_txt.setText("");
                lat2_txt.setText("");
                lon1_txt.setText("");
                lon2_txt.setText("");
                lat1_txt.clearFocus();
                lat2_txt.clearFocus();
                lon1_txt.clearFocus();
                lon2_txt.clearFocus();
                imageView.resetPaths();
            }
        });


        imageView.setBoundariesChangeListener(new MapView.BoundariesChangeListener() {
            @Override
            public void onBoundaryChanged(float[] coords) {
                lat1_txt.setText(Float.toString(coords[0]));
                lon1_txt.setText(Float.toString(coords[1]));
                lat2_txt.setText(Float.toString(coords[2]));
                lon2_txt.setText(Float.toString(coords[3]));
            }
        });

    }

    @Override
    public Object readInstance(XmlPullParser parser, String namespace, String name, PropertyInfo expected) throws IOException, XmlPullParserException {
        return Double.parseDouble(parser.nextText());
    }

    @Override
    public void writeInstance(XmlSerializer writer, Object instance) throws IOException {
        writer.text(instance.toString());
    }

    @Override
    public void register(SoapSerializationEnvelope envelope) {
        envelope.addMapping(envelope.xsd, "double", Double.class, this);

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
            SoapObject request = new SoapObject(NAMESPACE, METHOD_GET_RESIZED_IMAGE);
            PropertyInfo propertyInfo_lat1 = new PropertyInfo();
            propertyInfo_lat1.setName("x1");
            propertyInfo_lat1.setValue(params[0]);
            propertyInfo_lat1.setType(double.class);
            PropertyInfo propertyInfo_lat2 = new PropertyInfo();
            propertyInfo_lat2.setName("x2");
            propertyInfo_lat2.setValue(params[1]);
            propertyInfo_lat2.setType(double.class);
            PropertyInfo propertyInfo_lon1 = new PropertyInfo();
            propertyInfo_lon1.setName("y1");
            propertyInfo_lon1.setValue(params[1]);
            propertyInfo_lon1.setType(double.class);
            PropertyInfo propertyInfo_lon2 = new PropertyInfo();
            propertyInfo_lon2.setName("y2");
            propertyInfo_lon2.setValue(params[1]);
            propertyInfo_lon2.setType(double.class);

            request.addProperty(propertyInfo_lat1);
            request.addProperty(propertyInfo_lat2);
            request.addProperty(propertyInfo_lon1);
            request.addProperty(propertyInfo_lon2);

            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.dotNet = false;
            envelope.setOutputSoapObject(request);
            register(envelope);

            HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);

            try {
                androidHttpTransport.call(SOAP_ACTION_GET_RESIZED_IMAGE, envelope);
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
            SoapObject request = new SoapObject(NAMESPACE, METHOD_GET_FULL_IMAGE);
            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.dotNet = false;
            envelope.setOutputSoapObject(request);
            register(envelope);
            HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);

            try {
                androidHttpTransport.call(SOAP_ACTION_GET_FULL_IMAGE, envelope);
                SoapPrimitive  resultsRequestSOAP = (SoapPrimitive) envelope.getResponse();
                String x = resultsRequestSOAP.toString();
                return x;
            } catch (Exception e) {
                Log.e("Activity",e.toString());
            }
            return "something`s wrong?";
        }
    }
}


