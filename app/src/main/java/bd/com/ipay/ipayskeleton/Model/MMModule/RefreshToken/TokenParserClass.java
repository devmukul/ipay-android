package bd.com.ipay.ipayskeleton.Model.MMModule.RefreshToken;

public class TokenParserClass {

    private String iss;
    private String jti;
    private String sub;
    private String aud;
    private long iat;
    private long exp;
    private String deviceId;
    private String refId;

    public TokenParserClass() {

    }

    public String getIss() {
        return iss;
    }

    public String getJti() {
        return jti;
    }

    public String getSub() {
        return sub;
    }

    public String getAud() {
        return aud;
    }

    public long getIat() {
        return iat;
    }

    public long getExp() {
        return exp;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public String getRefId() {
        return refId;
    }
}
