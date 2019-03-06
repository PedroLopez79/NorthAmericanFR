package cultoftheunicorn.marvel;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import org.opencv.cultoftheunicorn.marvel.R;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.StringReader;
import java.util.List;

import cultoftheunicorn.marvel.dao.EmpleadoDAO;
import cultoftheunicorn.marvel.modelo.Empleado;
import cultoftheunicorn.marvel.modelo.Proyecto;

public class ListaEmpleados extends AppCompatActivity {

    String IMAGE[];
    String NOMBRES[];
    String IDEMPLEADO[];
    String CODIGOS[];
    String DOMICILIOS[];
    String IMSS[];
    String CIUDAD[];
    String TELEFONOS[];
    String modoremoto = "";
    private List<Empleado> mListEmpleado;

    public static final int SIGNATURE_ACTIVITY = 1;
    String TAG = "Response";
    String ip, resultString, numestacion, name, Nombre, Apellidos, Domicilio, Ciudad, Telefono, Proyecto;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_empleados);

        // ---------------------------------------------------------------------------------------//
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        modoremoto= prefs.getString("modoremoto", "SI");

        EmpleadoDAO empleado = new EmpleadoDAO(this);
        //obtener listado local de usuario, empleados, fotodefault abrir bases de datos local
        mListEmpleado = empleado.getAllEmpleado();

        AsyncCallWS checkpersonalnombre = new AsyncCallWS();// this is the Asynctask, which is used to process in background to reduce load on app process
        checkpersonalnombre.execute();

        Button newButton = (Button) findViewById(R.id.btnNuevo);
        newButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent2 = getIntent();
                name      = intent2.getStringExtra("name");
                Nombre    = intent2.getStringExtra("Nombre");
                Apellidos = intent2.getStringExtra("Apellidos");
                Domicilio = intent2.getStringExtra("Domicilio");
                Ciudad    = intent2.getStringExtra("Ciudad");
                Telefono  = intent2.getStringExtra("Telefono");
                Proyecto  = intent2.getStringExtra("Proyecto");

                Intent intent = new Intent(ListaEmpleados.this, Training.class);
                intent.putExtra("name", name);
                intent.putExtra("Nombre", Nombre);
                intent.putExtra("Apellidos", Apellidos);
                intent.putExtra("Domicilio", Domicilio);
                intent.putExtra("Ciudad", Ciudad);
                intent.putExtra("Telefono", Telefono);
                intent.putExtra("Proyecto", Proyecto);
                startActivityForResult(intent, SIGNATURE_ACTIVITY);
            }
        });
    }

    private class AsyncCallWS extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            Log.i(TAG, "onPreExecute");
        }

        @Override
        protected Void doInBackground(Void... params) {
            Log.i(TAG, "doInBackground");

            String Nombres = "";
            String Apellidos = "";
            String text = "";
            String idemp = "";
            String empleados = "";
            String descripcion = "";
            String foto = "";
            byte[] fotoemp;
            String referencia = "";

            Intent intent = getIntent();
            resultString = intent.getStringExtra("resultstring");
            Nombres   = intent.getStringExtra("Nombres");
            Apellidos = intent.getStringExtra("Apellidos");
            resultString = intent.getStringExtra("resultstring");

            int x; x=0;
            if (modoremoto.equals("NO")) {

                for (int z=0; z <= mListEmpleado.size()-1; z++)
                    if (mListEmpleado.get(z).getNombre().toString().trim().toUpperCase().equals(Nombres.toString().trim().toUpperCase() + " " + Apellidos.toString().trim().toUpperCase()))
                        x++;

                IMAGE = new String[x];
                NOMBRES = new String[x];
                IDEMPLEADO = new String[x];

                x = 0;
                for (int i=0; i <= mListEmpleado.size()-1; i++)
                {
                    String em = mListEmpleado.get(i).getNombre().toString();
                    String emcompara = Nombres.toString().trim().toUpperCase() + " " + Apellidos.toString().trim().toUpperCase();
                    if (em.toString().trim().toUpperCase().equals(emcompara.toString().trim().toUpperCase()))
                    {
                        empleados   = mListEmpleado.get(i).getNombre().toString();
                        fotoemp     = mListEmpleado.get(i).getFotoEmpleado1();

                        IDEMPLEADO[x] = Long.toString(mListEmpleado.get(i).getId());
                        NOMBRES[x] = mListEmpleado.get(i).getNombre().toString();;
                        //DESCRIPCION[c] = descripcion;
                        IMAGE[x] = Base64.encodeToString(fotoemp, Base64.DEFAULT);
                        x++;
                    }
                }
            }else {

                String datosempleados = resultString;

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

                        IMAGE = new String[i];
                        NOMBRES = new String[i];
                        //DESCRIPCION = new String[i];
                        //REFERENCIA = new String[i];

                        text = "";
                        empleados = "";
                        descripcion = "";
                        foto = "";
                        referencia = "";

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
                                        NOMBRES[c] = empleados;
                                        //DESCRIPCION[c] = descripcion;
                                        IMAGE[c] = foto;
                                        //REFERENCIA[c] = referencia;
                                        c++;

                                    } else if (tagname.equalsIgnoreCase("IDEMPLEADO")) {
                                        empleados = "Empleado..[" + text + "]" + ": ";
                                    } else if (tagname.equalsIgnoreCase("HORAINICIO")) {
                                        descripcion = "HORA DE ENTRADA: [" + text + "]" + "  HORA DE SALIDA: [";
                                    } else if (tagname.equalsIgnoreCase("HORAFINAL")) {
                                        descripcion = descripcion + text + "]";
                                    } else if (tagname.equalsIgnoreCase("NOMBREEMPLEADO")) {
                                        empleados = empleados + text;
                                    } else if (tagname.equalsIgnoreCase("REFERENCIA")) {
                                        referencia = "Referencia: " + text;
                                    } else if (tagname.equalsIgnoreCase("FOTOBASE64")) {
                                        foto = text;
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
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            ListView lvEmpleados= (ListView) findViewById(R.id.lvEmpleados);

            customadapter ca = new customadapter();
            lvEmpleados.setAdapter(ca);

            Log.i(TAG, "onPostExecute");
            //Toast.makeText(parametrosinventarios.this, "Response" + resultString.toString(), Toast.LENGTH_LONG).show();
        }
    }

    class customadapter extends BaseAdapter {

        @Override
        public int getCount() {
            if (NOMBRES!=null && NOMBRES.length > 0 ) {
                return NOMBRES.length;
            }
            else
                return 0;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            convertView = getLayoutInflater().inflate(R.layout.activity_encuentra_personal,null);

            if (NOMBRES.length > 0)
            {
                try {
                    ImageView imageview = (ImageView) convertView.findViewById(R.id.ivcustomlayout);
                    TextView textview1 = (TextView) convertView.findViewById(R.id.txtnombre);
                    //TextView textview2 = (TextView) convertView.findViewById(R.id.txtcustomlayout2);

                    TextView textview3 = (TextView) convertView.findViewById(R.id.textView7);

                    textview1.setText(NOMBRES[position].toString());
                    //textview2.setText(DESCRIPCION[position].toString());
                    //textview3.setText(REFERENCIA[position].toString());

                    byte[] decodeString = Base64.decode(IMAGE[position], Base64.DEFAULT);
                    Bitmap decode = BitmapFactory.decodeByteArray(decodeString, 0, decodeString.length);
                    imageview.setImageBitmap(decode);
                }catch(Exception e){}}

            return convertView;
        }
    }
}
