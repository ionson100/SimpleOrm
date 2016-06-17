package orm;


import java.util.List;

class cacheMetaDate<T> {

    private int isIAction = 0;
    private Class result = null;
    public List<ItemField> listColumn = null;
    public ItemField keyColumn = null;
    String tableName = null;


    public boolean isIAction() {
        return isIAction == 1;
    }

    public cacheMetaDate(Class<T> aClass) {
        SetClass(aClass);
    }

    private void SetClass(Class tClass) {
        if (result == null) {
            result = tClass;
        }
        if (tableName == null) {
            tableName = AnotationOrm.getTableName(tClass);
        }
        if (keyColumn == null) {
            keyColumn = AnotationOrm.getKeyName(tClass);
        }
        if (listColumn == null) {
            listColumn = AnotationOrm.getListColumn(tClass);
        }
        if (isIAction == 0) {
            isIAction = 2;
            for (Class aClass : tClass.getInterfaces()) {
                if (aClass == IActionOrm.class) {
                    isIAction = 1;
                }
            }
        }
    }



    public String[] getStringSelect() {
        String[] list = new String[listColumn.size() + 1];
        for (int i = 0; i < listColumn.size(); i++) {
            list[i] = listColumn.get(i).columName;
        }
        if(keyColumn!=null&&keyColumn.columName!=null)
        list[listColumn.size()] = keyColumn.columName;
        return list;
    }





     String getValueBulkString(String stringColumn, List<String> stringValue) {
        String[] col=stringColumn.split(",");
        StringBuilder sb=new StringBuilder();
        for (int i=0;i<col.length;i++){
            for (ItemField itemField : listColumn) {
                if(itemField.columName.equals(col[i])){
                    if(itemField.type==String.class){
                        sb.append(" '").append(stringValue.get(i)).append("',");
                    }else{
                        sb.append(" ").append(stringValue.get(i)).append(",");
                    }
                }
            }
        }
        return sb.substring(0, sb.lastIndexOf(","));
    }

     String resolver(String stringColumn, List<String> stringValue) {
        StringBuilder sb=new StringBuilder(" select ");
        String[] col=stringColumn.split(",");
        for (int i=0;i<col.length;i++){

            for (ItemField itemField : listColumn) {
                if(itemField.columName.equals(col[i])){
                    if(itemField.type==String.class){
                        sb.append(" '").append(stringValue.get(i)).append("' as ").append(col[i]).append(",");
                    }else{
                        sb.append(" ").append(stringValue.get(i)).append(" as ").append(col[i]).append(",");
                    }
                }
            }

        }
        return sb.substring(0,sb.lastIndexOf(",")).toString()+" ";
    }
}
