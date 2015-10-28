# simpleOrm for Android 
=======
Quickstart

**1.**  -Availability of the database file in the folder assets.

**2.**  -Initialization ORM at the entry point of the program:

```
 new Configure(
               getApplicationInfo().dataDir + "/test_data.pdb", //path file data
               getBaseContext(),// current contex
               true, // rewrite base file (debug)
               true  // Write log
       );
```

**3.**  -Create class - map database tables.
```
@Table(name = "test1")
public class Test1 implements IActionOrm<Test1>{
    @PrimaryKey(name = "id")
    public int id;

    @Column(name = "name")
    public  String name;

    @Column(name = "longs")
    public long longs;
    
    public void actionBeforeUpdate(Test1 test1) {
    }

    @Override
    public void actionAfterUpdate(Test1 test1) {
    }

    @Override
    public void actionBeforeInsert(Test1 test1) {
    }

    @Override
    public void actionAfterInsert(Test1 test1) {
    }

    @Override
    public void actionBeforeDelete(Test1 test1) {
    }

    @Override
    public void actionAfterDelete(Test1 test1) {
    }
}
```

**4.**  -Program realization:
```
  ISession ses=Configure.getSession();
        Object dds=  ses.executeScalar("SELECT name FROM sqlite_master WHERE type='table' AND name='test1';", null);
        if(dds==null){
            Configure.createTable(Test1.class);
        }
```

```
    <T> int update(T item);
    <T> int insert(T item);
    <T> int delete(T item);
    <T> List<T> getList(Class<T> tClass, String where, Object... objects) ;
    <T> T get(Class<T> tClass, Object id);
    <T> Object executeScalar(String sql, Object ... objects);
    void execSQL(String sql, Object ... objects);
    void beginTransaction();
    void commitTransaction();
    void endTransaction();
    void close();
    ```
    
    **implements IActionOrm - optional**
