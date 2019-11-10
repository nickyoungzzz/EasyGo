package com.nick.lib.network.util

import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

object HttpRequestTagStorage {

	private val reqTagMap = hashMapOf<String, CompositeDisposable>()

	fun addRequestTag(reqTag: String, disposable: Disposable) {
		if (reqTag.isNotEmpty()) {
			if (reqTagMap.containsKey(reqTag)) {
				val compositeDisposable = reqTagMap[reqTag]
				compositeDisposable?.add(disposable)
			} else {
				val compositeDisposable = CompositeDisposable(disposable)
				reqTagMap[reqTag] = compositeDisposable
			}
		}
	}

	fun cancelRequestTag(reqTag: String) {
		val compositeDisposable: CompositeDisposable? = reqTagMap[reqTag]
		compositeDisposable?.clear()
	}
}