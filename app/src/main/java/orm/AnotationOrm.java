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

    public static ItemField getKeyName(Class aClass){
        Field[] field = aClass.getFields();

        for(Field f: field){
            if(f.isAnnotationPresent(PrimaryKey.class)){
                final  PrimaryKey key=f.getAnnotation(PrimaryKey.class);
                ItemField fi=new ItemField();
                fi.type=f.getType();
                fi.fieldName=f.getName();
                fi.columName=key.name();
                return fi;
            }

        }
        return null;
    }

    public static List<ItemField> getListColumn(Class aClass){
        Field[] field = aClass.getFields();
        List<ItemField> list= new ArrayList<>();
        for(Field f: field){
            if(f.isAnnotationPresent(Column.class)){
                final Column key=f.getAnnotation(Column.class);
                ItemField fi=new ItemField();
                fi.columName=key.name();
                fi.fieldName=f.getName();
                fi.type=f.getType();
                list.add(fi);
            }
        }
        return list;
    }

}
