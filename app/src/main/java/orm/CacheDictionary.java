package orm;

import java.util.Dictionary;
import java.util.Hashtable;


class CacheDictionary {
    private static final Dictionary dic = new Hashtable();

    public static <T> cacheMetaDate getCacheMetaDate(Class<T> aClass) {
        cacheMetaDate g = (cacheMetaDate) dic.get(aClass.getName());
        if (g == null) {
            g = new cacheMetaDate<>(aClass);

            dic.put(aClass.getName(), g);
        }
        return g;
    }

}
