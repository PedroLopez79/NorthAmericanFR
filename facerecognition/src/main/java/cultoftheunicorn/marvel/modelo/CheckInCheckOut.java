package cultoftheunicorn.marvel.modelo;

public class CheckInCheckOut {
    public static final String TAG = "CheckInCheckOut";
    private static final long serialVersionUID = -7406082437623008161L;

    private long mId;
    private long mProyectoID;
    private long mEmpleadoID;
    private long mChecks;
    private long mFecha;
    private String mCheckInHecho;
    private String mSyncronizado;
    private String mRegistro;

    public CheckInCheckOut() {

    }

    public CheckInCheckOut(long mProyectoID, long mEmpleadoID, long mChecks, long mFecha, String mCheckInHecho, String mSyncronizado, String mRegistro)
    {
        this.mProyectoID   = mProyectoID;
        this.mEmpleadoID   = mEmpleadoID;
        this.mChecks       = mChecks;
        this.mFecha        = mFecha;
        this.mCheckInHecho = mCheckInHecho;
        this.mSyncronizado = mSyncronizado;
        this.mRegistro     = mRegistro;
    }

    public long getId() {
        return mId;
    }

    public void setId(long mId) {
        this.mId = mId;
    }

    public long getProyectoID() {
        return mProyectoID;
    }

    public void setProyectoID(long mProyectoID) {
        this.mProyectoID= mProyectoID;
    }

    public long getEmpleadoID() {
        return mEmpleadoID;
    }

    public void setEmpleadoID(long mEmpleadoID) {
        this.mEmpleadoID= mEmpleadoID;
    }

    public long getChecks() {
        return mChecks;
    }

    public void setChecks(long mChecks) {
        this.mChecks= mChecks;
    }

    public long getFecha() {
        return mFecha;
    }

    public void setFecha(long mFecha) {
        this.mFecha= mFecha;
    }

    public String getCheckInHecho(){
        return mCheckInHecho;
    }

    public void setCheckInHecho(String mCheckInHecho)
    {
        this.mCheckInHecho = mCheckInHecho;
    }

    public String getSyncronizado() { return mSyncronizado; }

    public void setSyncronizado(String mSyncronizado)
    {
        this.mSyncronizado = mSyncronizado;
    }

    public String getRegistro() { return mRegistro; }

    public void setRegistro(String mRegistro)
    {
        this.mRegistro = mRegistro;
    }
}
