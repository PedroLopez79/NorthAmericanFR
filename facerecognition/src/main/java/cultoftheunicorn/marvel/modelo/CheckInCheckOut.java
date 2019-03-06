package cultoftheunicorn.marvel.modelo;

public class CheckInCheckOut {
    public static final String TAG = "CheckInCheckOut";
    private static final long serialVersionUID = -7406082437623008161L;

    private long mId;
    private long mProyectoID;
    private long mEmpleadoID;
    private long mCheckIn;
    private long mCheckOut;
    private long mFecha;
    private String mCheckInHecho;

    public CheckInCheckOut() {

    }

    public CheckInCheckOut(long mProyectoID, long mEmpleadoID, long mCheckIn, long mCheckOut, long mFecha, String mCheckInHecho)
    {
        this.mProyectoID   = mProyectoID;
        this.mEmpleadoID   = mEmpleadoID;
        this.mCheckIn      = mCheckIn;
        this.mCheckOut     = mCheckOut;
        this.mFecha        = mFecha;
        this.mCheckInHecho = mCheckInHecho;
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

    public long getCheckIn() {
        return mCheckIn;
    }

    public void setCheckIn(long mCheckIn) {
        this.mCheckIn= mCheckIn;
    }

    public long getCheckOut() {
        return mCheckOut;
    }

    public void setCheckOut(long mCheckOut)
    {
        this.mCheckOut= mCheckOut;
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
}
