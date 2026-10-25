package online.mohmedbakr.newsfeed.core

abstract class ConstantsName {
    companion object {
        val englishMonths = listOf(
            "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep",
            "Oct", "Nov", "Dec"
        )
        val arabicMonths = listOf(
            "يناير", "فبراير", "مارس", "أبريل", "مايو", "يونيو", "يوليو", "أغسطس", "سبتمبر",
            "أكتوبر", "نوفمبر", "ديسمبر"
        )

        val englishDays = listOf(
            "Sat", "Sun", "Mon", "Tue", "Wed", "Thu", "Fri"
        )

        val arabicDays = listOf(
            "السبت", "الأحد", "الإثنين", "الثلاثاء", "الأربعاء", "الخميس", "الجمعة"
        )

        val arabic_12Hour = listOf("ص", "م")
        val english_12Hour = listOf("AM", "PM")

        val newspaperNames = listOf(
            "مصراوى",
            "رصد",
            "بى بى سى العربية",
            "مدى مصر",
            "العربية",
            "الحدث",
            "الجزيرة"
        )
    }
}
