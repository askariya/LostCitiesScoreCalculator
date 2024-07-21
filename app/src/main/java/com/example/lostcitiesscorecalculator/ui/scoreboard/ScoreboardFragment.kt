package com.example.lostcitiesscorecalculator.ui.scoreboard

import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.GridLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.get
import com.example.lostcitiesscorecalculator.R
import com.example.lostcitiesscorecalculator.databinding.FragmentScoreboardBinding

class ScoreboardFragment : Fragment() {

    private var _binding: FragmentScoreboardBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private lateinit var sharedScoreViewModel: SharedScoreViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val scoreboardViewModel =
            ViewModelProvider(this).get(ScoreboardViewModel::class.java)

        _binding = FragmentScoreboardBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // set the submitButton functionality
        val submitScoreButton : Button = binding.submitScoreButton
        submitScoreButton.setOnClickListener{
            //TODO display a warning here and ask user to confirm (or do this in SharedScoreViewModel)
            sharedScoreViewModel.submitCurrentPointsToTotal()
        }

        // set the resetButton functionality
        val endGameButton : Button = binding.endGameButton
        endGameButton.setOnClickListener{
            sharedScoreViewModel.resetGame()
        }

        sharedScoreViewModel = ViewModelProvider(requireActivity()).get()

        sharedScoreViewModel.player1CurrentPoints.observe(viewLifecycleOwner, player1CurrentScoreObserver)
        sharedScoreViewModel.player2CurrentPoints.observe(viewLifecycleOwner, player2CurrentScoreObserver)
        sharedScoreViewModel.player1TotalPoints.observe(viewLifecycleOwner, player1TotalScoreObserver)
        sharedScoreViewModel.player2TotalPoints.observe(viewLifecycleOwner, player2TotalScoreObserver)
        sharedScoreViewModel.roundScores.observe(viewLifecycleOwner, roundScoreObserver)

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun addNewRound(roundCount: Int, player1RoundScore: Int, player2RoundScore: Int) {
        // Create TextView for Round number
        val textSize = 20f
        val textColor = ContextCompat.getColor(requireContext(), R.color.white)
        val colorPrimary = ContextCompat.getColor(requireContext(), R.color.color_primary)
        val colorPrimaryVariant = ContextCompat.getColor(requireContext(), R.color.color_primary_variant)
        val colorSecondary = ContextCompat.getColor(requireContext(), R.color.color_secondary)
        var backgroundColor = colorSecondary
        val spacingInDp = 10 // Desired spacing in dp
        val spacingInPx = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            spacingInDp.toFloat(),
            resources.displayMetrics
        ).toInt()

        if (player1RoundScore > player2RoundScore)
            backgroundColor = ContextCompat.getColor(requireContext(), R.color.player1_colour)
        else if (player2RoundScore > player1RoundScore)
            backgroundColor = ContextCompat.getColor(requireContext(), R.color.player2_colour)

        val roundNumber = TextView(requireContext()).apply {
            layoutParams = GridLayout.LayoutParams().apply {
                width = 0
                height = ViewGroup.LayoutParams.WRAP_CONTENT
                columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 0.3f)
                bottomMargin = spacingInPx
            }
            text = roundCount.toString()
            setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize)
            setTextColor(textColor)
            setBackgroundColor(colorPrimaryVariant)
            setTypeface(typeface, android.graphics.Typeface.BOLD)
            gravity = android.view.Gravity.CENTER
        }

        // Create TextView for Player 1 Score
        val player1ScoreView = TextView(requireContext()).apply {
            layoutParams = GridLayout.LayoutParams().apply {
                width = 0
                height = ViewGroup.LayoutParams.WRAP_CONTENT
                columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 0.35f)
                bottomMargin = spacingInPx
            }
            text = player1RoundScore.toString()
            setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize)
            setTextColor(textColor)
            setBackgroundColor(backgroundColor)
            gravity = android.view.Gravity.CENTER
        }

        // Create TextView for Player 2 Score
        val player2ScoreView = TextView(requireContext()).apply {
            layoutParams = GridLayout.LayoutParams().apply {
                width = 0
                height = ViewGroup.LayoutParams.WRAP_CONTENT
                columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 0.35f)
                bottomMargin = spacingInPx
            }
            text = player2RoundScore.toString()
            setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize)
            setTextColor(textColor)
            setBackgroundColor(backgroundColor)
            gravity = android.view.Gravity.CENTER
        }

        // Add the views to the GridLayout
        val gridLayout = binding.scoreGridLayout
        gridLayout.addView(roundNumber)
        gridLayout.addView(player1ScoreView)
        gridLayout.addView(player2ScoreView)
        checkEmptyViewVisibility()
    }

    // Function to check and update empty view visibility
    private fun checkEmptyViewVisibility() {
        binding.emptyView.visibility = if (binding.scoreGridLayout.childCount <= 0) View.VISIBLE else View.GONE
    }

    private val player1CurrentScoreObserver = Observer<Int> { score ->
        val player1Score = binding.player1CurrentScoreView
        player1Score.text = score.toString()
    }
    private val player2CurrentScoreObserver = Observer<Int> { score ->
        val player2Score = binding.player2CurrentScoreView
        player2Score.text = score.toString()
    }

    private val player1TotalScoreObserver = Observer<Int> { totalScore ->
        val player1Score = binding.player1TotalScoreView
        player1Score.text = totalScore.toString()
    }
    private val player2TotalScoreObserver = Observer<Int> { totalScore ->
        val player2Score = binding.player2TotalScoreView
        player2Score.text = totalScore.toString()
    }

    private val roundScoreObserver = Observer<MutableMap<Int, Pair<Int, Int>>> { roundScores ->
        val roundNumbers = roundScores.keys.sorted()
        val scoreGrid = binding.scoreGridLayout
        scoreGrid.removeAllViews()

        for (round in roundNumbers) {
            val player1Score = roundScores[round]?.first ?: 0
            val player2Score = roundScores[round]?.second ?: 0
            addNewRound(round, player1Score, player2Score)
        }

        checkEmptyViewVisibility()
    }
}