package com.example.user.simpleorm;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import orm.Configure;
import orm.ISession;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


       new Configure(getApplicationInfo().dataDir + "/test_data.sqlite",getBaseContext(),true);
      Object dds=  Configure.getSession().executeScalar("SELECT name FROM sqlite_master WHERE type='table' AND name='test1';", null);
        if(dds==null){
            Configure.getSession().createTable(Test1.class);
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
        dd.inte = 353;


        ISession ses=Configure.getSession();
        try{
            ses.beginTransaction();
            ses.insert(dd);
            Test1 df=ses.get(Test1.class,dd.id);
            df.aBlob=null;
            df.aByte=100;
            df.longs=100;
            df.aShort=100;
            ses.update(df);

            if(1==1){
                throw new Exception("sdsd");
            }
            ses.commitTransaction();
        }catch (Exception ex){
            ses.endTransaction();
    }

       // Configure.getSession().delete(df);

        Object ss=ses.executeScalar("select count(*) from test1 ",null);
        int rre=6;
    }


}
