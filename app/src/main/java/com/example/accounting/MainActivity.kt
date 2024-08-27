package com.example.accounting

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import com.example.accounting.databinding.ActivityMainBinding
import com.example.accounting.InsertPageFragment
import com.example.accounting.ListPageFragment
import com.example.accounting.SearchPageFragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var dbHelper:MyDataBaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
//         删除名为 "my_database.db" 的数据库
//        deleteDatabase(this, "recordData.db")
//        Log.d("MainActivity","deleteDatabase")
        dbHelper = MyDataBaseHelper(this, "recordData.db", 1)
//        dbHelper.insertRecord("张大千",1000,"买画",false, getCurrentDate().toString())
//        dbHelper.insertRecord("李明", 800, "旅行", false, "2024-01-15")
//        dbHelper.insertRecord("王芳", 1200, "餐饮", false, "2024-02-10")
//        dbHelper.insertRecord("张伟", 500, "书籍", false, "2024-03-05")
//        dbHelper.insertRecord("刘洋", 1500, "医疗", false, "2024-04-20")
//        dbHelper.insertRecord("陈静", 300, "娱乐", false, "2024-05-25")
//        dbHelper.insertRecord("杨帆", 2000, "房租", false, "2024-06-30")
//        dbHelper.insertRecord("吴强", 700, "健身", false, "2024-07-15")
//        dbHelper.insertRecord("赵婷", 1100, "教育", false, "2024-08-10")
//        dbHelper.insertRecord("许磊", 400, "交通", false, "2024-09-05")
//        dbHelper.insertRecord("孙莉", 600, "礼物", false, "2024-10-20")
//        Log.d("MainActivity","recordData created")

        loadFragment(SearchPageFragment())
        binding.bottomNavigation.setOnNavigationItemSelectedListener (
            BottomNavigationView.OnNavigationItemSelectedListener{
                when (it.itemId) {
                    R.id.nav_search -> {
                        loadFragment(SearchPageFragment())
                        return@OnNavigationItemSelectedListener true
                    }

                    R.id.nav_list -> {
                        loadFragment(ListPageFragment())
                        return@OnNavigationItemSelectedListener true
                    }

                    R.id.nav_insert -> {
                        loadFragment(InsertPageFragment())
                        return@OnNavigationItemSelectedListener true
                    }
                }
                false
            }
        )
    }

    override fun onDestroy() {
        dbHelper.close()
        super.onDestroy()
    }

    private fun loadFragment(fragment: Fragment){
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.container,fragment)
        transaction.commit()
    }

    fun deleteDatabase(context: Context, databaseName: String) {
        Log.d("deleteDatabase","deleteDatabase~~")
        context.deleteDatabase(databaseName)
        Log.d("deleteDatabase","deleteDatabase??")
    }
}