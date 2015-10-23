package orm;

import java.util.List;

class cacheMetaDate<T> {

    private int isIAction=0;
    private Class result=null;
    public List<ItemFild> listColumn = null;
    public ItemFild keyColumn = null;
    String tableName=null;
    public boolean isIAction(){
        if(isIAction==1) return  true;
        return false;
    }

     public cacheMetaDate(Class<T> aClass){
         SetClass(aClass);
     }

     private void SetClass(Class tClass){
        if(result==null) {
            result =tClass;
        }
        if(tableName==null){
            tableName= AnotationOrm.getTableName(tClass);
        }
        if(keyColumn==null){
            keyColumn= AnotationOrm.getKeyName(tClass);
        }
        if(listColumn==null){
            listColumn= AnotationOrm.getListColumn(tClass);
        }
         if(isIAction==0){
             isIAction=2;
             for (Class aClass : tClass.getInterfaces()) {
                 if(aClass==IActionOrm.class){
                     isIAction=1;
                 }
             }
         }
    }

    public  String getStringForSelect(){
        StringBuilder sb=new StringBuilder();
        for(ItemFild f:listColumn){
            sb.append(f.columName);
            sb.append(", ");

        }
        String res=sb.toString().trim().substring(0,sb.toString().trim().length()-1);
        return "Select " +keyColumn.columName+"," +res +" from "+ tableName;
    }

    public String[] getStringSelect(){
        String[] list=new String[listColumn.size()+1];
        for (int i=0;i<listColumn.size();i++){
            list[i]= listColumn.get(i).columName;
        }
        list[listColumn.size()]=keyColumn.columName;
        return list;
    }
}
