package com.example.restaurantreview

import ReviewAdapter
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.restaurantreview.data.response.CustomerReviewsItem
import com.example.restaurantreview.data.response.Restaurant
import com.example.restaurantreview.data.response.RestaurantResponse
import com.example.restaurantreview.data.retrofit.ApiConfig
import com.example.restaurantreview.databinding.ActivityMainBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class MainActivity : AppCompatActivity() {
    private lateinit var binding :ActivityMainBinding

    companion object{
       private const val TAG = "ActivityMain"
      private const val RESTAURANT_ID = "uewq1zg2zlskfw1e867"
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


        val layoutManager = LinearLayoutManager(this)
        binding.rvReview.layoutManager = layoutManager
        val itemDecoration = DividerItemDecoration(this, layoutManager.orientation)
        binding.rvReview.addItemDecoration(itemDecoration)

        findRestaurant()
    }

    private fun setRestaurandData(restaurant : Restaurant) {
        binding.tvTitle.text = restaurant.name
        binding.tvDescription.text = restaurant.description

        Glide.with(this@MainActivity)
            .load("https://restaurant-api.dicoding.dev/images/large/${restaurant.pictureId}")
            .into(binding.ivPicture)

    }
    private fun setReviewData(customerReviewsItem: List<CustomerReviewsItem>){
        val adapter = ReviewAdapter()
        adapter.submitList(customerReviewsItem)
        binding.rvReview.adapter = adapter
        binding.etReview.setText("")
    }

    private fun findRestaurant() {
        showLoading(true)
        val client = ApiConfig.getApiService().getRestaurant(RESTAURANT_ID)
        client.enqueue(object : Callback<RestaurantResponse>{
            override fun onResponse(
                call: Call<RestaurantResponse>,
                response: Response<RestaurantResponse>
            ) {
                showLoading(false)
                if (response.isSuccessful){
                    val responseBody = response.body()
                    if (responseBody != null){
                        setRestaurandData(responseBody.restaurant)
                        setReviewData(responseBody.restaurant.customerReviews)
                    }
                    else{
                        Log.e(TAG,"onFailure ${response.message()}")
                    }

                }
            }

            override fun onFailure(call: Call<RestaurantResponse>, t: Throwable) {
                showLoading(false)
                Log.e(TAG,"onFailure: ${t.message}")
            }

        })
    }

    private fun showLoading(isLoading : Boolean) {
        if (isLoading){
            binding.progressBar.visibility = View.VISIBLE
        }else{
            binding.progressBar.visibility = View.INVISIBLE
        }
    }

}