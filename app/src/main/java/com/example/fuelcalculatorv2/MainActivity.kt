package com.example.fuelcalculatorv2

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.fuelcalculatorv2.databinding.ActivityMainBinding
import com.google.android.material.tabs.TabLayout
import kotlin.math.round

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupTabLayout()
        setupFuelCalculator()
        setupMazutCalculator()

        // Початково показуємо Fuel Calculator
        showFuelCalculator()
    }

    private fun setupTabLayout() {
        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                when (tab?.position) {
                    0 -> showFuelCalculator()
                    1 -> showMazutCalculator()
                }
            }
            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })
    }

    private fun showFuelCalculator() {
        binding.fuelCalculatorLayout.visibility = View.VISIBLE
        binding.mazutCalculatorLayout.visibility = View.GONE
        binding.resultsText.text = ""
    }

    private fun showMazutCalculator() {
        binding.fuelCalculatorLayout.visibility = View.GONE
        binding.mazutCalculatorLayout.visibility = View.VISIBLE
        binding.resultsText.text = ""
    }

    private fun setupFuelCalculator() {
        binding.btnCalculateFuel.setOnClickListener {
            calculateFuel()
        }
    }

    private fun setupMazutCalculator() {
        binding.btnCalculateMazut.setOnClickListener {
            calculateMazut()
        }
    }

    private fun calculateFuel() {
        try {
            val hp = binding.etHp.text.toString().toDoubleOrNull() ?: 0.0
            val cp = binding.etCp.text.toString().toDoubleOrNull() ?: 0.0
            val sp = binding.etSp.text.toString().toDoubleOrNull() ?: 0.0
            val np = binding.etNp.text.toString().toDoubleOrNull() ?: 0.0
            val op = binding.etOp.text.toString().toDoubleOrNull() ?: 0.0
            val wp = binding.etWp.text.toString().toDoubleOrNull() ?: 0.0
            val ap = binding.etAp.text.toString().toDoubleOrNull() ?: 0.0

            // Коефіцієнти перерахунку
            val krs = 100.0 / (100.0 - wp)
            val krg = 100.0 / (100.0 - wp - ap)

            // Склад сухої маси
            val hs = hp * krs
            val cs = cp * krs
            val ss = sp * krs
            val ns = np * krs
            val os = op * krs
            val aas = ap * krs
            val sumComponents1 = hs + cs + ss + ns + os + aas

            // Склад горючої маси
            val hg = hp * krg
            val cg = cp * krg
            val sg = sp * krg
            val ng = np * krg
            val og = op * krg
            val sumComponents2 = hg + cg + sg + ng + og

            // Нижча теплота згоряння
            val qrh = (339 * cp + 1030 * hp - 108.8 * (op - sp) - 25 * wp) / 1000
            val qch = (qrh + 0.025 * wp) * 100 / (100 - wp)
            val qgh = (qrh + 0.025 * wp) * 100 / (100 - wp - ap)

            // Формування результатів
            val results = buildString {
                appendLine("РЕЗУЛЬТАТИ:")
                appendLine()
                appendLine("1.1. Коефіцієнт переходу від робочої до сухої маси (КРС):")
                appendLine("${round2(krs)}")
                appendLine()
                appendLine("1.2. Коефіцієнт переходу від робочої до горючої маси (КРГ):")
                appendLine("${round2(krg)}")
                appendLine()
                appendLine("1.3. Склад сухої маси:")
                appendLine("  H^C: ${round2(hs)}%")
                appendLine("  C^C: ${round2(cs)}%")
                appendLine("  S^C: ${round2(ss)}%")
                appendLine("  N^C: ${round2(ns)}%")
                appendLine("  O^C: ${round2(os)}%")
                appendLine("  A^C: ${round2(aas)}%")
                appendLine("  Сума: ${round2(sumComponents1)}%")
                appendLine()
                appendLine("1.4. Склад горючої маси:")
                appendLine("  H^Г: ${round2(hg)}%")
                appendLine("  C^Г: ${round2(cg)}%")
                appendLine("  S^Г: ${round2(sg)}%")
                appendLine("  N^Г: ${round2(ng)}%")
                appendLine("  O^Г: ${round2(og)}%")
                appendLine("  Сума: ${round2(sumComponents2)}%")
                appendLine()
                appendLine("1.5. Нижча теплота згоряння для робочої маси (QРН):")
                appendLine("${round2(qrh)} МДж/кг")
                appendLine()
                appendLine("1.6. Нижча теплота згоряння для сухої маси (QСН):")
                appendLine("${round2(qch)} МДж/кг")
                appendLine()
                appendLine("1.7. Нижча теплота згоряння для горючої маси (QГН):")
                appendLine("${round2(qgh)} МДж/кг")
            }

            binding.resultsText.text = results

        } catch (e: Exception) {
            binding.resultsText.text = "Помилка: ${e.message}"
        }
    }

    private fun calculateMazut() {
        try {
            val carbon = binding.etCarbon.text.toString().toDoubleOrNull() ?: 0.0
            val hydrogen = binding.etHydrogen.text.toString().toDoubleOrNull() ?: 0.0
            val oxygen = binding.etOxygen.text.toString().toDoubleOrNull() ?: 0.0
            val sulfur = binding.etSulfur.text.toString().toDoubleOrNull() ?: 0.0
            val heatingValue = binding.etHeatingValue.text.toString().toDoubleOrNull() ?: 0.0
            val humidity = binding.etHumidity.text.toString().toDoubleOrNull() ?: 0.0
            val ash = binding.etAsh.text.toString().toDoubleOrNull() ?: 0.0
            val vanadium = binding.etVanadium.text.toString().toDoubleOrNull() ?: 0.0

            // Розрахунки
            val dryMass = 100.0 - humidity - ash
            val carbonWork = carbon * dryMass / 100.0
            val hydrogenWork = hydrogen * dryMass / 100.0
            val oxygenWork = oxygen * dryMass / 100.0
            val sulfurWork = sulfur * dryMass / 100.0
            val vanadiumWork = vanadium * dryMass / 100.0
            val oxygenCorrection = 0.025 * humidity

            val lowerHeatingValue = (heatingValue * dryMass / 100.0) - oxygenCorrection
            val ar = ash / dryMass * 100.0

            // Формування результатів
            val results = buildString {
                appendLine("РЕЗУЛЬТАТИ РОЗРАХУНКУ МАЗУТУ:")
                appendLine()
                appendLine("Склад робочої маси:")
                appendLine()
                appendLine("Вуглець (C^R): ${round2(carbonWork)}%")
                appendLine()
                appendLine("Водень (H^R): ${round2(hydrogenWork)}%")
                appendLine()
                appendLine("Кисень (O^R): ${round2(oxygenWork)}%")
                appendLine()
                appendLine("Сірка (S^R): ${round2(sulfurWork)}%")
                appendLine()
                appendLine("Відношення золи (A^R): ${round2(ar)}%")
                appendLine()
                appendLine("Ванадій (V^R): ${round2(vanadiumWork)} мг/кг")
                appendLine()
                appendLine("Нижча теплота згоряння (Q_R): ${round2(lowerHeatingValue)} МДж/кг")
            }

            binding.resultsText.text = results

        } catch (e: Exception) {
            binding.resultsText.text = "Помилка: ${e.message}"
        }
    }

    private fun round2(value: Double): String {
        return "%.2f".format(value)
    }
}