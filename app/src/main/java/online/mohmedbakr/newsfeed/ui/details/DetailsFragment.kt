package online.mohmedbakr.newsfeed.ui.details

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.*
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import online.mohmedbakr.newsfeed.databinding.FragmentDetailsBinding
import online.mohmedbakr.newsfeed.ui.MainActivity


private const val ARG_PARAM1 = "param1"


class DetailsFragment : Fragment() {
    private var param1: String = "null"
    private var _binding: FragmentDetailsBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1).toString()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val color = (activity as MainActivity).progressColor
        val args = arguments?.let { DetailsFragmentArgs.fromBundle(it) }
        args?.let {
            val circularProgressDrawable = CircularProgressDrawable(requireContext())
            circularProgressDrawable.strokeWidth = 10f
            circularProgressDrawable.setColorSchemeColors(color)
            circularProgressDrawable.centerRadius = 50f

            circularProgressDrawable.start()
            binding.loading.setImageDrawable(circularProgressDrawable)
            binding.webView.loadUrl(it.link)
            binding.webView.webViewClient = MyWebViewClient(binding.loading)
            binding.webView.settings.javaScriptEnabled = true
        }
    }
}

class MyWebViewClient(
    private val loading: ImageView,
) : WebViewClient() {
    override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
        super.onPageStarted(view, url, favicon)
        loading.visibility = View.GONE


    }

}