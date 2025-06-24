package com.example.civilreport.ui.addReport

import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.PopupMenu
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.civilreport.R
import com.example.civilreport.data.models.CardSize
import com.example.civilreport.data.models.ItemReportCard
import com.example.civilreport.databinding.NewReportFragmentBinding
import com.example.civilreport.util.Loading
import com.example.civilreport.util.Success
import com.example.civilreport.util.Error
import com.example.civilreport.util.installKeyboardAutoHide
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NewReportFragment : Fragment() {
    private var _binding: NewReportFragmentBinding? = null
    private val binding get() = _binding!!

    private val vm: NewReportViewModel by viewModels()

    private lateinit var adapter: NewReportAdapter

    private lateinit var locationAdapter: ArrayAdapter<String>

    private val pickImageLauncher = registerForActivityResult(
            ActivityResultContracts.OpenDocument()
        ) { uri ->
        uri?.let { requireContext().contentResolver.takePersistableUriPermission(
            it,
            Intent.FLAG_GRANT_READ_URI_PERMISSION
        )
            showDescriptionDialogAndAddItem(it) }
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = NewReportFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // hides the key board when clicking outside of text box (and for not going crazy)
        view.installKeyboardAutoHide()

        // Set up the RecyclerView with an adapter
        adapter = NewReportAdapter(
            onCardClick = ::showSizeMenu
        )
        binding.rvAttachments.layoutManager = LinearLayoutManager(requireContext())
        binding.rvAttachments.adapter = adapter

        ItemTouchHelper(dragCallback).attachToRecyclerView(binding.rvAttachments)

        // Observe the list of report cards (saving entries)
        vm.cards.observe(viewLifecycleOwner) { list ->
            adapter.submitList(list.toList())
        }

        // Observe the suggestions for location autocomplete
        locationAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_dropdown_item_1line,
            mutableListOf<String>()
        )
        binding.etLocation.setAdapter(locationAdapter)
        binding.etLocation.addTextChangedListener { editable ->
            vm.fetchLocationSuggestions(editable.toString())
        }

        // location suggestions
        vm.suggestions.observe(viewLifecycleOwner) { res ->
            when (res.status) {
                is Loading -> {
                    binding.progressLocation.visibility = View.VISIBLE
                    binding.etLocation.dismissDropDown()
                }
                is Success -> {
                    binding.progressLocation.visibility = View.GONE
                    val names = (res.status.data ?: emptyList()).map { it.display_name }
                    locationAdapter.clear()
                    locationAdapter.addAll(names)
                    if (names.isNotEmpty()) binding.etLocation.showDropDown()
                }
                is Error -> {
                    binding.progressLocation.visibility = View.GONE
                    Toast.makeText(
                        requireContext(),
                        "Location lookup failed: ${res.status.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

        // Set up the button to pick an image
        binding.btnAddImage.setOnClickListener {
            pickImageLauncher.launch(arrayOf("image/*"))
        }

        // Set up the cancel button to navigate back
        binding.btnCancelReport.setOnClickListener {
            findNavController().popBackStack()
        }

        vm.saveStatus.observe(viewLifecycleOwner) { res ->
            when (res.status) {
                is Loading ->{
//                    binding.progressBar.visibility = View.VISIBLE
                }

                is Success -> {
//                    binding.progressBar.visibility = View.GONE
                    findNavController().popBackStack()   // only NOW
                }
                is Error -> {
//                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(requireContext(),
                        "Save failed: ${res.status.message}",
                        Toast.LENGTH_SHORT).show()
                }
            }
        }

        binding.btnSaveReport.setOnClickListener {
            vm.saveReport(
                binding.etSubject.text.toString(),
                binding.etLocation.text.toString(),
                binding.etForWhom.text.toString()
            )
        }



    }

    private fun showDescriptionDialogAndAddItem(imageUri: Uri) {
        // Show a dialog to get the description from the user
        val editText = EditText(requireContext()).apply {
            hint = "Enter description"
            setSingleLine()
        }

        AlertDialog.Builder(requireContext())
            .setTitle("Image Description")
            .setView(editText)
            .setPositiveButton("Add") { dialog, _ ->
                val description = editText.text.toString().trim()
                if (description.isNotEmpty()) {
                     vm.addEntry(ItemReportCard(imageUri.toString(), description))
                }
                dialog.dismiss()


            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun showSizeMenu(view: View, position: Int) {
        PopupMenu(view.context, view).apply {
            inflate(R.menu.popup_card_size)
            setOnMenuItemClickListener {
                val newSize = when (it.itemId) {
                    R.id.action_small     -> CardSize.SMALL
                    R.id.action_medium    -> CardSize.MEDIUM
                    R.id.action_full_page -> CardSize.FULL
                    else                  -> return@setOnMenuItemClickListener false
                }
                vm.changeSize(position, newSize)
                true
            }
        }.show()
    }


// arrange the items in the RecyclerView by dragging
    private val dragCallback = object : ItemTouchHelper.SimpleCallback(
        ItemTouchHelper.UP or ItemTouchHelper.DOWN, 0
    ) {
        override fun onMove(
            rv: RecyclerView, vh: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder
        ): Boolean {
            val from = vh.bindingAdapterPosition
            val to   = target.bindingAdapterPosition
            vm.moveCard(from, to)
            adapter.onItemMove(from, to)
            return true
        }
        override fun onSwiped(vh: RecyclerView.ViewHolder, dir: Int) = Unit
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

