package com.example.androidtbc

import android.graphics.Color
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.androidtbc.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        binding.tvAndesMountain.typeface = ResourcesCompat.getFont(this, R.font.inter_semibold)
        binding.tvAndesMountain.text = getString(R.string.andes_mountain)
        binding.tvAndesMountain.setTextColor(Color.WHITE)
        binding.tvAndesMountain.textSize = 24f


        binding.tvSouthAmerica.typeface = ResourcesCompat.getFont(this, R.font.roboto)
        binding.tvSouthAmerica.text = getString(R.string.south_america)
        binding.tvSouthAmerica.setTextColor(Color.parseColor("#CAC8C8"))
        binding.tvSouthAmerica.textSize = 18f


        binding.tvPrice.typeface = ResourcesCompat.getFont(this, R.font.roboto)
        binding.tvPrice.text = getString(R.string.price)
        binding.tvPrice.setTextColor(Color.parseColor("#CAC8C8"))
        binding.tvPrice.textSize = 16f


        binding.tv230.typeface = ResourcesCompat.getFont(this, R.font.roboto_medium)
        binding.tv230.text = getString(R.string._230)
        binding.tv230.setTextColor(Color.parseColor("#CAC8C8"))
        binding.tv230.textSize = 26f


        binding.tvOverview.typeface = ResourcesCompat.getFont(this, R.font.inter_semibold)
        binding.tvOverview.text = getString(R.string.overview)
        binding.tvOverview.setTextColor(Color.parseColor("#1B1B1B"))
        binding.tvOverview.textSize = 22f


        binding.tvDetails.typeface = ResourcesCompat.getFont(this, R.font.inter_semibold)
        binding.tvDetails.text = getString(R.string.details)
        binding.tvDetails.setTextColor(Color.parseColor("#1B1B1BCC"))
        binding.tvDetails.textSize = 16f

        binding.tv8hours.typeface = ResourcesCompat.getFont(this, R.font.roboto_medium)
        binding.tv8hours.text = getString(R.string._8_hours)
        binding.tv8hours.setTextColor(Color.parseColor("#7E7E7E"))
        binding.tv8hours.textSize = 18f


        binding.tvTemperature.typeface = ResourcesCompat.getFont(this, R.font.roboto_medium)
        binding.tvTemperature.text = getString(R.string._16_c)
        binding.tvTemperature.setTextColor(Color.parseColor("#7E7E7E"))
        binding.tvTemperature.textSize = 18f

        binding.tv45.typeface = ResourcesCompat.getFont(this, R.font.roboto_medium)
        binding.tv45.text = "4.5"
        binding.tv45.setTextColor(Color.parseColor("#7E7E7E"))
        binding.tv45.textSize = 18f

        binding.tvDescription.typeface = ResourcesCompat.getFont(this, R.font.roboto_medium)
        binding.tvDescription.text = getString(R.string.description)
        binding.tvDescription.textSize = 18f

        binding.btnBookNow.typeface = ResourcesCompat.getFont(this, R.font.roboto_medium)
        binding.btnBookNow.text = getString(R.string.book_now)
        binding.btnBookNow.setTextColor(Color.parseColor("#FFFFFF"))
        binding.btnBookNow.textSize = 20f
    }
}