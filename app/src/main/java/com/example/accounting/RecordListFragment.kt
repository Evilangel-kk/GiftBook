package com.example.accounting

import android.annotation.SuppressLint
import android.graphics.Paint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.accounting.MyDataBaseHelper
import com.example.accounting.Record
import com.example.accounting.databinding.FragmentRecordContentBinding
import com.example.accounting.databinding.FragmentRecordListBinding
import com.example.accounting.`object`.RecordList

/**
 * A simple [Fragment] subclass.
 * Use the [RecordListFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class RecordListFragment : Fragment() {
    private lateinit var binding: FragmentRecordListBinding
    private lateinit var dbHelper: MyDataBaseHelper

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding= FragmentRecordListBinding.inflate(inflater,container,false)
        Log.d("RecordListFragment", "onCreateView called")
        return binding.root
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val layoutManager = LinearLayoutManager(activity)
        binding.recordListRecyclerView.layoutManager=layoutManager
        dbHelper= MyDataBaseHelper(requireActivity(), "recordData.db", 1)
        // 配置适配器
        val adapter = ResultAdapter(RecordList.records)
        binding.recordListRecyclerView.adapter = adapter
    }

    override fun onResume() {
        super.onResume()
        Log.d("RecordListFragment","onResume()")
        refreshData()
    }

    private fun refreshData() {
        // 实现刷新逻辑
        val recordList = dbHelper.getAllRecords()
        if (recordList.isNotEmpty()) {
            RecordList.records = recordList.toMutableList()
            Log.d("refreshData",RecordList.records.size.toString())
            setMeanings(1)
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setMeanings(result:Int) {
        if(result==1){
            binding.recordListRecyclerView.adapter = null
            binding.recordListRecyclerView.adapter = ResultAdapter(RecordList.records)
            (binding.recordListRecyclerView.adapter as? ResultAdapter)?.updateMeanings(1)
            Log.d("Notify",RecordList.records.size.toString())
        }
    }

    inner class ResultAdapter(private var recordList: MutableList<Record>) :
        RecyclerView.Adapter<ResultAdapter.ViewHolder>() {
        private lateinit var binding: FragmentRecordContentBinding

        @SuppressLint("NotifyDataSetChanged")
        fun updateMeanings(result:Int) {
            if(result==1){
                requireActivity().runOnUiThread{
                    recordList=RecordList.records
                    notifyDataSetChanged()
                    Log.d("notifyDataSetChanged",""+RecordList.records)
                }
            }
        }

        inner class ViewHolder(binding: FragmentRecordContentBinding) : RecyclerView.ViewHolder(binding.root) {
//            val Id=binding.resId
            val Name=binding.resName
            val Price=binding.resPrice
            val Event=binding.resEvent
            val InORout=binding.resInORout
            val Date=binding.resDate
            val Delete=binding.delete
        }
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            binding= FragmentRecordContentBinding.inflate(LayoutInflater.from(parent.context),parent,false)
            val holder = ViewHolder(binding)
            return holder
        }
        @SuppressLint("DiscouragedApi")
        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val record = recordList[position]
//            holder.Id.text=record.id
            holder.Name.text=record.name
            holder.Price.text=record.price.toString()
            holder.InORout.text=if (record.inORout) "收入" else "支出"
            holder.Event.text=record.event
            holder.Date.text=record.date
            holder.Delete.paintFlags= Paint.UNDERLINE_TEXT_FLAG
            holder.Delete.setOnClickListener {
                // 创建一个确认对话框
                AlertDialog.Builder(requireActivity())
                    .setTitle("确认删除")
                    .setMessage("确定要删除这条记录吗？")
                    .setPositiveButton("确定") { _, _ ->
                        // 用户点击"确定"按钮后的处理逻辑
                        // 在这里处理删除记录的操作
                        Log.d("AlertDialog","确认删除")
                        dbHelper.deleteRecord(record.name,record.price,record.event,record.inORout,record.date)

                        // 更新List
                        // 从数据源中删除记录
                        recordList.removeAt(position)
                        // 通知适配器项已删除
                        notifyItemRemoved(position)
                        Toast.makeText(requireContext(), "删除成功", Toast.LENGTH_SHORT).show()
                    }
                    .setNegativeButton("取消", null)  // 用户点击"取消"按钮时关闭对话框
                    .show()  // 显示对话框
            }
            Log.d("refreshDataHolder", "Binding view for position $position with data: ${recordList[position]}")
        }
        override fun getItemCount() = recordList.size
    }
}