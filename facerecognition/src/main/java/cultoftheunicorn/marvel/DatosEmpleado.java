package cultoftheunicorn.marvel;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.widget.ImageView;
import android.widget.TextView;

import org.opencv.cultoftheunicorn.marvel.R;

import java.awt.font.TextAttribute;
import java.util.List;

import cultoftheunicorn.marvel.dao.EmpleadoDAO;
import cultoftheunicorn.marvel.dao.ProyectoDAO;
import cultoftheunicorn.marvel.modelo.Empleado;
import cultoftheunicorn.marvel.modelo.Proyecto;

public class DatosEmpleado extends AppCompatActivity {

    private List<Empleado> mListEmpleado;
    private List<Proyecto> mListProyecto;

    public static final int SIGNATURE_ACTIVITY = 1;
    String TAG = "Response";
    String ip, resultString, numestacion, name, modoremoto;
    String IDEMPLEADO = "";
    String IDPROYECT  = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_datos_empleado);

        TextView  NombresTXT = (TextView) findViewById(R.id.txtnombre);
        TextView  CodigoTXT = (TextView) findViewById(R.id.txtcodigo);
        TextView  DomicilioTXT = (TextView) findViewById(R.id.txtdomicilio);
        TextView  CiudadTXT = (TextView) findViewById(R.id.txtCiudad);
        TextView  TelefonoTXT = (TextView) findViewById(R.id.txttelefono);
        TextView  IMSSTXT = (TextView) findViewById(R.id.txtcodigoimss);
        TextView  FechaAltaTXT = (TextView) findViewById(R.id.txtfechaalta);
        TextView  ProyectoTXT = (TextView) findViewById(R.id.txtProyectoDatos);
        ImageView IV = (ImageView) findViewById(R.id.ivcustomlayout2);
        // ---------------------------------------------------------------------------------------//
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        modoremoto= prefs.getString("modoremoto", "SI");

        Intent intent2 = getIntent();
        IDPROYECT = intent2.getStringExtra("IDPROYECTO");
        IDEMPLEADO= intent2.getStringExtra("IDEMPLEADO");

        EmpleadoDAO empleado = new EmpleadoDAO(this);
        ProyectoDAO proyecto = new ProyectoDAO(this);


        //obtener listado local de usuario, empleados, fotodefault abrir bases de datos local
        mListEmpleado = empleado.getAllEmpleadoxID(Long.parseLong(IDEMPLEADO));
        mListProyecto = proyecto.getAllProyectoxID(Long.parseLong(IDPROYECT));

        if (mListEmpleado.size()>0){
            NombresTXT.setText(mListEmpleado.get(0).getNombre());
            CodigoTXT.setText(mListEmpleado.get(0).getCodigoEmpleado());
            DomicilioTXT.setText(mListEmpleado.get(0).getDomicilio());
            CiudadTXT.setText(mListEmpleado.get(0).getCiudad());
            TelefonoTXT.setText(mListEmpleado.get(0).getTelefono());
            IMSSTXT.setText(mListEmpleado.get(0).getImss());
            FechaAltaTXT.setText(mListEmpleado.get(0).getFechaAlta());

            byte[] decodeString = mListEmpleado.get(0).getFotoEmpleado1();
            Bitmap decode = BitmapFactory.decodeByteArray(decodeString, 0, decodeString.length);
            IV.setImageBitmap(decode);
        }
        if (mListProyecto.size()>0){
            ProyectoTXT.setText(mListProyecto.get(0).getDescripcion());
        }
    }
}
