package cultoftheunicorn.marvel;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.opencv.cultoftheunicorn.marvel.R;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.StringReader;
import java.util.Calendar;
import java.util.List;

import cultoftheunicorn.marvel.dao.CheckInCheckOutDAO;
import cultoftheunicorn.marvel.dao.EmpleadoDAO;
import cultoftheunicorn.marvel.modelo.CheckInCheckOut;
import cultoftheunicorn.marvel.modelo.Empleado;

import static cultoftheunicorn.marvel.personalProyecto2.getMacAddress;

public class ListaEmpleados4 extends AppCompatActivity {

    String[] IMAGE;
    String[] NOMBRES;
    String[] IDEMPLEADO;
    String[] IDPROYECTO;
    String modoremoto = "";
    private List<Empleado> mListEmpleado;
    private List<CheckInCheckOut> mListCheckInCheckOut;

    public static final int SIGNATURE_ACTIVITY = 1;
    String TAG = "Response";
    String ip, resultString, numestacion, name;
    String proyect = "";
    String IDPROYECT = "";

    @Override
    public void onBackPressed() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String USARWIFI= prefs.getString("MACWIFI", "SI");

        if (USARWIFI.equals("SI")) {
            Intent resultIntent = new Intent();
            setResult(-200, resultIntent);
            finish();
        }else
        {
            finish();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch(resultCode) {

            //----------------OBTENER RESULTCODE PARA SABER SI ES REGISTRAR CHECKIN O CHECKOUT----//
            case -100:
                super.onActivityResult(requestCode, resultCode, data);
                String NOMBREEMPLEADO = data.getStringExtra("NOMBREEMPLEADO");
                String IDPROYECTO = data.getStringExtra("IDPROYECTO");
                String IDEMPLEADO = data.getStringExtra("IDEMPLEADO");
                String FECHA = data.getStringExtra("FECHA");
                String CHECK = data.getStringExtra("CHECK");

                FECHA = FECHA.substring(0,10);
                CHECK = CHECK.substring(0,10);

                Calendar rightNow = Calendar.getInstance();
                int horaactualformato24 = rightNow.get(Calendar.HOUR_OF_DAY); // return the hour in 24 hrs format (ranging from 0-23)

                CheckInCheckOutDAO checkincheckout = new CheckInCheckOutDAO(this);
                Cursor cursor = checkincheckout.getAllCheckInCheckOutXFechaDia(Long.parseLong(FECHA), Long.parseLong(IDEMPLEADO));
                cursor.moveToFirst();
                //------------REALIZAR GUARDADO EN BASE DE DATOS LC O REMOTA----------------------//
                while(!cursor.isAfterLast())
                {
                    //--------------------CONDICIONES PARA CHECK IN-------------------------------//
                    if (cursor.getCount() == 2)
                    {
                        if ((horaactualformato24 >=15) && (horaactualformato24 <= 18)) {
                            CheckInCheckOut createdcheckincheckout = checkincheckout.createCheckInCheckOut(Integer.parseInt(IDPROYECTO),
                                    Integer.parseInt(IDEMPLEADO),
                                    Long.parseLong(CHECK),
                                    Long.parseLong(FECHA),
                                    "CHECKIN",
                                    "NO",
                                    "NOREGISTRADO");
                            break;
                        }
                    }
                    //--------------------CONDICIONES PARA CHECK OUT------------------------------//
                    if ((cursor.getCount() == 1) || (cursor.getCount() == 3))
                    {
                        if (horaactualformato24 <= 13) {

                            if (Long.parseLong(cursor.getString(0)) <= 13) {

                                CheckInCheckOut createdcheckincheckout = checkincheckout.createCheckInCheckOut(Integer.parseInt(IDPROYECTO),
                                        Integer.parseInt(IDEMPLEADO),
                                        Long.parseLong(CHECK),
                                        Long.parseLong(FECHA),
                                        "CHECKOUT",
                                        "NO",
                                        "NOREGISTRADO");
                            }
                            break;
                        }

                        if (horaactualformato24 >=15 && horaactualformato24 <= 18)
                        {
                            if (Long.parseLong(cursor.getString(0)) <= 18) {

                                CheckInCheckOut createdcheckincheckout = checkincheckout.createCheckInCheckOut(Integer.parseInt(IDPROYECTO),
                                        Integer.parseInt(IDEMPLEADO),
                                        Long.parseLong(CHECK),
                                        Long.parseLong(FECHA),
                                        "CHECKOUT",
                                        "NO",
                                        "NOREGISTRADO");
                            }
                            break;
                        }
                    }
                    cursor.moveToNext();
                    //----------------------------------------------------------------------------//
                }
                if (cursor.getCount() < 1)
                {
                    CheckInCheckOut createdcheckincheckout = checkincheckout.createCheckInCheckOut(Integer.parseInt(IDPROYECTO),
                                                                                                   Integer.parseInt(IDEMPLEADO),
                                                                                                   Long.parseLong(CHECK),
                                                                                                   Long.parseLong(FECHA),
                                                                                      "CHECKIN",
                                                                                       "NO",
                                                                                           "NOREGISTRADO");
                }
                //--------------------------------------------------------------------------------//
                break;
            //------------------------------------------------------------------------------------//
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_empleados4);

        // ---------------------------------------------------------------------------------------//
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        modoremoto= prefs.getString("modoremoto", "SI");

        Intent intent2 = getIntent();
        proyect = intent2.getStringExtra("proyect");

        IDPROYECT = proyect.substring(1,proyect.indexOf("]"));

        EmpleadoDAO empleado = new EmpleadoDAO(this);
        //obtener listado local de usuario, empleados, fotodefault abrir bases de datos local
        mListEmpleado = empleado.getAllEmpleadoProyecto(Long.parseLong(IDPROYECT));

        ListaEmpleados4.AsyncCallWS checkpersonalnombre = new ListaEmpleados4.AsyncCallWS();// this is the Asynctask, which is used to process in background to reduce load on app process
        checkpersonalnombre.execute();

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

            int x; x=0;
            if (modoremoto.equals("NO")) {

                IMAGE = new String[mListEmpleado.size()];
                NOMBRES = new String[mListEmpleado.size()];
                IDEMPLEADO = new String[mListEmpleado.size()];
                IDPROYECTO = new String[mListEmpleado.size()];

                x = 0;
                for (int i=0; i <= mListEmpleado.size()-1; i++)
                {
                    String em = mListEmpleado.get(i).getNombre().toString();
                    //String emcompara = Nombres.toString().trim().toUpperCase() + " " + Apellidos.toString().trim().toUpperCase();

                    empleados   = mListEmpleado.get(i).getNombre().toString();
                    fotoemp     = mListEmpleado.get(i).getFotoEmpleado1();

                    IDEMPLEADO[i] = Long.toString(mListEmpleado.get(i).getId());
                    NOMBRES[i] = mListEmpleado.get(i).getNombre().toString();
                    IDPROYECTO[i] = String.valueOf(mListEmpleado.get(i).getIdproyecto());

                    //DESCRIPCION[c] = descripcion;
                    IMAGE[i] = Base64.encodeToString(fotoemp, Base64.DEFAULT);

                    x++;

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
                        IDEMPLEADO = new String[i];
                        //DESCRIPCION = new String[i];
                        //REFERENCIA = new String[i];

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
                                        IDEMPLEADO[c] = idemp;
                                        NOMBRES[c] = empleados;
                                        //DESCRIPCION[c] = descripcion;
                                        IMAGE[c] = foto;
                                        //REFERENCIA[c] = referencia;
                                        c++;

                                    } else if (tagname.equalsIgnoreCase("IDEMPLEADO")) {
                                        idemp = text;
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

            ListaEmpleados4.customadapter ca = new ListaEmpleados4.customadapter();
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
            final String ID = IDEMPLEADO[position].toString();
            final String NOMBREEMP = NOMBRES[position].toString();
            final String IDP = IDPROYECTO[position].toString();
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

            convertView.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getApplicationContext(), CheckinCheckout.class);
                    intent.putExtra("IDEMPLEADO", ID);
                    intent.putExtra("IDPROYECTO", IDP);
                    intent.putExtra("NOMBREEMPLEADO", NOMBREEMP);
                    startActivityForResult(intent, SIGNATURE_ACTIVITY);
                }
            });

            return convertView;
        }
    }
}
