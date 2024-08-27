package com.example.accounting

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import com.example.accounting.MyDataBaseHelper
import com.example.accounting.databinding.FragmentSearchPageBinding
import com.example.accounting.`object`.RecordList
import com.example.accounting.setupEditText

/**
 * A simple [Fragment] subclass.
 * Use the [SearchPageFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SearchPageFragment : Fragment() {
    private lateinit var binding: FragmentSearchPageBinding
    private lateinit var dbHelper: MyDataBaseHelper
    private lateinit var listFragment: RecordListFragment

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding= FragmentSearchPageBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupEditText(binding.myEditText)
        dbHelper=MyDataBaseHelper(requireContext(), "recordData.db", 1)
        Log.d("FragmentSearchBinding",""+dbHelper.getAllRecords())
        RecordList.clear()
        binding.logoImageView.visibility=View.VISIBLE
        // 创建 RecordListFragment 实例
        listFragment = RecordListFragment()
        // 动态添加 Fragment
        childFragmentManager.beginTransaction()
            .replace(binding.RecordListFrag.id, listFragment)
            .commit()
        binding.RecordListFrag.visibility=View.INVISIBLE
        binding.resultSum.visibility=View.INVISIBLE
        binding.myEditText.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                // 处理回车事件
                Log.d("actionId","EditorInfo.IME_ACTION_DONE")
                sendRequest()
                true
            } else {
                false
            }
        }

        binding.mySearchBtn.setOnClickListener {
            sendRequest()
        }
    }

    private fun sendRequest(){
        if(binding.myEditText.text.toString().trim().isNotEmpty()){
            // 获取输入的文本
            Log.d("Name", "KeyWord:"+binding.myEditText.text.toString())
            val name=binding.myEditText.text.toString()
            // 获取翻译并更新 Fragment
            getResult(name){res->
                Log.d("getResult",res.toString())
                if(res==1){
                    // 获取到结果
                    listFragment.setMeanings(1)
                    requireActivity().runOnUiThread{
                        binding.inSum.text=RecordList.inSum.toString()
                        binding.outSum.text=RecordList.outSum.toString()
                        binding.resultSum.visibility=View.VISIBLE
                        binding.RecordListFrag.visibility=View.VISIBLE
                        binding.logoImageView.visibility = View.GONE
                    }
                }else{
                    // 无记录
                    requireActivity().runOnUiThread{
                        binding.logoImageView.visibility=View.VISIBLE
                        binding.resultSum.visibility=View.INVISIBLE
                        binding.RecordListFrag.visibility=View.INVISIBLE
                        Toast.makeText(requireContext(), "查找的姓名不存在记录", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun getResult(name:String, callback: (result: Int) -> Unit){
        val recordList=dbHelper.getSomeoneRecords(name)
        if(!recordList.isEmpty()){
            RecordList.records= recordList.toMutableList()
            RecordList.inSum=dbHelper.getTotalIncomeByName(name)
            RecordList.outSum=dbHelper.getTotalExpenditureByName(name)
            callback(1)
        }else{
            callback(0)
        }
    }
}