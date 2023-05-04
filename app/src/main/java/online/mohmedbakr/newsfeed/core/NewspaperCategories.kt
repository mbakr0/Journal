package online.mohmedbakr.newsfeed.core

import android.content.res.Resources
import online.mohmedbakr.newsfeed.R

class NewspaperCategories(private val resources: Resources) {

    private val masrawyCategories: HashMap<String, HashMap<String, Int>> = HashMap()
    private val youm7Categories: HashMap<String, Int> = HashMap()
    private val alarabiyaCategories: HashMap<String, String> = HashMap()
    private val alhadathCategories: HashMap<String, Int> = HashMap()
    private val category: HashMap<String, Int> = HashMap()


    @Suppress("UNCHECKED_CAST")
    fun getMasrawyCategories(): HashMap<String, HashMap<String, Int>> {
        category[resources.getString(R.string.main)] = 25
        category[resources.getString(R.string.Egypt)] = 35
        category[resources.getString(R.string.ArabAndWorldNews)] = 202
        category[resources.getString(R.string.Journalism)] = 209
        category[resources.getString(R.string.Economy)] = 206
        category[resources.getString(R.string.accidents)] = 205
        category[resources.getString(R.string.Governorates)] = 204
        category[resources.getString(R.string.ReportsAndInvestigations)] = 207
        category[resources.getString(R.string.Articles)] = 211
        category[resources.getString(R.string.newsVideos)] = 121
        category[resources.getString(R.string.Technology)] = 382
        category[resources.getString(R.string.oldNews)] = 586

        (category.clone() as HashMap<String, Int>).also {
            masrawyCategories[resources.getString(R.string.news)] = it
        }
        category.clear()


        category[resources.getString(R.string.main)] = 27
        category[resources.getString(R.string.local)] = 577
        category[resources.getString(R.string.ArabAndWorldNews)] = 579
        category[resources.getString(R.string.otherSports)] = 578
        category[resources.getString(R.string.opinions)] = 227
        category[resources.getString(R.string.PhotoAlbum)] = 467
        category[resources.getString(R.string.SportsVideos)] = 468

        (category.clone() as HashMap<String, Int>).also {
            masrawyCategories[resources.getString(R.string.sports)] = it
        }
        category.clear()

        category[resources.getString(R.string.main)] = 217
        category[resources.getString(R.string.FashionAndBeauty)] = 218
        category[resources.getString(R.string.Relations)] = 223
        category[resources.getString(R.string.kitchen)] = 221
        category[resources.getString(R.string.MedicalTips)] = 219
        category[resources.getString(R.string.Pregnancy)] = 222
        category[resources.getString(R.string.man)] = 402
        category[resources.getString(R.string.travel)] = 603
        (category.clone() as HashMap<String, Int>).also {
            masrawyCategories[resources.getString(R.string.lifeStyle)] = it
        }
        category.clear()


        category[resources.getString(R.string.main)] = 210
        category[resources.getString(R.string.music)] = 255
        category[resources.getString(R.string.TheaterAndTelevision)] = 235
        category[resources.getString(R.string.Cinema)] = 254
        category[resources.getString(R.string.Zoom)] = 582
        category[resources.getString(R.string.Foreign)] = 583
        category[resources.getString(R.string.ArtVideos)] = 122
        category[resources.getString(R.string.blackAndWhite)] = 632
        (category.clone() as HashMap<String, Int>).also {
            masrawyCategories[resources.getString(R.string.arts)] = it
        }
        category.clear()

        category[resources.getString(R.string.main)] = 373
        category[resources.getString(R.string.news)] = 375
        category[resources.getString(R.string.PhotoAlbum)] = 378
        category[resources.getString(R.string.CarsVideos)] = 379
        category[resources.getString(R.string.Races)] = 376
        category[resources.getString(R.string.Tips)] = 598
        (category.clone() as HashMap<String, Int>).also {
            masrawyCategories[resources.getString(R.string.cars)] = it
        }
        category.clear()

        category[resources.getString(R.string.main)] = 262
        category[resources.getString(R.string.Fatwas)] = 61
        category[resources.getString(R.string.Articles)] = 50
        category[resources.getString(R.string.Prophet)] = 51
        category[resources.getString(R.string.Quran)] = 56
        category[resources.getString(R.string.Other)] = 445
        category[resources.getString(R.string.Stories)] = 446
        category[resources.getString(R.string.IslamicVideos)] = 215
        (category.clone() as HashMap<String, Int>).also {
            masrawyCategories[resources.getString(R.string.islamic)] = it
        }
        category.clear()

        return masrawyCategories
    }

    fun getYoum7Categories(): HashMap<String, Int> {
        youm7Categories[resources.getString(R.string.BreakingNews)] = 65
        youm7Categories[resources.getString(R.string.Policy)] = 319
        youm7Categories[resources.getString(R.string.accidents)] = 203
        youm7Categories[resources.getString(R.string.EgyptianReports)] = 97
        youm7Categories[resources.getString(R.string.Governorates)] = 296
        youm7Categories[resources.getString(R.string.Economy)] = 297
        youm7Categories[resources.getString(R.string.NewsSports)] = 298
        youm7Categories[resources.getString(R.string.universalBall)] = 332
        youm7Categories[resources.getString(R.string.art)] = 48
        youm7Categories[resources.getString(R.string.TV)] = 251
        youm7Categories[resources.getString(R.string.ArabicNews)] = 88
        youm7Categories[resources.getString(R.string.InternationalNews)] = 286
        youm7Categories[resources.getString(R.string.culture)] = 94
        youm7Categories[resources.getString(R.string.WomenAndMiscellaneous)] = 89
        youm7Categories[resources.getString(R.string.HealthAndMedicine)] = 245
        youm7Categories[resources.getString(R.string.Technology)] = 328
        youm7Categories[resources.getString(R.string.CitizenJournalism)] = 335
        youm7Categories[resources.getString(R.string.SeventhDayAlbums)] = 291
        youm7Categories[resources.getString(R.string.ComicsToday)] = 192
        youm7Categories[resources.getString(R.string.Horoscope)] = 330

        return youm7Categories
    }

    fun getAlarabiyaCategories(): HashMap<String, String> {
        alarabiyaCategories[resources.getString(R.string.main)] = ".xml"
        alarabiyaCategories[resources.getString(R.string.ArabAndWorldNews)] = "/arab-and-world.xml"
        alarabiyaCategories[resources.getString(R.string.SaudiArabia)] = "/saudi-today.xml"
        alarabiyaCategories[resources.getString(R.string.Economy)] = "/aswaq.xml"
        alarabiyaCategories[resources.getString(R.string.sports)] = "/sport.xml"
        alarabiyaCategories[resources.getString(R.string.Miscellaneous)] = "/variety.xml"
        alarabiyaCategories[resources.getString(R.string.Articles)] = "/views.xml"
        alarabiyaCategories[resources.getString(R.string.Technology)] = "/technology.xml"
        alarabiyaCategories[resources.getString(R.string.recentNews)] = "/last-page.xml"

        return alarabiyaCategories
    }

    fun getAlhadathCategories(): HashMap<String, Int> {

        alhadathCategories[resources.getString(R.string.ArabAndWorldNews)] = 2
        alhadathCategories[resources.getString(R.string.SaudiArabia)] = 3
        alhadathCategories[resources.getString(R.string.Miscellaneous)] = 4
        alhadathCategories[resources.getString(R.string.HealthAndMedicine)] = 6
        alhadathCategories[resources.getString(R.string.sports)] = 7
        alhadathCategories[resources.getString(R.string.Economy)] = 8
        alhadathCategories[resources.getString(R.string.Articles)] = 0

        return alhadathCategories
    }

}