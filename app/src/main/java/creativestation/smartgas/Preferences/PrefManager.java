package creativestation.smartgas.Preferences;

import android.content.Context;
import android.content.SharedPreferences;

public class PrefManager {
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    Context _context;

    // shared pref mode
    int PRIVATE_MODE = 0;

    // Shared preferences file name
    private static final String PREF_NAME = "Smart Gas";

    private static final String IS_LOGIN_IN = "isLoginIn";
    //alat
    private static final String IS_PUNYA_ALAT = "IsPunyaAlat";

    private static final String ALAT = "Alat";

    public PrefManager(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public void setLoginStatus(boolean isLoginIn) {
        editor.putBoolean(IS_LOGIN_IN, isLoginIn);
        editor.commit();
    }

    public boolean isLoginIn() {
        return pref.getBoolean(IS_LOGIN_IN, false);
    }

    public void setPunyaAlat(boolean isPunyaAlat){
        editor.putBoolean(IS_PUNYA_ALAT, isPunyaAlat);
        editor.commit();
    }

    public boolean isPunyaAlat() {
        return pref.getBoolean(IS_PUNYA_ALAT, false);
    }

    public void setAlat(String alat){
        editor.putString(ALAT, alat);
        editor.commit();
    }

    public String getAlat(){
        return pref.getString(ALAT, "");
    }

}
