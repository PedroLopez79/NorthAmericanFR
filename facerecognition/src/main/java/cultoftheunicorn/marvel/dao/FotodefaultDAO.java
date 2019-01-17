package cultoftheunicorn.marvel.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import cultoftheunicorn.marvel.modelo.FotoDefault;

public class FotodefaultDAO {

    public static final String TAG = "EmpleadoDAO";

    private SQLiteDatabase mDatabase;
    private AdminSqLiteOpenHelper mDbHelper;
    private Context mContext;
    private String[] mAllColumns = {
            AdminSqLiteOpenHelper.COLUMN_FOTODEFAULT1,
            AdminSqLiteOpenHelper.COLUMN_FOTODEFAULT2,
            AdminSqLiteOpenHelper.COLUMN_FOTODEFAULT3,
            AdminSqLiteOpenHelper.COLUMN_FOTODEFAULT4,
            AdminSqLiteOpenHelper.COLUMN_FOTODEFAULT5,
            AdminSqLiteOpenHelper.COLUMN_FOTODEFAULT6,
            AdminSqLiteOpenHelper.COLUMN_FOTODEFAULT7,
            AdminSqLiteOpenHelper.COLUMN_FOTODEFAULT8,
            AdminSqLiteOpenHelper.COLUMN_FOTODEFAULT9,
            AdminSqLiteOpenHelper.COLUMN_FOTODEFAULT10
    };

    public FotodefaultDAO(Context context){
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

    public FotoDefault createFotoEmpleado(byte[] FotoDefault1, byte[] FotoDefault2, byte[] FotoDefault3, byte[] FotoDefault4, byte[] FotoDefault5,
                                          byte[] FotoDefault6, byte[] FotoDefault7, byte[] FotoDefault8, byte[] FotoDefault9, byte[] FotoDefault10) {
        ContentValues values = new ContentValues();
        values.put(AdminSqLiteOpenHelper.COLUMN_FOTODEFAULT1, FotoDefault1);
        values.put(AdminSqLiteOpenHelper.COLUMN_FOTODEFAULT2, FotoDefault2);
        values.put(AdminSqLiteOpenHelper.COLUMN_FOTODEFAULT3, FotoDefault3);
        values.put(AdminSqLiteOpenHelper.COLUMN_FOTODEFAULT4, FotoDefault4);
        values.put(AdminSqLiteOpenHelper.COLUMN_FOTODEFAULT5, FotoDefault5);
        values.put(AdminSqLiteOpenHelper.COLUMN_FOTODEFAULT6, FotoDefault6);
        values.put(AdminSqLiteOpenHelper.COLUMN_FOTODEFAULT7, FotoDefault7);
        values.put(AdminSqLiteOpenHelper.COLUMN_FOTODEFAULT8, FotoDefault8);
        values.put(AdminSqLiteOpenHelper.COLUMN_FOTODEFAULT9, FotoDefault9);
        values.put(AdminSqLiteOpenHelper.COLUMN_FOTODEFAULT10, FotoDefault10);
        long insertId = mDatabase.insert(AdminSqLiteOpenHelper.TABLE_FOTODEFAULT, null, values);
        Cursor cursor = mDatabase.query(AdminSqLiteOpenHelper.TABLE_FOTODEFAULT, mAllColumns,
                null, null, null,
                null, null);
        cursor.moveToFirst();

        FotoDefault newFotoDefault = cursorToFotoDefault(cursor);
        //cursor.close();
        return newFotoDefault;
    }

    public void deleteFotoDefault(FotoDefault fotodefault) {
        //long id = fotodefault.getId();
        //System.out.println("the deleted employee has the id: " + id);
        mDatabase.delete(AdminSqLiteOpenHelper.TABLE_FOTODEFAULT, null, null);
    }

    public List<FotoDefault> getAllFotoEmpleado() {
        List<FotoDefault> listFotoDefault = new ArrayList<FotoDefault>();

        Cursor cursor = mDatabase.query(AdminSqLiteOpenHelper.TABLE_FOTODEFAULT, mAllColumns,
                null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            FotoDefault fotodefault = cursorToFotoDefault(cursor);
            listFotoDefault.add(fotodefault);
            cursor.moveToNext();
        }
        // make sure to close the cursor
        cursor.close();
        return listFotoDefault;
    }

    private FotoDefault cursorToFotoDefault(Cursor cursor) {
        FotoDefault fotodefault = new FotoDefault();
        fotodefault.setFotoDefault1(cursor.getBlob(1));
        fotodefault.setFotoDefault2(cursor.getBlob(2));
        fotodefault.setFotoDefault3(cursor.getBlob(3));
        fotodefault.setFotoDefault4(cursor.getBlob(4));
        fotodefault.setFotoDefault5(cursor.getBlob(5));
        fotodefault.setFotoDefault6(cursor.getBlob(6));
        fotodefault.setFotoDefault7(cursor.getBlob(7));
        fotodefault.setFotoDefault8(cursor.getBlob(8));
        fotodefault.setFotoDefault9(cursor.getBlob(9));
        fotodefault.setFotoDefault10(cursor.getBlob(10));

        return fotodefault;
    }

}
