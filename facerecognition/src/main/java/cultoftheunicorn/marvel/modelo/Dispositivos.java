package cultoftheunicorn.marvel.modelo;

public class Dispositivos {
    public static final String TAG = "Dispositivos";
    private static final long serialVersionUID = -7406082437623008161L;

    private long mId;
    private String mRemoto;
    private String mSyncronizado;
    private String mMacAddress;
    private String mDescripcion;
    private String mDepartamento;
    private String mArea;
    private String mSupervisorencargado;

    public Dispositivos() {
    }

    public Dispositivos(String mDescripcion) {
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

    public String getSyncronizado(){return mSyncronizado;}

    public void setSyncronizado(String mSyncronizado) {this.mSyncronizado = mSyncronizado;}

    public String getMacAddress() {return mMacAddress;}

    public void setMacAddress(String mMacAddress) {this.mMacAddress = mMacAddress;}

    public String getDepartamento() {return mDepartamento;}

    public void setDepartamento(String mDepartamento) {this.mDepartamento = mDepartamento;}

    public String getArea() {return mArea;}

    public void setArea(String mArea) {this.mArea = mArea;}

    public String getSupervisorencargado() {return mSupervisorencargado;}

    public void setSupervisorencargado(String mSupervisorencargado) {this.mSupervisorencargado = mSupervisorencargado;}

    public String getRemoto() {return mRemoto;}

    public void setRemoto(String mRemoto) {this.mRemoto = mRemoto;}
}
