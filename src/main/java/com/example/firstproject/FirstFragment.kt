package com.example.firstproject

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.firstproject.Api.ApiService
import com.example.firstproject.Api.SongData
import com.example.firstproject.databinding.FragmentFirstBinding
import com.squareup.picasso.Picasso
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class FirstFragment : Fragment() {

    private var _binding: FragmentFirstBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private val apiService = ApiService()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentFirstBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.buttonFirst.text = "获取热评配方"
        binding.buttonFirst.setOnClickListener {
            fetchSongData()
        }
    }

    private fun fetchSongData() {
        // 显示加载状态
        binding.textviewFirst.text = "正在获取数据..."
        
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val result = withContext(Dispatchers.IO) {
                    apiService.fetchSongData()
                }
                
                if (result.isSuccess) {
                    displaySongData(result.getOrNull()!!)
                } else {
                    binding.textviewFirst.text = "获取数据失败: ${result.exceptionOrNull()?.message}"
                }
            } catch (e: Exception) {
                binding.textviewFirst.text = "发生错误: ${e.message}"
                e.printStackTrace()
            }
        }
    }

    private fun displaySongData(songData: SongData) {
        binding.textviewFirst.text = """
            Song ID: ${songData.songId}
            Title: ${songData.title}
            Author: ${songData.author}
            Comment: ${songData.commentContent}
            Published Date: ${songData.commentPublishedDate}
        """.trimIndent()
        
        // 加载图片
        if (songData.imageUrl.isNotEmpty()) {
            Picasso.get()
                .load(songData.imageUrl)
                .placeholder(android.R.drawable.ic_menu_gallery)
                .error(android.R.drawable.ic_menu_gallery)
                .into(binding.imageView)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}