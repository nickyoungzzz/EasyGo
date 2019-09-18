package com.nick.lib_common.kt

import io.reactivex.Observable

public class Test (age : Int){

    companion object  {

        @JvmStatic
        fun main(args : ArrayList<String>)
        {
            System.out.println("S")
        }
    }


    fun max(a : Int, b : Int) : Int = if (a > b) a else b

    constructor(name:String, age: Int) : this(age)
    {
        Observable.just(1).subscribe()
    }


    fun ma(x : Int)
    {
        Test(1)
        val array = ArrayList<Int>();
        for (x in 1..3)
        {
        }
        for (item in array.indices)
        when(x){
            1,2-> print(x)
            else -> {
                print(x)
            }
        }
    }
}

data class User(val age: Int)
{
    
}

interface Named {
    val name: String
}

interface Person : Named {
    val firstName: String
    val lastName: String

    override val name: String get() = "$firstName $lastName"
}

data class Employee(
        // 不必实现“name”
        override val firstName: String,
        override val lastName: String
) : Person