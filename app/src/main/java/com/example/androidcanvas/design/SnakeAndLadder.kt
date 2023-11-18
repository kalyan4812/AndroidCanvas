package com.example.androidcanvas.design


import kotlin.random.Random

class SnakeAndLadder(numberOfPlayers: Int) {
    private var playerPositions = IntArray(numberOfPlayers)
    private val finalPosition = 100
    private val diceRange = 1..6
    private val snakesAndLadders = mapOf(
        16 to 6, 47 to 26, 49 to 11, 56 to 53,
        62 to 19, 64 to 60, 87 to 24, 93 to 73, 95 to 75, 98 to 78
    )

    fun playTurn(playerIndex: Int): Boolean {
        val diceRoll = rollDice()
        println("Player $playerIndex rolled a $diceRoll.")

        val newPosition = playerPositions[playerIndex] + diceRoll
        val finalPositionAfterSnakeLadder = snakesAndLadders[newPosition] ?: newPosition

        // Check for victory
        if (finalPositionAfterSnakeLadder == finalPosition) {
            println("Player $playerIndex wins!")
            return true
        }

        // Update player position
        playerPositions[playerIndex] = finalPositionAfterSnakeLadder

        println("Player $playerIndex moved to $finalPositionAfterSnakeLadder.")
        return false
    }

    private fun rollDice(): Int {
        return Random.nextInt(1, 7)
    }
}

fun main() {
    val numberOfPlayers = 2
    val game = SnakeAndLadder(numberOfPlayers)

    var currentPlayerIndex = 0
    while (true) {
        val end = game.playTurn(currentPlayerIndex)
        if (end) break
        currentPlayerIndex = (currentPlayerIndex + 1) % numberOfPlayers
    }
}
