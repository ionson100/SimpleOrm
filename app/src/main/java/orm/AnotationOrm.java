package orm;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;


class AnotationOrm{
    public static String getTableName(Class aClass){
        if(!aClass.isAnnotationPresent(Table.class)){
            return null;
        }
        return ((Table) aClass.getAnnotation(Table.class)).name();
    }

    public static ItemFild getKeyName(Class aClass){
        Field[] field = aClass.getFields();

        for(Field f: field){
            if(f.isAnnotationPresent(PrimaryKey.class)){
                final  PrimaryKey key=f.getAnnotation(PrimaryKey.class);
                ItemFild fi=new ItemFild();
                fi.type=f.getType();
                fi.fieldName=f.getName();
                fi.columName=key.name();
                return fi;
            }

        }
        return null;
    }

    public static List<ItemFild> getListColumn(Class aClass){
        Field[] field = aClass.getFields();
        List<ItemFild> list= new ArrayList<>();
        for(Field f: field){
            if(f.isAnnotationPresent(Column.class)){
                final Column key=f.getAnnotation(Column.class);
                ItemFild fi=new ItemFild();
                fi.columName=key.name();
                fi.fieldName=f.getName();
                fi.type=f.getType();
                list.add(fi);
            }
        }
        return list;
    }

}
