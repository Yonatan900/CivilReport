//package com.example.civilreport.data.remote_db
//
//
//import android.util.Log
//import com.example.civilreport.data.models.ImageTextEntry
//import com.example.civilreport.data.models.ItemReport
//import com.example.civilreport.util.Resource
//import com.google.firebase.firestore.DocumentSnapshot
//import com.google.firebase.firestore.FirebaseFirestore
//import kotlinx.coroutines.tasks.await
//import javax.inject.Inject
//class RemoteDataSource @Inject constructor(
//    private val firestore: FirebaseFirestore
//) {
//
//    suspend fun fetchAll(): Resource<List<ItemReport>> = try {
//        val snap = firestore.collection("reports")
//            .get()
//            .await()
//
//        val list = snap.documents.map { doc ->
//            toItemReport(doc)
//        }
//
//        Resource.success(list)
//    } catch (e: Exception) {
//        Resource.error(e.message ?: "Unknown error")
//    }
//
//    suspend fun fetchById(reportId: String): Resource<ItemReport> {
//        return try {
//            val docSnap = firestore.collection("reports")
//                .document(reportId)
//                .get()
//                .await()
//
//            if (!docSnap.exists()) {
//                Resource.error("Report not found")
//            } else {
//                Resource.success(toItemReport(docSnap))
//            }
//        } catch (e: Exception) {
//            Resource.error(e.message ?: "Unknown error")
//        }
//    }
//
//    //map a Firestore DocumentSnapshot into Room entity --
//    private fun toItemReport(doc: DocumentSnapshot): ItemReport {
//        val title        = doc.getString("title")          ?: ""
//        val costumerName = doc.getString("costumer_name")  ?: ""
//        val location     = doc.getString("location")       ?: ""
//        val mainImageUri = doc.getString("main_image_uri") ?: ""
//        val timestamp    = doc.getLong("timestamp")        ?: 0L
//
//        val rawEntries = doc.get("image_text_entries") as? List<Map<String, Any>>
//        val entries = rawEntries
//            ?.map { m ->
//                ImageTextEntry(
//                    imageUri = m["imageUri"] as? String ?: "",
//                    text     = m["text"]     as? String ?: ""
//                )
//            }
//            ?: emptyList()
//
//        return ItemReport(
//            title        = title,
//            costumerName = costumerName,
//            location     = location,
//            mainImageUri = mainImageUri,
//            timestamp    = timestamp,
//            entries      = entries
//        ).apply {
//            id = doc.getLong("id")?.toInt() ?: 0
//        }
//    }
//
//    suspend fun upsert(report: ItemReport) {
//        println("upserting")
//        try {
//            firestore.collection("reports")
//                .document(report.id.toString())
//                .set(report.toMap())
//                .await()
//            Log.d("Remote", "write OK for id=${report.id}")
//        } catch (e: Exception) {
//            Log.e("Remote", "write FAILED → ${e.message}")
//        }
//    }
//
//    suspend fun delete(reportId: Int) {
//        firestore.collection("reports")
//            .document(reportId.toString())
//            .delete()
//            .await()
//    }
//
//    //map ItemReport to firebase document
//    fun ItemReport.toMap() = mapOf(
//        "id"              to id,
//        "title"           to title,
//        "costumer_name"   to costumerName,
//        "location"        to location,
//        "main_image_uri"  to mainImageUri,
//        "timestamp"       to timestamp,
//        "image_text_entries" to entries.map {
//            mapOf("imageUri" to it.imageUri,
//                "text"     to it.text)
//        }
//    )
//}
//
