package com.sag0ld.barcodegenerator.ui

import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import com.sag0ld.barcodegenerator.R
import com.sag0ld.barcodegenerator.ui.fragment.GenerateBarcodeFragment
import com.sag0ld.barcodegenerator.ui.fragment.HistoryFragment
import kotlinx.android.synthetic.main.activity_main2.*
import kotlinx.android.synthetic.main.app_bar_main2.*

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private val generateFragment = GenerateBarcodeFragment()
    private val historyFragment = HistoryFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)
        setSupportActionBar(toolbar)

        val toggle = ActionBarDrawerToggle(
                this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        nav_view.setNavigationItemSelectedListener(this)
        nav_view.setCheckedItem(R.id.nav_generate_barcode)

        val transaction = supportFragmentManager.beginTransaction()
        transaction.add(fragment_holder.id, generateFragment, GenerateBarcodeFragment.TAG)
        transaction.commit()
    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        val transaction = supportFragmentManager.beginTransaction()
        when (item.itemId) {
            R.id.nav_history -> {
                if (supportFragmentManager.findFragmentByTag(HistoryFragment.TAG) == null) {
                    transaction.replace(fragment_holder.id, historyFragment, HistoryFragment.TAG)
                    transaction.addToBackStack(HistoryFragment.TAG)

                } else if (supportFragmentManager.findFragmentByTag(HistoryFragment.TAG).isRemoving) {
                    transaction.replace(fragment_holder.id, historyFragment, HistoryFragment.TAG)
                    transaction.addToBackStack(HistoryFragment.TAG)
                }
            }
            R.id.nav_generate_barcode -> {
                if (supportFragmentManager.findFragmentByTag(GenerateBarcodeFragment.TAG).isRemoving) {
                    transaction.replace(fragment_holder.id, generateFragment, GenerateBarcodeFragment.TAG)
                }
            }
        }
        transaction.commit()

        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }

    private fun getContentFromScannedBarcode() {

    }
}