package com.example.accounting

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.accounting.MyDataBaseHelper
import com.example.accounting.R
import com.example.accounting.databinding.FragmentListPageBinding
import com.example.accounting.databinding.FragmentSearchPageBinding
import com.example.accounting.`object`.RecordList
import com.example.accounting.publicFunction.Companion.exportToExcel

/**
 * A simple [Fragment] subclass.
 * Use the [ListPageFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ListPageFragment : Fragment() {
    private lateinit var binding: FragmentListPageBinding
    private lateinit var dbHelper: MyDataBaseHelper

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding= FragmentListPageBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dbHelper=MyDataBaseHelper(requireContext(), "recordData.db", 1)
        binding.shareBtn.setOnClickListener {
            exportToExcel(requireContext())
        }
        RecordList.clear()
        childFragmentManager.beginTransaction()
            .replace(R.id.record_list_fragment, RecordListFragment())
            .commit()
    }

    override fun onResume() {
        // 获取 RecordListFragment 实例
        Log.d("ListPageFragment","onResume()")
        val recordListFragment = childFragmentManager.findFragmentById(R.id.record_list_fragment) as RecordListFragment?
        super.onResume()
        recordListFragment?.let {
            val recordList = dbHelper.getAllRecords()
            if (recordList.isNotEmpty()) {
                RecordList.records = recordList.toMutableList()
                it.setMeanings(1)
            }
        }
    }
}