package com.sjaindl.travelcompanion

import android.annotation.SuppressLint
import android.content.Intent.EXTRA_ATTRIBUTION_TAGS
import android.content.Intent.EXTRA_END_TIME
import android.content.Intent.EXTRA_PERMISSION_GROUP_NAME
import android.content.Intent.EXTRA_START_TIME
import android.os.Build
import android.os.Build.VERSION_CODES
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.MyLocation
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sjaindl.travelcompanion.databinding.ActivityDataAccessRationaleBinding
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class DataAccessRationaleActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDataAccessRationaleBinding

    private val tag = "DataAccessRationaleActivity"

    @SuppressLint("BinaryOperationInTimber")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDataAccessRationaleBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (Build.VERSION.SDK_INT >= VERSION_CODES.S) {
            val groupName = intent.getStringExtra(EXTRA_PERMISSION_GROUP_NAME)
            val attributionTags =
                intent.getStringArrayExtra(EXTRA_ATTRIBUTION_TAGS)?.mapNotNull { it }?.joinToString(separator = ",").orEmpty()
            val startTime = intent.getLongExtra(EXTRA_START_TIME, -1)
            val endTime = intent.getLongExtra(EXTRA_END_TIME, -1)

            Timber.tag(tag).d(
                message = "Show location data access rationale for group: $groupName\n" +
                        "attributionTags: $attributionTags\n" +
                        "startTime: ${convertLongToTime(time = startTime)}\n" +
                        "endTime: ${convertLongToTime(time = endTime)}"
            )
        }

        binding.composeView.setContent {
            val scrollState = rememberScrollState()

            Column(
                modifier = Modifier
                    .padding(horizontal = 8.dp)
                    .padding(bottom = 32.dp)
                    .verticalScroll(scrollState),
                verticalArrangement = Arrangement.spacedBy(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = stringResource(id = R.string.dataUsageInfo),
                    modifier = Modifier.padding(top = 16.dp),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                )

                Image(
                    painter = painterResource(id = R.drawable.data_access),
                    contentDescription = null,
                    modifier = Modifier.heightIn(min = 600.dp),
                    contentScale = ContentScale.FillHeight,
                )

                Image(
                    imageVector = Icons.Rounded.MyLocation,
                    contentDescription = null,
                )

                Text(
                    text = stringResource(id = R.string.data_access_rationale_location),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                )
            }
        }
    }

    private fun convertLongToTime(time: Long): String {
        if (time == -1L) return ""

        val date = Date(time)
        val format = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault())
        return format.format(date)
    }
}
