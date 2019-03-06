package cultoftheunicorn.marvel;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import org.apache.http.util.EncodingUtils;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.opencv.cultoftheunicorn.marvel.R;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.Attributes;

import cultoftheunicorn.marvel.dao.ProyectoDAO;
import cultoftheunicorn.marvel.modelo.Proyecto;

public class NameActivity extends AppCompatActivity {

    // Declaring connection variables
    Connection con;
    String un,pass,db,ip,usuarioid, nombreusuario, numestacion;
    ProgressBar progressBar;

    String Nombres, Apellidos, Domicilio, Ciudad, Telefono;
    EditText name, apellidos, domicilio, ciudad, telefono;

    public static final int SIGNATURE_ACTIVITY = 1;
    String TAG = "Response";
    String resultString;
    String modoremoto = "";

    Spinner spinner;
    ArrayList<String> listado = new ArrayList<>();
    ArrayAdapter<String> adapter;

    ProyectoDAO proyecto;
    private List<Proyecto> mListProyecto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_name);

        // Declaring Server ip, username, database name and password
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);

        progressBar = (ProgressBar) findViewById(R.id.progressBar2);
        progressBar.setVisibility(View.GONE);
        spinner = (Spinner) findViewById(R.id.spinnerproyectos2);

        ip = prefs.getString("ipservidor","IANSERVICES.DDNS.NET");
        db = prefs.getString("dbservidor","AmericanGreenHouseCheck");
        un = prefs.getString("usuarioservidor","sa");
        pass = prefs.getString("passwordservidor","IAN32");
        numestacion = prefs.getString("numeroestacion","2601");
        modoremoto= prefs.getString("modoremoto", "SI");

        name = (EditText) findViewById(R.id.name);
        apellidos = (EditText) findViewById(R.id.apellido);
        domicilio = (EditText) findViewById(R.id.domicilio);
        ciudad = (EditText) findViewById(R.id.ciudad);
        telefono = (EditText) findViewById(R.id.telefono);

        final EditText domicilio = (EditText) findViewById(R.id.domicilio);
        final EditText ciudad = (EditText) findViewById(R.id.ciudad);
        final EditText telefono = (EditText) findViewById(R.id.telefono);
        Button nextButton = (Button) findViewById(R.id.nextButton);

        proyecto = new ProyectoDAO(this);
        //obtener listado local de usuario, empleados, fotodefault abrir bases de datos local
        mListProyecto = proyecto.getAllProyecto();

        NameActivity.AsyncCallWS task = new NameActivity.AsyncCallWS();
        task.execute();

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!name.getText().toString().equals("")) {
                    CheckPersonalNombre checkpersonalnombre = new CheckPersonalNombre();// this is the Asynctask, which is used to process in background to reduce load on app process
                    checkpersonalnombre.execute("");
                }
                else {
                    Toast.makeText(NameActivity.this, "Por favor agregue nombres", Toast.LENGTH_LONG).show();
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
            Toast.makeText(NameActivity.this, r, Toast.LENGTH_SHORT).show();
            if(isSuccess)
            {
                Toast.makeText(NameActivity.this , "Login Exitoso" , Toast.LENGTH_LONG).show();
                //finish();
            }
            if(!isSuccess)
            {
                Toast.makeText(NameActivity.this , "Usuario o Clave incorrecta" , Toast.LENGTH_LONG).show();
            }
        }
        @SuppressLint("WrongThread")
        @Override
        protected String doInBackground(String... params)
        {
            Nombres = name.getText().toString();
            Apellidos = apellidos.getText().toString();
            Domicilio = domicilio.getText().toString();
            Ciudad = ciudad.getText().toString();
            Telefono = telefono.getText().toString();

            if(Nombres.trim().equals("")|| Apellidos.trim().equals(""))
                z = "Por favor ingrese Nombres y Apellidos";
            else
            {
                resultString = "LOCAL";
                if (modoremoto.equals("NO")) {
                    Intent intent = new Intent(NameActivity.this, ListaEmpleados.class);
                    intent.putExtra("resultstring", resultString);
                    intent.putExtra("Nombres", Nombres);
                    intent.putExtra("Apellidos", Apellidos);
                    intent.putExtra("Domicilio", Domicilio);
                    intent.putExtra("Ciudad", Ciudad);
                    intent.putExtra("Telefono", Telefono);
                    intent.putExtra("Proyecto", spinner.getSelectedItem().toString());

                    intent.putExtra("name", Nombres + ' ' + Apellidos);
                    startActivityForResult(intent, SIGNATURE_ACTIVITY);
                }
                else {

                    resultString = conectar();

                    if (!resultString.equals("-1")) {
                        z = "Consulta obtenida";
                        isSuccess = true;

                        Intent intent = new Intent(NameActivity.this, ListaEmpleados.class);
                        intent.putExtra("resultstring", resultString);
                        intent.putExtra("Nombres", Nombres);
                        intent.putExtra("Apellidos", Apellidos);
                        intent.putExtra("Domicilio", Domicilio);
                        intent.putExtra("Ciudad", Ciudad);
                        intent.putExtra("Telefono", Telefono);
                        intent.putExtra("name", Nombres + ' ' + Apellidos);
                        intent.putExtra("Proyecto", spinner.getSelectedItem().toString());
                        startActivityForResult(intent, SIGNATURE_ACTIVITY);
                    }
                }
            }
            return z;
        }
    }

    public String obtenProyectos() {
        String SOAP_ACTION = "urn:androidserviceIntf-Iandroidservice#obtenproyectos";
        String METHOD_NAME = "obtenproyectos";
        String NAMESPACE = "urn:androidserviceIntf";
        String URL = "http://" + ip + ":1001/soap/Iandroidservice";

        try {
            SoapObject Request = new SoapObject(NAMESPACE, METHOD_NAME);

            SoapSerializationEnvelope soapEnvelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            soapEnvelope.dotNet = true;
            soapEnvelope.setOutputSoapObject(Request);

            HttpTransportSE transport = new HttpTransportSE(URL, 80000);

            transport.call(SOAP_ACTION, soapEnvelope);
            Object response = (Object) soapEnvelope.getResponse();
            resultString = response.toString();

            Log.i(TAG, "Result Celsius: " + resultString);
        } catch (Exception ex) {
            Log.e(TAG, "Error: " + ex.getMessage());
        }

        return resultString;
    }

    private class AsyncCallWS extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            Log.i(TAG, "onPreExecute");
        }

        @Override
        protected Void doInBackground(Void... params) {
            Log.i(TAG, "doInBackground");

            adapter = new ArrayAdapter<String>(NameActivity.this, R.layout.spinner_item, listado);

            String comboProyectos = "";

            if (modoremoto.equals("NO")) {
                //CONSULTA LOCAL////////////////////////////////////////////////////////////////////////
                if (mListProyecto.size() > 0) {
                    comboProyectos = comboProyectos + "[" + mListProyecto.get(0).getId() + "]" + " " + mListProyecto.get(0).getDescripcion().toString();
                    for (int z = 1; z <= mListProyecto.size() - 1; z++) {
                        comboProyectos = comboProyectos + ",";
                        comboProyectos = comboProyectos + "[" + mListProyecto.get(z).getId() + "]" + " " + mListProyecto.get(z).getDescripcion().toString();
                    }
                }

                if (!comboProyectos.equals("-1")) {
                    String[] parts1 = comboProyectos.split(",");
                    listado.clear();
                    /**rutina para poblar combo box o android splitter**/
                    int x = 0;
                    while (x < parts1.length) {
                        listado.add(parts1[x]);
                        x++;
                    }
                    /***************************************************/
                } else {
                    //z = "Usuario o Clave incorrecta";
                }
            }
            else {
                //CONSULTA REMOTA///////////////////////////////////////////////////////////////////////
                comboProyectos = obtenProyectos();

                if (!comboProyectos.equals("-1")) {
                    String[] parts1 = comboProyectos.split(",");
                    listado.clear();
                    /**rutina para poblar combo box o android splitter**/
                    int x = 0;
                    while (x < parts1.length) {
                        String[] parts2 = parts1[x].split("@");
                        listado.add("["+parts2[0]+"] "+parts2[1]);
                        x++;
                    }
                    /***************************************************/
                } else {
                    //z = "Usuario o Clave incorrecta";
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            Log.i(TAG, "onPostExecute");
            adapter.setDropDownViewResource(R.layout.spinner_item);
            spinner.setAdapter(adapter);
        }
    }
}
