package cultoftheunicorn.marvel.modelo;

public class Proyecto {
    public static final String TAG = "Proyecto";
    private static final long serialVersionUID = -7406082437623008161L;

    private long mId;
    private String mDescripcion;

    public Proyecto() {

    }

    public Proyecto(String mDescripcion) {
        this.mDescripcion = mDescripcion;
    }

    public long getId() {
        return mId;
    }

    public void setId(long mId) {
        this.mId = mId;
    }

    public String getDescripcion() {
        return mDescripcion;
    }

    public void setDescripcion(String mDescripcion) {
        this.mDescripcion= mDescripcion;
    }
}
