package com.example.civilreport.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.civilreport.R
import com.example.civilreport.databinding.ReportsFragmentBinding
import com.example.civilreport.util.Loading
import com.example.civilreport.util.Error
import com.example.civilreport.util.Success
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ReportsFragment : Fragment(), ReportAdapter.ReportItemListener {
    private var _binding: ReportsFragmentBinding? = null
    private val binding get() = _binding!!
    val viewModel: ReportsViewModel by viewModels()
    private lateinit var adapter: ReportAdapter
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = ReportsFragmentBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = ReportAdapter(this)
        binding.reportsRv.layoutManager = LinearLayoutManager(requireContext())
        binding.reportsRv.adapter = adapter



        viewModel.allReports.observe(viewLifecycleOwner) {

            when(it.status) {

                is Loading -> {
//                    binding.progressBar.visibility = View.VISIBLE
                }
                is Success -> {
//                    binding.progressBar.visibility = View.GONE
                    adapter.setReports(it.status.data.orEmpty())
                }
                is Error-> {
                    Toast.makeText(requireContext(),it.status.message,Toast.LENGTH_SHORT).show()
                }
            }
        }

        binding.fabNewReport.setOnClickListener {
            findNavController().navigate(
                R.id.action_reportsFragment_to_newReportFragment
            )
        }



    }
    //Button click listener for the "Preview Report" button
    override fun onReportClick(reportId: String) {
        findNavController().navigate(
            ReportsFragmentDirections.actionReportsFragmentToReportPreviewFragment(reportId)
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}