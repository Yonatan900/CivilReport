package com.example.civilreport.ui

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.civilreport.data.models.ItemReport
import com.example.civilreport.databinding.ReportPreviewFragmentBinding
import com.example.civilreport.util.Loading
import com.example.civilreport.util.Success
import com.example.civilreport.util.Error
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@AndroidEntryPoint
class ReportPreviewFragment :Fragment() {

    private var _binding: ReportPreviewFragmentBinding? = null
    private val binding get() = _binding!!
    private val vm : ReportsViewModel by viewModels()
    private val args by navArgs<ReportPreviewFragmentArgs>()
    lateinit var currentReport: ItemReport

    //Permission and file creation launchers
    private val requestStorageLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            createPdfLauncher.launch(generateFileName())
        } else {
            Toast.makeText(requireContext(), "Permission denied", Toast.LENGTH_SHORT).show()
        }
    }
    private val createPdfLauncher = registerForActivityResult(
        ActivityResultContracts.CreateDocument("application/pdf")
    ) { uri ->
        uri?.let {
            vm.savePdfToStorage(it, currentReport)
        } ?: run {
            Toast.makeText(requireContext(), "PDF creation failed", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = ReportPreviewFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // btn should only be clickable after the report was loaded
        binding.btnExport.isEnabled = false
        binding.btnDelete.isEnabled = false
        // Set up the UI with the report data
        val reportId = args.reportId

        vm.getReportById(reportId).observe(viewLifecycleOwner) {
            when (it.status) {
                is Loading -> {
//                    binding.contentGroup.visibility = View.GONE
//                    binding.progressBar.visibility  = View.VISIBLE
                    binding.btnExport.isEnabled     = false
                }
                is Success -> {
                    val report = it.status.data!!
                    currentReport = report

//                    binding.progressBar.visibility  = View.GONE
//                    binding.contentGroup.visibility = View.VISIBLE
                    binding.btnDelete.isEnabled   = true
                    binding.btnExport.isEnabled     = true

                    binding.tvReportTitle.text    = report.title
                    binding.tvCostumerName.text   = report.costumerName
                    binding.tvReportDate.text     = SimpleDateFormat(
                        "dd/MM/yyyy", Locale.getDefault()
                    ).format(Date(report.timestamp))
                    binding.tvReportLocation.text = report.location

                    // Set up the RecyclerView with the report entries
                    val adapter = PreviewEntryAdapter()
                    binding.rvPreviewEntries.layoutManager = LinearLayoutManager(requireContext())
                    binding.rvPreviewEntries.adapter = adapter
                    adapter.submitList(report.entries)
                }
                is Error -> {
                    Toast.makeText(requireContext(), it.status.message, Toast.LENGTH_SHORT).show()
                    if (!::currentReport.isInitialized)
                        findNavController().popBackStack()
                }
            }
        }
        binding.btnDelete.setOnClickListener {
            vm.deleteReport(reportId)
            findNavController().popBackStack()
        }
        binding.btnExport.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                createPdfLauncher.launch(generateFileName())
                return@setOnClickListener
            }

            if (
                ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                createPdfLauncher.launch(generateFileName())
            } else {
                requestStorageLauncher.launch(
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
            }
        }


    }

    private fun generateFileName(): String {
        val sdf = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault())
        return "report_${sdf.format(Date())}.pdf"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
