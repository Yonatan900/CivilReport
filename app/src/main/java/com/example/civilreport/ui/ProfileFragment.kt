package com.example.civilreport.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.navOptions
import com.example.civilreport.R
import com.example.civilreport.data.repository.PrefsRepo
import com.example.civilreport.databinding.ProfileFragmentBinding
import com.google.android.material.chip.Chip
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.util.Locale
import javax.inject.Inject

@AndroidEntryPoint
class ProfileFragment : Fragment() {
    @Inject lateinit var prefRepo: PrefsRepo
    private var _binding: ProfileFragmentBinding? = null
    private val binding get() = _binding!!
    private val viewModel: AuthViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = ProfileFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnLogout.setOnClickListener {
            viewModel.logout()
        }

        //theme switch
        binding.switchTheme.isChecked =
            AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES

        // 2. handle toggles
        binding.switchTheme.setOnCheckedChangeListener { _, checked ->
            val mode = if (checked)
                AppCompatDelegate.MODE_NIGHT_YES
            else
                AppCompatDelegate.MODE_NIGHT_NO

            // 2-a  persist
            viewLifecycleOwner.lifecycleScope.launch {
                prefRepo.setNightMode(mode)
            }
            // 2-b  apply
            AppCompatDelegate.setDefaultNightMode(mode)
        }

        viewModel.signedOut.observe(viewLifecycleOwner) {
            findNavController().navigate(
                R.id.loginFragment,
                null,
                navOptions {

                    popUpTo(R.id.loginFragment) { inclusive = true }
                }
            )
        }

        // Set up the language selection chips
        //current language
        val current = AppCompatDelegate
            .getApplicationLocales()
            .get(0)
            ?.language
            ?: Locale.getDefault().language

        // check the current language and set the appropriate chip as checked
        val toCheck = if (current == binding.chipHebrew.tag)
            binding.chipHebrew.id else binding.chipEnglish.id

        // language chips
        binding.chipGroupLanguages.check(toCheck)
        with(binding) {
            chipGroupLanguages.setOnCheckedStateChangeListener { _, checkedIds ->
                val chip = chipGroupLanguages.findViewById<Chip>(checkedIds.first())
                val tag  = chip.tag as String
                AppCompatDelegate.setApplicationLocales(
                    LocaleListCompat.forLanguageTags(tag)
                )
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}