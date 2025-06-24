package com.example.civilreport.util

import android.content.Context
import android.net.Uri
import com.example.civilreport.data.models.ItemReport
import com.tom_roush.pdfbox.pdmodel.PDDocument
import com.tom_roush.pdfbox.pdmodel.PDPageContentStream
import com.tom_roush.pdfbox.pdmodel.PDPageContentStream.AppendMode
import com.tom_roush.pdfbox.pdmodel.font.PDType1Font
import com.tom_roush.pdfbox.pdmodel.graphics.image.PDImageXObject
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import androidx.core.net.toUri

class ReportToPdfExporter @Inject constructor(
    @ApplicationContext private val ctx: Context
) {
    suspend fun exportReportPdf(uri: Uri, report: ItemReport): Boolean =
        withContext(Dispatchers.IO) {
            try {
                ctx.assets.open("report_template.pdf").use { templateStream ->
                    PDDocument.load(templateStream).use { document ->
                        // Prepare form for later if needed
                        val form = document.documentCatalog.acroForm!!
                        form.needAppearances = true

                        // The “template” page we’ll clone (zero-based index 2 → page 3)
                        val templatePage = document.getPage(2)

                        report.entries.forEachIndexed { idx, entry ->
                            // Clone & add a fresh page
                            val newPage = document.importPage(templatePage)

                            // Once per page: open a single content stream to draw image + text
                            PDPageContentStream(
                                document,
                                newPage,
                                AppendMode.APPEND,
                                /*compress*/ true,
                                /*resetContext*/ true
                            ).use { cs ->
                                // 1) Load & draw the image centered
                                entry.imageUri.let { uriStr ->
                                    ctx.contentResolver.openInputStream(uriStr.toUri())
                                        ?.use { input ->
                                            val bytes = input.readBytes()
                                            val pdImage = PDImageXObject.createFromByteArray(
                                                document,
                                                bytes,
                                                "img$idx"
                                            )

                                            val pageWidth = newPage.mediaBox.width
                                            val imgWidth  = 200f
                                            val imgHeight = 200f
                                            val imgX = (pageWidth - imgWidth) / 2
                                            val imgY = 450f

                                            cs.drawImage(pdImage, imgX, imgY, imgWidth, imgHeight)
                                        }
                                }

                                // 2) Draw the description text, centered below the image
                                val desc     = entry.imageDesc
                                val font     = PDType1Font.HELVETICA
                                val fontSize = 12f
                                val textWidth = font.getStringWidth(desc) / 1000 * fontSize
                                val pageWidth = newPage.mediaBox.width
                                val textX     = (pageWidth - textWidth) / 2
                                val textY     = 430f

                                cs.beginText()
                                cs.setFont(font, fontSize)
                                cs.newLineAtOffset(textX, textY)
                                cs.showText(desc)
                                cs.endText()
                            }
                        }

                        // If you still need fields elsewhere, regenerate their appearances
                        form.refreshAppearances()

                        // Save final PDF
                        ctx.contentResolver.openOutputStream(uri)?.use { out ->
                            document.save(out)
                        }
                    }
                }
                true
            } catch (e: Exception) {
                e.printStackTrace()
                false
            }
        }
}