package com.bonda.bonda.ui.article

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bonda.bonda.R

class ArticleViewModel : ViewModel() {
    private val _id = MutableLiveData<Int>()
    private val _isSaved = MutableLiveData<Boolean>()
    private val _coverImage = MutableLiveData<Int>()
    private val _category = MutableLiveData<String>()
    private val _title = MutableLiveData<String>()
    private val _subTitle = MutableLiveData<String>()
    private val _body = MutableLiveData<String>()
    private val _books = MutableLiveData<List<Book>>()
    private val _articles = MutableLiveData<List<Article>>()

    // read-only properties
    val id: LiveData<Int> = _id
    val isSaved: LiveData<Boolean> = _isSaved
    val coverImage: LiveData<Int> = _coverImage
    val category: LiveData<String> = _category
    val title: LiveData<String> = _title
    val subTitle: LiveData<String> = _subTitle
    val body: LiveData<String> = _body
    val books: LiveData<List<Book>> = _books
    val articles: LiveData<List<Article>> = _articles

    // data-class declaration
    data class Book(
        val id: Int,
        val coverImage: Int,
        val category: String,
        val title: String,
        val author: String,
        val body: String
    )
    data class Article(
        val id: Int,
        val coverImage: Int,
        val category : String,
        val title: String
    )

    // -------------------------------------------- >8 --------------------------------------------
    // dummy-init code
    init {
        _id.value = 1
        _isSaved.value = false
        _coverImage.value = R.drawable.dummy_article_cover2
        _category.value = "테마"
        _title.value = "한 가지 사물,\n수백 가지 이야기"
        _subTitle.value = "한 가지 사물에 숨겨진 다채로운 순간들"
        _body.value = """
            작은 사물 하나에도 수많은 이야기가 숨어 있습니다. 하지만, 우리는 그 사실을 잊을 때가 많죠.
            한 가지 사물에 집중해 다양한 형태와 상황을 담아낸 이 포토북 시리즈는 일상에서 흔히 지나치기 쉬운 물건들이 작가의 시선을 통해 특별한 이야기를 품고 재탄생합니다.
            
            각 책은 사물을 새롭게 해석한 작품이자, 순간의 아름다움을 포착한 예술 작품이죠.
            일상을 특별하게 기록한 이 포토북 시리즈를 통해 감각적인 시선과 유쾌한 상상력을 경험해보세요.
            『하트책』은 일상 속 하트 모양을 포착해 사랑스러운 순간을 담아내고, 『가구 산』은 가구가 빚어내는 독특한 풍경을, 『오로지 계란후라이』는 단순하지만 다채로운 계란후라이의 변주를 보여줍니다. 그리고 『엿책』은 살짝 짓궂지만 재치 있는 시선으로 ‘엿’을 포착해, 예상치 못한 상황에서 위트 있는 웃음을 선사합니다.
            
            기발한 발상과 사진가의 유쾌한 시선이 돋보이는 이 포토북은 일상 속 물건을 새로운 시각으로 바라보게 만들 거예요.
        """.trimIndent()
        _books.value = listOf(
            Book(1, R.drawable.dummy_book1, "사진", "하트책", "이혜승", "길을 걷다 문득 마주친 하트 모양, 우연히 발견한 작은 순간들을 기록한 사진집이에요.\n\n다양한 형태의 사랑이 존재하는 것처럼, 하트도 가지각색 분위기로 존재하는데요. 꾸준히 모아온 이 책 속 하트들은 보는 것만으로도 마음을 따뜻하게 만들어줍니다. 하트는 단순한 모양이 아니라, 사랑과 감정을 전하는 강력한 상징이잖아요. 마음속에 작은 하트를 품고 싶은 날, 이 책이 당신의 일상을 더욱 사랑스럽게 만들어줄 거예요. ♡"),
            Book(2, R.drawable.dummy_book2, "사진", "엿 책", "이혜승", "세상 곳곳에서 발견한 ‘엿(ㅗ, 凸)’을 유쾌한 시선으로 담아낸 사진집입니다.\n\n길을 걷다가 문득 익숙한 손짓을 발견할 때가 있어요. 통쾌하기도 하고, 묘하게 시원하기도 하고, 때론 괜히 미운 사람에게 슬며시 보여주고 싶어질 때도 있죠. 세상에는 생각보다 많은 엿이 숨어 있어요. 손짓뿐만 아니라 건물의 구조, 표지판, 조형물, 우연히 겹쳐 보이는 사물들까지! 페이지를 넘기다 보면 마치 숨은그림찾기처럼 엿을 발견하는 재미가 쏠쏠할 걸요?"),
            Book(3, R.drawable.dummy_book3, "사진", "가구 산", "김병덕", "『가구 산』은 오랫동안 자리했던 풍경이 허물어지는 순간들을 사진으로 담은 사진집입니다.\n\n이 책이 바라보는 것은 건축의 변화가 아닌, 아파트 곳곳에 버려진 가구들입니다. 그리고 그것들이 산처럼 쌓여가는 모습을 포착하며 ‘가구 산’이라는 이름을 붙였어요. 한때 누군가의 일상을 채웠던 가구들이 폐기물로 변해가는 모습을 보며, 우리는 어떤 감정을 느끼게 될까요? 익숙했던 공간이 사라진 후, 그곳을 채우던 것들은 어떤 의미로 남을까요?"),
            Book(4, R.drawable.dummy_book4, "사진", "오로지, 계란후라이", "오늘의잔업", "간단하지만 은근히 멋스럽고, 식탁을 채워주는 음식, 계란후라이의 다양한 순간을 담은 사진집입니다.\n\n아침 식탁 위에서, 따끈한 밥 한 공기 위에서, 혹은 메인 요리에 곁들여지는 계란후라이는 우리에게 가장 익숙한 음식 중 하나예요. 별 것 아닌 듯하지만, 어떤 요리에도 자연스럽게 어우러지는 존재죠. 페이지를 넘기다 보면 노릇하게 익은 가장자리, 탱글한 노른자, 반숙과 완숙의 미묘한 차이까지 쉽게 지나쳤던 계란후라이의 매력을 새삼 발견할 수 있을 거예요.")
        )
        _articles.value = listOf(
            Article(1, R.drawable.dummy_article_cover1, "작가/출판사", "오수영 작가의 사색과 감성"),
            Article(2, R.drawable.dummy_article_cover4, "테마", "함께 살아가는 따뜻 이야기"),
            Article(3, R.drawable.dummy_article_cover3, "테마", "집, 우리 삶의 거울"),
        )
    }
    // -------------------------------------------- >8 --------------------------------------------
}
