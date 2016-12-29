# Simple Orm for Android 
=======
Quickstart

**1.**  -Availability of the database file in the folder assets.

**2.**  -Initialization ORM at the entry point of the program:

```java
 new Configure(
               getApplicationInfo().dataDir + "/test_data.pdb", //path file data
               getBaseContext(),// current contex
               true  // you have to have assets test_data.sqlite base
                     //true - at each start, it will be overwritten.
                     //false - at the start, if it is not to be written, rewriting is not allowed
       );
```

**3.**  -Create class - map database tables.
```java
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
```java
  ISession ses=Configure.getSession();
        Object dds=  ses.executeScalar("SELECT name FROM sqlite_master WHERE type='table' AND name='test1';", null);
        if(dds==null){
            Configure.createTable(Test1.class);
        }
   Test1 dd=new Test1();
   dd.name="sdsdsd";
   dd.longs=12132388;
   dd.aShort=34;
   dd.aByte=45;
   byte[] rr=new byte[2];
   rr[0]=3;
   rr[1]=45;
   dd.aBlob=rr;
   dd.inte = 35;
   Configure.getSession().insert(dd);
   
   List<Test1> test1List=Configure.getSession().getList(Test1.class,null);
   List<Test1> test1List1=Configure.getSession().getList(Test1.class," id =? ",1);
   Test1 test1=Configure.getSession().get(Test1.class,1);
   int res= (int) Configure.getSession().executeScalar(" select count(*) from test1",null);
```

```java
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
