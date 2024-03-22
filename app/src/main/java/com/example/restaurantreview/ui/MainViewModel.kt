package com.example.restaurantreview.ui

import Event
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.restaurantreview.data.response.CustomerReviewsItem
import com.example.restaurantreview.data.response.PostReviewResponse
import com.example.restaurantreview.data.response.Restaurant
import com.example.restaurantreview.data.response.RestaurantResponse
import com.example.restaurantreview.data.retrofit.ApiConfig
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainViewModel : ViewModel() {
    private val _restaurant = MutableLiveData<Restaurant>()
            val restaurant : LiveData<Restaurant> = _restaurant

    private val _listView  = MutableLiveData<List<CustomerReviewsItem>>()
    val listView : LiveData<List<CustomerReviewsItem>> = _listView

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading : LiveData<Boolean> = _isLoading

//    menngunakan wraper untuk membungkus String
    private val _snackBarText = MutableLiveData<Event<String>>()
    val snackBarText : LiveData<Event<String>> =  _snackBarText

    companion object{
        private const val TAG = "MainViewModel"
        private const val RESTAURANT_ID = "uewq1zg2zlskfw1e867"
    }

    init {
        findRestaurant()
    }


        private fun findRestaurant() {
            _isLoading.value = (true)
            val client = ApiConfig.getApiService().getRestaurant(RESTAURANT_ID)
            client.enqueue(object : Callback<RestaurantResponse> {
                override fun onResponse(
                    call: Call<RestaurantResponse>,
                    response: Response<RestaurantResponse>
                ) {
                    _isLoading.value = (false)
                    if (response.isSuccessful) {
                        val responseBody = response.body()
                        if (responseBody != null) {
                            _restaurant.value = (responseBody.restaurant)
                            _listView.value = (responseBody.restaurant.customerReviews)
                        } else {
                            Log.e(TAG, "onFailure ${response.message()}")
                        }

                    }
                }

                override fun onFailure(call: Call<RestaurantResponse>, t: Throwable) {
                    _isLoading.value = (false)
                    Log.e(TAG, "onFailure: ${t.message}")
                }

            })
        }

    internal fun postReview(review: String) {
        _isLoading.value = (true)
        val client = ApiConfig.getApiService().postReview(RESTAURANT_ID, "Dicoding", review)
        client.enqueue(object : Callback<PostReviewResponse> {
            override fun onResponse(
                call: Call<PostReviewResponse>,
                response: Response<PostReviewResponse>
            ) {
                _isLoading.value = (false)
                val responseBody = response.body()
                if (response.isSuccessful && responseBody != null) {
                    _listView.value = (responseBody.customerReviews)
                    _snackBarText.value =Event(response.body()?.message.toString())
                } else {
                    Log.e(TAG, "onFailure: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<PostReviewResponse>, t: Throwable) {
                _isLoading.value = (false)
                Log.e(TAG, "onFailure: ${t.message}")
            }
        })
    }


}