package cultoftheunicorn.marvel;

import static  com.googlecode.javacv.cpp.opencv_highgui.*;
import static  com.googlecode.javacv.cpp.opencv_core.*;

import static  com.googlecode.javacv.cpp.opencv_imgproc.*;
import static org.opencv.imgproc.Imgproc.getRotationMatrix2D;
import static org.opencv.imgproc.Imgproc.warpAffine;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;

import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.MatOfRect;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;

import com.googlecode.javacv.cpp.opencv_imgproc;
import com.googlecode.javacv.cpp.opencv_contrib.FaceRecognizer;
import com.googlecode.javacv.cpp.opencv_core.IplImage;
import com.googlecode.javacv.cpp.opencv_core.MatVector;

import android.graphics.Bitmap;
import android.media.FaceDetector;
import android.util.Log;

public  class PersonRecognizer {

	FaceRecognizer faceRecognizer;
	String mPath;
	int count=0;
	Labels labelsFile;

	private double DESIRED_LEFT_EYE_Y = 0.14;
	private double DESIRED_LEFT_EYE_X = 0.16;

	//private int FaceWidth = 100;
	//private int FaceHeight = 100;
	private int FaceWidth = 225;
	private int FaceHeight = 225;

	static  final int WIDTH= 225;
    static  final int HEIGHT= 225;
	//static  final int WIDTH= 128;
	//static  final int HEIGHT= 128;
    //private int mProb=99;
    private int mProb=999;


    private Mat GeometricalTransformation(Mat face, Mat warped, Rect leftEye, Rect rightEye)
	{
		Point left = new Point(leftEye.x + leftEye.width/2, leftEye.y + leftEye.height/2);
		Point right= new Point(rightEye.x + rightEye.width/2, rightEye.y + rightEye.height/2);
		Point eyesCenter = new Point((left.x + right.x)*0.5f, (left.y + right.y)*0.5f);

		//obtener el angulo entr los dos ojos
		double dy = (right.y - left.y);
		double dx = (right.x - left.x);
		double len = Math.sqrt(dx*dx + dy*dy);
		double angle = Math.atan2(dy,dx)*180.0/Math.PI;


		//--------------------------------------------------------------Calculate size of new matrix
		double radians = Math.toRadians(angle);
		double sin = Math.abs(Math.sin(radians));
		double cos = Math.abs(Math.cos(radians));
		int newWidth = (int) (face.width() * cos + face.height() * sin);
		int newHeight = (int) (face.width() * sin + face.height() * cos);

		// rotating image
		Point center = new Point(newWidth / 2, newHeight / 2);
		Mat rotMatrix = Imgproc.getRotationMatrix2D(center, angle, 1.0); //1.0 means 100 % scale

		Size size = new Size(newWidth, newHeight);
		Imgproc.warpAffine(face, face, rotMatrix, face.size());
		return face;
		//------------------------------------------------------------------------------------------

		//Mediciones manuales sugieren que el centro del ojo izquierdo idealmente
		//deberia estar a 0.19, 0.14 de una imagen de la cara escalada.
		//double DESIRED_RIGHT_EYE_X = (1.0f - DESIRED_LEFT_EYE_X);

		//Obtener cantidad para escalar la imagen para que sea el tamaño fijo deseado
		//double desiredLen = (DESIRED_RIGHT_EYE_X - DESIRED_LEFT_EYE_X) * FaceWidth;
		//double desiredLen = (DESIRED_RIGHT_EYE_X - DESIRED_LEFT_EYE_X) / len;
		//double scale = desiredLen / len;

		//Obtener la matriz de transformacion para girar y escalar la cara al angulo y tamaño calculado
		//Mat rot_mat = getRotationMatrix2D(eyesCenter, angle, scale);

		//Cambie el centro de los ojos para que sea el centro deseado entre los ojos.
		//rot_mat.at<double>(0,2) += FaceWidth * 0.5f - eyesCenter.x;
		//rot_mat.get(0,2)[0] = rot_mat.get(0,2)[0] + (100 * 0.5f - eyesCenter.x);
		//rot_mat.put(0,2, rot_mat.get(0,2));

		//rot_mat.at<double>(1,2) += FaceHeight * DESIRED_LEFT_EYE_Y - eyeCenter.y
		//rot_mat.get(1,2)[0] = rot_mat.get(1,2)[0] + 100 * DESIRED_LEFT_EYE_Y - eyesCenter.y;
		//rot_mat.put(1,2, rot_mat.get(1,2));

		//warped = new Mat(100, 100, CV_8U, new Scalar(128));

		//warpAffine(face, warped, rot_mat, face.size());

		//return warped;
	}

	PersonRecognizer(String path) {
		//faceRecognizer =  com.googlecode.javacv.cpp.opencv_contrib.createLBPHFaceRecognizer(4,8,8,8,200);
		//faceRecognizer =  com.googlecode.javacv.cpp.opencv_contrib.createLBPHFaceRecognizer(3,8,8,8,200);

		faceRecognizer =  com.googlecode.javacv.cpp.opencv_contrib.createLBPHFaceRecognizer(2,8,8,8,200);

		//faceRecognizer =  com.googlecode.javacv.cpp.opencv_contrib.createLBPHFaceRecognizer(1,8,8,8,200);
		//faceRecognizer = com.googlecode.javacv.cpp.opencv_contrib.createFisherFaceRecognizer();

		//faceRecognizer = com.googlecode.javacv.cpp.opencv_contrib.createEigenFaceRecognizer();
		// path=Environment.getExternalStorageDirectory()+"/facerecog/faces/";
		mPath=path;
		labelsFile= new Labels(mPath);
	}

	void add(Mat m, String description) {
		Bitmap bmp= Bitmap.createBitmap(m.width(), m.height(), Bitmap.Config.ARGB_8888);

		Utils.matToBitmap(m,bmp);
		bmp= Bitmap.createScaledBitmap(bmp, WIDTH, HEIGHT, false);

		FileOutputStream f;
		try {
			f = new FileOutputStream(mPath+description+"-"+count+".jpg",true);
			count++;
			bmp.compress(Bitmap.CompressFormat.JPEG, 100, f);
			f.close();

		} catch (Exception e) {
			Log.e("error",e.getCause()+" "+e.getMessage());
			e.printStackTrace();

		}
	}

	public boolean train() {

		File root = new File(mPath);

		FilenameFilter pngFilter = new FilenameFilter() {
			public boolean accept(File dir, String name) {
				return name.toLowerCase().endsWith(".jpg");

			};
		};

		File[] imageFiles = root.listFiles(pngFilter);

		MatVector images = new MatVector(imageFiles.length);

		int[] labels = new int[imageFiles.length];

		int counter = 0;
		int label;

		IplImage img;
		IplImage grayImg;

		//--//
		IplImage imageProcessed;
		//////

		int i1=mPath.length();

		for (File image : imageFiles) {
			String p = image.getAbsolutePath();
			img = cvLoadImage(p);

			if (img==null)
				Log.e("Error","Error cVLoadImage");
			Log.i("image",p);

			int i2=p.lastIndexOf("-");
			int i3=p.lastIndexOf(".");
			int icount=Integer.parseInt(p.substring(i2+1,i3));
			if (count<icount) count++;

			String description=p.substring(i1,i2);

			if (labelsFile.get(description)<0)
				labelsFile.add(description, labelsFile.max()+1);

			label = labelsFile.get(description);

			cvSaveImage(mPath + "NOGRAY.jpg",img);

			grayImg = IplImage.create(img.width(), img.height(), IPL_DEPTH_8U, 1);
			cvCvtColor(img, grayImg, CV_BGR2GRAY);

			cvSaveImage(mPath + "YAGRAYNOEQUALIZADO.jpg",grayImg);
			//------------------------------------------------------------------------------------//
			//imageProcessed = cvCreateImage(cvSize(WIDTH, HEIGHT), IPL_DEPTH_8U, 1);
			imageProcessed = cvCreateImage(cvSize(128, 128), IPL_DEPTH_8U, 1);

            // Make the image a fixed size.
            // CV_INTER_CUBIC or CV_INTER_LINEAR is good for enlarging, and
			// CV_INTER_AREA is good for shrinking / decimation, but bad at enlarging.
			cvResize(grayImg, imageProcessed, CV_INTER_LINEAR);
			// Give the image a standard brightness and contrast.
			cvEqualizeHist(imageProcessed, imageProcessed);
			//------------------------------------------------------------------------------------//

			cvSaveImage(mPath + "GRAYEQUALIAADO.jpg",imageProcessed);


			//images.put(counter, grayImg);
			images.put(counter, imageProcessed);

			labels[counter] = label;

			counter++;
		}
		if (counter>0)
			if (labelsFile.max()>1)
				faceRecognizer.train(images, labels);
		labelsFile.Save();
		return true;
	}

	public boolean canPredict()
	{
		if (labelsFile.max()>1)
			return true;
		else
			return false;

	}

	public String predict(Mat m, Rect lEye, Rect rEye) {
		if (!canPredict())
			return "";
		int n[] = new int[1];
		double p[] = new double[1];

		/*GEOMETRICAL TRANFORMATION*/
		m = GeometricalTransformation(m, m, lEye, rEye);
		/////////////////////////////

		IplImage ipl = MatToIplImage(m,WIDTH, HEIGHT);
//		IplImage ipl = MatToIplImage(m,-1, -1);


		//--//
		IplImage imageProcessed;
		IplImage grayImg;
        //////

		//grayImg = IplImage.create(WIDTH, HEIGHT, IPL_DEPTH_8U, 1);
		//cvCvtColor(ipl, grayImg, CV_BGR2GRAY);

        //------------------------------------------------------------------------------------//
        //imageProcessed = cvCreateImage(cvSize(WIDTH, HEIGHT), IPL_DEPTH_8U, 1);
		imageProcessed = cvCreateImage(cvSize(128, 128), IPL_DEPTH_8U, 1);

        // Make the image a fixed size.
        // CV_INTER_CUBIC or CV_INTER_LINEAR is good for enlarging, and
        // CV_INTER_AREA is good for shrinking / decimation, but bad at enlarging.
		cvResize(ipl, imageProcessed, CV_INTER_LINEAR);
        // Give the image a standard brightness and contrast.

		cvSaveImage(mPath + "noequalizado.jpg",imageProcessed);

		cvEqualizeHist(imageProcessed, imageProcessed);
        //------------------------------------------------------------------------------------//

		cvSaveImage(mPath + "equalizado.jpg",imageProcessed);


		faceRecognizer.predict(imageProcessed, n, p);
		//faceRecognizer.predict(ipl, n, p);

		if (n[0]!=-1)
			mProb=(int)p[0];
		else
			mProb=-1;
		//if ((n[0] != -1)&&(p[0]<50))

		if ((n[0] != -1)&&(p[0]<=50))
		//if (n[0] != -1)
			return labelsFile.get(n[0]) + String.valueOf(p[0]) + "->" + String.valueOf(n[0]);
		else
			//return "**Unknown**";

		   return "Unknown- Score .-" + String.valueOf(p[0]) + "->" + String.valueOf(n[0]);
		   //return "Person.-" + " [" + labelsFile.get(n[0]) + "]. " ;
	}




	IplImage MatToIplImage(Mat m,int width,int heigth)
	{


		Bitmap bmp=Bitmap.createBitmap(m.width(), m.height(), Bitmap.Config.ARGB_8888);


		Utils.matToBitmap(m, bmp);
		return BitmapToIplImage(bmp,width, heigth);

	}

	IplImage BitmapToIplImage(Bitmap bmp, int width, int height) {

		if ((width != -1) || (height != -1)) {
			Bitmap bmp2 = Bitmap.createScaledBitmap(bmp, width, height, false);
			bmp = bmp2;
		}

		IplImage image = IplImage.create(bmp.getWidth(), bmp.getHeight(),
				IPL_DEPTH_8U, 4);

		bmp.copyPixelsToBuffer(image.getByteBuffer());

		IplImage grayImg = IplImage.create(image.width(), image.height(),
				IPL_DEPTH_8U, 1);

		cvCvtColor(image, grayImg, opencv_imgproc.CV_BGR2GRAY);

		return grayImg;
	}

	protected void SaveBmp(Bitmap bmp,String path)
	{
		FileOutputStream file;
		try {
			file = new FileOutputStream(path , true);

			bmp.compress(Bitmap.CompressFormat.JPEG,100,file);
			file.close();
		}
		catch (Exception e) {
			// TODO Auto-generated catch block
			Log.e("",e.getMessage()+e.getCause());
			e.printStackTrace();
		}

	}


	public void load() {
		train();
	}

	public int getProb() {
		// TODO Auto-generated method stub
		return mProb;
	}
}
