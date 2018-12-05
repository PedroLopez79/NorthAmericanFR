package cultoftheunicorn.marvel;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.opencv.cultoftheunicorn.marvel.R;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


public class MainActivity2 extends AppCompatActivity
{
    // Declaring layout button, edit texts
    public static final int SIGNATURE_ACTIVITY = 1;
    String TAG = "Response";
    String resultString;


    Button login;
    EditText username,password;
    ProgressBar progressBar;
    // End Declaring layout button, edit texts

    // Declaring connection variables
    Connection con;
    String un,pass,db,ip,usuarioid, nombreusuario, numestacion;

    String usr, psw;
    //End Declaring connection variables

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
        progressBar.setVisibility(View.GONE);
        // End Getting values from button, texts and progress bar

        // Declaring Server ip, username, database name and password
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);


        ip = prefs.getString("ipservidor","192.168.0.6");
        db = prefs.getString("dbservidor","GAUSS_VER1.1");
        un = prefs.getString("usuarioservidor","sa");
        pass = prefs.getString("passwordservidor","Cistem32");

        numestacion = prefs.getString("numeroestacion","2601");

        // Declaring Server ip, username, database name and password


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
                /*try
                {
                    con = connectionclass(un, pass, db, ip);        // Connect to database
                    if (con == null)
                    {
                        z = "Verifique su coneccion de red!";
                    }
                    else
                    {
                        String query = "select * from USUARIO where USERNAME= '" + usernam.toString() + "' and USERPASSWORD = '"+ passwordd.toString() +"'  ";
                        Statement stmt = con.createStatement();
                        ResultSet rs = stmt.executeQuery(query);
                        if(rs.next())
                        {
                            usuarioid = rs.getString(1);
                            nombreusuario= rs.getString(3);
                            z = "Login Exitoso";
                            isSuccess=true;
                            con.close();

                            Intent intent = new Intent(getApplicationContext(), BitacoraServicio.class);
                            intent.putExtra("usuarioid",usuarioid);
                            intent.putExtra("nombreusuario",nombreusuario);
                            startActivityForResult(intent, SIGNATURE_ACTIVITY);
                        }
                        else
                        {
                            z = "Usuario o Password incorrectos!";
                            isSuccess = false;
                        }
                    }
                }
                catch (Exception ex)
                {
                    isSuccess = false;
                    z = ex.getMessage();
                }*/
                resultString = conectar();

                if (!resultString.equals("-1"))
                {
                    //if (numestacion.equals("2601"))
                    //{
                    String[] parts = resultString.split("@");
                    usuarioid = parts[0];
                    nombreusuario = parts[1];
                    z = "Login Exitoso";
                    isSuccess = true;

                    Intent intent = new Intent(getApplicationContext(), Menu.class);
                    intent.putExtra("usuarioid", usuarioid);
                    intent.putExtra("nombreusuario", nombreusuario);
                    startActivityForResult(intent, SIGNATURE_ACTIVITY);
                    //}
                    //else
                    //{
                    //    isSuccess = false;
                    //}
                }
            }
            return z;
        }
    }


    @SuppressLint("NewApi")
    public Connection connectionclass(String user, String password, String database, String server)
    {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        Connection connection = null;
        String ConnectionURL = null;
        try
        {
            Class.forName("net.sourceforge.jtds.jdbc.Driver");
            //ConnectionURL = "jdbc:jtds:sqlserver://" + server + "/" + database + ";user=" + user+ ";password=" + password + ";instance=SQLEXPRESS";
            ConnectionURL = "jdbc:jtds:sqlserver://" + server + "/" + database + ";user=" + user+ ";password=" + password + ";";
            connection = DriverManager.getConnection(ConnectionURL);
        }
        catch (SQLException se)
        {
            Log.e("error here 1 : ", se.getMessage());
        }
        catch (ClassNotFoundException e)
        {
            Log.e("error here 2 : ", e.getMessage());
        }
        catch (Exception e)
        {
            Log.e("error here 3 : ", e.getMessage());
        }
        return connection;
    }
}
