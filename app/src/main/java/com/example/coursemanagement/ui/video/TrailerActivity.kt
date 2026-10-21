package com.example.coursemanagement.ui.video

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.example.coursemanagement.databinding.ActivityTrailerBinding
import com.example.coursemanagement.util.CourseValidator

/** Media3 ExoPlayer: phát trailer trong ứng dụng, không mở trình duyệt ngoài. */
class TrailerActivity : AppCompatActivity() {
    private lateinit var binding: ActivityTrailerBinding
    private var player: ExoPlayer? = null
    private val videoUrl by lazy { intent.getStringExtra(EXTRA_URL).orEmpty() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTrailerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.toolbar.title = intent.getStringExtra(EXTRA_TITLE) ?: "Video trailer"
        binding.toolbar.setNavigationOnClickListener { finish() }
    }

    override fun onStart() {
        super.onStart()
        if (!CourseValidator.isHttpUrl(videoUrl)) {
            binding.textStatus.text = "URL video không hợp lệ."
            return
        }
        val createdPlayer = ExoPlayer.Builder(this).build()
        player = createdPlayer
        binding.playerView.player = createdPlayer
        createdPlayer.addListener(object : Player.Listener {
            override fun onPlaybackStateChanged(playbackState: Int) {
                if (playbackState == Player.STATE_READY) binding.textStatus.visibility = View.GONE
            }

            override fun onPlayerError(error: PlaybackException) {
                binding.textStatus.visibility = View.VISIBLE
                binding.textStatus.text = "Không phát được trailer. Hãy dùng URL MP4 trực tiếp hợp lệ.\n${error.errorCodeName}"
            }
        })
        createdPlayer.setMediaItem(MediaItem.fromUri(videoUrl))
        createdPlayer.prepare()
        createdPlayer.playWhenReady = true
    }

    override fun onStop() {
        binding.playerView.player = null
        player?.release()
        player = null
        super.onStop()
    }

    companion object {
        const val EXTRA_URL = "trailerUrl"
        const val EXTRA_TITLE = "title"
    }
}
