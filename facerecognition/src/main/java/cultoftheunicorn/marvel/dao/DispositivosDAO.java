package cultoftheunicorn.marvel.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import cultoftheunicorn.marvel.modelo.Dispositivos;

public class DispositivosDAO {
    public static final String TAG = "DispositivosDAO";

    private SQLiteDatabase mDatabase;
    private AdminSqLiteOpenHelper mDbHelper;
    private Context mContext;
    private String[] mAllColumns = {
            AdminSqLiteOpenHelper.COLUMN_DDispositivoID,
            AdminSqLiteOpenHelper.COLUMN_DREMOTO,
            AdminSqLiteOpenHelper.COLUMN_DSYNCRONIZADO,
            AdminSqLiteOpenHelper.COLUMN_DMACADDRESS,
            AdminSqLiteOpenHelper.COLUMN_DDESCRIPCION,
            AdminSqLiteOpenHelper.COLUMN_DDEPARTAMENTO,
            AdminSqLiteOpenHelper.COLUMN_DAREA,
            AdminSqLiteOpenHelper.COLUMN_DSUPERVISORENCARGADO
    };

    public DispositivosDAO(Context context){
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

    public Dispositivos createDispositivos(String REMOTOS, String SYNCRONIZADO, String MACADDRESS, String DESCRIPCION, String DEPARTAMENTO, String AREA, String SUPERVISORENCARGADO, long ID) {
        ContentValues values = new ContentValues();
        values.put(AdminSqLiteOpenHelper.COLUMN_DDispositivoID, ID);
        values.put(AdminSqLiteOpenHelper.COLUMN_DREMOTO, REMOTOS);
        values.put(AdminSqLiteOpenHelper.COLUMN_DSYNCRONIZADO, SYNCRONIZADO);
        values.put(AdminSqLiteOpenHelper.COLUMN_DMACADDRESS, MACADDRESS);
        values.put(AdminSqLiteOpenHelper.COLUMN_DDESCRIPCION, DESCRIPCION);
        values.put(AdminSqLiteOpenHelper.COLUMN_DAREA, AREA);
        values.put(AdminSqLiteOpenHelper.COLUMN_DSUPERVISORENCARGADO, SUPERVISORENCARGADO);
        long insertId = mDatabase
                .insert(AdminSqLiteOpenHelper.TABLE_DISPOSITIVOS, null, values);
        Cursor cursor = mDatabase.query(AdminSqLiteOpenHelper.TABLE_DISPOSITIVOS, mAllColumns,
                AdminSqLiteOpenHelper.COLUMN_DDispositivoID + " = " + insertId, null, null,
                null, null);
        cursor.moveToFirst();
        Dispositivos newDispositivo = cursorToDispositivo(cursor);
        cursor.close();
        return newDispositivo;
    }

    public void deleteDispositivos(Dispositivos dispositivo) {
        long id = dispositivo.getId();
        System.out.println("the deleted employee has the id: " + id);
        //mDatabase.delete(AdminSqLiteOpenHelper.TABLE_DISPOSITIVOS, AdminSqLiteOpenHelper.COLUMN_DDispositivoID
        //        + " = " + id, null);

        mDatabase.delete(AdminSqLiteOpenHelper.TABLE_DISPOSITIVOS, null, null);
    }

    public List<Dispositivos> getAllDispostivo() {
        List<Dispositivos> listDispositivo= new ArrayList<Dispositivos>();

        Cursor cursor = mDatabase.query(AdminSqLiteOpenHelper.TABLE_DISPOSITIVOS, mAllColumns,
                null, null, null, null, null);

        //String POSTS_SELECT_QUERY =
        //        String.format("SELECT * FROM %s",
        //                AdminSqLiteOpenHelper.TABLE_EMPLEADOS);
        //Cursor cursor = mDatabase.rawQuery(POSTS_SELECT_QUERY, null);

        try {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                Dispositivos dispositivo = cursorToDispositivo(cursor);
                listDispositivo.add(dispositivo);
                cursor.moveToNext();
            }
        }catch (Exception e) {
            Log.d(TAG, "Error while trying to get posts from database");
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }
        return listDispositivo;
    }

    /*public List<Dispositivos> getAllProyectoxID(long IDPROYECTO) {
        List<Proyecto> listEmpleadoProyecto = new ArrayList<>();

        Cursor cursor = mDatabase.query(AdminSqLiteOpenHelper.TABLE_PROYECTO, mAllColumns,
                AdminSqLiteOpenHelper.COLUMN_PROYECTOID + " = ?",
                new String[] { String.valueOf(IDPROYECTO) }, null, null, null);

        try {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                Proyecto empleado = cursorToProyecto(cursor);
                listEmpleadoProyecto.add(empleado);
                cursor.moveToNext();
            }
        }catch (Exception e) {
            Log.d(TAG, "Error while trying to get posts from database");
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }
        return listEmpleadoProyecto;
    }*/

    /*public List<Empleado> getEmployeesOfCompany(long companyId) {
        List<Empleado> listEmployees = new ArrayList<Empleado>();

        Cursor cursor = mDatabase.query(AdminSqLiteOpenHelper.TABLE_EMPLEADOS, mAllColumns,
                AdminSqLiteOpenHelper.COLUMN_IDEMPLEADOS + " = ?",
                new String[] { String.valueOf(companyId) }, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Empleado employee = cursorToEmploye(cursor);
            listEmployees.add(employee);
            cursor.moveToNext();
        }
        // make sure to close the cursor
        cursor.close();
        return listEmployees;
    }*/

    private Dispositivos cursorToDispositivo(Cursor cursor) {
        Dispositivos dispositivo = new Dispositivos();
        dispositivo.setId(cursor.getLong(0));
        dispositivo.setDescripcion(cursor.getString(1));

        return dispositivo;
    }
}
