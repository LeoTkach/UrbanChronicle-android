package com.leotkach.urbanchronicle.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [CategoryEntity::class, ArticleEntity::class, CommentEntity::class],
    version = 1,
    exportSchema = false,
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun categories(): CategoryDao
    abstract fun articles(): ArticleDao
    abstract fun comments(): CommentDao

    companion object {
        @Volatile
        private var instance: AppDatabase? = null

        fun get(context: Context): AppDatabase {
            return instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "urban_chronicle.db",
                ).build().also { instance = it }
            }
        }
    }
}

suspend fun AppDatabase.ensureSeeded() {
    if (categories().count() > 0) return

    val poglyad = categories().insert(CategoryEntity(name = "Погляд"))
    val city = categories().insert(CategoryEntity(name = "Місто"))
    val byt = categories().insert(CategoryEntity(name = "Побут"))
    val now = categories().insert(CategoryEntity(name = "Сучасність"))

    fun ts(iso: String): Long {
        // 2026-05-12 21:40:00 local-ish epoch for stable seed order
        val parts = iso.split(' ', ':', '-')
        // yyyy MM dd HH mm ss
        val y = parts[0].toInt()
        val m = parts[1].toInt()
        val d = parts[2].toInt()
        val h = parts[3].toInt()
        val min = parts[4].toInt()
        val s = parts[5].toInt()
        return java.util.Calendar.getInstance().apply {
            set(y, m - 1, d, h, min, s)
            set(java.util.Calendar.MILLISECOND, 0)
        }.timeInMillis
    }

    val a1 = articles().insert(
        ArticleEntity(
            title = "Київська набережна після зміни",
            body =
                "Стояв на бетонній набережній і довго не міг відвести очей від води. " +
                    "Дніпро темний, майже чорний під ліхтарями; збоку гуркочуть самокати, хтось сміється " +
                    "в навушники, черга по каву тягнеться вздовж поручнів. У горлі щось стискається - " +
                    "ніби місто нове, а біль старий.\n\n" +
                    "Самі собою виринули рядки, що я колись писав іншому берегові:\n\n" +
                    "«Як умру, то поховайте\nМене на могилі,\nСеред степу широкого,\nНа Вкраїні милій…»\n\n" +
                    "Степу тут немає. Є плитка під ногами, запах мокрого каменю й ріка, яка все одно " +
                    "зветься додому.",
            author = "Тарас Шевченко",
            categoryId = poglyad,
            createdAt = ts("2026-05-12 21:40:00"),
        ),
    )
    val a2 = articles().insert(
        ArticleEntity(
            title = "Львівська аптека після девʼятої",
            body =
                "Після девʼятої вузька вулиця мокра, неон аптеки ріже очі. Стою в черзі, тримаю номер " +
                    "на екрані термінала, і болить не тільки те, за чим прийшла - болить саме чекання.\n\n" +
                    "Біль знайомий. Він не в костюмі драми і не на сцені - він тут, у Львові, між мокрим " +
                    "каменем і синім світлом аптеки.",
            author = "Леся Українка",
            categoryId = city,
            createdAt = ts("2026-05-18 14:10:00"),
        ),
    )
    articles().insert(
        ArticleEntity(
            title = "Привокзальний ринок о сьомій",
            body =
                "О сьомій Привокзальний уже гуде. Ящики з морквою, пластикові пакети, голос торговки - " +
                    "і я серед цього, з важкою сумкою, ніби знову взявся за спільну працю.\n\n" +
                    "Тут ніхто не піднімає рук хором. Хтось тягне сітку до маршрутки, хтось рахує гривні. " +
                    "А все одно відчуваю той самий жар у плечах: місто збирається в одному місці.",
            author = "Іван Франко",
            categoryId = byt,
            createdAt = ts("2026-06-02 03:15:00"),
        ),
    )
    articles().insert(
        ArticleEntity(
            title = "Харківські панелі на світанку",
            body =
                "Світанок у спальному районі тихий, як після довгої хвороби. Сірі балкони ще сплять, " +
                    "білизна на мотузках ледь ворушиться, з кіоска біля зупинки тягне теплим хлібом.\n\n" +
                    "Краса тепер не в степу, а в цій тиші між ніччю й ранковим шумом.",
            author = "Архів",
            categoryId = city,
            createdAt = ts("2026-06-20 17:05:00"),
        ),
    )
    val a5 = articles().insert(
        ArticleEntity(
            title = "Тиша в метро між станціями",
            body =
                "Між «Золотими воротами» і «Університетом» вагон на секунду тихне - тільки гул коліс. " +
                    "Я тримаюся за поручень і дивлюся не в телефон, а в підлогу.\n\n" +
                    "«Господи, гніву пречистого\nблагаю - не май за зле.»",
            author = "Василь Стус",
            categoryId = now,
            createdAt = ts("2026-07-01 11:30:00"),
        ),
    )
    articles().insert(
        ArticleEntity(
            title = "Харків між пероном і двором",
            body =
                "Зійшов із перону - і місто вдарило звичним повітрям: мазут, кава з кіоска, голоси " +
                    "під навісом. Іду дворами, які знаю напамʼять.\n\n" +
                    "Комуналка пахне смаженою цибулею; у прохідному дворі сушать білизну. Мені тепло " +
                    "й трохи боляче - бо памʼять не про красивий пейзаж, а про адреси, які тримають.",
            author = "Сергій Жадан",
            categoryId = now,
            createdAt = ts("2026-07-28 19:20:00"),
        ),
    )

    comments().insert(
        CommentEntity(
            articleId = a1,
            author = "Іван - Камінний хрест",
            text =
                "Набережна нова, а вода та сама. Хто їде з села в місто - теж шукає, де стати " +
                    "ногою. Самокат мине, ріка - ні. Читаю цей запис і згадую свій камінь: важкий, " +
                    "але свій. Тут бетон і ліхтарі, а все одно хочеться покласти долоню на поручень " +
                    "і сказати: я теж прийшов здалеку.",
            createdAt = ts("2026-05-13 09:00:00"),
        ),
    )
    comments().insert(
        CommentEntity(
            articleId = a1,
            author = "Мелашка - Кайдашева сімʼя",
            text =
                "Черга по каву вздовж поручнів - ніби хтось розкладає чужі турботи на вигляд. " +
                    "Мені шкода автора: місто гучне, а в горлі тиша. Хай хоч Дніпро слухає, " +
                    "коли люди вже не чують.",
            createdAt = ts("2026-05-13 12:40:00"),
        ),
    )
    comments().insert(
        CommentEntity(
            articleId = a2,
            author = "Мавка - Лісова пісня",
            text =
                "Черга за ліками - не ліс, але біль чути й тут. Телефон біля вуха голосніший " +
                    "за шелест; аптека світить, як чужа зоря. Я б простягнула руку крізь неон " +
                    "і сказала жінці з адресою в голосі: ти не сама в цій мокрій вулиці.",
            createdAt = ts("2026-05-19 08:22:00"),
        ),
    )
    comments().insert(
        CommentEntity(
            articleId = a5,
            author = "Чіпка - Хіба ревуть воли…",
            text =
                "У метро тісно, як у долі. Лікті чужі, запах мокрої куртки, хтось шурхотить " +
                    "навушниками - і все одно між станціями є чесна тиша. Не злюсь на натовп. " +
                    "Просто тримаюся за поручень і думаю: не зігнутися, не розчинитися в екранах. " +
                    "Коли двері гупнуть, тиша ніби виходить зі мною на перон.",
            createdAt = ts("2026-07-02 10:11:00"),
        ),
    )
    comments().insert(
        CommentEntity(
            articleId = a5,
            author = "Свирид Голохвостий - За двома зайцями",
            text =
                "Я б у такому вагоні ще й жарт сказав, але тут жарт не лізе. Автор правильно " +
                    "дивиться в підлогу: іноді правда ближча, ніж реклама над дверима. " +
                    "Тримайтеся, пане Стус - і ми за вами, хоч і в навушниках.",
            createdAt = ts("2026-07-02 18:05:00"),
        ),
    )
}
