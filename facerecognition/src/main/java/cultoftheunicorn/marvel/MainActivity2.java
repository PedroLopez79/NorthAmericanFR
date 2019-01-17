package cultoftheunicorn.marvel;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Image;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.opencv.cultoftheunicorn.marvel.R;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.StringReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;

import cultoftheunicorn.marvel.dao.EmpleadoDAO;
import cultoftheunicorn.marvel.dao.FotodefaultDAO;
import cultoftheunicorn.marvel.dao.UsuarioDAO;
import cultoftheunicorn.marvel.modelo.Empleado;
import cultoftheunicorn.marvel.modelo.FotoDefault;
import cultoftheunicorn.marvel.modelo.Usuario;


public class MainActivity2 extends AppCompatActivity
{
    // Declaring layout button, edit texts
    public static final int SIGNATURE_ACTIVITY = 1;
    String TAG = "Response";
    String resultString;


    Button login;
    ImageButton syncbutton;
    EditText username,password;
    ProgressBar progressBar;
    // End Declaring layout button, edit texts

    // Declaring connection variables
    Connection con;
    String un,pass,db,ip,usuarioid, nombreusuario, numestacion, modoremoto;

    String usr, psw;
    //End Declaring connection variables

    //preubas esto se asignara con la consulta//
    String lista1 = "";
    String lista2 = "";
    private List<Empleado> mListEmpleado;
    private List<Usuario> mListUsuario;
    private List<FotoDefault> mListFotoDefault;

    String IMAGE1,IMAGE2,IMAGE3,IMAGE4,IMAGE5,IMAGE6,IMAGE7,IMAGE8,IMAGE9,IMAGE10;
    String NOMBRESEMPLEADOS, NOMBRESUSUARIOS;
    String IMAGEDEFAULT1,IMAGEDEFAULT2,IMAGEDEFAULT3,IMAGEDEFAULT4,IMAGEDEFAULT5,IMAGEDEFAULT6,
            IMAGEDEFAULT7,IMAGEDEFAULT8,IMAGEDEFAULT9,IMAGEDEFAULT10;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        // Getting values from button, texts and progress bar
        login = (Button) findViewById(R.id.button);
        username = (EditText) findViewById(R.id.editText);
        password = (EditText) findViewById(R.id.editText2);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        syncbutton = (ImageButton) findViewById(R.id.sync);
        progressBar.setVisibility(View.GONE);
        // End Getting values from button, texts and progress bar

        // Declaring Server ip, username, database name and password
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);

        ip = prefs.getString("ipservidor","192.168.0.6");
        db = prefs.getString("dbservidor","GAUSS_VER1.1");
        un = prefs.getString("usuarioservidor","sa");
        pass = prefs.getString("passwordservidor","Cistem32");
        numestacion = prefs.getString("numeroestacion","2601");
        modoremoto = prefs.getString("modoremoto", "SI");

        if (modoremoto.equals("SI")){
            syncbutton.setVisibility(View.VISIBLE);
        }else {
            syncbutton.setVisibility(View.INVISIBLE);
        }


        // Setting up the function when button login is clicked
        login.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                CheckLogin checkLogin = new CheckLogin();// this is the Asynctask, which is used to process in background to reduce load on app process
                checkLogin.execute("");
            }
        });
        //End Setting up the function when button login is clicked

        // funcion de syncronizado
        syncbutton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Synconizar();
            }
        });
        // fin de la funcion de syncroniado-------------------------------------------------------//
    }

    public String Synconizar (){
        String result = "";

        //obtener listado local de usuario, empleados, fotodefault abrir bases de datos local
        EmpleadoDAO empleado = new EmpleadoDAO(this);
        FotodefaultDAO fotodefault = new FotodefaultDAO(this);
        UsuarioDAO usuario =  new UsuarioDAO(this);

        mListEmpleado = empleado.getAllEmpleado();
        mListFotoDefault = fotodefault.getAllFotoEmpleado();
        mListUsuario = usuario.getAllUsuario();

        //obtener elementos a syncronizar de forma local de base de datos remota------------////////
        lista1="'Javier Soto', 'Omar Luna'";
        lista2="'Javier Soto', 'Omar Luna'";
        syncronization syncroniza = new syncronization();
        syncroniza.execute("");
        ////////////////////////////////////////////////////////////////////////////////////////////

        return result;
    }

    public String obtenlistasyncronizage(String list1, String list2) {
        String SOAP_ACTION = "urn:androidserviceIntf-Iandroidservice#obtenListaSyncronizado";
        String METHOD_NAME = "obtenListaSyncronizado";
        String NAMESPACE = "urn:androidserviceIntf";
        String URL = "http://"+ip+":1001/soap/Iandroidservice";
        String z;
        boolean isSuccess;

        try {
            SoapObject Request = new SoapObject(NAMESPACE, METHOD_NAME);
            Request.addProperty("listadosyncronizar1", list1);
            Request.addProperty("listadosyncronizar2", list2);

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

    public String conectar() {
        String SOAP_ACTION = "urn:androidserviceIntf-Iandroidservice#login";
        String METHOD_NAME = "login";
        String NAMESPACE = "urn:androidserviceIntf";
        String URL = "http://"+ip+":1001/soap/Iandroidservice";
        String z;
        boolean isSuccess;

        try {
            SoapObject Request = new SoapObject(NAMESPACE, METHOD_NAME);
            Request.addProperty("usr", usr);
            Request.addProperty("password", psw);

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

    public void onClickConfiguracion(View view) {
        Intent intent = new Intent(getApplicationContext(), SettingsActivity.class);
        startActivityForResult(intent, SIGNATURE_ACTIVITY);
    }

    public class CheckLogin extends AsyncTask<String,String,String>
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
            Toast.makeText(MainActivity2.this, r, Toast.LENGTH_SHORT).show();
            if(isSuccess)
            {
                Toast.makeText(MainActivity2.this , "Login Exitoso" , Toast.LENGTH_LONG).show();
                //finish();
            }
            if(!isSuccess)
            {
                Toast.makeText(MainActivity2.this , "Usuario o Clave incorrecta" , Toast.LENGTH_LONG).show();
            }
        }
        @Override
        protected String doInBackground(String... params)
        {
            usr = username.getText().toString();
            psw = password.getText().toString();
            if(usr.trim().equals("")|| psw.trim().equals(""))
                z = "Por favor ingrese usuario y password";
            else
            {
                resultString = conectar();

                if (!resultString.equals("-1"))
                {
                    String[] parts = resultString.split("@");
                    usuarioid = parts[0];
                    nombreusuario = parts[1];
                    z = "Login Exitoso";
                    isSuccess = true;

                    Intent intent = new Intent(getApplicationContext(), Menu.class);
                    intent.putExtra("usuarioid", usuarioid);
                    intent.putExtra("nombreusuario", nombreusuario);
                    startActivityForResult(intent, SIGNATURE_ACTIVITY);
                }
            }
            return z;
        }
    }

    public class obten10rostros5paresempleados extends AsyncTask<String,String,String>
    {
        @Override
        protected void onPreExecute()
        {
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(String r)
        {
            progressBar.setVisibility(View.GONE);
            Toast.makeText(MainActivity2.this, r, Toast.LENGTH_SHORT).show();
        }
        @Override
        protected String doInBackground(String... params)
        {
            resultString = obtenlistasyncronizage(lista1, lista2);

            if (!resultString.equals("")){

                String datosempleados = resultString;

                /*---------------------GUARDAR ROSTROS 10 IMAGENES JPG----------------------------*/
                if (!datosempleados.trim().equals("")) {

                    try {

                        String substr = datosempleados.substring(0, datosempleados.indexOf("<"));
                        datosempleados = datosempleados.substring(datosempleados.indexOf("<"));
                        int i = Integer.parseInt(substr);

                        XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                        factory.setNamespaceAware(true);
                        XmlPullParser xpp = factory.newPullParser();

                        xpp.setInput(new StringReader(datosempleados));

                        xpp.next();
                        int eventType = xpp.getEventType();

                        int c = 0;

                        IMAGE1 ="";IMAGE2="";IMAGE3="";IMAGE4="";IMAGE5="";IMAGE6="";IMAGE7="";
                        IMAGE8 ="";IMAGE9="";IMAGE10 = "";

                        IMAGEDEFAULT1 ="";IMAGEDEFAULT2 ="";IMAGEDEFAULT3 ="";IMAGEDEFAULT4 ="";
                        IMAGEDEFAULT5 ="";IMAGEDEFAULT6 ="";IMAGEDEFAULT7 ="";IMAGEDEFAULT8 ="";
                        IMAGEDEFAULT9 ="";IMAGEDEFAULT10 ="";

                        NOMBRESEMPLEADOS = "";
                        NOMBRESUSUARIOS  = "";

                        String text = "";
                        String empleados = "";
                        String usuarios = "";

                        String fotodefault1 = "";
                        String fotodefault2 = "";
                        String fotodefault3 = "";
                        String fotodefault4 = "";
                        String fotodefault5 = "";
                        String fotodefault6 = "";
                        String fotodefault7 = "";
                        String fotodefault8 = "";
                        String fotodefault9 = "";
                        String fotodefault10 = "";

                        while (eventType != XmlPullParser.END_DOCUMENT) {
                            String tagname = xpp.getName();
                            switch (eventType) {
                                case XmlPullParser.START_TAG:
                                    if (tagname.equalsIgnoreCase("SERVICES")) {
                                        // create a new instance of employee
                                        //employee = new Employee();
                                    }
                                    break;

                                case XmlPullParser.TEXT:
                                    text = xpp.getText();
                                    break;

                                case XmlPullParser.END_TAG:
                                    if (tagname.equalsIgnoreCase("SERVICES")) {
                                        NOMBRESEMPLEADOS = empleados;
                                        NOMBRESUSUARIOS  = usuarios;

                                        IMAGEDEFAULT1 = fotodefault1;
                                        IMAGEDEFAULT2 = fotodefault2;
                                        IMAGEDEFAULT3 = fotodefault3;
                                        IMAGEDEFAULT4 = fotodefault4;
                                        IMAGEDEFAULT5 = fotodefault5;
                                        IMAGEDEFAULT6 = fotodefault6;
                                        IMAGEDEFAULT7 = fotodefault7;
                                        IMAGEDEFAULT8 = fotodefault8;
                                        IMAGEDEFAULT9 = fotodefault9;
                                        IMAGEDEFAULT10 = fotodefault10;
                                        c++;

                                    } else if (tagname.equalsIgnoreCase("NOMBRESEMPLEADOS")) {
                                        empleados = empleados + text;
                                    } else if (tagname.equalsIgnoreCase("NOMBRESUSUARIOS")) {
                                        usuarios = usuarios + text;
                                    } else if (tagname.equalsIgnoreCase("FOTODEFAULT1BASE64")) {
                                        fotodefault1 = text;
                                    } else if (tagname.equalsIgnoreCase("FOTODEFAULT2BASE64")) {
                                        fotodefault2 = text;
                                    } else if (tagname.equalsIgnoreCase("FOTODEFAULT3BASE64")) {
                                        fotodefault3 = text;
                                    } else if (tagname.equalsIgnoreCase("FOTODEFAULT4BASE64")) {
                                        fotodefault4 = text;
                                    } else if (tagname.equalsIgnoreCase("FOTODEFAULT5BASE64")) {
                                        fotodefault5 = text;
                                    } else if (tagname.equalsIgnoreCase("FOTODEFAULT6BASE64")) {
                                        fotodefault6 = text;
                                    } else if (tagname.equalsIgnoreCase("FOTODEFAULT7BASE64")) {
                                        fotodefault7 = text;
                                    } else if (tagname.equalsIgnoreCase("FOTODEFAULT8BASE64")) {
                                        fotodefault8 = text;
                                    } else if (tagname.equalsIgnoreCase("FOTODEFAULT9BASE64")) {
                                        fotodefault9 = text;
                                    } else if (tagname.equalsIgnoreCase("FOTODEFAULT10BASE64")) {
                                        fotodefault10 = text;
                                    }
                                    break;

                                default:
                                    break;
                            }
                            eventType = xpp.next();
                        }

                    } catch (XmlPullParserException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                        finish();
                    } finally {
                    }
                }
                /*--------------------------------------------------------------------------------*/
            }

            resultString = "OK";
            return resultString;
        }
    }
    }

    public class syncronization extends AsyncTask<String,String,String>
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
            Toast.makeText(MainActivity2.this, r, Toast.LENGTH_SHORT).show();

            /*PROBAR REALIZAR EL LLAMADO PARA LAS IMAGEN EN PARES DE 5 EB 5 PARA SU POSTERIOR ----*/
            /*ALMACENAMIENTO EN LA BASE DE DATOS LOCAL*********************************************/

            //if(isSuccess)
            //{
            //    Toast.makeText(MainActivity2.this , "Login Exitoso" , Toast.LENGTH_LONG).show();
            //    //finish();
            //}
            //if(!isSuccess)
            //{
            //    Toast.makeText(MainActivity2.this , "Usuario o Clave incorrecta" , Toast.LENGTH_LONG).show();
            //}
        }
        @Override
        protected String doInBackground(String... params)
        {
            resultString = obtenlistasyncronizage(lista1, lista2);

            if (!resultString.equals("")){

                String datosempleados = resultString;

                /*---------------------GUARDAR ROSTROS 10 IMAGENES JPG----------------------------*/
                if (!datosempleados.trim().equals("")) {

                    try {

                        String substr = datosempleados.substring(0, datosempleados.indexOf("<"));
                        datosempleados = datosempleados.substring(datosempleados.indexOf("<"));
                        int i = Integer.parseInt(substr);

                        XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                        factory.setNamespaceAware(true);
                        XmlPullParser xpp = factory.newPullParser();

                        xpp.setInput(new StringReader(datosempleados));

                        xpp.next();
                        int eventType = xpp.getEventType();

                        int c = 0;

                        IMAGE1 ="";IMAGE2="";IMAGE3="";IMAGE4="";IMAGE5="";IMAGE6="";IMAGE7="";
                        IMAGE8 ="";IMAGE9="";IMAGE10 = "";

                        IMAGEDEFAULT1 ="";IMAGEDEFAULT2 ="";IMAGEDEFAULT3 ="";IMAGEDEFAULT4 ="";
                        IMAGEDEFAULT5 ="";IMAGEDEFAULT6 ="";IMAGEDEFAULT7 ="";IMAGEDEFAULT8 ="";
                        IMAGEDEFAULT9 ="";IMAGEDEFAULT10 ="";

                        NOMBRESEMPLEADOS = "";
                        NOMBRESUSUARIOS  = "";

                        String text = "";
                        String empleados = "";
                        String usuarios = "";

                        String fotodefault1 = "";
                        String fotodefault2 = "";
                        String fotodefault3 = "";
                        String fotodefault4 = "";
                        String fotodefault5 = "";
                        String fotodefault6 = "";
                        String fotodefault7 = "";
                        String fotodefault8 = "";
                        String fotodefault9 = "";
                        String fotodefault10 = "";

                        while (eventType != XmlPullParser.END_DOCUMENT) {
                            String tagname = xpp.getName();
                            switch (eventType) {
                                case XmlPullParser.START_TAG:
                                    if (tagname.equalsIgnoreCase("SERVICES")) {
                                        // create a new instance of employee
                                        //employee = new Employee();
                                    }
                                    break;

                                case XmlPullParser.TEXT:
                                    text = xpp.getText();
                                    break;

                                case XmlPullParser.END_TAG:
                                    if (tagname.equalsIgnoreCase("SERVICES")) {
                                        NOMBRESEMPLEADOS = empleados;
                                        NOMBRESUSUARIOS  = usuarios;

                                        IMAGEDEFAULT1 = fotodefault1;
                                        IMAGEDEFAULT2 = fotodefault2;
                                        IMAGEDEFAULT3 = fotodefault3;
                                        IMAGEDEFAULT4 = fotodefault4;
                                        IMAGEDEFAULT5 = fotodefault5;
                                        IMAGEDEFAULT6 = fotodefault6;
                                        IMAGEDEFAULT7 = fotodefault7;
                                        IMAGEDEFAULT8 = fotodefault8;
                                        IMAGEDEFAULT9 = fotodefault9;
                                        IMAGEDEFAULT10 = fotodefault10;
                                        c++;

                                    } else if (tagname.equalsIgnoreCase("NOMBRESEMPLEADOS")) {
                                        empleados = empleados + text;
                                    } else if (tagname.equalsIgnoreCase("NOMBRESUSUARIOS")) {
                                        usuarios = usuarios + text;
                                    } else if (tagname.equalsIgnoreCase("FOTODEFAULT1BASE64")) {
                                        fotodefault1 = text;
                                    } else if (tagname.equalsIgnoreCase("FOTODEFAULT2BASE64")) {
                                        fotodefault2 = text;
                                    } else if (tagname.equalsIgnoreCase("FOTODEFAULT3BASE64")) {
                                        fotodefault3 = text;
                                    } else if (tagname.equalsIgnoreCase("FOTODEFAULT4BASE64")) {
                                        fotodefault4 = text;
                                    } else if (tagname.equalsIgnoreCase("FOTODEFAULT5BASE64")) {
                                        fotodefault5 = text;
                                    } else if (tagname.equalsIgnoreCase("FOTODEFAULT6BASE64")) {
                                        fotodefault6 = text;
                                    } else if (tagname.equalsIgnoreCase("FOTODEFAULT7BASE64")) {
                                        fotodefault7 = text;
                                    } else if (tagname.equalsIgnoreCase("FOTODEFAULT8BASE64")) {
                                        fotodefault8 = text;
                                    } else if (tagname.equalsIgnoreCase("FOTODEFAULT9BASE64")) {
                                        fotodefault9 = text;
                                    } else if (tagname.equalsIgnoreCase("FOTODEFAULT10BASE64")) {
                                        fotodefault10 = text;
                                    }
                                    break;

                                default:
                                    break;
                            }
                            eventType = xpp.next();
                        }

                    } catch (XmlPullParserException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                        finish();
                    } finally {
                    }
                }
                /*--------------------------------------------------------------------------------*/
            }

            resultString = "OK";
            return resultString;
        }
    }
}
