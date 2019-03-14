package com.hyogeun.githubrepos.ui.main

import android.content.Context
import android.content.Intent
import android.os.Bundle
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
        getRepos(userName)
    }

    private fun getUser(userName: String) {
        RestClient.service.user(userName).enqueue(object: Callback<User> {
            override fun onFailure(call: Call<User>, t: Throwable) {
            }

            override fun onResponse(call: Call<User>, response: Response<User>?) {
                if(response?.isSuccessful == true) {
                    response.body()?.let { mRepos.add(0, it) }
                    mAdapter.notifyDataSetChanged()
                }
            }
        })
    }

    private fun getRepos(userName: String) {
        RestClient.service.repos(userName).enqueue(object: Callback<ArrayList<Repo>> {
            override fun onFailure(call: Call<ArrayList<Repo>>, t: Throwable) {
            }

            override fun onResponse(call: Call<ArrayList<Repo>>, response: Response<ArrayList<Repo>>?) {
                if(response?.isSuccessful == true) {
                    response.body()?.let { mRepos.addAll(it) }
                    mAdapter.notifyDataSetChanged()
                }
            }
        })
    }

    class GithubReposAdapter(private val mRepos: ArrayList<in BaseModel>) : RecyclerView.Adapter<BaseViewHolder<out BaseModel>>() {

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
            if(getItemViewType(position) == USER) {
                (holder as UserViewHolder).bind(mRepos[position] as User)
            } else {
                (holder as RepoViewHolder).bind(mRepos[position] as Repo)
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
