package com.example.androidcanvas.design


enum class PieceType {
    PAWN, ROOK, KNIGHT, BISHOP, QUEEN, KING
}

enum class Color {
    WHITE, BLACK
}

abstract class ChessPiece(val color: Color, var position: Position) {
    abstract val type: PieceType
    abstract fun isValidMove(newPosition: Position, board: Array<Array<ChessPiece?>>): Boolean
}

class Pawn(color: Color, position: Position) : ChessPiece(color, position) {
    override val type: PieceType = PieceType.PAWN

    override fun isValidMove(newPosition: Position, board: Array<Array<ChessPiece?>>): Boolean {
        val direction = if (color == Color.WHITE) 1 else -1
        val rowDiff = newPosition.row - position.row
        val colDiff = newPosition.col - position.col

        if (colDiff == 0) {
            // Move forward
            if (rowDiff == direction && board[newPosition.row][newPosition.col] == null) {
                return true
            }
            // Initial double move
            if (position.row == 1 && rowDiff == 2 * direction && board[newPosition.row][newPosition.col] == null &&
                board[position.row + direction][position.col] == null
            ) {
                return true
            }
        }

        // Capture
        if (rowDiff == direction && (colDiff == 1 || colDiff == -1) &&
            board[newPosition.row][newPosition.col]?.color != color
        ) {
            return true
        }

        return false
    }
}

class Rook(color: Color, position: Position) : ChessPiece(color, position) {
    override val type: PieceType = PieceType.ROOK

    override fun isValidMove(newPosition: Position, board: Array<Array<ChessPiece?>>): Boolean {
        return newPosition.row == position.row || newPosition.col == position.col
    }
}

class Knight(color: Color, position: Position) : ChessPiece(color, position) {
    override val type: PieceType = PieceType.KNIGHT

    override fun isValidMove(newPosition: Position, board: Array<Array<ChessPiece?>>): Boolean {
        val rowDiff = newPosition.row - position.row
        val colDiff = newPosition.col - position.col
        return (rowDiff == 2 && colDiff == 1) || (rowDiff == 1 && colDiff == 2)
    }
}

class Bishop(color: Color, position: Position) : ChessPiece(color, position) {
    override val type: PieceType = PieceType.BISHOP

    override fun isValidMove(newPosition: Position, board: Array<Array<ChessPiece?>>): Boolean {
        return Math.abs(newPosition.row - position.row) == Math.abs(newPosition.col - position.col)
    }
}

class Queen(color: Color, position: Position) : ChessPiece(color, position) {
    override val type: PieceType = PieceType.QUEEN

    override fun isValidMove(newPosition: Position, board: Array<Array<ChessPiece?>>): Boolean {
        return Rook(color, position).isValidMove(newPosition, board) ||
                Bishop(color, position).isValidMove(newPosition, board)
    }
}

class King(color: Color, position: Position) : ChessPiece(color, position) {
    override val type: PieceType = PieceType.KING

    override fun isValidMove(newPosition: Position, board: Array<Array<ChessPiece?>>): Boolean {
        val rowDiff = Math.abs(newPosition.row - position.row)
        val colDiff = Math.abs(newPosition.col - position.col)
        return (rowDiff == 1 && colDiff == 0) ||
                (rowDiff == 0 && colDiff == 1) ||
                (rowDiff == 1 && colDiff == 1)
    }
}

class ChessBoard {
    val board: Array<Array<ChessPiece?>> = Array(8) { Array(8) { null } }

    fun initialize() {
        // Pawns
        for (col in 0 until 8) {
            board[1][col] = Pawn(Color.WHITE, Position(1, col))
            board[6][col] = Pawn(Color.BLACK, Position(6, col))
        }

        // Rooks
        board[0][0] = Rook(Color.WHITE, Position(0, 0))
        board[0][7] = Rook(Color.WHITE, Position(0, 7))
        board[7][0] = Rook(Color.BLACK, Position(7, 0))
        board[7][7] = Rook(Color.BLACK, Position(7, 7))

        // Knights
        board[0][1] = Knight(Color.WHITE, Position(0, 1))
        board[0][6] = Knight(Color.WHITE, Position(0, 6))
        board[7][1] = Knight(Color.BLACK, Position(7, 1))
        board[7][6] = Knight(Color.BLACK, Position(7, 6))

        // Bishops
        board[0][2] = Bishop(Color.WHITE, Position(0, 2))
        board[0][5] = Bishop(Color.WHITE, Position(0, 5))
        board[7][2] = Bishop(Color.BLACK, Position(7, 2))
        board[7][5] = Bishop(Color.BLACK, Position(7, 5))

        // Queens
        board[0][3] = Queen(Color.WHITE, Position(0, 3))
        board[7][3] = Queen(Color.BLACK, Position(7, 3))

        // Kings
        board[0][4] = King(Color.WHITE, Position(0, 4))
        board[7][4] = King(Color.BLACK, Position(7, 4))
    }

    fun printBoard() {
        for (row in 0 until 8) {
            for (col in 0 until 8) {
                val piece = board[row][col]
                print("${piece?.type?.name?.firstOrNull() ?: '.'}\t")
            }
            println()
        }
    }

    fun movePiece(piece: ChessPiece, newPosition: Position) {
        // Check if the move is valid and update the board
        if (piece.isValidMove(newPosition, board)) {
            board[piece.position.row][piece.position.col] = null
            board[newPosition.row][newPosition.col] = piece
            piece.position = newPosition
        } else {
            println("Invalid move for ${piece.type} at $newPosition.")
        }
    }
}

fun main() {
    val chessBoard = ChessBoard()
    chessBoard.initialize()
    chessBoard.printBoard()

    val knight = chessBoard.board[0][1]
    val newPosition = Position(2, 0)
    knight?.let { chessBoard.movePiece(it, newPosition) }

    chessBoard.printBoard()
}

data class Position(val row: Int, val col: Int)