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

  public static boolean isKingInCheck(byte [] board, PlayerFaction.Side side) {
    int kingFile = -1;
    int kingRank = -1;
    byte kingPiece = ChessPiece.encode(ChessPiece.KING, side);

    for (int rank = 0; rank < 8; rank++) {
      for (int file = 0; file < 8; file++) {
        if (board[rank * 8 + file] == kingPiece) {
          kingFile = file;
          kingRank = rank;
        }
      }
    }

    if (kingFile == -1) {
      return false;
    }

    for (int rank = 0; rank < 8; rank++) {
      for (int file = 0; file < 8; file++) {
        byte square = board[rank * 8 + file];
        if (ChessPiece.isEmpty(square) || ChessPiece.sideOf(square) == side) {
          continue;
        }

        List<int[]> attackerMoves = movesFor(board, file, rank);
        for (int[] move : attackerMoves) {
          if (move[0] == kingFile && move[1] == kingRank) {
            return true;
          }
        }
      }
    }

    return false;
  }

  public static List<int[]> legalMovesFor(byte[] board, int file, int rank) {
    byte piece = board[rank * 8 + file];
    if (ChessPiece.isEmpty(piece)) {
      return new ArrayList<>();
    }

    PlayerFaction.Side side = ChessPiece.sideOf(piece);
    List<int[]> rawMoves = movesFor(board, file, rank);
    List<int[]> legalMoves = new ArrayList<>();

    for (int[] move : rawMoves) {
      byte[] simulated = board.clone();
      simulated[rank * 8 + file] = ChessPiece.EMPTY;
      simulated[move[1] * 8 + move[0]] = piece;

      if (!isKingInCheck(simulated, side)) {
        legalMoves.add(move);
      }
    }

    return legalMoves;
  }

  public static List<int[]> legalMovesFor(byte[] board, int file, int rank, boolean canCastleKingside, boolean canCastleQueenside) {
    List<int[]> legalMoves = legalMovesFor(board, file, rank);

    byte piece = board[rank * 8 + file];
    if (!ChessPiece.isEmpty(piece) && ChessPiece.type(piece) == ChessPiece.KING) {
      PlayerFaction.Side side = ChessPiece.sideOf(piece);
      legalMoves.addAll(castlingMoves(board, file, rank, side, canCastleKingside, canCastleQueenside));
    }

    return legalMoves;
  }

  public static List<int[]> legalMovesFor(byte[] board, int file, int rank, boolean canCastleKingside, boolean canCastleQueenside, int enPassantFile) {
    List<int[]> legalMoves = legalMovesFor(board, file, rank, canCastleKingside, canCastleQueenside);

    byte piece = board[rank * 8 + file];
    if (!ChessPiece.isEmpty(piece) && ChessPiece.type(piece) == ChessPiece.PAWN && enPassantFile != -1) {
      PlayerFaction.Side side = ChessPiece.sideOf(piece);
      int captureRank = (side == PlayerFaction.Side.WHITE) ? 4 : 3;
      int targetRank = (side == PlayerFaction.Side.WHITE) ? 5 : 2;
      if (rank == captureRank && Math.abs(file - enPassantFile) == 1) {
        legalMoves.add(new int[]{enPassantFile, targetRank});
      }
    }

    return legalMoves;
  }

  public static List<int[]> castlingMoves(byte[] board, int file, int rank, PlayerFaction.Side side, boolean canKingside, boolean canQueenside) {
    List<int[]> moves = new ArrayList<>();

    if(isKingInCheck(board, side)) {
      return moves;
    }

    if (canKingside) {
      if (isEmpty(board, file + 1, rank) && isEmpty(board, file + 2, rank)
         && !isSquareAttacked(board, file + 1, rank, side)
         && !isSquareAttacked(board, file + 2, rank, side)) {
        moves.add(new int[]{file + 2, rank});
      }
    }

    if (canQueenside) {
      if (isEmpty(board, file - 1, rank) && isEmpty(board, file - 2, rank)
         && !isSquareAttacked(board, file - 1, rank, side)
         && !isSquareAttacked(board, file - 2, rank, side)) {
        moves.add(new int[]{file - 2, rank});
      }
    }

    return moves;
  }

  private static boolean isSquareAttacked(byte[] board, int targetFile, int targetRank, PlayerFaction.Side side) {
    for (int rank = 0; rank < 8; rank++) {
      for (int file = 0; file < 8; file++) {
        byte square = board[rank * 8 + file];
        if (ChessPiece.isEmpty(square) || ChessPiece.sideOf(square) == side) {
          continue;
        }
        List<int[]> attackerMoves = movesFor(board, file, rank);
        for (int[] move : attackerMoves) {
          if (move[0] == targetFile && move[1] == targetRank) {
            return true;
          }
        }
      }
    }
    return false;
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