package com.nick.base

import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

object Test {

    @kotlin.jvm.Volatile
    private var i = 1

    var dis:Disposable? = null

    @JvmStatic
    fun main(args: Array<String>) {
//        val map = mutableMapOf(0 to "s", 1 to "ss")
//        val list = mutableListOf(11,21,31,41,51)
//        for ((k,v) in map){
//            println("$k $v")
//        }

//        map.filter { (k,v)->k<1 && v != "s" }.forEach { (k,v)->
//            println("$k $v")}
//        list.filter { s ->s < 5 }.takeLast(2).forEach { s-> println() }

//        Thread{
//            while (true) {
//                Thread.sleep(500)
//                i++
//                if (i >= 10) {
//                    dis?.dispose()
//                }
//                println("thread $i")
//            }
//        }.start()

//        dis = Observable.interval(1, TimeUnit.SECONDS).subscribeOn(Schedulers.io()).unsubscribeOn(Schedulers.io()).observeOn(Schedulers.newThread()).subscribe { println(i) }

        Observable.create<Int> { emitter -> emitter.onNext(1) }.subscribeOn(Schedulers.io()).observeOn(Schedulers.io()).subscribe{ t -> println("Sss")}
    }

    open class B {
        var s = "";

        inner class C {
            fun d() {
                H("").a
            }
        }

        fun e() {
            B().C().d()
            F.a()
        }
    }

    class A(var number: Int) {
    }

}

data class H(@JvmField val a: String) {

    @get:JvmName("ss")
    @set:JvmName("s")
   var c: String? = ""
}

object F{
    @JvmStatic
    fun a(){}
}
interface SS {

    fun b()

    fun a(ss:String){

    }
}