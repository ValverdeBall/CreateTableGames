package com.valverdeball.createtablegames;

import java.util.ArrayList;
import java.util.List;

public final class ChessMoves{

  private ChessMoves() {}

  public static List<int[]> pawnMoves(byte[] board, int file, int rank, PlayerFaction.Side side) {
    List<int[]> moves = new ArrayList<>();
    int direction = (side == PlayerFaction.Side.WHITE) ? 1 : -1;
    int startRank = (side == PlayerFaction.Side.WHITE) ? 1 : 6;

    int oneForward = rank + direction;
    if (isOnBoard(file, oneForward) && isEmpty(board, file, oneForward)) {
      moves.add(new int[]{file, oneForward});

      int twoForward = rank + (direction * 2);
      if (rank == startRank && isEmpty(board, file, twoForward)) {
        moves.add(new int[]{file, twoForward});
      }
    }

    for (int df : new int[]{-1, 1}) {
      int captureFile = file + df;
      int captureRank = rank + direction;
      if (isOnBoard(captureFile, captureRank) && isEnemy(board, captureFile, captureRank, side)) {
        moves.add(new int[]{captureFile, captureRank});
      }
    }

    return moves;
  }

  public static List<int[]> knightMoves(byte[] board, int file, int rank, PlayerFaction.Side side) {
    List<int[]> moves = new ArrayList<>();
    int[][] offsets = {
      {1,2}, {2,1}, {2, -1}, {1, -2},
      {-1, -2}, {-2, -1}, {-2, 1}, {-1, 2}
    };

    for (int[] offset : offsets) {
      int targetFile = file + offset[0];
      int targetRank = rank + offset[1];
      if (isOnBoard(targetFile, targetRank) && !isFriendly(board, targetFile, targetRank, side)) {
        moves.add(new int[]{targetFile, targetRank});
      }
    }

    return moves;
  }

  public static List<int[]> bishopMoves(byte[] board, int file, int rank, PlayerFaction.Side side) {
    return slidingMoves(board, file, rank, side, new int[][]{{1, 1}, {1, -1}, {-1, 1}, {-1, -1}});
  }

  public static List<int[]> rookMoves(byte[] board, int file, int rank, PlayerFaction.Side side) {
    return slidingMoves(board, file, rank, side, new int[][]{{1, 0}, {-1, 0}, {0, 1}, {0, -1}});
  }

  public static List<int[]> queenMoves(byte[] board, int file, int rank, PlayerFaction.Side side) {
    return slidingMoves(board, file, rank, side, new int[][]{
      {1, 0}, {-1, 0}, {0, 1}, {0, -1},
      {1, 1}, {1, -1}, {-1, 1}, {-1, -1}
    });
  }

  public static List<int[]> kingMoves(byte[] board, int file, int rank, PlayerFaction.Side side) {
    List<int[]> moves = new ArrayList<>();
    for (int df = -1; df <= 1; df++) {
      for (int dr = -1; dr <= 1; dr++) {
        if (df == 0 && dr == 0) continue;
        int targetFile = file + df;
        int targetRank = rank + dr;
        if (isOnBoard(targetFile, targetRank) && !isFriendly(board, targetFile, targetRank, side)) {
          moves.add(new int[]{targetFile, targetRank});
        }
      }
    }
    return moves;
  }

  private static List<int[]> slidingMoves(byte[] board, int file, int rank, PlayerFaction.Side side, int[][] directions) {
    List<int[]> moves = new ArrayList<>();

    for (int[] dir : directions) {
      int targetFile = file + dir[0];
      int targetRank = rank + dir[1];

      while (isOnBoard(targetFile, targetRank)) {
        if (isEmpty(board, targetFile, targetRank)) {
          moves.add(new int[]{targetFile, targetRank});
        } else if (isEnemy(board, targetFile, targetRank, side)) {
          moves.add(new int[]{targetFile, targetRank});
          break;
        } else {
          break;
        }
        targetFile += dir[0];
        targetRank += dir[1];
      }
    }

    return moves;
  }

  private static boolean isOnBoard(int file, int rank) {
    return file >= 0 && file < 8 && rank >= 0 && rank < 8;
  }
  
  private static boolean isEmpty(byte[] board, int file, int rank) {
    return ChessPiece.isEmpty(board[rank * 8 + file]);
  }

  private static boolean isEnemy(byte[] board, int file, int rank, PlayerFaction.Side side) {
    byte square = board[rank * 8 + file];
    if (ChessPiece.isEmpty(square)) return false;
    return ChessPiece.sideOf(square) != side;
  }

  private static boolean isFriendly(byte[] board, int file, int rank, PlayerFaction.Side side) {
    byte square = board[rank * 8 + file];
    if (ChessPiece.isEmpty(square)) return false;
    return ChessPiece.sideOf(square) == side;
  }

  public static List<int[]> movesFor(byte[] board, int file, int rank) {
    byte square = board[rank * 8 + file];
    if (ChessPiece.isEmpty(square)) return new ArrayList<>();

    PlayerFaction.Side side = ChessPiece.sideOf(square);
    byte type = ChessPiece.type(square);

    return switch (type) {
      case ChessPiece.PAWN -> pawnMoves(board, file, rank, side);
      case ChessPiece.KNIGHT -> knightMoves(board, file, rank, side);
      case ChessPiece.BISHOP -> bishopMoves(board, file, rank, side);
      case ChessPiece.ROOK -> rookMoves(board, file, rank, side);
      case ChessPiece.QUEEN -> queenMoves(board, file, rank, side);
      case ChessPiece.KING -> kingMoves(board, file, rank, side);
      default -> new ArrayList<>();
    };
  }
}

// i dont got anything better to do rather than adding some random comments, yeet