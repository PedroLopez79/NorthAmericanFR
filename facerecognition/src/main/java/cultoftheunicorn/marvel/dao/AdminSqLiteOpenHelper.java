package cultoftheunicorn.marvel.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.Nullable;
import android.util.Log;

public class AdminSqLiteOpenHelper extends SQLiteOpenHelper {

    private static final String TAG = "AdminSqLiteOpenHelper";

    //columnas tabla proyectos
    public static final String TABLE_CHECKINCHECKOUT     =  "ChechInCheckOut";
    public static final String COLUMN_CHECKINCHECKOUTID  =  "CHECKINCHECKOUTID";
    public static final String COLUMN_CPROYECTOID        =  "PROYECTOID";
    public static final String COLUMN_CEMPLEADOID        =  "EMPLEADOID";
    public static final String COLUMN_FCHECKS            =  "CHECKS";
    public static final String COLUMN_CFECHA             =  "FECHA";
    public static final String COLUMN_CHECKINHECHO       =  "CHECKINHECHO";
    public static final String COLUMN_CSYNCRONIZADO       =  "SYNCRONIZADO";
    public static final String COLUMN_CREGISTRO           =  "REGISTRO";

    //columnas tabla proyectos
    public static final String TABLE_PROYECTO       =  "Proyecto";
    public static final String COLUMN_PROYECTOID    =  "IDPROYECTO";
    public static final String COLUMN_PDESCRIPCION  =  "DESCRIPCION";

    //columnas tabla empleados
    public static final String TABLE_EMPLEADOS       = "Empleados";
    public static final String COLUMN_IDEMPLEADOS    = "IDEMPLEADO";
    public static final String COLUMN_NOMBRE         = "NOMBRE";
    public static final String COLUMN_CODIGOEMPLEADO = "CODIGOEMPLEADO";
    public static final String COLUMN_DOMICILIO      = "DOMICILIO";
    public static final String COLUMN_CIUDAD         = "CIUDAD";
    public static final String COLUMN_TELEFONO       = "TELEFONO";
    public static final String COLUMN_CUENTACONTABLE = "CUENTACONTABLE";
    public static final String COLUMN_FECHAALTA      = "FECHAALTA";
    public static final String COLUMN_FECHABAJA      = "FECHABAJA";
    public static final String COLUMN_IMSS           = "IMSS";
    public static final String COLUMN_STATUS         = "STATUS";
    public static final String COLUMN_FOTOEMPLEADO   = "FOTOEMPLEADO";
    public static final String COLUMN_IDESTACION     = "IDESTACION";
    public static final String COLUMN_TURNO          = "TURNO";
    public static final String COLUMN_FOTOEMPLEADO1  = "FOTOEMPLEADO1";
    public static final String COLUMN_FOTOEMPLEADO2  = "FOTOEMPLEADO2";
    public static final String COLUMN_FOTOEMPLEADO3  = "FOTOEMPLEADO3";
    public static final String COLUMN_FOTOEMPLEADO4  = "FOTOEMPLEADO4";
    public static final String COLUMN_FOTOEMPLEADO5  = "FOTOEMPLEADO5";
    public static final String COLUMN_FOTOEMPLEADO6  = "FOTOEMPLEADO6";
    public static final String COLUMN_FOTOEMPLEADO7  = "FOTOEMPLEADO7";
    public static final String COLUMN_FOTOEMPLEADO8  = "FOTOEMPLEADO8";
    public static final String COLUMN_FOTOEMPLEADO9  = "FOTOEMPLEADO9";
    public static final String COLUMN_FOTOEMPLEADO10 = "FOTOEMPLEADO10";
    public static final String COLUMN_IDDISPOSITIVO  = "IDDISPOSITIVO";
    public static final String COLUMN_REGISTRO       = "REGISTRO";
    public static final String COLUMN_SYNCRONIZADO   = "SYNCRONIZADO";
    public static final String COLUMN_IDPROYECTO     = "IDPROYECTO";

    //columnas tabla FOTODEFAULT
    public static final String TABLE_FOTODEFAULT     = "FOTODEFAULT";
    public static final String COLUMN_FOTODEFAULT1   = "FOTODEFAULT1";
    public static final String COLUMN_FOTODEFAULT2   = "FOTODEFAULT2";
    public static final String COLUMN_FOTODEFAULT3   = "FOTODEFAULT3";
    public static final String COLUMN_FOTODEFAULT4   = "FOTODEFAULT4";
    public static final String COLUMN_FOTODEFAULT5   = "FOTODEFAULT5";
    public static final String COLUMN_FOTODEFAULT6   = "FOTODEFAULT6";
    public static final String COLUMN_FOTODEFAULT7   = "FOTODEFAULT7";
    public static final String COLUMN_FOTODEFAULT8   = "FOTODEFAULT8";
    public static final String COLUMN_FOTODEFAULT9   = "FOTODEFAULT9";
    public static final String COLUMN_FOTODEFAULT10  = "FOTODEFAULT10";

    //columnas tabla USUARIO
    public static final String TABLE_USUARIO         = "USUARIO";
    public static final String COLUMN_IDEMPLEADO     = "IDEMPLEADO";
    public static final String COLUMN_CODIGO         = "CODIGO";
    public static final String COLUMN_NOMBREUSUARIO  = "NOMBRE";
    public static final String COLUMN_USERNAME       = "USERNAME";
    public static final String COLUMN_USERPASSWORD   = "USERPASSWORD";
    public static final String COLUMN_FIRMADEFUALT   = "FIRMADEFAULT";
    public static final String COLUMN_AUDITOR        = "AUDITOR";

    private static final String DATABASENAME = "NorthAmericanGreenHousesCheck.db";
    private static final int DATABASE_VERSION= 1;

    //sql statement (crear tabla empleados)
    private static final String SQL_CREATE_TABLE_EMPLEADOS = "CREATE TABLE "+ TABLE_EMPLEADOS + " ("
            + COLUMN_IDEMPLEADOS    + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COLUMN_NOMBRE         + " TEXT, "
            + COLUMN_CODIGOEMPLEADO + " TEXT, "
            + COLUMN_DOMICILIO      + " TEXT, "
            + COLUMN_CIUDAD         + " TEXT, "
            + COLUMN_TELEFONO       + " TEXT, "
            + COLUMN_CUENTACONTABLE + " TEXT, "
            + COLUMN_FECHAALTA      + " TEXT, "
            + COLUMN_FECHABAJA      + " TEXT, "
            + COLUMN_IMSS           + " TEXT, "
            + COLUMN_STATUS         + " STATUS, "
            + COLUMN_FOTOEMPLEADO   + " BLOB, "
            + COLUMN_IDESTACION     + " INTEGER, "
            + COLUMN_TURNO          + " INTEGER, "
            + COLUMN_FOTOEMPLEADO1   + " BLOB, "
            + COLUMN_FOTOEMPLEADO2   + " BLOB, "
            + COLUMN_FOTOEMPLEADO3   + " BLOB, "
            + COLUMN_FOTOEMPLEADO4   + " BLOB, "
            + COLUMN_FOTOEMPLEADO5   + " BLOB, "
            + COLUMN_FOTOEMPLEADO6   + " BLOB, "
            + COLUMN_FOTOEMPLEADO7   + " BLOB, "
            + COLUMN_FOTOEMPLEADO8   + " BLOB, "
            + COLUMN_FOTOEMPLEADO9   + " BLOB, "
            + COLUMN_FOTOEMPLEADO10  + " BLOB, "
            + COLUMN_IDDISPOSITIVO   + " INTEGER, "
            + COLUMN_REGISTRO        + " TEXT, "
            + COLUMN_SYNCRONIZADO    + " TEXT, "
            + COLUMN_IDPROYECTO      + " TEXT"
            + ");";

    //sql statement (crear tabla FOTODEFAULT)
    private static final String SQL_CREATE_TABLE_FOTODEFAULT = "CREATE TABLE "+ TABLE_FOTODEFAULT + " ("
            + COLUMN_FOTODEFAULT1   + " BLOB, "
            + COLUMN_FOTODEFAULT2   + " BLOB, "
            + COLUMN_FOTODEFAULT3   + " BLOB, "
            + COLUMN_FOTODEFAULT4   + " BLOB, "
            + COLUMN_FOTODEFAULT5   + " BLOB, "
            + COLUMN_FOTODEFAULT6   + " BLOB, "
            + COLUMN_FOTODEFAULT7   + " BLOB, "
            + COLUMN_FOTODEFAULT8   + " BLOB, "
            + COLUMN_FOTODEFAULT9   + " BLOB, "
            + COLUMN_FOTODEFAULT10  + " BLOB "
            + ");";

    //sql statement (crear tabla USUARIO)
    private static final String SQL_CREATE_TABLE_USUARIO = "CREATE TABLE "+ TABLE_USUARIO + " ("
            + COLUMN_IDEMPLEADO     + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COLUMN_CODIGO         + " TEXT, "
            + COLUMN_NOMBRE         + " TEXT, "
            + COLUMN_USERNAME       + " TEXT, "
            + COLUMN_USERPASSWORD   + " TEXT, "
            + COLUMN_FIRMADEFUALT   + " BLOB, "
            + COLUMN_AUDITOR        + " TEXT "
            + ");";

    private static final String SQL_CREATE_TABLE_PROYECTO = "CREATE TABLE " + TABLE_PROYECTO + " ("
            + COLUMN_PROYECTOID     + " INTEGER PRIMARY KEY, "
            + COLUMN_PDESCRIPCION   + " TEXT "
            + ");";

    private static final String SQL_CREATE_TABLE_CHECKINCHECKOUT = "CREATE TABLE " + TABLE_CHECKINCHECKOUT + " ("
            + COLUMN_CHECKINCHECKOUTID     + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COLUMN_CPROYECTOID   + " INT, "
            + COLUMN_CEMPLEADOID   + " INT, "
            + COLUMN_FCHECKS       + " NUMERIC, "
            + COLUMN_CFECHA        + " NUMERIC, "
            + COLUMN_CHECKINHECHO  + " TEXT, "
            + COLUMN_CSYNCRONIZADO + " TEXT, "
            + COLUMN_CREGISTRO     + " TEXT "
            + ");";

    public AdminSqLiteOpenHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, DATABASENAME, factory, DATABASE_VERSION);
    }

    public AdminSqLiteOpenHelper(Context context)
    {
        super(context, DATABASENAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(SQL_CREATE_TABLE_EMPLEADOS);
        sqLiteDatabase.execSQL(SQL_CREATE_TABLE_FOTODEFAULT);
        sqLiteDatabase.execSQL(SQL_CREATE_TABLE_USUARIO);
        sqLiteDatabase.execSQL(SQL_CREATE_TABLE_PROYECTO);
        sqLiteDatabase.execSQL(SQL_CREATE_TABLE_CHECKINCHECKOUT);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(TAG,
                "Upgrading the database from version " + oldVersion + " to "+ newVersion);
        // clear all data
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_EMPLEADOS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FOTODEFAULT);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USUARIO);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PROYECTO);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CHECKINCHECKOUT);

        // recreate the tables
        onCreate(db);
    }
}
