// NewReportViewModel.kt
package com.example.civilreport.ui.addReport

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.civilreport.data.models.CardSize
import com.example.civilreport.data.models.ImageTextEntry
import com.example.civilreport.data.models.ItemReport
import com.example.civilreport.data.models.ItemReportCard
import com.example.civilreport.data.models.LocationIqInfo
import com.example.civilreport.data.repository.ReportRepository
import com.example.civilreport.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NewReportViewModel @Inject constructor(
    private val repo: ReportRepository
) : ViewModel() {

    // LiveData to hold the list of entries for the report
    private val _cards = MutableLiveData<MutableList<ItemReportCard>>(mutableListOf())
    val cards: LiveData<MutableList<ItemReportCard>> = _cards

    // location autocomplete
    private val _suggestions = MutableLiveData<Resource<List<LocationIqInfo>>>()
    val suggestions: LiveData<Resource<List<LocationIqInfo>>> = _suggestions
    private var searchJob: Job? = null

    private val _saveStatus = MutableLiveData<Resource<Unit>>()
    val saveStatus: LiveData<Resource<Unit>> = _saveStatus

    fun addEntry(entry: ItemReportCard) {
        _cards.value?.add(entry)
        _cards.value = _cards.value
    }

    fun changeSize(position: Int, newSize: CardSize) {
        _cards.value?.let { list ->
            list[position] = list[position].copy(size = newSize)
            _cards.value = list
        }
    }

    fun moveCard(from: Int, to: Int) {
        _cards.value?.let { list ->
            val moved = list.removeAt(from)
            list.add(to, moved)
            _cards.value = list
        }
    }


    fun saveReport(title: String, location: String, forWhom: String) {
        _saveStatus.postValue(Resource.loading())
        val now = System.currentTimeMillis()

        // map the ui model to the data model
        val entries = _cards.value!!.map {
            ImageTextEntry(
                imageUri    = it.uri.toString(),
                imageDesc  = it.caption,
                imageSize   = it.size,
            )
        }.toMutableList()

        val report = ItemReport(
            title         = title,
            mainImageUri  = entries.firstOrNull()?.imageUri ?: "",
            location      = location,
            costumerName  = forWhom,
            timestamp     = now,
            entries       = entries
        )

        viewModelScope.launch {
            try {
                repo.insertReport(report)
                _cards.postValue(mutableListOf())
                _saveStatus.postValue(Resource.success(Unit))
            } catch (e: Exception) {
                _saveStatus.postValue(Resource.error(e.message ?: "Save failed"))
            }
        }
    }

    fun fetchLocationSuggestions(query: String) {
        searchJob?.cancel()
        if (query.isBlank()) {
            _suggestions.value = Resource.success(emptyList())
            return
        }
        searchJob = viewModelScope.launch {
            delay(300)
            _suggestions.value = Resource.loading()
            val result = repo.autocompleteLocation(query)
            _suggestions.value = result
        }
    }

}
