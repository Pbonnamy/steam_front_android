package com.example.steamlike

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.steamlike.api.ApiClient
import kotlinx.coroutines.*

class WishlistActivity : AppCompatActivity() {
    private var appbarTitle: TextView? = null
    private var likeBtn : ImageButton? = null
    private var wishlistBtn : ImageButton? = null
    private var leftBtn : ImageButton? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.wishlist_activity)

        this.appbarTitle = findViewById(R.id.appbarTitle)
        this.likeBtn = findViewById(R.id.likeBtn)
        this.wishlistBtn = findViewById(R.id.wishlistBtn)
        this.leftBtn = findViewById(R.id.leftBtn)

        val sharedPref = getSharedPreferences("values", MODE_PRIVATE)
        var token = sharedPref.getString("token", null)

        if (token == null) {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        this.handleAppBar()
        this.loadWishlist(token!!)
    }

    private fun handleAppBar () {
        this.likeBtn?.visibility = View.GONE
        this.wishlistBtn?.visibility = View.GONE
        this.appbarTitle?.text = getString(R.string.wishlistTitle)
        this.leftBtn?.setBackgroundResource(R.drawable.close)


        this.leftBtn?.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }

    private fun loadWishlist(token : String) {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val response = withContext(Dispatchers.IO) { ApiClient.apiService.listWishlist(token) }

                if (response.isSuccessful && response.body() != null) {
                    val games = response.body()
                    val size = games?.size

                    if (size!! > 0) {
                        findViewById<RecyclerView>(R.id.list).apply {
                            layoutManager = LinearLayoutManager(this@WishlistActivity)
                            adapter = GameListView.ListAdapter(games!!)
                        }
                    } else {
                        val emptyList = findViewById<ConstraintLayout>(R.id.noItems)
                        emptyList.visibility = View.VISIBLE
                    }
                } else {
                    Toast.makeText(this@WishlistActivity, "Une erreur est survenue", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@WishlistActivity, "Service indisponible", Toast.LENGTH_SHORT).show()
            }
        }
    }
}