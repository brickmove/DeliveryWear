package com.ciot.deliverywear.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.widget.AppCompatEditText
import com.ciot.deliverywear.R
import com.ciot.deliverywear.ui.base.BaseFragment

class PwdFragment : BaseFragment() {
    private companion object {
        val TAG = PwdFragment::class.java.simpleName

        const val OPERATOR_NUM = 0

        const val OPERATOR_CLEAR = 1

        const val OPERATOR_BACK = 2

    }

    private var button0: Button? = null
    private var button1: Button? = null
    private var button2: Button? = null
    private var button3: Button? = null
    private var button4: Button? = null
    private var button5: Button? = null
    private var button6: Button? = null
    private var button7: Button? = null
    private var button8: Button? = null
    private var button9: Button? = null
    private var btnReturn: ImageView? = null
    private var btnClear: ImageView? = null
    private var editTextView: AppCompatEditText? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_bind_code, container, false)
        initView(view)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initListener()
    }

    private fun initView(view: View?) {
        button0 = view?.findViewById(R.id.button0)
        button1 = view?.findViewById(R.id.button1)
        button2 = view?.findViewById(R.id.button2)
        button3 = view?.findViewById(R.id.button3)
        button4 = view?.findViewById(R.id.button4)
        button5 = view?.findViewById(R.id.button5)
        button6 = view?.findViewById(R.id.button6)
        button7 = view?.findViewById(R.id.button7)
        button8 = view?.findViewById(R.id.button8)
        button9 = view?.findViewById(R.id.button9)
        btnReturn = view?.findViewById(R.id.return_img)
        btnClear = view?.findViewById(R.id.clear_img)
        editTextView = view?.findViewById(R.id.tv_enter_password)
    }

    private fun initListener() {
        button0?.setOnClickListener { operatorEdit(OPERATOR_NUM, 0) }

        button1?.setOnClickListener { operatorEdit(OPERATOR_NUM, 1) }

        button2?.setOnClickListener { operatorEdit(OPERATOR_NUM, 2) }

        button3?.setOnClickListener { operatorEdit(OPERATOR_NUM, 3) }

        button4?.setOnClickListener { operatorEdit(OPERATOR_NUM, 4) }

        button5?.setOnClickListener { operatorEdit(OPERATOR_NUM, 5) }

        button6?.setOnClickListener { operatorEdit(OPERATOR_NUM, 6) }

        button7?.setOnClickListener { operatorEdit(OPERATOR_NUM, 7) }

        button8?.setOnClickListener { operatorEdit(OPERATOR_NUM, 8) }

        button9?.setOnClickListener { operatorEdit(OPERATOR_NUM, 9) }

        btnReturn?.setOnClickListener { operatorEdit(OPERATOR_BACK, -1) }

        btnClear?.setOnClickListener { operatorEdit(OPERATOR_CLEAR, -1) }
    }

    /**
     * 处理键盘输入
     * [type] 输入类型
     * [num] 数值
     */
    private fun operatorEdit(type: Int, num: Int) {
        when (type) {
            OPERATOR_NUM -> {
                editTextView?.append(num.toString())
            }
            OPERATOR_BACK -> {
                //editTextView.back()
            }
            OPERATOR_CLEAR -> {
                val selectionStart: Int? = editTextView?.selectionStart
                if (selectionStart != null) {
                    if (selectionStart > 0) {
                        editTextView?.getText()?.delete(selectionStart - 1, selectionStart);
                    }
                }
            }
        }
    }

}