package cultoftheunicorn.marvel.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import cultoftheunicorn.marvel.modelo.Empleado;

public class EmpleadoDAO {

    public static final String TAG = "EmpleadoDAO";

    private SQLiteDatabase mDatabase;
    private AdminSqLiteOpenHelper mDbHelper;
    private Context mContext;
    private String[] mAllColumns = {
            AdminSqLiteOpenHelper.COLUMN_IDEMPLEADOS,
            AdminSqLiteOpenHelper.COLUMN_NOMBRE,
            AdminSqLiteOpenHelper.COLUMN_CODIGOEMPLEADO,
            AdminSqLiteOpenHelper.COLUMN_DOMICILIO,
            AdminSqLiteOpenHelper.COLUMN_CIUDAD,
            AdminSqLiteOpenHelper.COLUMN_TELEFONO,
            AdminSqLiteOpenHelper.COLUMN_CUENTACONTABLE,
            AdminSqLiteOpenHelper.COLUMN_FECHAALTA,
            AdminSqLiteOpenHelper.COLUMN_FECHABAJA,
            AdminSqLiteOpenHelper.COLUMN_IMSS,
            AdminSqLiteOpenHelper.COLUMN_STATUS,
            AdminSqLiteOpenHelper.COLUMN_FOTOEMPLEADO,
            AdminSqLiteOpenHelper.COLUMN_IDESTACION,
            AdminSqLiteOpenHelper.COLUMN_TURNO,
            AdminSqLiteOpenHelper.COLUMN_FOTOEMPLEADO1,
            AdminSqLiteOpenHelper.COLUMN_FOTOEMPLEADO2,
            AdminSqLiteOpenHelper.COLUMN_FOTOEMPLEADO3,
            AdminSqLiteOpenHelper.COLUMN_FOTOEMPLEADO4,
            AdminSqLiteOpenHelper.COLUMN_FOTOEMPLEADO5,
            AdminSqLiteOpenHelper.COLUMN_FOTOEMPLEADO6,
            AdminSqLiteOpenHelper.COLUMN_FOTOEMPLEADO7,
            AdminSqLiteOpenHelper.COLUMN_FOTOEMPLEADO8,
            AdminSqLiteOpenHelper.COLUMN_FOTOEMPLEADO9,
            AdminSqLiteOpenHelper.COLUMN_FOTOEMPLEADO10,
            AdminSqLiteOpenHelper.COLUMN_IDDISPOSITIVO,
            AdminSqLiteOpenHelper.COLUMN_REGISTRO,
            AdminSqLiteOpenHelper.COLUMN_SYNCRONIZADO,
            AdminSqLiteOpenHelper.COLUMN_IDPROYECTO
    };

    public EmpleadoDAO(Context context){
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

    public Empleado createEmpleado(String Nombre,String CodigoEmpleado,String Domicilio,String Ciudad,String Telefono,String CuentaContable,String FechaAlta,
                                  String FechaBaja,String Imss,String Status,byte[] FotoEmpleado,long IdEstacion,long Turno,byte[] FotoEmpleado1,byte[] FotoEmpleado2,
                                  byte[] FotoEmpleado3,byte[] FotoEmpleado4,byte[] FotoEmpleado5,byte[] FotoEmpleado6,byte[] FotoEmpleado7,byte[] FotoEmpleado8,
                                  byte[] FotoEmpleado9,byte[] FotoEmpleado10,long IdDispositivo,String Registro,String Syncronizado, long IdProyecto) {
        ContentValues values = new ContentValues();
        values.put(AdminSqLiteOpenHelper.COLUMN_NOMBRE, Nombre);
        values.put(AdminSqLiteOpenHelper.COLUMN_CODIGOEMPLEADO, CodigoEmpleado);
        values.put(AdminSqLiteOpenHelper.COLUMN_DOMICILIO, Domicilio);
        values.put(AdminSqLiteOpenHelper.COLUMN_CIUDAD, Ciudad);
        values.put(AdminSqLiteOpenHelper.COLUMN_TELEFONO, Telefono);
        values.put(AdminSqLiteOpenHelper.COLUMN_CUENTACONTABLE, CuentaContable);
        values.put(AdminSqLiteOpenHelper.COLUMN_FECHAALTA, FechaAlta);
        values.put(AdminSqLiteOpenHelper.COLUMN_FECHABAJA, FechaBaja);
        values.put(AdminSqLiteOpenHelper.COLUMN_IMSS, Imss);
        values.put(AdminSqLiteOpenHelper.COLUMN_STATUS, Status);
        values.put(AdminSqLiteOpenHelper.COLUMN_FOTOEMPLEADO, FotoEmpleado);
        values.put(AdminSqLiteOpenHelper.COLUMN_IDESTACION, IdEstacion);
        values.put(AdminSqLiteOpenHelper.COLUMN_TURNO, Turno);
        values.put(AdminSqLiteOpenHelper.COLUMN_FOTOEMPLEADO1, FotoEmpleado1);
        values.put(AdminSqLiteOpenHelper.COLUMN_FOTOEMPLEADO2, FotoEmpleado2);
        values.put(AdminSqLiteOpenHelper.COLUMN_FOTOEMPLEADO3, FotoEmpleado3);
        values.put(AdminSqLiteOpenHelper.COLUMN_FOTOEMPLEADO4, FotoEmpleado4);
        values.put(AdminSqLiteOpenHelper.COLUMN_FOTOEMPLEADO5, FotoEmpleado5);
        values.put(AdminSqLiteOpenHelper.COLUMN_FOTOEMPLEADO6, FotoEmpleado6);
        values.put(AdminSqLiteOpenHelper.COLUMN_FOTOEMPLEADO7, FotoEmpleado7);
        values.put(AdminSqLiteOpenHelper.COLUMN_FOTOEMPLEADO8, FotoEmpleado8);
        values.put(AdminSqLiteOpenHelper.COLUMN_FOTOEMPLEADO9, FotoEmpleado9);
        values.put(AdminSqLiteOpenHelper.COLUMN_FOTOEMPLEADO10, FotoEmpleado10);
        values.put(AdminSqLiteOpenHelper.COLUMN_IDDISPOSITIVO, IdDispositivo);
        values.put(AdminSqLiteOpenHelper.COLUMN_REGISTRO, Registro);
        values.put(AdminSqLiteOpenHelper.COLUMN_SYNCRONIZADO, Syncronizado);
        values.put(AdminSqLiteOpenHelper.COLUMN_IDPROYECTO, IdProyecto);
        long insertId = mDatabase
                .insert(AdminSqLiteOpenHelper.TABLE_EMPLEADOS, null, values);
        Cursor cursor = mDatabase.query(AdminSqLiteOpenHelper.TABLE_EMPLEADOS, mAllColumns,
                AdminSqLiteOpenHelper.COLUMN_IDEMPLEADOS + " = " + insertId, null, null,
                null, null);
        cursor.moveToFirst();
        Empleado newEmpleado = cursorToEmpleado(cursor);
        cursor.close();
        return newEmpleado;
    }

    public void updateEmployee(EmpleadoDAO employee, long ids) {
        System.out.println("the updated employee has te id: " + ids);
        ContentValues args = new ContentValues();
        args.put("SYNCRONIZADO", "SI");
        mDatabase.update(AdminSqLiteOpenHelper.TABLE_EMPLEADOS, args,AdminSqLiteOpenHelper.COLUMN_IDEMPLEADO
                + " = " + ids, null);
    }

    public void deleteEmployee(Empleado employee) {
        long id = employee.getId();
        System.out.println("the deleted employee has the id: " + id);
        mDatabase.delete(AdminSqLiteOpenHelper.TABLE_EMPLEADOS, AdminSqLiteOpenHelper.COLUMN_IDEMPLEADOS
                + " = " + id, null);
    }

    public List<Empleado> getAllEmpleadoProyecto(long PROYECTOID) {
        List<Empleado> listEmpleadoProyecto = new ArrayList<>();

        Cursor cursor = mDatabase.query(AdminSqLiteOpenHelper.TABLE_EMPLEADOS, mAllColumns,
                AdminSqLiteOpenHelper.COLUMN_IDPROYECTO + " = ?",
                new String[] { String.valueOf(PROYECTOID) }, null, null, null);

        try {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                Empleado empleado = cursorToEmpleado(cursor);
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

    public List<Empleado> getAllEmpleadoxID(long IDEMPLEADO) {
        List<Empleado> listEmpleadoProyecto = new ArrayList<>();

        Cursor cursor = mDatabase.query(AdminSqLiteOpenHelper.TABLE_EMPLEADOS, mAllColumns,
                AdminSqLiteOpenHelper.COLUMN_IDEMPLEADOS + " = ?",
                new String[] { String.valueOf(IDEMPLEADO) }, null, null, null);

        try {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                Empleado empleado = cursorToEmpleado(cursor);
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

    public List<Empleado> getAllEmpleado() {
        List<Empleado> listEmpleado= new ArrayList<Empleado>();

        Cursor cursor = mDatabase.query(AdminSqLiteOpenHelper.TABLE_EMPLEADOS, mAllColumns,
                null, null, null, null, null);

        //String POSTS_SELECT_QUERY =
        //        String.format("SELECT * FROM %s",
        //                AdminSqLiteOpenHelper.TABLE_EMPLEADOS);
        //Cursor cursor = mDatabase.rawQuery(POSTS_SELECT_QUERY, null);

        try {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                Empleado empleado = cursorToEmpleado(cursor);
                listEmpleado.add(empleado);
                cursor.moveToNext();
            }
        }catch (Exception e) {
                Log.d(TAG, "Error while trying to get posts from database");
            } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }
        return listEmpleado;
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

    private Empleado cursorToEmpleado(Cursor cursor) {
        Empleado empleado = new Empleado();
        empleado.setId(cursor.getLong(0));
        empleado.setNombre(cursor.getString(1));
        empleado.setCodigoEmpleado(cursor.getString(2));
        empleado.setDomicilio(cursor.getString(3));
        empleado.setCiudad(cursor.getString(4));
        empleado.setTelefono(cursor.getString(5));
        empleado.setCuentaContable(cursor.getDouble(6));
        empleado.setFechaAlta(cursor.getString(7));
        empleado.setFechaBaja(cursor.getString(8));
        empleado.setImss(cursor.getString(9));
        empleado.setStatus(cursor.getString(10));
        empleado.setFotoEmpleado(cursor.getBlob(11));
        empleado.setIdEstacion(cursor.getLong(12));
        empleado.setTurno(cursor.getLong(13));
        empleado.setFotoEmpleado1(cursor.getBlob(14));
        empleado.setFotoEmpleado2(cursor.getBlob(15));
        empleado.setFotoEmpleado3(cursor.getBlob(16));
        empleado.setFotoEmpleado4(cursor.getBlob(17));
        empleado.setFotoEmpleado5(cursor.getBlob(18));
        empleado.setFotoEmpleado6(cursor.getBlob(19));
        empleado.setFotoEmpleado7(cursor.getBlob(20));
        empleado.setFotoEmpleado8(cursor.getBlob(21));
        empleado.setFotoEmpleado9(cursor.getBlob(22));
        empleado.setFotoEmpleado10(cursor.getBlob(23));
        empleado.setIdDispositivo(cursor.getLong(24));
        empleado.setRegistro(cursor.getString(25));
        empleado.setSyncronizado(cursor.getString(26));
        empleado.setIdproyecto(cursor.getLong(27));

        return empleado;
    }
}
