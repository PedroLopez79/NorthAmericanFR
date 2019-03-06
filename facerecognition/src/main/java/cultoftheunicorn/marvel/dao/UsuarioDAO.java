package cultoftheunicorn.marvel.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import cultoftheunicorn.marvel.modelo.Usuario;

public class UsuarioDAO {

    public static final String TAG = "UsuarioDAO";

    private SQLiteDatabase mDatabase;
    private AdminSqLiteOpenHelper mDbHelper;
    private Context mContext;
    private String[] mAllColumns = {
            AdminSqLiteOpenHelper.COLUMN_IDEMPLEADO,
            AdminSqLiteOpenHelper.COLUMN_CODIGO,
            AdminSqLiteOpenHelper.COLUMN_NOMBRE,
            AdminSqLiteOpenHelper.COLUMN_USERNAME,
            AdminSqLiteOpenHelper.COLUMN_USERPASSWORD,
            AdminSqLiteOpenHelper.COLUMN_FIRMADEFUALT,
            AdminSqLiteOpenHelper.COLUMN_AUDITOR
    };

    public UsuarioDAO(Context context){
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

    public Usuario createUsuario(String Codigo, String Nombre, String UserName, String UserPassword, byte[] FirmaDefault, String Auditor) {
        ContentValues values = new ContentValues();
        values.put(AdminSqLiteOpenHelper.COLUMN_CODIGO, Codigo);
        values.put(AdminSqLiteOpenHelper.COLUMN_NOMBRE, Nombre);
        values.put(AdminSqLiteOpenHelper.COLUMN_USERNAME, UserName);
        values.put(AdminSqLiteOpenHelper.COLUMN_USERPASSWORD, UserPassword);
        values.put(AdminSqLiteOpenHelper.COLUMN_FIRMADEFUALT, FirmaDefault);
        values.put(AdminSqLiteOpenHelper.COLUMN_AUDITOR, Auditor);
        long insertId = mDatabase
                .insert(AdminSqLiteOpenHelper.TABLE_USUARIO, null, values);
        Cursor cursor = mDatabase.query(AdminSqLiteOpenHelper.TABLE_USUARIO, mAllColumns,
                AdminSqLiteOpenHelper.COLUMN_IDEMPLEADO + " = " + insertId, null, null,
                null, null);
        cursor.moveToFirst();
        Usuario newEmployee = cursorToUsuario(cursor);
        cursor.close();
        return newEmployee;
    }

    public void deleteUsuario(Usuario usuario) {
        long id = usuario.getId();
        System.out.println("the deleted employee has the id: " + id);
        mDatabase.delete(AdminSqLiteOpenHelper.TABLE_USUARIO, AdminSqLiteOpenHelper.COLUMN_IDEMPLEADO
                + " = " + id, null);
    }

    public List<Usuario> getAllUsuario() {
        List<Usuario> listUsuario = new ArrayList<Usuario>();

        Cursor cursor = mDatabase.query(AdminSqLiteOpenHelper.TABLE_USUARIO, mAllColumns,
                null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Usuario usuario = cursorToUsuario(cursor);
            listUsuario.add(usuario);
            cursor.moveToNext();
        }
        // make sure to close the cursor
        cursor.close();
        return listUsuario;
    }

    private Usuario cursorToUsuario(Cursor cursor) {
        Usuario usuario = new Usuario();
        usuario.setId(cursor.getLong(0));
        usuario.setCodigo(cursor.getString(1));
        usuario.setNombre(cursor.getString(2));
        usuario.setUserName(cursor.getString(3));
        usuario.setUserPassword(cursor.getString(4));
        usuario.setFirmaDefault(cursor.getBlob(5));
        usuario.setAuditor(cursor.getString(6));

        return usuario;
    }
}
