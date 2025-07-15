package online.mohmedbakr.newsfeed.data.datasources.remote
import online.mohmedbakr.newsfeed.core.ConstantsName

fun main() {
    print(
        translateDate("السبت، 12 يوليو 2025 11:12 ص")
    )

}
 
fun translateDate(data: String): String {
    var translatedDate = data
    var englishDay: String
    var englishMonth: String
    var english12Hour: String

    ConstantsName.arabicMonths.forEachIndexed { index, value ->
        englishMonth = ConstantsName.englishMonths[index]
        translatedDate = translatedDate.replace(value, englishMonth)
    }

    ConstantsName.arabicDays.forEachIndexed { index, value ->
        englishDay = ConstantsName.englishDays[index]
        translatedDate = translatedDate.replace(value, englishDay)
    }

    ConstantsName.arabic_12Hour.forEachIndexed { index, value ->
        english12Hour = ConstantsName.english_12Hour[index]
        translatedDate = translatedDate.replace(" $value", " $english12Hour")
    }

    translatedDate = translatedDate.replace("،", ",")

    return translatedDate
}