package com.akrio.datastoreexample

import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.lifecycleScope
import com.akrio.datastoreexample.databinding.ActivityMainBinding
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding!!

    private val dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

    lateinit var activityContext: Context

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        activityContext = baseContext

        binding.saveButton.setOnClickListener {
            lifecycleScope.launch {
                save(
                    binding.enterKey.text.toString(),
                    binding.enterValue.text.toString()

                )
                binding.enterKey.text.clear()
                binding.enterValue.text.clear()
                Toast.makeText(activityContext, getString(R.string.save_pair), Toast.LENGTH_LONG)
                    .show()

            }
        }

        binding.readButton.setOnClickListener {
            lifecycleScope.launch {
                val result = read(binding.readValue.text.toString())
                binding.readValue.text.clear()
                Toast.makeText(
                    activityContext,
                    getString(R.string.received_value, result),
                    Toast.LENGTH_LONG
                ).show()
            }
        }

    }

    private suspend fun save(key: String, value: String) {
        val dataStoreKey = stringPreferencesKey(key)
        dataStore.edit {
            it[dataStoreKey] = value
        }
    }

    private suspend fun read(key: String): String {
        val dataStoreKey = stringPreferencesKey(key)
        val preferences = dataStore.data.first()
        return preferences[dataStoreKey] ?: " is not found"
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }


}