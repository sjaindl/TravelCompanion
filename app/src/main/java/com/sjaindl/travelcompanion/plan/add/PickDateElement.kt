package com.sjaindl.travelcompanion.plan.add

import android.app.DatePickerDialog
import android.widget.DatePicker
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.sjaindl.travelcompanion.R
import com.sjaindl.travelcompanion.theme.TravelCompanionTheme
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Date

@Composable
fun PickDateElement(
    modifier: Modifier = Modifier,
    prefilled: Date? = null,
    minDate: Date? = null,
    onDateSelected: (Date) -> Unit,
) {
    val context = LocalContext.current
    val calendar = Calendar.getInstance()

    var selectedDateText by remember { mutableStateOf("") }

    val formatter = DateTimeFormatter.ISO_LOCAL_DATE.withZone(ZoneId.systemDefault())

    TravelCompanionTheme {
        val year = calendar[Calendar.YEAR]
        val month = calendar[Calendar.MONTH]
        val dayOfMonth = calendar[Calendar.DAY_OF_MONTH]

        val datePicker = DatePickerDialog(
            context,
            { _: DatePicker, selectedYear: Int, selectedMonth: Int, selectedDayOfMonth: Int ->
                val pickedCalender = Calendar.getInstance()
                pickedCalender.set(selectedYear, selectedMonth, selectedDayOfMonth)
                val date = pickedCalender.time

                val formattedDate = formatter.format(date.toInstant())
                selectedDateText = formattedDate

                onDateSelected(date)
            },
            year,
            month,
            dayOfMonth,
        )

        minDate?.let {
            datePicker.datePicker.minDate = it.time
        }

        if (prefilled != null && selectedDateText.isEmpty()) {
            val prefilledCalendar = Calendar.getInstance()
            prefilledCalendar.time = prefilled

            datePicker.updateDate(
                prefilledCalendar[Calendar.YEAR],
                prefilledCalendar[Calendar.MONTH],
                prefilledCalendar[Calendar.DAY_OF_MONTH],
            )

            val formattedDate = formatter.format(prefilled.toInstant())
            selectedDateText = formattedDate
        }

        val buttonColors = ButtonDefaults.buttonColors(
            containerColor = colorResource(id = R.color.colorMain),
            contentColor = colorResource(id = R.color.textLight),
        )

        Row(
            modifier = modifier,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                modifier = Modifier
                    .padding(all = 8.dp)
                    .weight(1f),
                color = colorScheme.primary,
                text = selectedDateText
            )

            Button(
                modifier = Modifier
                    .padding(all = 8.dp)
                    .weight(1f),
                onClick = {
                    datePicker.show()
                },
                colors = buttonColors,
            ) {
                Text(
                    text = stringResource(id = R.string.pick_date)
                )
            }
        }
    }
}

@Preview
@Composable
fun PickDateElementPreview() {
    PickDateElement(
        onDateSelected = { }
    )
}
