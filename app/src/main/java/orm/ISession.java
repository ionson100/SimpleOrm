package orm;

import java.util.List;

public interface ISession {


    <T> int update(T item);
    <T> int insert(T item);
    <T> int delete(T item);
    <T> List<T> getList(Class<T> tClass, String where, Object... objectses) ;

    <T> T get(Class<T> tClass, Object id);
    <T> Object executeScalar(String sql, Object ... objects);
    void execSQL(String sql, Object ... objects);
    void beginTransaction();
    void commitTransaction();
    void endTaransaction();
    void close();


}



