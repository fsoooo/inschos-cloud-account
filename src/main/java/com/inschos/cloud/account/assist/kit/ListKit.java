package com.inschos.cloud.account.assist.kit;

import java.util.*;
import java.util.function.Function;

/**
 * Created by IceAnt on 2017/9/11.
 */
public class ListKit {


    public static <K, V> Map<K, V> toMap(List<V> list,Function<? super V, ? extends K> keyMapper) {

        Map<K, V> map = new HashMap<K, V>();
        if (list != null) {
            for (V value : list) {

                try {
                    K key = keyMapper.apply(value);
                    map.put(key, value);
                } catch (Exception e) {
                    L.log.error("field can't match the key!",e);
                }

            }

        }

        return map;
    }


    public static <K, V> Map<K, V> toMap(List<V> list,String fieldKey) {

        Map<K, V> map = new HashMap<K, V>();
        if (list != null) {
            for (V value : list) {

                try {
                    @SuppressWarnings("unchecked")
                    K k = (K) ReflectKit.getter(value,fieldKey);
                    map.put(k, value);
                } catch (Exception e) {
                    L.log.error("field can't match the key!",e);
                }

            }

        }

        return map;
    }

    public static <K, V> Map<K, List<V>> toMapList(List<V> list,String fieldKey) {

        Map<K, List<V>> map = new HashMap<>();
        if (list != null) {
            for (V value : list) {

                try {
                    @SuppressWarnings("unchecked")
                    K k = (K) ReflectKit.getter(value,fieldKey);
                    if(map.containsKey(k)){
                        map.get(k).add(value);
                    }else{
                        List<V> vList = new ArrayList<>();
                        vList.add(value);
                        map.put(k,vList);
                    }
                } catch (Exception e) {
                    L.log.error("field can't match the key!",e);
                }

            }

        }

        return map;
    }

    public static <K, V> Map<K, List<V>> toMapList(List<V> list,Function<? super V, ? extends K> keyMapper) {

        Map<K, List<V>> map = new HashMap<>();
        if (list != null) {
            for (V value : list) {

                try {
                    K key = keyMapper.apply(value);
                    if(map.containsKey(key)){
                        map.get(key).add(value);
                    }else{
                        List<V> vList = new ArrayList<>();
                        vList.add(value);
                        map.put(key,vList);
                    }
                } catch (Exception e) {
                    L.log.error("field can't match the key!",e);
                }

            }

        }

        return map;
    }

    public static <E,T> List<E> toColumnList(List<T> list,String fieldKey){
        if (list != null) {
            List<E> arrayList = new ArrayList<>();
            for (T t : list)
                try {
                    @SuppressWarnings("unchecked")
                    E e = (E) ReflectKit.getter(t, fieldKey);
                    arrayList.add(e);
                } catch (Exception e) {
                    L.log.error("field can't match the key!", e);
                }
            return arrayList;
        }
        return null;
    }
    public static <E,T> List<E> toColumnList(List<T> list,Function<? super T, ? extends E> keyMapper){
        if (list != null) {
            List<E> arrayList = new ArrayList<>();
            for (T t : list)
                try {
                    E e = keyMapper.apply(t);
                    arrayList.add(e);
                } catch (Exception e) {
                    L.log.error("field can't match the key!", e);
                }
            return arrayList;
        }
        return null;
    }


    public static <K,V> List<V> toList(Map<K,V> map){

        if(map!=null){
            return new ArrayList<>(map.values());
        }
        return null;
    }

    public static <T> List<T> toUnique(List<T> list){
        if(list!=null){
            Set<T> set = new HashSet<>();
            set.addAll(list);
            list.clear();
            list.addAll(set);
        }
        return list;
    }

    public static <T> boolean toRemoveO(List<T> list, T o){
        boolean result = false;
        if(list!=null){
            if(o instanceof Integer){
                int index = list.indexOf(o);
                if(index>-1 && index<list.size()){
                    T remove = list.remove(index);
                    result = remove!=null;
                }
            }else{
                result = list.remove(o);
            }
        }
        return result;
    }


}
