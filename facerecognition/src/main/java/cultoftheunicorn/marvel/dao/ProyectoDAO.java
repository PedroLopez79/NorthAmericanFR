package cultoftheunicorn.marvel.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import cultoftheunicorn.marvel.modelo.Proyecto;

public class ProyectoDAO {
    public static final String TAG = "ProyectoDAO";

    private SQLiteDatabase mDatabase;
    private AdminSqLiteOpenHelper mDbHelper;
    private Context mContext;
    private String[] mAllColumns = {
            AdminSqLiteOpenHelper.COLUMN_PROYECTOID,
            AdminSqLiteOpenHelper.COLUMN_PDESCRIPCION
    };

    public ProyectoDAO(Context context){
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

    public Proyecto createProyecto(String Descripcion, long ID) {
        ContentValues values = new ContentValues();
        values.put(AdminSqLiteOpenHelper.COLUMN_PDESCRIPCION, Descripcion);
        values.put(AdminSqLiteOpenHelper.COLUMN_PROYECTOID, ID);
        long insertId = mDatabase
                .insert(AdminSqLiteOpenHelper.TABLE_PROYECTO, null, values);
        Cursor cursor = mDatabase.query(AdminSqLiteOpenHelper.TABLE_PROYECTO, mAllColumns,
                AdminSqLiteOpenHelper.COLUMN_PROYECTOID + " = " + insertId, null, null,
                null, null);
        cursor.moveToFirst();
        Proyecto newProyecto = cursorToProyecto(cursor);
        cursor.close();
        return newProyecto;
    }

    public void deleteProyecto(Proyecto proyect) {
        long id = proyect.getId();
        System.out.println("the deleted employee has the id: " + id);
        //mDatabase.delete(AdminSqLiteOpenHelper.TABLE_PROYECTO, AdminSqLiteOpenHelper.COLUMN_PROYECTOID
        //        + " = " + id, null);

        mDatabase.delete(AdminSqLiteOpenHelper.TABLE_PROYECTO, null, null);
    }

    public List<Proyecto> getAllProyecto() {
        List<Proyecto> listProyecto= new ArrayList<Proyecto>();

        Cursor cursor = mDatabase.query(AdminSqLiteOpenHelper.TABLE_PROYECTO, mAllColumns,
                null, null, null, null, null);

        //String POSTS_SELECT_QUERY =
        //        String.format("SELECT * FROM %s",
        //                AdminSqLiteOpenHelper.TABLE_EMPLEADOS);
        //Cursor cursor = mDatabase.rawQuery(POSTS_SELECT_QUERY, null);

        try {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                Proyecto proyect = cursorToProyecto(cursor);
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

    public List<Proyecto> getAllProyectoxID(long IDPROYECTO) {
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
    }

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

    private Proyecto cursorToProyecto(Cursor cursor) {
        Proyecto proyect = new Proyecto();
        proyect.setId(cursor.getLong(0));
        proyect.setDescripcion(cursor.getString(1));

        return proyect;
    }
}
