package cultoftheunicorn.marvel.modelo;

public class Usuario {

    public static final String TAG = "Usuario";
    private static final long serialVersionUID = -7406082437623008161L;

    private long mId;
    private String mCodigo;
    private String mNombre;
    private String mUserName;
    private String mUserPassword;
    private byte[] mFirmaDefault;
    private String mAuditor;

    public Usuario() {

    }

    public Usuario(String mCodigo, String mNombre,String mUserName,String mUserPassword,byte[] mFirmaDefault,String mAuditor) {
        this.mCodigo = mCodigo;
        this.mNombre = mNombre;
        this.mUserName = mUserName;
        this.mUserPassword = mUserPassword;
        this.mFirmaDefault = mFirmaDefault;
        this.mAuditor = mAuditor;
    }

    public long getId() {
        return mId;
    }

    public void setId(long mId) {
        this.mId = mId;
    }

    public String getCodigo() {
        return mCodigo;
    }

    public void setCodigo(String mCodigo) {
        this.mCodigo = mCodigo;
    }

    public String getNombre() {
        return mNombre;
    }

    public void setNombre(String mNombre) {
        this.mNombre = mNombre;
    }

    public String getUserName() {
        return mUserName;
    }

    public void setUserName(String mUserName) {
        this.mUserName = mUserName;
    }

    public String getUserPassword() {
        return mUserPassword;
    }

    public void setUserPassword(String mUserPassword) {
        this.mUserPassword = mUserPassword;
    }

    public byte[] getFirmaDefault() {
        return mFirmaDefault;
    }

    public void setFirmaDefault(byte[] mFirmaDefault) {
        this.mFirmaDefault = mFirmaDefault;
    }

    public String getAuditor() { return mAuditor; }

    public void setAuditor(String mAuditor) {
        this.mAuditor = mAuditor;
    }
}
