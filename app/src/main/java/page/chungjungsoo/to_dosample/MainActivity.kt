package page.chungjungsoo.to_dosample

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowInsetsController
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Set content view - loads activity_main.xml
        setContentView(R.layout.activity_main)

        // Set app status bar color : white, force light status bar mode
        window.statusBarColor = ContextCompat.getColor(this, R.color.white)
        window.insetsController!!.setSystemBarsAppearance(WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS,
            WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS)

        

    }
}