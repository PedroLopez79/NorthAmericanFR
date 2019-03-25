package cultoftheunicorn.marvel;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.opencv.cultoftheunicorn.marvel.R;

import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import cultoftheunicorn.marvel.dao.ProyectoDAO;
import cultoftheunicorn.marvel.modelo.Proyecto;

public class personalProyecto2 extends AppCompatActivity {

    String TAG = "personalProyecto";
    String resultString, ip, numestacion;
    public static final int SIGNATURE_ACTIVITY = 1;

    Button btnbuscar;

    Spinner spinner;
    ArrayList<String> listado = new ArrayList<>();
    ArrayAdapter<String> adapter;

    ProyectoDAO proyecto;
    private List<Proyecto> mListProyecto;

    String proyect, modoremoto, MACADDRESS, USARWIFI = "";

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String USARWIFI= prefs.getString("MACWIFI", "SI");

        super.onActivityResult(requestCode, resultCode, data);
        switch(resultCode) {
            case -200:
                if (USARWIFI.equals("SI")) {
                    finish();
                }

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_proyecto2);

        spinner = (Spinner) findViewById(R.id.spinnerproyectos);
        btnbuscar = (Button) findViewById(R.id.btnbuscar);

        //****************************************************************************************//
        // ---------------------------------------------------------------------------------------//
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        USARWIFI= prefs.getString("MACWIFI", "SI");

        if (USARWIFI.equals("SI")) {
            MACADDRESS = getMacAddress().toUpperCase();
        }
        //****************************************************************************************//
        //****************************************************************************************//

        ip = prefs.getString("ipservidor", "192.168.0.6");
        numestacion = prefs.getString("numeroestacion","2601");
        modoremoto= prefs.getString("modoremoto", "SI");

        proyecto = new ProyectoDAO(this);
        //obtener listado local de usuario, empleados, fotodefault abrir bases de datos local
        mListProyecto = proyecto.getAllProyecto();

        if (USARWIFI.equals("SI")) {
            Cursor proyectoxmac = proyecto.getAllProyectoxMAC(MACADDRESS);
            proyectoxmac.moveToFirst();

            if (proyectoxmac.getCount() > 0) {
                proyect = proyectoxmac.getString(0);
                Intent intent = new Intent(personalProyecto2.this, ListaEmpleados4.class);
                intent.putExtra("proyect", proyect);
                startActivityForResult(intent, SIGNATURE_ACTIVITY);
            }
        }

        personalProyecto2.AsyncCallWS task = new personalProyecto2.AsyncCallWS();
        task.execute();

        btnbuscar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                proyect= spinner.getSelectedItem().toString();

                Intent intent = new Intent(personalProyecto2.this,  ListaEmpleados4.class);
                intent.putExtra("proyect", proyect);
                startActivityForResult(intent, SIGNATURE_ACTIVITY);
            }
        });
    }

    public static String getMacAddress() {
        try {
            List<NetworkInterface> all = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface nif : all) {
                if (!nif.getName().equalsIgnoreCase("wlan0")) continue;

                byte[] macBytes = nif.getHardwareAddress();
                if (macBytes == null) {
                    return "";
                }

                StringBuilder res1 = new StringBuilder();
                for (byte b : macBytes) {
                    res1.append(Integer.toHexString(b & 0xFF) + ":");
                }

                if (res1.length() > 0) {
                    res1.deleteCharAt(res1.length() - 1);
                }
                return res1.toString();
            }
        } catch (Exception ex) {
            Log.e("Error", ex.getMessage());
        }
        return "";
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

            adapter = new ArrayAdapter<String>(personalProyecto2.this, R.layout.spinner_item2, listado);

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
            adapter.setDropDownViewResource(R.layout.spinner_item2);
            spinner.setAdapter(adapter);
        }
    }

}
