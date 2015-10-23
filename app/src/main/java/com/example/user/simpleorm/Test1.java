package com.example.user.simpleorm;

import orm.Column;
import orm.IActionOrm;
import orm.PrimaryKey;
import orm.Table;

@Table(name = "test1")
public class Test1 implements IActionOrm<Test1>{
    @PrimaryKey(name = "id")
    public int id;

    @Column(name = "name")
    public  String name;

    @Column(name = "longs")
    public long longs;

    @Column(name = "inte")
    public Integer inte;

    @Column(name = "ashort")
    public short aShort;

    @Column(name = "abyte")
    public byte aByte;

    @Column(name = "ablob")
    public byte[] aBlob;

    @Override
    public void actionBeforeUpdate(Test1 test1) {

    }

    @Override
    public void actionAfterUpdate(Test1 test1) {

    }

    @Override
    public void actionBeforeInsert(Test1 test1) {

        String dd= test1.name;
    }

    @Override
    public void actionAfterInsert(Test1 test1) {

        int id= test1.id;
    }

    @Override
    public void actionBeforeDelete(Test1 test1) {

    }

    @Override
    public void actionAfterDelete(Test1 test1) {

    }
}
