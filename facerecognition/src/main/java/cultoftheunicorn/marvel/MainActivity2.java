package cultoftheunicorn.marvel;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

import cultoftheunicorn.marvel.dao.CheckInCheckOutDAO;
import cultoftheunicorn.marvel.dao.EmpleadoDAO;
import cultoftheunicorn.marvel.dao.FotodefaultDAO;
import cultoftheunicorn.marvel.dao.ProyectoDAO;
import cultoftheunicorn.marvel.dao.UsuarioDAO;
import cultoftheunicorn.marvel.modelo.Empleado;
import cultoftheunicorn.marvel.modelo.FotoDefault;
import cultoftheunicorn.marvel.modelo.Proyecto;
import cultoftheunicorn.marvel.modelo.Usuario;

import java.util.*;

import static java.lang.Math.abs;


public class MainActivity2 extends AppCompatActivity {
    // Declaring layout button, edit texts
    public static final int SIGNATURE_ACTIVITY = 1;
    String TAG = "Response";
    String resultString;
    String stringProyectos;
    String XMLFILEEXIST;
    int TIMEOUT = 0;

    Button login;
    ImageButton syncbutton;
    EditText username, password;
    ProgressBar progressBar;
    // End Declaring layout button, edit texts

    // Declaring connection variables
    Connection con;
    String un, pass, db, ip, usuarioid, nombreusuario, numestacion, modoremoto;

    String usr, psw;
    //End Declaring connection variables

    //preubas esto se asignara con la consulta//
    String lista1 = "";
    String lista2 = "";
    private List<Empleado> mListEmpleado;
    String NOMBREEMP, CODIGOEMPLEADO, DOMICILIO, CIUDAD, TELEFONO, CUENTACONTABLE, FECHAALTA, FECHABAJA,
            IMSS, STATUS, REGISTRO, SYNCRONIZADO;

    String CHECKPROYECTOID, CHECKEMPLEADOID, CHECKFECHA, CHECKCHECKS, CHECKCHECKINHECHO;
    long IDESTACION, TURNO, IDDISPOSITIVO, IDPROYECTO;

    private List<Usuario> mListUsuario;
    private List<FotoDefault> mListFotoDefault;
    private List<Proyecto> mListProyecto;

    String IMAGE1, IMAGE2, IMAGE3, IMAGE4, IMAGE5, IMAGE6, IMAGE7, IMAGE8, IMAGE9, IMAGE10;
    String IMAGE11, IMAGE12, IMAGE13, IMAGE14, IMAGE15, IMAGE16, IMAGE17, IMAGE18, IMAGE19, IMAGE20;
    String IMAGE21, IMAGE22, IMAGE23, IMAGE24, IMAGE25, IMAGE26, IMAGE27, IMAGE28, IMAGE29, IMAGE30;
    String IMAGE31, IMAGE32, IMAGE33, IMAGE34, IMAGE35, IMAGE36, IMAGE37, IMAGE38, IMAGE39, IMAGE40;
    String IMAGE41, IMAGE42, IMAGE43, IMAGE44, IMAGE45, IMAGE46, IMAGE47, IMAGE48, IMAGE49, IMAGE50;


    String NOMBRESEMPLEADOS, NOMBRESUSUARIOS;
    String IMAGEDEFAULT1, IMAGEDEFAULT2, IMAGEDEFAULT3, IMAGEDEFAULT4, IMAGEDEFAULT5, IMAGEDEFAULT6,
           IMAGEDEFAULT7, IMAGEDEFAULT8, IMAGEDEFAULT9, IMAGEDEFAULT10;


    public static long roundUp(long num, long divisor) {
        int sign = (num > 0 ? 1 : -1) * (divisor > 0 ? 1 : -1);
        return sign * (abs(num) + abs(divisor) - 1) / abs(divisor);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
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

        ip = prefs.getString("ipservidor", "192.168.0.6");
        db = prefs.getString("dbservidor", "GAUSS_VER1.1");
        un = prefs.getString("usuarioservidor", "sa");
        pass = prefs.getString("passwordservidor", "Cistem32");
        numestacion = prefs.getString("numeroestacion", "2601");
        modoremoto = prefs.getString("modoremoto", "SI");
        TIMEOUT =  Integer.parseInt(prefs.getString("timeout", "50000"));

        if (modoremoto.equals("NO")) {
            syncbutton.setVisibility(View.VISIBLE);
        } else {
            syncbutton.setVisibility(View.INVISIBLE);
        }

        // Setting up the function when button login is clicked
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckLogin checkLogin = new CheckLogin();// this is the Asynctask, which is used to process in background to reduce load on app process
                checkLogin.execute("");
            }
        });
        //End Setting up the function when button login is clicked

        // funcion de syncronizado
        syncbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Synconizar();
            }
        });
        // fin de la funcion de syncroniado-------------------------------------------------------//
    }

    public void delay(int i){
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // Do something after 5s = 5000ms
                //buttons[inew][jnew].setBackgroundColor(Color.BLACK);
            }
        }, i);
    }

    private void procesaXML()
    {
        try {

            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            XmlPullParser xpp = factory.newPullParser();


            File file = new File(Environment.getExternalStorageDirectory()+"/facerecogOCV/", "datosxml.xml");
            file.mkdirs();
            InputStream in_s = new FileInputStream(new File(Environment.getExternalStorageDirectory()+"/facerecogOCV/", "datosxml.xml"));

            xpp.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            xpp.setInput(in_s, null);

            xpp.next();
            int eventType = xpp.getEventType();

            int c = 0;

            IMAGE1 = ""; IMAGE11 = ""; IMAGE21 = ""; IMAGE31 = ""; IMAGE41 = "";
            IMAGE2 = ""; IMAGE12 = ""; IMAGE22 = ""; IMAGE32 = ""; IMAGE42 = "";
            IMAGE3 = ""; IMAGE13 = ""; IMAGE23 = ""; IMAGE33 = ""; IMAGE43 = "";
            IMAGE4 = ""; IMAGE14 = ""; IMAGE24 = ""; IMAGE34 = ""; IMAGE44 = "";
            IMAGE5 = ""; IMAGE15 = ""; IMAGE25 = ""; IMAGE35 = ""; IMAGE45 = "";
            IMAGE6 = ""; IMAGE16 = ""; IMAGE26 = ""; IMAGE36 = ""; IMAGE46 = "";
            IMAGE7 = ""; IMAGE17 = ""; IMAGE27 = ""; IMAGE37 = ""; IMAGE47 = "";
            IMAGE8 = ""; IMAGE18 = ""; IMAGE28 = ""; IMAGE38 = ""; IMAGE48 = "";
            IMAGE9 = ""; IMAGE19 = ""; IMAGE29 = ""; IMAGE39 = ""; IMAGE49 = "";
            IMAGE10 = ""; IMAGE20 = ""; IMAGE30 = ""; IMAGE40 = ""; IMAGE50 = "";

            NOMBRESEMPLEADOS = "";
            NOMBRESUSUARIOS = "";

            String text = "";
            String empleados = "";
            String usuarios = "";

            String foto1 = ""; String foto2 = ""; String foto3 = ""; String foto4 = ""; String foto5 = "";
            String foto6 = ""; String foto7 = ""; String foto8 = ""; String foto9 = ""; String foto10 = "";
            String foto11 = ""; String foto12 = ""; String foto13 = ""; String foto14 = ""; String foto15 = "";
            String foto16 = ""; String foto17 = ""; String foto18 = ""; String foto19 = ""; String foto20 = "";
            String foto21 = ""; String foto22 = ""; String foto23 = ""; String foto24 = ""; String foto25 = "";
            String foto26 = ""; String foto27 = ""; String foto28 = ""; String foto29 = ""; String foto30 = "";
            String foto31 = ""; String foto32 = ""; String foto33 = ""; String foto34 = ""; String foto35 = "";
            String foto36 = ""; String foto37 = ""; String foto38 = ""; String foto39 = ""; String foto40 = "";
            String foto41 = ""; String foto42 = ""; String foto43 = ""; String foto44 = ""; String foto45 = "";
            String foto46 = ""; String foto47 = ""; String foto48 = ""; String foto49 = ""; String foto50 = "";

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
                            NOMBRESUSUARIOS = usuarios;

                            IMAGE1 = foto1; IMAGE11 = foto11; IMAGE21 = foto21; IMAGE31 = foto31; IMAGE41 = foto41;
                            IMAGE2 = foto2; IMAGE12 = foto12; IMAGE22 = foto22; IMAGE32 = foto32; IMAGE42 = foto42;
                            IMAGE3 = foto3; IMAGE13 = foto13; IMAGE23 = foto23; IMAGE33 = foto33; IMAGE43 = foto43;
                            IMAGE4 = foto4; IMAGE14 = foto14; IMAGE24 = foto24; IMAGE34 = foto34; IMAGE44 = foto44;
                            IMAGE5 = foto5; IMAGE15 = foto15; IMAGE25 = foto25; IMAGE35 = foto35; IMAGE45 = foto45;
                            IMAGE6 = foto6; IMAGE16 = foto16; IMAGE26 = foto26; IMAGE36 = foto36; IMAGE46 = foto46;
                            IMAGE7 = foto7; IMAGE17 = foto17; IMAGE27 = foto27; IMAGE37 = foto37; IMAGE47 = foto47;
                            IMAGE8 = foto8; IMAGE18 = foto18; IMAGE28 = foto28; IMAGE38 = foto38; IMAGE48 = foto48;
                            IMAGE9 = foto9; IMAGE19 = foto19; IMAGE29 = foto29; IMAGE39 = foto39; IMAGE49 = foto49;
                            IMAGE10 = foto10; IMAGE20 = foto20; IMAGE30 = foto30; IMAGE40 = foto40; IMAGE50 = foto50;
                            c++;

                        } else if (tagname.equalsIgnoreCase("NOMBRESEMPLEADOS")) {
                            empleados = empleados + text;
                        } else if (tagname.equalsIgnoreCase("NOMBRESUSUARIOS")) {
                            usuarios = usuarios + text;
                        } else if (tagname.equalsIgnoreCase("FOTO1EMPLEADO1BASE64")) {
                            foto1 = text;
                        } else if (tagname.equalsIgnoreCase("FOTO2EMPLEADO1BASE64")) {
                            foto2 = text;
                        } else if (tagname.equalsIgnoreCase("FOTO3EMPLEADO1BASE64")) {
                            foto3 = text;
                        } else if (tagname.equalsIgnoreCase("FOTO4EMPLEADO1BASE64")) {
                            foto4 = text;
                        } else if (tagname.equalsIgnoreCase("FOTO5EMPLEADO1BASE64")) {
                            foto5 = text;
                        } else if (tagname.equalsIgnoreCase("FOTO6EMPLEADO1BASE64")) {
                            foto6 = text;
                        } else if (tagname.equalsIgnoreCase("FOTO7EMPLEADO1BASE64")) {
                            foto7 = text;
                        } else if (tagname.equalsIgnoreCase("FOTO8EMPLEADO1BASE64")) {
                            foto8 = text;
                        } else if (tagname.equalsIgnoreCase("FOTO9EMPLEADO1BASE64")) {
                            foto9 = text;
                        } else if (tagname.equalsIgnoreCase("FOTO10EMPLEADO1BASE64")) {
                            foto10 = text;

                        }else if (tagname.equalsIgnoreCase("FOTO1EMPLEADO2BASE64")) {
                            foto11 = text;
                        } else if (tagname.equalsIgnoreCase("FOTO2EMPLEADO2BASE64")) {
                            foto12 = text;
                        } else if (tagname.equalsIgnoreCase("FOTO3EMPLEADO2BASE64")) {
                            foto13 = text;
                        } else if (tagname.equalsIgnoreCase("FOTO4EMPLEADO2BASE64")) {
                            foto14 = text;
                        } else if (tagname.equalsIgnoreCase("FOTO5EMPLEADO2BASE64")) {
                            foto15 = text;
                        } else if (tagname.equalsIgnoreCase("FOTO6EMPLEADO2BASE64")) {
                            foto16 = text;
                        } else if (tagname.equalsIgnoreCase("FOTO7EMPLEADO2BASE64")) {
                            foto17 = text;
                        } else if (tagname.equalsIgnoreCase("FOTO8EMPLEADO2BASE64")) {
                            foto18 = text;
                        } else if (tagname.equalsIgnoreCase("FOTO9EMPLEADO2BASE64")) {
                            foto19 = text;
                        } else if (tagname.equalsIgnoreCase("FOTO10EMPLEADO2BASE64")) {
                            foto20 = text;

                        } else if (tagname.equalsIgnoreCase("FOTO1EMPLEADO3BASE64")) {
                            foto21 = text;
                        } else if (tagname.equalsIgnoreCase("FOTO2EMPLEADO3BASE64")) {
                            foto22 = text;
                        } else if (tagname.equalsIgnoreCase("FOTO3EMPLEADO3BASE64")) {
                            foto23 = text;
                        } else if (tagname.equalsIgnoreCase("FOTO4EMPLEADO3BASE64")) {
                            foto24 = text;
                        } else if (tagname.equalsIgnoreCase("FOTO5EMPLEADO3BASE64")) {
                            foto25 = text;
                        } else if (tagname.equalsIgnoreCase("FOTO6EMPLEADO3BASE64")) {
                            foto26 = text;
                        } else if (tagname.equalsIgnoreCase("FOTO7EMPLEADO3BASE64")) {
                            foto27 = text;
                        } else if (tagname.equalsIgnoreCase("FOTO8EMPLEADO3BASE64")) {
                            foto28 = text;
                        } else if (tagname.equalsIgnoreCase("FOTO9EMPLEADO3BASE64")) {
                            foto29 = text;
                        } else if (tagname.equalsIgnoreCase("FOTO10EMPLEADO3BASE64")) {
                            foto30 = text;

                        } else if (tagname.equalsIgnoreCase("FOTO1EMPLEADO4BASE64")) {
                            foto31 = text;
                        } else if (tagname.equalsIgnoreCase("FOTO2EMPLEADO4BASE64")) {
                            foto32 = text;
                        } else if (tagname.equalsIgnoreCase("FOTO3EMPLEADO4BASE64")) {
                            foto33 = text;
                        } else if (tagname.equalsIgnoreCase("FOTO4EMPLEADO4BASE64")) {
                            foto34 = text;
                        } else if (tagname.equalsIgnoreCase("FOTO5EMPLEADO4BASE64")) {
                            foto35 = text;
                        } else if (tagname.equalsIgnoreCase("FOTO6EMPLEADO4BASE64")) {
                            foto36 = text;
                        } else if (tagname.equalsIgnoreCase("FOTO7EMPLEADO4BASE64")) {
                            foto37 = text;
                        } else if (tagname.equalsIgnoreCase("FOTO8EMPLEADO4BASE64")) {
                            foto38 = text;
                        } else if (tagname.equalsIgnoreCase("FOTO9EMPLEADO4BASE64")) {
                            foto39 = text;
                        } else if (tagname.equalsIgnoreCase("FOTO10EMPLEADO4BASE64")) {
                            foto40 = text;

                        } else if (tagname.equalsIgnoreCase("FOTO1EMPLEADO5BASE64")) {
                            foto41 = text;
                        } else if (tagname.equalsIgnoreCase("FOTO2EMPLEADO5BASE64")) {
                            foto42 = text;
                        } else if (tagname.equalsIgnoreCase("FOTO3EMPLEADO5BASE64")) {
                            foto43 = text;
                        } else if (tagname.equalsIgnoreCase("FOTO4EMPLEADO5BASE64")) {
                            foto44 = text;
                        } else if (tagname.equalsIgnoreCase("FOTO5EMPLEADO5BASE64")) {
                            foto45 = text;
                        } else if (tagname.equalsIgnoreCase("FOTO6EMPLEADO5BASE64")) {
                            foto46 = text;
                        } else if (tagname.equalsIgnoreCase("FOTO7EMPLEADO5BASE64")) {
                            foto47 = text;
                        } else if (tagname.equalsIgnoreCase("FOTO8EMPLEADO5BASE64")) {
                            foto48 = text;
                        } else if (tagname.equalsIgnoreCase("FOTO9EMPLEADO5BASE64")) {
                            foto49 = text;
                        } else if (tagname.equalsIgnoreCase("FOTO10EMPLEADO5BASE64")) {
                            foto50 = text;
                        }
                        break;

                    default:
                        break;
                }
                eventType = xpp.next();
            }

        } catch (XmlPullParserException e) {
            e.printStackTrace();
            System.out.println("error aqui xmlpullparserexception");
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("error aqui IOException");
            finish();
        } finally {
            resultString = "OK";
        }
    }

    private void savetextasfile(String filename, String content) throws IOException {
        String Filename = filename + ".xml";

        //crea archivo
        File file = new File(Environment.getExternalStorageDirectory()+"/facerecogOCV/", Filename);
        //file.mkdirs();

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file);
            fos.write(content.getBytes());
            fos.flush();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            XMLFILEEXIST = "OK";
        }
    }

    public String Synconizar() {
        Calendar cal = Calendar.getInstance();
        long StartTime= System.currentTimeMillis();
        long currentTime =StartTime;

        String result = "";
        String[] parts1= new String[0];
        String[] parts2= new String[0];
        resultString = "";

        //obtener listado local de usuario, empleados, fotodefault abrir bases de datos local
        EmpleadoDAO empleado = new EmpleadoDAO(this);
        FotodefaultDAO fotodefault = new FotodefaultDAO(this);
        UsuarioDAO usuario = new UsuarioDAO(this);
        ProyectoDAO proyecto = new ProyectoDAO(this);

        mListEmpleado = empleado.getAllEmpleado();
        mListFotoDefault = fotodefault.getAllFotoDefault();
        mListUsuario = usuario.getAllUsuario();
        mListProyecto = proyecto.getAllProyecto();

        //LISTA DE EMPLEADOS-----------------------------------------------------------------///////
        lista1 = "";
        if (mListEmpleado.size() > 0)
        {
            lista1 = lista1+mListEmpleado.get(0).getNombre().toString();
            for (int z=1; z<=mListEmpleado.size()-1;z++)
            {
                lista1 = lista1 + ",";
                lista1 = lista1 + mListEmpleado.get(z).getNombre().toString();
            }
        }
        //////////////////------------------------------------------------------------------////////
        //LISTA DE USUARIOS----------------------------------------------------------------/////////
        lista2 = "";
        if (mListUsuario.size() > 0)
        {
            lista2 = lista2+ mListUsuario.get(0).getNombre().toString();
            for (int z=1; z<=mListUsuario.size()-1;z++)
            {
                lista2 = lista2 + ",";
                lista2 = lista2 + mListUsuario.get(z).getNombre().toString();
            }
        }
        //////////////////-----------------------------------------------------------------/////////

        //obtener elementos a syncronizar de forma local de base de datos remota------------////////
        ////////////////////////////////////////////////////////////////////////////////////////////
        //lista1 = "'Javier Soto', 'Omar Luna'";
        if (lista1.equals("")) lista1 = "'Pedro Ejemplo', 'Pedro Ejemplo'";
        if (lista2.equals("")) lista2 = "'Pedro Ejemplo', 'Pedro Ejemplo'";
        //lista2 = "'Javier Soto', 'Omar Luna'";
        ////////////////////////////////////////////////////////////////////////////////////////////
        syncronization syncroniza = new syncronization();
        syncroniza.execute("");
        ////////////////////////////////////////////////////////////////////////////////////////////
        //agregar ciclo con timeout---------------------------------------------------------////////
        ////////////////////////////////////////////////////////////////////////////////////////////
        cal = Calendar.getInstance();
        StartTime= System.currentTimeMillis();
        currentTime =StartTime;
        currentTime = cal.getTimeInMillis();

        while ((currentTime<(StartTime + TIMEOUT))&& !resultString.equals("OK"))
        {
            System.out.println("Start time is "+StartTime);
            System.out.println("Current time is "+currentTime);
            System.out.println("Timer will has stopped");
            resultString = resultString;
            currentTime++;
        }
        if (resultString.equals("NOT OK") || resultString.equals("")) {
            resultString = "ERROR.- REVISE SU CONECCION INALAMBRICA, O CONECCION CON INTERNET";
            result = "ERROR.- REVISE SU CONECCION INALAMBRICA, O CONECCION CON INTERNET";
            return result;
        }
        ////////////////////////////////////////////////////////////////////////////////////////////////////////////
        //realizar  guardado en la base de datos local del dispositivo----------------------////////////////////////
        //FOTODEFAULT-----------------------------------------------------------------------////////////////////////
        if (IMAGEDEFAULT1!=null && mListFotoDefault.size() <= 0) {
            FotoDefault createdfotodefault = fotodefault.createFotoDefault(Base64.decode(IMAGEDEFAULT1, Base64.DEFAULT),
                    Base64.decode(IMAGEDEFAULT2, Base64.DEFAULT),
                    Base64.decode(IMAGEDEFAULT3, Base64.DEFAULT),
                    Base64.decode(IMAGEDEFAULT4, Base64.DEFAULT),
                    Base64.decode(IMAGEDEFAULT5, Base64.DEFAULT),
                    Base64.decode(IMAGEDEFAULT6, Base64.DEFAULT),
                    Base64.decode(IMAGEDEFAULT7, Base64.DEFAULT),
                    Base64.decode(IMAGEDEFAULT8, Base64.DEFAULT),
                    Base64.decode(IMAGEDEFAULT9, Base64.DEFAULT),
                    Base64.decode(IMAGEDEFAULT10, Base64.DEFAULT));
            Log.d(TAG, "Foto default agregada");
            Toast.makeText(MainActivity2.this, "Foto default agregada", Toast.LENGTH_SHORT).show();
        }
        //USUARIO---------------------------------------------------------------------------////////////////////////
        if (NOMBRESUSUARIOS!= null && NOMBRESUSUARIOS!= "") {
            parts1 = NOMBRESUSUARIOS.split("/");
            if (parts1.length > 0) {
                for (int a = 0; a < parts1.length; a++) {
                    parts2 = parts1[a].split("@");

                    Usuario createdusuario = usuario.createUsuario("",
                            parts2[0],
                            parts2[1],
                            parts2[2],
                            Base64.decode("", Base64.DEFAULT),
                            "");
                }
            }
        }
        ////////////////////////////////////////////////////////////////////////////////////////////////////////////
        //formatear cadena
        if (NOMBRESEMPLEADOS!=null && NOMBRESEMPLEADOS!= "") {
            parts1 = NOMBRESEMPLEADOS.split("/");
            int size = parts1.length;

            long veces1 = roundUp(size, 5);
            //long veces1 = 2;


            for (int i = 1; i <= veces1; i++) {
                int veces2 = i * 5;
                int r = 0;

                if (i == 1) r = 1;
                if (i > 1) r = (1) + ((i - 1) * 5);

                NOMBRESEMPLEADOS = "";
                NOMBRESEMPLEADOS = NOMBRESEMPLEADOS + "'" + parts1[r - 1] + "'";

                for (int j = r; j <= veces2 - 1; j++) {

                    if (j < parts1.length) {
                        NOMBRESEMPLEADOS = NOMBRESEMPLEADOS + ",";
                        NOMBRESEMPLEADOS = NOMBRESEMPLEADOS + "'" + parts1[j] + "'";
                    }
                }
                lista1 = NOMBRESEMPLEADOS;
                parts2 = NOMBRESEMPLEADOS.split(",");

                XMLFILEEXIST = "NO";
                syncronization10rostros5empleados syncroniza10rostros5empleados = new syncronization10rostros5empleados();
                syncroniza10rostros5empleados.execute();
                ////////////////////////////////////////////////////////////////////////////////////////////////////////
                //agregar ciclo con timeout---------------------------------------------------------////////////////////
                ////////////////////////////////////////////////////////////////////////////////////////////////////////
                cal = Calendar.getInstance();
                StartTime = System.currentTimeMillis();
                currentTime = StartTime;
                currentTime = cal.getTimeInMillis();

                System.out.println(resultString);
                while (!XMLFILEEXIST.equals("OK")) {
                    System.out.println("Start time is " + StartTime);
                    System.out.println("Current time is " + currentTime);
                    System.out.println("--------------------------------------");
                    System.out.println("Timer will has stopped");
                    resultString = resultString;
                    currentTime++;
                }
                if (resultString.equals("NOT OK")) {
                    resultString = "ERROR.- REVISE SU CONECCION INALAMBRICA, O CONECCION CON INTERNET";
                    result = "ERROR.- REVISE SU CONECCION INALAMBRICA, O CONECCION CON INTERNET";
                    return result;
                }
                //PROCESAR ARCHIVO XML CON LOS 5 EMPLEDOS Y SUS 10 IMAGENES-------------------------////////////////////
                procesaXML();
                ////////////////////////////////////////////////////////////////////////////////////////////////////////
                //realizar guardado en la base de datos local del dispositivo-----------------------////////////////////
                //IMAGENEMPLEADO1
                if (0 < parts2.length) {
                    Empleado createdempleado1 = empleado.createEmpleado(parts2[0].toString().replace("'",""), "", "", "", "", "", "", "", "", "", Base64.decode(IMAGE1, Base64.DEFAULT),
                            2601, 1, Base64.decode(IMAGE1, Base64.DEFAULT),
                            Base64.decode(IMAGE2, Base64.DEFAULT),
                            Base64.decode(IMAGE3, Base64.DEFAULT),
                            Base64.decode(IMAGE4, Base64.DEFAULT),
                            Base64.decode(IMAGE5, Base64.DEFAULT),
                            Base64.decode(IMAGE6, Base64.DEFAULT),
                            Base64.decode(IMAGE7, Base64.DEFAULT),
                            Base64.decode(IMAGE8, Base64.DEFAULT),
                            Base64.decode(IMAGE9, Base64.DEFAULT),
                            Base64.decode(IMAGE10, Base64.DEFAULT), 1, "", "",1);
                }
                //IMAGENEMPLEADO2
                if (1 < parts2.length) {
                    Empleado createdempleado2 = empleado.createEmpleado(parts2[1].toString().replace("'",""), "", "", "", "", "", "", "", "", "", Base64.decode(IMAGE1, Base64.DEFAULT),
                            2601, 1, Base64.decode(IMAGE11, Base64.DEFAULT),
                            Base64.decode(IMAGE12, Base64.DEFAULT),
                            Base64.decode(IMAGE13, Base64.DEFAULT),
                            Base64.decode(IMAGE14, Base64.DEFAULT),
                            Base64.decode(IMAGE15, Base64.DEFAULT),
                            Base64.decode(IMAGE16, Base64.DEFAULT),
                            Base64.decode(IMAGE17, Base64.DEFAULT),
                            Base64.decode(IMAGE18, Base64.DEFAULT),
                            Base64.decode(IMAGE19, Base64.DEFAULT),
                            Base64.decode(IMAGE20, Base64.DEFAULT), 1, "", "",1);
                }
                //IMAGENEMPLEADO3
                if (2 < parts2.length) {
                    Empleado createdempleado3 = empleado.createEmpleado(parts2[2].toString().replace("'",""), "", "", "", "", "", "", "", "", "", Base64.decode(IMAGE1, Base64.DEFAULT),
                            2601, 1, Base64.decode(IMAGE21, Base64.DEFAULT),
                            Base64.decode(IMAGE22, Base64.DEFAULT),
                            Base64.decode(IMAGE23, Base64.DEFAULT),
                            Base64.decode(IMAGE24, Base64.DEFAULT),
                            Base64.decode(IMAGE25, Base64.DEFAULT),
                            Base64.decode(IMAGE26, Base64.DEFAULT),
                            Base64.decode(IMAGE27, Base64.DEFAULT),
                            Base64.decode(IMAGE28, Base64.DEFAULT),
                            Base64.decode(IMAGE29, Base64.DEFAULT),
                            Base64.decode(IMAGE30, Base64.DEFAULT), 1, "", "",1);
                }
                //IMAGENEMPLEADO4
                if (3 < parts2.length) {
                    Empleado createdempleado4 = empleado.createEmpleado(parts2[3].toString().replace("'",""), "", "", "", "", "", "", "", "", "", Base64.decode(IMAGE1, Base64.DEFAULT),
                            2601, 1, Base64.decode(IMAGE31, Base64.DEFAULT),
                            Base64.decode(IMAGE32, Base64.DEFAULT),
                            Base64.decode(IMAGE33, Base64.DEFAULT),
                            Base64.decode(IMAGE34, Base64.DEFAULT),
                            Base64.decode(IMAGE35, Base64.DEFAULT),
                            Base64.decode(IMAGE36, Base64.DEFAULT),
                            Base64.decode(IMAGE37, Base64.DEFAULT),
                            Base64.decode(IMAGE38, Base64.DEFAULT),
                            Base64.decode(IMAGE39, Base64.DEFAULT),
                            Base64.decode(IMAGE40, Base64.DEFAULT), 1, "", "",1);
                }
                //IMAGENEMPLEADO5
                if (4 < parts2.length) {
                    Empleado createdempleado5 = empleado.createEmpleado(parts2[4].toString().replace("'",""), "", "", "", "", "", "", "", "", "", Base64.decode(IMAGE1, Base64.DEFAULT),
                            2601, 1, Base64.decode(IMAGE41, Base64.DEFAULT),
                            Base64.decode(IMAGE42, Base64.DEFAULT),
                            Base64.decode(IMAGE43, Base64.DEFAULT),
                            Base64.decode(IMAGE44, Base64.DEFAULT),
                            Base64.decode(IMAGE45, Base64.DEFAULT),
                            Base64.decode(IMAGE46, Base64.DEFAULT),
                            Base64.decode(IMAGE47, Base64.DEFAULT),
                            Base64.decode(IMAGE48, Base64.DEFAULT),
                            Base64.decode(IMAGE49, Base64.DEFAULT),
                            Base64.decode(IMAGE50, Base64.DEFAULT), 1, "", "",1);
                }
            }
        }
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//////////---SYNCRONIZADO DE TABLA PROYECTO REMOTO A LOCAL----------------------------------------////////////////////////////////////////////////////////////
        syncronizationProyectos sincronizaproyectos = new syncronizationProyectos();
        sincronizaproyectos.execute();

        Proyecto createProyecto = proyecto.createProyecto("", 0);
        proyecto.deleteProyecto(createProyecto);

////////////////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////////
///////// agregar ciclo con timeout---------------------------------------------------------////////
        while (stringProyectos.trim().toString() == "")
        {
            parts1 = stringProyectos.split(",");
        }

        if (stringProyectos.trim().toString() != "") {
            parts1 = stringProyectos.split(",");
        }
////////////////////////////////////////////////////////////////////////////////////////////////////
        for (int z=0; z<=parts1.length - 1; z++){
            parts2 = parts1[z].split("@");
            createProyecto = proyecto.createProyecto(parts2[1].toString(), Long.parseLong(parts2[0].toString()));
        }
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//////////---PROCESO PARA GUARDAR EN BASE DE DATOS REMOTA EMPLEADOS DADOS DE ALTA EN DISPOSITIVO--////////////////////////////////////////////////////////////
        mListEmpleado = empleado.getAllEmpleado();

        //LISTA DE EMPLEADOS-----------------------------------------------------------------///////
        lista1 = "";
        if (mListEmpleado.size() > 0)
        {
            for (int z=1; z<=mListEmpleado.size()-1;z++)
            {
                if ((mListEmpleado.get(z).getSyncronizado().toString().equals("NO"))&&
                        (mListEmpleado.get(z).getRegistro().toString().equals("LOCAL")))
                {
                    String foto = "";
                    byte[] fotoemp;

                    NOMBREEMP   = mListEmpleado.get(z).getNombre().toString();
                    fotoemp     = mListEmpleado.get(z).getFotoEmpleado1();
                    //IDEMPLEADO[x] = Long.toString(mListEmpleado.get(z));
                    CODIGOEMPLEADO = mListEmpleado.get(z).getCodigoEmpleado();
                    DOMICILIO = mListEmpleado.get(z).getDomicilio();
                    CIUDAD = mListEmpleado.get(z).getCiudad();
                    TELEFONO = mListEmpleado.get(z).getTelefono();
                    CUENTACONTABLE = mListEmpleado.get(z).getCuentaContable();
                    FECHAALTA = mListEmpleado.get(z).getFechaAlta();
                    FECHABAJA = mListEmpleado.get(z).getFechaBaja();
                    IMSS = mListEmpleado.get(z).getImss();
                    STATUS = mListEmpleado.get(z).getStatus();
                    IDESTACION = mListEmpleado.get(z).getIdEstacion();
                    TURNO = mListEmpleado.get(z).getTurno();
                    IDDISPOSITIVO = mListEmpleado.get(z).getIdDispositivo();
                    REGISTRO = mListEmpleado.get(z).getRegistro();
                    SYNCRONIZADO = mListEmpleado.get(z).getSyncronizado();
                    IDPROYECTO = mListEmpleado.get(z).getIdproyecto();

                    IMAGE1 = Base64.encodeToString(fotoemp, Base64.DEFAULT);
                    fotoemp     = mListEmpleado.get(z).getFotoEmpleado2();
                    IMAGE2 = Base64.encodeToString(fotoemp, Base64.DEFAULT);
                    fotoemp     = mListEmpleado.get(z).getFotoEmpleado3();
                    IMAGE3 = Base64.encodeToString(fotoemp, Base64.DEFAULT);
                    fotoemp     = mListEmpleado.get(z).getFotoEmpleado4();
                    IMAGE4 = Base64.encodeToString(fotoemp, Base64.DEFAULT);
                    fotoemp     = mListEmpleado.get(z).getFotoEmpleado5();
                    IMAGE5 = Base64.encodeToString(fotoemp, Base64.DEFAULT);
                    fotoemp     = mListEmpleado.get(z).getFotoEmpleado6();
                    IMAGE6 = Base64.encodeToString(fotoemp, Base64.DEFAULT);
                    fotoemp     = mListEmpleado.get(z).getFotoEmpleado7();
                    IMAGE7 = Base64.encodeToString(fotoemp, Base64.DEFAULT);
                    fotoemp     = mListEmpleado.get(z).getFotoEmpleado8();
                    IMAGE8 = Base64.encodeToString(fotoemp, Base64.DEFAULT);
                    fotoemp     = mListEmpleado.get(z).getFotoEmpleado9();
                    IMAGE9 = Base64.encodeToString(fotoemp, Base64.DEFAULT);
                    fotoemp     = mListEmpleado.get(z).getFotoEmpleado10();
                    IMAGE10 = Base64.encodeToString(fotoemp, Base64.DEFAULT);

                    //ALIMENTA REGISTROS PARA GUARDADO EN BD REMOTA///////////////////////////////////////////////////////////////////////////////////////////
                    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
                    syncronizationEmpleadosRemoto syncEmpleadosRemoto = new syncronizationEmpleadosRemoto();
                    syncEmpleadosRemoto.execute();
                    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
                    //agregar ciclo con timeout---------------------------------------------------------//////////////////////////////////////////////////////
                    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
                    cal = Calendar.getInstance();
                    StartTime = System.currentTimeMillis();
                    currentTime = StartTime;
                    currentTime = cal.getTimeInMillis();

                    System.out.println(resultString);
                    while (!resultString.equals("OK")) {
                        System.out.println("Start time is " + StartTime);
                        System.out.println("Current time is " + currentTime);
                        System.out.println("--------------------------------------");
                        System.out.println("Timer will has stopped");
                        resultString = resultString;
                        currentTime++;
                    }
                    if (resultString.equals("NOT OK")) {
                        resultString = "ERROR.- REVISE SU CONECCION INALAMBRICA, O CONECCION CON INTERNET";
                        result = "ERROR.- REVISE SU CONECCION INALAMBRICA, O CONECCION CON INTERNET";
                        return result;
                    }
                    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
                    //MODIFICA REGISTRO QUE FUE AGREGADO A BASE DE DATOS REMOTA-------------------////////////////////////////////////////////////////////////
                    empleado.updateEmployee(empleado, mListEmpleado.get(z).getId());
                    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
                }
            }
        }
//////////----------------------------------------------------------------------------------------////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//////////------------------PROCESO PARA GUARDAR EN BASE DE DATOS REMOTA TABLA CHECKINCHECKOUT----////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        CheckInCheckOutDAO checkincheckout = new CheckInCheckOutDAO(this);
        Cursor cursor = checkincheckout.getAllCheckInCheckOutX();

        cursor.moveToFirst();

        while(!cursor.isAfterLast()) {
            CHECKPROYECTOID = cursor.getString(0);
            CHECKEMPLEADOID = cursor.getString(1);
            CHECKCHECKS = cursor.getString(2);
            CHECKFECHA = cursor.getString(3);
            CHECKCHECKINHECHO = cursor.getString(4);

//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
            //ALIMENTA REGISTROS PARA GUARDADO EN BD REMOTA///////////////////////////////////////////////////////////////////////////////////////////
            //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
            syncronizationCheckInCheckOutRemoto syncCheckInCheckOutRemoto = new syncronizationCheckInCheckOutRemoto();
            syncCheckInCheckOutRemoto.execute();
            //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
            //agregar ciclo con timeout---------------------------------------------------------//////////////////////////////////////////////////////
            //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
            cal = Calendar.getInstance();
            StartTime = System.currentTimeMillis();
            currentTime = StartTime;
            currentTime = cal.getTimeInMillis();

            System.out.println(resultString);
            while (!resultString.equals("OK")) {
                System.out.println("Start time is " + StartTime);
                System.out.println("Current time is " + currentTime);
                System.out.println("--------------------------------------");
                System.out.println("Timer will has stopped");
                resultString = resultString;
                currentTime++;
            }
            if (resultString.equals("NOT OK")) {
                resultString = "ERROR.- REVISE SU CONECCION INALAMBRICA, O CONECCION CON INTERNET";
                result = "ERROR.- REVISE SU CONECCION INALAMBRICA, O CONECCION CON INTERNET";
                return result;
            }
            cursor.moveToNext();
        }
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


        return result;
    }

    public String guardacheckincheckout() {
        String SOAP_ACTION = "urn:androidserviceIntf-Iandroidservice#registracheckincheckout";
        String METHOD_NAME = "registracheckincheckout";
        String NAMESPACE = "urn:androidserviceIntf";
        String URL = "http://"+ip+":1001/soap/Iandroidservice";
        String z;

        try {
            SoapObject Request = new SoapObject(NAMESPACE, METHOD_NAME);
            Request.addProperty("ProyectoID", CHECKPROYECTOID);
            Request.addProperty("EmpleadoID", CHECKEMPLEADOID);
            Request.addProperty("Checks", CHECKCHECKS);
            Request.addProperty("Fecha", CHECKFECHA);
            Request.addProperty("CheckInHecho", CHECKCHECKINHECHO);

            SoapSerializationEnvelope soapEnvelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            soapEnvelope.dotNet = true;
            soapEnvelope.setOutputSoapObject(Request);
            HttpTransportSE transport = new HttpTransportSE(URL, 80000);

            transport.call(SOAP_ACTION, soapEnvelope);

            //resultString = (SoapPrimitive) soapEnvelope.getResponse();
            Object  response = (Object) soapEnvelope.getResponse();
            resultString = response.toString();

            resultString = "OK";
            return resultString;

            //Log.i(TAG, "Result Celsius: " + resultString);
        } catch (Exception ex) {
            Log.e(TAG, "Error: " + ex.getMessage());
        }
        return SOAP_ACTION;
    }

    public String guardaempleadoRemotosSync() {
        String SOAP_ACTION = "urn:androidserviceIntf-Iandroidservice#registrapersonalrostro";
        String METHOD_NAME = "registrapersonalrostro";
        String NAMESPACE = "urn:androidserviceIntf";
        String URL = "http://"+ip+":1001/soap/Iandroidservice";
        String z;

        String nombre;
        boolean isSuccess;

        //----FECHA ACTUAL------------------------------------------------------------------------//
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        Date date = new Date();
        String fecha = dateFormat.format(date);
        if (FECHAALTA.equals("")) FECHAALTA = fecha;
        //-----variable para imagen base64--------------------------------------------------------//
        String encodedImage2;
        byte[] byteArray;
        Bitmap image = null;
        //----------------------------------------------------------------------------------------//

        nombre = getIntent().getStringExtra("name");
        try {
            SoapObject Request = new SoapObject(NAMESPACE, METHOD_NAME);
            Request.addProperty("Nombre", NOMBREEMP);
            Request.addProperty("CodigoEmpleado", CODIGOEMPLEADO);
            Request.addProperty("Domicilio", DOMICILIO);
            Request.addProperty("Ciudad", CIUDAD);
            Request.addProperty("Telefono", TELEFONO);
            Request.addProperty("CuentaContable", CUENTACONTABLE);
            Request.addProperty("Fecha", FECHAALTA);
            Request.addProperty("UsuarioID", "1");
            Request.addProperty("EstacionID", "2601");
            //---------------------obtener las 10 imagenes ya capturadas--------------------------//
            Request.addProperty("foto1base64", IMAGE1);
            Request.addProperty("foto2base64", IMAGE2);
            Request.addProperty("foto3base64", IMAGE3);
            Request.addProperty("foto4base64", IMAGE4);
            Request.addProperty("foto5base64", IMAGE5);
            Request.addProperty("foto6base64", IMAGE6);
            Request.addProperty("foto7base64", IMAGE7);
            Request.addProperty("foto8base64", IMAGE8);
            Request.addProperty("foto9base64", IMAGE9);
            Request.addProperty("foto10base64", IMAGE10);
            //------------------------------------------------------------------------------------//

            SoapSerializationEnvelope soapEnvelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            soapEnvelope.dotNet = true;
            soapEnvelope.setOutputSoapObject(Request);
            HttpTransportSE transport = new HttpTransportSE(URL, 80000);

            transport.call(SOAP_ACTION, soapEnvelope);

            //resultString = (SoapPrimitive) soapEnvelope.getResponse();
            Object  response = (Object) soapEnvelope.getResponse();
            resultString = response.toString();

            resultString = "OK";
            return resultString;

            //Log.i(TAG, "Result Celsius: " + resultString);
        } catch (Exception ex) {
            Log.e(TAG, "Error: " + ex.getMessage());
        }
        return SOAP_ACTION;
    }

    public String obtenProyectos() {
        String SOAP_ACTION = "urn:androidserviceIntf-Iandroidservice#obtenproyectos";
        String METHOD_NAME = "obtenproyectos";
        String NAMESPACE = "urn:androidserviceIntf";
        String URL = "http://" + ip + ":1001/soap/Iandroidservice";
        String z;
        boolean isSuccess;

        try {
            SoapObject Request = new SoapObject(NAMESPACE, METHOD_NAME);

            SoapSerializationEnvelope soapEnvelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            soapEnvelope.dotNet = true;
            soapEnvelope.setOutputSoapObject(Request);

            HttpTransportSE transport = new HttpTransportSE(URL, 80000);

            transport.call(SOAP_ACTION, soapEnvelope);
            //resultString = (SoapPrimitive) soapEnvelope.getResponse();
            Object response = (Object) soapEnvelope.getResponse();
            resultString = response.toString();

            return resultString;

            //Log.i(TAG, "Result Celsius: " + resultString);
        } catch (Exception ex) {
            Log.e(TAG, "Error: " + ex.getMessage());
        }
        return SOAP_ACTION;
    }

    public String obten10lista5syncronizage(String list1) {
        String SOAP_ACTION = "urn:androidserviceIntf-Iandroidservice#obten10rostros5empleados";
        String METHOD_NAME = "obten10rostros5empleados";
        String NAMESPACE = "urn:androidserviceIntf";
        String URL = "http://" + ip + ":1001/soap/Iandroidservice";
        String z;
        boolean isSuccess;

        try {
            SoapObject Request = new SoapObject(NAMESPACE, METHOD_NAME);
            Request.addProperty("listadosyncronizar", list1);

            SoapSerializationEnvelope soapEnvelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            soapEnvelope.dotNet = true;
            soapEnvelope.setOutputSoapObject(Request);

            HttpTransportSE transport = new HttpTransportSE(URL, 80000);

            transport.call(SOAP_ACTION, soapEnvelope);
            //resultString = (SoapPrimitive) soapEnvelope.getResponse();
            Object response = (Object) soapEnvelope.getResponse();
            resultString = response.toString();

            return resultString;

            //Log.i(TAG, "Result Celsius: " + resultString);
        } catch (Exception ex) {
            Log.e(TAG, "Error: " + ex.getMessage());
        }
        return SOAP_ACTION;
    }

    public String SyncronizaEmpleadosRemoto(String list1, String list2) {
        String SOAP_ACTION = "urn:androidserviceIntf-Iandroidservice#obtenListaSyncronizado";
        String METHOD_NAME = "obtenListaSyncronizado";
        String NAMESPACE = "urn:androidserviceIntf";
        String URL = "http://" + ip + ":1001/soap/Iandroidservice";
        String z;
        boolean isSuccess;

        try {
            SoapObject Request = new SoapObject(NAMESPACE, METHOD_NAME);
            Request.addProperty("listadosyncronizar1", list1);
            Request.addProperty("listadosyncronizar2", list2);

            SoapSerializationEnvelope soapEnvelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            soapEnvelope.dotNet = true;
            soapEnvelope.setOutputSoapObject(Request);

            HttpTransportSE transport = new HttpTransportSE(URL, 80000);

            transport.call(SOAP_ACTION, soapEnvelope);
            //resultString = (SoapPrimitive) soapEnvelope.getResponse();
            Object response = (Object) soapEnvelope.getResponse();
            resultString = response.toString();

            return resultString;

            //Log.i(TAG, "Result Celsius: " + resultString);
        } catch (Exception ex) {
            Log.e(TAG, "Error: " + ex.getMessage());
        }
        return SOAP_ACTION;
    }

    public String obtenlistasyncronizage(String list1, String list2) {
        String SOAP_ACTION = "urn:androidserviceIntf-Iandroidservice#obtenListaSyncronizado";
        String METHOD_NAME = "obtenListaSyncronizado";
        String NAMESPACE = "urn:androidserviceIntf";
        String URL = "http://" + ip + ":1001/soap/Iandroidservice";
        String z;
        boolean isSuccess;

        try {
            SoapObject Request = new SoapObject(NAMESPACE, METHOD_NAME);
            Request.addProperty("listadosyncronizar1", list1);
            Request.addProperty("listadosyncronizar2", list2);

            SoapSerializationEnvelope soapEnvelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            soapEnvelope.dotNet = true;
            soapEnvelope.setOutputSoapObject(Request);

            HttpTransportSE transport = new HttpTransportSE(URL, 80000);

            transport.call(SOAP_ACTION, soapEnvelope);
            //resultString = (SoapPrimitive) soapEnvelope.getResponse();
            Object response = (Object) soapEnvelope.getResponse();
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
        String URL = "http://" + ip + ":1001/soap/Iandroidservice";
        String z;
        boolean isSuccess;

        try {
            SoapObject Request = new SoapObject(NAMESPACE, METHOD_NAME);
            Request.addProperty("usr", usr);
            Request.addProperty("password", psw);

            SoapSerializationEnvelope soapEnvelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            soapEnvelope.dotNet = true;
            soapEnvelope.setOutputSoapObject(Request);

            HttpTransportSE transport = new HttpTransportSE(URL, 80000);

            transport.call(SOAP_ACTION, soapEnvelope);
            //resultString = (SoapPrimitive) soapEnvelope.getResponse();
            Object response = (Object) soapEnvelope.getResponse();
            resultString = response.toString();

            return resultString;

            //Log.i(TAG, "Result Celsius: " + resultString);
        } catch (Exception ex) {
            Log.e(TAG, "Error: " + ex.getMessage());
        }
        return SOAP_ACTION;
    }

    public String conectarLC(){
        String resultado = "-1";

        //obtener listado local de usuario
        UsuarioDAO usuario = new UsuarioDAO(this);
        mListUsuario = usuario.getAllUsuario();

        for (int i=0; i<=mListUsuario.size()-1;i++) {
            if (mListUsuario.get(i).getUserName().toString().equals(usr.toUpperCase()))
            {
                resultado= mListUsuario.get(i).getUserName().toString();
                if (mListUsuario.get(i).getUserPassword().toString().equals(psw.toUpperCase()))
                {
                    resultado= resultado + '@' + mListUsuario.get(i).getUserPassword().toString();
                    break;
                }
            }
        }

        return resultado;
    }

    public void onClickConfiguracion(View view) {
        Intent intent = new Intent(getApplicationContext(), SettingsActivity.class);
        startActivityForResult(intent, SIGNATURE_ACTIVITY);
    }

    public class CheckLogin extends AsyncTask<String, String, String> {
        String z = "";
        Boolean isSuccess = false;

        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(String r) {
            progressBar.setVisibility(View.GONE);
            Toast.makeText(MainActivity2.this, r, Toast.LENGTH_SHORT).show();
            if (isSuccess) {
                Toast.makeText(MainActivity2.this, "Login Exitoso", Toast.LENGTH_LONG).show();
                //finish();
            }
            if (!isSuccess) {
                Toast.makeText(MainActivity2.this, "Usuario o Clave incorrecta", Toast.LENGTH_LONG).show();
            }
        }

        @Override
        protected String doInBackground(String... params) {
            usr = username.getText().toString();
            psw = password.getText().toString();
            if (usr.trim().equals("") || psw.trim().equals(""))
                z = "Por favor ingrese usuario y password";
            else {

                if (modoremoto.equals("NO")) {
                    resultString = conectarLC();
                } else {
                    resultString = conectar();
                }

                if (!resultString.equals("-1")) {
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

    public class syncronization10rostros5empleados extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(String r) {
            progressBar.setVisibility(View.GONE);
            r = resultString;
            //Toast.makeText(MainActivity2.this, r, Toast.LENGTH_SHORT).show();

        }

        @Override
        protected String doInBackground(String... params) {
            resultString = obten10lista5syncronizage(lista1);

            if (resultString.equals("")) resultString = "NOT OK";

            try {
                savetextasfile("datosxml", resultString);
            } catch (IOException e) {
                e.printStackTrace();
            }

            return resultString;
        }
    }

    public class syncronizationCheckInCheckOutRemoto extends AsyncTask<String,String,String> {
        String z = "";
        Boolean isSuccess = false;

        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(String r) {
            progressBar.setVisibility(View.GONE);
            r = resultString;
            Toast.makeText(MainActivity2.this, r, Toast.LENGTH_SHORT).show();

        }

        @Override
        protected String doInBackground(String... params) {

            resultString = guardacheckincheckout();

            if (resultString.equals("")) resultString = "NOT OK";
            return resultString;
        }
    }

    public class syncronizationEmpleadosRemoto extends AsyncTask<String,String,String> {
        String z = "";
        Boolean isSuccess = false;

        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(String r) {
            progressBar.setVisibility(View.GONE);
            r = resultString;
            Toast.makeText(MainActivity2.this, r, Toast.LENGTH_SHORT).show();

        }

        @Override
        protected String doInBackground(String... params) {

            resultString =  guardaempleadoRemotosSync();

            if (resultString.equals("")) resultString = "NOT OK";
            return resultString;
        }
    }

    public class syncronizationProyectos extends AsyncTask<String,String,String> {
        String z = "";
        Boolean isSuccess = false;

        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(String r) {
            progressBar.setVisibility(View.GONE);
            r = resultString;
            Toast.makeText(MainActivity2.this, r, Toast.LENGTH_SHORT).show();

        }

        @Override
        protected String doInBackground(String... params) {
            stringProyectos = "";

            stringProyectos =  obtenProyectos();

            if (stringProyectos.equals(""))
            {stringProyectos= "-1";};

            if (resultString.equals("")) resultString = "NOT OK";
            else
            {resultString = "OK";}
            return resultString;
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
            r = resultString;
            Toast.makeText(MainActivity2.this, r, Toast.LENGTH_SHORT).show();

        }
        @Override
        protected String doInBackground(String... params)
        {
            resultString = obtenlistasyncronizage(lista1, lista2);

            if (!resultString.equals("")){

                String datosempleados = resultString;
                resultString = "";
                /*---------------------GUARDAR ROSTROS 10 IMAGENES JPG----------------------------*/
                if (!datosempleados.trim().equals("")) {

                    try {

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
                        resultString = "OK";
                    }
                }
                /*--------------------------------------------------------------------------------*/
            }

            if (resultString.equals("")) resultString = "NOT OK";

            return resultString;
        }
    }
}

