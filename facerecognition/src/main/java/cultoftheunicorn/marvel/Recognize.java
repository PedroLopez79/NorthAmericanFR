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
import android.widget.ProgressBar;
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
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.cultoftheunicorn.marvel.R;
import org.opencv.objdetect.CascadeClassifier;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.apache.http.util.EncodingUtils;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

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

    private int faceState=IDLE;

    private Mat                    mRgba;
    private Mat                    mGray;
    private File mCascadeFile;
    private CascadeClassifier mJavaDetector;

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
    String NOMBRES;
    String band = "Disable Scann";
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
                        InputStream is = getResources().openRawResource(R.raw.lbpcascade_frontalface);
                        File cascadeDir = getDir("cascade", Context.MODE_PRIVATE);
                        mCascadeFile = new File(cascadeDir, "lbpcascade.xml");
                        FileOutputStream os = new FileOutputStream(mCascadeFile);

                        byte[] buffer = new byte[4096];
                        int bytesRead;
                        while ((bytesRead = is.read(buffer)) != -1) {
                            os.write(buffer, 0, bytesRead);
                        }
                        is.close();
                        os.close();

                        mJavaDetector = new CascadeClassifier(mCascadeFile.getAbsolutePath());
                        if (mJavaDetector.empty()) {
                            Log.e(TAG, "Failed to load cascade classifier");
                            mJavaDetector = null;
                        } else
                            Log.i(TAG, "Loaded cascade classifier from " + mCascadeFile.getAbsolutePath());

                        cascadeDir.delete();

                    } catch (IOException e) {
                        e.printStackTrace();
                        Log.e(TAG, "Failed to load cascade. Exception thrown: " + e);
                    }

                    mOpenCvCameraView.setCamFront();
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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recognize);

        scan = (ToggleButton) findViewById(R.id.scan);
        final TextView results = (TextView) findViewById(R.id.results);

        // Declaring Server ip, username, database name and password
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        ip = prefs.getString("ipservidor","IANSERVICES.DDNS.NET");
        db = prefs.getString("dbservidor","AmericanGreenHouseCheck");
        un = prefs.getString("usuarioservidor","sa");
        pass = prefs.getString("passwordservidor","IAN32");
        numestacion = prefs.getString("numeroestacion","2601");

        //mPath=getFilesDir()+"/facerecogOCV/";
        mPath = Environment.getExternalStorageDirectory()+"/facerecogOCV/";
        //---------RUTINA DE LIMPIADO DE ARCHIVOS EN DIRECTORIO facerecogOCV----------------------//
        deleteRecursive(mPath);

        //---------RUTINA PARA GENERAR LOS ARCHIVO JPG, 10 imagenes default- 10 imagens sujeto a autenticar//
        ObtenRostros obtenrostros = new ObtenRostros();// this is the Asynctask, which is used to process in background to reduce load on app process
        obtenrostros.execute("");

        mOpenCvCameraView = (Tutorial3View) findViewById(R.id.tutorial3_activity_java_surface_view);
        mOpenCvCameraView.setCvCameraViewListener(this);

        Log.e("Path", mPath);

        //-------------------------POSICION ORIGINAL DE LABELSFILE--------------------------------//
        labelsFile= new Labels(mPath);
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
                }
                else
                {
                    results.setText("");
                    results.append("Unknown" + "\n");
                }
            }
        };

        scan.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b) {
                    if(!fr.canPredict()) {
                        scan.setChecked(false);
                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.SCanntoPredic), Toast.LENGTH_LONG).show();
                        return;
                    }
                    faceState = SEARCHING;
                }
                else {
                    faceState = IDLE;
                }
            }
        });

        boolean success=(new File(mPath)).mkdirs();
        if (!success)
        {
            Log.e("Error","Error creating directory");
        }

        Button submit = (Button) findViewById(R.id.submit);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(uniqueNames.size() > 0) {
                    Intent intent = new Intent(Recognize.this, ReviewResults.class);
                    intent.putExtra("list", uniqueNamesArray);
                    startActivity(intent);
                }
                else {
                    Toast.makeText(Recognize.this, "Empty list cannot be sent further", Toast.LENGTH_LONG).show();
                }
            }
        });

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
        Boolean isSuccess = false;

        @Override
        protected void onPreExecute()
        {
            /*progressBar.setVisibility(View.VISIBLE);*/
        }

        @Override
        protected void onPostExecute(String r)
        {
            //progressBar.setVisibility(View.GONE);
            Toast.makeText(Recognize.this, r, Toast.LENGTH_SHORT).show();
            if(isSuccess)
            {
                Toast.makeText(Recognize.this , "Login Exitoso" , Toast.LENGTH_LONG).show();
                if (band.equals("EnableScann"))
                {
                    scan.setEnabled(true);
                }
            }
            if(!isSuccess)
            {
                Toast.makeText(Recognize.this , "Usuario o Clave incorrecta" , Toast.LENGTH_LONG).show();
                if (band.equals("EnableScann"))
                {
                    scan.setEnabled(true);
                }
            }
        }
        @Override
        protected String doInBackground(String... params)
        {
            Intent intent = getIntent();
            String idempleado = intent.getStringExtra("IDEMPLEADO");

            resultString = conectar(idempleado);

            if (!resultString.equals("")){
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

                        IMAGE1 ="";IMAGE2="";IMAGE3="";IMAGE4="";IMAGE5="";IMAGE6="";IMAGE7="";
                        IMAGE8 ="";IMAGE9="";IMAGE10 = "";

                        IMAGEDEFAULT1 ="";IMAGEDEFAULT2 ="";IMAGEDEFAULT3 ="";IMAGEDEFAULT4 ="";
                        IMAGEDEFAULT5 ="";IMAGEDEFAULT6 ="";IMAGEDEFAULT7 ="";IMAGEDEFAULT8 ="";
                        IMAGEDEFAULT9 ="";IMAGEDEFAULT10 ="";

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
                        savebitmap(NOMBRES+"-1",IMAGE1);
                        savebitmap(NOMBRES+"-2",IMAGE2);
                        savebitmap(NOMBRES+"-3",IMAGE3);
                        savebitmap(NOMBRES+"-4",IMAGE4);
                        savebitmap(NOMBRES+"-5",IMAGE5);
                        savebitmap(NOMBRES+"-6",IMAGE6);
                        savebitmap(NOMBRES+"-7",IMAGE7);
                        savebitmap(NOMBRES+"-8",IMAGE8);
                        savebitmap(NOMBRES+"-9",IMAGE9);
                        savebitmap(NOMBRES+"-10",IMAGE10);

                        savebitmap("DEFAULT-"+"1",IMAGEDEFAULT1);
                        savebitmap("DEFAULT-"+"2",IMAGEDEFAULT2);
                        savebitmap("DEFAULT-"+"3",IMAGEDEFAULT3);
                        savebitmap("DEFAULT-"+"4",IMAGEDEFAULT4);
                        savebitmap("DEFAULT-"+"5",IMAGEDEFAULT5);
                        savebitmap("DEFAULT-"+"6",IMAGEDEFAULT6);
                        savebitmap("DEFAULT-"+"7",IMAGEDEFAULT7);
                        savebitmap("DEFAULT-"+"8",IMAGEDEFAULT8);
                        savebitmap("DEFAULT-"+"9",IMAGEDEFAULT9);
                        savebitmap("DEFAULT-"+"10",IMAGEDEFAULT10);
                    }
                }
                /*--------------------------------------------------------------------------------*/
                band= "EnableScann";
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

    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
        mRgba = inputFrame.rgba();
        mGray = inputFrame.gray();

        if (mAbsoluteFaceSize == 0) {
            int height = mGray.rows();
            if (Math.round(height * mRelativeFaceSize) > 0) {
                mAbsoluteFaceSize = Math.round(height * mRelativeFaceSize);
            }
            //  mNativeDetector.setMinFaceSize(mAbsoluteFaceSize);
        }

        MatOfRect faces = new MatOfRect();

        if (mDetectorType == JAVA_DETECTOR) {
            if (mJavaDetector != null)
                mJavaDetector.detectMultiScale(mGray, faces, 1.1, 2, 2, // TODO: objdetect.CV_HAAR_SCALE_IMAGE
                        new Size(mAbsoluteFaceSize, mAbsoluteFaceSize), new Size());
        }
        else if (mDetectorType == NATIVE_DETECTOR) {
            /*if (mNativeDetector != null)
                mNativeDetector.detect(mGray, faces);*/
        }
        else {
            Log.e(TAG, "Detection method is not selected!");
        }

        Rect[] facesArray = faces.toArray();

        if ((facesArray.length>0) && (faceState==SEARCHING))
        {
            Mat m=new Mat();
            m=mGray.submat(facesArray[0]);
            mBitmap = Bitmap.createBitmap(m.width(),m.height(), Bitmap.Config.ARGB_8888);


            Utils.matToBitmap(m, mBitmap);
            Message msg = new Message();
            String textTochange = "IMG";
            msg.obj = textTochange;
            //mHandler.sendMessage(msg);

            textTochange = fr.predict(m);
            mLikely=fr.getProb();
            msg = new Message();
            msg.obj = textTochange;
            mHandler.sendMessage(msg);

        }
        for (int i = 0; i < facesArray.length; i++)
            Core.rectangle(mRgba, facesArray[i].tl(), facesArray[i].br(), FACE_RECT_COLOR, 3);

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
        mOpenCvCameraView.disableView();
    }

//    because capitalize is the new black
    private String capitalize(final String line) {
        return Character.toUpperCase(line.charAt(0)) + line.substring(1);
    }
}
