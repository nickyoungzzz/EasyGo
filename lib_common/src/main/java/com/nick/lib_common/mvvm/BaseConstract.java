package com.nick.lib_common.mvvm;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;

import retrofit2.Retrofit;

public class BaseConstract {

    public static void main(String[] args)
    {
//        t((F<BaseModel>) o -> {
//            System.out.println(o.name);
//        });
        try {
            t((F<String>) o -> System.out.println(o));
        } catch (Exception e) {
            e.printStackTrace();
        }

//        t((F<List<BaseModel>>) strings -> System.out.println(strings.get(1).name));
//        Type[] types = f.getClass().getGenericInterfaces();
//        Type[] params = ((ParameterizedType) types[0]).getActualTypeArguments();
//        Class<?> reponseClass = (Class<?>) params[0];
//        System.out.println(reponseClass.getComponentType().getSimpleName());
//        System.out.println(getSuperClassGenricType(f.getClass()));
        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<BaseModel>>(){}.getType();
        String json = "[{\"name\":\"zhangsan\"}, {\"name\":\"lisi\"}]";
//        List list = gson.fromJson(json, params[0]);
//        System.out.println(list.get(1).getClass().getSimpleName());
    }

    static void t(F f) throws Exception {
//        Type[] types = f.getClass().getGenericInterfaces();
//        Type[] params = ((ParameterizedType) types[0]).getActualTypeArguments();
//        Class<?> reponseClass = (Class<?>) params[0];
//        System.out.println(reponseClass.getComponentType().getSimpleName());
//        System.out.println(getSuperClassGenricType(f.getClass()));
        Gson gson = new Gson();
//        Type type = new TypeToken<ArrayList<BaseModel>>(){}.getType();
        String json = "[{\"name\":\"zhangsan\"}, {\"name\":\"lisi\"}]";
        String json1 = "{\"name\":\"zhangsan\"}";
//        BaseModel baseModel = gson.fromJson(json1, f.getGenericType());
//        if (f.getGenericType() == String.class)
//        {
//
//        }
//        else
//        {
//
//        }
        Type type = f.getGenericType();
        if (type == String.class)
        {
            f.i(json1);
        }
        else
        {
            f.i(gson.fromJson(json1, type));
        }
//        System.out.println(list.get(1).getClass().getSimpleName());
    }

    @FunctionalInterface
    public interface F<T>
    {
       void i(T t);

       default Type getGenericType() throws Exception {
           Type[] types = this.getClass().getGenericInterfaces();
           if (types.length > 0)
           {
               Type type = types[0];
               Retrofit retrofit = new Retrofit.Builder().client(null).build();
               System.out.println(type.toString());
               if (type instanceof ParameterizedType)
               {
                   return ((ParameterizedType) type).getActualTypeArguments()[0];
               }
               else
               {
                   throw new Exception();
               }
           }
           return String.class;
       }
    }

}
