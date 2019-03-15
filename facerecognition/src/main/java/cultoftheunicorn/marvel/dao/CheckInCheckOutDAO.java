package cultoftheunicorn.marvel.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import cultoftheunicorn.marvel.modelo.CheckInCheckOut;

public class CheckInCheckOutDAO {
    public static final String TAG = "CheckInCheckOutDAO";

    private SQLiteDatabase mDatabase;
    private AdminSqLiteOpenHelper mDbHelper;
    private Context mContext;
    private String[] mAllColumns = {
            AdminSqLiteOpenHelper.COLUMN_CHECKINCHECKOUTID,
            AdminSqLiteOpenHelper.COLUMN_CPROYECTOID,
            AdminSqLiteOpenHelper.COLUMN_CEMPLEADOID,
            AdminSqLiteOpenHelper.COLUMN_FCHECKS,
            AdminSqLiteOpenHelper.COLUMN_CFECHA,
            AdminSqLiteOpenHelper.COLUMN_CHECKINHECHO,
            AdminSqLiteOpenHelper.COLUMN_CSYNCRONIZADO,
            AdminSqLiteOpenHelper.COLUMN_CREGISTRO
    };

    public CheckInCheckOutDAO(Context context){
        this.mContext = context;
        mDbHelper = new AdminSqLiteOpenHelper(context);

        //open database
        try{
            open();
        } catch (SQLException e){
            Log.e(TAG, "Excepcion de SQL al abrir base de datos " + e.getMessage());
        }
    }

    public void open() throws SQLException{
        mDatabase = mDbHelper.getWritableDatabase();
    }

    public void close() {
        mDatabase.close();
    }

    public CheckInCheckOut createCheckInCheckOut(long ProyectoID, long EmpleadoID, long Checks, long Fecha, String CheckInHecho, String Syncronizado, String Registro) {
        ContentValues values = new ContentValues();
        values.put(AdminSqLiteOpenHelper.COLUMN_CPROYECTOID, ProyectoID);
        values.put(AdminSqLiteOpenHelper.COLUMN_CEMPLEADOID, EmpleadoID);
        values.put(AdminSqLiteOpenHelper.COLUMN_FCHECKS, Checks);
        values.put(AdminSqLiteOpenHelper.COLUMN_CFECHA, Fecha);
        values.put(AdminSqLiteOpenHelper.COLUMN_CHECKINHECHO, CheckInHecho);
        values.put(AdminSqLiteOpenHelper.COLUMN_CSYNCRONIZADO, Syncronizado);
        values.put(AdminSqLiteOpenHelper.COLUMN_CREGISTRO, Registro);
        long insertId = mDatabase
                .insert(AdminSqLiteOpenHelper.TABLE_CHECKINCHECKOUT, null, values);
        Cursor cursor = mDatabase.query(AdminSqLiteOpenHelper.TABLE_CHECKINCHECKOUT, mAllColumns,
                AdminSqLiteOpenHelper.COLUMN_CHECKINCHECKOUTID + " = " + insertId, null, null,
                null, null);
        cursor.moveToFirst();
        CheckInCheckOut newCheckInCheckOut = cursorToCheckInCheckOut(cursor);
        cursor.close();
        return newCheckInCheckOut;
    }

    public void deleteCheckInCheckOut(CheckInCheckOut CheckinCheckoutID) {
        long id = CheckinCheckoutID.getId();
        System.out.println("the deleted employee has the id: " + id);
        //mDatabase.delete(AdminSqLiteOpenHelper.TABLE_PROYECTO, AdminSqLiteOpenHelper.COLUMN_PROYECTOID
        //        + " = " + id, null);

        mDatabase.delete(AdminSqLiteOpenHelper.TABLE_CHECKINCHECKOUT, null, null);
    }

    public List<CheckInCheckOut> getAllCheckInCheckOut() {
        List<CheckInCheckOut> listCheckInCheckOut= new ArrayList<CheckInCheckOut>();

        Cursor cursor = mDatabase.query(AdminSqLiteOpenHelper.TABLE_CHECKINCHECKOUT, mAllColumns,
                null, null, null, null, null);

        //String POSTS_SELECT_QUERY =
        //        String.format("SELECT * FROM %s",
        //                AdminSqLiteOpenHelper.TABLE_EMPLEADOS);
        //Cursor cursor = mDatabase.rawQuery(POSTS_SELECT_QUERY, null);

        try {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                CheckInCheckOut proyect = cursorToCheckInCheckOut(cursor);
                listCheckInCheckOut.add(proyect);
                cursor.moveToNext();
            }
        }catch (Exception e) {
            Log.d(TAG, "Error while trying to get posts from database");
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }
        return listCheckInCheckOut;
    }

    public List<CheckInCheckOut> getAllCheckInCheckOutXFecha(long FECHA){
        List<CheckInCheckOut> listCheckInCheckOutXFecha= new ArrayList<CheckInCheckOut>();

        Cursor cursor = mDatabase.query(AdminSqLiteOpenHelper.TABLE_CHECKINCHECKOUT, mAllColumns,
                AdminSqLiteOpenHelper.COLUMN_CFECHA + " = ?",
                new String[] { String.valueOf(FECHA) }, null, null, null);

        try {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                CheckInCheckOut checkincheckout = cursorToCheckInCheckOut(cursor);
                listCheckInCheckOutXFecha.add(checkincheckout);
                cursor.moveToNext();
            }
        }catch (Exception e) {
            Log.d(TAG, "Error while trying to get posts from database");
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }

        return listCheckInCheckOutXFecha;
    }

    public Cursor getAllCheckInCheckOutXFechaDia(long FECHA, long IDEMPLEADO){

        Cursor cursor = mDatabase.rawQuery("SELECT strftime('%H',(datetime(ChechInCheckOut.CHECKS, 'unixepoch', 'localtime'))) AS HORADIAACTUAL, " +
                                                "CHECKINHECHO FROM ChechInCheckOut WHERE FECHA = "+ String.valueOf(FECHA) + " AND EMPLEADOID= " +
                                                String.valueOf(IDEMPLEADO),null);

        return cursor;
    }

    public Cursor getAllCheckInCheckOutX()
    {
        Cursor cursor = mDatabase.rawQuery("SELECT ProyectoID, EmpleadoID, strftime('%Y-%m-%dT%H:%M:%S', (datetime(ChechInCheckOut.CHECKS, 'unixepoch', 'localtime'))) AS CHECKS, " +
                "strftime('%Y-%m-%dT%H:%M:%S', (datetime(ChechInCheckOut.FECHA, 'unixepoch', 'localtime'))) AS FECHA, " +
                "CHECKINHECHO FROM ChechInCheckOut", null);

        return cursor;
    }

    private CheckInCheckOut cursorToCheckInCheckOut(Cursor cursor) {
        CheckInCheckOut checkincheckout = new CheckInCheckOut();
        checkincheckout.setId(cursor.getLong(0));
        checkincheckout.setProyectoID(cursor.getLong(1));
        checkincheckout.setEmpleadoID(cursor.getLong(2));
        checkincheckout.setChecks(cursor.getLong(3));
        checkincheckout.setFecha(cursor.getLong(4));
        checkincheckout.setCheckInHecho(cursor.getString(5));

        return checkincheckout;
    }
}
