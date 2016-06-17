package orm;

import android.util.Log;


class Loger {
    public static boolean isWrite = true;

    public static void LogE(String msg) {
        if (isWrite) {
            Log.e("ORM", msg);
        }

    }

    public static void LogI(String msg) {
        if (isWrite) {
            Log.i("ORM", msg);
        }

    }
}
