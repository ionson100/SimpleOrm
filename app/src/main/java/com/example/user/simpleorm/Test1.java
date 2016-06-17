package com.example.user.simpleorm;

import orm.Column;
import orm.IActionOrm;
import orm.PrimaryKey;
import orm.Table;

@Table("test1")
public class Test1 implements IActionOrm<Test1> {
    @PrimaryKey("id")
    public int id;

    @Column("name")
    public  String name;

    @Column("longs")
    public long longs;

    @Column("inte")
    public Integer inte;

    @Column("ashort")
    public short aShort;

    @Column("abyte")
    public byte aByte;

    @Column("ablob")
    public byte[] aBlob;


    @Override
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
