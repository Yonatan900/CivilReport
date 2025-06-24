package com.example.civilreport.data.repository
import android.net.Uri
import com.example.civilreport.data.models.ImageTextEntry
import com.example.civilreport.data.models.ItemReport
import com.example.civilreport.data.models.LocationIqInfo
import com.example.civilreport.data.remote_db.LocationRemoteDataSource
import com.example.civilreport.util.Resource
import com.google.firebase.firestore.FirebaseFirestore
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.asLiveData
import com.example.civilreport.data.models.CardSize
import com.example.civilreport.util.ReportToPdfExporter
import com.google.firebase.firestore.DocumentSnapshot
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Singleton
import kotlin.collections.orEmpty


@Singleton
class ReportRepository @Inject constructor(
    private val locationRemoteDataSource : LocationRemoteDataSource,
    private val firestore: FirebaseFirestore,
    private val authRepo: AuthRepository,
    private val pdfExporter: ReportToPdfExporter
){
    private fun currUserId() : String? = authRepo.getCurrentUserId()

    fun getAllReports(): LiveData<Resource<List<ItemReport>>> = callbackFlow {
        val userId = currUserId()
        if (userId.isNullOrBlank()) {
            trySend(Resource.error("Not Signed In"))
            close()
            return@callbackFlow
        }
        trySend(Resource.loading())

        val registration = firestore
            .collection("users")
            .document(userId)
            .collection("reports")
            .orderBy("timestamp")
            .addSnapshotListener { snaps, e ->
                if (e != null) trySend(Resource.error(e.message ?: ""))
                else {
                    val list = snaps!!.map(::fromSnapshot)
                    trySend(Resource.success(list))
                }
            }

        awaitClose { registration.remove() }
    }.flowOn(Dispatchers.IO).asLiveData()


    fun getReportById(id: String): LiveData<Resource<ItemReport>> = liveData(Dispatchers.IO) {
        emit(Resource.loading())
        val userId = currUserId()
        if (userId== null) {
            emit(Resource.error("Not signed in"))
            return@liveData
        }
        try {
            val doc = firestore
                .collection("users")
                .document(userId)
                .collection("reports")
                .document(id)
                .get()
                .await()
            if (doc.exists()) {
                emit(Resource.success(fromSnapshot(doc)))
            } else {
                emit(Resource.error("Report $id not found"))
            }
        } catch (e: Exception) {
            emit(Resource.error("Failed to fetch report: ${e.message}"))
        }
    }

    suspend fun deleteReport(id: String): Resource<Unit> {
        val userId = currUserId()
            ?: return Resource.error("Not signed in")
        return try {
            firestore
                .collection("users")
                .document(userId)
                .collection("reports")
                .document(id)
                .delete()
                .await()
            Resource.success(Unit)
        } catch (e: Exception) {
            Resource.error("Delete failed: ${e.message}")
        }
    }

    suspend fun insertReport(report: ItemReport) : Resource<ItemReport> {
        val userId = currUserId()
            ?: return Resource.error("Not signed in")
        val col = firestore
            .collection("users")
            .document(userId)
            .collection("reports")
        // decide new vs existing ID without mutating the original model
        val (finalReport, docRef) = if (report.id.isBlank()) {
            val newRef = col.document()
            report.copy(id = newRef.id) to newRef
        } else {
            report to col.document(report.id)
        }

        val data = mapOf(
            "title"               to finalReport.title,
            "costumer_name"       to finalReport.costumerName,
            "location"            to finalReport.location,
            "main_image_uri"      to finalReport.mainImageUri,
            "timestamp"           to finalReport.timestamp,
            "image_text_entries"  to finalReport.entries.map { e ->
                mapOf(
                    "image_uri" to e.imageUri,
                    "text"      to e.imageDesc,
                    "image_size" to e.imageSize.name
                )
            }
        )
        return try {
            docRef.set(data).await()
            Resource.success(finalReport)
        } catch (e: Exception) {
            Resource.error("Save failed: ${e.message}")
        }
    }

    suspend fun exportReportPdf(uri: Uri, report: ItemReport): Boolean{
        return pdfExporter.exportReportPdf(uri, report)
    }

    suspend fun autocompleteLocation( query: String): Resource<List<LocationIqInfo>> =
        locationRemoteDataSource.autoComplete(query)

    private fun fromSnapshot(doc: DocumentSnapshot) : ItemReport {
        val data = doc.data.orEmpty()
        val entries = (data["image_text_entries"] as? List<Map<String, Any>> ?: emptyList())
            .map { entryData ->
                val sizeEnum = CardSize.entries
                    .firstOrNull { it.name == entryData["image_size"] }
                    ?: CardSize.MEDIUM
                ImageTextEntry(
                    imageUri = entryData["image_uri"] as? String ?: "",
                    imageDesc = entryData["text"] as? String ?: "",
                    imageSize = sizeEnum
                )
            }

        return ItemReport(
            id           = doc.id,
            title        = data["title"]          as? String ?: "",
            costumerName = data["costumer_name"]  as? String ?: "",
            location     = data["location"]       as? String ?: "",
            mainImageUri = data["main_image_uri"] as? String ?: "",
            timestamp    = data["timestamp"]      as? Long   ?: 0L,
            entries      = entries
        )
    }
}