package co.winds.smsotp

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main_launcher.*

class MainLauncherActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_launcher)

        btna.setOnClickListener {
            startActivity(Intent(this, MainActivityMain::class.java))
        }
    }
}
