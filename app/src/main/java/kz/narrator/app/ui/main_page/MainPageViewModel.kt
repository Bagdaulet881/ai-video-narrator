package kz.narrator.app.ui.main_page

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.recyclerview.widget.RecyclerView
import kz.narrator.app.R
import kz.narrator.app.data.enums.app.ProfileEnums
import kz.narrator.app.data.enums.app.main_page.MainPageNavEnums
import kz.narrator.app.data.models.blog.Blog
import kz.narrator.app.data.models.main_page.EcoForm
import kz.narrator.app.data.models.main_page.Services
import kz.narrator.app.data.models.main_page.indicator.IndicatorModel
import kz.narrator.app.data.models.my_suggestions_and_reports.MySuggestionsAndReports
import kz.narrator.app.data.models.forecast.WeatherForecastModel
import kz.narrator.app.data.models.map.Station
import kz.narrator.app.data.preferences.Preferences
import kz.narrator.app.data.repository.appeal.AppealRepository
import kz.narrator.app.data.repository.blog.BlogRepository
import kz.narrator.app.data.repository.map.MapRepository
import kz.narrator.app.data.repository.suggestions.SuggestionsRepository
import kz.narrator.app.data.repository.weather.WeatherRepository
import kz.narrator.app.permission.base.OnPermissionGrantedListener
import kz.narrator.app.ui_common.callbacks.RecyclerViewItemClickCallback
import kz.narrator.app.ui_common.callbacks.RetryCallback
import kz.narrator.app.ui_common.callbacks.SwipeToRefreshCallback
import kz.narrator.app.utils.live_data.Event
import javax.inject.Inject

class MainPageViewModel
@Inject constructor(
    private val app: Application,
    private val blogRepository: BlogRepository,
    private val suggestionsRepository: SuggestionsRepository,
    private val weatherRepository: WeatherRepository,
    private val mapRepository: MapRepository,
    private val appealRepository: AppealRepository,
    private val preferences: Preferences
) : AndroidViewModel(app) {

    private val getWeather = MutableLiveData<Unit>()
    var isRefreshing = MutableLiveData<Boolean>()

    val retryCallback = object : RetryCallback {
        override fun onRetryClick() {
            getWeather.postValue(Unit)
        }
    }

    val swipeToRefreshCallback = object :
        SwipeToRefreshCallback {
        override fun onSwipeToRefresh() {
            getWeather.postValue(Unit)
            isRefreshing.value = false
        }
    }
    val locationPermissionCallback = object : OnPermissionGrantedListener {

        override fun onPermissionGranted() {
            _requestLocationUpdate.postValue(Event(Unit))
        }

        override fun onPermissionNotGranted() {
            _requestLocationPermission.postValue(Event(Unit))
        }

        override fun onPermissionExit() {}
    }
    private val _requestLocationPermission = MutableLiveData<Event<Unit>>()
    val requestLocationPermission: LiveData<Event<Unit>>
        get() = _requestLocationPermission
    private val _requestLocationUpdate = MutableLiveData<Event<Unit>>()
    val requestLocationUpdate: LiveData<Event<Unit>>
        get() = _requestLocationUpdate
    private val _listEcoForm = MutableLiveData<Event<List<EcoForm>>>()
    val listEcoForm: LiveData<Event<List<EcoForm>>>
        get() = _listEcoForm

    private val _listReportsAndSugs = MutableLiveData<Event<List<MySuggestionsAndReports>>>()
    val listReportsAndSugs: LiveData<Event<List<MySuggestionsAndReports>>>
        get() = _listReportsAndSugs

    private val _listBlog = MutableLiveData<Event<List<Blog>>>()
    val listBlog: LiveData<Event<List<Blog>>>
        get() = _listBlog

    private val _listServices = MutableLiveData<Event<List<Services>>>()
    val listServices: LiveData<Event<List<Services>>>
        get() = _listServices

    val countOfListEcoForms = 2
    private val _setCurrentItem = MutableLiveData<Event<Int>>()
    val setCurrentItem: LiveData<Event<Int>>
        get() = _setCurrentItem
    private var position: Int = 0


    private val mappingBlogRequest = MutableLiveData<Unit>()
    var blogDataLoad = Transformations.switchMap(mappingBlogRequest) {
        blogRepository.getBlogs(1)
    }

    private val mappingSuggsRequest = MutableLiveData<Unit>()
    private val mappingRepsRequest = MutableLiveData<Unit>()

    var suggsDataLoad = Transformations.switchMap(mappingSuggsRequest) {
        suggestionsRepository.getSuggs(1)
    }

    val reportsDataLoad = Transformations.switchMap(mappingRepsRequest) {
        appealRepository.getAppeals(1)
    }

    private val _weather = MutableLiveData<WeatherForecastModel>()
    val weather: LiveData<WeatherForecastModel>
        get() = _weather

    private val _indicatorsList = MutableLiveData<Event<List<IndicatorModel>>>()
    val indicatorsList: LiveData<Event<List<IndicatorModel>>>
        get() = _indicatorsList

    private val _nearestStation = MutableLiveData<Station>()
    val nearestStation: LiveData<Station>
        get() = _nearestStation

    private val _showCollectionPoints = MutableLiveData<Event<Unit>>()
    val showCollectionPoints: LiveData<Event<Unit>>
        get() = _showCollectionPoints

    private val getIndicatorList = MutableLiveData<Unit>()
    private val getIndicatorListByLocation = MutableLiveData<Unit>()

    val getWeatherResource = Transformations.switchMap(getWeather) {
        weatherRepository.getForecast()
    }

    val weatherStatus
        get() = Transformations.map(getWeatherResource) {
            it.status
        }

    val weatherResource
        get() = Transformations.map(getWeatherResource) {
            it
        }

    val getIndicatorsListResource = Transformations.switchMap(getIndicatorList) {
        mapRepository.getIndicators()
    }

    val status
        get() = Transformations.map(getIndicatorsListResource) {
            it.status
        }

    val resource
        get() = Transformations.map(getIndicatorsListResource) {
            it
        }

    val getIndicatorsListResourceByLocation =
        Transformations.switchMap(getIndicatorListByLocation) {
            val latLng = preferences.getLocationData()
            mapRepository.getIndicatorsByLocation(latLng[0], latLng[1])
        }

    val statusByLocation
        get() = Transformations.map(getIndicatorsListResourceByLocation) {
            it.status
        }

    fun onWeatherResource(weather: WeatherForecastModel) {
        _weather.postValue(weather)
    }

    fun onIndicatorListResource(indicators: List<IndicatorModel>) {
        _indicatorsList.postValue(Event(indicators))
    }
    fun onNearestStationResource(station: Station) {
        if(!station.address.isNullOrBlank()){
            _nearestStation.postValue(station)
        }else{
            station.address = app.getString(R.string.map_default_point)
            _nearestStation.postValue(station)
        }
    }

    init {
        _requestLocationPermission.postValue(Event(Unit))
        getWeather.postValue(Unit)
        if(preferences.getLocationDataState()){
            getIndicatorListByLocation.postValue(Unit)
        }else{
            getIndicatorList.postValue(Unit)
        }
        onSuccess()
    }
    fun setLocationData(lat: Double, lng: Double){
        preferences.setLocationData(lat, lng)
        getIndicatorListByLocation.postValue(Unit)
    }
    fun onBlogsDataLoaded(blogs: List<Blog>?) {
        val temp = mutableListOf<Blog>()
        blogs?.forEach {

            temp.add(
                Blog(
                    id = it.id,
                    title = it.title,
                    datetime = splitDateTime(it.datetime!!),
                    thumbnail = it.thumbnail,
                    content = it.content,
                    site_link = it.site_link,
                    instagram_link = it.instagram_link
                )
            )
        }
        _listBlog.postValue(Event(temp))
    }
    fun splitDateTime(standardDateTime: String): String {
        var new = standardDateTime.split(" ").get(0).split("-")
        return new[2] + "/" + new[1] + "/" + new[0].substring(2, 4)
    }

    private val tempSuggsAndReps = mutableListOf<MySuggestionsAndReports>()

    fun onSuggsDataSuccess(suggs: List<MySuggestionsAndReports>?) {
        var count = 0

        if (tempSuggsAndReps.size < 4) {
            suggs?.forEach {
                if (count < 2) {
                    tempSuggsAndReps.add(
                        MySuggestionsAndReports(
                            id = it.id,
                            topic = it.topic,
                            title = it.title,
                            description = it.description,
                            status = if (it.status?.toInt() == 1) {
                                app.getString(R.string.suggestions_and_reports_status_true)
                            } else app.getString(R.string.suggestions_and_reports_status_false),
                            address = it.address,
                            src = R.drawable.ic_suggestions_and_reports_placeholder,
                            amountVotesAgainst = it.amountVotesAgainst,
                            amountVotesFor = it.amountVotesFor,
                            type = 1,
                            total_votes = it.total_votes,
                            datetime = it.datetime,
                            thumbnail = it.thumbnail,
                            category = it.category
                        )
                    )
                }
                count++
            }
        }
        _listReportsAndSugs.postValue(Event(tempSuggsAndReps))
    }

    fun onRepsDataSuccess(reps: List<MySuggestionsAndReports>?) {
        var count = 0
        if (tempSuggsAndReps.size < 4) {
            reps?.forEach {
                if (count < 2) {
                    tempSuggsAndReps.add(
                        MySuggestionsAndReports(
                            id = it.id,
                            topic = it.topic,
                            title = it.title,
                            description = it.description,
                            status = if (it.status?.toInt() == 1) {
                                app.getString(R.string.suggestions_and_reports_status_true)
                            } else app.getString(R.string.suggestions_and_reports_status_false),
                            address = it.address,
                            src = R.drawable.ic_suggestions_and_reports_placeholder,
                            amountVotesAgainst = it.amountVotesAgainst,
                            amountVotesFor = it.amountVotesFor,
                            total_votes = it.total_votes,
                            type = 2,
                            datetime = it.datetime,
                            thumbnail = it.thumbnail,
                            category = it.category
                        )
                    )
                }
                count++
            }
        }
        _listReportsAndSugs.postValue(Event(tempSuggsAndReps))
    }

    fun onSuccess() {
        mappingBlogRequest.value = Unit
        mappingSuggsRequest.value = Unit
        mappingRepsRequest.value = Unit
        val tempEcoForm = mutableListOf<EcoForm>()
        val tempServices = mutableListOf<Services>()

        tempEcoForm.add(
            EcoForm(
                id = 0,
                title = app.getString(R.string.main_page_sugg_text),
                btnText = app.getString(R.string.main_page_btn_open_sugg_form)
            )
        )
        tempEcoForm.add(
            EcoForm(
                id = 1,
                title = app.getString(R.string.main_page_rep_text),
                btnText = app.getString(R.string.main_page_btn_open_rep_form)
            )
        )


        tempServices.add(
            Services(
                id = 0,
                value = app.getString(R.string.main_page_eco_report),
                icon = R.drawable.ic_shield
            )
        )
        tempServices.add(
            Services(
                id = 1,
                value = app.getString(R.string.main_page_invite),
                icon = R.drawable.ic_paper_plus
            )
        )
        tempServices.add(
            Services(
                id = 2,
                value = app.getString(R.string.main_page_points),
                icon = R.drawable.ic_delete
            )
        )
//        tempServices.add(
//            Services(
//                id = 3,
//                value = app.getString(R.string.main_page_how_to_protect),
//                icon = R.drawable.ic_shield_done
//            )
//        )
        tempServices.add(
            Services(
                id = 4,
                value = app.getString(R.string.main_page_faq),
                icon = R.drawable.ic_info_scuare
            )
        )
        tempServices.add(
            Services(
                id = 5,
                value = app.getString(R.string.main_page_contacts),
                icon = R.drawable.ic_contacts
            )
        )

        _listEcoForm.postValue(Event(tempEcoForm))
        _listServices.postValue(Event(tempServices))
    }

    private val _openDialogAirCondition = MutableLiveData<Event<IndicatorModel>>()
    val openDialogAirCondition: LiveData<Event<IndicatorModel>>
        get() = _openDialogAirCondition

    private val _openDialogIndicatorMainPage = MutableLiveData<Event<Unit>>()
    val openDialogIndicatorMainPage: LiveData<Event<Unit>>
        get() = _openDialogIndicatorMainPage

    private val _openDetailAirCondition = MutableLiveData<Event<Unit>>()
    val openDetailAirCondition: LiveData<Event<Unit>>
        get() = _openDetailAirCondition

    private val _openDetailSuggsAndReps = MutableLiveData<Event<MySuggestionsAndReports>>()
    val openDetailSuggsAndReps: LiveData<Event<MySuggestionsAndReports>>
        get() = _openDetailSuggsAndReps

    private val _openBlogDetail = MutableLiveData<Event<Blog>>()
    val openBlogDetail: LiveData<Event<Blog>>
        get() = _openBlogDetail

    private val _goAirConditionPage = MutableLiveData<Event<Unit>>()
    val goAirConditionPage: LiveData<Event<Unit>>
        get() = _goAirConditionPage

    private val _goBlogPage = MutableLiveData<Event<Unit>>()
    val goBlogPage: LiveData<Event<Unit>>
        get() = _goBlogPage

    private val _goFaqPage = MutableLiveData<Event<Unit>>()
    val goFaqPage: LiveData<Event<Unit>>
        get() = _goFaqPage

    private val _goMapPage = MutableLiveData<Event<Unit>>()
    val goMapPage: LiveData<Event<Unit>>
        get() = _goMapPage

    private val _goFormPage = MutableLiveData<Event<Int>>()
    val goFormPage: LiveData<Event<Int>>
        get() = _goFormPage

    private val _goWeatherPage = MutableLiveData<Event<Unit>>()
    val goWeatherPage: LiveData<Event<Unit>>
        get() = _goWeatherPage

    private val _goProfilePage = MutableLiveData<Event<Unit>>()
    val goProfilePage: LiveData<Event<Unit>>
        get() = _goProfilePage

    private val _goSuggsAndReps = MutableLiveData<Event<ProfileEnums>>()
    val goSuggsAndReps: LiveData<Event<ProfileEnums>>
        get() = _goSuggsAndReps

    private val _showToast = MutableLiveData<Event<Unit>>()
    val showToast: LiveData<Event<Unit>>
        get() = _showToast

    private val _goContacts = MutableLiveData<Event<Int>>()
    val goContacts: LiveData<Event<Int>>
        get() = _goContacts

    private val _goWeatherForWeek = MutableLiveData<Event<Unit>>()
    val goWeatherForWeek: LiveData<Event<Unit>>
        get() = _goWeatherForWeek

    private val _goAuthPage = MutableLiveData<Event<Unit>>()
    val goAuthPage: LiveData<Event<Unit>>
        get() = _goAuthPage

    private lateinit var indicatorModel: IndicatorModel

    fun onIndicatorMainPageDialogBtnClick() {
        _openDialogIndicatorMainPage.postValue(Event(Unit))
    }

    fun setIndicator(indicatorModel: IndicatorModel) {
        this.indicatorModel = indicatorModel
    }

    fun onAirConditionBtnClick() {
        _goAirConditionPage.postValue(Event(Unit))
    }

    fun onSuggsAndRepsBtnClick() {
        _goSuggsAndReps.postValue(Event(ProfileEnums.SUGGESTIONS))
    }

    fun onDetailAirConditionBtnClick() {
        _openDialogAirCondition.postValue(Event(indicatorModel))
    }

    fun onFaqBtnClick() {
        _goFaqPage.postValue(Event(Unit))
    }

    fun onBlogBtnClick() {
        _goBlogPage.postValue(Event(Unit))
    }

    fun onMapBtnClick() {
        _goMapPage.postValue(Event(Unit))
    }

    fun onFormBtnClick() {
//        _goFormPage.postValue(Event(Unit))
//        _showToast.postValue(Event(Unit))
    }

    fun onSelect(selectedValue: Int) {
        _openDetailAirCondition.postValue(Event(Unit))
    }

    fun onWeatherBtnClick() {
        _goWeatherForWeek.postValue(Event(Unit))
    }

    fun onContactBtnClick() {
        _goProfilePage.postValue(Event(Unit))
    }
    val recyclerScrolledListener = object : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)

            if (recyclerView.scrollState == RecyclerView.SCROLL_STATE_SETTLING)
                if (dx > 0) {
                    if (position != countOfListEcoForms) {
                        position+=1
                        _setCurrentItem.postValue(Event(position))
                    }
                } else if (dx < 0) {
                    if (position != 0) {
                        position-=1
                        _setCurrentItem.postValue(Event(position))
                    }
                }

        }
    }
    val recyclerViewItemClickCallback =
        object : RecyclerViewItemClickCallback {
            override fun onRecyclerViewItemClick(any: Any) {
                when (any) {
                    is EcoForm -> {
                        any.id.let {
                            if (it != null) {
                                if (!preferences.getAppToken().isNullOrBlank()) {
                                    _goFormPage.postValue(Event(it))
                                } else {
                                    _goAuthPage.postValue(Event(Unit))
                                }
                            }
                        }
                    }
                    is Services -> {
                        when (any.id) {
                            MainPageNavEnums.ECO_REPORT.id -> {
                                _goSuggsAndReps.postValue(Event(ProfileEnums.REPORTS))
                            }
                            MainPageNavEnums.INVITE.id -> {
                                _goSuggsAndReps.postValue(Event(ProfileEnums.SUGGESTIONS))
                            }
                            MainPageNavEnums.POINTS.id -> {
                                _showCollectionPoints.postValue(Event(Unit))
                            }
                            MainPageNavEnums.HOW_TO_PROTECT.id -> {
                                _showToast.postValue(Event(Unit))
                            }
                            MainPageNavEnums.FAQ.id -> {
                                onFaqBtnClick()
                            }
                            MainPageNavEnums.CONTACTS.id -> {
                                _goContacts.postValue(Event(any.id))
                            }
                        }
                    }
                    is MySuggestionsAndReports -> {
                        _openDetailSuggsAndReps.postValue(Event(any))

                    }
                    is IndicatorModel -> {
                        _openDialogAirCondition.postValue(Event(any))
                    }
                    is Blog -> {
                        _openBlogDetail.postValue(Event(any))
                    }
                }
            }
        }

}