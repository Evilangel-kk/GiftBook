package com.example.accounting

import android.graphics.Paint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.accounting.MyDataBaseHelper
import com.example.accounting.databinding.FragmentInsertPageBinding
import com.loper7.date_time_picker.DateTimePicker
import com.loper7.date_time_picker.dialog.CardDatePickerDialog
import java.lang.Integer.parseInt
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.Date
import java.util.Locale


/**
 * A simple [Fragment] subclass.
 * Use the [InsertPageFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class InsertPageFragment : Fragment() {
    private lateinit var binding: FragmentInsertPageBinding
    private lateinit var dbHelper: MyDataBaseHelper
    private lateinit var selectedDate:String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding= FragmentInsertPageBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dbHelper=MyDataBaseHelper(requireContext(), "recordData.db", 1)
        binding.submit.setOnClickListener {
            submit()
        }

        binding.clickChooseDate.paintFlags= Paint.UNDERLINE_TEXT_FLAG

        binding.clickChooseDate.setOnClickListener {
            val datePickerDialog = CardDatePickerDialog.builder(requireActivity())
                .setTitle("选择日期")
                .showBackNow(true)
                .showDateLabel(true)
                .showFocusDateInfo(true)
                .setLabelText("年","月","日")
                .setOnChoose("选择",object :CardDatePickerDialog.OnChooseListener{
                    override fun onChoose(millisecond: Long) {
                        // 将毫秒转换为 Date 对象
                        val date = Date(millisecond)
                        // 定义日期格式
                        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                        // 格式化日期
                        selectedDate = dateFormat.format(date)
                        // 输出格式化后的日期
                        Log.d("DatePicker", "Selected date: $selectedDate")
                        binding.selectedDate.text=selectedDate
                    }
                })
            datePickerDialog.setDisplayType(intArrayOf(
                DateTimePicker.YEAR,//显示年
                DateTimePicker.MONTH,//显示月
                DateTimePicker.DAY).toMutableList())//显示日
            datePickerDialog.build().show()
        }

    }

    private fun submit(){
        val name=binding.editTextName.text.toString()
        val price=binding.editTextAmount.text.toString()
        val inORout= binding.radioGroupType.checkedRadioButtonId==binding.radioIncome.id
        val event=binding.editTextEvent.text.toString()
        val date=binding.selectedDate.text.toString()
        if (name.isEmpty()) {
            Toast.makeText(requireContext(), "对象姓名不能为空", Toast.LENGTH_SHORT).show()
        } else if (price.isEmpty()) {
            Toast.makeText(requireContext(), "钱数不能为空", Toast.LENGTH_SHORT).show()
        } else if (binding.radioGroupType.checkedRadioButtonId == -1) { // 如果有初始化问题，可以检查
            Toast.makeText(requireContext(), "请选择类型", Toast.LENGTH_SHORT).show()
        } else if (event.isEmpty()) {
            Toast.makeText(requireContext(), "事件不能为空", Toast.LENGTH_SHORT).show()
        } else if (date=="年-月-日") {
            Toast.makeText(requireContext(), "请选择日期", Toast.LENGTH_SHORT).show()
        } else {
            // 如果所有字段都不为空，可以继续处理数据
            dbHelper.insertRecord(name,parseInt(price),event,inORout,date)
            Toast.makeText(requireContext(), "添加新纪录成功！", Toast.LENGTH_SHORT).show()
            binding.editTextName.text.clear()
            binding.editTextAmount.text.clear()
            binding.radioGroupType.clearCheck()
            binding.editTextEvent.text.clear()
            binding.selectedDate.setText("年-月-日")
        }
    }
}