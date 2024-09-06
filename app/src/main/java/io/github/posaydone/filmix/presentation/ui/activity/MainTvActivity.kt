package io.github.posaydone.filmix.presentation.ui.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import io.github.posaydone.filmix.R
import io.github.posaydone.filmix.ui.fragment.ShowsRowsFragment

class MainTvActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_tv)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.rows_fragment, ShowsRowsFragment())
                .commit()
        }
    }
}