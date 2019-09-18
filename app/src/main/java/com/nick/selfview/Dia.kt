package com.nick.selfview

import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import com.nick.base.R

/**
 * @author NICK
 */
class Dia : DialogFragment() {

	override fun onCreateView(
		inflater: LayoutInflater,
		container: ViewGroup?,
		savedInstanceState: Bundle?
	): View? {
		return inflater.inflate(R.layout.dia, container)
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		view.findViewById<TextView>(R.id.tv).setOnClickListener {
			Toast.makeText(context, "111111111111", Toast.LENGTH_SHORT).show()
		}
		super.onViewCreated(view, savedInstanceState)
	}

}
