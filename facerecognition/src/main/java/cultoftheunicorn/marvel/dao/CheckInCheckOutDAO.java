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
            AdminSqLiteOpenHelper.COLUMN_FCHECKIN,
            AdminSqLiteOpenHelper.COLUMN_FCHECKOUT,
            AdminSqLiteOpenHelper.COLUMN_CFECHA,
            AdminSqLiteOpenHelper.COLUMN_CHECKINHECHO
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

    public CheckInCheckOut createCheckInCheckOut(long ProyectoID, long EmpleadoID, long CheckIn, long CheckOut, long Fecha, String CheckInHecho) {
        ContentValues values = new ContentValues();
        values.put(AdminSqLiteOpenHelper.COLUMN_CPROYECTOID, ProyectoID);
        values.put(AdminSqLiteOpenHelper.COLUMN_CEMPLEADOID, EmpleadoID);
        values.put(AdminSqLiteOpenHelper.COLUMN_FCHECKIN, CheckIn);
        values.put(AdminSqLiteOpenHelper.COLUMN_FCHECKOUT, CheckOut);
        values.put(AdminSqLiteOpenHelper.COLUMN_CFECHA, Fecha);
        values.put(AdminSqLiteOpenHelper.COLUMN_CHECKINHECHO, CheckInHecho);
        long insertId = mDatabase
                .insert(AdminSqLiteOpenHelper.TABLE_CHECKINCHECKOUT, null, values);
        Cursor cursor = mDatabase.query(AdminSqLiteOpenHelper.TABLE_CHECKINCHECKOUT, mAllColumns,
                AdminSqLiteOpenHelper.COLUMN_CPROYECTOID + " = " + insertId, null, null,
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
        List<CheckInCheckOut> listProyecto= new ArrayList<CheckInCheckOut>();

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
                listProyecto.add(proyect);
                cursor.moveToNext();
            }
        }catch (Exception e) {
            Log.d(TAG, "Error while trying to get posts from database");
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }
        return listProyecto;
    }

    private CheckInCheckOut cursorToCheckInCheckOut(Cursor cursor) {
        CheckInCheckOut checkincheckout = new CheckInCheckOut();
        checkincheckout.setId(cursor.getLong(0));
        checkincheckout.setProyectoID(cursor.getLong(1));
        checkincheckout.setEmpleadoID(cursor.getLong(2));
        checkincheckout.setCheckIn(cursor.getLong(3));
        checkincheckout.setCheckOut(cursor.getLong(4));
        checkincheckout.setFecha(cursor.getLong(5));
        checkincheckout.setCheckInHecho(cursor.getString(6));

        return checkincheckout;
    }
}
