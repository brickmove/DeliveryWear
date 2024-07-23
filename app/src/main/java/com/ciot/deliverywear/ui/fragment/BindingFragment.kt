package com.ciot.deliverywear.ui.fragment

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.os.CountDownTimer
import android.text.InputFilter
import android.text.InputType
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import com.ciot.deliverywear.R
import com.ciot.deliverywear.network.RetrofitManager
import com.ciot.deliverywear.ui.MainActivity
import com.ciot.deliverywear.ui.base.BaseFragment
import com.ciot.deliverywear.ui.dialog.BindingSuccessDialog
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import okhttp3.ResponseBody

class BindingFragment : BaseFragment() {
    private companion object {
        val TAG = "BindingFragment"
        const val OPERATOR_NUM = 0
        const val OPERATOR_CLEAR = 1
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
    private var gotItButton: Button? = null
    private var btnClear: ImageView? = null
    private var editTextView: AppCompatEditText? = null
    private var editHint: AppCompatTextView? = null
    private var isEdit: Boolean = false
    private val gotItDrawable1 = GradientDrawable()
    private val gotItDrawable2 = GradientDrawable()
    private var errorHint: TextView? = null
    private var shake: AnimatorSet? = null
    private var countdownTimer: CountDownTimer? = null
    private var mLoginDisposable: CompositeDisposable? = null

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
        gotItButton = view?.findViewById(R.id.button_got_it)
        btnClear = view?.findViewById(R.id.clear_img)
        editTextView = view?.findViewById(R.id.tv_enter_password)
        editHint = view?.findViewById(R.id.ed_hint)
        errorHint = view?.findViewById(R.id.error_pwd_hint)

        // 设置输入类型为数字
        editTextView?.inputType = InputType.TYPE_CLASS_NUMBER
        // 限制输入长度为8位
        val inputFilter: InputFilter = InputFilter.LengthFilter(8)
        editTextView?.filters = arrayOf(inputFilter)

        // 设置提交按钮背景
        gotItDrawable1.setColor(Color.parseColor("#FFB500"))
        gotItDrawable1.cornerRadius = 20f
        gotItDrawable2.setColor(Color.parseColor("#26FFFFFF"))
        gotItDrawable2.cornerRadius = 19.39f

        // 创建错误提示抖动动画
        shake = errorHint?.let { createShakeAnimation(it) }
    }

    private fun startCountdownAndClose() {
        countdownTimer = object : CountDownTimer(3000, 1000) {
            override fun onTick(millisUntilFinished: Long) {

            }

            override fun onFinish() {
                errorHint?.visibility = View.GONE
            }
        }.start()
    }

    private fun createShakeAnimation(view: TextView): AnimatorSet {
        val shakeSet = AnimatorSet()

        // 创建X轴上的平移动画
        val shakeX = ObjectAnimator.ofFloat(view, View.TRANSLATION_X, -0.8f, 0.8f)
        shakeX.repeatCount = 6
        shakeX.repeatMode = ObjectAnimator.REVERSE // 动画结束时反向播放
        shakeX.duration = 100

        // 添加动画监听器，在动画结束时启动计时器
        shakeX.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                super.onAnimationEnd(animation)
                startCountdownAndClose()
            }
        })

        shakeSet.play(shakeX)
        return shakeSet
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

        btnClear?.setOnClickListener { operatorEdit(OPERATOR_CLEAR, -1) }

        gotItButton?.setOnClickListener{
            if (editTextView?.text?.length == 8) {
                RetrofitManager.instance.setWuHanPassWord(editTextView!!.text.toString())
                RetrofitManager.instance.firstLogin()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(object : Observer<ResponseBody> {
                        override fun onSubscribe(d: Disposable) {
                            addSubscription(d)
                        }

                        override fun onNext(body: ResponseBody) {
                            RetrofitManager.instance.parseLoginResponseBody(body)
                            showSuccessDialog()
                            (activity as MainActivity).setBindInfo(editTextView!!.text.toString(), RetrofitManager.instance.getDefaultServer())
                        }

                        override fun onError(e: Throwable) {
                            Log.w(TAG,"登录失败 onError: ${e.message}")
                            errorHint?.visibility = View.VISIBLE
                            shake?.start()
                            if (editTextView?.text?.length == 8) {
                                editTextView?.setTextColor(ContextCompat.getColor(activity?.applicationContext!!, R.color.state_red))
                            }
                        }

                        override fun onComplete() {

                        }
                    })
            }
        }
    }

    // 绑定成功弹窗
    private var mBindingSuccessDialog: BindingSuccessDialog? = null
    private fun showSuccessDialog() {
        if (mBindingSuccessDialog?.dialog?.isShowing != true) {
            mBindingSuccessDialog = BindingSuccessDialog()
            mBindingSuccessDialog?.show(childFragmentManager, "ScanBindingSuccessDialog")
            Log.d(TAG,"showSuccessDialog>>>>>>")
        }
    }
    private fun dismissSuccessDialog() {
        if (mBindingSuccessDialog?.dialog?.isShowing == true) {
            mBindingSuccessDialog?.dismiss()
        }
    }

    /**
     * 处理键盘输入
     * [type] 输入类型
     * [num] 数值
     */
    private fun operatorEdit(type: Int, num: Int) {
        if (!isEdit) {
            editTextView?.setText("")
            editTextView?.visibility = View.VISIBLE
            btnClear?.visibility = View.VISIBLE
            editHint?.visibility = View.GONE
            editTextView?.setTextColor(ContextCompat.getColor(activity?.applicationContext!!, R.color.white))
            isEdit = true
        }
        when (type) {
            OPERATOR_NUM -> {
                editTextView?.append(num.toString())
                if (editTextView?.text?.length == 8) {
                    gotItButton?.background = gotItDrawable1
                    gotItButton?.setTextColor(ContextCompat.getColor(activity?.applicationContext!!, R.color.white))
                }
            }
            OPERATOR_CLEAR -> {
                val selectionStart: Int? = editTextView?.selectionStart
                if (selectionStart != null) {
                    if (selectionStart > 0) {
                        editTextView?.getText()?.delete(selectionStart - 1, selectionStart);
                    }
                }
                if (editTextView?.text?.length!! < 8) {
                    gotItButton?.background = gotItDrawable2
                    gotItButton?.setTextColor(ContextCompat.getColor(activity?.applicationContext!!, R.color.default_got_it))
                    editTextView?.setTextColor(ContextCompat.getColor(activity?.applicationContext!!, R.color.white))
                }
            }
        }
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (hidden) {
            onUnsubscribe()
            initViewData()
            isEdit = false
            countdownTimer?.cancel()
            dismissSuccessDialog()
            errorHint?.visibility = View.GONE
        }
    }

    private fun initViewData() {
        editHint?.visibility = View.VISIBLE
        editTextView?.setText("")
        editTextView?.visibility = View.GONE
        editTextView?.setTextColor(ContextCompat.getColor(activity?.applicationContext!!, R.color.white))
        btnClear?.visibility = View.GONE
        gotItButton?.background = gotItDrawable2
        gotItButton?.setTextColor(ContextCompat.getColor(activity?.applicationContext!!, R.color.default_got_it))
    }

    private fun onUnsubscribe() {
        mLoginDisposable?.clear()
    }

    fun addSubscription(disposable: Disposable?) {
        if (mLoginDisposable == null) {
            mLoginDisposable = CompositeDisposable()
        }
        disposable?.let { mLoginDisposable?.add(it) }
    }
}