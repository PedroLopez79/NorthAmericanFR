package cultoftheunicorn.marvel;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.cultoftheunicorn.marvel.R;
import org.opencv.objdetect.CascadeClassifier;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import cultoftheunicorn.marvel.dao.EmpleadoDAO;
import cultoftheunicorn.marvel.dao.FotodefaultDAO;
import cultoftheunicorn.marvel.modelo.Empleado;
import cultoftheunicorn.marvel.modelo.FotoDefault;
import static com.googlecode.javacv.cpp.opencv_objdetect.CV_HAAR_DO_ROUGH_SEARCH;
import static com.googlecode.javacv.cpp.opencv_objdetect.CV_HAAR_FIND_BIGGEST_OBJECT;
//import android.content.Context;

public class Recognize extends AppCompatActivity implements CameraBridgeViewBase.CvCameraViewListener2 {

    private static final String    TAG                 = "OCVSample::Activity";
    private static final Scalar FACE_RECT_COLOR     = new Scalar(0, 255, 0, 255);
    public static final int        JAVA_DETECTOR       = 0;
    public static final int        NATIVE_DETECTOR     = 1;

    public static final int SEARCHING= 1;
    public static final int IDLE= 2;

    private static final int frontCam =1;
    private static final int backCam =2;

    private List<Empleado> mListEmpleado;
    private List<FotoDefault> mListFotoDefault;
    String modoremoto = "";

    private int faceState=IDLE;

    private Mat                    mRgba;
    private Mat                    mGray;
    private File mCascadeFile, mCascadeFileER, mCascadeFileEL;
    private CascadeClassifier mJavaDetector, mCascadeER, mCascadeEL;

    private float EYE_SX = 0.12f;
    private float EYE_SY = 0.17f;
    private float EYE_SW = 0.37f;
    private float EYE_SH = 0.36f;

    // rectangulos donde estan los ojos y cara
    private Rect rostro = null;
    private Rect lEye = null;
    private Rect rEye = null;

    private int                    mDetectorType       = JAVA_DETECTOR;
    private String[]               mDetectorName;

    private float                  mRelativeFaceSize   = 0.2f;
    private int                    mAbsoluteFaceSize   = 0;
    private int mLikely=999;

    String mPath="";

    //--------------------------------------------------------------------------------------------//
    public static final int SIGNATURE_ACTIVITY = 1;
    String TAG2 = "Response";
    String resultString;
    String un,pass,db,ip,usuarioid, nombreusuario, numestacion;
    String IMAGE1,IMAGE2,IMAGE3,IMAGE4,IMAGE5,IMAGE6,IMAGE7,IMAGE8,IMAGE9,IMAGE10;
    String IMAGEDEFAULT1,IMAGEDEFAULT2,IMAGEDEFAULT3,IMAGEDEFAULT4,IMAGEDEFAULT5,IMAGEDEFAULT6,
           IMAGEDEFAULT7,IMAGEDEFAULT8,IMAGEDEFAULT9,IMAGEDEFAULT10;
    byte[] image1; byte[] image2; byte[] image3; byte[] image4; byte[] image5; byte[] image6;
    byte[] image7; byte[] image8; byte[] image9; byte[] image10;
    byte[] defaultimage1; byte[] defaultimage2; byte[] defaultimage3; byte[] defaultimage4; byte[] defaultimage5; byte[] defaultimage6;
    byte[] defaultimage7; byte[] defaultimage8; byte[] defaultimage9; byte[] defaultimage10;

    String NOMBRES;
    String band = "Disable Scann";
    String FACESFILEEXIST;
    //--------------------------------------------------------------------------------------------//

    private Tutorial3View   mOpenCvCameraView;

    private ImageView Iv;
    Bitmap mBitmap;
    Handler mHandler;

    PersonRecognizer fr;
    ToggleButton scan;

    Set<String> uniqueNames = new HashSet<String>();

    // max number of people to detect in a session
    String[] uniqueNamesArray = new String[10];

    static final long MAXIMG = 10;

    Labels labelsFile;

    static {
        OpenCVLoader.initDebug();
        System.loadLibrary("opencv_java");
    }

    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                {
                    Log.i(TAG, "OpenCV loaded successfully");

                    fr=new PersonRecognizer(mPath);
                    String s = getResources().getString(R.string.Straininig);
                    //Toast.makeText(getApplicationContext(),s, Toast.LENGTH_LONG).show();
                    fr.load();

                    try {
                        // load cascade file from application resources
                        InputStream is = getResources().openRawResource(R.raw.haarcascade_frontalface_alt);
                        File cascadeDir = getDir("cascade", Context.MODE_PRIVATE);
                        //mCascadeFile = new File(cascadeDir, "lbpcascade.xml");
                        mCascadeFile = new File(cascadeDir, "haarcascade_frontalface_alt.xml");

                        FileOutputStream os = new FileOutputStream(mCascadeFile);

                        byte[] buffer = new byte[4096];
                        int bytesRead;
                        while ((bytesRead = is.read(buffer)) != -1) {
                            os.write(buffer, 0, bytesRead);
                        }
                        is.close();
                        os.close();
//--------------------------------------------------------------------------------------------------------------------------
                        // --------------------------------- load left eye classificator -----------------------------------
                        InputStream iser = getResources().openRawResource(R.raw.haarcascade_righteye_2splits);
                        File cascadeDirER = getDir("cascadeER", Context.MODE_PRIVATE);
                        mCascadeFileER = new File(cascadeDirER, "haarcascade_righteye_2splits.xml");

                        FileOutputStream oser = new FileOutputStream(mCascadeFileER);

                        byte[] bufferER = new byte[4096];
                        int bytesReadER;
                        while ((bytesReadER = iser.read(bufferER)) != -1) {
                            oser.write(bufferER, 0, bytesReadER);
                        }
                        iser.close();
                        oser.close();
//----------------------------------------------------------------------------------------------------
// --------------------------------- load right eye classificator ------------------------------------
                        InputStream isel = getResources().openRawResource(R.raw.haarcascade_lefteye_2splits);
                        File cascadeDirEL = getDir("cascadeEL", Context.MODE_PRIVATE);
                        mCascadeFileEL = new File(cascadeDirEL, "haarcascade_lefteye_2splits.xml");

                        FileOutputStream osel = new FileOutputStream(mCascadeFileEL);

                        byte[] bufferEL = new byte[4096];
                        int bytesReadEL;
                        while ((bytesReadEL = isel.read(bufferEL)) != -1) {
                            osel.write(bufferEL, 0, bytesReadEL);
                        }
                        isel.close();
                        osel.close();
// ------------------------------------------------------------------------------------------------------


                        mJavaDetector = new CascadeClassifier(mCascadeFile.getAbsolutePath());
                /**/    mCascadeER = new CascadeClassifier(mCascadeFileER.getAbsolutePath());
                /**/    mCascadeEL = new CascadeClassifier(mCascadeFileEL.getAbsolutePath());
                        if (mJavaDetector.empty()|| mCascadeER.empty() || mCascadeEL.empty()) {
                            Log.e(TAG, "Failed to load cascade classifier");
                            mJavaDetector = null;
                /**/        mCascadeER = null;
                            mCascadeEL = null;
                        } else
                            Log.i(TAG, "Loaded cascade classifier from " + mCascadeFile.getAbsolutePath());

                        cascadeDir.delete();
                /**/    cascadeDirER.delete();
                /**/    cascadeDirEL.delete();

                    } catch (IOException e) {
                        e.printStackTrace();
                        Log.e(TAG, "Failed to load cascade. Exception thrown: " + e);
                    }

                    mOpenCvCameraView.setCamFront();

                    //mOpenCvCameraView.setCamBack();
                    mOpenCvCameraView.enableView();

                } break;
                default:
                {
                    super.onManagerConnected(status);
                } break;

            }
        }
    };

    public Recognize() {
        mDetectorName = new String[2];
        mDetectorName[JAVA_DETECTOR] = "Java";
        mDetectorName[NATIVE_DETECTOR] = "Native (tracking)";

        Log.i(TAG, "Instantiated new " + this.getClass());
    }

    public void deleteRecursive(String fileDirectory) {

        File file = new File(fileDirectory);
        String[] files;
        files = file.list();
        for (int i=0; i<files.length; i++) {
            File myFile = new File(file, files[i]);
            myFile.delete();
        }
    }

    public void guarda10fotoempleado10fotodefault(String NOMBREEMPELADO){
        EmpleadoDAO empleado = new EmpleadoDAO(this);
        FotodefaultDAO fotodefault = new FotodefaultDAO(this);

        mListEmpleado = empleado.getAllEmpleado();
        mListFotoDefault = fotodefault.getAllFotoDefault();

        if (mListFotoDefault.size() > 0)
        {
            defaultimage1 = mListFotoDefault.get(0).getFotoDefault1();
            defaultimage2 = mListFotoDefault.get(0).getFotoDefault2();
            defaultimage3 = mListFotoDefault.get(0).getFotoDefault3();
            defaultimage4 = mListFotoDefault.get(0).getFotoDefault4();
            defaultimage5 = mListFotoDefault.get(0).getFotoDefault5();
            defaultimage6 = mListFotoDefault.get(0).getFotoDefault6();
            defaultimage7 = mListFotoDefault.get(0).getFotoDefault7();
            defaultimage8 = mListFotoDefault.get(0).getFotoDefault8();
            defaultimage9 = mListFotoDefault.get(0).getFotoDefault9();
            defaultimage10 = mListFotoDefault.get(0).getFotoDefault10();
        }

        for (int i=0; i <= mListEmpleado.size()-1; i++)
        {
            String em = mListEmpleado.get(i).getNombre().toString();
            if (em.toString().trim().toUpperCase().equals(NOMBREEMPELADO.toString().trim().toUpperCase())) {
                image1 = mListEmpleado.get(i).getFotoEmpleado1();
                image2 = mListEmpleado.get(i).getFotoEmpleado2();
                image3 = mListEmpleado.get(i).getFotoEmpleado3();
                image4 = mListEmpleado.get(i).getFotoEmpleado4();
                image5 = mListEmpleado.get(i).getFotoEmpleado5();
                image6 = mListEmpleado.get(i).getFotoEmpleado6();
                image7 = mListEmpleado.get(i).getFotoEmpleado7();
                image8 = mListEmpleado.get(i).getFotoEmpleado8();
                image9 = mListEmpleado.get(i).getFotoEmpleado9();
                image10 = mListEmpleado.get(i).getFotoEmpleado10();

                NOMBRES = NOMBREEMPELADO.toString().trim().toUpperCase();
                break;
            }
        }

        savebitmapLC("DEFAULT-" + "1", defaultimage1); savebitmapLC("DEFAULT-" + "2", defaultimage2);
        savebitmapLC("DEFAULT-" + "3", defaultimage3); savebitmapLC("DEFAULT-" + "4", defaultimage4);
        savebitmapLC("DEFAULT-" + "5", defaultimage5); savebitmapLC("DEFAULT-" + "6", defaultimage6);
        savebitmapLC("DEFAULT-" + "7", defaultimage7); savebitmapLC("DEFAULT-" + "8", defaultimage8);
        savebitmapLC("DEFAULT-" + "9", defaultimage9); savebitmapLC("DEFAULT-" + "10", defaultimage10);

        savebitmapLC(NOMBRES + "-1", image1); savebitmapLC(NOMBRES + "-2", image2);
        savebitmapLC(NOMBRES + "-3", image3); savebitmapLC(NOMBRES + "-4", image4);
        savebitmapLC(NOMBRES + "-5", image5); savebitmapLC(NOMBRES + "-6", image6);
        savebitmapLC(NOMBRES + "-7", image7); savebitmapLC(NOMBRES + "-8", image8);
        savebitmapLC(NOMBRES + "-9", image9); savebitmapLC(NOMBRES + "-10", image10);

        band = "EnableScann";
        FACESFILEEXIST = "OK";
    }

    public void savebitmap(String bitmapfilename, String imagebase64) {
        File file;
        file = new File(mPath, bitmapfilename+".jpg");
        try{
            OutputStream stream = null;
            stream = new FileOutputStream(file);
            byte[] decodedString = Base64.decode(imagebase64, Base64.DEFAULT);
            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            decodedByte = getResizedBitmap(decodedByte, 128, 128);
            decodedByte.compress(Bitmap.CompressFormat.JPEG,100,stream);

            stream.flush();
            stream.close();

        }catch (IOException e) // Catch the exception
        {
            e.printStackTrace();
        }
    }

    public void savebitmapLC(String bitmapfilename, byte[] image) {
        File file;
        file = new File(mPath, bitmapfilename+".jpg");
        try{
            OutputStream stream = null;
            stream = new FileOutputStream(file);
            Bitmap decodedByte = BitmapFactory.decodeByteArray(image, 0, image.length);
            decodedByte = getResizedBitmap(decodedByte, 128, 128);
            decodedByte.compress(Bitmap.CompressFormat.JPEG,100,stream);

            stream.flush();
            stream.close();

        }catch (IOException e) // Catch the exception
        {
            e.printStackTrace();
        }
    }

    public Bitmap getResizedBitmap(Bitmap bm, int newWidth, int newHeight) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // CREATE A MATRIX FOR THE MANIPULATION
        Matrix matrix = new Matrix();
        // RESIZE THE BIT MAP
        matrix.postScale(scaleWidth, scaleHeight);

        // "RECREATE" THE NEW BITMAP
        Bitmap resizedBitmap = Bitmap.createBitmap(
                bm, 0, 0, width, height, matrix, false);
        bm.recycle();
        return resizedBitmap;
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(Recognize.this, Recognize.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("EXIT", true);
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recognize);

        Calendar cal = Calendar.getInstance();
        long StartTime= System.currentTimeMillis();
        long currentTime =StartTime;

        scan = (ToggleButton) findViewById(R.id.scan);
        final TextView results = (TextView) findViewById(R.id.results);

        // Declaring Server ip, username, database name and password
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        ip = prefs.getString("ipservidor","IANSERVICES.DDNS.NET");
        db = prefs.getString("dbservidor","AmericanGreenHouseCheck");
        un = prefs.getString("usuarioservidor","sa");
        pass = prefs.getString("passwordservidor","IAN32");
        numestacion = prefs.getString("numeroestacion","2601");
        modoremoto= prefs.getString("modoremoto", "SI");

        //SI SE PRESIONA BACK QUE TERMINE LA FORMA------------------------------------------------//
        if (getIntent().getBooleanExtra("EXIT", false)) {
            finish();
        }
        //----------------------------------------------------------------------------------------//

        if (!getIntent().getBooleanExtra("EXIT", false)) {
            //mPath=getFilesDir()+"/facerecogOCV/";
            mPath = Environment.getExternalStorageDirectory() + "/facerecogOCV/";
            //---------RUTINA DE LIMPIADO DE ARCHIVOS EN DIRECTORIO facerecogOCV----------------------//
            deleteRecursive(mPath);
            //---------RUTINA PARA GENERAR LOS ARCHIVO JPG, 10 imagenes default- 10 imagens sujeto a autenticar//

            ObtenRostros obtenrostros = new ObtenRostros();// this is the Asynctask, which is used to process in background to reduce load on app process
            obtenrostros.execute("");
            //------------------------------------------------------------------------------------//
            //--PROCESO PARA ASEGURAR EL GUARDADO DE LOS ROSTROS----------------------------------//
            FACESFILEEXIST = "NO";
            cal = Calendar.getInstance();
            StartTime = System.currentTimeMillis();
            currentTime = StartTime;
            currentTime = cal.getTimeInMillis();

            while (!FACESFILEEXIST.equals("OK"))
            {
                System.out.println("Start time is "+StartTime);
                System.out.println("Current time is "+currentTime);
                System.out.println("Timer will has stopped");
                currentTime++;
            }
            //------------------------------------------------------------------------------------//
            //------------------------------------------------------------------------------------//
            mOpenCvCameraView = (Tutorial3View) findViewById(R.id.tutorial3_activity_java_surface_view);
            mOpenCvCameraView.setCvCameraViewListener(this);

            Log.e("Path", mPath);
            //-------------------------POSICION ORIGINAL DE LABELSFILE--------------------------------//
            labelsFile = new Labels(mPath);
            //-------------------------------------------------------------------------------------------------//

            mHandler = new Handler() {
                @Override
                public void handleMessage(Message msg) {
                /*
                    display a newline separated list of individual names
                 */
                    uniqueNames.clear();
                    String tempName = msg.obj.toString();
                    if (!(tempName.equals("Unknown"))) {
                        tempName = capitalize(tempName);
                        uniqueNames.add(tempName);
                        uniqueNamesArray = uniqueNames.toArray(new String[uniqueNames.size()]);
                        StringBuilder strBuilder = new StringBuilder();
                        strBuilder.append(uniqueNamesArray[0]);
                        for (int i = 0; i < uniqueNamesArray.length; i++) {
                            strBuilder.append(uniqueNamesArray[i] + "\n");
                        }
                        String textToDisplay = strBuilder.toString();
                        results.setText(textToDisplay);

//!!!!!!!!!!!!!!!!!!!!!!//-----------CODIGO PARA REGISTRAR A PERSONA RECONOCIDA [REGISTRA FECHA]!!!!!!!!-------//
                        if (!textToDisplay.contains("Unknown"))
                        //if (!textToDisplay.trim().equals("**Unknown****Unknown**"))
                        {
                            Intent intent = getIntent();
                            String IDPROYECTO = getIntent().getStringExtra("IDPROYECTO");

                            long currentTime = System.currentTimeMillis();
                            Calendar calendar = Calendar.getInstance();
                            calendar.set(Calendar.HOUR_OF_DAY, 0);
                            calendar.set(Calendar.MINUTE, 0);
                            calendar.set(Calendar.SECOND, 0);
                            calendar.set(Calendar.MILLISECOND, 0);
                            long onlydate = calendar.getTimeInMillis();

                            Intent resultIntent = new Intent();
                            resultIntent.putExtra("NOMBREEMPLEADO", textToDisplay);
                            resultIntent.putExtra("FECHA", String.valueOf(onlydate));
                            resultIntent.putExtra("CHECK", String.valueOf(currentTime));
                            resultIntent.putExtra("IDPROYECTO", IDPROYECTO);
                            setResult(-100, resultIntent);

                            Toast.makeText(getApplicationContext(),textToDisplay, Toast.LENGTH_LONG).show();
                            finish();
                        }
//!!!!!!!!!!!!!!!!!!!!!!//-------------------------------------------------------------------------------------//
                    } else {
                        results.setText("");
                        results.append("Unknown" + "\n");
                    }
                }
            };


            scan.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    if (b) {
                        if (!fr.canPredict()) {
                            scan.setChecked(false);
                            Toast.makeText(getApplicationContext(), getResources().getString(R.string.SCanntoPredic), Toast.LENGTH_LONG).show();
                            return;
                        }
                        faceState = SEARCHING;
                    } else {
                        faceState = IDLE;
                    }
                }
            });

            boolean success = (new File(mPath)).mkdirs();
            if (!success) {
                Log.e("Error", "Error creating directory");
            }

            Button submit = (Button) findViewById(R.id.submit);
            submit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (uniqueNames.size() > 0) {
                        Intent intent = new Intent(Recognize.this, ReviewResults.class);
                        intent.putExtra("list", uniqueNamesArray);
                        startActivity(intent);
                    } else {
                        Toast.makeText(Recognize.this, "Empty list cannot be sent further", Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    }

    public String conectar(String idemp) {
        String SOAP_ACTION = "urn:androidserviceIntf-Iandroidservice#obtenRostros10";
        String METHOD_NAME = "obtenRostros10";
        String NAMESPACE = "urn:androidserviceIntf";
        String URL = "http://"+ip+":1001/soap/Iandroidservice";
        String z;

        String nombre;
        boolean isSuccess;

        //----FECHA ACTUAL------------------------------------------------------------------------//
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        Date date = new Date();
        String fecha = dateFormat.format(date);
        //-----variable para imagen base64--------------------------------------------------------//
        String encodedImage2;
        byte[] byteArray;
        Bitmap image = null;
        //----------------------------------------------------------------------------------------//

        nombre = getIntent().getStringExtra("name");
        try {
            SoapObject Request = new SoapObject(NAMESPACE, METHOD_NAME);
            Request.addProperty("idemp", idemp);
            //---------------------obtener las 10 imagenes ya capturadas--------------------------//
            encodedImage2 = "";
            //------------------------------------------------------------------------------------//

            SoapSerializationEnvelope soapEnvelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            soapEnvelope.dotNet = true;
            soapEnvelope.setOutputSoapObject(Request);
            HttpTransportSE transport = new HttpTransportSE(URL, 80000);

            transport.call(SOAP_ACTION, soapEnvelope);

            //resultString = (SoapPrimitive) soapEnvelope.getResponse();
            Object  response = (Object) soapEnvelope.getResponse();
            resultString = response.toString();

            return resultString;

            //Log.i(TAG, "Result Celsius: " + resultString);
        } catch (Exception ex) {
            Log.e(TAG2, "Error: " + ex.getMessage());
        }
        return SOAP_ACTION;
    }

    public class ObtenRostros extends AsyncTask<String,String,String>
    {
        String z = "";
        //Boolean isSuccess = false;

        @Override
        protected void onPreExecute()
        {
            /*progressBar.setVisibility(View.VISIBLE);*/
        }

        @Override
        protected void onPostExecute(String r)
        {
            //progressBar.setVisibility(View.GONE);
            /*Toast.makeText(Recognize.this, r, Toast.LENGTH_SHORT).show();
            if(isSuccess)
            {
                Toast.makeText(Recognize.this , "Listo para reconociento facial" , Toast.LENGTH_LONG).show();
                if (band.equals("EnableScann"))
                {
                    scan.setEnabled(true);
                }
            }*/
            //if(!isSuccess)
            //{
                Toast.makeText(Recognize.this , "Listo para reconociento facial" , Toast.LENGTH_LONG).show();
                if (band.equals("EnableScann"))
                {
                    scan.setEnabled(true);
                }
            //}
        }
        @Override
        protected String doInBackground(String... params)
        {
            Intent intent = getIntent();
            String idempleado = intent.getStringExtra("IDEMPLEADO");
            String nombreemp  = intent.getStringExtra("NOMBREEMPLEADO");

            if (modoremoto.equals("NO")) {
                guarda10fotoempleado10fotodefault(nombreemp);
            } else {

                resultString = conectar(idempleado);

                if (!resultString.equals("")) {
                    /*---------------------GUARDAR ROSTROS 10 IMAGENES JPG----------------------------*/
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

                            IMAGE1 = "";
                            IMAGE2 = "";
                            IMAGE3 = "";
                            IMAGE4 = "";
                            IMAGE5 = "";
                            IMAGE6 = "";
                            IMAGE7 = "";
                            IMAGE8 = "";
                            IMAGE9 = "";
                            IMAGE10 = "";

                            IMAGEDEFAULT1 = "";
                            IMAGEDEFAULT2 = "";
                            IMAGEDEFAULT3 = "";
                            IMAGEDEFAULT4 = "";
                            IMAGEDEFAULT5 = "";
                            IMAGEDEFAULT6 = "";
                            IMAGEDEFAULT7 = "";
                            IMAGEDEFAULT8 = "";
                            IMAGEDEFAULT9 = "";
                            IMAGEDEFAULT10 = "";

                            NOMBRES = "";

                            String text = "";
                            String empleados = "";
                            String foto1 = "";
                            String foto2 = "";
                            String foto3 = "";
                            String foto4 = "";
                            String foto5 = "";
                            String foto6 = "";
                            String foto7 = "";
                            String foto8 = "";
                            String foto9 = "";
                            String foto10 = "";

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
                                            NOMBRES = empleados;
                                            IMAGE1 = foto1;
                                            IMAGE2 = foto2;
                                            IMAGE3 = foto3;
                                            IMAGE4 = foto4;
                                            IMAGE5 = foto5;
                                            IMAGE6 = foto6;
                                            IMAGE7 = foto7;
                                            IMAGE8 = foto8;
                                            IMAGE9 = foto9;
                                            IMAGE10 = foto10;

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

                                        } else if (tagname.equalsIgnoreCase("NOMBREEMPLEADO")) {
                                            empleados = empleados + text;
                                        } else if (tagname.equalsIgnoreCase("FOTO1BASE64")) {
                                            foto1 = text;
                                        } else if (tagname.equalsIgnoreCase("FOTO2BASE64")) {
                                            foto2 = text;
                                        } else if (tagname.equalsIgnoreCase("FOTO3BASE64")) {
                                            foto3 = text;
                                        } else if (tagname.equalsIgnoreCase("FOTO4BASE64")) {
                                            foto4 = text;
                                        } else if (tagname.equalsIgnoreCase("FOTO5BASE64")) {
                                            foto5 = text;
                                        } else if (tagname.equalsIgnoreCase("FOTO6BASE64")) {
                                            foto6 = text;
                                        } else if (tagname.equalsIgnoreCase("FOTO7BASE64")) {
                                            foto7 = text;
                                        } else if (tagname.equalsIgnoreCase("FOTO8BASE64")) {
                                            foto8 = text;
                                        } else if (tagname.equalsIgnoreCase("FOTO9BASE64")) {
                                            foto9 = text;
                                        } else if (tagname.equalsIgnoreCase("FOTO10BASE64")) {
                                            foto10 = text;
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
                            savebitmap(NOMBRES + "-1", IMAGE1);
                            savebitmap(NOMBRES + "-2", IMAGE2);
                            savebitmap(NOMBRES + "-3", IMAGE3);
                            savebitmap(NOMBRES + "-4", IMAGE4);
                            savebitmap(NOMBRES + "-5", IMAGE5);
                            savebitmap(NOMBRES + "-6", IMAGE6);
                            savebitmap(NOMBRES + "-7", IMAGE7);
                            savebitmap(NOMBRES + "-8", IMAGE8);
                            savebitmap(NOMBRES + "-9", IMAGE9);
                            savebitmap(NOMBRES + "-10", IMAGE10);

                            savebitmap("DEFAULT-" + "1", IMAGEDEFAULT1);
                            savebitmap("DEFAULT-" + "2", IMAGEDEFAULT2);
                            savebitmap("DEFAULT-" + "3", IMAGEDEFAULT3);
                            savebitmap("DEFAULT-" + "4", IMAGEDEFAULT4);
                            savebitmap("DEFAULT-" + "5", IMAGEDEFAULT5);
                            savebitmap("DEFAULT-" + "6", IMAGEDEFAULT6);
                            savebitmap("DEFAULT-" + "7", IMAGEDEFAULT7);
                            savebitmap("DEFAULT-" + "8", IMAGEDEFAULT8);
                            savebitmap("DEFAULT-" + "9", IMAGEDEFAULT9);
                            savebitmap("DEFAULT-" + "10", IMAGEDEFAULT10);
                        }
                    }
                    /*--------------------------------------------------------------------------------*/
                    band = "EnableScann";
                    FACESFILEEXIST = "OK";
                }
            }

            resultString = "OK";
            return resultString;
        }
    }

    @Override
    public void onCameraViewStarted(int width, int height) {
        mGray = new Mat();
        mRgba = new Mat();
    }

    @Override
    public void onCameraViewStopped() {
        mGray.release();
        mRgba.release();
    }

    void detectEyes(Mat frame, CascadeClassifier faceCascade, CascadeClassifier eyesCascade)
    {
        Mat frameGray = new Mat();
        //Imgproc.cvtColor(frame, frameGray, Imgproc.COLOR_BGR2GRAY);
        //Imgproc.equalizeHist(frameGray, frameGray);
        // -- Detect faces
        MatOfRect faces = new MatOfRect();
        faceCascade.detectMultiScale(frameGray, faces, 1.1, 1, 0, // TODO: objdetect.CV_HAAR_SCALE_IMAGE
                new Size(80, 80), new Size());
        List<Rect> listOfFaces = faces.toList();
        for (Rect face : listOfFaces) {
            Point center = new Point(face.x + face.width / 2, face.y + face.height / 2);
            //Imgproc.ellipse(frame, center, new Size(face.width / 2, face.height / 2), 0, 0, 360,
            //        new Scalar(255, 0, 255));
            Mat faceROI = frameGray.submat(face);
            // -- In each face, detect eyes
            MatOfRect eyes = new MatOfRect();
            eyesCascade.detectMultiScale(faceROI, eyes);
            List<Rect> listOfEyes = eyes.toList();
            for (Rect eye : listOfEyes) {
                Point eyeCenter = new Point(face.x + eye.x + eye.width / 2, face.y + eye.y + eye.height / 2);
                int radius = (int) Math.round((eye.width + eye.height) * 0.25);
                Core.rectangle(frame, eye.tl(), eye.br(), new Scalar(255, 0, 0, 255), 2);
                //Imgproc.circle(frame, eyeCenter, radius, new Scalar(255, 0, 0), 4);
            }
        }
        //-- Show what you got
        //HighGui.imshow("Capture - Face detection", frame );
        //if ((rEye != null) && (lEye != null)) {
        //    Core.rectangle(mRgba, rEye.tl(), rEye.br(), new Scalar(255, 0, 0, 255), 2);
        //}
    }


    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
        mRgba = inputFrame.rgba();
        mGray = inputFrame.gray();

        //detectEyes(mGray, mJavaDetector, mCascadeEL);

        int flags = CV_HAAR_FIND_BIGGEST_OBJECT;

        if (mAbsoluteFaceSize == 0) {
            int height = mGray.rows();
            if (Math.round(height * mRelativeFaceSize) > 0) {
                mAbsoluteFaceSize = Math.round((height * mRelativeFaceSize));
            }
              //mNativeDetector.setMinFaceSize(mAbsoluteFaceSize);
        }

        MatOfRect faces = new MatOfRect();

        if (mDetectorType == JAVA_DETECTOR) {
            if (mJavaDetector != null)
                mJavaDetector.detectMultiScale(mGray, faces, 1.1, 1, flags, // TODO: objdetect.CV_HAAR_SCALE_IMAGE
                        new Size(80, 80), new Size());
                //mJavaDetector.detectMultiScale(mGray, faces, 1.1f, 2, flags, // TODO: objdetect.CV_HAAR_SCALE_IMAGE
                //        new Size(mAbsoluteFaceSize, mAbsoluteFaceSize), new Size());
        }
        else if (mDetectorType == NATIVE_DETECTOR) {
            /*if (mNativeDetector != null)
                mNativeDetector.detect(mGray, faces);*/
        }
        else {
            Log.e(TAG, "Detection method is not selected!");
        }

        Rect[] facesArray = faces.toArray();

        if ((facesArray.length>0) && (faceState==SEARCHING)) {
            //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
            if (faces.toArray().length == 1) {

                List<Rect> listOfFaces = faces.toList();
                rostro = listOfFaces.get(0);


                Point center = new Point(rostro.x + rostro.width / 2, rostro.y + rostro.height / 2);
                Mat faceROI = mGray.submat(rostro);
                // -- In each face, detect eyes
                MatOfRect eyes = new MatOfRect();

                mCascadeER.detectMultiScale(faceROI, eyes);
                List<Rect> listOfEyes = eyes.toList();

                if (listOfEyes.size() == 2) {
                    rEye = listOfEyes.get(0);
                    lEye = listOfEyes.get(1);
                }

                /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
                Mat m = new Mat();
                m = mGray.submat(facesArray[0]);

                mBitmap = Bitmap.createBitmap(m.width(), m.height(), Bitmap.Config.ARGB_8888);

                Utils.matToBitmap(m, mBitmap);
                Message msg = new Message();
                String textTochange = "IMG";
                msg.obj = textTochange;
                //mHandler.sendMessage(msg);
                //

                if ((rEye != null) && (lEye != null)) {
                    //Core.rectangle(mRgba, lEye.tl(), lEye.br(), FACE_RECT_COLOR, 3);

                    textTochange = fr.predict(m, lEye, rEye);


                mLikely = fr.getProb();
                msg = new Message();
                msg.obj = textTochange;
                mHandler.sendMessage(msg);
                }

            }
        }

        //Core.rectangle(mRgba, facesArray[0].tl(), facesArray[0].br(), FACE_RECT_COLOR, 3);
        for (int i = 0; i < facesArray.length; i++) {
            if (faces.toArray().length == 1) {
                Core.rectangle(mRgba, facesArray[0].tl(), facesArray[0].br(), FACE_RECT_COLOR, 3);
                /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

                /*List<Rect> listOfFaces = faces.toList();
                for (Rect face : listOfFaces) {
                    Point center = new Point(face.x + face.width / 2, face.y + face.height / 2);
                    Core.ellipse(mRgba, center, new Size(face.width / 2, face.height / 2), 0, 0, 360,
                                    new Scalar(255, 0, 255));
                    //Imgproc.ellipse(frame, center, new Size(face.width / 2, face.height / 2), 0, 0, 360,
                    //        new Scalar(255, 0, 255));

                    Mat faceROI = mGray.submat(face);
                    // -- In each face, detect eyes
                    MatOfRect eyes = new MatOfRect();

                    mCascadeER.detectMultiScale(faceROI, eyes);
                    List<Rect> listOfEyes = eyes.toList();

                    for (Rect eye : listOfEyes) {
                        Point eyeCenter = new Point(face.x + eye.x + eye.width / 2, face.y + eye.y + eye.height / 2);
                        int radius = (int) Math.round((eye.width + eye.height) * 0.25);
                        //Imgproc.circle(frame, eyeCenter, radius, new Scalar(255, 0, 0), 4);

                        Core.circle(mRgba, eyeCenter, radius, new Scalar(255, 0, 0), 2);
                    }
                }*/
            }
            /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        }

        return mRgba;
    }

    @Override
    protected void onResume() {
        super.onResume();
        mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if(null !=mOpenCvCameraView)
        mOpenCvCameraView.disableView();
    }

//    because capitalize is the new black
    private String capitalize(final String line) {
        return Character.toUpperCase(line.charAt(0)) + line.substring(1);
    }
}
