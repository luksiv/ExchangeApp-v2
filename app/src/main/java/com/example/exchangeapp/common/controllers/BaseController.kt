package com.example.exchangeapp.common.controllers

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bluelinelabs.conductor.Controller
import timber.log.Timber


abstract class BaseController : Controller() {
    abstract fun inject()

    abstract fun onCreateControllerView(inflater: LayoutInflater, container: ViewGroup): View

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup): View {
        addLogs()

        inject()

        return onCreateControllerView(inflater, container)
    }

    override fun onAttach(view: View) {
        super.onAttach(view)
        addLogs()
    }

    override fun onDetach(view: View) {
        super.onDetach(view)
        addLogs()
    }

    override fun onDestroyView(view: View) {
        super.onDestroyView(view)
        addLogs()
    }

    override fun onDestroy() {
        super.onDestroy()
        addLogs()
    }

    override fun onActivityPaused(activity: Activity) {
        super.onActivityPaused(activity)
        addLogs()
    }

    override fun onActivityResumed(activity: Activity) {
        super.onActivityResumed(activity)
        addLogs()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        addLogs()
    }

    override fun onActivityStarted(activity: Activity) {
        super.onActivityStarted(activity)
        addLogs()
    }

    override fun onActivityStopped(activity: Activity) {
        super.onActivityStopped(activity)
        addLogs()
    }

    private fun addLogs() {
        val methodName = Thread.currentThread().stackTrace[3].methodName
        Timber.tag(this.javaClass.simpleName)
        Timber.v(methodName)
    }
}
