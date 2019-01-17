package cultoftheunicorn.marvel.modelo;

public class Empleado {

    public static final String TAG = "Empleado";
    private static final long serialVersionUID = -7406082437623008161L;

    private long mId;
    private String mNombre;
    private String mCodigoEmpleado;
    private String mDomicilio;
    private String mCiudad;
    private String mTelefono;
    private String mCuentaContable;
    private String mFechaAlta;
    private String mFechaBaja;
    private String mImss;
    private String mStatus;
    private byte[] mFotoEmpleado;
    private long mIdEstacion;
    private long mTurno;
    private byte[] mFotoEmpleado1;
    private byte[] mFotoEmpleado2;
    private byte[] mFotoEmpleado3;
    private byte[] mFotoEmpleado4;
    private byte[] mFotoEmpleado5;
    private byte[] mFotoEmpleado6;
    private byte[] mFotoEmpleado7;
    private byte[] mFotoEmpleado8;
    private byte[] mFotoEmpleado9;
    private byte[] mFotoEmpleado10;
    private long mIdDispositivo;
    private String mRegistro;
    private String mSyncronizado;

    public Empleado() {

    }

    public Empleado(String mNombre,String mCodigoEmpleado,String mDomicilio,String mCiudad,String mTelefono,String mCuentaContable,String mFechaAlta,
                    String mFechaBaja,String mImss,String mStatus,byte[] mFotoEmpleado,long mIdEstacion,long mTurno,byte[] mFotoEmpleado1,byte[] mFotoEmpleado2,
                    byte[] mFotoEmpleado3,byte[] mFotoEmpleado4,byte[] mFotoEmpleado5,byte[] mFotoEmpleado6,byte[] mFotoEmpleado7,byte[] mFotoEmpleado8,
                    byte[] mFotoEmpleado9,byte[] mFotoEmpleado10,long mIdDispositivo,String mRegistro,String mSyncronizado) {
        this.mNombre = mNombre;
        this.mCodigoEmpleado = mCodigoEmpleado;
        this.mDomicilio = mDomicilio;
        this.mCiudad = mCiudad;
        this.mTelefono = mTelefono;
        this.mCuentaContable = mCuentaContable;
        this.mFechaAlta = mFechaAlta;
        this.mFechaBaja = mFechaBaja;
        this.mImss = mImss;
        this.mStatus = mStatus;
        this.mFotoEmpleado = mFotoEmpleado;
        this.mIdEstacion = mIdEstacion;
        this.mTurno = mTurno;
        this.mFotoEmpleado1 = mFotoEmpleado1;
        this.mFotoEmpleado2 = mFotoEmpleado2;
        this.mFotoEmpleado3 = mFotoEmpleado3;
        this.mFotoEmpleado4 = mFotoEmpleado4;
        this.mFotoEmpleado5 = mFotoEmpleado5;
        this.mFotoEmpleado6 = mFotoEmpleado6;
        this.mFotoEmpleado7 = mFotoEmpleado7;
        this.mFotoEmpleado8 = mFotoEmpleado8;
        this.mFotoEmpleado9 = mFotoEmpleado9;
        this.mFotoEmpleado10 = mFotoEmpleado10;
        this.mIdDispositivo = mIdDispositivo;
        this.mRegistro = mRegistro;
        this.mSyncronizado = mSyncronizado;
    }

    public long getId() {
        return mId;
    }

    public void setId(long mId) {
        this.mId = mId;
    }

    public String getNombre() {
        return mNombre;
    }

    public void setNombre(String mNombre) {
        this.mNombre = mNombre;
    }

    public String getCodigoEmpleado() {
        return mCodigoEmpleado;
    }

    public void setCodigoEmpleado(String mCodigoEmpleado) {
        this.mCodigoEmpleado = mCodigoEmpleado;
    }

    public String getDomicilio() {
        return mDomicilio;
    }

    public void setDomicilio(String mDomicilio) {
        this.mDomicilio = mDomicilio;
    }

    public String getCiudad() {
        return mCiudad;
    }

    public void setCiudad(String mPhoneNumber) {
        this.mCiudad = mCiudad;
    }

    public String getTelefono() {
        return mTelefono;
    }

    public void setTelefono(String mEmail) {
        this.mTelefono = mTelefono;
    }

    public String getCuentaContable() {
        return mCuentaContable;
    }

    public void setCuentaContable(double mSalary) {
        this.mCuentaContable = mCuentaContable;
    }

    public String getFechaAlta() {
        return mFechaAlta;
    }

    public void setFechaAlta(String mFechaAlta) {
        this.mFechaAlta = mFechaAlta;
    }

    public String getFechaBaja() {
        return mFechaBaja;
    }

    public void setFechaBaja(String mFechaBaja) {
        this.mFechaBaja = mFechaBaja;
    }

    public String getImss() {
        return mImss;
    }

    public void setImss(String mImss) {
        this.mImss = mImss;
    }

    public String getStatus() {
        return mStatus;
    }

    public void setStatus(String mStatus) {
        this.mStatus = mStatus;
    }

    public byte[] getFotoEmpleado() {
        return mFotoEmpleado;
    }

    public void setFotoEmpleado(byte[] mFotoEmpleado) {
        this.mFotoEmpleado = mFotoEmpleado;
    }

    public long getIdEstacion() {
        return mIdEstacion;
    }

    public void setIdEstacion(long mIdEstacion) {
        this.mIdEstacion = mIdEstacion;
    }

    public long getTurno() {
        return mTurno;
    }

    public void setTurno(long mTurno) {
        this.mTurno = mTurno;
    }

    public byte[] getFotoEmpleado1() {
        return mFotoEmpleado1;
    }

    public void setFotoEmpleado1(byte[] mFotoEmpleado1) {
        this.mFotoEmpleado1 = mFotoEmpleado1;
    }

    public byte[] getFotoEmpleado2() {
        return mFotoEmpleado2;
    }

    public void setFotoEmpleado2(byte[] mFotoEmpleado2) {
        this.mFotoEmpleado2 = mFotoEmpleado2;
    }

    public byte[] getFotoEmpleado3() {
        return mFotoEmpleado3;
    }

    public void setFotoEmpleado3(byte[] mFotoEmpleado3) {
        this.mFotoEmpleado3 = mFotoEmpleado3;
    }

    public byte[] getFotoEmpleado4() {
        return mFotoEmpleado4;
    }

    public void setFotoEmpleado4(byte[] mFotoEmpleado4) {
        this.mFotoEmpleado4 = mFotoEmpleado4;
    }

    public byte[] getFotoEmpleado5() {
        return mFotoEmpleado5;
    }

    public void setFotoEmpleado5(byte[] mFotoEmpleado5) {
        this.mFotoEmpleado5 = mFotoEmpleado5;
    }

    public byte[] getFotoEmpleado6() {
        return mFotoEmpleado6;
    }

    public void setFotoEmpleado6(byte[] mFotoEmpleado6) {
        this.mFotoEmpleado6 = mFotoEmpleado6;
    }

    public byte[] getFotoEmpleado7() {
        return mFotoEmpleado7;
    }

    public void setFotoEmpleado7(byte[] mFotoEmpleado7) {
        this.mFotoEmpleado7 = mFotoEmpleado7;
    }

    public byte[] getFotoEmpleado8() {
        return mFotoEmpleado8;
    }

    public void setFotoEmpleado8(byte[] mFotoEmpleado8) {
        this.mFotoEmpleado8 = mFotoEmpleado8;
    }

    public byte[] getFotoEmpleado9() {
        return mFotoEmpleado9;
    }

    public void setFotoEmpleado9(byte[] mFotoEmpleado9) {
        this.mFotoEmpleado9 = mFotoEmpleado9;
    }

    public byte[] getFotoEmpleado10() {
        return mFotoEmpleado10;
    }

    public void setFotoEmpleado10(byte[] mFotoEmpleado10) {
        this.mFotoEmpleado10 = mFotoEmpleado10;
    }

    public long getIdDispositivo() {
        return mIdDispositivo;
    }

    public void setIdDispositivo(long mIdDispositivo) {
        this.mIdDispositivo = mIdDispositivo;
    }

    public String getRegistro() {
        return mRegistro;
    }

    public void setRegistro(String mRegistro) {
        this.mRegistro = mRegistro;
    }

    public String getSyncronizado() {
        return mSyncronizado;
    }

    public void setSyncronizado(String mSyncronizado) {
        this.mSyncronizado = mSyncronizado;
    }
}
