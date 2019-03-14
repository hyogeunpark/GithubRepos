package com.hyogeun.githubrepos.ui.main

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.hyogeun.githubrepos.R
import com.hyogeun.githubrepos.common.BaseModel
import com.hyogeun.githubrepos.common.BaseViewHolder
import com.hyogeun.githubrepos.databinding.ListItemReposBinding
import com.hyogeun.githubrepos.databinding.ListItemUserBinding
import com.hyogeun.githubrepos.model.Repo
import com.hyogeun.githubrepos.model.User
import com.hyogeun.githubrepos.network.RestClient
import kotlinx.android.synthetic.main.activity_main.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {

    companion object {
        private val EXTRA_USER_NAME = "extra_user_name"

        fun createInstance(context: Context, name: String?) {
            val intent = Intent(context, MainActivity::class.java)
            name.let { intent.putExtra(EXTRA_USER_NAME, it) }
            context.startActivity(intent)
        }
    }

    private val mRepos: ArrayList<in BaseModel> = arrayListOf()
    private val mAdapter: GithubReposAdapter = GithubReposAdapter(mRepos)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        with(main_recycler_view) {
            adapter = mAdapter
            layoutManager = LinearLayoutManager(this@MainActivity)
        }
        val userName = intent.getStringExtra(EXTRA_USER_NAME) ?: "hyogeunpark"
        getUser(userName)
        main_refresh.setOnRefreshListener {
            dismissLoading()
            getUser(userName)
        }
    }

    private fun getUser(userName: String) {
        showLoading()
        RestClient.service.user(userName).enqueue(object : Callback<User> {
            override fun onFailure(call: Call<User>, t: Throwable) {
                dismissLoading()
                showError(getString(R.string.error_network))
            }

            override fun onResponse(call: Call<User>, response: Response<User>?) {
                dismissLoading()
                if (response?.isSuccessful == true) {
                    response.body()?.let { mRepos.add(0, it) }
                    getRepos(userName)
                } else {
                    showError(getString(R.string.error_user))
                }
            }
        })
    }

    private fun getRepos(userName: String) {
        showLoading()
        RestClient.service.repos(userName).enqueue(object : Callback<ArrayList<Repo>> {
            override fun onFailure(call: Call<ArrayList<Repo>>, t: Throwable) {
                dismissLoading()
                showError(getString(R.string.error_network))
            }

            override fun onResponse(call: Call<ArrayList<Repo>>, response: Response<ArrayList<Repo>>?) {
                dismissLoading()
                if (response?.isSuccessful == true) {
                    response.body()?.let { repos ->
                        mRepos.addAll(repos.sortedBy { repo ->
                            repo.starCount
                        })
                    }
                    mAdapter.notifyDataSetChanged()
                } else {
                    showError(getString(R.string.error_repo))
                }
            }
        })
    }

    private fun showLoading() {
        main_refresh.isRefreshing = true
    }

    private fun dismissLoading() {
        main_refresh.isRefreshing = false
    }

    private fun showError(error: String) {
        Snackbar.make(
            window.decorView.findViewById(android.R.id.content),
            error,
            Snackbar.LENGTH_SHORT
        ).show()
    }

    class GithubReposAdapter(private val mRepos: ArrayList<in BaseModel>) :
        RecyclerView.Adapter<BaseViewHolder<out BaseModel>>() {

        private val USER: Int = 3998
        private val REPOS: Int = 3999

        override fun getItemViewType(position: Int): Int = if (position == 0) USER else REPOS

        override fun getItemCount(): Int = mRepos.count()

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<out BaseModel> {
            return if (viewType == USER) {
                UserViewHolder(ListItemUserBinding.inflate(LayoutInflater.from(parent.context), parent, false))
            } else {
                RepoViewHolder(ListItemReposBinding.inflate(LayoutInflater.from(parent.context), parent, false))
            }
        }

        override fun onBindViewHolder(holder: BaseViewHolder<out BaseModel>, position: Int) {
            val item = mRepos[position]
            val viewType = getItemViewType(position)
            if (viewType == USER && item is User) {
                (holder as UserViewHolder).bind(item)
            } else if (viewType == REPOS && item is Repo) {
                (holder as RepoViewHolder).bind(item)
            }
        }

        class UserViewHolder(private val mBinding: com.hyogeun.githubrepos.databinding.ListItemUserBinding) :
            BaseViewHolder<User>(mBinding.root) {

            override fun bind(data: User) {
                mBinding.user = data
                Glide.with(mBinding.userImage.context).load(data.image).into(mBinding.userImage)
            }
        }

        class RepoViewHolder(private val mBinding: com.hyogeun.githubrepos.databinding.ListItemReposBinding) :
            BaseViewHolder<Repo>(mBinding.root) {

            override fun bind(data: Repo) {
                mBinding.repo = data
            }
        }
    }
}
