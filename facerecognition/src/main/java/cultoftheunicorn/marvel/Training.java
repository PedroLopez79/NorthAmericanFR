package cultoftheunicorn.marvel;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
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
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
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
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.apache.http.util.EncodingUtils;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

public class Training extends AppCompatActivity implements CameraBridgeViewBase.CvCameraViewListener2 {

    private static final String    TAG                 = "OCVSample::Activity";
    private static final Scalar FACE_RECT_COLOR     = new Scalar(0, 255, 0, 255);
    public static final int        JAVA_DETECTOR       = 0;
    public static final int        NATIVE_DETECTOR     = 1;

    public static final int TRAINING= 0;
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

    private Tutorial3View   mOpenCvCameraView;

    String text;
    private ImageView Iv;
    Bitmap mBitmap;
    Handler mHandler;

    PersonRecognizer fr;
    ToggleButton capture;

    static final long MAXIMG = 10;

    int countImages=0;

    //--------------------------------------------------------------------------------------------//
    public static final int SIGNATURE_ACTIVITY = 1;
    String TAG2 = "Response";
    String resultString;
    String un,pass,db,ip,usuarioid, nombreusuario, numestacion;

    ArrayList<Bitmap> bitmapArray = new ArrayList<Bitmap>();
    String dbface = "REGISTERING";
    //--------------------------------------------------------------------------------------------//

    Labels labelsFile;
    static {
        OpenCVLoader.initDebug();
        System.loadLibrary("opencv_java");
    }

    public Training() {
        mDetectorName = new String[2];
        mDetectorName[JAVA_DETECTOR] = "Java";
        mDetectorName[NATIVE_DETECTOR] = "Native (tracking)";

        Log.i(TAG, "Instantiated new " + this.getClass());
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_training);

        /*Toolbar toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);

        if(getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Training");
        }*/

        // Declaring Server ip, username, database name and password
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);

        ip = prefs.getString("ipservidor","IANSERVICES.DDNS.NET");
        db = prefs.getString("dbservidor","AmericanGreenHouseCheck");
        un = prefs.getString("usuarioservidor","sa");
        pass = prefs.getString("passwordservidor","IAN32");
        numestacion = prefs.getString("numeroestacion","2601");

        text = getIntent().getStringExtra("name");
        Iv = (ImageView) findViewById(R.id.imagePreview);

        capture = (ToggleButton) findViewById(R.id.capture);
        capture.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                captureOnClick();
            }
        });

        mOpenCvCameraView = (Tutorial3View) findViewById(R.id.tutorial3_activity_java_surface_view);
        mOpenCvCameraView.setCvCameraViewListener(this);

        //mPath=getFilesDir()+"/facerecogOCV/";
        mPath = Environment.getExternalStorageDirectory()+"/facerecogOCV/";

        Log.e("Path", mPath);

        labelsFile= new Labels(mPath);

        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.obj=="IMG")
                {
                    Canvas canvas = new Canvas();
                    canvas.setBitmap(mBitmap);
                    Iv.setImageBitmap(mBitmap);
                    if (countImages>=MAXIMG-1)
                    {
                        capture.setChecked(false);
                        captureOnClick();
                    }
                }
            }
        };

        boolean success=(new File(mPath)).mkdirs();

        if (!success)
            Log.e("Error","Error creating directory");

    }

    public String conectar() {
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
        //-----variable para imagen base64--------------------------------------------------------//
        String encodedImage2;
        byte[] byteArray;
        Bitmap image = null;
        //----------------------------------------------------------------------------------------//

        nombre = getIntent().getStringExtra("name");
        try {
            SoapObject Request = new SoapObject(NAMESPACE, METHOD_NAME);
            Request.addProperty("Nombre", nombre);
            Request.addProperty("CodigoEmpleado", "");
            Request.addProperty("Domicilio", "");
            Request.addProperty("Ciudad", "");
            Request.addProperty("Telefono", "");
            Request.addProperty("CuentaContable", "");
            Request.addProperty("Fecha", fecha);
            Request.addProperty("UsuarioID", "1");
            Request.addProperty("EstacionID", "2601");
            //---------------------obtener las 10 imagenes ya capturadas--------------------------//
            encodedImage2 = "";
            if (bitmapArray.size() > 0) {
                image = bitmapArray.get(0);

                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                image.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream);
                byteArray = byteArrayOutputStream.toByteArray();
                encodedImage2 = Base64.encodeToString(byteArray, Base64.DEFAULT);
            }
            Request.addProperty("foto1base64", encodedImage2);

            encodedImage2 = "";
            if (bitmapArray.size() > 0) {
                image = bitmapArray.get(1);

                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                image.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream);
                byteArray = byteArrayOutputStream.toByteArray();
                encodedImage2 = Base64.encodeToString(byteArray, Base64.DEFAULT);
            }
            Request.addProperty("foto2base64", encodedImage2);

            encodedImage2 = "";
            if (bitmapArray.size() > 0) {
                image = bitmapArray.get(2);

                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                image.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream);
                byteArray = byteArrayOutputStream.toByteArray();
                encodedImage2 = Base64.encodeToString(byteArray, Base64.DEFAULT);
            }
            Request.addProperty("foto3base64", encodedImage2);

            encodedImage2 = "";
            if (bitmapArray.size() > 0) {
                image = bitmapArray.get(3);

                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                image.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream);
                byteArray = byteArrayOutputStream.toByteArray();
                encodedImage2 = Base64.encodeToString(byteArray, Base64.DEFAULT);
            }
            Request.addProperty("foto4base64", encodedImage2);

            encodedImage2 = "";
            if (bitmapArray.size() > 0) {
                image = bitmapArray.get(4);

                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                image.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream);
                byteArray = byteArrayOutputStream.toByteArray();
                encodedImage2 = Base64.encodeToString(byteArray, Base64.DEFAULT);
            }
            Request.addProperty("foto5base64", encodedImage2);

            encodedImage2 = "";
            if (bitmapArray.size() > 0) {
                image = bitmapArray.get(5);

                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                image.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream);
                byteArray = byteArrayOutputStream.toByteArray();
                encodedImage2 = Base64.encodeToString(byteArray, Base64.DEFAULT);
            }
            Request.addProperty("foto6base64", encodedImage2);

            encodedImage2 = "";
            if (bitmapArray.size() > 0) {
                image = bitmapArray.get(6);

                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                image.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream);
                byteArray = byteArrayOutputStream.toByteArray();
                encodedImage2 = Base64.encodeToString(byteArray, Base64.DEFAULT);
            }
            Request.addProperty("foto7base64", encodedImage2);

            encodedImage2 = "";
            if (bitmapArray.size() > 0) {
                image = bitmapArray.get(7);

                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                image.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream);
                byteArray = byteArrayOutputStream.toByteArray();
                encodedImage2 = Base64.encodeToString(byteArray, Base64.DEFAULT);
            }
            Request.addProperty("foto8base64", encodedImage2);

            encodedImage2 = "";
            if (bitmapArray.size() > 0) {
                image = bitmapArray.get(8);

                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                image.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream);
                byteArray = byteArrayOutputStream.toByteArray();
                encodedImage2 = Base64.encodeToString(byteArray, Base64.DEFAULT);
            }
            Request.addProperty("foto9base64", encodedImage2);

            encodedImage2 = "";
            if (bitmapArray.size() > 0) {
                image = bitmapArray.get(9);

                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                image.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream);
                byteArray = byteArrayOutputStream.toByteArray();
                encodedImage2 = Base64.encodeToString(byteArray, Base64.DEFAULT);
            }
            Request.addProperty("foto10base64", encodedImage2);
            //------------------------------------------------------------------------------------//

            SoapSerializationEnvelope soapEnvelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            soapEnvelope.dotNet = true;
            soapEnvelope.setOutputSoapObject(Request);
            HttpTransportSE transport = new HttpTransportSE(URL, 80000);

            if (dbface.equals("REGISTERING")) {
                transport.call(SOAP_ACTION, soapEnvelope);
            }
            //resultString = (SoapPrimitive) soapEnvelope.getResponse();
            Object  response = (Object) soapEnvelope.getResponse();
            resultString = response.toString();

            dbface = "REGISTERDONE";
            return resultString;

            //Log.i(TAG, "Result Celsius: " + resultString);
        } catch (Exception ex) {
            Log.e(TAG2, "Error: " + ex.getMessage());
        }
        return SOAP_ACTION;
    }

    public class RegistraRostros extends AsyncTask<String,String,String>
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
            Toast.makeText(Training.this, r, Toast.LENGTH_SHORT).show();
            if(isSuccess)
            {
                Toast.makeText(Training.this , "Login Exitoso" , Toast.LENGTH_LONG).show();
                //finish();
            }
            if(!isSuccess)
            {
                Toast.makeText(Training.this , "Usuario o Clave incorrecta" , Toast.LENGTH_LONG).show();
            }
        }
        @Override
        protected String doInBackground(String... params)
        {
            resultString = conectar();

            if (resultString == "OK"){
                Toast.makeText(Training.this, "ROSTRO REGISTRADO CORRECTAMENTE", Toast.LENGTH_SHORT).show();
            }

            return resultString;
        }
    }

    void captureOnClick()
    {
        if (capture.isChecked()) {
            if (countImages==0){ bitmapArray.clear();}
            faceState = TRAINING;
        }
        else {
            Toast.makeText(this, "Captured", Toast.LENGTH_SHORT).show();
            countImages=0;
            faceState=IDLE;
            Iv.setImageResource(R.drawable.user_image);


            //--MANDAR GUARDAR IMAGENES A BD EMPLEADO NUEVO---------------------------------------//
            if ((bitmapArray.size() == 10)&&(dbface == "REGISTERING")) {
                RegistraRostros registrarostros = new RegistraRostros();// this is the Asynctask, which is used to process in background to reduce load on app process
                registrarostros.execute("");

                finish();
            }
            //------------------------------------------------------------------------------------//
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

        if ((facesArray.length==1)&&(faceState==TRAINING)&&(countImages<MAXIMG)&&(!text.equals("")))
        {
            Mat m;
            Rect r=facesArray[0];


            m=mRgba.submat(r);
            mBitmap = Bitmap.createBitmap(m.width(),m.height(), Bitmap.Config.ARGB_8888);

            //----LIMPIAR ARREGLO DE BITMAPS------------------------------------------------------//
            if (bitmapArray.size() < MAXIMG)
            {
                bitmapArray.add(mBitmap);
            }
            //------------------------------------------------------------------------------------//

            Utils.matToBitmap(m, mBitmap);

            Message msg = new Message();
            String textTochange = "IMG";
            msg.obj = textTochange;
            mHandler.sendMessage(msg);
            if (countImages<MAXIMG)
            {
                fr.add(m, text);
                countImages++;
            }

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
}
