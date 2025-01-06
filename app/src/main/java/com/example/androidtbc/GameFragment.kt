package com.example.androidtbc

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridLayout
import android.widget.ImageButton
import android.widget.ImageView
import com.example.androidtbc.databinding.FragmentGameBinding
import com.google.android.material.snackbar.Snackbar

class GameFragment(private val boardSize: Int) : Fragment() {
    private var _binding: FragmentGameBinding? = null
    private val binding get() = _binding!!


    private var gameOver = false
    private var currentPlayer = "X"
    private lateinit var board: Array<Array<String?>>
    private var currentSnackbar: Snackbar? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        board = Array(boardSize) {
            arrayOfNulls(boardSize)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGameBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.tvPlayerTurn.text = "player: $currentPlayer"
        createBoard(boardSize)
        setUpClickListeners()

    }

    private fun setUpClickListeners(){
        binding.btnChangeSize.setOnClickListener{
            currentSnackbar?.dismiss()
            parentFragmentManager.popBackStack()
        }
        binding.btnRestart.setOnClickListener{
            currentSnackbar?.dismiss()
            restartGame()
        }
    }


    private fun createBoard(boardSize: Int) {
        binding.gridLayout.removeAllViews()

        binding.gridLayout.rowCount = boardSize
        binding.gridLayout.columnCount = boardSize

        val screenWidth = resources.displayMetrics.widthPixels
        val buttonSize = (screenWidth - 64) / boardSize

        for (row in 0 until boardSize) {
            for (col in 0 until boardSize) {
                val imageButton = ImageButton(requireContext())

                imageButton.layoutParams = GridLayout.LayoutParams().apply {
                    width = buttonSize
                    height = buttonSize
                    setMargins(10, 10, 10, 10)
                }

                imageButton.scaleType = ImageView.ScaleType.CENTER_INSIDE
                imageButton.setBackgroundResource(R.drawable.bg_rounded_image_button)


                imageButton.setOnClickListener {
                    checkResult(imageButton, row, col)
                }

                binding.gridLayout.addView(imageButton)
            }
        }
    }


    private fun checkResult(button: ImageButton, row: Int, col: Int) {
        if (gameOver == true || button.tag != null) return
        button.tag = currentPlayer
        board[row][col] = currentPlayer

        if (currentPlayer == "X") {
            button.setImageResource(R.drawable.icon_x)
        } else {
            button.setImageResource(R.drawable.icon_o)
        }

        if (checkWin(row, col)) {
            displayGameOverMsg("$currentPlayer Wins!")
            gameOver = true
            return
        }

        if (isBoardFull()) {
            displayGameOverMsg("It's a Draw!")
            gameOver = true
            return
        }

        currentPlayer = if (currentPlayer == "X") "O" else "X"
        binding.tvPlayerTurn.text = "player: $currentPlayer"

        val color = if (currentPlayer == "X") {
            requireContext().getColor(R.color.red)
        } else {
            requireContext().getColor(R.color.green)
        }
        binding.tvPlayerTurn.setTextColor(color)

    }


    private fun checkWin(row: Int, col: Int): Boolean {
        if (board[row].all { it == currentPlayer })
            return true

        if (board.all {
                it[col] == currentPlayer
            })
            return true

        if (row == col && (0 until boardSize).all {
                board[it][it] == currentPlayer
            })
            return true

        if (row + col == boardSize - 1 && (0 until boardSize).all {
                board[it][boardSize - 1 - it] == currentPlayer
            })
            return true

        return false
    }


    private fun displayGameOverMsg(message: String) {
        currentSnackbar?.dismiss()
        currentSnackbar = Snackbar.make(binding.root, "Game Over: $message", Snackbar.LENGTH_INDEFINITE)
            .setAction("TRY OR PLAY AGAIN") {
                restartGame()
            }
            currentSnackbar?.show()
    }


    private fun restartGame() {
        board = Array(boardSize) {
            arrayOfNulls(boardSize)
        }
        createBoard(boardSize)
        currentPlayer = "X"
        gameOver = false
        binding.tvPlayerTurn.text = "player: $currentPlayer"
        binding.tvPlayerTurn.setTextColor(requireContext().getColor(R.color.red))

    }

    private fun isBoardFull(): Boolean {
        return board.all { row -> row.all { it != null } }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        currentSnackbar?.dismiss()
        _binding = null
    }

}
