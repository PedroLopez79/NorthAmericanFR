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
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.cultoftheunicorn.marvel.R;
import org.opencv.imgproc.Imgproc;
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
import java.util.List;

import org.apache.http.util.EncodingUtils;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import cultoftheunicorn.marvel.dao.EmpleadoDAO;
import cultoftheunicorn.marvel.modelo.Empleado;

import static com.googlecode.javacv.cpp.opencv_core.CV_8U;
import static com.googlecode.javacv.cpp.opencv_core.CV_8UC1;
import static org.opencv.imgproc.Imgproc.warpAffine;

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

    private File mCascadeFile, mCascadeFileER, mCascadeFileEL;
    private CascadeClassifier mJavaDetector, mCascadeER, mCascadeEL;

    // rectangulos donde estan los ojos y cara
    private Rect rostro = null;
    private Rect lEye = null;
    private Rect rEye = null;

    private int                    mDetectorType       = JAVA_DETECTOR;
    private String[]               mDetectorName;

    private float                  mRelativeFaceSize   = 0.2f;
    private int                    mAbsoluteFaceSize   = 0;
    private int mLikely=999;

    private double DESIRED_LEFT_EYE_Y = 0.14;
    private double DESIRED_LEFT_EYE_X = 0.16;

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
    String Nombres, Apellidos, Domicilio, Ciudad, Telefono, Proyecto, IDPROYECTO;
    //--------------------------------------------------------------------------------------------//
    public static final int SIGNATURE_ACTIVITY = 1;
    String TAG2 = "Response";
    String resultString;
    String un,pass,db,ip,usuarioid, nombreusuario, numestacion;

    ArrayList<Bitmap> bitmapArray = new ArrayList<Bitmap>();
    String dbface = "REGISTERING";
    //--------------------------------------------------------------------------------------------//
    String modoremoto = "";
    byte[] byte1; byte[] byte2; byte[] byte3; byte[] byte4; byte[] byte5; byte[] byte6;
    byte[] byte7; byte[] byte8; byte[] byte9; byte[] byte10;
    //--------------------------------------------------------------------------------------------//

    Labels labelsFile;
    static {
        OpenCVLoader.initDebug();
        System.loadLibrary("opencv_java");
    }

    private Mat ElipticalMask(Mat face)
    {
        Mat mask = new Mat(face.rows(), face.cols(), CV_8UC1, Scalar.all(0));

        double dw = face.cols();
        double dh = face.rows();
        Point faceCenter = new Point(Math.round(dw*0.5), Math.round(dh*0.5));
        Size size = new Size(Math.round(dw*0.35), Math.round(dh*0.55));

        Core.ellipse( mask,
                faceCenter,
                size,
                0,
                0.0,
                360.0,
                new Scalar( 255, 255, 255 ),
                -1,
                8,
                0 );

        Mat cropped = new Mat(face.rows(), face.cols(), CV_8UC1, Scalar.all(0));

        face.copyTo( cropped, mask );

        return cropped;
    }

    private Mat histogramequalization(Mat face)
    {
        int w = face.cols();
        int h = face.rows();
        Mat wholeFace = new Mat();
        //cvEqualizeHist(face, wholeFace);
        Imgproc.equalizeHist(face, wholeFace);
        int midX = w/2;

        Rect RectCrop1 = new Rect(0, 0, midX, h);
        Mat leftSide = new Mat(face, RectCrop1);
        Rect RectCrop2 = new Rect(midX, 0, w-midX,h);
        Mat rightSide= new Mat(face, RectCrop2);
        //cvEquilizeHist(leftSide, leftSide);
        //cvEquilizeHist(rightSide, rightSide);
        Imgproc.equalizeHist(leftSide, leftSide);
        Imgproc.equalizeHist(rightSide, rightSide);


        for (int y =0; y <h; y++)
        {
            for (int x=0; x<w; x++)
            {
                double v =  0;
                if (x < w/4) {
                    //LEFT 25%: just use the left face.
                    //v = leftSide.at<uchar>(y,x);
                    v = leftSide.get(y,x)[0];
                }
                else if (x< w*2/4){
                    //Mide-left 25%: blend the left face & whole face.
                    //int lv = leftSide.at<uchar>(y,x);
                    double lv = leftSide.get(y,x)[0];
                    //int wv = wholeFace.at<uchar>(y,x);
                    double wv = wholeFace.get(y,x)[0];

                    //Blend more of the whole face as it moves
                    //further right along the face.
                    float f = (x - w*1/4) / (float) (w/4);
                    v = Math.round((1.0f - f) * lv + (f) * wv);
                }
                else if (x < w*3/4) {
                    //Mid-right 25%: blend right face & whole face.
                    //int rv = rightSide.at<uchar>(y,x-midX);
                    double rv = rightSide.get(y,x-midX)[0];
                    //int wv = wholeFace.at<uchar>(y,x);
                    double wv = wholeFace.get(y,x)[0];

                    //Blend more of the right-side face as it moves
                    //further right along the face.
                    float f = (x-w*2/4)/(float)(w/4);
                    v = Math.round((1.0f - f) * wv + (f) *rv);
                }
                else {
                    // Right 25%: just use the right face.
                    //v = rightSide.at<uchar>(y,x-midX);
                    v = rightSide.get(y,x-midX)[0];
                }
                //face.at<uchar>(y,x) = v;
                face.put(y,x,v);
            }
        }

        Mat warped = new Mat(100, 100, CV_8U, new Scalar(128));

        Mat filtered = new Mat(warped.size(), CV_8U);
        Imgproc.bilateralFilter(face, filtered, 0, 20.0, 2.0);

        return filtered;
    }

    private Mat AlignFace(Mat face, Mat warped, Rect leftEye, Rect rightEye)
    //private Mat GeometricalTransformation(Mat face, Mat warped, Point leftEye, Point rightEye)
    {
        Point left = new Point(leftEye.x + leftEye.width/2, leftEye.y + leftEye.height/2);
        Point right= new Point(rightEye.x + rightEye.width/2, rightEye.y + rightEye.height/2);
        //Point left = leftEye;
        //Point right = rightEye;
        Point eyesCenter = new Point((left.x + right.x)*0.5f, (left.y + right.y)*0.5f);

        //obtener el angulo entr los dos ojos
        double dy = (right.y - left.y);
        double dx = (right.x - left.x);
        double len = Math.sqrt(dx*dx + dy*dy);
        //double angle = Math.atan2(dy,dx)*180.0/Math.PI;
        double angle = Math.atan2(dy,dx)*180/Math.PI;


        //--------------------------------------------------------------Calculate size of new matrix
        double radians = Math.toRadians(angle);
        double sin = Math.abs(Math.sin(radians));
        double cos = Math.abs(Math.cos(radians));
        int newWidth = (int) (face.width() * cos + face.height() * sin);
        int newHeight = (int) (face.width() * sin + face.height() * cos);

        // rotating image
        //Point center = new Point(newWidth / 2, newHeight / 2);
        //Mat rotMatrix = Imgproc.getRotationMatrix2D(center, angle, 1.0); //1.0 means 100 % scale
        //Size size = new Size(newWidth, newHeight);
        //Imgproc.warpAffine(face, face, rotMatrix, face.size());
        //return face;
        //------------------------------------------------------------------------------------------

        //Mediciones manuales sugieren que el centro del ojo izquierdo idealmente
        //deberia estar a 0.19, 0.14 de una imagen de la cara escalada.
        double DESIRED_RIGHT_EYE_X = (1.0f - DESIRED_LEFT_EYE_X);

        //Obtener cantidad para escalar la imagen para que sea el tamaño fijo deseado
        //double desiredLen = (DESIRED_RIGHT_EYE_X - DESIRED_LEFT_EYE_X) * FaceWidth;
        double desiredLen = (DESIRED_RIGHT_EYE_X - DESIRED_LEFT_EYE_X) / len;
        long scale = Math.round(desiredLen / len);

        //Obtener la matriz de transformacion para girar y escalar la cara al angulo y tamaño calculado
        //Mat rot_mat = getRotationMatrix2D(eyesCenter, angle, scale);
        Mat rotMatrix;
        if (angle < 100)
        {
            rotMatrix = Imgproc.getRotationMatrix2D(eyesCenter, angle, 1); //1.0 means 100 % scale
        }
        else {
            rotMatrix = Imgproc.getRotationMatrix2D(eyesCenter, angle + 180, 1); //1.0 means 100 % scale
        };

        //Cambie el centro de los ojos para que sea el centro deseado entre los ojos.
        //rot_mat.at<double>(0,2) += FaceWidth * 0.5f - eyesCenter.x;
        rotMatrix.get(0,2)[0] = rotMatrix.get(0,2)[0] + (100 * 0.5f - eyesCenter.x);
        rotMatrix.put(0,2, rotMatrix.get(0,2));

        //rot_mat.at<double>(1,2) += FaceHeight * DESIRED_LEFT_EYE_Y - eyeCenter.y
        rotMatrix.get(1,2)[0] = rotMatrix.get(1,2)[0] + 100 * DESIRED_LEFT_EYE_Y - eyesCenter.y;
        rotMatrix.put(1,2, rotMatrix.get(1,2));

        warped = new Mat(100, 100, CV_8U, new Scalar(128));
        warpAffine(face, warped, rotMatrix, face.size());

        return warped;
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
                        InputStream is = getResources().openRawResource(R.raw.haarcascade_frontalface_default);
                        File cascadeDir = getDir("cascade", Context.MODE_PRIVATE);
                        mCascadeFile = new File(cascadeDir, "haarcascade_frontalface_default.xml");
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
        modoremoto= prefs.getString("modoremoto", "SI");
//------------------------------------------------------------------------------------------------//
        text    = getIntent().getStringExtra("name");
        Nombres = getIntent().getStringExtra("Nombres");
        Apellidos = getIntent().getStringExtra("Apellidos");
        Domicilio = getIntent().getStringExtra("Domicilio");
        Ciudad = getIntent().getStringExtra("Ciudad");
        Telefono = getIntent().getStringExtra("Telefono");
        Proyecto = getIntent().getStringExtra("Proyecto");

        IDPROYECTO = Proyecto.substring(1,Proyecto.indexOf("]"));
//------------------------------------------------------------------------------------------------//

        if (text == null) text= "";

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

    public void RegistraRostrosLC()
    {
        String nombre;
        //-----variable para imagen base64--------------------------------------------------------//
        String encodedImage2;
        String IMAGE1, IMAGE2, IMAGE3, IMAGE4, IMAGE5, IMAGE6, IMAGE7, IMAGE8, IMAGE9, IMAGE10;

        IMAGE1 = ""; IMAGE2 = "";  IMAGE3 = "";  IMAGE4 = "";  IMAGE5 = ""; IMAGE6 = ""; IMAGE7 = "";
        IMAGE8 = ""; IMAGE9 = "";  IMAGE10 = "";

        Bitmap image = null;
        //----------------------------------------------------------------------------------------//
        EmpleadoDAO empleado = new EmpleadoDAO(this);
        //----------------------------------------------------------------------------------------//
        nombre = getIntent().getStringExtra("name");
        encodedImage2 = "";
        if (bitmapArray.size() > 0) {
            image = bitmapArray.get(0);

            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            image.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream);
            byte1 = byteArrayOutputStream.toByteArray();
            //encodedImage2 = Base64.encodeToString(byte1, Base64.DEFAULT);
        }
        if (bitmapArray.size() > 0) {
            image = bitmapArray.get(1);

            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            image.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream);
            byte2 = byteArrayOutputStream.toByteArray();
            //encodedImage2 = Base64.encodeToString(byte1, Base64.DEFAULT);
        }
        if (bitmapArray.size() > 0) {
            image = bitmapArray.get(2);

            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            image.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream);
            byte3 = byteArrayOutputStream.toByteArray();
            //encodedImage2 = Base64.encodeToString(byte1, Base64.DEFAULT);
        }
        if (bitmapArray.size() > 0) {
            image = bitmapArray.get(3);

            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            image.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream);
            byte4 = byteArrayOutputStream.toByteArray();
            //encodedImage2 = Base64.encodeToString(byte1, Base64.DEFAULT);
        }
        if (bitmapArray.size() > 0) {
            image = bitmapArray.get(4);

            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            image.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream);
            byte5 = byteArrayOutputStream.toByteArray();
            //encodedImage2 = Base64.encodeToString(byte1, Base64.DEFAULT);
        }
        if (bitmapArray.size() > 0) {
            image = bitmapArray.get(5);

            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            image.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream);
            byte6 = byteArrayOutputStream.toByteArray();
            //encodedImage2 = Base64.encodeToString(byte1, Base64.DEFAULT);
        }
        if (bitmapArray.size() > 0) {
            image = bitmapArray.get(6);

            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            image.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream);
            byte7 = byteArrayOutputStream.toByteArray();
            //encodedImage2 = Base64.encodeToString(byte1, Base64.DEFAULT);
        }
        if (bitmapArray.size() > 0) {
            image = bitmapArray.get(7);

            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            image.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream);
            byte8 = byteArrayOutputStream.toByteArray();
            //encodedImage2 = Base64.encodeToString(byte1, Base64.DEFAULT);
        }
        if (bitmapArray.size() > 0) {
            image = bitmapArray.get(8);

            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            image.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream);
            byte9 = byteArrayOutputStream.toByteArray();
            //encodedImage2 = Base64.encodeToString(byte1, Base64.DEFAULT);
        }
        if (bitmapArray.size() > 0) {
            image = bitmapArray.get(9);

            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            image.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream);
            byte10 = byteArrayOutputStream.toByteArray();
            //encodedImage2 = Base64.encodeToString(byte1, Base64.DEFAULT);
        }

        dbface = "REGISTERDONE";
        Empleado createdempleado1 = empleado.createEmpleado(nombre.toString().replace("'",""), "", "", "", "", "", "", "", "", "", byte1,
                2601, 1, byte1, byte2, byte3, byte4, byte5, byte6, byte7, byte8, byte9, byte10, 1, "LOCAL", "NO", Long.parseLong(IDPROYECTO));
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
            Request.addProperty("Domicilio", Domicilio);
            Request.addProperty("Ciudad", Ciudad);
            Request.addProperty("Telefono", Telefono);
            Request.addProperty("CuentaContable", "");
            Request.addProperty("Fecha", fecha);
            Request.addProperty("UsuarioID", "1");
            Request.addProperty("EstacionID", "2601");
            Request.addProperty("ProyectoID", IDPROYECTO);
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
            if (countImages==0){
                bitmapArray.clear();

                if (bitmapArray.equals(null))
                    bitmapArray = new ArrayList<Bitmap>();
            }
            faceState = TRAINING;
        }
        else {
            Toast.makeText(this, "Captured", Toast.LENGTH_SHORT).show();
            countImages=0;
            faceState=IDLE;
            Iv.setImageResource(R.drawable.user_image);


            //--MANDAR GUARDAR IMAGENES A BD EMPLEADO NUEVO---------------------------------------//
            if ((bitmapArray.size() == 10)&&(dbface == "REGISTERING")) {

                if (modoremoto.equals("NO")) {
                    //RUTINA DE GUARDADO DE IMAGENES EN BASE DE DATOS LOCAL---------------------------//
                    RegistraRostrosLC();
                    //--------------------------------------------------------------------------------//
                }
                else {
                    //RUTINA DE GUARDADO DE IMAGENES EN BASE DE DATOS REMOTA--------------------------//
                    RegistraRostros registrarostros = new RegistraRostros();// this is the Asynctask, which is used to process in background to reduce load on app process
                    registrarostros.execute("");
                    //--------------------------------------------------------------------------------//
                }

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
            }

            Mat m;
            Rect r=facesArray[0];
            m=mRgba.submat(r);

            /*GEOMETRICAL TRANFORMATION*/
            m = AlignFace(m, m, lEye, rEye);
            //m = histogramequalization(m);
            m = ElipticalMask(m);
            /////////////////////////////
            //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

            //Mat m;
            //Rect r=facesArray[0];
            //m=mRgba.submat(r);

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
