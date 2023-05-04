package online.mohmedbakr.newsfeed.core

abstract class ConstantsName {
    companion object {
        val englishMonths = arrayOf(
            "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep",
            "Oct", "Nov", "Dec"
        )
        val arabicMonths = arrayOf(
            "يناير", "فبراير", "مارس", "أبريل", "مايو", "يونيو", "يوليو", "أغسطس", "سبتمبر",
            "أكتوبر", "نوفمبر", "ديسمبر"
        )

        val englishDays = arrayOf(
            "Sat", "Sun", "Mon", "Tue", "Wed", "Thu", "Fri"
        )


        val arabicDays = arrayOf(
            "السبت", "الأحد", "الإثنين", "الثلاثاء", "الأربعاء", "الخميس", "الجمعة"
        )

        val arabic_12Hour = arrayOf("ص", "م")
        val english_12Hour = arrayOf("AM", "PM")

        val newspaperNames = arrayOf(
            "مصراوى",
            "رصد",
            "بى بى سى العربية",
            "مدى مصر",
            "العربية",
            "الحدث",
            "الجزيرة",
            "اليوم السابع"
        )

    }
}