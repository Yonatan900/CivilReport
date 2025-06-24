package com.example.civilreport.ui

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import com.example.civilreport.data.repository.ReportRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.civilreport.data.models.ItemReport
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ReportsViewModel @Inject constructor(
    private val reportsRepository: ReportRepository,
) : ViewModel() {

    val allReports = reportsRepository.getAllReports()

    private val _pdfResult = MutableLiveData<Boolean>()

    fun getReportById(id: String) = reportsRepository.getReportById(id)

    fun deleteReport(id: String) = viewModelScope.launch {
            reportsRepository.deleteReport(id)
        }


    fun savePdfToStorage(uri : Uri, report : ItemReport) {
        viewModelScope.launch {
            val result = reportsRepository.exportReportPdf(uri, report)
            _pdfResult.postValue(result)
        }
    }
}