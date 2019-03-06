package cultoftheunicorn.marvel;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.googlecode.javacv.cpp.opencv_core;

import org.apache.http.util.EncodingUtils;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import org.opencv.cultoftheunicorn.marvel.R;

public class NameActivity2 extends AppCompatActivity {

    String un,pass,db,ip,usuarioid, nombreusuario, numestacion;
    ProgressBar progressBar, progress;

    String Nombres, Apellidos;
    EditText name, apellidos;

    public static final int SIGNATURE_ACTIVITY = 1;
    String TAG = "Response";
    String resultString;

    String modoremoto = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        // Declaring Server ip, username, database name and password
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);

        progressBar = (ProgressBar) findViewById(R.id.progressBar2);
        progressBar.setVisibility(View.GONE);

        //progress = (ProgressBar)findViewById(R.id.progress);
        //progress.setVisibility(View.GONE);

        ip = prefs.getString("ipservidor","IANSERVICES.DDNS.NET");
        db = prefs.getString("dbservidor","AmericanGreenHouseCheck");
        un = prefs.getString("usuarioservidor","sa");
        pass = prefs.getString("passwordservidor","IAN32");
        numestacion = prefs.getString("numeroestacion","2601");
        modoremoto= prefs.getString("modoremoto", "SI");

        name = (EditText) findViewById(R.id.name);
        apellidos = (EditText) findViewById(R.id.apellido);

        Button nextButton = (Button) findViewById(R.id.nextButton);

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!name.getText().toString().equals("")) {
                    NameActivity2.CheckPersonalNombre checkpersonalnombre = new NameActivity2.CheckPersonalNombre();// this is the Asynctask, which is used to process in background to reduce load on app process
                    checkpersonalnombre.execute("");
                }
                else {
                    Toast.makeText(NameActivity2.this, "Por favor agregue nombres", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public String conectar() {
        String SOAP_ACTION = "urn:androidserviceIntf-Iandroidservice#consultanombresrostros";
        String METHOD_NAME = "consultanombresrostros";
        String NAMESPACE = "urn:androidserviceIntf";
        String URL = "http://"+ip+":1001/soap/Iandroidservice";
        String z;
        boolean isSuccess;

        try {
            SoapObject Request = new SoapObject(NAMESPACE, METHOD_NAME);
            Request.addProperty("Nombres", Nombres);
            Request.addProperty("Apellidos", Apellidos);

            SoapSerializationEnvelope soapEnvelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            soapEnvelope.dotNet = true;
            soapEnvelope.setOutputSoapObject(Request);

            HttpTransportSE transport = new HttpTransportSE(URL,80000);

            transport.call(SOAP_ACTION, soapEnvelope);
            //resultString = (SoapPrimitive) soapEnvelope.getResponse();
            Object  response = (Object) soapEnvelope.getResponse();
            resultString = response.toString();

            return resultString;

            //Log.i(TAG, "Result Celsius: " + resultString);
        } catch (Exception ex) {
            Log.e(TAG, "Error: " + ex.getMessage());
        }
        return SOAP_ACTION;
    }

    public class CheckPersonalNombre extends AsyncTask<String,String,String>
    {
        String z = "";
        Boolean isSuccess = false;

        @Override
        protected void onPreExecute()
        {
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(String r)
        {
            progressBar.setVisibility(View.GONE);
            Toast.makeText(NameActivity2.this, r, Toast.LENGTH_SHORT).show();
            if(isSuccess)
            {
                Toast.makeText(NameActivity2.this , "Login Exitoso" , Toast.LENGTH_LONG).show();
                //finish();
            }
            if(!isSuccess)
            {
                Toast.makeText(NameActivity2.this , "Usuario o Clave incorrecta" , Toast.LENGTH_LONG).show();
            }
        }
        @Override
        protected String doInBackground(String... params)
        {
            Nombres = name.getText().toString();
            Apellidos = apellidos.getText().toString();
            if(Nombres.trim().equals("")|| Apellidos.trim().equals(""))
                z = "Por favor ingrese Nombres y Apellidos";
            else
            {
                if (modoremoto.equals("NO")) {
                    resultString= "BUSCAR";
                    Intent intent = new Intent(NameActivity2.this, ListaEmpleados2.class);
                    intent.putExtra("resultstring", resultString);
                    intent.putExtra("Nombres", Nombres);
                    intent.putExtra("Apellidos", Apellidos);
                    startActivityForResult(intent, SIGNATURE_ACTIVITY);

                } else {

                    resultString = conectar();

                    if (!resultString.equals("-1")) {
                        z = "Consulta obtenida";
                        isSuccess = true;

                        Intent intent = new Intent(NameActivity2.this, ListaEmpleados2.class);
                        intent.putExtra("resultstring", resultString);
                        intent.putExtra("name", Nombres + ' ' + Apellidos);
                        startActivityForResult(intent, SIGNATURE_ACTIVITY);
                    }
                }
            }
            return z;
        }
    }
}
