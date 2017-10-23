package com.shuhart.moneyedittext.sample

import android.content.res.Resources
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import com.shuhart.moneyedittext.MoneyEditText
import java.util.*

class MainActivity : AppCompatActivity() {
    private val localePtPt = Locale("pt", "pt")
    private val localePtBr = Locale("pt", "br")
    private val localeRu = Locale("ru", "ru")

    private val locales = mapOf<Locale, Currency>(
            Locale.US to Currency.getInstance(Locale.US),
            Locale.FRANCE to Currency.getInstance(Locale.FRANCE),
            Locale.CHINA to Currency.getInstance(Locale.CHINA),
            Locale.JAPAN to Currency.getInstance(Locale.JAPAN),
            localePtPt to Currency.getInstance(localePtPt),
            localePtBr to Currency.getInstance(localePtBr),
            localeRu to Currency.getInstance(localeRu))

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val spinner = findViewById<Spinner>(R.id.spinner)
        val moneyEditText = findViewById<MoneyEditText>(R.id.edittext)
        setupSpinner(spinner, moneyEditText)
        moneyEditText.setAmount(amount = 50000.0000, silently = true)
    }

    private fun setupSpinner(spinner: Spinner?, moneyEditText: MoneyEditText) {
        val spinnerAdapter = object : ArrayAdapter<Currency>(this@MainActivity, android.R.layout.simple_dropdown_item_1line,
                locales.values.toTypedArray()) {
            override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup?): View {
                val view = super.getDropDownView(position, convertView, parent)
                val locale = locales.keys.toList()[position]
                view.tag = locale
                (view as TextView).text = "${locale.displayCountry}, ${locales[locale].toString()}"
                return view
            }

            override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
                val view = super.getView(position, convertView, parent)
                val locale = locales.keys.toList()[position]
                view.tag = locale
                (view as TextView).text = "${locale.displayCountry}, ${locales[locale].toString()}"
                return view
            }
        }
        spinner?.apply {
            spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) {}

                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    if (view == null) return
                    val locale = view.tag as Locale
                    if (locale == Locale.getDefault()) return
                    forceLocale(locale)
                    adapter = spinnerAdapter
                    setSelection(position)
                    moneyEditText.currency = locales[locale].toString()
                }
            }
            adapter = spinnerAdapter
        }
    }

    private fun forceLocale(locale: Locale) {
        val conf = resources.configuration
        conf.locale = locale
        resources.updateConfiguration(conf, resources.displayMetrics)

        val systemConf = Resources.getSystem().configuration
        systemConf.locale = locale
        Resources.getSystem().updateConfiguration(systemConf, Resources.getSystem().displayMetrics)

        Locale.setDefault(locale)
    }
}
