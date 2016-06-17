package orm;

import android.annotation.TargetApi;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class Configure implements ISession {
    // static Connection c = null;
    public static String dataBaseName;
    private static DataBaseHelper myDbHelper;
    private static boolean reloadBase = false;

    private SQLiteDatabase sqLiteDatabaseForReadable = null;
    private SQLiteDatabase sqLiteDatabaseForWritable = null;


    private Configure() {


        sqLiteDatabaseForReadable = GetSqLiteDatabaseForReadable();
        sqLiteDatabaseForWritable = GetSqLiteDatabaseForWritable();
    }


    public Configure(String dataBaseName, Context context, boolean isWriteLog,boolean reloadBase) {
        Configure.reloadBase = reloadBase;
        Loger.isWrite = isWriteLog;
        new Configure(dataBaseName, context);
    }


    private Configure(String dataBaseName, Context context) {
        Configure.dataBaseName = dataBaseName;

        myDbHelper = new DataBaseHelper(context, Configure.dataBaseName);
        if (reloadBase) {
            myDbHelper.getReadableDatabase();
            try {
                myDbHelper.copyDataBase();
            } catch (IOException e) {
                Loger.LogE(e.getMessage());
                throw new Error("MError copying database -" + e.getMessage());
            }
        } else {
            if (!myDbHelper.checkDataBase()) {
                myDbHelper.createDataBase();
            }

        }


    }

    public static String getBaseName() {
        return dataBaseName;
    }

    public static Configure getSession() {
        return new Configure();
    }

    private static SQLiteDatabase GetSqLiteDatabaseForReadable() throws SQLException {
        try {
            return myDbHelper.openDataBaseForReadable();
        } catch (Exception ex) {
            return null;
        }

    }

    private static SQLiteDatabase GetSqLiteDatabaseForWritable() throws SQLException {

        return myDbHelper.openDataBaseForWritable();
    }

    public static void createBase(String path) {
        File f = new File(path);
        if (f.exists()) return;
        f.getParentFile().mkdirs();
        try {
            f.createNewFile();
        } catch (IOException e) {
            Loger.LogE(e.getMessage());
            e.printStackTrace();
        }
    }

    private static String pizdaticusKey(ItemField field) {
        if (field.type == double.class || field.type == float.class || field.type == Double.class || field.type == Float.class) {
            return " REAL ";
        }
        if (field.type == int.class || field.type == long.class || field.type == short.class || field.type == byte.class || field.type == Integer.class ||
                field.type == Long.class || field.type == Short.class || field.type == Byte.class) {
            return " INTEGER ";
        }
        if (field.type == String.class) {
            return " TEXT ";
        }
        if (field.type == boolean.class) {
            return " BOOL ";
        }
        return "";
    }

    private static String pizdaticusField(ItemField field) {
        if (field.type == double.class || field.type == float.class || field.type == Double.class || field.type == Float.class) {
            return " REAL DEFAULT 0, ";
        }
        if (field.type == int.class || field.type == Enum.class || field.type == long.class || field.type == short.class || field.type == byte.class || field.type == Integer.class ||
                field.type == Long.class || field.type == Short.class) {
            return " INTEGER DEFAULT 0, ";
        }
        if (field.type == String.class) {
            return " TEXT, ";
        }
        if (field.type == boolean.class || field.type == Boolean.class) {
            return " BOOL DEFAULT 0, ";
        }

        if (field.type == byte[].class) {
            return " BLOB, ";
        }
        return "";
    }

    public static void createTable(Class<?> aClass) {
        cacheMetaDate date = CacheDictionary.getCacheMetaDate(aClass);
        StringBuilder sb = new StringBuilder("CREATE TABLE " + date.tableName + " (");
        sb.append(date.keyColumn.columName).append(" ");
        sb.append(pizdaticusKey(date.keyColumn));
        sb.append("PRIMARY KEY, ");
        for (Object f : date.listColumn) {
            ItemField ff = (ItemField) f;
            sb.append(ff.columName);
            sb.append(pizdaticusField(ff));
        }
        String s = sb.toString().trim();
        String ss = s.substring(0, s.length() - 1);
        String sql = ss + ")";
        Configure.getSession().execSQL(sql);
    }

    @Override
    public <T> int update(T item) {
        SQLiteDatabase con = sqLiteDatabaseForWritable;
        cacheMetaDate d = CacheDictionary.getCacheMetaDate(item.getClass());
        ContentValues values = null;
        try {
            values = getContentValues(item, d);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
        Object key = null;
        try {
            Field field=d.keyColumn.field;
            field.setAccessible(true);
            key = field.get(item);
        } catch (Exception e) {
            Loger.LogE(e.getMessage());
            e.printStackTrace();
        }
        assert key != null;
        if (d.isIAction()) {
            ((IActionOrm) item).actionBeforeUpdate(item);
        }
        int i = con.update(d.tableName, values, d.keyColumn.columName + " = ?", new String[]{key.toString()});
        if (i == -1) {
            try {

                throw new Exception("Not Update");
            } catch (Exception e) {
                Loger.LogE(e.getMessage());
                e.printStackTrace();
                throw new RuntimeException("ORM simple update - " + e.getMessage());
            }
        } else {
            if (d.isIAction()) {
                ((IActionOrm) item).actionAfterUpdate(item);
            }
        }
        return i;
    }

    @Override
    public <T> int insert(T item) {

        SQLiteDatabase con = sqLiteDatabaseForWritable;
        cacheMetaDate d = CacheDictionary.getCacheMetaDate(item.getClass());
        ContentValues values = null;
        try {
            values = getContentValues(item, d);
        } catch (NoSuchFieldException e) {

            throw  new RuntimeException(e.getMessage());
          //  e.printStackTrace();
        }
        if (d.isIAction()) {
            ((IActionOrm) item).actionBeforeInsert(item);
        }
        int i = (int) con.insert(d.tableName, null, values);
        if (i == -1) {
            try {
                throw new Exception("Not insert");
            } catch (Exception e) {
                Loger.LogE(e.getMessage());
                e.printStackTrace();
            }
        } else {
            if (d.isIAction()) {
                ((IActionOrm) item).actionAfterInsert(item);
            }
        }
        try {
            d.keyColumn.field.setAccessible(true);
            d.keyColumn.field.set(item,i);
//            Field field=item.getClass().getDeclaredField(d.keyColumn.fieldName);
//            field.setAccessible(true);
//            field.set(item, i);
        } catch (Exception e) {
            Loger.LogE(e.getMessage());
            new RuntimeException("ORM insert ---" + e.getMessage());
        }
        return i;
    }

    private <T> ContentValues getContentValues(T item, cacheMetaDate<?> d) throws NoSuchFieldException {
        ContentValues values = new ContentValues();
        for (ItemField str : d.listColumn) {
            Field field= str.field;//item.getClass().getDeclaredField(str.fieldName);
            field.setAccessible(true);

            if (str.type == String.class)
                try {

                    values.put(str.columName, (String) field.get(item));
                } catch (Exception e) {
                    Loger.LogE(e.getMessage());
                    e.printStackTrace();
                }

            if (str.type == int.class)
                try {
                    values.put(str.columName, (int)  field.get(item));
                } catch (Exception e) {
                    Loger.LogE(e.getMessage());
                    e.printStackTrace();
                }
            if (str.type == long.class)
                try {
                    values.put(str.columName, (long) field.get(item));
                } catch (Exception e) {
                    Loger.LogE(e.getMessage());
                    e.printStackTrace();
                }
            if (str.type == short.class)
                try {
                    values.put(str.columName, (short) field.get(item));
                } catch (Exception e) {
                    Loger.LogE(e.getMessage());
                    e.printStackTrace();
                }
            if (str.type == byte.class)
                try {
                    values.put(str.columName, (byte) field.get(item));
                } catch (Exception e) {
                    Loger.LogE(e.getMessage());
                    e.printStackTrace();
                }

            if (str.type == Short.class)
                try {
                    values.put(str.columName, (Short) field.get(item));
                } catch (Exception e) {
                    Loger.LogE(e.getMessage());
                    e.printStackTrace();
                }
            if (str.type == Long.class)
                try {
                    values.put(str.columName, (Long)    field.get(item));
                } catch (Exception e) {
                    Loger.LogE(e.getMessage());
                    e.printStackTrace();
                }

            if (str.type == Integer.class)
                try {
                    values.put(str.columName, (Integer) field.get(item));
                } catch (Exception e) {
                    Loger.LogE(e.getMessage());
                    e.printStackTrace();
                }
            if (str.type == Double.class)
                try {
                    values.put(str.columName, (Double)  field.get(item));
                } catch (Exception e) {
                    Loger.LogE(e.getMessage());
                    e.printStackTrace();
                }

            if (str.type == Float.class)
                try {
                    values.put(str.columName, (Float)   field.get(item));
                } catch (Exception e) {
                    Loger.LogE(e.getMessage());
                    e.printStackTrace();
                }


            if (str.type == byte[].class)
                try {
                    values.put(str.columName, (byte[]) field.get(item));
                } catch (Exception e) {
                    Loger.LogE(e.getMessage());
                    e.printStackTrace();
                }

            if (str.type == double.class)
                try {
                    values.put(str.columName, (double)   field.get(item));
                } catch (Exception e) {
                    Loger.LogE(e.getMessage());
                    e.printStackTrace();
                }

            if (str.type == boolean.class)
                try {
                    boolean val = (boolean) field.get(item);
                    if (val) {
                        values.put(str.columName, 1);
                    } else {
                        values.put(str.columName, 0);
                    }

                } catch (Exception e) {
                    Loger.LogE(e.getMessage());
                    e.printStackTrace();
                }
        }
        return values;
    }

    @Override
    public <T> int delete(T item) {
        SQLiteDatabase con = sqLiteDatabaseForWritable;
        cacheMetaDate d = CacheDictionary.getCacheMetaDate(item.getClass());

        Object key = null;
        try {
            Field field=d.keyColumn.field;//item.getClass().getDeclaredField(d.keyColumn.fieldName);
            field.setAccessible(true);
            key = field.get(item);
        } catch (Exception e) {
            Loger.LogE(e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("ORM simple dlete - " + e.getMessage());
        }
        assert key != null;
        if (d.isIAction()) {
            ((IActionOrm) item).actionBeforeDelete(item);
        }
        int res = con.delete(d.tableName, d.keyColumn.columName + "=?", new String[]{key.toString()});
        if (res != 0) {
            if (d.isIAction()) {
                ((IActionOrm) item).actionAfterDelete(item);
            }
        } else {
            Loger.LogE("Not Delete");
        }
        return res;
    }

    @Override
    public <T> List<T> getList(Class<T> tClass, String where, Object... objects) {
        List<T> list = new ArrayList<>();
        SQLiteDatabase con;
        try {
            con = sqLiteDatabaseForReadable;
            cacheMetaDate d = CacheDictionary.getCacheMetaDate(tClass);
            Cursor c = null;
            String[] sdd = d.getStringSelect();
            if (where == null && objects == null||where == null && objects.length==0) {

                c = con.query(d.tableName, sdd, null, null, null, null, null, null);
            }else if (where != null && objects == null||where != null && objects.length==0) {
                c = con.query(d.tableName, sdd, where, null, null, null, null, null);
            }


            if (where != null && objects != null) {
                String[] lstr = new String[objects.length];
                for (int i = 0; i < objects.length; i++) {
                    lstr[i] = String.valueOf(objects[i]);
                }
                c = con.query(d.tableName, sdd, where, lstr, null, null, null, null);
            }
            if (c != null) {
                try {
                    if (c.moveToFirst()) {
                        do {
                            Object sd = tClass.newInstance();
                            Companaund(d.listColumn, d.keyColumn, c, sd);
                            list.add((T) sd);
                        } while (c.moveToNext());
                    }
                } finally {
                    c.close();
                }
            }
        } catch (SQLException e) {
            //Loger.LogE(e.getMessage());
            new RuntimeException("ORM getList ---" + e.getMessage());
            return null;
        } catch (Exception e) {
            //Loger.LogE(e.getMessage());
            new RuntimeException("ORM getList---" + e.getMessage());
        }
        return list;
    }

    private void Companaund(List<ItemField> listIf, ItemField key, Cursor c, Object o) throws NoSuchFieldException, IllegalAccessException {
        for (ItemField str : listIf) {
            int i = c.getColumnIndex(str.columName);
            Field res=  str.field;// o.getClass().getDeclaredField(str.fieldName);
            res.setAccessible(true);
            if (str.type == int.class) {
                    res.setInt(o, c.getInt(i));
            }
            if (str.type == String.class) {
                    res.set(o, c.getString(i));
            }
            if (str.type == double.class) {
                    res.setDouble(o, c.getDouble(i));
            }
            if (str.type == float.class) {
                    res.setFloat(o, c.getFloat(i));
            }
            if (str.type == long.class) {
                    res.setLong(o, c.getLong(i));
            }
            if (str.type == short.class) {
                    res.setShort(o, c.getShort(i));
            }
            if (str.type == byte[].class) {
                    res.set(o, c.getBlob(i));
            }
            if (str.type == byte.class) {
                    res.setByte(o, (byte) c.getLong(i));
            }
            if (str.type == Integer.class) {
                    int ii = c.getInt(i);
                    res.set(o, ii);
            }
            ////////
            if (str.type == Double.class) {
                    Double d = c.getDouble(i);
                    res.set(o, d);
            }
            if (str.type == Float.class) {
                    Float f = c.getFloat(i);
                    res.set(o, f);
            }
            if (str.type == Long.class) {
                    Long l = c.getLong(i);
                    res.set(o, l);
            }
            if (str.type == Short.class) {
                    Short sh = c.getShort(i);
                    res.set(o, sh);
            }
            if (str.type == boolean.class) {
                boolean val;
                val = c.getInt(i) != 0;
                    res.setBoolean(o, val);
            }
        }
        try {
            Field field=key.field;
            field.setAccessible(true);
           field.set(o, c.getInt(c.getColumnIndex(key.columName)));
        } catch (Exception e) {
            throw new RuntimeException("orm get id"+e.getMessage());
        }
    }

    private boolean containInterface(Class<?>[] classes){
        for (Class<?> aClass : classes) {
            if(aClass==IUsingGuidId.class){
                return true;
            }
        }
        return false;
    }

    @Override
    public <T> T get(Class<T> tClass, Object id) {
        cacheMetaDate d = CacheDictionary.getCacheMetaDate(tClass);
        List<T> res;
        if(containInterface(tClass.getInterfaces())&& id instanceof String){
            if(id==null||id.toString().trim().length()==0){
                return null;
            }
            res = getList(tClass, " idu = ? ",id.toString());
        }else{
            res = getList(tClass, d.keyColumn.columName + "=?", id);
        }

        if (res.size() == 0) return null;
        if (res.size() > 1) {
            throw new RuntimeException("orm (get) more than one");
        }
        return res.get(0);
    }

    @Override
    public Object executeScalar(String sql, Object... objects) {
        List<String> arrayList = new ArrayList<>();
        String[] array = null;
        if (objects != null && objects.length > 0) {
            for (Object object : objects) {
                arrayList.add(String.valueOf(object));
            }
            array = new String[arrayList.size()];
            arrayList.toArray(array);
        }
        Loger.LogI(sql);
        return InnerListExe(sql, array);
    }

    @Override
    public void execSQL(String sql, Object... objects) {

        List<String> arrayList = new ArrayList<>();
        String[] array;
        if (objects != null && objects.length > 0) {
            for (Object object : objects) {
                arrayList.add(String.valueOf(object));
            }
            array = new String[arrayList.size()];
            arrayList.toArray(array);
            sqLiteDatabaseForWritable.execSQL(sql, array);
        } else {
            sqLiteDatabaseForWritable.execSQL(sql);
        }
        Loger.LogI(sql);
    }

    @Override
    public void deleteTable(String tableName){
        sqLiteDatabaseForWritable.delete(tableName,null,null);
    }



    @Override
    public void beginTransaction() {
        myDbHelper.getWritableDatabase().beginTransaction();
    }

    @Override
    public void commitTransaction() {
        myDbHelper.getWritableDatabase().setTransactionSuccessful();
    }

    @Override
    public void endTransaction() {
        myDbHelper.getWritableDatabase().endTransaction();
    }

    @Override
    public void close() {
        myDbHelper.close();
        Loger.LogI("Close");
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private Object InnerListExe(String sql, String[] strings) {
        Cursor c;
        if (strings == null) {
            c = sqLiteDatabaseForReadable.rawQuery(sql, null);
        } else {
            c = sqLiteDatabaseForReadable.rawQuery(sql, strings);
        }
        if (c != null) {
            try {
                if (c.moveToFirst()) {
                    do {
                        int i = c.getType(0);
                        if (i == 0) {
                            return null;
                        }
                        if (i == 1) {
                            return c.getInt(0);
                        }
                        if (i == 2) {
                            return c.getFloat(0);
                        }
                        if (i == 3) {
                            return c.getString(0);
                        }
                        if (i == 4) {
                            return c.getBlob(0);
                        }
                    } while (c.moveToNext());
                }
            } finally {
                c.close();
            }
        }
        return null;
    }

}
