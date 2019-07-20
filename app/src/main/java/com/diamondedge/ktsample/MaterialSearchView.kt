package com.diamondedge.ktsample

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.Context
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.graphics.drawable.Drawable
import android.os.Build
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewAnimationUtils
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import timber.log.Timber

/*
    based on code from https://github.com/EugeneHoran/Android-Material-SearchView as of 10/10/18
    no copyright or license listed
 */

class MaterialSearchView : CardView {

    private var animateSearchView: Boolean = false
    private var searchMenuPosition: Int = 0
    private var searchHint: String? = null
    private var searchTextColor: Int = 0
    private var searchIconColor: Int = 0
    private var mOldQueryText: CharSequence? = null
    private var hasAdapter = false
    private var hideSearch = false

    lateinit var editText: EditText
    lateinit var imageBack: ImageView
    lateinit var imageClear: ImageView
    lateinit var recyclerView: RecyclerView
    lateinit var suggestionsContainer: View

    private var listenerQuery: OnQueryTextListener? = null
    private var visibilityListener: OnVisibilityListener? = null

    /**
     * Callback to watch the text field for empty/non-empty
     */
    private val mTextWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence, start: Int, before: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, after: Int) {
            submitText(s)
        }

        override fun afterTextChanged(s: Editable) {}
    }

    private val mOnEditorActionListener = TextView.OnEditorActionListener { _, _, _ ->
        val consumed = onSubmitQuery()
        if (!consumed)
            editText.hideKeyboard()
        consumed
    }

    val query: CharSequence
        get() = editText.text

    /**
     * The IME options set on the query text field.
     */
    var imeOptions: Int
        get() = editText.imeOptions
        set(imeOptions) {
            editText.imeOptions = imeOptions
        }

    /**
     * The input type set on the query text field.
     */
    var inputType: Int
        get() = editText.inputType
        set(inputType) {
            editText.inputType = inputType
        }

    val isVisible: Boolean
        get() = visibility == View.VISIBLE

    // text color
    var textColor: Int
        get() = searchTextColor
        set(textColor) {
            this.searchTextColor = textColor
            invalidate()
            requestFocus()
        }

    constructor(context: Context) : super(context) {
        init(context, null)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init(context, attrs)
    }

    private fun init(context: Context, attrs: AttributeSet?) {
        val a = context.theme.obtainStyledAttributes(attrs, R.styleable.MaterialSearchView, 0, 0)

        LayoutInflater.from(context).inflate(R.layout.view_search, this, true)
        animateSearchView = a.getBoolean(R.styleable.MaterialSearchView_search_animate, true)
        searchMenuPosition = a.getInteger(R.styleable.MaterialSearchView_search_menu_position, 0)
        searchHint = a.getString(R.styleable.MaterialSearchView_search_hint)
        searchTextColor =
            a.getColor(R.styleable.MaterialSearchView_search_text_color, resources.getColor(android.R.color.black))
        searchIconColor =
            a.getColor(R.styleable.MaterialSearchView_search_icon_color, resources.getColor(android.R.color.black))

        editText = findViewById(R.id.editText)
        imageBack = findViewById(R.id.search_back_button)
        imageClear = findViewById(R.id.clear_icon)
        recyclerView = findViewById(R.id.search_suggestions)
        suggestionsContainer = findViewById(R.id.suggestionsCont)
        editText.addTextChangedListener(mTextWatcher)
        editText.setOnEditorActionListener(mOnEditorActionListener)

        val imeOptions = a.getInt(R.styleable.MaterialSearchView_android_imeOptions, -1)
        if (imeOptions != -1) {
            this.imeOptions = imeOptions
        }

        val inputType = a.getInt(R.styleable.MaterialSearchView_android_inputType, -1)
        if (inputType != -1) {
            this.inputType = inputType
        }

        val focusable: Boolean
        focusable = a.getBoolean(R.styleable.MaterialSearchView_search_focusable, true)
        editText.isFocusable = focusable

        a.recycle()

        editText.hint = getSearchHint()
        editText.setTextColor(textColor)
        setDrawableTint(imageBack.drawable, searchIconColor)
        setDrawableTint(imageClear.drawable, searchIconColor)
        checkForAdapter()

        imageClear.setOnClickListener {
            setSearchText(null)
            editText.showKeyboard(View.FOCUS_LEFT)
        }
    }

    private fun submitText(s: CharSequence) {
        Timber.d("submitText(%s) %s", s, editText.text)
        updateClearButton()
        if (!TextUtils.equals(s, mOldQueryText)) {
            listenerQuery?.onQueryTextChange(s.toString())
        }
        mOldQueryText = s.toString()
    }

    fun onSubmitQuery(): Boolean {
        if (listenerQuery != null) {
            suggestionsContainer.visibility = View.GONE
            return listenerQuery?.onQueryTextSubmit(editText.text.toString()) ?: false
        }
        return false
    }

    private fun updateClearButton() {
        val hasText = !TextUtils.isEmpty(editText.text)
        imageClear.visibility = if (hasText) View.VISIBLE else View.GONE
    }

    fun setQuery(query: String?, submit: Boolean) {
        val text = editText.text
        Timber.d("setQuery(%s, %s) current: %s", query, submit, text)
        val queryChanged = if (text.isNullOrEmpty() && query.isNullOrEmpty())
            false
        else if (text.isNullOrEmpty())
            true
        else
            text.toString() != query

        if (queryChanged) {
            editText.setText(query, TextView.BufferType.EDITABLE)
            // If the query is not empty and submit is requested, submit the query
            if (submit && !TextUtils.isEmpty(query)) {
                onSubmitQuery()
            }
        }
    }

    private fun setSearchText(queryText: String?) {
        editText.setText(queryText)
    }

    fun setSearchSuggestionsAdapter(adapter: RecyclerView.Adapter<*>) {
        recyclerView.adapter = adapter
        checkForAdapter()
    }

    fun hideSuggestions() {
        Timber.d("hideSuggestions()")
        hideSearch = true
        suggestionsContainer.visibility = View.GONE
    }

    fun showSuggestions() {
        Timber.d("showSuggestions()")
        hideSearch = false
        suggestionsContainer.visibility = View.VISIBLE
    }

    private val centerX: Int
        get() {
            // TODO not correct but close
            val icons = (width - convertDpToPixel((21 * (1 + searchMenuPosition)).toFloat())).toInt()
            val padding = convertDpToPixel((searchMenuPosition * 21).toFloat()).toInt()
            return icons - padding
        }

    fun showBackButton(listener: OnClickListener) {
        imageBack.visibility = View.VISIBLE
        imageBack.setOnClickListener(listener)
    }

    fun showBackButtonAsHideSearch(listener: OnClickListener) {
        showBackButton(OnClickListener {
            hideSearch()
        })
    }

/*
    fun showMenuButton(listener: OnClickListener) {
        imageBack.visibility = View.VISIBLE
        imageBack.setImageResource(R.drawable.ic_menu)
        imageBack.setOnClickListener(listener)
    }
*/

    fun showSearch() {
        hideSearch = false
        checkForAdapter()
        visibility = View.VISIBLE
        if (animateSearchView)
            if (Build.VERSION.SDK_INT >= 21) {
                val animatorShow = ViewAnimationUtils.createCircularReveal(
                    this, // view
                    centerX, // center x
                    convertDpToPixel(23f).toInt(), // center y
                    0f, // start radius
                    Math.hypot(width.toDouble(), height.toDouble()).toFloat() // end radius
                )
                animatorShow.addListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        super.onAnimationEnd(animation)
                        visibilityListener?.onOpen()
                        if (hasAdapter) {
                            suggestionsContainer.visibility = View.VISIBLE
                        }
                    }
                })
                animatorShow.start()
            } else {
                if (hasAdapter) {
                    suggestionsContainer.visibility = View.VISIBLE
                }
            }
    }

    fun hideSearch() {
        checkForAdapter()
        if (hasAdapter) {
            suggestionsContainer.visibility = View.GONE
        }
        if (animateSearchView) {
            if (Build.VERSION.SDK_INT >= 21) {
                val animatorHide = ViewAnimationUtils.createCircularReveal(
                    this, // View
                    centerX, // center x
                    convertDpToPixel(23f).toInt(), // center y
                    Math.hypot(width.toDouble(), height.toDouble()).toFloat(), // start radius
                    0f// end radius
                )
                animatorHide.startDelay = if (hasAdapter) ANIMATION_DURATION else 0L
                animatorHide.addListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        super.onAnimationEnd(animation)
                        visibilityListener?.onClose()
                        visibility = View.GONE
                    }
                })
                animatorHide.start()
            } else {
                visibility = View.GONE
            }
        }
    }

    fun setMenuPosition(menuPosition: Int) {
        this.searchMenuPosition = menuPosition
        invalidate()
        requestFocus()
    }

    fun getSearchHint(): String = searchHint ?: "Search"

    fun setSearchHint(searchHint: String) {
        this.searchHint = searchHint
        invalidate()
        requestFocus()
    }

    fun setSearchIconColor(searchIconColor: Int) {
        this.searchIconColor = searchIconColor
        invalidate()
        requestFocus()
    }

    fun setQueryTextListener(listenerQuery: OnQueryTextListener) {
        this.listenerQuery = listenerQuery
    }

    interface OnQueryTextListener {
        fun onQueryTextSubmit(query: String): Boolean
        fun onQueryTextChange(newText: String): Boolean
    }

    fun setOnVisibilityListener(visibilityListener: OnVisibilityListener) {
        this.visibilityListener = visibilityListener
    }

    interface OnVisibilityListener {
        fun onOpen(): Boolean
        fun onClose(): Boolean
    }

    fun setDrawableTint(resDrawable: Drawable, resColor: Int) {
        resDrawable.colorFilter = PorterDuffColorFilter(resColor, PorterDuff.Mode.SRC_ATOP)
        resDrawable.mutate()
    }

    private fun convertDpToPixel(dp: Float): Float {
        return dp * (context.resources.displayMetrics.densityDpi / 160f)
    }

    private fun checkForAdapter() {
        val adapter = recyclerView.adapter
        hasAdapter = if (adapter == null) false else !hideSearch && adapter.itemCount > 0
    }

    companion object {
        internal val TAG = "MaterialSearchView"
        private val ANIMATION_DURATION = 250L
    }
}
