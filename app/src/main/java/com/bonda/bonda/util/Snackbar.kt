package com.bonda.bonda.util

import android.animation.LayoutTransition
import android.graphics.Paint
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isEmpty
import androidx.core.view.updatePadding
import com.bonda.bonda.R
import com.bonda.bonda.databinding.ViewSnackbarBinding

enum class SnackbarType { SAVE, BADGE, ERROR }

fun AppCompatActivity.showSnackbar(
    message: String,
    buttonText: String? = null,
    onButtonClick: (() -> Unit)? = null,
    type: SnackbarType
) {
    val snackbarBottomMargin = (12 * resources.displayMetrics.density).toInt()

    val root = findViewById<ViewGroup>(android.R.id.content)

    val containerTag = "SNACKBAR_CONTAINER"
    val container = (root.findViewWithTag<ViewGroup>(containerTag) ?: run {
        LinearLayout(this).apply {
            tag = containerTag
            orientation = LinearLayout.VERTICAL
            val lp = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                gravity = Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL
                bottomMargin = snackbarBottomMargin
            }
            root.addView(this, lp)

            val transition = LayoutTransition()
            transition.enableTransitionType(LayoutTransition.CHANGING)
            transition.setAnimator(LayoutTransition.APPEARING, null)
            transition.setAnimator(LayoutTransition.DISAPPEARING, null)
            this.layoutTransition = transition

            ViewCompat.requestApplyInsets(this)
            ViewCompat.setOnApplyWindowInsetsListener(this) { view, insets ->
                val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
                view.updatePadding(
                    systemBars.left,
                    systemBars.top,
                    systemBars.right,
                    systemBars.bottom
                )
                insets
            }
        }
    }) as LinearLayout

    val binding = ViewSnackbarBinding.inflate(layoutInflater, container, false)

    /**
     * text
     */
    binding.text.text = message

    /**
     * button text
     */
    if (buttonText.isNullOrBlank()) {
        binding.button.visibility = View.GONE

        val lp = binding.text.layoutParams as ConstraintLayout.LayoutParams
        lp.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID
        binding.text.layoutParams = lp
    }

    if (!buttonText.isNullOrBlank() && onButtonClick != null) {
        binding.button.apply {
            text = buttonText
            paintFlags = binding.button.paintFlags or Paint.UNDERLINE_TEXT_FLAG
            setOnClickListener { onButtonClick() }
        }
    }

    /**
     * icon & icon tint & background tint
     */
    when (type) {
        SnackbarType.SAVE -> {
            binding.icon.setImageResource(R.drawable.ic_notification_check_24dp)
        }

        SnackbarType.BADGE -> {
            binding.icon.setImageResource(R.drawable.ic_description_reward_24dp)
        }

        SnackbarType.ERROR -> {
            binding.root.apply {
                strokeWidth = (1 * resources.displayMetrics.density).toInt()
                strokeColor = ContextCompat.getColor(context, R.color.system_error_primary)
                backgroundTintList =
                    ContextCompat.getColorStateList(context, R.color.system_error_secondary)
            }
            binding.icon.apply {
                setImageResource(R.drawable.ic_notification_incomplete_24dp)
                imageTintList =
                    ContextCompat.getColorStateList(context, R.color.system_error_primary)
            }
            binding.text.apply {
                text = "저장에 실패했어요. 다시 시도해 주세요."
                setTextColor(ContextCompat.getColor(context, R.color.system_error_primary))
            }
        }
    }

    val childLp = LinearLayout.LayoutParams(
        LinearLayout.LayoutParams.WRAP_CONTENT,
        LinearLayout.LayoutParams.WRAP_CONTENT
    )
    childLp.topMargin = snackbarBottomMargin

    /**
     * 이미 표시중인 스낵바가 있는지 검사합니다
     */
    if (container.isEmpty()) {
        container.addView(binding.root, childLp)
        binding.root.startAnimation(
            AnimationUtils.loadAnimation(this, android.R.anim.fade_in)
        )
    } else {
        container.postDelayed({
            container.addView(binding.root, childLp)
            binding.root.startAnimation(
                AnimationUtils.loadAnimation(this, android.R.anim.fade_in)
            )
        }, 1000L)
    }

    /**
     * 애니메이션
     */
    binding.root.startAnimation(
        AnimationUtils.loadAnimation(this, android.R.anim.fade_in)
    )

    /**
     * disappearance animation
     */
    binding.root.postDelayed({
        binding.root.animate()
            .alpha(0f)
            .setDuration(300)
            .withEndAction {
                container.removeView(binding.root)
                if (container.isEmpty()) root.removeView(container)
            }
            .start()
    }, 2500)

}